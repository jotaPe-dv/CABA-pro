package com.pagina.Caba.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                      HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        String redirectURL = request.getContextPath();
        
        if (hasRole(authentication, "ROLE_ADMIN")) {
            redirectURL = "/admin/dashboard";
        } else if (hasRole(authentication, "ROLE_ARBITRO")) {
            redirectURL = "/arbitro/dashboard";
        }
        
        response.sendRedirect(redirectURL);
    }
    
    private boolean hasRole(Authentication auth, String role) {
        return auth.getAuthorities().stream()
            .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(role));
    }
}
