package modelo;

import java.sql.Timestamp;

public class CodigoQR {
    private int id;
    private int carnetId;
    private String codigo;
    private Timestamp fecha;

    public CodigoQR() {}

    public CodigoQR(int carnetId, String codigo) {
        this.carnetId = carnetId;
        this.codigo = codigo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCarnetId() { return carnetId; }
    public void setCarnetId(int carnetId) { this.carnetId = carnetId; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}
