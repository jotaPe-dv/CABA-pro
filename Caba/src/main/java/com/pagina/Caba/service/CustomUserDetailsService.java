package com.pagina.Caba.service;

import com.pagina.Caba.model.Usuario;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        
        // Determinar rol basado en el tipo de usuario
        if (usuario instanceof Administrador) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else if (usuario instanceof Arbitro) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ARBITRO"));
            
            // Verificar que el árbitro esté disponible
            Arbitro arbitro = (Arbitro) usuario;
            if (!arbitro.getDisponible()) {
                throw new UsernameNotFoundException("Cuenta de árbitro no disponible");
            }
        }

        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .authorities(authorities)
            .build();
    }
}