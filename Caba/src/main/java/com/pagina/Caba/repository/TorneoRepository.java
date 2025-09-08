package com.pagina.Caba.repository;

import com.pagina.Caba.model.Torneo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TorneoRepository extends JpaRepository<Torneo, Long> {
    
    List<Torneo> findByFechaInicioBetween(LocalDate inicio, LocalDate fin);
    
    List<Torneo> findByFechaFinAfter(LocalDate fecha);
    
    Optional<Torneo> findByNombre(String nombre);
}
