package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "arbitros")
@DiscriminatorValue("ARBITRO")
public class Arbitro extends Usuario {
    
    @NotBlank(message = "El numero de licencia no puede estar vacio")
    @Size(min = 5, max = 20, message = "El numero de licencia debe tener entre 5 y 20 caracteres")
    @Column(name = "numero_licencia", nullable = false, unique = true, length = 20)
    private String numeroLicencia;
    
    @Pattern(regexp = "^[0-9]{10}$", message = "El telefono debe tener 10 digitos")
    @Column(length = 15)
    private String telefono;
    
    @Size(max = 100, message = "La direccion no puede superar los 100 caracteres")
    @Column(length = 100)
    private String direccion;
    
    @Column(name = "tarifa_base", precision = 10, scale = 2)
    private BigDecimal tarifaBase;
    
    @Column(name = "disponible", nullable = false)
    private Boolean disponible = true;
    
    @NotBlank(message = "La especialidad no puede estar vacía")
    @Column(name = "especialidad", nullable = false, length = 30)
    private String especialidad; // Principal, Auxiliar, Mesa

    @NotBlank(message = "El escalafón no puede estar vacío")
    @Column(name = "escalafon", nullable = false, length = 30)
    private String escalafon;

    @Size(max = 255, message = "La URL de la foto no puede superar los 255 caracteres")
    @Column(name = "foto_url", length = 255)
    private String fotoUrl;
    
    @OneToMany(mappedBy = "arbitro", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Asignacion> asignaciones = new HashSet<>();
    
    public Arbitro() { super(); }
    
    public Arbitro(String nombre, String apellido, String email, String password, 
                   String numeroLicencia, String telefono, String especialidad, String escalafon, String fotoUrl) {
        super(nombre, apellido, email, password);
        this.numeroLicencia = numeroLicencia;
        this.telefono = telefono;
        this.especialidad = especialidad;
        this.escalafon = escalafon;
        this.fotoUrl = fotoUrl;
    }
    
    public void marcarDisponible() { this.disponible = true; }
    public void marcarNoDisponible() { this.disponible = false; }
    
    // Getters y Setters
    public String getNumeroLicencia() { return numeroLicencia; }
    public void setNumeroLicencia(String numeroLicencia) { this.numeroLicencia = numeroLicencia; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    
    public BigDecimal getTarifaBase() { return tarifaBase; }
    public void setTarifaBase(BigDecimal tarifaBase) { this.tarifaBase = tarifaBase; }
    
    public Boolean getDisponible() { return disponible; }
    public void setDisponible(Boolean disponible) { this.disponible = disponible; }
    
    public String getEspecialidad() { return especialidad; }
    public void setEspecialidad(String especialidad) { this.especialidad = especialidad; }

    public String getEscalafon() { return escalafon; }
    public void setEscalafon(String escalafon) { this.escalafon = escalafon; }

    public String getFotoUrl() { return fotoUrl; }
    public void setFotoUrl(String fotoUrl) { this.fotoUrl = fotoUrl; }
    
    public Set<Asignacion> getAsignaciones() { return asignaciones; }
    public void setAsignaciones(Set<Asignacion> asignaciones) { this.asignaciones = asignaciones; }
}
