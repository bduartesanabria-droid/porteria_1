package controlador;

import modelo.*;

import java.sql.SQLException;
import java.util.List;

/**
 * Control de asistencia a clases por parte del instructor.
 * Espejo de app/routes/usuarios/asistencia.py de la web.
 */
public class AsistenciaControlador {

    /**
     * Devuelve aprendices de la ficha del instructor que ingresaron hoy,
     * para que el instructor los marque como presentes/ausentes.
     */
    public static List<Usuario> obtenerAprendicesParaAsistencia(String ficha) throws SQLException {
        return AsistenciaClaseDAO.listarAprendicesConAccesoHoy(ficha);
    }

    /**
     * Guarda la asistencia de un aprendiz.
     * Si ya existe registro del mismo instructor+aprendiz hoy, lo actualiza.
     */
    public static void registrarAsistencia(int instructorId, int aprendizId,
                                            String ficha, boolean presente, String evaluacion) throws SQLException {
        // Verificar si ya existe registro hoy
        AsistenciaClase existente = buscarAsistenciaHoy(instructorId, aprendizId);
        if (existente != null) {
            actualizarAsistencia(existente.getId(), presente, evaluacion);
        } else {
            AsistenciaClase a = new AsistenciaClase(instructorId, aprendizId, ficha, presente);
            a.setEvaluacion(evaluacion);
            AsistenciaClaseDAO.registrar(a);
        }
    }

    /** Registra asistencia en lote para todos los aprendices de la lista. */
    public static void registrarAsistenciaLote(int instructorId, String ficha,
                                                List<AsistenciaClase> asistencias) throws SQLException {
        for (AsistenciaClase a : asistencias) {
            registrarAsistencia(instructorId, a.getAprendizId(), ficha, a.isPresente(), a.getEvaluacion());
        }
    }

    /** Historial de clases de una ficha. */
    public static List<AsistenciaClase> obtenerHistorial(String ficha) throws SQLException {
        return AsistenciaClaseDAO.listarPorFicha(ficha);
    }

    /** Historial de clases dictadas por un instructor. */
    public static List<AsistenciaClase> obtenerHistorialInstructor(int instructorId) throws SQLException {
        return AsistenciaClaseDAO.listarPorInstructor(instructorId);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private static AsistenciaClase buscarAsistenciaHoy(int instructorId, int aprendizId) throws SQLException {
        String sql = "SELECT * FROM asistencia_clases WHERE instructor_id=? AND aprendiz_id=? " +
                     "AND fecha::date = CURRENT_DATE ORDER BY fecha DESC LIMIT 1";
        try (java.sql.Connection cn = Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, instructorId);
            ps.setInt(2, aprendizId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    AsistenciaClase a = new AsistenciaClase();
                    a.setId(rs.getInt("id"));
                    a.setInstructorId(rs.getInt("instructor_id"));
                    a.setAprendizId(rs.getInt("aprendiz_id"));
                    a.setFicha(rs.getString("ficha"));
                    a.setFecha(rs.getTimestamp("fecha"));
                    a.setPresente(rs.getBoolean("presente"));
                    a.setEvaluacion(rs.getString("evaluacion"));
                    return a;
                }
            }
        }
        return null;
    }

    private static void actualizarAsistencia(int id, boolean presente, String evaluacion) throws SQLException {
        String sql = "UPDATE asistencia_clases SET presente=?, evaluacion=? WHERE id=?";
        try (java.sql.Connection cn = Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setBoolean(1, presente);
            ps.setString(2, evaluacion);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }
}
