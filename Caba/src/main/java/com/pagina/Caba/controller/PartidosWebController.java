
package com.pagina.Caba.controller;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.repository.PartidoRepository;
import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.repository.AdministradorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import jakarta.annotation.PostConstruct;
import java.util.List;

@Controller

public class PartidosWebController {

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AdministradorRepository administradorRepository;

    private static final String TORNEO_AMISTOSO_NOMBRE = "Amistosos";
    private Torneo torneoAmistoso;

    @PostConstruct
    public void initTorneoAmistoso() {
        List<Torneo> torneos = torneoRepository.findByNombreContainingIgnoreCase(TORNEO_AMISTOSO_NOMBRE);
        if (torneos.isEmpty()) {
            Administrador admin;
            List<Administrador> admins = administradorRepository.findAll();
            if (admins.isEmpty()) {
                // Crear admin por defecto con contraseña encriptada
                String rawPassword = "admin123";
                String encodedPassword = passwordEncoder.encode(rawPassword);
                admin = new Administrador("Admin", "Default", "admin@caba.com", encodedPassword, "SuperAdmin");
                admin.setActivo(true);
                admin.setPermisosEspeciales(true);
                admin.setTelefono("0000000000");
                admin.setFechaRegistro(java.time.LocalDateTime.now());
                admin = administradorRepository.save(admin);
            } else {
                admin = admins.get(0);
            }
            Torneo t = new Torneo();
            t.setNombre(TORNEO_AMISTOSO_NOMBRE);
            t.setDescripcion("Torneo especial para partidos amistosos");
            t.setFechaInicio(java.time.LocalDate.now().minusYears(10));
            t.setFechaFin(java.time.LocalDate.now().plusYears(10));
            t.setActivo(true);
            t.setAdministrador(admin);
            torneoAmistoso = torneoRepository.save(t);
        } else {
            torneoAmistoso = torneos.get(0);
        }
    }

    // --- Listar partidos ---
    @GetMapping("/partidos")
    public String listarPartidos(Model model) {
        List<Partido> partidos = partidoRepository.findAll();
        model.addAttribute("partidos", partidos);
        return "partidos";
    }

    // --- Formulario nuevo partido ---
    @GetMapping("/partidos/nuevo")
    public String mostrarFormularioNuevoPartido(Model model) {
        model.addAttribute("partido", new Partido());
        model.addAttribute("torneos", torneoRepository.findByActivoTrue());
        model.addAttribute("torneoAmistosoId", torneoAmistoso != null ? torneoAmistoso.getId() : null);
        return "partidos/nuevo";
    }

    // --- Guardar nuevo partido ---
    @PostMapping("/partidos/guardar")
    public String guardarNuevoPartido(
            @Valid @ModelAttribute Partido partido,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "torneoId", required = false) Long torneoId) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("torneos", torneoRepository.findByActivoTrue());
            model.addAttribute("torneoAmistosoId", torneoAmistoso != null ? torneoAmistoso.getId() : null);
            return "partidos/nuevo";
        }
        if ("Amistoso".equalsIgnoreCase(partido.getTipoPartido())) {
            partido.setTorneo(torneoAmistoso);
        } else {
            Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
            partido.setTorneo(torneo);
        }
        partidoRepository.save(partido);
        return "redirect:/partidos";
    }

    // --- Formulario editar partido ---
    @GetMapping("/partidos/editar/{id}")
    public String mostrarFormularioEditarPartido(@PathVariable Long id, Model model) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de partido inválido: " + id));
        model.addAttribute("partido", partido);
        model.addAttribute("torneos", torneoRepository.findByActivoTrue());
        model.addAttribute("torneoAmistosoId", torneoAmistoso != null ? torneoAmistoso.getId() : null);
        return "partidos/editar";
    }

    // --- Actualizar partido ---
    @PostMapping("/partidos/actualizar/{id}")
    public String actualizarPartido(
            @PathVariable Long id,
            @Valid @ModelAttribute Partido partidoActualizado,
            BindingResult bindingResult,
            Model model,
            @RequestParam(value = "torneoId", required = false) Long torneoId) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("partido", partidoActualizado);
            model.addAttribute("torneos", torneoRepository.findByActivoTrue());
            model.addAttribute("torneoAmistosoId", torneoAmistoso != null ? torneoAmistoso.getId() : null);
            return "partidos/editar";
        }
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de partido inválido: " + id));
        partido.setEquipoLocal(partidoActualizado.getEquipoLocal());
        partido.setEquipoVisitante(partidoActualizado.getEquipoVisitante());
        partido.setFechaPartido(partidoActualizado.getFechaPartido());
        partido.setUbicacion(partidoActualizado.getUbicacion());
        partido.setTipoPartido(partidoActualizado.getTipoPartido());
        if ("Amistoso".equalsIgnoreCase(partidoActualizado.getTipoPartido())) {
            partido.setTorneo(torneoAmistoso);
        } else {
            Torneo torneo = torneoRepository.findById(torneoId).orElse(null);
            partido.setTorneo(torneo);
        }
        partidoRepository.save(partido);
        return "redirect:/partidos";
    }

    // --- Confirmar eliminación ---
    @GetMapping("/partidos/eliminar/{id}")
    public String mostrarConfirmacionEliminarPartido(@PathVariable Long id, Model model) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de partido inválido: " + id));
        model.addAttribute("partido", partido);
        return "partidos/eliminar";
    }

    // --- Eliminar partido ---
    @PostMapping("/partidos/eliminar/{id}")
    public String eliminarPartido(@PathVariable Long id) {
        partidoRepository.deleteById(id);
        return "redirect:/partidos";
    }
}
