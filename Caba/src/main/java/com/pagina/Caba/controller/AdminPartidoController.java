    // ...existing code...
package com.pagina.Caba.controller;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.service.PartidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/partidos")
public class AdminPartidoController {
    @GetMapping("/nuevo")
    public String nuevoPartido(Model model) {
        model.addAttribute("partido", new Partido());
        return "admin/partido-form";
    }
    @Autowired
    private PartidoService partidoService;

    @Autowired
    private com.pagina.Caba.service.AsignacionService asignacionService;

    @GetMapping
    public String listarPartidos(Model model) {
        List<Partido> partidos = partidoService.findAll();
        java.util.Map<Long, java.util.List<String>> arbitrosPorPartido = new java.util.HashMap<>();
        for (Partido partido : partidos) {
            java.util.List<com.pagina.Caba.model.Asignacion> asignaciones = asignacionService.findByPartidoId(partido.getId());
            java.util.List<String> nombres = new java.util.ArrayList<>();
            for (com.pagina.Caba.model.Asignacion asignacion : asignaciones) {
                if (asignacion.getArbitro() != null) {
                    nombres.add(asignacion.getArbitro().getNombre());
                }
            }
            arbitrosPorPartido.put(partido.getId(), nombres);
        }
        model.addAttribute("partidos", partidos);
        model.addAttribute("arbitrosPorPartido", arbitrosPorPartido);
        model.addAttribute("partidoEditar", new Partido());
        return "admin/partidos";
    }

    @PostMapping("/guardar")
    public String guardarPartido(@ModelAttribute Partido partido) {
        partidoService.save(partido);
        return "redirect:/admin/partidos";
    }

    @GetMapping("/editar/{id}")
    public String editarPartido(@PathVariable Long id, Model model) {
        Partido partido = partidoService.findById(id).orElse(new Partido());
        model.addAttribute("partido", partido);
        return "admin/partido-edit";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPartido(@PathVariable Long id) {
        partidoService.deleteById(id);
        return "redirect:/admin/partidos";
    }
}