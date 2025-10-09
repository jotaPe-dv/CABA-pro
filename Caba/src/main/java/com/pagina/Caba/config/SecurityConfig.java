package com.pagina.Caba.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                // Recursos públicos
                .requestMatchers("/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .requestMatchers("/", "/login", "/register").permitAll()
                
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
                
                // Cualquier otra ruta requiere autenticación
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
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")
            )
            .headers(headers -> headers
                .frameOptions().sameOrigin() // Para H2 Console
            );

        return http.build();
    }
}