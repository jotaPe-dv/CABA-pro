package com.pagina.Caba.service;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.repository.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UsuarioArbitroLookupService {
    @Autowired
    private ArbitroRepository arbitroRepository;

    /**
     * Busca un árbitro por email (usando el campo de Usuario)
     */
    public Optional<Arbitro> buscarPorEmail(String email) {
        return arbitroRepository.findAll().stream()
                .filter(a -> a.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
