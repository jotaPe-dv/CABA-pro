package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensajes")
public class Mensaje {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El remitente no puede ser nulo")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "remitente_id", nullable = false)
    private Usuario remitente;
    
    @NotNull(message = "El destinatario no puede ser nulo")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private Usuario destinatario;
    
    @NotBlank(message = "El contenido no puede estar vacío")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenido;
    
    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;
    
    @Column(name = "leido", nullable = false)
    private Boolean leido = false;
    
    @Column(name = "tipo_remitente", length = 20)
    private String tipoRemitente; // "ADMIN" o "ARBITRO"
    
    @Column(name = "tipo_destinatario", length = 20)
    private String tipoDestinatario; // "ADMIN" o "ARBITRO"
    
    public Mensaje() {
        this.fechaEnvio = LocalDateTime.now();
    }
    
    public Mensaje(Usuario remitente, Usuario destinatario, String contenido) {
        this();
        this.remitente = remitente;
        this.destinatario = destinatario;
        this.contenido = contenido;
        this.tipoRemitente = getTipoUsuario(remitente);
        this.tipoDestinatario = getTipoUsuario(destinatario);
    }
    
    private String getTipoUsuario(Usuario usuario) {
        if (usuario instanceof Administrador) {
            return "ADMIN";
        } else if (usuario instanceof Arbitro) {
            return "ARBITRO";
        }
        return "DESCONOCIDO";
    }
    
    public void marcarComoLeido() {
        this.leido = true;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Usuario getRemitente() { return remitente; }
    public void setRemitente(Usuario remitente) { 
        this.remitente = remitente;
        this.tipoRemitente = getTipoUsuario(remitente);
    }
    
    public Usuario getDestinatario() { return destinatario; }
    public void setDestinatario(Usuario destinatario) { 
        this.destinatario = destinatario;
        this.tipoDestinatario = getTipoUsuario(destinatario);
    }
    
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
    
    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }
    
    public Boolean getLeido() { return leido; }
    public void setLeido(Boolean leido) { this.leido = leido; }
    
    public String getTipoRemitente() { return tipoRemitente; }
    public void setTipoRemitente(String tipoRemitente) { this.tipoRemitente = tipoRemitente; }
    
    public String getTipoDestinatario() { return tipoDestinatario; }
    public void setTipoDestinatario(String tipoDestinatario) { this.tipoDestinatario = tipoDestinatario; }
}