package com.pagina.Caba.dto;

import com.pagina.Caba.model.EstadoAsignacion;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO para transferencia de datos de Asignación
 */
public class AsignacionDto {

    private Long id;
    private Long arbitroId;
    private String arbitroNombre;
    private String arbitroEmail;
    private Long partidoId;
    private String partidoDescripcion;
    private String torneoNombre;
    private LocalDateTime fechaPartido;
    private String lugarPartido;
    private String rolEspecifico;
    private String estado; // Cambio a String para API REST
    private BigDecimal montoCalculado;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaRespuesta;
    private String comentarios;
    
    // Objetos anidados para API REST
    private PartidoDto partido;
    private ArbitroDto arbitro;

    // Constructor vacío
    public AsignacionDto() {}

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getArbitroId() {
        return arbitroId;
    }

    public void setArbitroId(Long arbitroId) {
        this.arbitroId = arbitroId;
    }

    public String getArbitroNombre() {
        return arbitroNombre;
    }

    public void setArbitroNombre(String arbitroNombre) {
        this.arbitroNombre = arbitroNombre;
    }

    public String getArbitroEmail() {
        return arbitroEmail;
    }

    public void setArbitroEmail(String arbitroEmail) {
        this.arbitroEmail = arbitroEmail;
    }

    public Long getPartidoId() {
        return partidoId;
    }

    public void setPartidoId(Long partidoId) {
        this.partidoId = partidoId;
    }

    public String getPartidoDescripcion() {
        return partidoDescripcion;
    }

    public void setPartidoDescripcion(String partidoDescripcion) {
        this.partidoDescripcion = partidoDescripcion;
    }

    public String getTorneoNombre() {
        return torneoNombre;
    }

    public void setTorneoNombre(String torneoNombre) {
        this.torneoNombre = torneoNombre;
    }

    public LocalDateTime getFechaPartido() {
        return fechaPartido;
    }

    public void setFechaPartido(LocalDateTime fechaPartido) {
        this.fechaPartido = fechaPartido;
    }

    public String getLugarPartido() {
        return lugarPartido;
    }

    public void setLugarPartido(String lugarPartido) {
        this.lugarPartido = lugarPartido;
    }

    public String getRolEspecifico() {
        return rolEspecifico;
    }

    public void setRolEspecifico(String rolEspecifico) {
        this.rolEspecifico = rolEspecifico;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getMontoCalculado() {
        return montoCalculado;
    }

    public void setMontoCalculado(BigDecimal montoCalculado) {
        this.montoCalculado = montoCalculado;
    }

    public LocalDateTime getFechaAsignacion() {
        return fechaAsignacion;
    }

    public void setFechaAsignacion(LocalDateTime fechaAsignacion) {
        this.fechaAsignacion = fechaAsignacion;
    }

    public LocalDateTime getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(LocalDateTime fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
    
    public PartidoDto getPartido() {
        return partido;
    }

    public void setPartido(PartidoDto partido) {
        this.partido = partido;
    }

    public ArbitroDto getArbitro() {
        return arbitro;
    }

    public void setArbitro(ArbitroDto arbitro) {
        this.arbitro = arbitro;
    }
}
