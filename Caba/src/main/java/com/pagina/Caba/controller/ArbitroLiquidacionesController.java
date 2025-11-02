package com.pagina.Caba.controller;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.service.ArbitroService;
import com.pagina.Caba.service.LiquidacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ArbitroLiquidacionesController {
    
    @Autowired
    private LiquidacionService liquidacionService;
    
    @Autowired
    private ArbitroService arbitroService;
    
    @GetMapping("/arbitro/liquidaciones")
    public String liquidaciones(Model model, Authentication authentication) {
        String email = authentication.getName();
        Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        
        // Obtener liquidaciones del árbitro
        List<Liquidacion> liquidaciones = liquidacionService.obtenerLiquidacionesPorArbitro(arbitro.getId());
        
        // Calcular totales
        BigDecimal totalPendiente = liquidaciones.stream()
            .filter(l -> l.getEstado().name().equals("PENDIENTE") || l.getEstado().name().equals("APROBADA"))
            .map(Liquidacion::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalPagado = liquidaciones.stream()
            .filter(l -> l.getEstado().name().equals("PAGADA"))
            .map(Liquidacion::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        model.addAttribute("liquidaciones", liquidaciones);
        model.addAttribute("arbitro", arbitro);
        model.addAttribute("totalPendiente", totalPendiente);
        model.addAttribute("totalPagado", totalPagado);
        
        return "arbitro/liquidaciones";
    }
    
    @GetMapping("/arbitro/liquidaciones/{id}")
    public String verDetalleLiquidacion(@PathVariable Long id, Model model, Authentication authentication) {
        String email = authentication.getName();
        Arbitro arbitro = arbitroService.obtenerTodos().stream()
            .filter(a -> a.getEmail().equalsIgnoreCase(email))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Árbitro no encontrado"));
        
        Liquidacion liquidacion = liquidacionService.obtenerPorId(id)
            .orElseThrow(() -> new RuntimeException("Liquidación no encontrada"));
        
        // Verificar que la liquidación pertenece al árbitro
        if (!liquidacion.getAsignacion().getArbitro().getId().equals(arbitro.getId())) {
            throw new RuntimeException("No tienes permiso para ver esta liquidación");
        }
        
        model.addAttribute("liquidacion", liquidacion);
        model.addAttribute("arbitro", arbitro);
        
        return "arbitro/liquidacion-detalle";
    }
}
