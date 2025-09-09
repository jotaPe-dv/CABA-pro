package com.pagina.Caba.service;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PartidoService {
    @Autowired
    private PartidoRepository partidoRepository;

    public Partido save(Partido partido) {
        return partidoRepository.save(partido);
    }

    public List<Partido> findAll() {
        return partidoRepository.findAll();
    }

    public Optional<Partido> findById(Long id) {
        return partidoRepository.findById(id);
    }

    public void deleteById(Long id) {
        partidoRepository.deleteById(id);
    }
}
