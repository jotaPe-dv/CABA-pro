package com.pagina.Caba.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // Retorna el nombre de la vista (index.html)
        return "index";
    }
}
