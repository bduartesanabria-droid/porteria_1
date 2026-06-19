package controlador;

import modelo.*;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Operaciones administrativas: gestión de usuarios y respaldos del sistema.
 * Espejo de app/routes/usuarios/admin_usuarios.py de la web.
 */
public class AdminControlador {

    private static final String DIRECTORIO_RESPALDOS = "respaldos/";

    // ─── GESTIÓN DE USUARIOS ──────────────────────────────────────────────────

    public static List<Usuario> listarTodos() throws SQLException {
        return UsuarioDAO.listarTodos();
    }

    /**
     * Crea un usuario desde el panel de administración (puede asignar cualquier cargo/rol).
     * Devuelve el usuario creado con ID asignado, o null si falló.
     */
    public static Usuario crearUsuario(String nombre, String correo, String documento,
                                        String contrasena, String cargo, String programa,
                                        String ficha, String horario) throws SQLException {
        if (UsuarioDAO.buscarPorCorreo(correo.trim()) != null) {
            throw new IllegalArgumentException("El correo ya está registrado.");
        }
        if (UsuarioDAO.buscarPorDocumento(documento.trim()) != null) {
            throw new IllegalArgumentException("El documento ya está registrado.");
        }

        int rolId = "Administrador".equalsIgnoreCase(cargo) ? 1 : 2;

        Usuario u = new Usuario();
        u.setNombre(nombre.trim());
        u.setCorreo(correo.trim().toLowerCase());
        u.setDocumento(documento.trim());
        u.setContrasenaHash(modelo.ContrasenaUtil.hashear(contrasena));
        u.setRolId(rolId);
        u.setCargo(cargo);
        u.setPrograma(programa);
        u.setFicha(ficha);
        u.setHorario(horario);
        u.setCorreoVerificado(true);    // admin crea usuarios ya verificados
        u.setPerfilCompleto(false);
        UsuarioDAO.crear(u);

        // Registrar en auditoría
        registrarAuditoria(null, "usuarios", u.getId(), "CREAR_USUARIO",
            "Creado por administrador", null);

        return u;
    }

    public static void cambiarCargo(int usuarioId, String nuevoCargo) throws SQLException {
        UsuarioDAO.actualizarCargo(usuarioId, nuevoCargo);
        registrarAuditoria(usuarioId, "usuarios", usuarioId, "CAMBIO_CARGO", "Cargo actualizado", null);
    }

    public static void activarUsuario(int usuarioId) throws SQLException {
        UsuarioDAO.actualizarEstado(usuarioId, "Activo");
        registrarAuditoria(usuarioId, "usuarios", usuarioId, "ACTIVAR", null, null);
    }

    public static void inactivarUsuario(int usuarioId) throws SQLException {
        UsuarioDAO.actualizarEstado(usuarioId, "Inactivo");
        registrarAuditoria(usuarioId, "usuarios", usuarioId, "INACTIVAR", null, null);
    }

    public static void resetearContrasena(int usuarioId, String nuevaContrasena) throws SQLException {
        AuthControlador.cambiarContrasena(usuarioId, nuevaContrasena);
        // Forzar cambio en próximo login
        String sql = "UPDATE usuarios SET debe_cambiar_contrasena=true WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        }
        registrarAuditoria(usuarioId, "usuarios", usuarioId, "RESET_CONTRASENA",
            "Contraseña reseteada por admin", null);
    }

    // ─── HISTORIAL DE CAMBIOS (AUDITORÍA) ────────────────────────────────────

    public static List<Auditoria> obtenerHistorialCambios(int limite) throws SQLException {
        return AuditoriaDAO.listarTodos(limite);
    }

    public static List<Auditoria> obtenerHistorialPorTabla(String tabla, int limite) throws SQLException {
        return AuditoriaDAO.listarPorTabla(tabla, limite);
    }

    // ─── RESPALDOS ────────────────────────────────────────────────────────────

    /**
     * Genera un respaldo SQL de la base de datos usando mysqldump.
     * Requiere que mysqldump esté en el PATH del sistema.
     */
    public static File generarRespaldo(String host, String usuario, String contrasena, String baseDatos)
            throws IOException, InterruptedException {
        new File(DIRECTORIO_RESPALDOS).mkdirs();
        String marca = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String nombreArchivo = DIRECTORIO_RESPALDOS + "respaldo_" + marca + ".sql";

        ProcessBuilder pb = new ProcessBuilder(
            "mysqldump",
            "-h", host,
            "-u", usuario,
            "-p" + contrasena,
            baseDatos
        );
        pb.redirectOutput(new File(nombreArchivo));
        pb.redirectErrorStream(true);

        Process proceso = pb.start();
        int exitCode = proceso.waitFor();

        if (exitCode != 0) throw new IOException("mysqldump falló con código " + exitCode);
        return new File(nombreArchivo);
    }

    /** Lista los archivos de respaldo disponibles. */
    public static File[] listarRespaldos() {
        File dir = new File(DIRECTORIO_RESPALDOS);
        if (!dir.exists()) return new File[0];
        File[] archivos = dir.listFiles((d, n) -> n.endsWith(".sql"));
        return archivos != null ? archivos : new File[0];
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static void registrarAuditoria(Integer usuarioId, String tabla, int registroId,
                                            String accion, String motivo, String detalles) {
        try {
            Usuario ejecutor = AuthControlador.getUsuarioActual();
            Auditoria a = new Auditoria();
            a.setUsuarioId(usuarioId);
            a.setNombreUsuario(ejecutor != null ? ejecutor.getNombre() : "Sistema");
            a.setTablaAfectada(tabla);
            a.setRegistroId(registroId);
            a.setAccion(accion);
            a.setAutorizadoPor(ejecutor != null ? ejecutor.getNombre() : "Sistema");
            a.setMotivo(motivo);
            a.setDetalles(detalles);
            AuditoriaDAO.registrar(a);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
