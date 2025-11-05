package com.pagina.Caba.repository;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PartidoRepository extends JpaRepository<Partido, Long> {
    
    List<Partido> findByTorneo(Torneo torneo);
    
    @Query("SELECT p FROM Partido p WHERE p.torneo.id = :torneoId")
    List<Partido> findByTorneoId(@Param("torneoId") Long torneoId);
    
    List<Partido> findByCompletadoTrue();
    List<Partido> findByCompletadoFalse();
    List<Partido> findByEquipoLocalContainingIgnoreCaseOrEquipoVisitanteContainingIgnoreCase(
        String equipoLocal, String equipoVisitante);
    
    @Query("SELECT p FROM Partido p WHERE p.fechaPartido BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPartido")
    List<Partido> findPartidosEnRangoFechas(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT p FROM Partido p WHERE p.fechaPartido > :fecha AND p.completado = false ORDER BY p.fechaPartido")
    List<Partido> findPartidosPendientes(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT p FROM Partido p WHERE p.fechaPartido < :fecha AND p.completado = false")
    List<Partido> findPartidosVencidos(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT COUNT(p) FROM Partido p WHERE p.completado = false")
    long countPartidosPendientes();
    
    @Query("SELECT COUNT(p) FROM Partido p WHERE p.torneo = :torneo")
    long countPartidosByTorneo(@Param("torneo") Torneo torneo);
    
    List<Partido> findByUbicacionContainingIgnoreCase(String ubicacion);
}
