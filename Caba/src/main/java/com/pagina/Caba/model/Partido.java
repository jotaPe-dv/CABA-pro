package com.pagina.Caba.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partido")
@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
public class Partido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Column(nullable = false, length = 100)
    private String lugar;

    @Column(name = "equipo_local", nullable = false, length = 100)
    private String equipoLocal;

    @Column(name = "equipo_visitante", nullable = false, length = 100)
    private String equipoVisitante;

    // Relación con Torneo (Muchos partidos pertenecen a un torneo)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "torneo_id", nullable = false)
    // private Torneo torneo;

    // Relación con Asignacion (Un partido puede tener varias asignaciones de árbitros)
    // @OneToMany(mappedBy = "partido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Asignacion> asignaciones = new ArrayList<>();

    // ============================
    // Constructores
    // ============================
    public Partido() {
    }

    public Partido(LocalDateTime fechaHora, String lugar, String equipoLocal, String equipoVisitante) {
        this.fechaHora = fechaHora;
        this.lugar = lugar;
        this.equipoLocal = equipoLocal;
        this.equipoVisitante = equipoVisitante;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
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

    /*
    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public List<Asignacion> getAsignaciones() {
        return asignaciones;
    }

    public void addAsignacion(Asignacion asignacion) {
        asignaciones.add(asignacion);
        asignacion.setPartido(this);
    }
    */
}
