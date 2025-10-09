package com.pagina.Caba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArbitroLiquidacionesController {
    @GetMapping("/arbitro/liquidaciones")
    public String liquidaciones(Model model) {
        // Aquí puedes agregar atributos al modelo si lo necesitas
        return "arbitro/liquidaciones";
    }
}
