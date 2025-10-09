package com.pagina.Caba.repository;

import com.pagina.Caba.model.Especialidad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EspecialidadRepository extends JpaRepository<Especialidad, Long> {
    
    Optional<Especialidad> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Especialidad> findByActivaTrue();
    List<Especialidad> findByActivaFalse();
    List<Especialidad> findByNombreContainingIgnoreCase(String nombre);
    
    @Query("SELECT e FROM Especialidad e WHERE e.activa = true ORDER BY e.nombre")
    List<Especialidad> findAllActivasOrdenadas();
    
    @Query("SELECT COUNT(e) FROM Especialidad e WHERE e.activa = true")
    long countEspecialidadesActivas();
    
    @Query("SELECT e FROM Especialidad e JOIN e.arbitros a WHERE a.id = :arbitroId AND e.activa = true")
    List<Especialidad> findEspecialidadesByArbitroId(Long arbitroId);
}
