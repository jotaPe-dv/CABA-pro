package com.pagina.Caba.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "administradores")
@DiscriminatorValue("ADMINISTRADOR")
public class Administrador extends Usuario {
    
    @NotBlank(message = "El cargo no puede estar vacio")
    @Size(min = 3, max = 50, message = "El cargo debe tener entre 3 y 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cargo;
    
    @Size(max = 20, message = "El telefono no puede superar los 20 caracteres")
    @Column(length = 20)
    private String telefono;
    
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;
    
    @Column(name = "permisos_especiales", nullable = false)
    private Boolean permisosEspeciales = false;
    
    @OneToMany(mappedBy = "administrador", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Torneo> torneosCreados = new HashSet<>();
    
    public Administrador() { super(); }
    
    public Administrador(String nombre, String apellido, String email, String password, String cargo) {
        super(nombre, apellido, email, password);
        this.cargo = cargo;
    }
    
    public void registrarAcceso() { this.ultimoAcceso = LocalDateTime.now(); }
    public void otorgarPermisosEspeciales() { this.permisosEspeciales = true; }
    public void revocarPermisosEspeciales() { this.permisosEspeciales = false; }
    
    public boolean puedeGestionarTorneos() { return this.getActivo() && this.permisosEspeciales; }
    public boolean puedeGestionarArbitros() { return this.getActivo(); }
    
    // Getters y Setters
    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }
    
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    
    public LocalDateTime getUltimoAcceso() { return ultimoAcceso; }
    public void setUltimoAcceso(LocalDateTime ultimoAcceso) { this.ultimoAcceso = ultimoAcceso; }
    
    public Boolean getPermisosEspeciales() { return permisosEspeciales; }
    public void setPermisosEspeciales(Boolean permisosEspeciales) { this.permisosEspeciales = permisosEspeciales; }
    
    public Set<Torneo> getTorneosCreados() { return torneosCreados; }
    public void setTorneosCreados(Set<Torneo> torneosCreados) { this.torneosCreados = torneosCreados; }
}
