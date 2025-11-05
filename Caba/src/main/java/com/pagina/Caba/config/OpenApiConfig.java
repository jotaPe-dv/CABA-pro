package com.pagina.Caba.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API REST
 * 
 * Acceder a la documentación en:
 * - Swagger UI: http://localhost:8080/swagger-ui.html
 * - OpenAPI JSON: http://localhost:8080/v3/api-docs
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-03
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI cabaProOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CABA Pro API")
                        .description("""
                                API REST para el sistema de gestión de árbitros de CABA Pro.
                                
                                Esta API permite:
                                - Gestionar árbitros y sus datos
                                - Administrar asignaciones de partidos
                                - Consultar liquidaciones y pagos
                                - Gestionar torneos y partidos
                                - Acceder a estadísticas y reportes
                                
                                **Autenticación:** La mayoría de endpoints requieren autenticación mediante 
                                Spring Security con roles ADMIN o ARBITRO.
                                
                                **Roles:**
                                - ADMIN: Acceso completo a todos los recursos
                                - ARBITRO: Acceso limitado a sus propios datos
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("CABA Pro Support")
                                .email("support@cabapro.com")
                                .url("https://github.com/jotaPe-dv/CABA-pro"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de Desarrollo"),
                        new Server()
                                .url("https://api.cabapro.com")
                                .description("Servidor de Producción")))
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Autenticación HTTP Basic con email y contraseña")))
                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}
