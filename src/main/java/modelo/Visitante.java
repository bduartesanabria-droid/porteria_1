package modelo;

import java.sql.Timestamp;

public class Visitante {
    private int id;
    private String nombre;
    private String documento;
    private String motivo;
    private String qrCode;
    private boolean activo;
    private Timestamp fechaCreacion;

    public Visitante() {}

    public Visitante(String nombre, String documento, String motivo) {
        this.nombre = nombre;
        this.documento = documento;
        this.motivo = motivo;
        this.activo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Timestamp getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(Timestamp fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    @Override
    public String toString() { return nombre + " (" + documento + ")"; }
}
