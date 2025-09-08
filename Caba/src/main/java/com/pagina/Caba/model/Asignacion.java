package com.pagina.Caba.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asignacion")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol_especifico", nullable = false, length = 100)
    private String rolEspecifico;

    @Column(name = "pago_calculado", nullable = false)
    private Float pagoCalculado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsignacion estado;

    // Relaciones con Arbitro y Partido
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partido_id")
    private Partido partido;

    // ============================
    // Constructores
    // ============================
    public Asignacion() {
    }

    // Constructor para pruebas sin relaciones
    public Asignacion(String rolEspecifico, Float pagoCalculado, EstadoAsignacion estado) {
        this.rolEspecifico = rolEspecifico;
        this.pagoCalculado = pagoCalculado;
        this.estado = estado;
    }

    // Constructor completo
    public Asignacion(String rolEspecifico, Float pagoCalculado, EstadoAsignacion estado, Arbitro arbitro, Partido partido) {
        this.rolEspecifico = rolEspecifico;
        this.pagoCalculado = pagoCalculado;
        this.estado = estado;
        this.arbitro = arbitro;
        this.partido = partido;
    }

    // ============================
    // Métodos de negocio
    // ============================
    public void aceptar() {
        this.estado = EstadoAsignacion.ACEPTADA;
    }

    public void rechazar() {
        this.estado = EstadoAsignacion.RECHAZADA;
    }

    // ============================
    // Getters y Setters
    // ============================
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRolEspecifico() {
        return rolEspecifico;
    }

    public void setRolEspecifico(String rolEspecifico) {
        this.rolEspecifico = rolEspecifico;
    }

    public Float getPagoCalculado() {
        return pagoCalculado;
    }

    public void setPagoCalculado(Float pagoCalculado) {
        this.pagoCalculado = pagoCalculado;
    }

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }

    public Arbitro getArbitro() {
        return arbitro;
    }

    public void setArbitro(Arbitro arbitro) {
        this.arbitro = arbitro;
    }

    public Partido getPartido() {
        return partido;
    }

    public void setPartido(Partido partido) {
        this.partido = partido;
    }
}
