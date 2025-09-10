package com.pagina.Caba.controller;

import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.model.UserPrincipal;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/arbitro")
public class ArbitroViewController {
    
    @Autowired
    private AsignacionService asignacionService;
    
    // ...existing code...
}
