package com.pagina.Caba.controller;

import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.UsuarioArbitroLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
        var arbitroOpt = usuarioArbitroLookupService.buscarPorEmail(email);
        if (arbitroOpt.isEmpty()) {
            model.addAttribute("error", "No se encontró el árbitro para el usuario actual");
            model.addAttribute("asignaciones", java.util.Collections.emptyList());
        } else {
            var asignaciones = asignacionService.obtenerAsignacionesPorArbitro(arbitroOpt.get().getId());
            model.addAttribute("asignaciones", asignaciones);
        }
        model.addAttribute("pageTitle", "Mis Asignaciones");
        return "arbitro/asignaciones";
    }
}
