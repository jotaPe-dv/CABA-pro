package com.pagina.Caba.api;

import com.pagina.Caba.dto.AsignacionDto;
import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * API REST para el dashboard del árbitro
 * Proporciona estadísticas, resumen y próximos partidos
 */
@RestController
@RequestMapping("/api/arbitro")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "http://localhost:5173"})
public class DashboardApiController {

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private LiquidacionService liquidacionService;

    /**
     * GET /api/arbitro/dashboard
     * Obtiene el dashboard completo del árbitro autenticado
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> obtenerDashboard() {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            Arbitro arbitro = arbitroOpt.get();
            Long arbitroId = arbitro.getId();
            
            // Obtener todas las asignaciones
            List<AsignacionDto> todasAsignaciones = asignacionService.obtenerPorArbitro(arbitroId);
            
            // Contar asignaciones por estado
            long totalAsignaciones = todasAsignaciones.size();
            long pendientes = todasAsignaciones.stream()
                .filter(a -> "PENDIENTE".equals(a.getEstado()))
                .count();
            long aceptadas = todasAsignaciones.stream()
                .filter(a -> "ACEPTADA".equals(a.getEstado()))
                .count();
            long completadas = todasAsignaciones.stream()
                .filter(a -> "COMPLETADA".equals(a.getEstado()))
                .count();
            long rechazadas = todasAsignaciones.stream()
                .filter(a -> "RECHAZADA".equals(a.getEstado()))
                .count();
            
            // Obtener liquidaciones
            List<LiquidacionDto> todasLiquidaciones = liquidacionService.obtenerPorArbitro(arbitroId);
            
            long liquidacionesPendientes = todasLiquidaciones.stream()
                .filter(l -> "PENDIENTE".equals(l.getEstado()))
                .count();
            long liquidacionesPagadas = todasLiquidaciones.stream()
                .filter(l -> "PAGADA".equals(l.getEstado()))
                .count();
            
            // Calcular total ganado (liquidaciones pagadas)
            BigDecimal totalGanado = todasLiquidaciones.stream()
                .filter(l -> "PAGADA".equals(l.getEstado()))
                .map(LiquidacionDto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Calcular total por cobrar (liquidaciones pendientes)
            BigDecimal totalPorCobrar = todasLiquidaciones.stream()
                .filter(l -> "PENDIENTE".equals(l.getEstado()))
                .map(LiquidacionDto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Próximos partidos (asignaciones aceptadas con fecha futura)
            List<AsignacionDto> proximosPartidos = todasAsignaciones.stream()
                .filter(a -> "ACEPTADA".equals(a.getEstado()))
                .filter(a -> a.getPartido().getFechaPartido().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(a -> a.getPartido().getFechaPartido()))
                .limit(5)
                .collect(Collectors.toList());
            
            // Últimos partidos completados
            List<AsignacionDto> ultimosPartidos = todasAsignaciones.stream()
                .filter(a -> "COMPLETADA".equals(a.getEstado()))
                .sorted(Comparator.comparing(AsignacionDto::getFechaAsignacion).reversed())
                .limit(5)
                .collect(Collectors.toList());
            
            // Estadísticas por tipo de partido (últimos 3 meses)
            LocalDateTime hace3Meses = LocalDateTime.now().minusMonths(3);
            Map<String, Long> partidosPorTipo = todasAsignaciones.stream()
                .filter(a -> "COMPLETADA".equals(a.getEstado()))
                .filter(a -> a.getFechaAsignacion().isAfter(hace3Meses))
                .collect(Collectors.groupingBy(
                    a -> a.getPartido().getTipoPartido(),
                    Collectors.counting()
                ));
            
            // Estadísticas por rol
            Map<String, Long> asignacionesPorRol = todasAsignaciones.stream()
                .filter(a -> "COMPLETADA".equals(a.getEstado()) || "ACEPTADA".equals(a.getEstado()))
                .collect(Collectors.groupingBy(
                    AsignacionDto::getRolEspecifico,
                    Collectors.counting()
                ));
            
            // Tasa de aceptación
            double tasaAceptacion = totalAsignaciones > 0 ? 
                (double) aceptadas / totalAsignaciones * 100 : 0;
            
            // Construir respuesta
            Map<String, Object> dashboard = new HashMap<>();
            
            // Resumen general
            Map<String, Object> resumen = new HashMap<>();
            resumen.put("totalAsignaciones", totalAsignaciones);
            resumen.put("pendientes", pendientes);
            resumen.put("aceptadas", aceptadas);
            resumen.put("completadas", completadas);
            resumen.put("rechazadas", rechazadas);
            resumen.put("tasaAceptacion", Math.round(tasaAceptacion * 100.0) / 100.0);
            dashboard.put("resumen", resumen);
            
            // Finanzas
            Map<String, Object> finanzas = new HashMap<>();
            finanzas.put("totalGanado", totalGanado);
            finanzas.put("totalPorCobrar", totalPorCobrar);
            finanzas.put("liquidacionesPendientes", liquidacionesPendientes);
            finanzas.put("liquidacionesPagadas", liquidacionesPagadas);
            dashboard.put("finanzas", finanzas);
            
            // Próximos partidos
            dashboard.put("proximosPartidos", proximosPartidos);
            
            // Últimos partidos
            dashboard.put("ultimosPartidos", ultimosPartidos);
            
            // Estadísticas
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("partidosPorTipo", partidosPorTipo);
            estadisticas.put("asignacionesPorRol", asignacionesPorRol);
            dashboard.put("estadisticas", estadisticas);
            
            // Info del árbitro
            Map<String, Object> arbitroInfo = new HashMap<>();
            arbitroInfo.put("nombre", arbitro.getNombre() + " " + arbitro.getApellido());
            arbitroInfo.put("email", arbitro.getEmail());
            arbitroInfo.put("especialidad", arbitro.getEspecialidad());
            arbitroInfo.put("escalafon", arbitro.getEscalafon());
            arbitroInfo.put("disponible", arbitro.isDisponible());
            dashboard.put("arbitro", arbitroInfo);
            
            return ResponseEntity.ok(dashboard);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener dashboard: " + e.getMessage()));
        }
    }

    /**
     * GET /api/arbitro/estadisticas
     * Obtiene estadísticas detalladas del árbitro
     */
    @GetMapping("/estadisticas")
    public ResponseEntity<?> obtenerEstadisticas(
            @RequestParam(required = false, defaultValue = "3") int meses) {
        try {
            String email = obtenerEmailUsuarioAutenticado();
            
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorEmail(email);
            
            if (arbitroOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Árbitro no encontrado"));
            }
            
            Long arbitroId = arbitroOpt.get().getId();
            
            // Obtener asignaciones del período
            LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(meses);
            List<AsignacionDto> asignaciones = asignacionService.obtenerPorArbitro(arbitroId)
                .stream()
                .filter(a -> a.getFechaAsignacion().isAfter(fechaInicio))
                .toList();
            
            // Estadísticas por mes
            Map<String, Long> asignacionesPorMes = asignaciones.stream()
                .filter(a -> "COMPLETADA".equals(a.getEstado()))
                .collect(Collectors.groupingBy(
                    a -> a.getFechaAsignacion().getMonth().toString(),
                    Collectors.counting()
                ));
            
            // Ingresos por mes
            List<LiquidacionDto> liquidaciones = liquidacionService.obtenerPorArbitro(arbitroId)
                .stream()
                .filter(l -> l.getFechaCreacion().isAfter(fechaInicio))
                .toList();
            
            Map<String, BigDecimal> ingresosPorMes = liquidaciones.stream()
                .filter(l -> "PAGADA".equals(l.getEstado()))
                .collect(Collectors.groupingBy(
                    l -> l.getFechaCreacion().getMonth().toString(),
                    Collectors.reducing(BigDecimal.ZERO, LiquidacionDto::getMonto, BigDecimal::add)
                ));
            
            Map<String, Object> estadisticas = new HashMap<>();
            estadisticas.put("periodo", meses + " meses");
            estadisticas.put("totalAsignaciones", asignaciones.size());
            estadisticas.put("asignacionesPorMes", asignacionesPorMes);
            estadisticas.put("ingresosPorMes", ingresosPorMes);
            estadisticas.put("totalIngresos", ingresosPorMes.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            return ResponseEntity.ok(estadisticas);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    /**
     * Método auxiliar para obtener el email del usuario autenticado
     */
    private String obtenerEmailUsuarioAutenticado() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }
}
