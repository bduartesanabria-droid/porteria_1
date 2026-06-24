package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehiculoDAO {

    public static void crear(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculos (placa, tipo, propietario, motivo, qr_code, activo) VALUES (?,?,?,?,?,true)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getTipo());
            ps.setString(3, v.getPropietario());
            ps.setString(4, v.getMotivo());
            ps.setString(5, v.getQrCode());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) v.setId(keys.getInt(1));
            }
        }
    }

    public static Vehiculo buscarPorId(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM vehiculos WHERE id=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Vehiculo buscarPorPlaca(String placa) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM vehiculos WHERE placa=?")) {
            ps.setString(1, placa.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static Vehiculo buscarPorQr(String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM vehiculos WHERE qr_code=?")) {
            ps.setString(1, qrCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    public static List<Vehiculo> listarActivos() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM vehiculos WHERE activo=true ORDER BY placa");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static List<Vehiculo> listarTodos() throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("SELECT * FROM vehiculos ORDER BY placa");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public static void actualizar(Vehiculo v) throws SQLException {
        String sql = "UPDATE vehiculos SET placa=?, tipo=?, propietario=?, motivo=?, activo=? WHERE id=?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, v.getPlaca());
            ps.setString(2, v.getTipo());
            ps.setString(3, v.getPropietario());
            ps.setString(4, v.getMotivo());
            ps.setBoolean(5, v.isActivo());
            ps.setInt(6, v.getId());
            ps.executeUpdate();
        }
    }

    public static void actualizarQr(int id, String qrCode) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE vehiculos SET qr_code=? WHERE id=?")) {
            ps.setString(1, qrCode);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public static void desactivar(int id) throws SQLException {
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement("UPDATE vehiculos SET activo=false WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private static Vehiculo mapear(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getInt("id"));
        v.setPlaca(rs.getString("placa"));
        v.setTipo(rs.getString("tipo"));
        v.setPropietario(rs.getString("propietario"));
        v.setMotivo(rs.getString("motivo"));
        v.setQrCode(rs.getString("qr_code"));
        v.setActivo(rs.getBoolean("activo"));
        return v;
    }
}
