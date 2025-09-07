package com.pagina.Caba.repository;

import com.pagina.Caba.model.Arbitro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArbitroRepository extends JpaRepository<Arbitro, Long> {
    
}