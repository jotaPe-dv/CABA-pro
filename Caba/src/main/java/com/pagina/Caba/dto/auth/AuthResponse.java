package com.pagina.Caba.dto.auth;

import com.pagina.Caba.dto.ArbitroDto;

/**
 * DTO para respuesta de autenticación exitosa
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-10
 */
public class AuthResponse {
    
    private String token;
    private String tipo;
    private ArbitroDto arbitro;
    private String message;
    
    public AuthResponse() {}
    
    public AuthResponse(String token, String tipo, ArbitroDto arbitro, String message) {
        this.token = token;
        this.tipo = tipo;
        this.arbitro = arbitro;
        this.message = message;
    }
    
    /**
     * Crea una respuesta exitosa con token
     */
    public static AuthResponse success(String token, ArbitroDto arbitro) {
        return new AuthResponse(token, "Bearer", arbitro, "Autenticación exitosa");
    }
    
    /**
     * Crea una respuesta de error
     */
    public static AuthResponse error(String message) {
        return new AuthResponse(null, null, null, message);
    }
    
    // Getters y Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    public ArbitroDto getArbitro() {
        return arbitro;
    }
    
    public void setArbitro(ArbitroDto arbitro) {
        this.arbitro = arbitro;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
}
