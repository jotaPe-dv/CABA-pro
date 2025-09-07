package com.pagina.Caba.service;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.repository.ArbitroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ArbitroService {

    @Autowired
    private ArbitroRepository arbitroRepository;

    public List<Arbitro> findAll() {
        return arbitroRepository.findAll();
    }

    public Optional<Arbitro> findById(Long id) {
        return arbitroRepository.findById(id);
    }

    public Arbitro save(Arbitro arbitro) {
        // La lógica de negocio va aquí. Por ejemplo, antes de guardar:
        // - Podrías encriptar la contraseña.
        // - Validar que el email no esté repetido.
        return arbitroRepository.save(arbitro);
    }

    public void deleteById(Long id) {
        arbitroRepository.deleteById(id);
    }
}