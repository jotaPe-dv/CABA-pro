package com.pagina.Caba;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain; 
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // 1. Permite el acceso a la consola H2
                .requestMatchers(toH2Console()).permitAll()
                
                // 2. Mantén el acceso permitido a tus rutas de admin para pruebas
                .requestMatchers("/admin/**").permitAll() 
                
                // 3. Por ahora, permite todo lo demás para simplificar
                .anyRequest().permitAll()
            );
        
        // 4. Deshabilita la protección CSRF específicamente para la consola H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()));
        
        // 5. Permite que la consola H2 se cargue dentro de un frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}