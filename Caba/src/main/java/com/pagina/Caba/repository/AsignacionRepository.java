package com.pagina.Caba.repository;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {
    
    @Query("SELECT a FROM Asignacion a JOIN FETCH a.arbitro ar JOIN FETCH a.partido p WHERE ar.id = :arbitroId AND a.estado = :estado")
    List<Asignacion> findByArbitroIdAndEstado(@Param("arbitroId") Long arbitroId, @Param("estado") EstadoAsignacion estado);

    @Query("SELECT a FROM Asignacion a JOIN FETCH a.arbitro ar JOIN FETCH a.partido p WHERE ar.id = :arbitroId")
    List<Asignacion> findByArbitroId(@Param("arbitroId") Long arbitroId);
}

