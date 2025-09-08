package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.model.UserPrincipal;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/arbitro")
public class ArbitroViewController {
    
    @Autowired
    private AsignacionService asignacionService;
    
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        // Obtener árbitro actual
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        
        // Cargar asignaciones pendientes
        List<Asignacion> asignacionesPendientes = asignacionService
            .findByArbitroIdAndEstado(userPrincipal.getId(), EstadoAsignacion.PENDIENTE);
        
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        return "dashboard-arbitro";
    }
}
