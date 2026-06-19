package modelo;

public class Vehiculo {
    private int id;
    private String placa;
    private String tipo;        // SENA | Externo
    private String propietario;
    private String motivo;
    private String qrCode;
    private boolean activo;

    public Vehiculo() {}

    public Vehiculo(String placa, String tipo, String propietario, String motivo) {
        this.placa = placa.toUpperCase();
        this.tipo = tipo;
        this.propietario = propietario;
        this.motivo = motivo;
        this.activo = true;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = (placa != null) ? placa.toUpperCase() : null; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getQrCode() { return qrCode; }
    public void setQrCode(String qrCode) { this.qrCode = qrCode; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() { return placa + " (" + tipo + ")"; }
}
