package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccesoDAO {

    public static void registrar(Acceso a) throws SQLException {
        String sql = "INSERT INTO accesos (punto_id, carnet_id, referencia_id, tipo_referencia, tipo, fecha, equipos_str) " +
                     "VALUES (?,?,?,?,?,NOW(),?)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getPuntoId() == 0 ? 1 : a.getPuntoId());
            if (a.getCarnetId() != null) ps.setInt(2, a.getCarnetId()); else ps.setNull(2, Types.INTEGER);
            if (a.getReferenciaId() != null) ps.setInt(3, a.getReferenciaId()); else ps.setNull(3, Types.INTEGER);
            ps.setString(4, a.getTipoReferencia());
            ps.setString(5, a.getTipo());
            ps.setString(6, a.getEquiposStr());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        }
    }

    /** Devuelve los últimos N accesos con datos del usuario/entidad (para el historial del dashboard). */
    public static List<Acceso> listarUltimos(int limite) throws SQLException {
        List<Acceso> lista = new ArrayList<>();
        String sql = "SELECT a.*, " +
                     "CASE a.tipo_referencia " +
                     "  WHEN 'Usuario' THEN u.nombre " +
                     "  WHEN 'Visitante' THEN v.nombre " +
                     "  WHEN 'Vehiculo' THEN vh.placa " +
                     "  WHEN 'ObjetoExterno' THEN ob.descripcion END AS nombre_persona, " +
                     "CASE a.tipo_referencia " +
                     "  WHEN 'Usuario' THEN u.documento " +
                     "  WHEN 'Visitante' THEN v.documento " +
                     "  ELSE NULL END AS documento_persona, " +
                     "u.cargo AS cargo_persona, u.programa AS programa_persona, " +
                     "u.ficha AS ficha_persona, u.foto AS foto_persona " +
                     "FROM accesos a " +
                     "LEFT JOIN usuarios u    ON a.tipo_referencia='Usuario'       AND a.referencia_id=u.id " +
                     "LEFT JOIN visitantes v  ON a.tipo_referencia='Visitante'     AND a.referencia_id=v.id " +
                     "LEFT JOIN vehiculos vh  ON a.tipo_referencia='Vehiculo'      AND a.referencia_id=vh.id " +
                     "LEFT JOIN objetos_externos ob ON a.tipo_referencia='ObjetoExterno' AND a.referencia_id=ob.id " +
                     "ORDER BY a.fecha DESC LIMIT ?";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Filtra por cargo y/o ficha. */
    public static List<Acceso> listarConFiltros(String cargo, String ficha, int limite) throws SQLException {
        List<Acceso> lista = new ArrayList<>();
        StringBuilder sb = new StringBuilder(
            "SELECT a.*, u.nombre AS nombre_persona, u.documento AS documento_persona, " +
            "u.cargo AS cargo_persona, u.programa AS programa_persona, " +
            "u.ficha AS ficha_persona, u.foto AS foto_persona " +
            "FROM accesos a JOIN usuarios u ON a.referencia_id=u.id AND a.tipo_referencia='Usuario' WHERE 1=1");
        if (cargo != null && !cargo.isEmpty()) sb.append(" AND u.cargo=?");
        if (ficha != null && !ficha.isEmpty()) sb.append(" AND u.ficha=?");
        sb.append(" ORDER BY a.fecha DESC LIMIT ?");
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sb.toString())) {
            int idx = 1;
            if (cargo != null && !cargo.isEmpty()) ps.setString(idx++, cargo);
            if (ficha != null && !ficha.isEmpty()) ps.setString(idx++, ficha);
            ps.setInt(idx, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Obtiene el último acceso de una entidad para detectar discrepancias. */
    public static Acceso obtenerUltimoAcceso(int referenciaId, String tipoReferencia) throws SQLException {
        String sql = "SELECT a.* FROM accesos a WHERE a.referencia_id=? AND a.tipo_referencia=? " +
                     "ORDER BY a.fecha DESC LIMIT 1";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, referenciaId);
            ps.setString(2, tipoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        }
    }

    /** Cuenta accesos de hoy agrupados por cargo (para KPIs del dashboard). */
    public static int contarHoyPorCargo(String cargo) throws SQLException {
        String sql = "SELECT COUNT(*) FROM accesos a JOIN usuarios u ON a.referencia_id=u.id " +
                     "AND a.tipo_referencia='Usuario' " +
                     "WHERE a.fecha::date = CURRENT_DATE AND u.cargo=? AND a.tipo='Entrada'";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Cuenta entidades externas actualmente dentro (último acceso = Entrada). */
    public static int contarDentro(String tipoReferencia) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT a.referencia_id) FROM accesos a " +
                     "WHERE a.tipo_referencia = ? AND a.tipo = 'Entrada' " +
                     "AND NOT EXISTS (" +
                     "  SELECT 1 FROM accesos a2 " +
                     "  WHERE a2.referencia_id = a.referencia_id " +
                     "    AND a2.tipo_referencia = a.tipo_referencia " +
                     "    AND a2.fecha > a.fecha" +
                     ")";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, tipoReferencia);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    /** Stats de accesos por día (últimos 7 días) filtrado por cargo. */
    public static int[] statsSemana(String cargo) throws SQLException {
        int[] dias = new int[7];
        String sql = "SELECT (CURRENT_DATE - a.fecha::date) AS dias_atras, COUNT(*) AS total " +
                     "FROM accesos a JOIN usuarios u ON a.referencia_id=u.id AND a.tipo_referencia='Usuario' " +
                     "WHERE u.cargo=? AND a.tipo='Entrada' AND a.fecha >= CURRENT_DATE - INTERVAL '6 days' " +
                     "GROUP BY dias_atras";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int d = rs.getInt("dias_atras");
                    if (d >= 0 && d < 7) dias[6 - d] = rs.getInt("total");
                }
            }
        }
        return dias;
    }

    private static Acceso mapear(ResultSet rs) throws SQLException {
        Acceso a = new Acceso();
        a.setId(rs.getInt("id"));
        a.setPuntoId(rs.getInt("punto_id"));
        int cid = rs.getInt("carnet_id");
        if (!rs.wasNull()) a.setCarnetId(cid);
        int rid = rs.getInt("referencia_id");
        if (!rs.wasNull()) a.setReferenciaId(rid);
        a.setTipoReferencia(rs.getString("tipo_referencia"));
        a.setTipo(rs.getString("tipo"));
        a.setFecha(rs.getTimestamp("fecha"));
        a.setEquiposStr(rs.getString("equipos_str"));
        try { a.setNombrePersona(rs.getString("nombre_persona")); } catch (SQLException ignored) {}
        try { a.setDocumentoPersona(rs.getString("documento_persona")); } catch (SQLException ignored) {}
        try { a.setCargoPersona(rs.getString("cargo_persona")); } catch (SQLException ignored) {}
        try { a.setProgramaPersona(rs.getString("programa_persona")); } catch (SQLException ignored) {}
        try { a.setFichaPersona(rs.getString("ficha_persona")); } catch (SQLException ignored) {}
        try { a.setFotoPersona(rs.getString("foto_persona")); } catch (SQLException ignored) {}
        return a;
    }
}
