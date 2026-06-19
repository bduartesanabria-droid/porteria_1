package modelo;

import java.sql.Timestamp;

public class ObjetoExterno {
    private int id;
    private String descripcion;
    private String serial;
    private String propietario;
    private String motivo;
    private boolean activo;
    private String qrCode;
    private Timestamp fechaCreacion;

    public ObjetoExterno() {}

    public ObjetoExterno(String descripcion, String serial, String propietario, String motivo) {
        this.descripcion = descripcion;
        this.serial = serial;
        this.propietario = propietario;
        this.motivo = motivo;
        this.activo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() { return descripcion + (serial != null ? " [" + serial + "]" : ""); }
}
