package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "partidos")
public class Partido {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "El equipo local no puede estar vacio")
    @Size(min = 3, max = 50, message = "El nombre del equipo local debe tener entre 3 y 50 caracteres")
    @Column(name = "equipo_local", nullable = false, length = 50)
    private String equipoLocal;
    
    @NotBlank(message = "El equipo visitante no puede estar vacio")
    @Size(min = 3, max = 50, message = "El nombre del equipo visitante debe tener entre 3 y 50 caracteres")
    @Column(name = "equipo_visitante", nullable = false, length = 50)
    private String equipoVisitante;
    
    @NotNull(message = "La fecha del partido no puede ser nula")
    @Column(name = "fecha_partido", nullable = false)
    private LocalDateTime fechaPartido;
    
    @NotBlank(message = "La ubicacion no puede estar vacia")
    @Size(min = 5, max = 100, message = "La ubicacion debe tener entre 5 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String ubicacion;
    
    @NotBlank(message = "El tipo de partido no puede estar vacío")
    @Size(min = 3, max = 50, message = "El tipo de partido debe tener entre 3 y 50 caracteres")
    @Column(name = "tipo_partido", nullable = false, length = 50)
    private String tipoPartido;
    
    @Column(name = "marcador_local")
    private Integer marcadorLocal;
    
    @Column(name = "marcador_visitante")
    private Integer marcadorVisitante;
    
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "completado", nullable = false)
    private Boolean completado = false;
    
    @Size(max = 500, message = "Las observaciones no pueden superar los 500 caracteres")
    @Column(length = 500)
    private String observaciones;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;
    
    @OneToMany(mappedBy = "partido", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();
    
    public Partido() { this.fechaCreacion = LocalDateTime.now(); }
    
    public Partido(String equipoLocal, String equipoVisitante, LocalDateTime fechaPartido, 
                   String ubicacion, Torneo torneo) {
        this();
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
        this.fechaPartido = fechaPartido;
        this.ubicacion = ubicacion;
        this.torneo = torneo;
    }
    
    public void completarPartido(Integer marcadorLocal, Integer marcadorVisitante) {
        this.marcadorLocal = marcadorLocal;
        this.marcadorVisitante = marcadorVisitante;
        this.completado = true;
    }
    
    public boolean estaCompletado() { return completado; }
    
    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getEquipoLocal() { return equipoLocal; }
    public void setEquipoLocal(String equipoLocal) { this.equipoLocal = equipoLocal; }
    
    public String getEquipoVisitante() { return equipoVisitante; }
    public void setEquipoVisitante(String equipoVisitante) { this.equipoVisitante = equipoVisitante; }
    
    public LocalDateTime getFechaPartido() { return fechaPartido; }
    public void setFechaPartido(LocalDateTime fechaPartido) { this.fechaPartido = fechaPartido; }
    
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    
    public Integer getMarcadorLocal() { return marcadorLocal; }
    public void setMarcadorLocal(Integer marcadorLocal) { this.marcadorLocal = marcadorLocal; }
    
    public Integer getMarcadorVisitante() { return marcadorVisitante; }
    public void setMarcadorVisitante(Integer marcadorVisitante) { this.marcadorVisitante = marcadorVisitante; }
    
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    
    public Boolean getCompletado() { return completado; }
    public void setCompletado(Boolean completado) { this.completado = completado; }
    
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    
    public Torneo getTorneo() { return torneo; }
    public void setTorneo(Torneo torneo) { this.torneo = torneo; }
    
    public Set<Asignacion> getAsignaciones() { return asignaciones; }
    public void setAsignaciones(Set<Asignacion> asignaciones) { this.asignaciones = asignaciones; }

    public String getTipoPartido() { return tipoPartido; }
    public void setTipoPartido(String tipoPartido) { this.tipoPartido = tipoPartido; }
}
