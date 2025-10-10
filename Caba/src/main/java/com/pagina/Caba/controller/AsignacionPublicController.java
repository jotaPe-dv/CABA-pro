package com.pagina.Caba.controller;

import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.UsuarioArbitroLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/asignaciones")
public class AsignacionPublicController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private UsuarioArbitroLookupService usuarioArbitroLookupService;

    @GetMapping("/mis-asignaciones")
    public String misAsignaciones(Authentication authentication, Model model) {
        String email = authentication.getName();
        System.out.println("[DEBUG] Árbitro solicitando sus asignaciones: " + email);
        
        var arbitroOpt = usuarioArbitroLookupService.buscarPorEmail(email);
        if (arbitroOpt.isEmpty()) {
            System.out.println("[DEBUG] No se encontró árbitro para email: " + email);
            model.addAttribute("error", "No se encontró el árbitro para el usuario actual");
            model.addAttribute("asignaciones", java.util.Collections.emptyList());
        } else {
            var arbitro = arbitroOpt.get();
            System.out.println("[DEBUG] Árbitro encontrado: " + arbitro.getNombreCompleto() + " (ID: " + arbitro.getId() + ")");
            
            var asignaciones = asignacionService.obtenerAsignacionesPorArbitro(arbitro.getId());
            System.out.println("[DEBUG] Asignaciones encontradas: " + asignaciones.size());
            
            model.addAttribute("asignaciones", asignaciones);
            model.addAttribute("arbitro", arbitro);
        }
        model.addAttribute("pageTitle", "Mis Asignaciones");
        return "arbitro/asignaciones";
    }

    @PostMapping("/aceptar/{id}")
    public String aceptarAsignacion(@PathVariable Long id, 
                                    @RequestParam(required = false) String comentarios,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            System.out.println("[DEBUG] Árbitro " + email + " aceptando asignación ID: " + id);
            
            asignacionService.aceptarAsignacion(id, comentarios);
            redirectAttributes.addFlashAttribute("success", "Asignación aceptada exitosamente");
            System.out.println("[DEBUG] Asignación aceptada exitosamente");
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al aceptar asignación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al aceptar asignación: " + e.getMessage());
        }
        return "redirect:/asignaciones/mis-asignaciones";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazarAsignacion(@PathVariable Long id, 
                                     @RequestParam String motivo,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            System.out.println("[DEBUG] Árbitro " + email + " rechazando asignación ID: " + id + ", motivo: " + motivo);
            
            asignacionService.rechazarAsignacion(id, motivo);
            redirectAttributes.addFlashAttribute("success", "Asignación rechazada");
            System.out.println("[DEBUG] Asignación rechazada exitosamente");
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al rechazar asignación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al rechazar asignación: " + e.getMessage());
        }
        return "redirect:/asignaciones/mis-asignaciones";
    }
}
