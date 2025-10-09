package com.pagina.Caba.repository;

import com.pagina.Caba.model.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {
    boolean existsByEmail(String email);
    
    Optional<Arbitro> findByNumeroLicencia(String numeroLicencia);
    boolean existsByNumeroLicencia(String numeroLicencia);
    List<Arbitro> findByDisponibleTrueAndActivoTrue();
    List<Arbitro> findByDisponibleFalse();
    Optional<Arbitro> findByTelefono(String telefono);
    
    
    @Query("SELECT COUNT(a) FROM Arbitro a WHERE a.activo = true")
    long countArbitrosActivos();
    
    @Query("SELECT COUNT(a) FROM Arbitro a WHERE a.disponible = true AND a.activo = true")
    long countArbitrosDisponibles();
}
