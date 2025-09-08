package com.pagina.Caba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    
    @GetMapping("/torneos")
    public String torneos() {
        return "admin/torneos";
    }
    
    @GetMapping("/tarifas")
    public String tarifas() {
        return "admin/tarifas";
    }
    
    @GetMapping("/liquidaciones")
    public String liquidaciones() {
        return "admin/liquidaciones";
    }
}
