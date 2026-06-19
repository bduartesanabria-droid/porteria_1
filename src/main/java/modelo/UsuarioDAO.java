package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private static Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setDocumento(rs.getString("documento"));
        u.setContrasenaHash(rs.getString("contraseña"));
        u.setRolId(rs.getInt("rol_id"));
        u.setCargo(rs.getString("cargo"));
        u.setPerfilCompleto(rs.getBoolean("perfil_completo"));
        u.setCorreoVerificado(rs.getBoolean("correo_verificado"));
        u.setCodigoVerificacion(rs.getString("codigo_verificacion"));
        u.setCodigoExpiracion(rs.getTimestamp("codigo_expiracion"));
        u.setIntentosFallidos(rs.getInt("intentos_fallidos"));
        u.setBloqueadoHasta(rs.getTimestamp("bloqueado_hasta"));
        u.setDebeCambiarContrasena(rs.getBoolean("debe_cambiar_contrasena"));
        u.setCodigoRecuperacion(rs.getString("codigo_recuperacion"));
        u.setRecuperacionExpiracion(rs.getTimestamp("recuperacion_expiracion"));
        u.setPrograma(rs.getString("programa"));
        u.setFicha(rs.getString("ficha"));
        u.setHorario(rs.getString("horario"));
        u.setTipoSangre(rs.getString("tipo_sangre"));
        u.setFoto(rs.getString("foto"));
        u.setSessionToken(rs.getString("session_token"));
        try { u.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignored) {}
        try { u.setUpdatedAt(rs.getTimestamp("updated_at")); } catch (SQLException ignored) {}
        // columna del JOIN con roles (puede no existir)
        try { u.setRolNombre(rs.getString("rol_nombre")); } catch (SQLException ignored) {}
        return u;
    }

    public static void crear(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, correo, documento, contraseña, rol_id, cargo, " +
                     "correo_verificado, codigo_verificacion, codigo_expiracion, programa, ficha, horario) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getContrasenaHash());
            ps.setInt(5, u.getRolId() == 0 ? 2 : u.getRolId());
            ps.setString(6, u.getCargo() != null ? u.getCargo() : "Aprendiz");
            ps.setBoolean(7, u.isCorreoVerificado());
            ps.setString(8, u.getCodigoVerificacion());
            ps.setTimestamp(9, u.getCodigoExpiracion());
            ps.setString(10, u.getPrograma());
            ps.setString(11, u.getFicha());
            ps.setString(12, u.getHorario());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    public static Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id WHERE u.id = ?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Usuario buscarPorCorreo(String correo) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id WHERE u.correo = ?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Usuario buscarPorDocumento(String documento) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id WHERE u.documento = ?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Busca por correo o documento (para el campo de login). */
    public static Usuario buscarPorIdentificador(String identificador) throws SQLException {
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id WHERE u.correo = ? OR u.documento = ?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, identificador);
            ps.setString(2, identificador);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id ORDER BY u.nombre";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static List<Usuario> listarPorFicha(String ficha) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT u.*, r.nombre AS rol_nombre FROM usuarios u " +
                     "JOIN roles r ON r.id = u.rol_id WHERE u.ficha = ? ORDER BY u.nombre";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ficha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static void actualizarPerfil(Usuario u) throws SQLException {
        String sql = "UPDATE usuarios SET nombre=?, correo=?, documento=?, programa=?, ficha=?, " +
                     "horario=?, tipo_sangre=?, foto=?, perfil_completo=?, updated_at=NOW() WHERE id=?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setString(3, u.getDocumento());
            ps.setString(4, u.getPrograma());
            ps.setString(5, u.getFicha());
            ps.setString(6, u.getHorario());
            ps.setString(7, u.getTipoSangre());
            ps.setString(8, u.getFoto());
            ps.setBoolean(9, u.isPerfilCompleto());
            ps.setInt(10, u.getId());
            ps.executeUpdate();
        }
    }

    public static void actualizarContrasena(int id, String hash) throws SQLException {
        String sql = "UPDATE usuarios SET contraseña=?, debe_cambiar_contrasena=false WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, hash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void actualizarIntentosFallidos(int id, int intentos) throws SQLException {
        String sql = "UPDATE usuarios SET intentos_fallidos=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, intentos);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void bloquear(int id, Timestamp hasta) throws SQLException {
        String sql = "UPDATE usuarios SET bloqueado_hasta=?, intentos_fallidos=0 WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setTimestamp(1, hasta);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void actualizarToken(int id, String token) throws SQLException {
        String sql = "UPDATE usuarios SET session_token=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void actualizarVerificacion(int id, String codigo, Timestamp expiracion) throws SQLException {
        String sql = "UPDATE usuarios SET codigo_verificacion=?, codigo_expiracion=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setTimestamp(2, expiracion);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public static void marcarCorreoVerificado(int id) throws SQLException {
        String sql = "UPDATE usuarios SET correo_verificado=true, codigo_verificacion=NULL, codigo_expiracion=NULL WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public static void actualizarEstado(int id, String estado) throws SQLException {
        // Mapea 'estado' a campos: Activo → correo_verificado=1, Inactivo → correo_verificado=0
        // Para mayor compatibilidad, se usa cargo como referencia del estado activo
        String sql = "UPDATE usuarios SET correo_verificado=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, "Activo".equals(estado));
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void actualizarCargo(int id, String cargo) throws SQLException {
        String sql = "UPDATE usuarios SET cargo=?, updated_at=NOW() WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }
}
