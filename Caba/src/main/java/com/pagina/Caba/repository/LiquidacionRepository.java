package com.pagina.Caba.repository;

import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoLiquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {
    
    List<Liquidacion> findByAsignacion(Asignacion asignacion);
    List<Liquidacion> findByEstado(EstadoLiquidacion estado);
    List<Liquidacion> findByMetodoPago(String metodoPago);
    List<Liquidacion> findByMontoBetween(BigDecimal montoMin, BigDecimal montoMax);
    
    @Query("SELECT l FROM Liquidacion l WHERE l.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Liquidacion> findLiquidacionesEnRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                    @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT l FROM Liquidacion l WHERE l.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    List<Liquidacion> findLiquidacionesPagadasEnRango(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                     @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT l FROM Liquidacion l WHERE l.estado = :estado AND " +
           "l.fechaCreacion < :fechaLimite")
    List<Liquidacion> findLiquidacionesAntiguas(@Param("estado") EstadoLiquidacion estado, 
                                               @Param("fechaLimite") LocalDateTime fechaLimite);
    
    @Query("SELECT COUNT(l) FROM Liquidacion l WHERE l.estado = :estado")
    long countByEstado(@Param("estado") EstadoLiquidacion estado);
    
    @Query("SELECT SUM(l.monto) FROM Liquidacion l WHERE l.estado = :estado")
    BigDecimal sumMontoByEstado(@Param("estado") EstadoLiquidacion estado);
    
    @Query("SELECT l FROM Liquidacion l JOIN l.asignacion a WHERE a.arbitro.id = :arbitroId")
    List<Liquidacion> findLiquidacionesByArbitroId(@Param("arbitroId") Long arbitroId);
}
