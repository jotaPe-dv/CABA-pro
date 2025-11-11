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
        
        // DEBUG: Mostrar tipo de clase
        System.out.println("🔍 DEBUG: Usuario encontrado - Email: " + email);
        System.out.println("🔍 DEBUG: Clase del usuario: " + usuario.getClass().getName());
        System.out.println("🔍 DEBUG: Es Administrador? " + (usuario instanceof Administrador));
        System.out.println("🔍 DEBUG: Es Arbitro? " + (usuario instanceof Arbitro));
        
        // Determinar rol basado en el tipo de usuario
        if (usuario instanceof Administrador) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            System.out.println("✅ DEBUG: Asignado rol ROLE_ADMIN");
        } else if (usuario instanceof Arbitro) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ARBITRO"));
            System.out.println("✅ DEBUG: Asignado rol ROLE_ARBITRO");
        } else {
            System.out.println("⚠️ DEBUG: NO se asignó ningún rol!");
        }

        // Verificar que el usuario esté activo (no confundir con disponible)
        if (!usuario.getActivo()) {
            throw new UsernameNotFoundException("Cuenta de usuario inactiva");
        }

        return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getPassword())
            .authorities(authorities)
            .build();
    }
}