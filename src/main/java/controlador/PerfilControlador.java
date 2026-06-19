package controlador;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import modelo.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;

/**
 * Gestión del perfil del usuario: completar datos, foto, y generación de QR.
 * Espejo de app/routes/usuarios/perfil.py de la web.
 */
public class PerfilControlador {

    private static final String DIRECTORIO_FOTOS = "uploads/perfiles/";
    private static final String DIRECTORIO_QRS   = "uploads/qr/";

    public enum ResultadoPerfil {
        OK, CAMPOS_INCOMPLETOS, PROGRAMA_INVALIDO, DOCUMENTO_DUPLICADO, ERROR_BD
    }

    /**
     * Actualiza los datos del perfil y marca perfil_completo si todos los campos requeridos están presentes.
     * Requeridos para todos: documento, tipo_sangre, foto.
     * Adicionales para Aprendiz: programa, ficha.
     */
    public static ResultadoPerfil completarPerfil(int usuarioId, String documento, String tipoSangre,
                                                   String fotoRuta, String programa, String ficha,
                                                   String horario) {
        try {
            // Validar que el documento no esté tomado por otro usuario
            Usuario otro = UsuarioDAO.buscarPorDocumento(documento.trim());
            if (otro != null && otro.getId() != usuarioId) return ResultadoPerfil.DOCUMENTO_DUPLICADO;

            // Validar programa: solo letras, espacios y acentos
            if (programa != null && !programa.isEmpty() && !programa.matches("[\\p{L}\\s]+")) {
                return ResultadoPerfil.PROGRAMA_INVALIDO;
            }

            Usuario u = UsuarioDAO.buscarPorId(usuarioId);
            if (u == null) return ResultadoPerfil.ERROR_BD;

            u.setDocumento(documento.trim());
            u.setTipoSangre(tipoSangre);
            u.setFoto(fotoRuta);
            u.setPrograma(programa);
            u.setFicha(ficha);
            u.setHorario(horario);

            // Determinar si el perfil está completo
            boolean completo = tienesDatosBasicos(u);
            if (u.esAprendiz()) completo = completo && programa != null && !programa.isEmpty()
                                                     && ficha != null && !ficha.isEmpty();
            u.setPerfilCompleto(completo);

            UsuarioDAO.actualizarPerfil(u);

            // Generar QR si el perfil ya está completo y no tiene carnet
            if (completo) {
                generarQR(u);
            }

            return ResultadoPerfil.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPerfil.ERROR_BD;
        }
    }

    public static ResultadoPerfil actualizarDatos(int usuarioId, String nombre, String programa,
                                                   String ficha, String horario, String tipoSangre) {
        try {
            Usuario u = UsuarioDAO.buscarPorId(usuarioId);
            if (u == null) return ResultadoPerfil.ERROR_BD;
            if (nombre != null && !nombre.isEmpty()) u.setNombre(nombre.trim());
            u.setPrograma(programa);
            u.setFicha(ficha);
            u.setHorario(horario);
            u.setTipoSangre(tipoSangre);
            UsuarioDAO.actualizarPerfil(u);
            return ResultadoPerfil.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPerfil.ERROR_BD;
        }
    }

    public static ResultadoPerfil actualizarFoto(int usuarioId, String rutaFoto) {
        try {
            Usuario u = UsuarioDAO.buscarPorId(usuarioId);
            if (u == null) return ResultadoPerfil.ERROR_BD;
            u.setFoto(rutaFoto);
            UsuarioDAO.actualizarPerfil(u);
            return ResultadoPerfil.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPerfil.ERROR_BD;
        }
    }

    /**
     * Genera el código QR del carnet del usuario y lo guarda como imagen PNG.
     * El código sigue el formato: SENA-CARNET:<documento>
     */
    public static File generarQR(Usuario u) {
        try {
            // Crear o recuperar carnet
            int carnetId = obtenerOCrearCarnet(u.getId());

            String contenido = "SENA-CARNET:" + u.getDocumento();

            // Guardar código en BD
            guardarCodigoQR(carnetId, contenido);

            // Generar imagen PNG
            new File(DIRECTORIO_QRS).mkdirs();
            String ruta = DIRECTORIO_QRS + "qr_usuario_" + u.getId() + ".png";
            generarImagenQR(contenido, ruta, 300, 300);
            return new File(ruta);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static BufferedImage generarImagenQRBuffered(String contenido, int ancho, int alto) {
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto);
            return MatrixToImageWriter.toBufferedImage(matrix);
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static boolean tienesDatosBasicos(Usuario u) {
        return u.getDocumento() != null && !u.getDocumento().isEmpty()
                && u.getTipoSangre() != null && !u.getTipoSangre().isEmpty()
                && u.getFoto() != null && !u.getFoto().isEmpty();
    }

    private static int obtenerOCrearCarnet(int usuarioId) throws SQLException {
        String selectSql = "SELECT id FROM carnets WHERE usuario_id=?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(selectSql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("id");
            }
        }
        String insertSql = "INSERT INTO carnets (usuario_id) VALUES (?)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("No se pudo crear el carnet para usuario " + usuarioId);
    }

    private static void guardarCodigoQR(int carnetId, String codigo) throws SQLException {
        String sql = "INSERT INTO codigos_qr (carnet_id, codigo) VALUES (?,?) ON CONFLICT DO NOTHING";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, carnetId);
            ps.setString(2, codigo);
            ps.executeUpdate();
        }
    }

    private static void generarImagenQR(String contenido, String ruta, int ancho, int alto)
            throws WriterException, IOException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(contenido, BarcodeFormat.QR_CODE, ancho, alto);
        BufferedImage img = MatrixToImageWriter.toBufferedImage(matrix);
        ImageIO.write(img, "PNG", new File(ruta));
    }
}
