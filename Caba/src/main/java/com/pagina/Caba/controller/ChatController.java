package com.pagina.Caba.controller;

import com.pagina.Caba.dto.MensajeDTO;
import com.pagina.Caba.model.Usuario;
import com.pagina.Caba.service.MensajeService;
import com.pagina.Caba.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador para el sistema de chat en tiempo real.
 * Maneja tanto endpoints REST como mensajes WebSocket.
 */
@Controller
public class ChatController {
    
    @Autowired
    private MensajeService mensajeService;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    // ==================== ENDPOINTS WEB ====================
    
    /**
     * Página principal del chat para Admin
     */
    @GetMapping("/admin/chat")
    public String adminChat(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        model.addAttribute("usuarioActual", usuario);
        model.addAttribute("pageTitle", "Chat - Administrador");
        
        return "admin/chat";
    }
    
    /**
     * Página principal del chat para Árbitro
     */
    @GetMapping("/arbitro/chat")
    public String arbitroChat(Authentication authentication, Model model) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        model.addAttribute("usuarioActual", usuario);
        model.addAttribute("pageTitle", "Chat - Árbitro");
        
        return "arbitro/chat";
    }
    
    // ==================== ENDPOINTS REST API ====================
    
    /**
     * Obtiene la conversación entre dos usuarios
     */
    @GetMapping("/api/chat/conversacion/{otroUsuarioId}")
    @ResponseBody
    public Map<String, Object> obtenerConversacion(
            @PathVariable Long otroUsuarioId,
            Authentication authentication) {
        
        String email = authentication.getName();
        Usuario usuarioActual = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        List<MensajeDTO> mensajes = mensajeService.obtenerConversacion(
            usuarioActual.getId(), otroUsuarioId
        );
        
        // Marcar como leídos
        mensajeService.marcarConversacionComoLeida(usuarioActual.getId(), otroUsuarioId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("mensajes", mensajes);
        response.put("success", true);
        
        return response;
    }
    
    /**
     * Obtiene el contador de mensajes no leídos
     */
    @GetMapping("/api/chat/no-leidos")
    @ResponseBody
    public Map<String, Object> contarNoLeidos(Authentication authentication) {
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        long count = mensajeService.contarMensajesNoLeidos(usuario.getId());
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("success", true);
        
        return response;
    }
    
    /**
     * Obtiene el contador de mensajes no leídos de un usuario específico
     */
    @GetMapping("/api/chat/no-leidos/{otroUsuarioId}")
    @ResponseBody
    public Map<String, Object> contarNoLeidosDeUsuario(
            @PathVariable Long otroUsuarioId,
            Authentication authentication) {
        
        String email = authentication.getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        long count = mensajeService.contarMensajesNoLeidosDeUsuario(
            usuario.getId(), otroUsuarioId
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("count", count);
        response.put("success", true);
        
        return response;
    }
    
    // ==================== WEBSOCKET ENDPOINTS ====================
    
    /**
     * Recibe mensajes del cliente vía WebSocket
     * Ruta: /app/chat.send
     */
    @MessageMapping("/chat.send")
    public void enviarMensaje(@Payload Map<String, Object> payload) {
        try {
            Long remitenteId = Long.parseLong(payload.get("remitenteId").toString());
            Long destinatarioId = Long.parseLong(payload.get("destinatarioId").toString());
            String contenido = payload.get("contenido").toString();
            
            // Guardar en base de datos
            MensajeDTO mensaje = mensajeService.enviarMensaje(remitenteId, destinatarioId, contenido);
            
            // Enviar al destinatario vía WebSocket
            messagingTemplate.convertAndSend(
                "/topic/chat/" + destinatarioId,
                mensaje
            );
            
            // Confirmar al remitente
            messagingTemplate.convertAndSend(
                "/topic/chat/" + remitenteId,
                mensaje
            );
            
            System.out.println("[CHAT] Mensaje enviado de " + remitenteId + " a " + destinatarioId);
            
        } catch (Exception e) {
            System.err.println("[CHAT ERROR] " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Notifica que un usuario está escribiendo
     */
    @MessageMapping("/chat.typing")
    public void notificarEscribiendo(@Payload Map<String, Object> payload) {
        Long usuarioId = Long.parseLong(payload.get("usuarioId").toString());
        Long destinatarioId = Long.parseLong(payload.get("destinatarioId").toString());
        
        messagingTemplate.convertAndSend(
            "/topic/chat/typing/" + destinatarioId,
            Map.of("usuarioId", usuarioId, "escribiendo", true)
        );
    }
}