package modelo;

import java.sql.Timestamp;

public class Acceso {
    private int id;
    private int puntoId;
    private Integer carnetId;        // nullable
    private Integer referenciaId;    // nullable (id del usuario/visitante/vehiculo/objeto)
    private String tipoReferencia;   // Usuario | Visitante | Vehiculo | ObjetoExterno
    private String tipo;             // Entrada | Salida
    private Timestamp fecha;
    private String equiposStr;       // IDs de equipos separados por coma

    // Auxiliares (JOINs para mostrar en la UI)
    private String nombrePersona;
    private String documentoPersona;
    private String cargoPersona;
    private String programaPersona;
    private String fichaPersona;
    private String fotoPersona;

    public Acceso() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPuntoId() { return puntoId; }
    public void setPuntoId(int puntoId) { this.puntoId = puntoId; }

    public Integer getCarnetId() { return carnetId; }
    public void setCarnetId(Integer carnetId) { this.carnetId = carnetId; }

    public Integer getReferenciaId() { return referenciaId; }
    public void setReferenciaId(Integer referenciaId) { this.referenciaId = referenciaId; }

    public String getTipoReferencia() { return tipoReferencia; }
    public void setTipoReferencia(String tipoReferencia) { this.tipoReferencia = tipoReferencia; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public String getEquiposStr() { return equiposStr; }
    public void setEquiposStr(String equiposStr) { this.equiposStr = equiposStr; }

    public String getNombrePersona() { return nombrePersona; }
    public void setNombrePersona(String nombrePersona) { this.nombrePersona = nombrePersona; }

    public String getDocumentoPersona() { return documentoPersona; }
    public void setDocumentoPersona(String documentoPersona) { this.documentoPersona = documentoPersona; }

    public String getCargoPersona() { return cargoPersona; }
    public void setCargoPersona(String cargoPersona) { this.cargoPersona = cargoPersona; }

    public String getProgramaPersona() { return programaPersona; }
    public void setProgramaPersona(String programaPersona) { this.programaPersona = programaPersona; }

    public String getFichaPersona() { return fichaPersona; }
    public void setFichaPersona(String fichaPersona) { this.fichaPersona = fichaPersona; }

    public String getFotoPersona() { return fotoPersona; }
    public void setFotoPersona(String fotoPersona) { this.fotoPersona = fotoPersona; }
}
