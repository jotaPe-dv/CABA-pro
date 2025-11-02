package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para las acciones de los árbitros sobre sus asignaciones
 */
@Controller
@RequestMapping("/asignaciones")
public class AsignacionArbitroController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ArbitroService arbitroService;

    /**
     * Página de "Mis Asignaciones" para el árbitro autenticado
     */
    @GetMapping("/mis-asignaciones")
    public String misAsignaciones(Authentication authentication, Model model) {
        // Obtener el árbitro autenticado
        String email = authentication.getName();
        Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElse(null);

        if (arbitro == null) {
            return "redirect:/login";
        }

        // Obtener todas las asignaciones del árbitro
        List<Asignacion> todasAsignaciones = asignacionService.obtenerAsignacionesPorArbitro(arbitro.getId());
        
        // Separar por estado
        List<Asignacion> pendientes = todasAsignaciones.stream()
            .filter(a -> a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.PENDIENTE)
            .toList();
        
        List<Asignacion> aceptadas = todasAsignaciones.stream()
            .filter(a -> a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.ACEPTADA)
            .toList();
        
        List<Asignacion> completadas = todasAsignaciones.stream()
            .filter(a -> a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.COMPLETADA)
            .toList();
        
        List<Asignacion> rechazadas = todasAsignaciones.stream()
            .filter(a -> a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.RECHAZADA)
            .toList();

        model.addAttribute("pendientes", pendientes);
        model.addAttribute("aceptadas", aceptadas);
        model.addAttribute("completadas", completadas);
        model.addAttribute("rechazadas", rechazadas);
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("pageTitle", "Mis Asignaciones");

        return "arbitro/asignaciones";
    }

    /**
     * Aceptar una asignación (GET para enlaces directos)
     */
    @GetMapping("/aceptar/{id}")
    public String aceptarAsignacion(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar que la asignación pertenece al árbitro autenticado
            Optional<Asignacion> asignacionOpt = asignacionService.obtenerPorId(id);
            if (asignacionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Asignación no encontrada");
                return "redirect:/arbitro/dashboard";
            }

            Asignacion asignacion = asignacionOpt.get();
            String emailArbitro = authentication.getName();
            
            if (!asignacion.getArbitro().getEmail().equalsIgnoreCase(emailArbitro)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para aceptar esta asignación");
                return "redirect:/arbitro/dashboard";
            }

            // Aceptar la asignación
            asignacionService.aceptarAsignacion(id, null);
            
            redirectAttributes.addFlashAttribute("success", 
                "✅ Asignación aceptada exitosamente. Partido: " + 
                asignacion.getPartido().getEquipoLocal() + " vs " + 
                asignacion.getPartido().getEquipoVisitante());
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede aceptar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al aceptar asignación: " + e.getMessage());
        }

        return "redirect:/arbitro/dashboard";
    }

    /**
     * Mostrar formulario para rechazar con motivo
     */
    @GetMapping("/rechazar/{id}")
    public String mostrarFormularioRechazo(
            @PathVariable Long id,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<Asignacion> asignacionOpt = asignacionService.obtenerPorId(id);
            if (asignacionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Asignación no encontrada");
                return "redirect:/arbitro/dashboard";
            }

            Asignacion asignacion = asignacionOpt.get();
            String emailArbitro = authentication.getName();
            
            if (!asignacion.getArbitro().getEmail().equalsIgnoreCase(emailArbitro)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para rechazar esta asignación");
                return "redirect:/arbitro/dashboard";
            }

            model.addAttribute("asignacion", asignacion);
            model.addAttribute("pageTitle", "Rechazar Asignación");
            return "arbitro/rechazar-asignacion";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            return "redirect:/arbitro/dashboard";
        }
    }

    /**
     * Procesar el rechazo con motivo (POST)
     */
    @PostMapping("/rechazar/{id}")
    public String rechazarAsignacion(
            @PathVariable Long id,
            @RequestParam String motivoRechazo,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Verificar que la asignación pertenece al árbitro autenticado
            Optional<Asignacion> asignacionOpt = asignacionService.obtenerPorId(id);
            if (asignacionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Asignación no encontrada");
                return "redirect:/arbitro/dashboard";
            }

            Asignacion asignacion = asignacionOpt.get();
            String emailArbitro = authentication.getName();
            
            if (!asignacion.getArbitro().getEmail().equalsIgnoreCase(emailArbitro)) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para rechazar esta asignación");
                return "redirect:/arbitro/dashboard";
            }

            // Validar que se proporcionó un motivo
            if (motivoRechazo == null || motivoRechazo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debes proporcionar un motivo para rechazar la asignación");
                return "redirect:/asignaciones/rechazar/" + id;
            }

            // Rechazar la asignación
            asignacionService.rechazarAsignacion(id, motivoRechazo);
            
            redirectAttributes.addFlashAttribute("warning", 
                "⚠️ Asignación rechazada. Partido: " + 
                asignacion.getPartido().getEquipoLocal() + " vs " + 
                asignacion.getPartido().getEquipoVisitante());
            
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "No se puede rechazar: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar asignación: " + e.getMessage());
        }

        return "redirect:/arbitro/dashboard";
    }

    /**
     * Acción rápida con modal (vía AJAX/POST con confirmación)
     */
    @PostMapping("/aceptar-rapido/{id}")
    @ResponseBody
    public String aceptarRapido(
            @PathVariable Long id,
            Authentication authentication) {
        
        try {
            Optional<Asignacion> asignacionOpt = asignacionService.obtenerPorId(id);
            if (asignacionOpt.isEmpty()) {
                return "error:Asignación no encontrada";
            }

            Asignacion asignacion = asignacionOpt.get();
            String emailArbitro = authentication.getName();
            
            if (!asignacion.getArbitro().getEmail().equalsIgnoreCase(emailArbitro)) {
                return "error:No autorizado";
            }

            asignacionService.aceptarAsignacion(id, null);
            return "success:Asignación aceptada";
            
        } catch (Exception e) {
            return "error:" + e.getMessage();
        }
    }
}
