package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/arbitros")
public class ArbitroController {

    @Autowired
    private ArbitroService arbitroService;

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
    public String guardarNuevo(@Valid @ModelAttribute("arbitro") Arbitro arbitro,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Nuevo Árbitro");
            return "admin/arbitros/nuevo";
        }
        try {
            arbitroService.crear(arbitro);
            redirectAttributes.addFlashAttribute("success", "Árbitro creado correctamente");
        } catch (IllegalArgumentException ex) {
            result.reject("error.arbitro", ex.getMessage());
            model.addAttribute("pageTitle", "Nuevo Árbitro");
            return "admin/arbitros/nuevo";
        }
        return "redirect:/arbitros";
    }
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Arbitro arbitro = arbitroService.obtenerPorId(id).orElse(null);
        if (arbitro == null) {
            redirectAttributes.addFlashAttribute("error", "Árbitro no encontrado");
            return "redirect:/arbitros";
        }
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("pageTitle", "Editar Árbitro");
        return "admin/arbitros/editar";
    }

    @PostMapping("/editar/{id}")
    public String guardarEdicion(@PathVariable Long id,
                                 @Valid @ModelAttribute("arbitro") Arbitro arbitro,
                                 BindingResult result,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            arbitro.setId(id);
            model.addAttribute("pageTitle", "Editar Árbitro");
            return "admin/arbitros/editar";
        }
        try {
            arbitroService.actualizar(id, arbitro);
            redirectAttributes.addFlashAttribute("success", "Árbitro actualizado correctamente");
        } catch (IllegalArgumentException ex) {
            arbitro.setId(id);
            result.reject("error.arbitro", ex.getMessage());
            model.addAttribute("pageTitle", "Editar Árbitro");
            return "admin/arbitros/editar";
        }
        return "redirect:/arbitros";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<Arbitro> arbitroOpt = arbitroService.obtenerPorId(id);
            if (arbitroOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Árbitro no encontrado");
                return "redirect:/arbitros";
            }
            arbitroService.eliminar(id);
            redirectAttributes.addFlashAttribute("success", "Árbitro eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar árbitro: " + e.getMessage());
        }
        return "redirect:/arbitros";
    }
}
