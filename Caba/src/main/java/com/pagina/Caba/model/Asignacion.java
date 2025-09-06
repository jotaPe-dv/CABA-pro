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
}
