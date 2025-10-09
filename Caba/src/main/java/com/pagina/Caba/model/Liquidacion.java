package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "liquidaciones")
public class Liquidacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @NotNull(message = "El estado no puede ser nulo")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoLiquidacion estado = EstadoLiquidacion.PENDIENTE;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_pago")
    private LocalDateTime fechaPago;
    
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String observaciones;
    
    @Size(max = 100, message = "El metodo de pago no puede superar los 100 caracteres")
    @Column(name = "metodo_pago", length = 100)
    private String metodoPago;
    
    @Size(max = 100, message = "La referencia de pago no puede superar los 100 caracteres")
    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asignacion_id", nullable = false, unique = true)
    private Asignacion asignacion;
    
    public Liquidacion() { this.fechaCreacion = LocalDateTime.now(); }
    
    public Liquidacion(Asignacion asignacion, BigDecimal monto) {
        this();
        this.asignacion = asignacion;
        this.monto = monto;
    }
    
    public void marcarComoPagada(String metodoPago, String referenciaPago) {
        if (this.estado != EstadoLiquidacion.PENDIENTE) {
            throw new IllegalStateException("Solo se pueden pagar liquidaciones pendientes");
        }
        this.estado = EstadoLiquidacion.PAGADA;
        this.fechaPago = LocalDateTime.now();
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
    }
    
    public void cancelar(String motivo) {
        if (this.estado == EstadoLiquidacion.PAGADA) {
            throw new IllegalStateException("No se puede cancelar una liquidacion ya pagada");
        }
        this.estado = EstadoLiquidacion.CANCELADA;
        this.observaciones = motivo;
    }
    
    public boolean estaPagada() { return this.estado == EstadoLiquidacion.PAGADA; }
    public boolean estaPendiente() { return this.estado == EstadoLiquidacion.PENDIENTE; }
    public boolean estaCancelada() { return this.estado == EstadoLiquidacion.CANCELADA; }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public EstadoLiquidacion getEstado() { return estado; }
    public void setEstado(EstadoLiquidacion estado) { this.estado = estado; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    
    public String getReferenciaPago() { return referenciaPago; }
    public void setReferenciaPago(String referenciaPago) { this.referenciaPago = referenciaPago; }
    
    public Asignacion getAsignacion() { return asignacion; }
    public void setAsignacion(Asignacion asignacion) { this.asignacion = asignacion; }
}
