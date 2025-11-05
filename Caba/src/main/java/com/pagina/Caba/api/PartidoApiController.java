package com.pagina.Caba.api;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.repository.PartidoRepository;
import com.pagina.Caba.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST para gestión de Partidos
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@RestController
@RequestMapping("/api/v1/partidos")
@Tag(name = "Partidos", description = "Operaciones de gestión de partidos")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class PartidoApiController {

    @Autowired
    private PartidoRepository partidoRepository;
    
    @Autowired
    private WeatherService weatherService;

    /**
     * Lista todos los partidos con filtros opcionales
     */
    @GetMapping
    @Operation(summary = "Listar todos los partidos",
               description = "Obtiene la lista completa de partidos con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<List<Partido>> listarPartidos(
            @Parameter(description = "Filtrar por ID de torneo")
            @RequestParam(required = false) Long torneoId,
            @Parameter(description = "Filtrar solo completados")
            @RequestParam(required = false) Boolean completado) {
        
        List<Partido> partidos = partidoRepository.findAll();
        
        // Aplicar filtros
        if (torneoId != null) {
            partidos = partidos.stream()
                    .filter(p -> p.getTorneo() != null && torneoId.equals(p.getTorneo().getId()))
                    .collect(Collectors.toList());
        }
        
        if (completado != null) {
            partidos = partidos.stream()
                    .filter(p -> completado.equals(p.getCompletado()))
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(partidos);
    }

    /**
     * Obtiene un partido por ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener partido por ID",
               description = "Obtiene los datos completos de un partido específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido encontrado"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<Partido> obtenerPartido(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable Long id) {
        
        return partidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene un partido con información del clima
     */
    @GetMapping("/{id}/clima")
    @Operation(summary = "Obtener partido con clima",
               description = "Obtiene los datos del partido junto con la información meteorológica de la ubicación")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido y clima obtenidos exitosamente"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'ARBITRO')")
    public ResponseEntity<Map<String, Object>> obtenerPartidoConClima(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable Long id) {
        
        return partidoRepository.findById(id)
                .map(partido -> {
                    Map<String, Object> respuesta = new HashMap<>();
                    respuesta.put("partido", partido);
                    
                    // Extraer ciudad de la ubicación del partido
                    String ciudad = extractCityFromUbicacion(partido.getUbicacion());
                    WeatherService.WeatherData clima = weatherService.obtenerClimaPorCiudad(ciudad);
                    respuesta.put("clima", clima);
                    
                    return ResponseEntity.ok(respuesta);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Método auxiliar para extraer el nombre de la ciudad desde la ubicación
     * Ejemplos:
     * - "Estadio Monumental, Buenos Aires" -> "Buenos Aires"
     * - "Cancha de River, Capital Federal" -> "Capital Federal"
     * - "La Bombonera" -> "Buenos Aires" (default)
     */
    private String extractCityFromUbicacion(String ubicacion) {
        if (ubicacion == null || ubicacion.isEmpty()) {
            return "Buenos Aires"; // Default
        }
        
        // Si la ubicación contiene coma, tomar lo que viene después
        if (ubicacion.contains(",")) {
            String[] partes = ubicacion.split(",");
            return partes[partes.length - 1].trim();
        }
        
        // Si menciona "Buenos Aires" o "CABA" o "Capital"
        String ubicacionLower = ubicacion.toLowerCase();
        if (ubicacionLower.contains("buenos aires") || 
            ubicacionLower.contains("caba") || 
            ubicacionLower.contains("capital")) {
            return "Buenos Aires";
        }
        
        // Por defecto, retornar Buenos Aires
        return "Buenos Aires";
    }

    /**
     * Crea un nuevo partido
     */
    @PostMapping
    @Operation(summary = "Crear nuevo partido",
               description = "Registra un nuevo partido en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partido creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Partido> crearPartido(@RequestBody Partido partido) {
        partido.setCompletado(false);
        Partido nuevoPartido = partidoRepository.save(partido);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPartido);
    }

    /**
     * Actualiza un partido existente
     */
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar partido",
               description = "Actualiza los datos de un partido existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido actualizado"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Partido> actualizarPartido(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable Long id,
            @RequestBody Partido partidoActualizado) {
        
        Partido partido = partidoRepository.findById(id)
                .orElse(null);
        
        if (partido == null) {
            return ResponseEntity.notFound().build();
        }
        
        partido.setEquipoLocal(partidoActualizado.getEquipoLocal());
        partido.setEquipoVisitante(partidoActualizado.getEquipoVisitante());
        partido.setFechaPartido(partidoActualizado.getFechaPartido());
        partido.setUbicacion(partidoActualizado.getUbicacion());
        
        Partido actualizado = partidoRepository.save(partido);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Marca un partido como completado
     */
    @PostMapping("/{id}/completar")
    @Operation(summary = "Marcar partido como completado",
               description = "Marca un partido como finalizado con resultado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partido marcado como completado"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Partido> completarPartido(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable Long id,
            @Parameter(description = "Goles equipo local")
            @RequestParam(required = false) Integer golesLocal,
            @Parameter(description = "Goles equipo visitante")
            @RequestParam(required = false) Integer golesVisitante) {
        
        Partido partido = partidoRepository.findById(id)
                .orElse(null);
        
        if (partido == null) {
            return ResponseEntity.notFound().build();
        }
        
        partido.completarPartido(golesLocal, golesVisitante);
        
        Partido actualizado = partidoRepository.save(partido);
        return ResponseEntity.ok(actualizado);
    }

    /**
     * Elimina un partido
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar partido",
               description = "Elimina un partido del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Partido eliminado"),
        @ApiResponse(responseCode = "404", description = "Partido no encontrado"),
        @ApiResponse(responseCode = "403", description = "Acceso denegado")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarPartido(
            @Parameter(description = "ID del partido", required = true)
            @PathVariable Long id) {
        
        if (partidoRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        partidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
