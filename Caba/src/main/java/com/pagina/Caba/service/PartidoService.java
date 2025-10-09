package com.pagina.Caba.service;

import com.pagina.Caba.repository.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class PartidoService {
    @Autowired
    private PartidoRepository partidoRepository;

    public long contarPartidosDelMesActual() {
        LocalDateTime inicioMes = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime finMes = inicioMes.plusMonths(1).minusSeconds(1);
        return partidoRepository.findPartidosEnRangoFechas(inicioMes, finMes).size();
    }
}
