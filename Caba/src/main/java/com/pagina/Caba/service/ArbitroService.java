package com.pagina.Caba.service;

import jakarta.annotation.PostConstruct;

import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.repository.ArbitroRepository;
import com.pagina.Caba.repository.AsignacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ArbitroService {

    @PostConstruct
    public void crearArbitrosPorDefecto() {
        if (arbitroRepository.count() == 0) {
            Arbitro principal = new Arbitro("Juan", "Principal", "principal@caba.com", passwordEncoder.encode("123456"), "LIC001", "3000000001", "Principal", "A", "");
            principal.setDisponible(true);
            principal.setActivo(true);
            arbitroRepository.save(principal);

            Arbitro asistente = new Arbitro("Pedro", "Asistente", "asistente@caba.com", passwordEncoder.encode("123456"), "LIC002", "3000000002", "Auxiliar", "B", "");
            asistente.setDisponible(true);
            asistente.setActivo(true);
            arbitroRepository.save(asistente);

            Arbitro mesa = new Arbitro("Maria", "Mesa", "mesa@caba.com", passwordEncoder.encode("123456"), "LIC003", "3000000003", "Mesa", "C", "");
            mesa.setDisponible(true);
            mesa.setActivo(true);
            arbitroRepository.save(mesa);
        }
    }
    
    @Autowired
    private ArbitroRepository arbitroRepository;
    
    @Autowired
    private AsignacionRepository asignacionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    
    // CRUD Básico
    public List<Arbitro> obtenerTodos() {
        return arbitroRepository.findAll();
    }
    
    public Optional<Arbitro> obtenerPorId(Long id) {
        return arbitroRepository.findById(id);
    }
    
    public Arbitro crear(Arbitro arbitro) {
        validarNumeroLicencia(null, arbitro.getNumeroLicencia());
        validarEmail(null, arbitro.getEmail());

        if (!StringUtils.hasText(arbitro.getPassword())) {
            throw new IllegalArgumentException("La contraseña es obligatoria");
        }

        arbitro.setPassword(passwordEncoder.encode(arbitro.getPassword()));

        if (arbitro.getActivo() == null) {
            arbitro.setActivo(true);
        }

        if (arbitro.getDisponible() == null) {
            arbitro.setDisponible(true);
        }

        return arbitroRepository.save(arbitro);
    }

    public Arbitro actualizar(Long id, Arbitro datosActualizados) {
        Arbitro existente = arbitroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Árbitro no encontrado con id=" + id));

        validarNumeroLicencia(id, datosActualizados.getNumeroLicencia());
        validarEmail(id, datosActualizados.getEmail());

        existente.setNombre(datosActualizados.getNombre());
        existente.setApellido(datosActualizados.getApellido());
        existente.setEmail(datosActualizados.getEmail());
        existente.setNumeroLicencia(datosActualizados.getNumeroLicencia());
        existente.setTelefono(datosActualizados.getTelefono());
        existente.setDireccion(datosActualizados.getDireccion());
        existente.setTarifaBase(datosActualizados.getTarifaBase());
        Boolean disponible = datosActualizados.getDisponible();
        if (disponible != null) {
            existente.setDisponible(disponible);
        }
        existente.setEspecialidad(datosActualizados.getEspecialidad());
        existente.setEscalafon(datosActualizados.getEscalafon());
        existente.setFotoUrl(datosActualizados.getFotoUrl());

        String nuevaPassword = datosActualizados.getPassword();
        if (StringUtils.hasText(nuevaPassword)) {
            if (!nuevaPassword.equals(existente.getPassword()) && !nuevaPassword.startsWith("$2a$")) {
                existente.setPassword(passwordEncoder.encode(nuevaPassword));
            } else {
                existente.setPassword(nuevaPassword);
            }
        }

        return arbitroRepository.save(existente);
    }
    
    public void eliminar(Long id) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(id);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            // Borrar asignaciones relacionadas primero para evitar FK constraint
            try {
                List<Asignacion> asignaciones = asignacionRepository.findByArbitro(arbitro);
                if (asignaciones != null && !asignaciones.isEmpty()) {
                    asignacionRepository.deleteAll(asignaciones);
                }
            } catch (Exception ex) {
                // Log y rethrow para que el llamador pueda manejarlo (y evitar dejar datos en estado inconsistentes)
                System.out.println("⚠️ Error al eliminar asignaciones relacionadas con el árbitro id=" + id + ": " + ex.getMessage());
                throw ex;
            }

            // Finalmente eliminar el árbitro
            arbitroRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Árbitro no encontrado con id=" + id);
        }
    }
    
    // Lógica de Negocio - Obtener árbitros activos
    
    public List<Arbitro> obtenerArbitrosDisponibles() {
        return arbitroRepository.findByDisponibleTrueAndActivoTrue();
    }    public Optional<Arbitro> buscarPorNumeroLicencia(String numeroLicencia) {
        return arbitroRepository.findByNumeroLicencia(numeroLicencia);
    }
    
    public List<Arbitro> buscarPorEspecialidad(Long especialidadId) {
        return List.of(); // Removed logic related to Especialidad
    }
    
    public void activarArbitro(Long arbitroId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            arbitro.activar();
            arbitro.marcarDisponible();
            arbitroRepository.save(arbitro);
        }
    }
    
    public void desactivarArbitro(Long arbitroId) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            arbitro.desactivar();
            arbitro.marcarNoDisponible();
            arbitroRepository.save(arbitro);
        }
    }
    
    public void cambiarDisponibilidad(Long arbitroId, boolean disponible) {
        Optional<Arbitro> arbitroOpt = arbitroRepository.findById(arbitroId);
        if (arbitroOpt.isPresent()) {
            Arbitro arbitro = arbitroOpt.get();
            if (disponible) {
                arbitro.marcarDisponible();
            } else {
                arbitro.marcarNoDisponible();
            }
            arbitroRepository.save(arbitro);
        }
    }
    
    public void asignarEspecialidad(Long arbitroId, Long especialidadId) {
        // Removed method logic related to asignarEspecialidad
    }
    
    public void removerEspecialidad(Long arbitroId, Long especialidadId) {
        // Removed method logic related to removerEspecialidad
    }
    
    // Estadísticas
    public long contarArbitrosActivos() {
        return arbitroRepository.countArbitrosActivos();
    }
    
    public long contarArbitrosDisponibles() {
        return arbitroRepository.countArbitrosDisponibles();
    }
    
    // Validaciones
    private void validarNumeroLicencia(Long arbitroId, String numeroLicencia) {
        if (!StringUtils.hasText(numeroLicencia)) {
            throw new IllegalArgumentException("El número de licencia es obligatorio");
        }

        Optional<Arbitro> existente = arbitroRepository.findByNumeroLicencia(numeroLicencia);
        if (existente.isPresent() && !existente.get().getId().equals(arbitroId)) {
            throw new IllegalArgumentException("Ya existe un árbitro con ese número de licencia");
        }
    }

    private void validarEmail(Long arbitroId, String email) {
        if (!StringUtils.hasText(email)) {
            throw new IllegalArgumentException("El email es obligatorio");
        }

        Optional<Arbitro> existente = arbitroRepository.findByEmail(email);
        if (existente.isPresent() && !existente.get().getId().equals(arbitroId)) {
            throw new IllegalArgumentException("Ya existe un usuario registrado con ese email");
        }
    }
    
    // Búsquedas
    public List<Arbitro> buscarPorTexto(String texto) {
        return arbitroRepository.findAll(); // Simplificado temporalmente
    }
}