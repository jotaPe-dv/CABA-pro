package com.pagina.Caba.dto;

import com.pagina.Caba.model.EstadoAsignacion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de Asignación.
 */
public class AsignacionDto {

    private Long id;

    @NotNull(message = "El rol específico es obligatorio")
    private String rolEspecifico;

    @NotNull(message = "El pago calculado es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El pago debe ser mayor que cero")
    private BigDecimal pagoCalculado;

    @NotNull(message = "El estado es obligatorio")
    private EstadoAsignacion estado;

    private Long arbitroId;
    private String arbitroNombre;
    private String arbitroEscalafon;

    private Long partidoId;
    private String equipoLocal;
    private String equipoVisitante;

    private Long liquidacionId;

    // ============================
    // Constructores
    // ============================

    public AsignacionDto() {
    }

    public AsignacionDto(String rolEspecifico, BigDecimal pagoCalculado, EstadoAsignacion estado) {
        this.rolEspecifico = rolEspecifico;
        this.pagoCalculado = pagoCalculado;
        this.estado = estado;
    }

    public AsignacionDto(Long id, String rolEspecifico, BigDecimal pagoCalculado, 
                        EstadoAsignacion estado, Long arbitroId, String arbitroNombre) {
        this.id = id;
        this.rolEspecifico = rolEspecifico;
        this.pagoCalculado = pagoCalculado;
        this.estado = estado;
        this.arbitroId = arbitroId;
        this.arbitroNombre = arbitroNombre;
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

    public BigDecimal getPagoCalculado() {
        return pagoCalculado;
    }

    public void setPagoCalculado(BigDecimal pagoCalculado) {
        this.pagoCalculado = pagoCalculado;
    }

    public EstadoAsignacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsignacion estado) {
        this.estado = estado;
    }

    public Long getArbitroId() {
        return arbitroId;
    }

    public void setArbitroId(Long arbitroId) {
        this.arbitroId = arbitroId;
    }

    public String getArbitroNombre() {
        return arbitroNombre;
    }

    public void setArbitroNombre(String arbitroNombre) {
        this.arbitroNombre = arbitroNombre;
    }

    public String getArbitroEscalafon() {
        return arbitroEscalafon;
    }

    public void setArbitroEscalafon(String arbitroEscalafon) {
        this.arbitroEscalafon = arbitroEscalafon;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getEquipoLocal() {
        return equipoLocal;
    }

    public void setEquipoLocal(String equipoLocal) {
        this.equipoLocal = equipoLocal;
    }

    public String getEquipoVisitante() {
        return equipoVisitante;
    }

    public void setEquipoVisitante(String equipoVisitante) {
        this.equipoVisitante = equipoVisitante;
    }

    public Long getLiquidacionId() {
        return liquidacionId;
    }

    public void setLiquidacionId(Long liquidacionId) {
        this.liquidacionId = liquidacionId;
    }

    @Override
    public String toString() {
        return "AsignacionDto{" +
                "id=" + id +
                ", rolEspecifico='" + rolEspecifico + '\'' +
                ", pagoCalculado=" + pagoCalculado +
                ", estado=" + estado +
                ", arbitroId=" + arbitroId +
                ", partidoId=" + partidoId +
                '}';
    }
}
