package com.pagina.Caba.controller;

import com.pagina.Caba.dto.LiquidacionDto;
import com.pagina.Caba.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class LiquidacionesWebController {
    @Autowired
    private LiquidacionService liquidacionService;

    @GetMapping("/liquidaciones")
    public String listarLiquidaciones(Model model) {
        List<LiquidacionDto> liquidaciones = liquidacionService.findAll();
        model.addAttribute("liquidaciones", liquidaciones);
        return "liquidaciones";
    }
}
