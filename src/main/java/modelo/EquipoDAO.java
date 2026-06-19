package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipoDAO {

    public static void crear(Equipo e) throws SQLException {
        String sql = "INSERT INTO equipos (nombre, serial, tipo, estado, usuario_id) VALUES (?,?,?,?,?)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getSerial());
            ps.setString(3, e.getTipo());
            ps.setString(4, e.getEstado() != null ? e.getEstado() : "Afuera");
            if (e.getUsuarioId() != null) ps.setInt(5, e.getUsuarioId()); else ps.setNull(5, Types.INTEGER);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) e.setId(keys.getInt(1));
            }
        }
    }

    public static Equipo buscarPorId(int id) throws SQLException {
        String sql = "SELECT eq.*, u.nombre AS nombre_usuario FROM equipos eq " +
                     "LEFT JOIN usuarios u ON u.id=eq.usuario_id WHERE eq.id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Equipo buscarPorSerial(String serial) throws SQLException {
        String sql = "SELECT eq.*, u.nombre AS nombre_usuario FROM equipos eq " +
                     "LEFT JOIN usuarios u ON u.id=eq.usuario_id WHERE eq.serial=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static List<Equipo> listarPorUsuario(int usuarioId) throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT eq.*, u.nombre AS nombre_usuario FROM equipos eq " +
                     "LEFT JOIN usuarios u ON u.id=eq.usuario_id WHERE eq.usuario_id=? ORDER BY eq.nombre";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static List<Equipo> listarTodos() throws SQLException {
        List<Equipo> lista = new ArrayList<>();
        String sql = "SELECT eq.*, u.nombre AS nombre_usuario FROM equipos eq " +
                     "LEFT JOIN usuarios u ON u.id=eq.usuario_id ORDER BY eq.nombre";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static void actualizar(Equipo e) throws SQLException {
        String sql = "UPDATE equipos SET nombre=?, serial=?, tipo=?, usuario_id=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getNombre());
            ps.setString(2, e.getSerial());
            ps.setString(3, e.getTipo());
            if (e.getUsuarioId() != null) ps.setInt(4, e.getUsuarioId()); else ps.setNull(4, Types.INTEGER);
            ps.setInt(5, e.getId());
            ps.executeUpdate();
        }
    }

    public static void cambiarEstado(int id, String estado) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE equipos SET estado=? WHERE id=?")) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    private static Equipo mapear(ResultSet rs) throws SQLException {
        Equipo e = new Equipo();
        e.setId(rs.getInt("id"));
        e.setNombre(rs.getString("nombre"));
        e.setSerial(rs.getString("serial"));
        e.setTipo(rs.getString("tipo"));
        e.setEstado(rs.getString("estado"));
        int uid = rs.getInt("usuario_id");
        if (!rs.wasNull()) e.setUsuarioId(uid);
        try { e.setNombreUsuario(rs.getString("nombre_usuario")); } catch (SQLException ignored) {}
        return e;
    }
}
