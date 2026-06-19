package modelo;

import java.sql.Timestamp;

public class Carnet {
    private int id;
    private int usuarioId;
    private Timestamp createdAt;

    public Carnet() {}

    public Carnet(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
