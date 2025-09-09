package com.pagina.Caba.controller;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.service.TorneoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/torneos")
public class AdminTorneoController {
    @GetMapping("/nuevo")
    public String nuevoTorneo(Model model) {
        model.addAttribute("torneo", new com.pagina.Caba.model.Torneo());
        return "admin/torneo-form";
    }
    @Autowired
    private TorneoService torneoService;

    @GetMapping
    public String listarTorneos(Model model) {
        List<Torneo> torneos = torneoService.findAll();
        model.addAttribute("torneos", torneos);
        model.addAttribute("torneoEditar", new Torneo());
        return "admin/torneos";
    }

    @PostMapping("/guardar")
    public String guardarTorneo(@ModelAttribute Torneo torneo) {
        torneoService.save(torneo);
        return "redirect:/admin/torneos";
    }

    @GetMapping("/editar/{id}")
    public String editarTorneo(@PathVariable Long id, Model model) {
        Torneo torneo = torneoService.findById(id).orElse(new Torneo());
        model.addAttribute("torneo", torneo);
        return "admin/torneo-edit";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarTorneo(@PathVariable Long id) {
        torneoService.deleteById(id);
        return "redirect:/admin/torneos";
    }
}