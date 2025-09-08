package com.pagina.Caba.controller;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/partidos")
public class PartidoController {

    @Autowired
    private PartidoRepository partidoRepository;

    // 🔹 1. Listar partidos 
    @GetMapping("/listar")
    public String listarPartidos(Model model) {
        List<Partido> partidos = partidoRepository.findAll();
        model.addAttribute("partidos", partidos);
        return "partidos/listar"; // nombre del template Thymeleaf
    }

    // 🔹 2. Mostrar 
    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("partido", new Partido());
        return "partidos/crear"; // template con formulario
    }

    // 🔹 3. Guardar partido 
    @PostMapping("/guardar")
    public String guardarPartido(@ModelAttribute Partido partido, Model model) {
        partidoRepository.save(partido);
        model.addAttribute("mensaje", "Partido creado satisfactoriamente");
        return "redirect:/partidos/listar"; // redirige al listado
    }

    // 🔹 4. Ver detalles de un partido 
    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Partido partido = partidoRepository.findById(id).orElse(null);
        if (partido == null) {
            model.addAttribute("error", "Partido no encontrado");
            return "redirect:/partidos/listar";
        }
        model.addAttribute("partido", partido);
        return "partidos/ver";
    }

    // 🔹 5. Eliminar partido 
    @GetMapping("/eliminar/{id}")
    public String eliminarPartido(@PathVariable Long id) {
        partidoRepository.deleteById(id);
        return "redirect:/partidos/listar";
    }
}
