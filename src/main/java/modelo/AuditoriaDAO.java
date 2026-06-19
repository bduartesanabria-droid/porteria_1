package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AuditoriaDAO {

    public static void registrar(Auditoria a) throws SQLException {
        String sql = "INSERT INTO auditoria (usuario_id, nombre_usuario, tabla_afectada, registro_id, " +
                     "accion, autorizado_por, motivo, detalles, fecha) VALUES (?,?,?,?,?,?,?,?,NOW())";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            if (a.getUsuarioId() != null) ps.setInt(1, a.getUsuarioId()); else ps.setNull(1, Types.INTEGER);
            ps.setString(2, a.getNombreUsuario());
            ps.setString(3, a.getTablaAfectada());
            if (a.getRegistroId() != null) ps.setInt(4, a.getRegistroId()); else ps.setNull(4, Types.INTEGER);
            ps.setString(5, a.getAccion());
            ps.setString(6, a.getAutorizadoPor());
            ps.setString(7, a.getMotivo());
            ps.setString(8, a.getDetalles());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        }
    }

    public static List<Auditoria> listarTodos(int limite) throws SQLException {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria ORDER BY fecha DESC LIMIT ?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static List<Auditoria> listarPorTabla(String tabla, int limite) throws SQLException {
        List<Auditoria> lista = new ArrayList<>();
        String sql = "SELECT * FROM auditoria WHERE tabla_afectada=? ORDER BY fecha DESC LIMIT ?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tabla);
            ps.setInt(2, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    private static Auditoria mapear(ResultSet rs) throws SQLException {
        Auditoria a = new Auditoria();
        a.setId(rs.getInt("id"));
        int uid = rs.getInt("usuario_id");
        if (!rs.wasNull()) a.setUsuarioId(uid);
        a.setNombreUsuario(rs.getString("nombre_usuario"));
        a.setTablaAfectada(rs.getString("tabla_afectada"));
        int rid = rs.getInt("registro_id");
        if (!rs.wasNull()) a.setRegistroId(rid);
        a.setAccion(rs.getString("accion"));
        a.setAutorizadoPor(rs.getString("autorizado_por"));
        a.setMotivo(rs.getString("motivo"));
        a.setDetalles(rs.getString("detalles"));
        a.setFecha(rs.getTimestamp("fecha"));
        return a;
    }
}
