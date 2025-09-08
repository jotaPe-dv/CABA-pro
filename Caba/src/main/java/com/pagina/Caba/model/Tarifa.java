package com.pagina.Caba.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "tarifas")
public class Tarifa {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal monto;
    
    @Column(nullable = false, length = 50)
    private String escalafon;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id")
    private Torneo torneo;
    
    // Constructor vacío
    public Tarifa() {}
    
    // Constructor con parámetros
    public Tarifa(BigDecimal monto, String escalafon, Torneo torneo) {
        this.monto = monto;
        this.escalafon = escalafon;
        this.torneo = torneo;
    }
    
    // Getters y Setters
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
    
    public Torneo getTorneo() {
        return torneo;
    }
    
    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }
    
    @Override
    public String toString() {
        return "Tarifa{" +
                "id=" + id +
                ", monto=" + monto +
                ", escalafon='" + escalafon + '\'' +
                ", torneoId=" + (torneo != null ? torneo.getId() : null) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarifa tarifa = (Tarifa) o;
        return Objects.equals(id, tarifa.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
