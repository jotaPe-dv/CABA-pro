package com.pagina.Caba.service;

import com.pagina.Caba.model.Configuracion;
import com.pagina.Caba.repository.ConfiguracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestionar configuraciones globales del sistema.
 * Permite activar/desactivar características en tiempo de ejecución.
 */
@Service
@Transactional
public class ConfiguracionService {
    
    // Claves de configuración predefinidas
    public static final String VALIDAR_ARBITROS_SIMULACION = "VALIDAR_ARBITROS_SIMULACION";
    
    @Autowired
    private ConfiguracionRepository configuracionRepository;
    
    /**
     * Obtiene el valor booleano de una configuración.
     * Si no existe, retorna el valor por defecto.
     */
    public boolean getConfigBoolean(String clave, boolean defaultValue) {
        return configuracionRepository.findByClave(clave)
                .map(Configuracion::getValorBoolean)
                .orElse(defaultValue);
    }
    
    /**
     * Obtiene el valor string de una configuración.
     * Si no existe, retorna el valor por defecto.
     */
    public String getConfigString(String clave, String defaultValue) {
        return configuracionRepository.findByClave(clave)
                .map(Configuracion::getValor)
                .orElse(defaultValue);
    }
    
    /**
     * Establece o actualiza una configuración booleana
     */
    public void setConfigBoolean(String clave, boolean valor, String descripcion, String modificadoPor) {
        Configuracion config = configuracionRepository.findByClave(clave)
                .orElseGet(() -> {
                    Configuracion nueva = new Configuracion();
                    nueva.setClave(clave);
                    nueva.setDescripcion(descripcion);
                    return nueva;
                });
        
        config.setValorBoolean(valor);
        config.setModificadoPor(modificadoPor);
        configuracionRepository.save(config);
    }
    
    /**
     * Establece o actualiza una configuración string
     */
    public void setConfigString(String clave, String valor, String descripcion, String modificadoPor) {
        Configuracion config = configuracionRepository.findByClave(clave)
                .orElseGet(() -> {
                    Configuracion nueva = new Configuracion();
                    nueva.setClave(clave);
                    nueva.setDescripcion(descripcion);
                    return nueva;
                });
        
        config.setValor(valor);
        config.setModificadoPor(modificadoPor);
        configuracionRepository.save(config);
    }
    
    /**
     * Toggle (invertir) una configuración booleana
     */
    public boolean toggleConfig(String clave, String descripcion, String modificadoPor) {
        boolean valorActual = getConfigBoolean(clave, true);
        boolean nuevoValor = !valorActual;
        setConfigBoolean(clave, nuevoValor, descripcion, modificadoPor);
        return nuevoValor;
    }
    
    /**
     * Lista todas las configuraciones
     */
    public List<Configuracion> listarTodas() {
        return configuracionRepository.findAll();
    }
    
    /**
     * Obtiene una configuración completa por clave
     */
    public Configuracion obtenerPorClave(String clave) {
        return configuracionRepository.findByClave(clave).orElse(null);
    }
    
    /**
     * Verifica si está habilitada la validación de árbitros para simulación
     */
    public boolean debeValidarArbitrosParaSimulacion() {
        // Por defecto TRUE (validar siempre)
        return getConfigBoolean(VALIDAR_ARBITROS_SIMULACION, true);
    }
    
    /**
     * Inicializa configuraciones por defecto si no existen
     */
    public void inicializarConfiguraciones() {
        if (!configuracionRepository.existsByClave(VALIDAR_ARBITROS_SIMULACION)) {
            setConfigBoolean(
                VALIDAR_ARBITROS_SIMULACION, 
                true, 
                "Validar que todos los árbitros hayan aceptado antes de simular un partido",
                "SYSTEM"
            );
        }
    }
}
