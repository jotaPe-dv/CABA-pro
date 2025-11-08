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
        try {
            List<TarifaDto> tarifas = tarifaService.findAll();
            System.out.println("[DEBUG] Tarifas encontradas: " + (tarifas != null ? tarifas.size() : "null"));
            if (tarifas == null) {
                tarifas = new java.util.ArrayList<>();
            }
            model.addAttribute("tarifas", tarifas);
            return "tarifas";
        } catch (Exception e) {
            System.err.println("[ERROR] Error al cargar tarifas: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("tarifas", new java.util.ArrayList<>());
            model.addAttribute("error", "Error al cargar las tarifas: " + e.getMessage());
            return "tarifas";
        }
    }
}
