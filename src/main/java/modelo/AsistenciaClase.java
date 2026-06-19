package modelo;

import java.sql.Timestamp;

public class AsistenciaClase {
    private int id;
    private int instructorId;
    private int aprendizId;
    private String ficha;
    private Timestamp fecha;
    private boolean presente;
    private String evaluacion;

    // Auxiliares (JOIN)
    private String nombreAprendiz;
    private String documentoAprendiz;
    private String nombreInstructor;

    public AsistenciaClase() {}

    public AsistenciaClase(int instructorId, int aprendizId, String ficha, boolean presente) {
        this.instructorId = instructorId;
        this.aprendizId = aprendizId;
        this.ficha = ficha;
        this.presente = presente;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getInstructorId() { return instructorId; }
    public void setInstructorId(int instructorId) { this.instructorId = instructorId; }

    public int getAprendizId() { return aprendizId; }
    public void setAprendizId(int aprendizId) { this.aprendizId = aprendizId; }

    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public boolean isPresente() { return presente; }
    public void setPresente(boolean presente) { this.presente = presente; }

    public String getEvaluacion() { return evaluacion; }
    public void setEvaluacion(String evaluacion) { this.evaluacion = evaluacion; }

    public String getNombreAprendiz() { return nombreAprendiz; }
    public void setNombreAprendiz(String nombreAprendiz) { this.nombreAprendiz = nombreAprendiz; }

    public String getDocumentoAprendiz() { return documentoAprendiz; }
    public void setDocumentoAprendiz(String documentoAprendiz) { this.documentoAprendiz = documentoAprendiz; }

    public String getNombreInstructor() { return nombreInstructor; }
    public void setNombreInstructor(String nombreInstructor) { this.nombreInstructor = nombreInstructor; }
}
