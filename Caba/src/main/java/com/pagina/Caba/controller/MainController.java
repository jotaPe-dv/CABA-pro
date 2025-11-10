// ...existing code...
package com.pagina.Caba.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.LiquidacionService;
import com.pagina.Caba.service.PartidoService;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.model.EstadoLiquidacion;

@Controller
public class MainController {
    @Autowired
    private ArbitroService arbitroService;
    @Autowired
    private AsignacionService asignacionService;
    @Autowired
    private LiquidacionService liquidacionService;
    @Autowired
    private PartidoService partidoService;

    @GetMapping("/")
    public String home(Authentication authentication) {
        // Si el usuario está autenticado, redirigir a su dashboard
        if (authentication != null && authentication.isAuthenticated()) {
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return "redirect:/admin/dashboard";
            } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ARBITRO"))) {
                return "redirect:/arbitro/dashboard";
            }
        }
        // Si no está autenticado, mostrar landing page
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        // Redirigir según el rol del usuario
        if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            return "redirect:/admin/dashboard";
        } else if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ARBITRO"))) {
            return "redirect:/arbitro/dashboard";
        }
        
        return "redirect:/login";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        model.addAttribute("pageTitle", "Panel de Administrador");
        long arbitrosActivos = arbitroService.contarArbitrosActivos();
        long asignacionesPendientes = asignacionService.contarAsignacionesPorEstado(EstadoAsignacion.PENDIENTE);
        long liquidacionesPendientes = liquidacionService.contarLiquidacionesPorEstado(EstadoLiquidacion.PENDIENTE);
        long partidosDelMes = partidoService.contarPartidosDelMesActual();
        model.addAttribute("arbitrosActivos", arbitrosActivos);
        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("liquidacionesPendientes", liquidacionesPendientes);
        model.addAttribute("partidosDelMes", partidosDelMes);
        return "admin/dashboard";
    }

    @GetMapping("/arbitro/dashboard")
    public String arbitroDashboard(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "Panel de Árbitro");

        // Obtener el árbitro autenticado
        String email = authentication.getName();
        com.pagina.Caba.model.Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst().orElse(null);
        if (arbitro == null) {
            // Si no se encuentra, redirigir al login
            return "redirect:/login";
        }
        Long arbitroId = arbitro.getId();
        
        // Agregar estado de disponibilidad
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("disponible", arbitro.getDisponible());

        // Estadísticas
    var asignacionesPendientesList = asignacionService.obtenerAsignacionesPendientesArbitro(arbitroId);
    long asignacionesPendientes = asignacionesPendientesList.size();
        long partidosCompletados = asignacionService.obtenerAsignacionesPorArbitro(arbitroId).stream()
            .filter(a -> a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.COMPLETADA).count();
        long liquidacionesPendientes = liquidacionService.obtenerLiquidacionesPorArbitro(arbitroId).stream()
            .filter(l -> l.getEstado() == com.pagina.Caba.model.EstadoLiquidacion.PENDIENTE).count();

        // Ingresos del mes (liquidaciones pagadas este mes)
        java.time.LocalDateTime inicioMes = java.time.LocalDate.now().withDayOfMonth(1).atStartOfDay();
        java.time.LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);
        java.math.BigDecimal ingresosMes = liquidacionService.obtenerLiquidacionesPorArbitro(arbitroId).stream()
            .filter(l -> l.getEstado() == com.pagina.Caba.model.EstadoLiquidacion.PAGADA)
            .filter(l -> l.getFechaPago() != null &&
                        !l.getFechaPago().isBefore(inicioMes) &&
                        !l.getFechaPago().isAfter(finMes))
            .map(com.pagina.Caba.model.Liquidacion::getMonto)
            .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

        model.addAttribute("asignacionesPendientes", asignacionesPendientes);
        model.addAttribute("asignacionesPendientesList", asignacionesPendientesList);

        // Próximos partidos: asignaciones aceptadas o completadas con fecha futura
        var proximosPartidos = asignacionService.obtenerAsignacionesPorArbitro(arbitroId).stream()
            .filter(a -> (a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.ACEPTADA ||
                         a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.COMPLETADA)
                        && a.getPartido().getFechaPartido().isAfter(java.time.LocalDateTime.now()))
            .sorted(java.util.Comparator.comparing(a -> a.getPartido().getFechaPartido()))
            .limit(5)
            .toList();
        model.addAttribute("proximosPartidos", proximosPartidos);
        model.addAttribute("partidosCompletados", partidosCompletados);
        model.addAttribute("liquidacionesPendientes", liquidacionesPendientes);
        model.addAttribute("ingresosMes", ingresosMes);
        return "arbitro/dashboard";
    }
    @GetMapping("/arbitro/calendario")
    public String arbitroCalendario(Authentication authentication, Model model) {
        String email = authentication.getName();
        com.pagina.Caba.model.Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst().orElse(null);
        if (arbitro == null) {
            return "redirect:/login";
        }
        Long arbitroId = arbitro.getId();
        var asignaciones = asignacionService.obtenerAsignacionesPorArbitro(arbitroId).stream()
            .sorted(java.util.Comparator.comparing(a -> a.getPartido().getFechaPartido()))
            .toList();
        model.addAttribute("asignaciones", asignaciones);
        model.addAttribute("pageTitle", "Calendario de Partidos");
        return "arbitro/calendario";
    }
}