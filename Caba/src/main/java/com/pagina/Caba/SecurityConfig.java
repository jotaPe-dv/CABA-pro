package com.pagina.Caba;

import com.pagina.Caba.config.CustomAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain; 
import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
    // NoOp para entorno local/pruebas: permite contraseñas en texto plano.
    // NO usar en producción.
    return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                // Permite el acceso a la consola H2
                .requestMatchers(toH2Console()).permitAll()
                
                // Permite acceso público a página principal y login
                .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                
                // Protege rutas de admin
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // Protege rutas de arbitro
                .requestMatchers("/arbitro/**").hasRole("ARBITRO")
                
                // Cualquier otra petición requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(customAuthenticationSuccessHandler)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        
        // Deshabilita la protección CSRF específicamente para la consola H2
        http.csrf(csrf -> csrf.ignoringRequestMatchers(toH2Console()));
        
        // Permite que la consola H2 se cargue dentro de un frame
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }
}