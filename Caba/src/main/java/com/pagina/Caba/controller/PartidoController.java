package com.pagina.Caba.controller;

import com.pagina.Caba.model.Partido;
import com.pagina.Caba.service.PartidoService;
import com.pagina.Caba.service.AsignacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/partidos")
@CrossOrigin(origins = "*")
public class PartidoController {
    @Autowired
    private AsignacionService asignacionService;
    // Obtener árbitros relacionados a un partido
    @GetMapping("/{id}/arbitros")
    public ResponseEntity<List<String>> getArbitrosByPartido(@PathVariable Long id) {
        var asignaciones = asignacionService.findByPartidoId(id);
        // Devuelve una lista de nombres de árbitros y su rol
        List<String> arbitros = asignaciones.stream()
            .map(a -> a.getArbitro().getNombre() + " (" + a.getRolEspecifico() + ")")
            .toList();
        return ResponseEntity.ok(arbitros);
    }

    @Autowired
    private PartidoService partidoService;

    // CREATE
    @PostMapping
    public ResponseEntity<Partido> createPartido(@RequestBody Partido partido) {
        try {
            Partido nuevoPartido = partidoService.save(partido);
            return new ResponseEntity<>(nuevoPartido, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Partido>> getAllPartidos() {
        try {
            List<Partido> partidos = partidoService.findAll();
            if (partidos.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(partidos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Partido> getPartidoById(@PathVariable Long id) {
        Optional<Partido> partidoData = partidoService.findById(id);
        return partidoData.map(partido -> new ResponseEntity<>(partido, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePartido(@PathVariable Long id) {
        try {
            partidoService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
