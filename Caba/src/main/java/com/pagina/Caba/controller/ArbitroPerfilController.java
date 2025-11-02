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
public class ArbitroPerfilController {
    
    @Autowired
    private ArbitroService arbitroService;
    
    @GetMapping("/arbitro/perfil")
    public String perfil(Model model, Authentication authentication) {
        String email = authentication.getName();
        Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        
        model.addAttribute("arbitro", arbitro);
        return "arbitro/perfil";
    }
    
    @PostMapping("/arbitro/perfil/actualizar")
    public String actualizarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String telefono,
            @RequestParam String direccion,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        
        try {
            String email = authentication.getName();
            Arbitro arbitro = arbitroService.obtenerTodos().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
            
            arbitro.setNombre(nombre);
            arbitro.setApellido(apellido);
            arbitro.setTelefono(telefono);
            arbitro.setDireccion(direccion);
            
            arbitroService.actualizar(arbitro.getId(), arbitro);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/arbitro/perfil";
    }
}
