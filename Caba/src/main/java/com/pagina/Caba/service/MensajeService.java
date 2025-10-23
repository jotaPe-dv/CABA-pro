package com.pagina.Caba.service;

import com.pagina.Caba.dto.MensajeDTO;
import com.pagina.Caba.model.Mensaje;
import com.pagina.Caba.model.Usuario;
import com.pagina.Caba.repository.MensajeRepository;
import com.pagina.Caba.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class MensajeService {
    
    @Autowired
    private MensajeRepository mensajeRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    /**
     * Envía un mensaje de un usuario a otro
     */
    public MensajeDTO enviarMensaje(Long remitenteId, Long destinatarioId, String contenido) {
        Usuario remitente = usuarioRepository.findById(remitenteId)
            .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado"));
        
        Usuario destinatario = usuarioRepository.findById(destinatarioId)
            .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));
        
        if (contenido == null || contenido.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }
        
        Mensaje mensaje = new Mensaje(remitente, destinatario, contenido.trim());
        mensaje = mensajeRepository.save(mensaje);
        
        return convertirADTO(mensaje);
    }
    
    /**
     * Obtiene la conversación completa entre dos usuarios
     */
    public List<MensajeDTO> obtenerConversacion(Long usuario1Id, Long usuario2Id) {
        Usuario usuario1 = usuarioRepository.findById(usuario1Id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario 1 no encontrado"));
        
        Usuario usuario2 = usuarioRepository.findById(usuario2Id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario 2 no encontrado"));
        
        List<Mensaje> mensajes = mensajeRepository.findConversacionEntreUsuarios(usuario1, usuario2);
        
        return mensajes.stream()
            .map(this::convertirADTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Cuenta los mensajes no leídos para un usuario
     */
    public long contarMensajesNoLeidos(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        return mensajeRepository.countMensajesNoLeidos(usuario);
    }
    
    /**
     * Cuenta los mensajes no leídos de un remitente específico
     */
    public long contarMensajesNoLeidosDeUsuario(Long destinatarioId, Long remitenteId) {
        Usuario destinatario = usuarioRepository.findById(destinatarioId)
            .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));
        
        Usuario remitente = usuarioRepository.findById(remitenteId)
            .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado"));
        
        return mensajeRepository.countMensajesNoLeidosDeUsuario(destinatario, remitente);
    }
    
    /**
     * Marca todos los mensajes de una conversación como leídos
     */
    public void marcarConversacionComoLeida(Long destinatarioId, Long remitenteId) {
        Usuario destinatario = usuarioRepository.findById(destinatarioId)
            .orElseThrow(() -> new IllegalArgumentException("Destinatario no encontrado"));
        
        Usuario remitente = usuarioRepository.findById(remitenteId)
            .orElseThrow(() -> new IllegalArgumentException("Remitente no encontrado"));
        
        List<Mensaje> mensajes = mensajeRepository.findConversacionEntreUsuarios(destinatario, remitente);
        
        mensajes.stream()
            .filter(m -> m.getDestinatario().getId().equals(destinatarioId) && !m.getLeido())
            .forEach(m -> {
                m.marcarComoLeido();
                mensajeRepository.save(m);
            });
    }
    
    /**
     * Obtiene todos los usuarios con los que el usuario ha chateado
     */
    public List<Usuario> obtenerUsuariosConConversacion(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        return mensajeRepository.findUsuariosConConversacion(usuario);
    }
    
    /**
     * Convierte una entidad Mensaje a DTO
     */
    private MensajeDTO convertirADTO(Mensaje mensaje) {
        return new MensajeDTO(
            mensaje.getId(),
            mensaje.getRemitente().getId(),
            mensaje.getRemitente().getNombreCompleto(),
            mensaje.getTipoRemitente(),
            mensaje.getDestinatario().getId(),
            mensaje.getDestinatario().getNombreCompleto(),
            mensaje.getTipoDestinatario(),
            mensaje.getContenido(),
            mensaje.getFechaEnvio(),
            mensaje.getLeido()
        );
    }
}