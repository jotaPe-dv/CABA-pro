// TestController.java - Para probar que todo funciona
package com.pagina.Caba.controller;

import com.pagina.Caba.model.Usuario;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Partido;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.EstadoAsignacion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "✅ La aplicación Spring Boot está funcionando correctamente. " +
               "Fecha: " + LocalDateTime.now();
    }

    @GetMapping("/test-arbitro")
    public Arbitro testArbitro() {
        return new Arbitro(
            "Juan Pérez",
            "juan@email.com",
            "password123",
            "123456789",
            "Principal",
            "Primera División"
        );
    }

    @GetMapping("/test-admin")
    public Administrador testAdmin() {
        return new Administrador(
            "María González",
            "maria@admin.com",
            "admin123",
            "987654321",
            "SUPER_ADMIN",
            "Gestión Deportiva"
        );
    }

    @GetMapping("/test-usuarios")
    public List<Usuario> testUsuarios() {
        Usuario usuario1 = new Usuario("Carlos López", "carlos@email.com", "pass1", "111111");
        Arbitro arbitro1 = new Arbitro("Ana Torres", "ana@email.com", "pass2", "222222", "Asistente", "Segunda División");
        Administrador admin1 = new Administrador("Pedro Silva", "pedro@admin.com", "pass3", "333333", "ADMIN", "Tecnología");

        return Arrays.asList(usuario1, arbitro1, admin1);
    }

    @GetMapping("/test-partido")
    public Partido testPartido() {
        return new Partido(LocalDateTime.now().plusDays(1), "Estadio Central", "Equipo A", "Equipo B");
    }

    @GetMapping("/test-estado")
    public EstadoAsignacion testEstado() {
        return EstadoAsignacion.PENDIENTE;
    }

    @GetMapping("/test-asignacion")
    public Asignacion testAsignacion() {
        return new Asignacion("Árbitro de prueba", 50000.0f, EstadoAsignacion.ACEPTADA);
    }

    @GetMapping("/test-lista")
    public List<Object> testLista() {
        Partido partido = new Partido(LocalDateTime.now().plusDays(1), "Estadio Central", "Equipo A", "Equipo B");
        EstadoAsignacion estado = EstadoAsignacion.PENDIENTE;
        Asignacion asignacion = new Asignacion("Árbitro Demo", 35000.0f, estado);

        return Arrays.asList(partido, estado, asignacion);
    }
}
