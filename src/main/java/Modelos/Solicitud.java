package Modelos;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Solicitud {
    private int id_solicitud;
    private int id_usuario;
    private String motivo;
    private String estado;
    private Date fecha_creacion;
    
    @JsonProperty("Usuario")
    private Usuario usuario;

    // Getters y Setters
    public int getId_solicitud() { return id_solicitud; }
    public void setId_solicitud(int id_solicitud) { this.id_solicitud = id_solicitud; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Date getFecha_creacion() { return fecha_creacion; }
    public void setFecha_creacion(Date fecha_creacion) { this.fecha_creacion = fecha_creacion; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}