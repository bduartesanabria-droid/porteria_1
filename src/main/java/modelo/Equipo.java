package modelo;

public class Equipo {
    private int id;
    private String nombre;
    private String serial;
    private String tipo;       // Portátil, Celular, Tablet, etc.
    private String estado;     // Adentro | Afuera
    private Integer usuarioId;

    // Auxiliar (JOIN)
    private String nombreUsuario;

    public Equipo() {}

    public Equipo(String nombre, String serial, String tipo, Integer usuarioId) {
        this.nombre = nombre;
        this.serial = serial;
        this.tipo = tipo;
        this.usuarioId = usuarioId;
        this.estado = "Afuera";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getSerial() { return serial; }
    public void setSerial(String serial) { this.serial = serial; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }

    @Override
    public String toString() { return nombre + (serial != null ? " [" + serial + "]" : ""); }
}
