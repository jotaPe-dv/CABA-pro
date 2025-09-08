package com.pagina.Caba.repository;

import com.pagina.Caba.model.EstadoLiquidacion;
import com.pagina.Caba.model.Liquidacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LiquidacionRepository extends JpaRepository<Liquidacion, Long> {
    
    List<Liquidacion> findByArbitroId(Long arbitroId);
    
    List<Liquidacion> findByEstado(EstadoLiquidacion estado);
    
    List<Liquidacion> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
