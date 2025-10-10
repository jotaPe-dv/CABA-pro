package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.UsuarioArbitroLookupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/debug")
public class DebugAsignacionController {

    @Autowired
    private AsignacionService asignacionService;

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private UsuarioArbitroLookupService usuarioArbitroLookupService;

    @GetMapping("/asignaciones")
    @ResponseBody
    public Map<String, Object> debugAsignaciones(Authentication authentication) {
        Map<String, Object> debug = new HashMap<>();
        
        String email = authentication != null ? authentication.getName() : "NO_AUTH";
        debug.put("emailUsuario", email);
        
        // Buscar árbitro por email
        var arbitroOpt = usuarioArbitroLookupService.buscarPorEmail(email);
        debug.put("arbitroEncontrado", arbitroOpt.isPresent());
        
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            debug.put("arbitroId", arbitro.getId());
            debug.put("arbitroNombre", arbitro.getNombreCompleto());
            debug.put("arbitroEmail", arbitro.getEmail());
            debug.put("arbitroActivo", arbitro.getActivo());
            debug.put("arbitroDisponible", arbitro.getDisponible());
            
            // Buscar asignaciones
            List<Asignacion> asignaciones = asignacionService.obtenerAsignacionesPorArbitro(arbitro.getId());
            debug.put("cantidadAsignaciones", asignaciones.size());
            debug.put("asignaciones", asignaciones.stream().map(a -> {
                Map<String, Object> asignacionInfo = new HashMap<>();
                asignacionInfo.put("id", a.getId());
                asignacionInfo.put("estado", a.getEstado());
                asignacionInfo.put("rolEspecifico", a.getRolEspecifico());
                asignacionInfo.put("comentarios", a.getComentarios());
                if (a.getPartido() != null) {
                    asignacionInfo.put("partidoId", a.getPartido().getId());
                    asignacionInfo.put("equipoLocal", a.getPartido().getEquipoLocal());
                    asignacionInfo.put("equipoVisitante", a.getPartido().getEquipoVisitante());
                }
                if (a.getArbitro() != null) {
                    asignacionInfo.put("arbitroAsignado", a.getArbitro().getId());
                }
                return asignacionInfo;
            }).toList());
        }
        
        // Información general
        List<Arbitro> todosArbitros = arbitroService.obtenerTodos();
        debug.put("totalArbitros", todosArbitros.size());
        debug.put("arbitrosPorEmail", todosArbitros.stream().map(a -> 
            Map.of("id", a.getId(), "email", a.getEmail(), "nombre", a.getNombreCompleto())
        ).toList());
        
        // NUEVO: Específicamente verificar árbitros disponibles para asignaciones
        List<Arbitro> arbitrosDisponibles = arbitroService.obtenerArbitrosDisponibles();
        debug.put("totalArbitrosDisponibles", arbitrosDisponibles.size());
        debug.put("arbitrosDisponibles", arbitrosDisponibles.stream().map(a -> {
            Map<String, Object> info = new HashMap<>();
            info.put("id", a.getId());
            info.put("nombre", a.getNombreCompleto());
            info.put("email", a.getEmail());
            info.put("especialidad", a.getEspecialidad());
            info.put("escalafon", a.getEscalafon());
            info.put("activo", a.getActivo());
            info.put("disponible", a.getDisponible());
            return info;
        }).toList());
        
        List<Asignacion> todasAsignaciones = asignacionService.obtenerTodas();
        debug.put("totalAsignaciones", todasAsignaciones.size());
        debug.put("todasAsignaciones", todasAsignaciones.stream().map(a -> {
            Map<String, Object> info = new HashMap<>();
            info.put("id", a.getId());
            info.put("estado", a.getEstado());
            info.put("arbitroId", a.getArbitro() != null ? a.getArbitro().getId() : null);
            info.put("arbitroEmail", a.getArbitro() != null ? a.getArbitro().getEmail() : null);
            return info;
        }).toList());
        
        return debug;
    }
}