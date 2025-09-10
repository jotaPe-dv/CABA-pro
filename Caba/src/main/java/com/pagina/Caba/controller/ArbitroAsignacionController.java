
package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.model.UserPrincipal;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/arbitro/dashboard")
public class ArbitroAsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    // Mostrar asignaciones pendientes y historial en dashboard-arbitro.html
    @GetMapping("")
    public String verDashboard(Authentication auth, Model model) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        List<Asignacion> asignacionesPendientes = asignacionService.findByArbitroIdAndEstado(userPrincipal.getId(), EstadoAsignacion.PENDIENTE);
        List<Asignacion> asignacionesHistorial = asignacionService.findByArbitroId(userPrincipal.getId());
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("asignacionesHistorial", asignacionesHistorial);
        return "dashboard-arbitro";
    }

    // Aceptar asignación
    @PostMapping("/aceptar/{id}")
    public String aceptarAsignacion(@PathVariable Long id, Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        asignacionService.aceptarAsignacion(id, userPrincipal.getId());
        return "redirect:/arbitro/dashboard";
    }

    // Rechazar asignación
    @PostMapping("/rechazar/{id}")
    public String rechazarAsignacion(@PathVariable Long id, Authentication auth) {
        UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
        asignacionService.rechazarAsignacion(id, userPrincipal.getId());
        return "redirect:/arbitro/dashboard";
    }
}
