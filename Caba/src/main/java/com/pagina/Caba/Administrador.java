package com.pagina.Caba;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
@PrimaryKeyJoinColumn(name = "usuario_id")
public class Administrador extends Usuario {
    
    @Column(name = "nivel_acceso")
    private String nivelAcceso; // "SUPER_ADMIN", "ADMIN", "MODERADOR"
    
    private String departamento;
    
    @Column(name = "activo")
    private Boolean activo = true;
    
    // Constructor por defecto
    public Administrador() {
        super();
    }
    
    // Constructor con parámetros
    public Administrador(String nombre, String email, String password, String telefono,
                        String nivelAcceso, String departamento) {
        super(nombre, email, password, telefono);
        this.nivelAcceso = nivelAcceso;
        this.departamento = departamento;
    }
    
    // Getters y Setters
    public String getNivelAcceso() {
        return nivelAcceso;
    }
    
    public void setNivelAcceso(String nivelAcceso) {
        this.nivelAcceso = nivelAcceso;
    }
    
    public String getDepartamento() {
        return departamento;
    }
    
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    
    public Boolean getActivo() {
        return activo;
    }
    
    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return "Administrador{" +
                "id=" + getId() +
                ", nombre='" + getNombre() + '\'' +
                ", email='" + getEmail() + '\'' +
                ", nivelAcceso='" + nivelAcceso + '\'' +
                ", departamento='" + departamento + '\'' +
                ", activo=" + activo +
                '}';
    }
}