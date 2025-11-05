package com.pagina.Caba.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad para almacenar configuraciones globales del sistema.
 * Permite activar/desactivar características sin modificar código.
 */
@Entity
@Table(name = "configuraciones")
public class Configuracion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String clave; // Ej: "VALIDAR_ARBITROS_SIMULACION"
    
    @Column(nullable = false, length = 500)
    private String valor; // "true" o "false"
    
    @Column(length = 255)
    private String descripcion; // Descripción legible
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    @Column(name = "modificado_por", length = 100)
    private String modificadoPor; // Email del admin que modificó

    // Constructor vacío
    public Configuracion() {
        this.fechaModificacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Configuracion(String clave, String valor, String descripcion) {
        this.clave = clave;
        this.valor = valor;
        this.descripcion = descripcion;
        this.fechaModificacion = LocalDateTime.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
        this.fechaModificacion = LocalDateTime.now();
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    // Métodos helper
    public boolean getValorBoolean() {
        return "true".equalsIgnoreCase(this.valor);
    }

    public void setValorBoolean(boolean valor) {
        this.valor = String.valueOf(valor);
        this.fechaModificacion = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Configuracion{" +
                "id=" + id +
                ", clave='" + clave + '\'' +
                ", valor='" + valor + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaModificacion=" + fechaModificacion +
                ", modificadoPor='" + modificadoPor + '\'' +
                '}';
    }
}
