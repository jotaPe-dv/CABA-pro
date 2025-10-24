package com.pagina.Caba.controller;

import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.repository.AdministradorRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@Controller
public class TorneosWebController {
    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @GetMapping("/torneos")
    public String listarTorneos(Model model) {
        List<Torneo> torneos = torneoRepository.findAll();
        model.addAttribute("torneos", torneos);
        return "torneos";
    }

    @GetMapping("/torneos/nuevo")
    public String mostrarFormularioNuevoTorneo(Model model) {
        model.addAttribute("torneo", new Torneo());
        return "torneos/nuevo";
    }

    @GetMapping("/torneos/editar/{id}")
    public String mostrarFormularioEditarTorneo(@PathVariable Long id, Model model) {
        Torneo torneo = torneoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + id));
        model.addAttribute("torneo", torneo);
        return "torneos/editar";
    }

    @PostMapping("/torneos/guardar")
    public String guardarTorneo(@ModelAttribute Torneo torneo) {
        // Obtener el usuario autenticado
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Administrador admin = administradorRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            torneo.setAdministrador(admin);
            torneoRepository.save(torneo);
        }
        // Si no hay admin autenticado, no guarda el torneo (opcional: podrías lanzar error o redirigir con mensaje)
        return "redirect:/torneos";
    }

    @PostMapping("/torneos/actualizar/{id}")
    public String actualizarTorneo(@PathVariable Long id, @ModelAttribute Torneo torneo) {
        Torneo existente = torneoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + id));
        existente.setNombre(torneo.getNombre());
        existente.setDescripcion(torneo.getDescripcion());
        existente.setFechaInicio(torneo.getFechaInicio());
        existente.setFechaFin(torneo.getFechaFin());
        existente.setActivo(torneo.getActivo());
        torneoRepository.save(existente);
        return "redirect:/torneos";
    }

    // --- Confirmar eliminación ---
    @GetMapping("/torneos/eliminar/{id}")
    public String mostrarConfirmacionEliminarTorneo(@PathVariable Long id, Model model) {
        Torneo torneo = torneoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Torneo no encontrado: " + id));
        model.addAttribute("torneo", torneo);
        return "torneos/eliminar";
    }

    // --- Eliminar torneo ---
    @PostMapping("/torneos/eliminar/{id}")
    public String eliminarTorneo(@PathVariable Long id, org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            Torneo torneo = torneoRepository.findById(id).orElse(null);
            if (torneo != null) {
                // Verificar si tiene partidos asociados
                List<com.pagina.Caba.model.Partido> partidos = torneo.getPartidos() != null ? new java.util.ArrayList<>(torneo.getPartidos()) : java.util.Collections.emptyList();
                if (!partidos.isEmpty()) {
                    redirectAttributes.addFlashAttribute("error", "No se puede eliminar el torneo porque tiene " + partidos.size() + " partidos asociados. Elimine primero los partidos o reasígnelos.");
                    return "redirect:/torneos";
                }
            }
            torneoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Torneo eliminado correctamente.");
            return "redirect:/torneos";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el torneo: " + e.getMessage());
            return "redirect:/torneos";
        }
    }
}
