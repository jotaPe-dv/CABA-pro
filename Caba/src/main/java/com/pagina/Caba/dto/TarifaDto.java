package com.pagina.Caba.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de Tarifa.
 */
public class TarifaDto {

    private Long id;

    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor que cero")
    private BigDecimal monto;

    @NotNull(message = "El escalafón es obligatorio")
    @Size(min = 2, max = 50, message = "El escalafón debe tener entre 2 y 50 caracteres")
    private String escalafon;

    private Long torneoId;
    private String torneoNombre;

    // ============================
    // Constructores
    // ============================

    public TarifaDto() {
    }

    public TarifaDto(BigDecimal monto, String escalafon) {
        this.monto = monto;
        this.escalafon = escalafon;
    }

    public TarifaDto(Long id, BigDecimal monto, String escalafon, Long torneoId) {
        this.id = id;
        this.monto = monto;
        this.escalafon = escalafon;
        this.torneoId = torneoId;
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

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getEscalafon() {
        return escalafon;
    }

    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }

    public Long getTorneoId() {
        return torneoId;
    }

    public void setTorneoId(Long torneoId) {
        this.torneoId = torneoId;
    }

    public String getTorneoNombre() {
        return torneoNombre;
    }

    public void setTorneoNombre(String torneoNombre) {
        this.torneoNombre = torneoNombre;
    }

    @Override
    public String toString() {
        return "TarifaDto{" +
                "id=" + id +
                ", monto=" + monto +
                ", escalafon='" + escalafon + '\'' +
                ", torneoId=" + torneoId +
                '}';
    }
}
