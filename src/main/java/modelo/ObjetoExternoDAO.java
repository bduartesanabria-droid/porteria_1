package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ObjetoExternoDAO {

    public static void crear(ObjetoExterno o) throws SQLException {
        String sql = "INSERT INTO objetos_externos (descripcion, serial, propietario, motivo, qr_code, activo) " +
                     "VALUES (?,?,?,?,?,true)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, o.getDescripcion());
            ps.setString(2, o.getSerial());
            ps.setString(3, o.getPropietario());
            ps.setString(4, o.getMotivo());
            ps.setString(5, o.getQrCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getInt(1));
            }
        }
    }

    public static ObjetoExterno buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM objetos_externos WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static ObjetoExterno buscarPorSerial(String serial) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM objetos_externos WHERE serial=?")) {
            ps.setString(1, serial);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static ObjetoExterno buscarPorQr(String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM objetos_externos WHERE qr_code=?")) {
            ps.setString(1, qrCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static List<ObjetoExterno> listarActivos() throws SQLException {
        List<ObjetoExterno> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(
                 "SELECT * FROM objetos_externos WHERE activo=true ORDER BY descripcion");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static void actualizar(ObjetoExterno o) throws SQLException {
        String sql = "UPDATE objetos_externos SET descripcion=?, serial=?, propietario=?, motivo=?, activo=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, o.getDescripcion());
            ps.setString(2, o.getSerial());
            ps.setString(3, o.getPropietario());
            ps.setString(4, o.getMotivo());
            ps.setBoolean(5, o.isActivo());
            ps.setInt(6, o.getId());
            ps.executeUpdate();
        }
    }

    public static void actualizarQr(int id, String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE objetos_externos SET qr_code=? WHERE id=?")) {
            ps.setString(1, qrCode);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void desactivar(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE objetos_externos SET activo=false WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static ObjetoExterno mapear(ResultSet rs) throws SQLException {
        ObjetoExterno o = new ObjetoExterno();
        o.setId(rs.getInt("id"));
        o.setDescripcion(rs.getString("descripcion"));
        o.setSerial(rs.getString("serial"));
        o.setPropietario(rs.getString("propietario"));
        o.setMotivo(rs.getString("motivo"));
        o.setActivo(rs.getBoolean("activo"));
        o.setQrCode(rs.getString("qr_code"));
        o.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
        return o;
    }
}
