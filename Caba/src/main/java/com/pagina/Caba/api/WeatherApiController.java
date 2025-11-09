package com.pagina.Caba.api;

import com.pagina.Caba.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * API REST para consultar información del clima
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/clima")
@Tag(name = "Clima", description = "Consulta de información meteorológica usando OpenWeatherMap")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class WeatherApiController {

    @Autowired
    private WeatherService weatherService;

    /**
     * Obtiene el clima actual de una ciudad
     */
    @GetMapping("/ciudad/{ciudad}")
    @Operation(summary = "Obtener clima por ciudad",
               description = "Consulta el clima actual de una ciudad usando OpenWeatherMap API. Los resultados se cachean por 5 minutos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clima obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ciudad no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<WeatherService.WeatherData> obtenerClimaPorCiudad(
            @Parameter(description = "Nombre de la ciudad", required = true, example = "Buenos Aires")
            @PathVariable String ciudad) {
        
        WeatherService.WeatherData clima = weatherService.obtenerClimaPorCiudad(ciudad);
        
        if (clima == null || "No disponible".equals(clima.getCiudad())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(clima);
    }

    /**
     * Obtiene el clima por coordenadas geográficas
     */
    @GetMapping("/coordenadas")
    @Operation(summary = "Obtener clima por coordenadas",
               description = "Consulta el clima actual usando coordenadas geográficas (latitud y longitud)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clima obtenido exitosamente"),
        @ApiResponse(responseCode = "400", description = "Coordenadas inválidas"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<WeatherService.WeatherData> obtenerClimaPorCoordenadas(
            @Parameter(description = "Latitud", required = true, example = "-34.6037")
            @RequestParam double lat,
            @Parameter(description = "Longitud", required = true, example = "-58.3816")
            @RequestParam double lon) {
        
        WeatherService.WeatherData clima = weatherService.obtenerClimaPorCoordenadas(lat, lon);
        
        if (clima == null || "No disponible".equals(clima.getCiudad())) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(clima);
    }

    /**
     * Obtiene el clima actual sin autenticación (para el widget del navbar)
     */
    @GetMapping
    @Operation(summary = "Obtener clima público",
               description = "Endpoint público para obtener el clima de una ciudad. Usado por el widget del navbar.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Clima obtenido exitosamente"),
        @ApiResponse(responseCode = "404", description = "Ciudad no encontrada")
    })
    public ResponseEntity<WeatherService.WeatherData> obtenerClimaPublico(
            @Parameter(description = "Nombre de la ciudad (opcional, default: Medellin)", example = "Medellin")
            @RequestParam(required = false, defaultValue = "Medellin") String ciudad) {
        
        System.out.println("🌤️ [WEATHER API] Solicitud de clima para: " + ciudad);
        
        WeatherService.WeatherData clima = weatherService.obtenerClimaPorCiudad(ciudad);
        
        if (clima == null || "No disponible".equals(clima.getCiudad())) {
            System.out.println("❌ [WEATHER API] Clima no disponible para: " + ciudad);
            return ResponseEntity.notFound().build();
        }
        
        System.out.println("✅ [WEATHER API] Clima enviado: " + clima.getCiudad() + " - " + clima.getTemperaturaFormateada());
        return ResponseEntity.ok(clima);
    }

    /**
     * Endpoint de prueba para verificar la configuración de la API
     */
    @GetMapping("/test")
    @Operation(summary = "Test de API",
               description = "Endpoint de prueba para verificar que la integración con OpenWeatherMap funciona correctamente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "API funcionando correctamente")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> testApi() {
        WeatherService.WeatherData clima = weatherService.obtenerClimaPorCiudad("Buenos Aires");
        
        if (clima != null && !"No disponible".equals(clima.getCiudad())) {
            return ResponseEntity.ok("✅ API funcionando correctamente. Clima en " + clima.getCiudad() + 
                                   ": " + clima.getTemperaturaFormateada() + " - " + clima.getDescripcion());
        }
        
        return ResponseEntity.ok("⚠️ API configurada pero sin API key válida. Usando datos de prueba.");
    }
}
