package com.pagina.Caba.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "liquidaciones")
public class Liquidacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDate fecha;
    
    @Column(name = "monto_total", precision = 10, scale = 2, nullable = false)
    private BigDecimal montoTotal;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoLiquidacion estado;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "arbitro_id")
    private Arbitro arbitro;
    
    // Las asignaciones pueden estar relacionadas, pero mantenemos la estructura simple por ahora
    // @OneToMany(mappedBy = "liquidacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Asignacion> asignaciones = new ArrayList<>();
    
    // Constructor vacío
    public Liquidacion() {}
    
    // Constructor con parámetros
    public Liquidacion(LocalDate fecha, BigDecimal montoTotal, EstadoLiquidacion estado, Arbitro arbitro) {
        this.fecha = fecha;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.arbitro = arbitro;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDate getFecha() {
        return fecha;
    }
    
    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }
    
    public BigDecimal getMontoTotal() {
        return montoTotal;
    }
    
    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }
    
    public EstadoLiquidacion getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoLiquidacion estado) {
        this.estado = estado;
    }
    
    public Arbitro getArbitro() {
        return arbitro;
    }
    
    public void setArbitro(Arbitro arbitro) {
        this.arbitro = arbitro;
    }
    
    // Método para calcular monto total
    public void calcularMontoTotal() {
        // Implementación básica, se puede expandir según la lógica de negocio
        if (this.montoTotal == null) {
            this.montoTotal = BigDecimal.ZERO;
        }
    }
    
    @Override
    public String toString() {
        return "Liquidacion{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", montoTotal=" + montoTotal +
                ", estado=" + estado +
                ", arbitroId=" + (arbitro != null ? arbitro.getId() : null) +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Liquidacion that = (Liquidacion) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
