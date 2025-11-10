package com.pagina.Caba.config;

import com.pagina.Caba.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:4200,http://localhost:5173}")
    private String allowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, SessionRegistry sessionRegistry) throws Exception {
        http
            // ✅ CORS: Habilitar CORS para Node.js
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            .authorizeHttpRequests(authz -> authz
                // ✅ Recursos públicos PRIMERO
                .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .requestMatchers("/", "/login", "/register").permitAll()
                
                // ✅ API REST JWT - ENDPOINTS PÚBLICOS
                .requestMatchers("/api/auth/**").permitAll()
                
                // ✅ API pública del clima - SIN AUTENTICACIÓN
                .requestMatchers("/api/v1/clima", "/api/v1/clima/**").permitAll()
                
                // ✅ WebSocket - ANTES de anyRequest()
                .requestMatchers("/ws-chat/**").permitAll()
                
                // ✅ API REST - REQUIEREN JWT (stateless)
                .requestMatchers("/api/arbitro/**").hasRole("ARBITRO")
                .requestMatchers("/api/v1/**").authenticated()
                
                // ✅ API del Chat - ANTES de anyRequest()
                .requestMatchers("/api/chat/**").hasAnyRole("ADMIN", "ARBITRO")
                .requestMatchers("/api/arbitros/disponibles").hasRole("ADMIN")
                .requestMatchers("/api/administradores/principal").hasRole("ARBITRO")
                
                // ✅ Páginas de Chat - ANTES de anyRequest()
                .requestMatchers("/admin/chat").hasRole("ADMIN")
                .requestMatchers("/arbitro/chat").hasRole("ARBITRO")
                
                // Rutas para ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/administradores/**").hasRole("ADMIN")
                .requestMatchers("/torneos/**").hasRole("ADMIN")
                .requestMatchers("/partidos/**").hasRole("ADMIN")
                .requestMatchers("/liquidaciones/**").hasRole("ADMIN")
                
                // Rutas para ARBITRO
                .requestMatchers("/arbitro/**").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/mis-asignaciones").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/aceptar/**").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/rechazar/**").hasRole("ARBITRO")
                
                // Rutas compartidas
                .requestMatchers("/asignaciones/**").hasAnyRole("ADMIN", "ARBITRO")
                .requestMatchers("/especialidades/**").hasAnyRole("ADMIN", "ARBITRO")
                .requestMatchers("/tarifas").hasAnyRole("ADMIN", "ARBITRO")
                
                // ⚠️ IMPORTANTE: anyRequest() SIEMPRE AL FINAL
                .anyRequest().authenticated()
            )
            
            // ✅ JWT Filter: Agregar ANTES de UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            
            // ✅ Session Management: Mantener sesiones para web, pero JWT es stateless
            .sessionManagement(session -> session
                .maximumSessions(-1) // -1 = sesiones ilimitadas (permite múltiples pestañas)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?expired=true")
                .sessionRegistry(sessionRegistry)
            )
            
            // ✅ CSRF: Deshabilitado para API REST (JWT no necesita CSRF)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/ws-chat/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin()) // Para H2 Console
            );

        return http.build();
    }

    /**
     * Configuración CORS para permitir que Node.js/React/Angular consuman la API
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // ✅ Orígenes permitidos desde application.properties
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        
        // ✅ Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // ✅ Headers permitidos (incluyendo Authorization para JWT)
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // ✅ Permitir credenciales (cookies, Authorization header)
        configuration.setAllowCredentials(true);
        
        // ✅ Cache de preflight OPTIONS por 1 hora
        configuration.setMaxAge(3600L);
        
        // ✅ Aplicar CORS solo a rutas /api/**
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}