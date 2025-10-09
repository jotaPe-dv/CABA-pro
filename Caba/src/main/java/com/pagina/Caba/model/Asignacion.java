package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "asignaciones")
public class Asignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    

    @NotBlank(message = "El rol específico no puede estar vacío")
    @Column(name = "rol_especifico", nullable = false, length = 30)
    private String rolEspecifico;

    @NotNull(message = "El estado no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAsignacion estado = EstadoAsignacion.PENDIENTE;
    
    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
    
    @Column(name = "fecha_respuesta")
    private LocalDateTime fechaRespuesta;
    
    @Column(name = "monto_calculado", precision = 10, scale = 2)
    private BigDecimal montoCalculado;
    
    @Size(max = 500, message = "Los comentarios no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String comentarios;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id", nullable = false)
    private Partido partido;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id", nullable = false)
    private Arbitro arbitro;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarifa_id", nullable = false)
    private Tarifa tarifa;
    
    @OneToOne(mappedBy = "asignacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Liquidacion liquidacion;
    public String getRolEspecifico() { return rolEspecifico; }
    public void setRolEspecifico(String rolEspecifico) { this.rolEspecifico = rolEspecifico; }

    public Liquidacion getLiquidacion() { return liquidacion; }
    public void setLiquidacion(Liquidacion liquidacion) { this.liquidacion = liquidacion; }
    
    public Asignacion() { this.fechaAsignacion = LocalDateTime.now(); }
    
    public Asignacion(Partido partido, Arbitro arbitro, Tarifa tarifa) {
        this();
        this.partido = partido;
        this.arbitro = arbitro;
        this.tarifa = tarifa;
        this.montoCalculado = tarifa.getMonto();
    }
    
    public void aceptar() {
        if (this.estado != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden aceptar asignaciones pendientes");
        }
        this.estado = EstadoAsignacion.ACEPTADA;
        this.fechaRespuesta = LocalDateTime.now();
    }
    
    public void rechazar(String motivoRechazo) {
        if (this.estado != EstadoAsignacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden rechazar asignaciones pendientes");
        }
        this.estado = EstadoAsignacion.RECHAZADA;
        this.fechaRespuesta = LocalDateTime.now();
        this.comentarios = motivoRechazo;
    }
    
    public void completar() {
        if (this.estado != EstadoAsignacion.ACEPTADA) {
            throw new IllegalStateException("Solo se pueden completar asignaciones aceptadas");
        }
        this.estado = EstadoAsignacion.COMPLETADA;
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public EstadoAsignacion getEstado() { return estado; }
    public void setEstado(EstadoAsignacion estado) { this.estado = estado; }
    
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime fechaAsignacion) { this.fechaAsignacion = fechaAsignacion; }
    
    public LocalDateTime getFechaRespuesta() { return fechaRespuesta; }
    public void setFechaRespuesta(LocalDateTime fechaRespuesta) { this.fechaRespuesta = fechaRespuesta; }
    
    public BigDecimal getMontoCalculado() { return montoCalculado; }
    public void setMontoCalculado(BigDecimal montoCalculado) { this.montoCalculado = montoCalculado; }
    
    public String getComentarios() { return comentarios; }
    public void setComentarios(String comentarios) { this.comentarios = comentarios; }
    
    public Partido getPartido() { return partido; }
    public void setPartido(Partido partido) { this.partido = partido; }
    
    public Arbitro getArbitro() { return arbitro; }
    public void setArbitro(Arbitro arbitro) { this.arbitro = arbitro; }
    
    public Tarifa getTarifa() { return tarifa; }
    public void setTarifa(Tarifa tarifa) { this.tarifa = tarifa; }
    
}
