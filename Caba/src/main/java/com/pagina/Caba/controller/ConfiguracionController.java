package com.pagina.Caba.controller;

import com.pagina.Caba.model.Configuracion;
import com.pagina.Caba.service.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para gestionar configuraciones del sistema.
 * Solo accesible por administradores.
 */
@Controller
@RequestMapping("/admin/configuraciones")
public class ConfiguracionController {
    
    @Autowired
    private ConfiguracionService configuracionService;
    
    /**
     * Muestra el panel de configuraciones del sistema
     */
    @GetMapping
    public String verConfiguraciones(Model model) {
        // Inicializar configuraciones si no existen
        configuracionService.inicializarConfiguraciones();
        
        List<Configuracion> configuraciones = configuracionService.listarTodas();
        model.addAttribute("configuraciones", configuraciones);
        
        // Configuración específica para simulación de partidos
        boolean validarArbitros = configuracionService.debeValidarArbitrosParaSimulacion();
        model.addAttribute("validarArbitros", validarArbitros);
        
        return "admin/configuraciones";
    }
    
    /**
     * API REST: Toggle de validación de árbitros para simulación
     */
    @PostMapping("/api/toggle-validacion-arbitros")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> toggleValidacionArbitros(
            @RequestParam(required = false, defaultValue = "admin@caba.com") String modificadoPor) {
        
        boolean nuevoValor = configuracionService.toggleConfig(
                ConfiguracionService.VALIDAR_ARBITROS_SIMULACION,
                "Validar que todos los árbitros hayan aceptado antes de simular un partido",
                modificadoPor
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("nuevoValor", nuevoValor);
        response.put("mensaje", nuevoValor ? 
                "✅ Validación activada: Se requiere que todos los árbitros acepten antes de simular" :
                "⚠️ Validación desactivada: Se puede simular sin aceptación de árbitros");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API REST: Obtener estado actual de validación de árbitros
     */
    @GetMapping("/api/estado-validacion-arbitros")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getEstadoValidacion() {
        boolean validar = configuracionService.debeValidarArbitrosParaSimulacion();
        
        Map<String, Object> response = new HashMap<>();
        response.put("validar", validar);
        response.put("descripcion", validar ? 
                "Se requiere aceptación de todos los árbitros" :
                "Simulación libre sin validación");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * API REST: Establecer valor de configuración booleana
     */
    @PostMapping("/api/set-boolean")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> setConfigBoolean(
            @RequestParam String clave,
            @RequestParam boolean valor,
            @RequestParam String descripcion,
            @RequestParam(required = false, defaultValue = "admin@caba.com") String modificadoPor) {
        
        configuracionService.setConfigBoolean(clave, valor, descripcion, modificadoPor);
        
        Map<String, Object> response = new HashMap<>();
        response.put("ok", true);
        response.put("mensaje", "Configuración actualizada correctamente");
        
        return ResponseEntity.ok(response);
    }
}
