package com.pagina.Caba.controller;

import com.pagina.Caba.dto.TarifaDto;
import com.pagina.Caba.service.TarifaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class TarifasWebController {
    @Autowired
    private TarifaService tarifaService;

    @GetMapping("/tarifas")
    public String listarTarifas(Model model) {
        List<TarifaDto> tarifas = tarifaService.findAll();
        model.addAttribute("tarifas", tarifas);
        return "tarifas";
    }
}
