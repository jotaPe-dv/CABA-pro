package com.pagina.Caba.repository;

import com.pagina.Caba.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    
    List<Tarifa> findByTipoPartido(String tipoPartido);
    List<Tarifa> findByActivaTrue();
    List<Tarifa> findByActivaFalse();
    List<Tarifa> findByTipoPartidoContainingIgnoreCase(String tipoPartido);
    List<Tarifa> findByMontoBetween(BigDecimal montoMin, BigDecimal montoMax);
    
    @Query("SELECT t FROM Tarifa t WHERE t.activa = true AND " +
           "t.fechaVigenciaInicio <= :fecha AND " +
           "(t.fechaVigenciaFin IS NULL OR t.fechaVigenciaFin >= :fecha)")
    List<Tarifa> findTarifasVigentesEnFecha(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT t FROM Tarifa t WHERE t.tipoPartido = :tipoPartido AND t.activa = true AND " +
           "t.fechaVigenciaInicio <= :fecha AND " +
           "(t.fechaVigenciaFin IS NULL OR t.fechaVigenciaFin >= :fecha)")
    Optional<Tarifa> findTarifaVigentePorTipo(@Param("tipoPartido") String tipoPartido, 
                                             @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT t FROM Tarifa t WHERE t.tipoPartido = :tipoPartido AND t.escalafon = :escalafon AND t.activa = true AND " +
           "t.fechaVigenciaInicio <= :fecha AND " +
           "(t.fechaVigenciaFin IS NULL OR t.fechaVigenciaFin >= :fecha)")
    Optional<Tarifa> findTarifaVigentePorTipoYEscalafon(@Param("tipoPartido") String tipoPartido, 
                                                        @Param("escalafon") String escalafon,
                                                        @Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(t) FROM Tarifa t WHERE t.activa = true")
    long countTarifasActivas();
    
    @Query("SELECT t FROM Tarifa t WHERE t.activa = true ORDER BY t.tipoPartido")
    List<Tarifa> findAllActivasOrdenadas();
    
    @Query("SELECT DISTINCT t.tipoPartido FROM Tarifa t WHERE t.activa = true ORDER BY t.tipoPartido")
    List<String> findTiposPartidoActivos();
}
