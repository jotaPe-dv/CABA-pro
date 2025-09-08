package com.pagina.Caba.repository;

import com.pagina.Caba.model.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {
    
    List<Tarifa> findByTorneoId(Long torneoId);
    
    List<Tarifa> findByEscalafon(String escalafon);
    
    Optional<Tarifa> findByTorneoIdAndEscalafon(Long torneoId, String escalafon);
}
