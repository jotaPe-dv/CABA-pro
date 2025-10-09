package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.Arbitro;
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

@Controller
@RequestMapping("/admin/asignaciones")
public class AsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ArbitroService arbitroService;

    // ====== RUTAS PARA ADMIN ======
    
    @GetMapping
    public String listar(Model model) {
        System.out.println("[DEBUG] Cargando lista de asignaciones...");
        List<Asignacion> asignaciones = asignacionService.obtenerTodas();
        System.out.println("[DEBUG] Total asignaciones encontradas: " + asignaciones.size());
        
        model.addAttribute("asignaciones", asignaciones);
        // Crear lista de partidos únicos
        List<com.pagina.Caba.model.Partido> partidos = asignaciones.stream()
            .map(Asignacion::getPartido)
            .distinct()
            .toList();
        model.addAttribute("partidos", partidos);
        model.addAttribute("arbitros", arbitroService.obtenerTodos());
        model.addAttribute("pageTitle", "Gestión de Asignaciones");
        return "admin/asignaciones/lista";
    }

    @Autowired
    private com.pagina.Caba.repository.PartidoRepository partidoRepository;

    @Autowired
    private com.pagina.Caba.repository.TarifaRepository tarifaRepository;

    @GetMapping("/nueva")
    public String nuevaAsignacion(Model model) {
        model.addAttribute("asignacion", new Asignacion());
        model.addAttribute("arbitros", arbitroService.obtenerArbitrosDisponibles());
        model.addAttribute("partidos", partidoRepository.findByCompletadoFalse());
        model.addAttribute("pageTitle", "Nueva Asignación");
        return "admin/asignaciones/form";
    }

    @PostMapping("/guardar")
    public String guardar(
            @RequestParam Long partido,
            @RequestParam Long arbitroPrincipalId,
            @RequestParam Long arbitroAsistenteId,
            @RequestParam Long arbitroMesaId,
            @RequestParam(required = false) String comentarios,
            RedirectAttributes redirectAttributes) {
        System.out.println("[DEBUG] Entrando a guardar asignaciones");
        System.out.println("[DEBUG] Parámetros recibidos: partido=" + partido + 
                          ", principal=" + arbitroPrincipalId + 
                          ", asistente=" + arbitroAsistenteId + 
                          ", mesa=" + arbitroMesaId);
        try {
            if (arbitroPrincipalId.equals(arbitroAsistenteId) || arbitroPrincipalId.equals(arbitroMesaId) || arbitroAsistenteId.equals(arbitroMesaId)) {
                throw new IllegalArgumentException("No se puede asignar el mismo árbitro a más de un rol en el mismo partido");
            }
            var partidoOpt = partidoRepository.findById(partido);
            if (partidoOpt.isEmpty()) {
                throw new IllegalArgumentException("Partido no encontrado");
            }
            var partidoObj = partidoOpt.get();
            String tipoPartido = partidoObj.getTipoPartido();
            
            // Crear las tres asignaciones
            crearAsignacionConRol(partidoObj, arbitroPrincipalId, "Principal", tipoPartido, comentarios);
            crearAsignacionConRol(partidoObj, arbitroAsistenteId, "Auxiliar", tipoPartido, comentarios);
            crearAsignacionConRol(partidoObj, arbitroMesaId, "Mesa", tipoPartido, comentarios);

            System.out.println("[DEBUG] Asignaciones creadas exitosamente");
            System.out.println("[DEBUG] Total asignaciones en BD antes del redirect: " + asignacionService.obtenerTodas().size());
            
            redirectAttributes.addFlashAttribute("success", "Asignaciones creadas exitosamente");
        } catch (Exception e) {
            System.out.println("[DEBUG] Error al crear asignaciones: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al crear asignaciones: " + e.getMessage());
        }
        return "redirect:/admin/asignaciones";
    }

    private void crearAsignacionConRol(
            com.pagina.Caba.model.Partido partido,
            Long arbitroId,
            String rol,
            String tipoPartido,
            String comentarios) {
        System.out.println("[DEBUG] Creando asignación para rol: " + rol + ", arbitro ID: " + arbitroId);
        
        var arbitroOpt = arbitroService.obtenerPorId(arbitroId);
        if (arbitroOpt.isEmpty()) {
            throw new IllegalArgumentException("Árbitro no encontrado para el rol " + rol);
        }
        var arbitro = arbitroOpt.get();
        
        // Buscar tarifa específica para este tipo de partido y escalafón del árbitro
        String escalafon = arbitro.getEscalafon();
        System.out.println("[DEBUG] Buscando tarifa para tipo: " + tipoPartido + ", escalafón: " + escalafon);
        
        var tarifaOpt = tarifaRepository.findTarifaVigentePorTipoYEscalafon(tipoPartido, escalafon, java.time.LocalDateTime.now());
        if (tarifaOpt.isEmpty()) {
            throw new IllegalArgumentException("No hay tarifa vigente para partido tipo '" + tipoPartido + "' y escalafón '" + escalafon + "' del árbitro " + arbitro.getNombreCompleto());
        }
        var tarifa = tarifaOpt.get();
        System.out.println("[DEBUG] Tarifa encontrada: " + tarifa.getDescripcion() + " = $" + tarifa.getMonto());
        
        // Validar conflicto de horario
        boolean conflicto = asignacionService.obtenerAsignacionesPorArbitro(arbitroId).stream()
                .anyMatch(a -> (a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.ACEPTADA
                        || a.getEstado() == com.pagina.Caba.model.EstadoAsignacion.PENDIENTE)
                        && a.getPartido().getFechaPartido().isEqual(partido.getFechaPartido()));
        if (conflicto) {
            throw new IllegalArgumentException("El árbitro " + arbitro.getNombreCompleto() + " ya tiene un partido asignado en esa fecha y hora");
        }
        Asignacion asignacion = new Asignacion();
        asignacion.setPartido(partido);
        asignacion.setArbitro(arbitro);
        asignacion.setTarifa(tarifa);
        asignacion.setMontoCalculado(tarifa.getMonto());
        asignacion.setEstado(com.pagina.Caba.model.EstadoAsignacion.PENDIENTE);
        asignacion.setRolEspecifico(rol);
        asignacion.setComentarios(comentarios);
        
        System.out.println("[DEBUG] Guardando asignación para rol: " + rol);
        Asignacion asignacionGuardada = asignacionService.guardar(asignacion);
        System.out.println("[DEBUG] Asignación guardada con ID: " + asignacionGuardada.getId());
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Optional<Asignacion> asignacion = asignacionService.obtenerPorId(id);
        if (asignacion.isPresent()) {
            model.addAttribute("asignacion", asignacion.get());
            model.addAttribute("arbitros", arbitroService.obtenerTodos());
            model.addAttribute("pageTitle", "Editar Asignación");
            return "admin/asignaciones/form";
        }
    return "redirect:/admin/asignaciones";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // El AsignacionService no tiene método eliminar, usamos el repositorio o comentamos
            redirectAttributes.addFlashAttribute("info", "Funcionalidad de eliminación pendiente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar asignación: " + e.getMessage());
        }
    return "redirect:/admin/asignaciones";
    }

    // ====== RUTAS PARA ARBITRO ======
    
    @Autowired
    private com.pagina.Caba.service.UsuarioArbitroLookupService usuarioArbitroLookupService;

    @GetMapping("/mis-asignaciones")
    public String misAsignaciones(Authentication authentication, Model model) {
        String email = authentication.getName();
        // Buscar el árbitro por email
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

    @PostMapping("/aceptar/{id}")
    public String aceptarAsignacion(@PathVariable Long id, 
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {
        try {
            String email = authentication.getName();
            asignacionService.aceptarAsignacion(id, email);
            redirectAttributes.addFlashAttribute("success", "Asignación aceptada exitosamente");
        } catch (Exception e) {
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
            asignacionService.rechazarAsignacion(id, motivo);
            redirectAttributes.addFlashAttribute("success", "Asignación rechazada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al rechazar asignación: " + e.getMessage());
        }
        return "redirect:/asignaciones/mis-asignaciones";
    }
}