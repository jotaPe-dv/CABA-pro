package com.pagina.Caba.repository;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    
    List<Torneo> findByNombreContainingIgnoreCase(String nombre);
    List<Torneo> findByActivoTrue();
    List<Torneo> findByActivoFalse();
    List<Torneo> findByAdministrador(Administrador administrador);
    
    @Query("SELECT t FROM Torneo t WHERE t.fechaInicio <= :fecha AND t.fechaFin >= :fecha AND t.activo = true")
    List<Torneo> findTorneosActivosEnFecha(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT t FROM Torneo t WHERE t.fechaInicio > :fecha AND t.activo = true ORDER BY t.fechaInicio")
    List<Torneo> findTorneosFuturos(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT t FROM Torneo t WHERE t.fechaFin < :fecha ORDER BY t.fechaFin DESC")
    List<Torneo> findTorneosFinalizados(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(t) FROM Torneo t WHERE t.activo = true")
    long countTorneosActivos();
    
    List<Torneo> findByFechaInicioBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
