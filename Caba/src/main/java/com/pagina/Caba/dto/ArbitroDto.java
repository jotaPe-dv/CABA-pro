package com.pagina.Caba.dto;

import java.math.BigDecimal;

/**
 * DTO para transferencia de datos de Árbitro en la API REST
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
public class ArbitroDto {

    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String telefono;
    private String direccion;
    private String numeroLicencia;
    private String especialidad;
    private String escalafon;
    private BigDecimal tarifaBase;
    private Boolean disponible;
    private Boolean activo;
    private String fotoUrl;
    private Integer totalAsignaciones;
    private Integer asignacionesPendientes;
    private Integer asignacionesAceptadas;

    // Constructor por defecto
    public ArbitroDto() {
    }

    // Constructor completo
    public ArbitroDto(Long id, String email, String nombre, String apellido, String telefono,
                      String direccion, String numeroLicencia, String especialidad, String escalafon,
                      BigDecimal tarifaBase, Boolean disponible, Boolean activo, String fotoUrl,
                      Integer totalAsignaciones, Integer asignacionesPendientes, Integer asignacionesAceptadas) {
        this.id = id;
        this.email = email;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.direccion = direccion;
        this.numeroLicencia = numeroLicencia;
        this.especialidad = especialidad;
        this.escalafon = escalafon;
        this.tarifaBase = tarifaBase;
        this.disponible = disponible;
        this.activo = activo;
        this.fotoUrl = fotoUrl;
        this.totalAsignaciones = totalAsignaciones;
        this.asignacionesPendientes = asignacionesPendientes;
        this.asignacionesAceptadas = asignacionesAceptadas;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getTarifaBase() {
        return tarifaBase;
    }

    public void setTarifaBase(BigDecimal tarifaBase) {
        this.tarifaBase = tarifaBase;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public void setFotoUrl(String fotoUrl) {
        this.fotoUrl = fotoUrl;
    }

    public Integer getTotalAsignaciones() {
        return totalAsignaciones;
    }

    public void setTotalAsignaciones(Integer totalAsignaciones) {
        this.totalAsignaciones = totalAsignaciones;
    }

    public Integer getAsignacionesPendientes() {
        return asignacionesPendientes;
    }

    public void setAsignacionesPendientes(Integer asignacionesPendientes) {
        this.asignacionesPendientes = asignacionesPendientes;
    }

    public Integer getAsignacionesAceptadas() {
        return asignacionesAceptadas;
    }

    public void setAsignacionesAceptadas(Integer asignacionesAceptadas) {
        this.asignacionesAceptadas = asignacionesAceptadas;
    }

    // Métodos auxiliares
    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean isActivo() {
        return Boolean.TRUE.equals(activo);
    }

    public boolean isDisponible() {
        return Boolean.TRUE.equals(disponible);
    }
}
