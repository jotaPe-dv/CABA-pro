package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ArbitroDisponibilidadController {
    
    @Autowired
    private ArbitroService arbitroService;
    
    @GetMapping("/arbitro/disponibilidad")
    public String disponibilidad(Model model, Authentication authentication) {
        String email = authentication.getName();
        Arbitro arbitro = arbitroService.obtenerTodos().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("disponible", arbitro.getDisponible());
        
        return "arbitro/disponibilidad";
    }
    
    @PostMapping("/arbitro/disponibilidad/cambiar")
    public String cambiarDisponibilidad(@RequestParam("disponible") boolean disponible,
                                       @RequestParam(value = "returnTo", required = false, defaultValue = "disponibilidad") String returnTo,
                                       Authentication authentication,
                                       RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            Arbitro arbitro = arbitroService.obtenerTodos().stream()
                    .filter(a -> a.getEmail().equalsIgnoreCase(email))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
            
            arbitroService.cambiarDisponibilidad(arbitro.getId(), disponible);
            
            String mensaje = disponible ? 
                "Has sido marcado como DISPONIBLE para asignaciones" : 
                "Has sido marcado como NO DISPONIBLE para asignaciones";
            redirectAttributes.addFlashAttribute("success", mensaje);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar disponibilidad: " + e.getMessage());
        }
        
        // Redirigir según el parámetro returnTo
        if ("dashboard".equals(returnTo)) {
            return "redirect:/arbitro/dashboard";
        }
        return "redirect:/arbitro/disponibilidad";
    }
}
