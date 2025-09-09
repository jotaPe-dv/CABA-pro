package com.pagina.Caba.controller;

import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.repository.PartidoRepository;
import com.pagina.Caba.repository.ArbitroRepository;
import com.pagina.Caba.repository.LiquidacionRepository;
import com.pagina.Caba.model.EstadoLiquidacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminViewController {
    @Autowired
    private TorneoRepository torneoRepository;
    @Autowired
    private PartidoRepository partidoRepository;
    @Autowired
    private ArbitroRepository arbitroRepository;
    @Autowired
    private LiquidacionRepository liquidacionRepository;
    @GetMapping("/partidos")
    public String partidos() {
        return "admin/partidos";
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        long torneosActivos = torneoRepository.count();
        long partidosHoy = partidoRepository.count();
        long arbitrosActivos = arbitroRepository.count();
        long liquidacionesPendientes = liquidacionRepository.findByEstado(EstadoLiquidacion.PENDIENTE).size();
        model.addAttribute("torneosActivos", torneosActivos);
        model.addAttribute("partidosHoy", partidosHoy);
        model.addAttribute("arbitrosActivos", arbitrosActivos);
        model.addAttribute("liquidacionesPendientes", liquidacionesPendientes);
        return "admin/dashboard";
    }
    
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
