package controlador;

import modelo.*;

import java.sql.*;
import java.util.*;

/**
 * Analíticas por cargo/rol e historial de clases.
 * Espejo de app/routes/porteria/reportes.py de la web.
 */
public class ReportesControlador {

    public static class AnaliticaCargo {
        public String cargo;
        public int totalHoy;
        public int[] statsSemana;       // 7 días, índice 0 = más antiguo
        public Map<String, Integer> porPrograma;  // programa → total
        public Map<String, Integer> porFicha;     // ficha → total

        public AnaliticaCargo(String cargo) {
            this.cargo = cargo;
            this.porPrograma = new LinkedHashMap<>();
            this.porFicha = new LinkedHashMap<>();
        }
    }

    /** Devuelve analíticas completas para un cargo: hoy + semana + desglose. */
    public static AnaliticaCargo obtenerAnaliticasCargo(String cargo) throws SQLException {
        AnaliticaCargo an = new AnaliticaCargo(cargo);
        an.totalHoy = AccesoDAO.contarHoyPorCargo(cargo);
        an.statsSemana = AccesoDAO.statsSemana(cargo);
        an.porPrograma = contarPorProgramaHoy(cargo);
        an.porFicha = contarPorFichaHoy(cargo);
        return an;
    }

    /** Analíticas para los tres cargos principales de una vez. */
    public static List<AnaliticaCargo> obtenerTodos() throws SQLException {
        List<AnaliticaCargo> lista = new ArrayList<>();
        for (String cargo : new String[]{"Aprendiz", "Instructor", "Celador"}) {
            lista.add(obtenerAnaliticasCargo(cargo));
        }
        return lista;
    }

    /** Historial de asistencia a clases, con filtro opcional por ficha. */
    public static List<AsistenciaClase> obtenerHistorialClases(String ficha) throws SQLException {
        if (ficha != null && !ficha.isEmpty()) {
            return AsistenciaClaseDAO.listarPorFicha(ficha);
        }
        // Sin filtro: todas las fichas (solo para admin)
        return listarTodasLasAsistencias();
    }

    private static Map<String, Integer> contarPorProgramaHoy(String cargo) throws SQLException {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT u.programa, COUNT(*) AS total " +
                     "FROM accesos a JOIN usuarios u ON a.referencia_id=u.id AND a.tipo_referencia='Usuario' " +
                     "WHERE u.cargo=? AND a.fecha::date = CURRENT_DATE AND a.tipo='Entrada'" +
                     "GROUP BY u.programa ORDER BY total DESC";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String prog = rs.getString("programa");
                    mapa.put(prog != null ? prog : "Sin programa", rs.getInt("total"));
                }
            }
        }
        return mapa;
    }

    private static Map<String, Integer> contarPorFichaHoy(String cargo) throws SQLException {
        Map<String, Integer> mapa = new LinkedHashMap<>();
        String sql = "SELECT u.ficha, COUNT(*) AS total " +
                     "FROM accesos a JOIN usuarios u ON a.referencia_id=u.id AND a.tipo_referencia='Usuario' " +
                     "WHERE u.cargo=? AND a.fecha::date = CURRENT_DATE AND a.tipo='Entrada'" +
                     "GROUP BY u.ficha ORDER BY total DESC";
        try (Connection cn = Conexion.obtener(); PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, cargo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String ficha = rs.getString("ficha");
                    mapa.put(ficha != null ? ficha : "Sin ficha", rs.getInt("total"));
                }
            }
        }
        return mapa;
    }

    private static List<AsistenciaClase> listarTodasLasAsistencias() throws SQLException {
        List<AsistenciaClase> lista = new ArrayList<>();
        String sql = "SELECT ac.*, " +
                     "ap.nombre AS nombre_aprendiz, ap.documento AS documento_aprendiz, " +
                     "ins.nombre AS nombre_instructor " +
                     "FROM asistencia_clases ac " +
                     "JOIN usuarios ap  ON ap.id  = ac.aprendiz_id " +
                     "JOIN usuarios ins ON ins.id = ac.instructor_id " +
                     "ORDER BY ac.fecha DESC LIMIT 500";
        try (Connection cn = Conexion.obtener();
             PreparedStatement ps = cn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AsistenciaClase a = new AsistenciaClase();
                a.setId(rs.getInt("id"));
                a.setInstructorId(rs.getInt("instructor_id"));
                a.setAprendizId(rs.getInt("aprendiz_id"));
                a.setFicha(rs.getString("ficha"));
                a.setFecha(rs.getTimestamp("fecha"));
                a.setPresente(rs.getBoolean("presente"));
                a.setEvaluacion(rs.getString("evaluacion"));
                a.setNombreAprendiz(rs.getString("nombre_aprendiz"));
                a.setDocumentoAprendiz(rs.getString("documento_aprendiz"));
                a.setNombreInstructor(rs.getString("nombre_instructor"));
                lista.add(a);
            }
        }
        return lista;
    }
}
