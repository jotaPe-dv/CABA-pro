package com.pagina.Caba.controller;

import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Usuario;
import com.pagina.Caba.repository.AdministradorRepository;
import com.pagina.Caba.repository.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * API REST adicional para el chat
 */
@RestController
@RequestMapping("/api")
public class ChatApiController {
    
    @Autowired
    private ArbitroRepository arbitroRepository;
    
    @Autowired
    private AdministradorRepository administradorRepository;
    
    /**
     * Obtiene todos los árbitros activos (para que Admin pueda chatear)
     */
    @GetMapping("/arbitros/disponibles")
    public Map<String, Object> obtenerArbitrosDisponibles() {
        List<Arbitro> arbitros = arbitroRepository.findByDisponibleTrueAndActivoTrue();
        
        List<Map<String, Object>> arbitrosDTO = arbitros.stream().map(a -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", a.getId());
            dto.put("nombre", a.getNombre());
            dto.put("apellido", a.getApellido());
            dto.put("nombreCompleto", a.getNombreCompleto());
            dto.put("email", a.getEmail());
            dto.put("especialidad", a.getEspecialidad());
            dto.put("escalafon", a.getEscalafon());
            return dto;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("arbitros", arbitrosDTO);
        
        return response;
    }
    
    /**
     * Obtiene el administrador principal (para que Árbitro pueda chatear)
     */
    @GetMapping("/administradores/principal")
    public Map<String, Object> obtenerAdminPrincipal() {
        // Buscar el primer administrador activo
        List<Administrador> admins = administradorRepository.findByActivoTrue();
        
        Map<String, Object> response = new HashMap<>();
        
        if (!admins.isEmpty()) {
            Administrador admin = admins.get(0);
            
            Map<String, Object> adminDTO = new HashMap<>();
            adminDTO.put("id", admin.getId());
            adminDTO.put("nombre", admin.getNombre());
            adminDTO.put("apellido", admin.getApellido());
            adminDTO.put("nombreCompleto", admin.getNombreCompleto());
            adminDTO.put("email", admin.getEmail());
            adminDTO.put("cargo", admin.getCargo());
            
            response.put("success", true);
            response.put("administrador", adminDTO);
        } else {
            response.put("success", false);
            response.put("message", "No hay administradores disponibles");
        }
        
        return response;
    }
    
    /**
     * Obtiene todos los usuarios disponibles para chatear
     */
    @GetMapping("/chat/usuarios-disponibles")
    public Map<String, Object> obtenerUsuariosDisponibles() {
        List<Arbitro> arbitros = arbitroRepository.findByDisponibleTrueAndActivoTrue();
        
        List<Map<String, Object>> usuarios = arbitros.stream().map(a -> {
            Map<String, Object> dto = new HashMap<>();
            dto.put("id", a.getId());
            dto.put("nombreCompleto", a.getNombreCompleto());
            dto.put("tipo", "ARBITRO");
            dto.put("especialidad", a.getEspecialidad());
            return dto;
        }).collect(Collectors.toList());
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("usuarios", usuarios);
        
        return response;
    }
}
