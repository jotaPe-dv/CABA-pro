package com.pagina.Caba.dto;

import java.time.LocalDateTime;

public class MensajeDTO {
    
    private Long id;
    private Long remitenteId;
    private String remitenteNombre;
    private String tipoRemitente;
    private Long destinatarioId;
    private String destinatarioNombre;
    private String tipoDestinatario;
    private String contenido;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    
    public MensajeDTO() {}
    
    public MensajeDTO(Long id, Long remitenteId, String remitenteNombre, String tipoRemitente,
                     Long destinatarioId, String destinatarioNombre, String tipoDestinatario,
                     String contenido, LocalDateTime fechaEnvio, Boolean leido) {
        this.id = id;
        this.remitenteId = remitenteId;
        this.remitenteNombre = remitenteNombre;
        this.tipoRemitente = tipoRemitente;
        this.destinatarioId = destinatarioId;
        this.destinatarioNombre = destinatarioNombre;
        this.tipoDestinatario = tipoDestinatario;
        this.contenido = contenido;
        this.fechaEnvio = fechaEnvio;
        this.leido = leido;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getRemitenteId() { return remitenteId; }
    public void setRemitenteId(Long remitenteId) { this.remitenteId = remitenteId; }
    
    public String getRemitenteNombre() { return remitenteNombre; }
    public void setRemitenteNombre(String remitenteNombre) { this.remitenteNombre = remitenteNombre; }
    
    public String getTipoRemitente() { return tipoRemitente; }
    public void setTipoRemitente(String tipoRemitente) { this.tipoRemitente = tipoRemitente; }
    
    public Long getDestinatarioId() { return destinatarioId; }
    public void setDestinatarioId(Long destinatarioId) { this.destinatarioId = destinatarioId; }
    
    public String getDestinatarioNombre() { return destinatarioNombre; }
    public void setDestinatarioNombre(String destinatarioNombre) { this.destinatarioNombre = destinatarioNombre; }
    
    public String getTipoDestinatario() { return tipoDestinatario; }
    public void setTipoDestinatario(String tipoDestinatario) { this.tipoDestinatario = tipoDestinatario; }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    
    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }
}