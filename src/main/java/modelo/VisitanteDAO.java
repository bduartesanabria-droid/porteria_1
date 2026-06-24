package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VisitanteDAO {

    public static void crear(Visitante v) throws SQLException {
        String sql = "INSERT INTO visitantes (nombre, documento, motivo, qr_code, activo) VALUES (?,?,?,?,true)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getDocumento());
            ps.setString(3, v.getMotivo());
            ps.setString(4, v.getQrCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setId(keys.getInt(1));
            }
        }
    }

    public static Visitante buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM visitantes WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Visitante buscarPorDocumento(String documento) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM visitantes WHERE documento=?")) {
            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Visitante buscarPorQr(String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM visitantes WHERE qr_code=?")) {
            ps.setString(1, qrCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static List<Visitante> listarActivos() throws SQLException {
        List<Visitante> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM visitantes WHERE activo=true ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static List<Visitante> listarTodos() throws SQLException {
        List<Visitante> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM visitantes ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static void actualizar(Visitante v) throws SQLException {
        String sql = "UPDATE visitantes SET nombre=?, documento=?, motivo=?, activo=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getNombre());
            ps.setString(2, v.getDocumento());
            ps.setString(3, v.getMotivo());
            ps.setBoolean(4, v.isActivo());
            ps.setInt(5, v.getId());
            ps.executeUpdate();
        }
    }

    public static void actualizarQr(int id, String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE visitantes SET qr_code=? WHERE id=?")) {
            ps.setString(1, qrCode);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void desactivar(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE visitantes SET activo=false WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static Visitante mapear(ResultSet rs) throws SQLException {
        Visitante v = new Visitante();
        v.setId(rs.getInt("id"));
        v.setNombre(rs.getString("nombre"));
        v.setDocumento(rs.getString("documento"));
        v.setMotivo(rs.getString("motivo"));
        v.setQrCode(rs.getString("qr_code"));
        v.setActivo(rs.getBoolean("activo"));
        v.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return v;
    }
}
