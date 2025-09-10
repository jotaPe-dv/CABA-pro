
package com.pagina.Caba.controller;

import com.pagina.Caba.model.Liquidacion;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.service.LiquidacionService;
import com.pagina.Caba.service.ArbitroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/liquidaciones")
public class LiquidacionController {

    @Autowired
    private LiquidacionService liquidacionService;

    @Autowired
    private ArbitroService arbitroService;

    // Mostrar lista de liquidaciones
    @GetMapping
    public String listarLiquidaciones(Model model) {
        List<Liquidacion> liquidaciones = liquidacionService.findAll();
        model.addAttribute("liquidaciones", liquidaciones);
        return "admin/liquidaciones";
    }

    // Mostrar formulario de nueva liquidación
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("liquidacion", new Liquidacion());
        List<Arbitro> arbitros = arbitroService.findAll();
        model.addAttribute("arbitros", arbitros);
        return "admin/liquidacion-form";
    }

    // Guardar nueva liquidación
    @PostMapping("/guardar")
    public String guardarLiquidacion(@ModelAttribute("liquidacion") Liquidacion liquidacion) {
        liquidacionService.save(liquidacion);
        return "redirect:/admin/liquidaciones";
    }

    // Mostrar formulario de edición
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model) {
        Liquidacion liquidacion = liquidacionService.findById(id).orElseThrow(() -> new IllegalArgumentException("ID de liquidación inválido: " + id));
        model.addAttribute("liquidacion", liquidacion);
        List<Arbitro> arbitros = arbitroService.findAll();
        model.addAttribute("arbitros", arbitros);
        return "admin/liquidacion-form";
    }

    // Actualizar liquidación
    @PostMapping("/actualizar/{id}")
    public String actualizarLiquidacion(@PathVariable Long id, @ModelAttribute("liquidacion") Liquidacion liquidacion) {
        liquidacion.setId(id);
        liquidacionService.save(liquidacion);
        return "redirect:/admin/liquidaciones";
    }

    // Eliminar liquidación
    @GetMapping("/eliminar/{id}")
    public String eliminarLiquidacion(@PathVariable Long id) {
        liquidacionService.deleteById(id);
        return "redirect:/admin/liquidaciones";
    }

    // Liquidar liquidación (cambiar estado a PAGADA)
    @PostMapping("/liquidar/{id}")
    public String liquidarLiquidacion(@PathVariable Long id) {
        var liquidacionOpt = liquidacionService.findById(id);
        if (liquidacionOpt.isPresent()) {
            var liquidacion = liquidacionOpt.get();
            liquidacion.setEstado(com.pagina.Caba.model.EstadoLiquidacion.PAGADA);
            liquidacionService.save(liquidacion);
        }
        return "redirect:/admin/liquidaciones";
    }
}
