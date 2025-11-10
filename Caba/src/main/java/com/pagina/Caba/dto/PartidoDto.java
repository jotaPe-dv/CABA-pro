package com.pagina.Caba.dto;

import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Partido
 */
public class PartidoDto {
    
    private Long id;
    private String equipoLocal;
    private String equipoVisitante;
    private String tipoPartido;
    private LocalDateTime fechaPartido;
    private String ubicacion;
    private Boolean completado;
    
    // Constructor vacío
    public PartidoDto() {}
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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
    
    public String getTipoPartido() {
        return tipoPartido;
    }
    
    public void setTipoPartido(String tipoPartido) {
        this.tipoPartido = tipoPartido;
    }
    
    public LocalDateTime getFechaPartido() {
        return fechaPartido;
    }
    
    public void setFechaPartido(LocalDateTime fechaPartido) {
        this.fechaPartido = fechaPartido;
    }
    
    public String getUbicacion() {
        return ubicacion;
    }
    
    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
    public Boolean getCompletado() {
        return completado;
    }
    
    public void setCompletado(Boolean completado) {
        this.completado = completado;
    }
}
