package com.pagina.Caba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.Arrays;
import java.util.List;

@Controller
public class EspecialidadesWebController {
    @GetMapping("/especialidades")
    public String listarEspecialidades(Model model) {
        // Puedes reemplazar esto por una consulta a la base de datos si tienes la entidad y repositorio
        List<String> especialidades = Arrays.asList("General", "Árbitro", "Mesa de Control");
        model.addAttribute("especialidades", especialidades);
        return "especialidades";
    }
}
