package modelo;

import java.sql.Timestamp;

public class TurnoCelador {
    private int id;
    private int celadorId;
    private Timestamp fechaIngreso;
    private Timestamp fechaSalida;
    private String estado;   // Activo | Finalizado

    // Auxiliar (JOIN)
    private String nombreCelador;

    public TurnoCelador() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCeladorId() { return celadorId; }
    public void setCeladorId(int celadorId) { this.celadorId = celadorId; }

    public Timestamp getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(Timestamp fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public Timestamp getFechaSalida() { return fechaSalida; }
    public void setFechaSalida(Timestamp fechaSalida) { this.fechaSalida = fechaSalida; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getNombreCelador() { return nombreCelador; }
    public void setNombreCelador(String nombreCelador) { this.nombreCelador = nombreCelador; }
}
