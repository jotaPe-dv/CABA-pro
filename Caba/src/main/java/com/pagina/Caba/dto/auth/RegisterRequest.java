package com.pagina.Caba.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para registro de nuevo árbitro
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-10
 */
public class RegisterRequest {
    
    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;
    
    private String telefono;
    private String direccion;
    
    @NotBlank(message = "El número de licencia es obligatorio")
    private String numeroLicencia;
    
    private String especialidad;
    private String escalafon;
    
    public RegisterRequest() {}
    
    public RegisterRequest(String email, String password, String nombre, String apellido,
                         String telefono, String direccion, String numeroLicencia,
                         String especialidad, String escalafon) {
        this.email = email;
        this.password = password;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroLicencia = numeroLicencia;
        this.especialidad = especialidad;
        this.escalafon = escalafon;
    }
    
    // Getters y Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public String getTelefono() {
        return telefono;
    }
    
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    public String getDireccion() {
        return direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getNumeroLicencia() {
        return numeroLicencia;
    }
    
    public void setNumeroLicencia(String numeroLicencia) {
        this.numeroLicencia = numeroLicencia;
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
}
