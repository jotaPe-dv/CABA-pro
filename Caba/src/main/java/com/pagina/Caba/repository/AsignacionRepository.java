package com.pagina.Caba.repository;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Partido;
import com.pagina.Caba.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    
    List<Asignacion> findByArbitro(Arbitro arbitro);
    List<Asignacion> findByPartido(Partido partido);
    List<Asignacion> findByEstado(EstadoAsignacion estado);
    List<Asignacion> findByArbitroAndEstado(Arbitro arbitro, EstadoAsignacion estado);
    List<Asignacion> findByPartidoAndEstado(Partido partido, EstadoAsignacion estado);
    
    @Query("SELECT a FROM Asignacion a WHERE a.arbitro = :arbitro AND a.estado = :estado")
    List<Asignacion> findAsignacionesByArbitroAndEstado(@Param("arbitro") Arbitro arbitro, 
                                                       @Param("estado") EstadoAsignacion estado);
    
    @Query("SELECT a FROM Asignacion a WHERE a.fechaAsignacion BETWEEN :fechaInicio AND :fechaFin")
    List<Asignacion> findAsignacionesEnRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                  @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT a FROM Asignacion a WHERE a.estado = :estado AND a.partido.fechaPartido < :fecha")
    List<Asignacion> findAsignacionesVencidas(@Param("estado") EstadoAsignacion estado, 
                                             @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.estado = :estado")
    long countByEstado(@Param("estado") EstadoAsignacion estado);
    
    @Query("SELECT COUNT(a) FROM Asignacion a WHERE a.arbitro = :arbitro AND a.estado IN :estados")
    long countAsignacionesActivasByArbitro(@Param("arbitro") Arbitro arbitro, 
                                          @Param("estados") List<EstadoAsignacion> estados);
    
    Optional<Asignacion> findByArbitroAndPartido(Arbitro arbitro, Partido partido);
}
