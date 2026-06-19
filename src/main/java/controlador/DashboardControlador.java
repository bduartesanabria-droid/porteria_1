package controlador;

import modelo.*;

import java.io.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

/**
 * KPIs, historial de accesos y exportación CSV del dashboard principal.
 * Espejo de app/routes/porteria/dashboard.py de la web.
 */
public class DashboardControlador {

    private static final SimpleDateFormat FMT_FECHA = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat FMT_HORA  = new SimpleDateFormat("HH:mm:ss");

    /** Devuelve los KPIs del día: conteos por cargo y entidades dentro. */
    public static Map<String, Integer> obtenerKPIs() throws SQLException {
        return ScannerControlador.obtenerKPIs();
    }

    /** Historial de accesos con filtros opcionales de cargo y ficha. */
    public static List<Acceso> obtenerHistorial(String cargo, String ficha) throws SQLException {
        boolean hayFiltro = (cargo != null && !cargo.isEmpty()) || (ficha != null && !ficha.isEmpty());
        if (hayFiltro) {
            return AccesoDAO.listarConFiltros(cargo, ficha, 100);
        }
        return AccesoDAO.listarUltimos(100);
    }

    /** Estadísticas de los últimos 7 días agrupadas por cargo. */
    public static int[] statsSemana(String cargo) throws SQLException {
        return AccesoDAO.statsSemana(cargo);
    }

    /**
     * Exporta el historial de accesos a un archivo CSV.
     * Columnas: Documento, Nombre, Cargo, Programa, Ficha, Equipos, Tipo Acceso, Fecha, Hora
     */
    public static void exportarCSV(String rutaArchivo) throws SQLException, IOException {
        List<Acceso> accesos = AccesoDAO.listarUltimos(10_000);
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(
                new FileOutputStream(rutaArchivo), "UTF-8"))) {
            pw.println("Documento,Nombre,Cargo,Programa,Ficha,Equipos,Tipo Acceso,Fecha,Hora");
            for (Acceso a : accesos) {
                pw.printf("%s,%s,%s,%s,%s,%s,%s,%s,%s%n",
                    csv(a.getDocumentoPersona()),
                    csv(a.getNombrePersona()),
                    csv(a.getCargoPersona()),
                    csv(a.getProgramaPersona()),
                    csv(a.getFichaPersona()),
                    csv(a.getEquiposStr()),
                    csv(a.getTipo()),
                    a.getFecha() != null ? FMT_FECHA.format(a.getFecha()) : "",
                    a.getFecha() != null ? FMT_HORA.format(a.getFecha()) : "");
            }
        }
    }

    private static String csv(String valor) {
        if (valor == null) return "";
        return "\"" + valor.replace("\"", "\"\"") + "\"";
    }
}
