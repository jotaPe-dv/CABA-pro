package com.pagina.Caba.config;

import com.pagina.Caba.model.Tarifa;
import com.pagina.Caba.repository.TarifaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Order(1) // Ejecutar primero (antes que CabaApplication @Order(2))
public class DataLoader implements CommandLineRunner {

    private final TarifaRepository tarifaRepository;

    public DataLoader(TarifaRepository tarifaRepository) {
        this.tarifaRepository = tarifaRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎯 DEBUG DataLoader: Iniciando carga de tarifas de prueba...");
        
        // Solo insertar tarifas si no existen
        if (tarifaRepository.count() == 0) {
            System.out.println("🎯 DEBUG DataLoader: No hay tarifas, creando tarifas de prueba...");
            
            // Crear tarifas para diferentes tipos de partido y escalafones
            // USAR LOS VALORES CORRECTOS que usa la aplicación
            String[] tiposPartido = {"Oficial", "Amistoso"};
            String[] escalafones = {"A", "B", "C"};
            
            for (String tipo : tiposPartido) {
                for (String escalafon : escalafones) {
                    Tarifa tarifa = new Tarifa();
                    tarifa.setTipoPartido(tipo);
                    tarifa.setEscalafon(escalafon);
                    tarifa.setMonto(calcularMonto(tipo, escalafon));
                    tarifa.setDescripcion("Tarifa para " + tipo + " - Escalafón " + escalafon);
                    tarifa.setActiva(true);
                    tarifa.setFechaCreacion(LocalDateTime.now());
                    tarifa.setFechaVigenciaInicio(LocalDateTime.now().minusDays(30));
                    tarifa.setFechaVigenciaFin(null); // Sin fecha fin = vigente indefinidamente
                    
                    tarifaRepository.save(tarifa);
                    System.out.println("✅ DEBUG DataLoader: Creada tarifa: " + tipo + " - " + escalafon + " = $" + tarifa.getMonto());
                }
            }
            
            System.out.println("🎯 DEBUG DataLoader: Creadas " + tarifaRepository.count() + " tarifas de prueba");
        } else {
            System.out.println("🎯 DEBUG DataLoader: Ya existen " + tarifaRepository.count() + " tarifas, saltando creación");
        }
    }
    
    private BigDecimal calcularMonto(String tipo, String escalafon) {
        BigDecimal base = BigDecimal.valueOf(100);
        
        // Multiplicador por tipo de partido
        switch (tipo) {
            case "Oficial":
                base = base.multiply(BigDecimal.valueOf(2.0));
                break;
            case "Amistoso":
                base = base.multiply(BigDecimal.valueOf(1.0));
                break;
            default:
                base = base.multiply(BigDecimal.valueOf(1.0));
                break;
        }
        
        // Multiplicador por escalafón
        switch (escalafon) {
            case "A": // Principal
                base = base.multiply(BigDecimal.valueOf(3.0));
                break;
            case "B": // Auxiliar
                base = base.multiply(BigDecimal.valueOf(2.0));
                break;
            case "C": // Mesa
                base = base.multiply(BigDecimal.valueOf(1.5));
                break;
            default:
                base = base.multiply(BigDecimal.valueOf(1.0));
                break;
        }
        
        return base;
    }
}