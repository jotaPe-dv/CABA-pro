package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "tarifas",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"tipo_partido", "escalafon"})}
)
public class Tarifa {
    @NotBlank(message = "El escalafón no puede estar vacío")
    @Column(name = "escalafon", nullable = false, length = 30)
    private String escalafon;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El tipo de partido no puede estar vacio")
    @Size(min = 3, max = 50, message = "El tipo de partido debe tener entre 3 y 50 caracteres")
    @Column(name = "tipo_partido", nullable = false, length = 50)
    private String tipoPartido;
    
    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.0", inclusive = false, message = "El monto debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;
    
    @Size(max = 200, message = "La descripcion no puede superar los 200 caracteres")
    @Column(length = 200)
    private String descripcion;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_vigencia_inicio", nullable = false)
    private LocalDateTime fechaVigenciaInicio;
    
    @Column(name = "fecha_vigencia_fin")
    private LocalDateTime fechaVigenciaFin;
    
    @Column(name = "activa", nullable = false)
    private Boolean activa = true;
    
    @OneToMany(mappedBy = "tarifa", fetch = FetchType.LAZY)
    private Set<Asignacion> asignaciones = new HashSet<>();
    
    public Tarifa() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaVigenciaInicio = LocalDateTime.now();
    }
    
    public Tarifa(String tipoPartido, String escalafon, BigDecimal monto, String descripcion) {
        this();
        this.tipoPartido = tipoPartido;
        this.escalafon = escalafon;
        this.monto = monto;
        this.descripcion = descripcion;
    }
    public String getEscalafon() { return escalafon; }
    public void setEscalafon(String escalafon) { this.escalafon = escalafon; }
    
    public void activar() { this.activa = true; }
    public void desactivar() { this.activa = false; }
    
    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return activa && 
               ahora.isAfter(fechaVigenciaInicio) && 
               (fechaVigenciaFin == null || ahora.isBefore(fechaVigenciaFin));
    }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTipoPartido() { return tipoPartido; }
    public void setTipoPartido(String tipoPartido) { this.tipoPartido = tipoPartido; }
    
    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public LocalDateTime getFechaVigenciaInicio() { return fechaVigenciaInicio; }
    public void setFechaVigenciaInicio(LocalDateTime fechaVigenciaInicio) { this.fechaVigenciaInicio = fechaVigenciaInicio; }
    
    public LocalDateTime getFechaVigenciaFin() { return fechaVigenciaFin; }
    public void setFechaVigenciaFin(LocalDateTime fechaVigenciaFin) { this.fechaVigenciaFin = fechaVigenciaFin; }
    
    public Boolean getActiva() { return activa; }
    public void setActiva(Boolean activa) { this.activa = activa; }
    
    public Set<Asignacion> getAsignaciones() { return asignaciones; }
    public void setAsignaciones(Set<Asignacion> asignaciones) { this.asignaciones = asignaciones; }
}
