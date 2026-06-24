package modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AsistenciaClaseDAO {

    public static void registrar(AsistenciaClase a) throws SQLException {
        String sql = "INSERT INTO asistencia_clases (instructor_id, aprendiz_id, ficha, fecha, presente, evaluacion) " +
                     "VALUES (?,?,?,NOW(),?,?)";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, a.getInstructorId());
            ps.setInt(2, a.getAprendizId());
            ps.setString(3, a.getFicha());
            ps.setBoolean(4, a.isPresente());
            ps.setString(5, a.getEvaluacion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
        }
    }

    public static List<AsistenciaClase> listarPorFicha(String ficha) throws SQLException {
        List<AsistenciaClase> lista = new ArrayList<>();
        String sql = "SELECT ac.*, " +
                     "ap.nombre AS nombre_aprendiz, ap.documento AS documento_aprendiz, " +
                     "ins.nombre AS nombre_instructor " +
                     "FROM asistencia_clases ac " +
                     "JOIN usuarios ap  ON ap.id  = ac.aprendiz_id " +
                     "JOIN usuarios ins ON ins.id = ac.instructor_id " +
                     "WHERE ac.ficha=? ORDER BY ac.fecha DESC";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ficha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static List<AsistenciaClase> listarRecientes(int limite) throws SQLException {
        List<AsistenciaClase> lista = new ArrayList<>();
        String sql = "SELECT ac.*, " +
                     "ap.nombre AS nombre_aprendiz, ap.documento AS documento_aprendiz, " +
                     "ins.nombre AS nombre_instructor " +
                     "FROM asistencia_clases ac " +
                     "JOIN usuarios ap  ON ap.id  = ac.aprendiz_id " +
                     "JOIN usuarios ins ON ins.id = ac.instructor_id " +
                     "ORDER BY ac.fecha DESC LIMIT ?";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public static List<AsistenciaClase> listarPorInstructor(int instructorId) throws SQLException {
        List<AsistenciaClase> lista = new ArrayList<>();
        String sql = "SELECT ac.*, " +
                     "ap.nombre AS nombre_aprendiz, ap.documento AS documento_aprendiz, " +
                     "ins.nombre AS nombre_instructor " +
                     "FROM asistencia_clases ac " +
                     "JOIN usuarios ap  ON ap.id  = ac.aprendiz_id " +
                     "JOIN usuarios ins ON ins.id = ac.instructor_id " +
                     "WHERE ac.instructor_id=? ORDER BY ac.fecha DESC";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    /** Devuelve aprendices de una ficha que ingresaron hoy (para el formulario de asistencia). */
    public static List<Usuario> listarAprendicesConAccesoHoy(String ficha) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT u.id, u.nombre, u.documento, u.ficha, u.programa, u.foto " +
                     "FROM usuarios u " +
                     "JOIN accesos a ON a.referencia_id=u.id AND a.tipo_referencia='Usuario' " +
                     "WHERE u.ficha=? AND u.cargo='Aprendiz' AND a.fecha::date = CURRENT_DATE AND a.tipo='Entrada' " +
                     "ORDER BY u.nombre";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, ficha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setNombre(rs.getString("nombre"));
                    u.setDocumento(rs.getString("documento"));
                    u.setFicha(rs.getString("ficha"));
                    u.setPrograma(rs.getString("programa"));
                    u.setFoto(rs.getString("foto"));
                    lista.add(u);
                }
            }
        }
        return lista;
    }

    private static AsistenciaClase mapear(ResultSet rs) throws SQLException {
        AsistenciaClase a = new AsistenciaClase();
        a.setId(rs.getInt("id"));
        a.setInstructorId(rs.getInt("instructor_id"));
        a.setAprendizId(rs.getInt("aprendiz_id"));
        a.setFicha(rs.getString("ficha"));
        a.setFecha(rs.getTimestamp("fecha"));
        a.setPresente(rs.getBoolean("presente"));
        a.setEvaluacion(rs.getString("evaluacion"));
        try { a.setNombreAprendiz(rs.getString("nombre_aprendiz")); } catch (SQLException ignored) {}
        try { a.setDocumentoAprendiz(rs.getString("documento_aprendiz")); } catch (SQLException ignored) {}
        try { a.setNombreInstructor(rs.getString("nombre_instructor")); } catch (SQLException ignored) {}
        return a;
    }
}
