package com.pagina.Caba.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para el sistema de chat en tiempo real.
 * Usa STOMP (Simple Text Oriented Messaging Protocol) sobre WebSocket.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el message broker.
     * - /topic: Para mensajes de tipo broadcast (público)
     * - /queue: Para mensajes punto a punto (privado)
     * - /app: Prefijo para destinos de aplicación
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un simple message broker en memoria
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para mensajes desde el cliente
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo para mensajes dirigidos a usuarios específicos
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra los endpoints STOMP.
     * Los clientes se conectarán a /ws-chat
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setAllowedOriginPatterns("*")
                .withSockJS(); // Fallback para navegadores sin soporte WebSocket
    }
}