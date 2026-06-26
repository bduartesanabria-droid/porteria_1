package controlador;

import modelo.*;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

/**
 * Gestiona autenticación: login, registro, verificación de correo y recuperación de contraseña.
 * Espejo de app/routes/auth/ de la web.
 */
public class AuthControlador {

    private static final int MAX_INTENTOS = 5;
    private static final int MINUTOS_BLOQUEO = 10;
    private static final int MINUTOS_CODIGO = 15;

    /** Usuario autenticado en la sesión actual. */
    private static Usuario usuarioActual;

    public static Usuario getUsuarioActual() { return usuarioActual; }

    // ─── LOGIN ───────────────────────────────────────────────────────────────

    public enum ResultadoLogin {
        OK, CREDENCIALES_INCORRECTAS, CUENTA_BLOQUEADA,
        PERFIL_INCOMPLETO, DEBE_CAMBIAR_CONTRASENA
    }

    public static ResultadoLogin iniciarSesion(String identificador, String contrasena) throws SQLException {
        Usuario u = UsuarioDAO.buscarPorIdentificador(identificador.trim());
        if (u == null) return ResultadoLogin.CREDENCIALES_INCORRECTAS;

        // Verificar bloqueo
        if (u.getBloqueadoHasta() != null && u.getBloqueadoHasta().after(new Timestamp(System.currentTimeMillis()))) {
            return ResultadoLogin.CUENTA_BLOQUEADA;
        }

        // Verificar contraseña
        if (!ContrasenaUtil.verificar(contrasena, u.getContrasenaHash())) {
            int intentos = u.getIntentosFallidos() + 1;
            if (intentos >= MAX_INTENTOS) {
                Timestamp hasta = new Timestamp(System.currentTimeMillis() + MINUTOS_BLOQUEO * 60_000L);
                UsuarioDAO.bloquear(u.getId(), hasta);
            } else {
                UsuarioDAO.actualizarIntentosFallidos(u.getId(), intentos);
            }
            return ResultadoLogin.CREDENCIALES_INCORRECTAS;
        }

        // Reset intentos fallidos
        if (u.getIntentosFallidos() > 0) {
            UsuarioDAO.actualizarIntentosFallidos(u.getId(), 0);
        }

        // El correo se usa solo como identificador de acceso; no requiere verificación.
        if (u.isDebeCambiarContrasena()) {
            usuarioActual = u;
            return ResultadoLogin.DEBE_CAMBIAR_CONTRASENA;
        }
        if (!u.isPerfilCompleto()) {
            usuarioActual = u;
            return ResultadoLogin.PERFIL_INCOMPLETO;
        }

        // Crear turno si es celador
        if (u.esCelador()) {
            TurnoCelador turno = new TurnoCelador();
            turno.setCeladorId(u.getId());
            turno.setEstado("Activo");
            guardarTurno(turno);
        }

        usuarioActual = u;
        return ResultadoLogin.OK;
    }

    public static void cerrarSesion() throws SQLException {
        if (usuarioActual != null) {
            // Finalizar turno activo si es celador
            if (usuarioActual.esCelador()) {
                finalizarTurnoActivo(usuarioActual.getId());
            }
            UsuarioDAO.actualizarToken(usuarioActual.getId(), null);
            usuarioActual = null;
        }
    }

    // ─── REGISTRO ────────────────────────────────────────────────────────────

    public enum ResultadoRegistro {
        OK, CORREO_DUPLICADO, DOCUMENTO_DUPLICADO, FICHA_INCONSISTENTE, ERROR_BD
    }

    public static ResultadoRegistro registrar(String nombre, String correo, String documento,
                                               String contrasena, String programa, String ficha,
                                               String horario, String cargo) {
        try {
            if (UsuarioDAO.buscarPorCorreo(correo.trim()) != null) return ResultadoRegistro.CORREO_DUPLICADO;
            if (UsuarioDAO.buscarPorDocumento(documento.trim()) != null) return ResultadoRegistro.DOCUMENTO_DUPLICADO;

            // Validar consistencia de ficha (misma ficha → mismo programa y jornada)
            if (ficha != null && !ficha.isEmpty()) {
                java.util.List<Usuario> compañeros = UsuarioDAO.listarPorFicha(ficha.trim());
                if (!compañeros.isEmpty()) {
                    Usuario ref = compañeros.get(0);
                    if (ref.getPrograma() != null && !ref.getPrograma().equalsIgnoreCase(programa)) {
                        return ResultadoRegistro.FICHA_INCONSISTENTE;
                    }
                }
            }

            Usuario u = new Usuario();
            u.setNombre(nombre.trim());
            u.setCorreo(correo.trim().toLowerCase());
            u.setDocumento(documento.trim());
            u.setContrasenaHash(ContrasenaUtil.hashear(contrasena));
            u.setRolId(2); // rol Usuario por defecto
            u.setCargo(cargo != null ? cargo : "Aprendiz");
            u.setPrograma(programa);
            u.setFicha(ficha);
            u.setHorario(horario);
            u.setCorreoVerificado(true);
            u.setCodigoVerificacion(null);
            u.setCodigoExpiracion(null);

            UsuarioDAO.crear(u);
            return ResultadoRegistro.OK;

        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoRegistro.ERROR_BD;
        }
    }

    // ─── CAMBIO DE CONTRASEÑA ─────────────────────────────────────────────────

    public static void cambiarContrasena(int usuarioId, String nuevaContrasena) throws SQLException {
        String hash = ContrasenaUtil.hashear(nuevaContrasena);
        UsuarioDAO.actualizarContrasena(usuarioId, hash);
    }

    public static boolean verificarContrasenaActual(int usuarioId, String contrasena) throws SQLException {
        Usuario u = UsuarioDAO.buscarPorId(usuarioId);
        return u != null && ContrasenaUtil.verificar(contrasena, u.getContrasenaHash());
    }

    // ─── RECUPERACIÓN ─────────────────────────────────────────────────────────

    public static String generarCodigoRecuperacion(int usuarioId) throws SQLException {
        String codigo = generarCodigo6Digitos();
        Timestamp exp = new Timestamp(System.currentTimeMillis() + MINUTOS_CODIGO * 60_000L);
        String sql = "UPDATE usuarios SET codigo_recuperacion=?, recuperacion_expiracion=? WHERE id=?";
        try (java.sql.Connection cn = modelo.Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setTimestamp(2, exp);
            ps.setInt(3, usuarioId);
            ps.executeUpdate();
        }
        return codigo;
    }

    // ─── HELPERS ──────────────────────────────────────────────────────────────

    private static String generarCodigo6Digitos() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private static void guardarTurno(TurnoCelador turno) {
        String sql = "INSERT INTO turnos_celador (celador_id, estado) VALUES (?,?)";
        try (java.sql.Connection cn = modelo.Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, turno.getCeladorId());
            ps.setString(2, turno.getEstado());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void finalizarTurnoActivo(int celadorId) {
        String sql = "UPDATE turnos_celador SET estado='Finalizado', fecha_salida=NOW() " +
                     "WHERE celador_id=? AND estado='Activo'";
        try (java.sql.Connection cn = modelo.Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, celadorId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
