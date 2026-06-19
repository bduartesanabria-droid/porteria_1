package controlador;

import modelo.*;

import java.sql.SQLException;

/**
 * Gestiona la creación y edición de pases manuales para
 * visitantes, vehículos y objetos externos.
 * Espejo de app/routes/porteria/pases.py de la web.
 */
public class PasesControlador {

    // ─── VISITANTES ───────────────────────────────────────────────────────────

    public enum ResultadoPase { OK, DUPLICADO, ERROR_BD }

    /**
     * Crea un visitante nuevo o reactiva uno existente con el mismo documento.
     */
    public static ResultadoPase crearOReactivarVisitante(String nombre, String documento, String motivo) {
        try {
            Visitante existente = VisitanteDAO.buscarPorDocumento(documento.trim());
            if (existente != null) {
                existente.setNombre(nombre.trim());
                existente.setMotivo(motivo);
                existente.setActivo(true);
                VisitanteDAO.actualizar(existente);
                // Generar QR si no tiene
                if (existente.getQrCode() == null || existente.getQrCode().isEmpty()) {
                    String qr = "SENA-VISIT:" + existente.getDocumento();
                    VisitanteDAO.actualizarQr(existente.getId(), qr);
                }
                return ResultadoPase.OK;
            }

            Visitante v = new Visitante(nombre.trim(), documento.trim(), motivo);
            v.setQrCode("SENA-VISIT:" + documento.trim());
            VisitanteDAO.crear(v);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static ResultadoPase actualizarVisitante(int id, String nombre, String documento, String motivo) {
        try {
            Visitante v = VisitanteDAO.buscarPorId(id);
            if (v == null) return ResultadoPase.ERROR_BD;
            v.setNombre(nombre.trim());
            v.setDocumento(documento.trim());
            v.setMotivo(motivo);
            VisitanteDAO.actualizar(v);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static void desactivarVisitante(int id) throws SQLException {
        VisitanteDAO.desactivar(id);
    }

    // ─── VEHÍCULOS ────────────────────────────────────────────────────────────

    /**
     * Crea un vehículo o reactiva uno existente con la misma placa.
     */
    public static ResultadoPase crearOReactivarVehiculo(String placa, String tipo,
                                                          String propietario, String motivo) {
        try {
            String placaUpper = placa.trim().toUpperCase();
            Vehiculo existente = VehiculoDAO.buscarPorPlaca(placaUpper);
            if (existente != null) {
                existente.setTipo(tipo);
                existente.setPropietario(propietario);
                existente.setMotivo(motivo);
                existente.setActivo(true);
                VehiculoDAO.actualizar(existente);
                if (existente.getQrCode() == null || existente.getQrCode().isEmpty()) {
                    String prefix = "SENA".equalsIgnoreCase(tipo) ? "SENA-VEH-S:" : "SENA-VEH-E:";
                    VehiculoDAO.actualizarQr(existente.getId(), prefix + placaUpper);
                }
                return ResultadoPase.OK;
            }

            String prefix = "SENA".equalsIgnoreCase(tipo) ? "SENA-VEH-S:" : "SENA-VEH-E:";
            Vehiculo v = new Vehiculo(placaUpper, tipo, propietario, motivo);
            v.setQrCode(prefix + placaUpper);
            VehiculoDAO.crear(v);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static ResultadoPase actualizarVehiculo(int id, String placa, String tipo,
                                                    String propietario, String motivo) {
        try {
            Vehiculo v = VehiculoDAO.buscarPorId(id);
            if (v == null) return ResultadoPase.ERROR_BD;
            v.setPlaca(placa.trim().toUpperCase());
            v.setTipo(tipo);
            v.setPropietario(propietario);
            v.setMotivo(motivo);
            VehiculoDAO.actualizar(v);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static void desactivarVehiculo(int id) throws SQLException {
        VehiculoDAO.desactivar(id);
    }

    // ─── OBJETOS EXTERNOS ─────────────────────────────────────────────────────

    /**
     * Crea un objeto externo. Genera serial automático si está vacío.
     */
    public static ResultadoPase crearObjetoExterno(String descripcion, String serial,
                                                    String propietario, String motivo) {
        try {
            // Auto-generar serial si está vacío
            String serialFinal = (serial == null || serial.trim().isEmpty())
                    ? "SN-" + System.currentTimeMillis()
                    : serial.trim();

            // Verificar duplicado de serial
            if (ObjetoExternoDAO.buscarPorSerial(serialFinal) != null) return ResultadoPase.DUPLICADO;

            ObjetoExterno obj = new ObjetoExterno(descripcion.trim(), serialFinal, propietario, motivo);
            obj.setQrCode("SENA-OBJ:" + serialFinal);
            ObjetoExternoDAO.crear(obj);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static ResultadoPase actualizarObjetoExterno(int id, String descripcion, String serial,
                                                          String propietario, String motivo) {
        try {
            ObjetoExterno obj = ObjetoExternoDAO.buscarPorId(id);
            if (obj == null) return ResultadoPase.ERROR_BD;
            obj.setDescripcion(descripcion.trim());
            if (serial != null && !serial.trim().isEmpty()) obj.setSerial(serial.trim());
            obj.setPropietario(propietario);
            obj.setMotivo(motivo);
            ObjetoExternoDAO.actualizar(obj);
            return ResultadoPase.OK;
        } catch (SQLException e) {
            e.printStackTrace();
            return ResultadoPase.ERROR_BD;
        }
    }

    public static void desactivarObjetoExterno(int id) throws SQLException {
        ObjetoExternoDAO.desactivar(id);
    }
}
