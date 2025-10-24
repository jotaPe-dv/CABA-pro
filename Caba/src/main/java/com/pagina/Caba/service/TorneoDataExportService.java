package com.pagina.Caba.service;

import com.pagina.Caba.model.*;
import com.pagina.Caba.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Servicio para exportar partidos simulados a formato JSONL para entrenamiento de modelos de IA.
 */
@Service
@Transactional(readOnly = true)
public class TorneoDataExportService {

    @Autowired
    private PartidoRepository partidoRepository;

    @Autowired
    private AsignacionRepository asignacionRepository;

    private final ObjectMapper objectMapper;

    public TorneoDataExportService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    /**
     * Exporta todos los partidos completados a un archivo JSONL.
     * Cada línea es un JSON con la estructura requerida para entrenamiento.
     */
    public String exportarPartidosCompletados(String outputPath) throws IOException {
        List<Partido> completados = partidoRepository.findAll().stream()
                .filter(p -> p.getCompletado() != null && p.getCompletado())
                .collect(Collectors.toList());

        File file = new File(outputPath);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            for (Partido partido : completados) {
                Map<String, Object> record = crearRegistroPartido(partido);
                writer.write(objectMapper.writeValueAsString(record) + "\n");
            }
        }

        return "Exportados " + completados.size() + " partidos a " + outputPath;
    }

    /**
     * Exporta partidos de un torneo específico.
     */
    public String exportarPartidosDeTorneo(Long torneoId, String outputPath) throws IOException {
        List<Partido> partidos = partidoRepository.findAll().stream()
                .filter(p -> p.getTorneo() != null && p.getTorneo().getId().equals(torneoId))
                .filter(p -> p.getCompletado() != null && p.getCompletado())
                .collect(Collectors.toList());

        File file = new File(outputPath);
        file.getParentFile().mkdirs();

        try (FileWriter writer = new FileWriter(file)) {
            for (Partido partido : partidos) {
                Map<String, Object> record = crearRegistroPartido(partido);
                writer.write(objectMapper.writeValueAsString(record) + "\n");
            }
        }

        return "Exportados " + partidos.size() + " partidos del torneo " + torneoId + " a " + outputPath;
    }

    private Map<String, Object> crearRegistroPartido(Partido partido) {
        Map<String, Object> record = new LinkedHashMap<>();
        
        record.put("torneoId", partido.getTorneo() != null ? partido.getTorneo().getId() : null);
        record.put("partidoId", partido.getId());
        record.put("fechaPartido", partido.getFechaPartido() != null ? partido.getFechaPartido().toString() : null);
        record.put("equipoLocal", partido.getEquipoLocal());
        record.put("equipoVisitante", partido.getEquipoVisitante());
        record.put("marcadorLocal", partido.getMarcadorLocal());
        record.put("marcadorVisitante", partido.getMarcadorVisitante());
        
        String ganador = partido.getMarcadorLocal() != null && partido.getMarcadorVisitante() != null
                ? (partido.getMarcadorLocal() > partido.getMarcadorVisitante() 
                    ? partido.getEquipoLocal() 
                    : partido.getEquipoVisitante())
                : null;
        record.put("ganador", ganador);
        
        record.put("fase", partido.getTipoPartido());
        record.put("ubicacion", partido.getUbicacion());
        record.put("simulado", true);

        // Asignaciones
        List<Asignacion> asignaciones = asignacionRepository.findByPartido(partido);
        List<Map<String, Object>> asignacionesList = new ArrayList<>();
        for (Asignacion a : asignaciones) {
            Map<String, Object> asigMap = new LinkedHashMap<>();
            asigMap.put("arbitroEmail", a.getArbitro() != null ? a.getArbitro().getEmail() : null);
            asigMap.put("rol", a.getRolEspecifico());
            asigMap.put("estado", a.getEstado() != null ? a.getEstado().name() : null);
            asigMap.put("monto", a.getMontoCalculado() != null ? a.getMontoCalculado().doubleValue() : null);
            asignacionesList.add(asigMap);
        }
        record.put("asignaciones", asignacionesList);

        // Metadata adicional
        Map<String, Object> metadatos = new LinkedHashMap<>();
        metadatos.put("observaciones", partido.getObservaciones());
        metadatos.put("fechaCreacion", partido.getFechaCreacion() != null ? partido.getFechaCreacion().toString() : null);
        record.put("metadatos", metadatos);

        return record;
    }

    /**
     * Retorna estadísticas de partidos disponibles para exportación.
     */
    public Map<String, Object> obtenerEstadisticasExportacion() {
        List<Partido> todos = partidoRepository.findAll();
        long completados = todos.stream().filter(p -> p.getCompletado() != null && p.getCompletado()).count();
        long pendientes = todos.size() - completados;

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalPartidos", todos.size());
        stats.put("partidosCompletados", completados);
        stats.put("partidosPendientes", pendientes);
        
        // Agrupar por torneo
        Map<Long, Long> porTorneo = todos.stream()
                .filter(p -> p.getTorneo() != null && p.getCompletado() != null && p.getCompletado())
                .collect(Collectors.groupingBy(p -> p.getTorneo().getId(), Collectors.counting()));
        stats.put("completadosPorTorneo", porTorneo);

        return stats;
    }
}
