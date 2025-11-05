package com.pagina.Caba.repository;

import com.pagina.Caba.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {
    
    /**
     * Busca una configuración por su clave única
     * @param clave La clave de la configuración (ej: "VALIDAR_ARBITROS_SIMULACION")
     * @return Optional con la configuración si existe
     */
    Optional<Configuracion> findByClave(String clave);
    
    /**
     * Verifica si existe una configuración con la clave especificada
     * @param clave La clave a verificar
     * @return true si existe, false si no
     */
    boolean existsByClave(String clave);
}
