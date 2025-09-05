package com.pagina.Caba;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "arbitros")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Arbitro extends Usuario {
    
    @Column(name = "foto_url")
    private String fotoUrl;
    
    @Column(nullable = false)
    private String especialidad; // "Principal", "Asistente", "Mesa de Control"
    
    private String escalafon;
    
    // @OneToMany(mappedBy = "arbitro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Asignacion> asignaciones;
    
    // @OneToMany(mappedBy = "arbitro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    // private List<Tarifa> tarifas;
    
    // Constructor por defecto
    public Arbitro() {
        super();
    }
    
    // Constructor con parámetros
    public Arbitro(String nombre, String email, String password, String telefono, 
                   String especialidad, String escalafon) {
        super(nombre, email, password, telefono);
        this.especialidad = especialidad;
        this.escalafon = escalafon;
    }
    
    // Getters y Setters
    public String getFotoUrl() {
        return fotoUrl;
    }
    
    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }
    
    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
    
    public String getEscalafon() {
        return escalafon;
    }
    
    public void setEscalafon(String escalafon) {
        this.escalafon = escalafon;
    }
    
    // public List<Asignacion> getAsignaciones() {
    //     return asignaciones;
    // }
    
    // public void setAsignaciones(List<Asignacion> asignaciones) {
    //     this.asignaciones = asignaciones;
    // }
    
    // public List<Tarifa> getTarifas() {
    //     return tarifas;
    // }
    
    // public void setTarifas(List<Tarifa> tarifas) {
    //     this.tarifas = tarifas;
    // }
    
    @Override
    public String toString() {
        return "Arbitro{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", especialidad='" + especialidad + '\'' +
                ", escalafon='" + escalafon + '\'' +
                '}';
    }
}