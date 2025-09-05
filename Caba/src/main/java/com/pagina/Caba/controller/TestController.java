// TestController.java - Para probar que todo funciona
package com.pagina.Caba.controller;

import com.pagina.Caba.Usuario;
import com.pagina.Caba.Arbitro;
import com.pagina.Caba.Administrador;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "¡Hola! La aplicación Spring Boot está funcionando correctamente. " +
               "Fecha: " + LocalDateTime.now();
    }
    
    @GetMapping("/test-arbitro")
    public Arbitro testArbitro() {
        // Crear un árbitro de prueba
        Arbitro arbitro = new Arbitro(
            "Juan Pérez", 
            "juan@email.com", 
            "password123", 
            "123456789",
            "Principal",
            "Primera División"
        );
        return arbitro;
    }
    
    @GetMapping("/test-admin")
    public Administrador testAdmin() {
        // Crear un administrador de prueba
        Administrador admin = new Administrador(
            "María González",
            "maria@admin.com",
            "admin123",
            "987654321",
            "SUPER_ADMIN",
            "Gestión Deportiva"
        );
        return admin;
    }
    
    @GetMapping("/test-usuarios")
    public List<Usuario> testUsuarios() {
        Usuario usuario1 = new Usuario("Carlos López", "carlos@email.com", "pass1", "111111");
        Arbitro arbitro1 = new Arbitro("Ana Torres", "ana@email.com", "pass2", "222222", "Asistente", "Segunda División");
        Administrador admin1 = new Administrador("Pedro Silva", "pedro@admin.com", "pass3", "333333", "ADMIN", "Tecnología");
        
        return Arrays.asList(usuario1, arbitro1, admin1);
    }
}