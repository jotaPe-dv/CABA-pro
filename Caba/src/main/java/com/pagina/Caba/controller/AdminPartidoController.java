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

    @GetMapping
    public String listarPartidos(Model model) {
        List<Partido> partidos = partidoService.findAll();
        model.addAttribute("partidos", partidos);
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