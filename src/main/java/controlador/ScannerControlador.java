package controlador;

import modelo.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Procesa lecturas de QR y registra movimientos de entrada/salida.
 * Espejo de app/routes/porteria/scanner.py de la web.
 *
 * Prefijos QR reconocidos:
 *   SENA-CARNET:<documento>  → Usuario
 *   SENA-VISIT:<documento>   → Visitante
 *   SENA-VEH-S:<placa>       → Vehículo SENA
 *   SENA-VEH-E:<placa>       → Vehículo Externo
 *   SENA-OBJ:<serial>        → ObjetoExterno
 *   <solo dígitos>           → buscar como documento de Usuario
 */
public class ScannerControlador {

    public enum TipoEntidad { USUARIO, VISITANTE, VEHICULO, OBJETO_EXTERNO, DESCONOCIDO }

    public static class ResultadoVerificacion {
        public TipoEntidad tipo;
        public Object entidad;   // Usuario | Visitante | Vehiculo | ObjetoExterno
        public boolean encontrado;
        public String mensaje;

        ResultadoVerificacion(TipoEntidad tipo, Object entidad, String mensaje) {
            this.tipo = tipo;
            this.entidad = entidad;
            this.encontrado = entidad != null;
            this.mensaje = mensaje;
        }
    }

    /** Parsea el código QR/texto y devuelve la entidad correspondiente. */
    public static ResultadoVerificacion verificar(String codigoQR) throws SQLException {
        if (codigoQR == null || codigoQR.trim().isEmpty()) {
            return new ResultadoVerificacion(TipoEntidad.DESCONOCIDO, null, "Código vacío");
        }
        String codigo = codigoQR.trim();

        if (codigo.startsWith("SENA-CARNET:")) {
            String doc = codigo.substring("SENA-CARNET:".length());
            Usuario u = UsuarioDAO.buscarPorDocumento(doc);
            return new ResultadoVerificacion(TipoEntidad.USUARIO, u, u != null ? "Usuario encontrado" : "Usuario no encontrado");
        }
        if (codigo.startsWith("SENA-VISIT:")) {
            String doc = codigo.substring("SENA-VISIT:".length());
            Visitante v = VisitanteDAO.buscarPorDocumento(doc);
            if (v == null) v = VisitanteDAO.buscarPorQr(codigo);
            return new ResultadoVerificacion(TipoEntidad.VISITANTE, v, v != null ? "Visitante encontrado" : "Visitante no encontrado");
        }
        if (codigo.startsWith("SENA-VEH-S:") || codigo.startsWith("SENA-VEH-E:")) {
            String placa = codigo.contains("SENA-VEH-S:")
                    ? codigo.substring("SENA-VEH-S:".length())
                    : codigo.substring("SENA-VEH-E:".length());
            Vehiculo vh = VehiculoDAO.buscarPorPlaca(placa);
            if (vh == null) vh = VehiculoDAO.buscarPorQr(codigo);
            return new ResultadoVerificacion(TipoEntidad.VEHICULO, vh, vh != null ? "Vehículo encontrado" : "Vehículo no encontrado");
        }
        if (codigo.startsWith("SENA-OBJ:")) {
            String serial = codigo.substring("SENA-OBJ:".length());
            ObjetoExterno obj = ObjetoExternoDAO.buscarPorSerial(serial);
            if (obj == null) obj = ObjetoExternoDAO.buscarPorQr(codigo);
            return new ResultadoVerificacion(TipoEntidad.OBJETO_EXTERNO, obj, obj != null ? "Objeto encontrado" : "Objeto no encontrado");
        }

        // Sin prefijo → buscar como documento de usuario
        Usuario u = UsuarioDAO.buscarPorDocumento(codigo);
        if (u != null) return new ResultadoVerificacion(TipoEntidad.USUARIO, u, "Usuario encontrado");

        // Intentar como visitante por documento
        Visitante v = VisitanteDAO.buscarPorDocumento(codigo);
        if (v != null) return new ResultadoVerificacion(TipoEntidad.VISITANTE, v, "Visitante encontrado");

        return new ResultadoVerificacion(TipoEntidad.DESCONOCIDO, null, "Entidad no encontrada para: " + codigo);
    }

    // ─── REGISTRO DE MOVIMIENTOS ──────────────────────────────────────────────

    public enum ResultadoMovimiento { OK, DISCREPANCIA, ENTIDAD_NO_ENCONTRADA, ERROR_BD }

    /** Registra entrada/salida de un Usuario. Detecta discrepancias. */
    public static ResultadoMovimiento registrarMovimientoUsuario(int usuarioId, String tipo,
                                                                  String equiposStr) {
        try {
            // Obtener carnet del usuario
            Carnet carnet = obtenerCarnet(usuarioId);

            Acceso ultimo = AccesoDAO.obtenerUltimoAcceso(usuarioId, "Usuario");
            boolean discrepancia = detectarDiscrepancia(ultimo, tipo);

            Acceso acceso = new Acceso();
            acceso.setPuntoId(1);
            if (carnet != null) acceso.setCarnetId(carnet.getId());
            acceso.setReferenciaId(usuarioId);
            acceso.setTipoReferencia("Usuario");
            acceso.setTipo(tipo);
            acceso.setEquiposStr(equiposStr);
            AccesoDAO.registrar(acceso);

            // Actualizar estado de equipos
            if (equiposStr != null && !equiposStr.isEmpty()) {
                actualizarEstadoEquipos(equiposStr, tipo);
            }

            return discrepancia ? ResultadoMovimiento.DISCREPANCIA : ResultadoMovimiento.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoMovimiento.ERROR_BD;
        }
    }

    /** Registra entrada/salida de Visitante, Vehículo u ObjetoExterno. */
    public static ResultadoMovimiento registrarMovimientoEntidad(String tipoEntidad, int entidadId, String tipo) {
        try {
            Acceso ultimo = AccesoDAO.obtenerUltimoAcceso(entidadId, tipoEntidad);
            boolean discrepancia = detectarDiscrepancia(ultimo, tipo);

            Acceso acceso = new Acceso();
            acceso.setPuntoId(1);
            acceso.setReferenciaId(entidadId);
            acceso.setTipoReferencia(tipoEntidad);
            acceso.setTipo(tipo);
            AccesoDAO.registrar(acceso);

            return discrepancia ? ResultadoMovimiento.DISCREPANCIA : ResultadoMovimiento.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoMovimiento.ERROR_BD;
        }
    }

    /** Auto-registra salida de todos los que están dentro (se llama a medianoche). */
    public static void autoSalidaTotal() throws SQLException {
        String sql = "SELECT referencia_id, tipo_referencia FROM accesos a1 " +
                     "WHERE tipo='Entrada' AND NOT EXISTS (" +
                     "  SELECT 1 FROM accesos a2 WHERE a2.referencia_id=a1.referencia_id " +
                     "  AND a2.tipo_referencia=a1.tipo_referencia AND a2.tipo='Salida' AND a2.fecha>a1.fecha" +
                     ")";
        try (java.sql.Connection cn = Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql);
             java.sql.ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int refId = rs.getInt("referencia_id");
                String tipoRef = rs.getString("tipo_referencia");
                Acceso salida = new Acceso();
                salida.setPuntoId(1);
                salida.setReferenciaId(refId);
                salida.setTipoReferencia(tipoRef);
                salida.setTipo("Salida");
                AccesoDAO.registrar(salida);
            }
        }
    }

    // ─── KPIs rápidos para el dashboard ──────────────────────────────────────

    public static Map<String, Integer> obtenerKPIs() throws SQLException {
        Map<String, Integer> kpis = new HashMap<>();
        kpis.put("aprendices_hoy", AccesoDAO.contarHoyPorCargo("Aprendiz"));
        kpis.put("instructores_hoy", AccesoDAO.contarHoyPorCargo("Instructor"));
        kpis.put("trabajadores_hoy", AccesoDAO.contarHoyPorCargo("Celador"));
        kpis.put("visitantes_dentro", AccesoDAO.contarDentro("Visitante"));
        kpis.put("vehiculos_dentro", AccesoDAO.contarDentro("Vehiculo"));
        kpis.put("objetos_dentro", AccesoDAO.contarDentro("ObjetoExterno"));
        return kpis;
    }

    // ─── Helpers privados ─────────────────────────────────────────────────────

    private static boolean detectarDiscrepancia(Acceso ultimo, String tipoNuevo) {
        if (ultimo == null) return "Salida".equals(tipoNuevo); // salida sin entrada previa
        return ultimo.getTipo().equals(tipoNuevo); // doble entrada o doble salida
    }

    private static Carnet obtenerCarnet(int usuarioId) throws SQLException {
        String sql = "SELECT * FROM carnets WHERE usuario_id=?";
        try (java.sql.Connection cn = Conexion.obtener();
             java.sql.PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Carnet c = new Carnet();
                    c.setId(rs.getInt("id"));
                    c.setUsuarioId(rs.getInt("usuario_id"));
                    return c;
                }
            }
        }
        return null;
    }

    private static void actualizarEstadoEquipos(String equiposStr, String tipoMovimiento) throws SQLException {
        String nuevoEstado = "Entrada".equals(tipoMovimiento) ? "Adentro" : "Afuera";
        for (String idStr : equiposStr.split(",")) {
            try {
                int equipoId = Integer.parseInt(idStr.trim());
                EquipoDAO.cambiarEstado(equipoId, nuevoEstado);
            } catch (NumberFormatException ignored) {}
        }
    }
}
