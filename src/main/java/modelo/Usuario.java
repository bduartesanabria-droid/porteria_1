package modelo;

import java.sql.Timestamp;

public class Usuario {

    private int id;
    private String nombre;
    private String correo;
    private String documento;
    private String contrasenaHash;
    private int rolId;
    private String cargo;           // Aprendiz | Instructor | Celador | Administrador
    private boolean perfilCompleto;
    private boolean correoVerificado;
    private String codigoVerificacion;
    private Timestamp codigoExpiracion;
    private int intentosFallidos;
    private Timestamp bloqueadoHasta;
    private boolean debeCambiarContrasena;
    private String codigoRecuperacion;
    private Timestamp recuperacionExpiracion;
    private String programa;
    private String ficha;
    private String horario;         // Diurna | Nocturna | Fines de semana | Mixta
    private String tipoSangre;
    private String foto;
    private String sessionToken;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Campo auxiliar — no está en la BD, se llena con JOIN
    private String rolNombre;

    public Usuario() {}

    // ─── Permisos derivados (espejo de las @property de Python) ───────────────

    public boolean esAdmin() {
        return "Admin".equalsIgnoreCase(rolNombre);
    }

    public boolean esAprendiz() {
        return "Aprendiz".equalsIgnoreCase(cargo);
    }

    public boolean esInstructor() {
        return "Instructor".equalsIgnoreCase(cargo);
    }

    public boolean esCelador() {
        return "Celador".equalsIgnoreCase(cargo) || "Portería".equalsIgnoreCase(cargo);
    }

    public boolean puedeOperarPorteria() {
        return esCelador() || esAdmin();
    }

    public boolean puedeGestionarAsistencia() {
        return esInstructor() || esAdmin();
    }

    public boolean puedeRegistrarEquipos() {
        return esInstructor() || esAdmin() || esCelador();
    }

    // ─── Getters / Setters ────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getContrasenaHash() { return contrasenaHash; }
    public void setContrasenaHash(String contrasenaHash) { this.contrasenaHash = contrasenaHash; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public boolean isPerfilCompleto() { return perfilCompleto; }
    public void setPerfilCompleto(boolean perfilCompleto) { this.perfilCompleto = perfilCompleto; }

    public boolean isCorreoVerificado() { return correoVerificado; }
    public void setCorreoVerificado(boolean correoVerificado) { this.correoVerificado = correoVerificado; }

    public String getCodigoVerificacion() { return codigoVerificacion; }
    public void setCodigoVerificacion(String codigoVerificacion) { this.codigoVerificacion = codigoVerificacion; }

    public Timestamp getCodigoExpiracion() { return codigoExpiracion; }
    public void setCodigoExpiracion(Timestamp codigoExpiracion) { this.codigoExpiracion = codigoExpiracion; }

    public int getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(int intentosFallidos) { this.intentosFallidos = intentosFallidos; }

    public Timestamp getBloqueadoHasta() { return bloqueadoHasta; }
    public void setBloqueadoHasta(Timestamp bloqueadoHasta) { this.bloqueadoHasta = bloqueadoHasta; }

    public boolean isDebeCambiarContrasena() { return debeCambiarContrasena; }
    public void setDebeCambiarContrasena(boolean debeCambiarContrasena) {
        this.debeCambiarContrasena = debeCambiarContrasena;
    }

    public String getCodigoRecuperacion() { return codigoRecuperacion; }
    public void setCodigoRecuperacion(String codigoRecuperacion) { this.codigoRecuperacion = codigoRecuperacion; }

    public Timestamp getRecuperacionExpiracion() { return recuperacionExpiracion; }
    public void setRecuperacionExpiracion(Timestamp recuperacionExpiracion) {
        this.recuperacionExpiracion = recuperacionExpiracion;
    }

    public String getPrograma() { return programa; }
    public void setPrograma(String programa) { this.programa = programa; }

    public String getFicha() { return ficha; }
    public void setFicha(String ficha) { this.ficha = ficha; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(String tipoSangre) { this.tipoSangre = tipoSangre; }

    public String getFoto() { return foto; }
    public void setFoto(String foto) { this.foto = foto; }

    public String getSessionToken() { return sessionToken; }
    public void setSessionToken(String sessionToken) { this.sessionToken = sessionToken; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

    public String getRolNombre() { return rolNombre; }
    public void setRolNombre(String rolNombre) { this.rolNombre = rolNombre; }

    @Override
    public String toString() { return nombre + " (" + cargo + ")"; }
}
