package com.pagina.Caba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
            .authorizeHttpRequests(authz -> authz
                // ✅ Recursos públicos PRIMERO
                .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .requestMatchers("/", "/login", "/register").permitAll()
                
                // ✅ WebSocket - ANTES de anyRequest()
                .requestMatchers("/ws-chat/**").permitAll()
                
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
                .requestMatchers("/tarifas/**").hasRole("ADMIN")
                .requestMatchers("/liquidaciones/**").hasRole("ADMIN")
                
                // Rutas para ARBITRO
                .requestMatchers("/arbitro/**").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/mis-asignaciones").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/aceptar/**").hasRole("ARBITRO")
                .requestMatchers("/asignaciones/rechazar/**").hasRole("ARBITRO")
                
                // Rutas compartidas
                .requestMatchers("/asignaciones/**").hasAnyRole("ADMIN", "ARBITRO")
                .requestMatchers("/especialidades/**").hasAnyRole("ADMIN", "ARBITRO")
                
                // ⚠️ IMPORTANTE: anyRequest() SIEMPRE AL FINAL
                .anyRequest().authenticated()
            )
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
            .sessionManagement(session -> session
                .maximumSessions(-1) // -1 = sesiones ilimitadas (permite múltiples pestañas)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/login?expired=true")
                .sessionRegistry(sessionRegistry)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/ws-chat/**", "/api/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Para H2 Console
            );

        return http.build();
    }
}