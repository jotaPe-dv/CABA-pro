package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/arbitros")
public class ArbitroController {

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private AsignacionService asignacionService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("arbitros", arbitroService.obtenerTodos());
        model.addAttribute("pageTitle", "Gestión de Árbitros");
        return "admin/arbitros/lista";
    }

    @GetMapping("/disponibles")
    public String disponibles(Model model) {
        model.addAttribute("arbitros", arbitroService.obtenerArbitrosDisponibles());
        model.addAttribute("pageTitle", "Árbitros Disponibles");
        return "admin/arbitros/disponibles";
    }

    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        model.addAttribute("pageTitle", "Nuevo Árbitro");
        return "admin/arbitros/nuevo";
    }

    @PostMapping("/guardar")

    public String guardarNuevo(@ModelAttribute("arbitro") Arbitro arbitro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Nuevo Árbitro");
            return "admin/arbitros/nuevo";
        }
        arbitroService.guardar(arbitro);
        return "redirect:/arbitros";
    }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@org.springframework.web.bind.annotation.PathVariable Long id, Model model) {
        Arbitro arbitro = arbitroService.obtenerPorId(id).orElse(null);
        if (arbitro == null) {
            return "redirect:/arbitros";
        }
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("pageTitle", "Editar Árbitro");
        return "admin/arbitros/editar";
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@org.springframework.web.bind.annotation.PathVariable Long id, @ModelAttribute("arbitro") Arbitro arbitro, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Editar Árbitro");
            return "admin/arbitros/editar";
        }
        arbitro.setId(id);
        arbitroService.guardar(arbitro);
        return "redirect:/arbitros";
    }
}
