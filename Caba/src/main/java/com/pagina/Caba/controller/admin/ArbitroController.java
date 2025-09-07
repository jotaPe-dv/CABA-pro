package com.pagina.Caba.controller.admin;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/arbitros") // Todas las rutas de este controlador empezarán con /admin/arbitros
public class ArbitroController {

    @Autowired
    private ArbitroService arbitroService;

    // READ: Muestra la lista de todos los árbitros
    @GetMapping
    public String listarArbitros(Model model) {
        model.addAttribute("arbitros", arbitroService.findAll());
        return "admin/arbitro/arbitros"; // Ruta al archivo HTML de la lista
    }

    // CREATE (GET): Muestra el formulario para crear un nuevo árbitro
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("arbitro", new Arbitro());
        return "admin/arbitro/arbitro-form"; // Ruta al archivo HTML del formulario
    }

    // CREATE & UPDATE (POST): Procesa los datos del formulario para guardar
    @PostMapping("/guardar")
    public String guardarArbitro(@ModelAttribute("arbitro") Arbitro arbitro) {
        // En un caso real, la contraseña debería ser encriptada.
        if (arbitro.getPassword() == null || arbitro.getPassword().isEmpty()) {
            arbitro.setPassword("defaultpass123");
        }
        arbitroService.save(arbitro);
        return "redirect:/admin/arbitros";
    }

    // UPDATE (GET): Muestra el formulario para editar un árbitro existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Arbitro arbitro = arbitroService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ID de Árbitro inválido:" + id));
        model.addAttribute("arbitro", arbitro);
        return "admin/arbitro/arbitro-form";
    }

    // DELETE: Elimina un árbitro por su ID
    @GetMapping("/eliminar/{id}")
    public String eliminarArbitro(@PathVariable Long id) {
        arbitroService.deleteById(id);
        return "redirect:/admin/arbitros";
    }
}