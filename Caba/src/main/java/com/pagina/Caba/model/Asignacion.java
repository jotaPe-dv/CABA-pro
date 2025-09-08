package com.pagina.Caba.model;

import jakarta.persistence.*;

@Entity
@Table(name = "asignaciones")
public class Asignacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rol_especifico", nullable = false, length = 100)
    private String rolEspecifico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsignacion estado;

    @Column(name = "pago_calculado", nullable = false)
    private float pagoCalculado;

    // Relación con Arbitro (Muchas asignaciones pueden pertenecer a un árbitro)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id", nullable = false)
    private Arbitro arbitro;

    // =========================
    // Constructores
    // =========================
    public Asignacion() {}

    public Asignacion(String rolEspecifico, float pagoCalculado, EstadoAsignacion estado, Arbitro arbitro) {
        this.rolEspecifico = rolEspecifico;
        this.pagoCalculado = pagoCalculado;
        this.estado = estado;
        this.arbitro = arbitro;
    }

    // =========================
    // Métodos de negocio
    // =========================
    public void aceptar() {
        this.estado = EstadoAsignacion.ACEPTADA;
    }

    public void rechazar() {
        this.estado = EstadoAsignacion.RECHAZADA;
    }

    // =========================
    // Getters y Setters
    // =========================
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

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }

    public float getPagoCalculado() {
        return pagoCalculado;
    }

    public void setPagoCalculado(float pagoCalculado) {
        this.pagoCalculado = pagoCalculado;
    }

    public Arbitro getArbitro() {
        return arbitro;
    }

    public void setArbitro(Arbitro arbitro) {
        this.arbitro = arbitro;
    }

    @Override
    public String toString() {
        return "Asignacion{" +
                "id=" + id +
                ", rolEspecifico='" + rolEspecifico + '\'' +
                ", estado=" + estado +
                ", pagoCalculado=" + pagoCalculado +
                ", arbitro=" + (arbitro != null ? arbitro.getNombre() : "null") +
                '}';
    }
}
