package com.pagina.Caba.config;

import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Tarifa;
import com.pagina.Caba.repository.AdministradorRepository;
import com.pagina.Caba.repository.ArbitroRepository;
import com.pagina.Caba.repository.TarifaRepository;
import com.pagina.Caba.repository.UsuarioRepository;
import com.pagina.Caba.service.ConfiguracionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.core.annotation.Order;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Order(1) // Ejecutar primero (antes que CabaApplication @Order(2))
public class DataLoader implements CommandLineRunner {

    private final TarifaRepository tarifaRepository;
    private final ConfiguracionService configuracionService;
    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final ArbitroRepository arbitroRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(
            TarifaRepository tarifaRepository, 
            ConfiguracionService configuracionService,
            UsuarioRepository usuarioRepository,
            AdministradorRepository administradorRepository,
            ArbitroRepository arbitroRepository,
            PasswordEncoder passwordEncoder) {
        this.tarifaRepository = tarifaRepository;
        this.configuracionService = configuracionService;
        this.usuarioRepository = usuarioRepository;
        this.administradorRepository = administradorRepository;
        this.arbitroRepository = arbitroRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🎯 DEBUG DataLoader: Iniciando carga de datos de prueba...");
        
        // 1. CREAR ADMIN SI NO EXISTE
        crearAdminInicial();
        
        // 2. CREAR ÁRBITROS BÁSICOS SI NO EXISTEN
        crearArbitrosIniciales();
        
        // 3. Inicializar configuraciones del sistema
        configuracionService.inicializarConfiguraciones();
        System.out.println("✅ DEBUG DataLoader: Configuraciones del sistema inicializadas");
        
        // 4. Solo insertar tarifas si no existen
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
    
    /**
     * Crea el administrador inicial si no existe
     */
    private void crearAdminInicial() {
        String emailAdmin = "admin@caba.com";
        
        if (!usuarioRepository.existsByEmail(emailAdmin)) {
            System.out.println("🎯 DEBUG DataLoader: Creando administrador inicial...");
            
            Administrador admin = new Administrador();
            admin.setEmail(emailAdmin);
            admin.setPassword(passwordEncoder.encode("123456")); // Hash correcto con BCrypt
            admin.setNombre("Admin");
            admin.setApellido("CABA");
            admin.setCargo("Administrador General");
            admin.setTelefono("3001234567");
            admin.setActivo(true);
            admin.setFechaRegistro(LocalDateTime.now());
            
            administradorRepository.save(admin);
            System.out.println("✅ DEBUG DataLoader: Admin creado - Email: " + emailAdmin + ", Password: 123456");
        } else {
            System.out.println("🎯 DEBUG DataLoader: Admin ya existe: " + emailAdmin);
        }
    }
    
    /**
     * Crea los 3 árbitros iniciales si no existen
     */
    private void crearArbitrosIniciales() {
        System.out.println("🎯 DEBUG DataLoader: Creando árbitros iniciales...");
        
        // Árbitro 1: Principal
        crearArbitro("principal@caba.com", "123456", "Carlos", "Ramírez", "PRINCIPAL", "3009876543");
        
        // Árbitro 2: Auxiliar 1 (Asistente)
        crearArbitro("asistente@caba.com", "123456", "Juan", "López", "AUXILIAR_1", "3012345678");
        
        // Árbitro 3: Auxiliar 2 (Mesa)
        crearArbitro("mesa@caba.com", "123456", "Pedro", "González", "AUXILIAR_2", "3011112222");
        
        System.out.println("✅ DEBUG DataLoader: " + arbitroRepository.count() + " árbitros creados");
    }
    
    /**
     * Crea un árbitro individual
     */
    private void crearArbitro(String email, String password, String nombre, String apellido, String escalafon, String telefono) {
        if (!usuarioRepository.existsByEmail(email)) {
            Arbitro arbitro = new Arbitro();
            arbitro.setEmail(email);
            arbitro.setPassword(passwordEncoder.encode(password)); // Hash correcto con BCrypt
            arbitro.setNombre(nombre);
            arbitro.setApellido(apellido);
            arbitro.setEscalafon(escalafon);
            arbitro.setTelefono(telefono);
            arbitro.setActivo(true);
            arbitro.setDisponible(true);
            arbitro.setNumeroLicencia("LIC-" + System.currentTimeMillis());
            arbitro.setDireccion("Bogotá, Colombia");
            arbitro.setEspecialidad("General");
            arbitro.setTarifaBase(BigDecimal.valueOf(100.0));
            arbitro.setFechaRegistro(LocalDateTime.now());
            
            arbitroRepository.save(arbitro);
            System.out.println("✅ DEBUG DataLoader: Árbitro creado - " + email + " (" + escalafon + ") - Password: " + password);
        } else {
            System.out.println("🎯 DEBUG DataLoader: Árbitro ya existe: " + email);
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