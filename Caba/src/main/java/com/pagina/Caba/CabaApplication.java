package com.pagina.Caba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.repository.AdministradorRepository;
import com.pagina.Caba.repository.ArbitroRepository;
import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.repository.PartidoRepository;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.service.TarifaService;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.model.Partido;
import java.util.List;

@SpringBootApplication
public class CabaApplication {


	public static void main(String[] args) {
		SpringApplication.run(CabaApplication.class, args);
	}

	@Bean
	@Order(2) // Ejecutar después del DataLoader que tiene @Order(1)
	public CommandLineRunner initTestUsers(
		AdministradorRepository administradorRepository,
		ArbitroRepository arbitroRepository,
		PasswordEncoder passwordEncoder,
		TorneoRepository torneoRepository,
		PartidoRepository partidoRepository,
		AsignacionService asignacionService,
		TarifaService tarifaService) {
	return args -> {
			// ADMIN
			if (!administradorRepository.existsByEmail("admin@caba.com")) {
				   Administrador admin = new Administrador(
					   "Admin", "CABA", "admin@caba.com",
					   passwordEncoder.encode("admin123"),
					   "Administrador Principal"
				   );
				   admin.setPermisosEspeciales(true);
				   admin.setActivo(true);
				   administradorRepository.save(admin);
			}

			// ARBITROS (Principal, Auxiliar, Mesa)
			if (!arbitroRepository.existsByEmail("principal@caba.com")) {
				Arbitro arbitroPrincipal = new Arbitro(
					"Carlos", "Ramirez", "principal@caba.com",
					passwordEncoder.encode("123456"),
					"ARB12345",
					"3001234567",
					"Principal",
					"A",
					"https://ejemplo.com/foto1.jpg"
				);
				arbitroPrincipal.setActivo(true);
				arbitroPrincipal.setDisponible(true);
				arbitroRepository.save(arbitroPrincipal);
			}
			
			if (!arbitroRepository.existsByEmail("asistente@caba.com")) {
				Arbitro arbitroAuxiliar = new Arbitro(
					"Maria", "Lopez", "asistente@caba.com",
					passwordEncoder.encode("123456"),
					"ARB67890",
					"3007654321",
					"Auxiliar",
					"B",
					"https://ejemplo.com/foto2.jpg"
				);
				arbitroAuxiliar.setActivo(true);
				arbitroAuxiliar.setDisponible(true);
				arbitroRepository.save(arbitroAuxiliar);
			}
			
			if (!arbitroRepository.existsByEmail("mesa@caba.com")) {
				Arbitro arbitroMesa = new Arbitro(
					"Juan", "Martinez", "mesa@caba.com",
					passwordEncoder.encode("123456"),
					"ARB11223",
					"3009876543",
					"Mesa",
					"C",
					"https://ejemplo.com/foto3.jpg"
				);
				arbitroMesa.setActivo(true);
				arbitroMesa.setDisponible(true);
				arbitroRepository.save(arbitroMesa);
			}
			// Crear un partido por defecto si no existe
			// Buscar el administrador por email
			Administrador adminDefault = administradorRepository.findByEmail("admin@caba.com").orElse(null);
			if (adminDefault != null) {
				// Buscar o crear un torneo por defecto
				String nombreTorneo = "Torneo Inicial";
				Torneo torneo = null;
				var torneos = torneoRepository.findByNombreContainingIgnoreCase(nombreTorneo);
				if (!torneos.isEmpty()) {
					torneo = torneos.get(0);
				} else {
					torneo = new Torneo(
						nombreTorneo,
						"Torneo de prueba por defecto",
						java.time.LocalDate.now(),
						java.time.LocalDate.now().plusMonths(1),
						adminDefault
					);
					torneoRepository.save(torneo);
				}

				// Crear torneo de baloncesto estilo eliminación directa desde cuartos
				var partidos = partidoRepository.findByTorneo(torneo);
				
				// Si no hay partidos, crear torneo de 8 equipos (Cuartos, Semifinales, Final)
				if (partidos.isEmpty()) {
					String[] equipos = {
						"Bulls Chicago", "Lakers Los Angeles",
						"Celtics Boston", "Warriors Golden State",
						"Heat Miami", "Nets Brooklyn",
						"Bucks Milwaukee", "Mavericks Dallas"
					};
					
					String estadio = "Arena Central";
					System.out.println("🏀 Creando torneo de baloncesto - Eliminación Directa...");
					
					java.time.LocalDateTime fechaBase = java.time.LocalDateTime.now().plusDays(1);
					
					// CUARTOS DE FINAL (4 partidos)
					System.out.println("📋 Fase: CUARTOS DE FINAL");
					for (int i = 0; i < 4; i++) {
						Partido cuarto = new Partido(
							equipos[i * 2],
							equipos[i * 2 + 1],
							fechaBase.plusDays(i),
							estadio,
							torneo
						);
						cuarto.setTipoPartido("Oficial");
						cuarto.setObservaciones("Cuartos de Final - Partido " + (i + 1));
						partidoRepository.save(cuarto);
						System.out.println("  ✓ Cuarto " + (i + 1) + ": " + equipos[i * 2] + " vs " + equipos[i * 2 + 1]);
					}
					
					// SEMIFINALES (2 partidos)
					System.out.println("📋 Fase: SEMIFINALES");
					fechaBase = fechaBase.plusDays(5);
					Partido semi1 = new Partido(
						"Ganador Cuarto 1",
						"Ganador Cuarto 2",
						fechaBase,
						estadio,
						torneo
					);
					semi1.setTipoPartido("Oficial");
					semi1.setObservaciones("Semifinal 1");
					partidoRepository.save(semi1);
					System.out.println("  ✓ Semifinal 1: Ganador Q1 vs Ganador Q2");
					
					Partido semi2 = new Partido(
						"Ganador Cuarto 3",
						"Ganador Cuarto 4",
						fechaBase.plusHours(3),
						estadio,
						torneo
					);
					semi2.setTipoPartido("Oficial");
					semi2.setObservaciones("Semifinal 2");
					partidoRepository.save(semi2);
					System.out.println("  ✓ Semifinal 2: Ganador Q3 vs Ganador Q4");
					
					// FINAL (1 partido)
					System.out.println("📋 Fase: FINAL");
					fechaBase = fechaBase.plusDays(3);
					Partido finalPartido = new Partido(
						"Ganador Semifinal 1",
						"Ganador Semifinal 2",
						fechaBase,
						estadio,
						torneo
					);
					finalPartido.setTipoPartido("Oficial");
					finalPartido.setObservaciones("GRAN FINAL DEL TORNEO");
					partidoRepository.save(finalPartido);
					System.out.println("  ✓ FINAL: Ganador S1 vs Ganador S2");
					
					System.out.println("✅ Torneo creado: 4 Cuartos + 2 Semis + 1 Final = 7 partidos");
					partidos = partidoRepository.findByTorneo(torneo);
				}
				
				// Tomar el primer partido para crear asignaciones de ejemplo
				Partido partidoObj = partidos.isEmpty() ? null : partidos.get(0);

				// Crear asignaciones por defecto: Principal, Auxiliar, Mesa si hay árbitros disponibles
				if (partidoObj != null) {
					// Verificar si ya existen asignaciones para este partido
					List<com.pagina.Caba.model.Asignacion> asignacionesExistentes = 
						asignacionService.obtenerAsignacionesPorPartido(partidoObj.getId());
					if (!asignacionesExistentes.isEmpty()) {
						System.out.println("ℹ️ Ya existen " + asignacionesExistentes.size() + 
							" asignaciones para el partido por defecto, omitiendo creación");
					} else {
						String[] roles = {"Principal", "Auxiliar", "Mesa"};
					for (String rol : roles) {
						// Buscar un árbitro disponible con la especialidad correspondiente
						Arbitro elegido = arbitroRepository.findByDisponibleTrueAndActivoTrue()
							.stream()
							.filter(a -> a.getEspecialidad() != null && a.getEspecialidad().equalsIgnoreCase(rol))
							.findFirst()
							.orElse(null);
						// Si no hay uno con la especialidad exacta, tomar cualquier disponible
						if (elegido == null) {
							elegido = arbitroRepository.findByDisponibleTrueAndActivoTrue()
							.stream().findFirst().orElse(null);
						}
								if (elegido != null) {
									try {
										// Buscar tarifa adecuada y crear la asignación LOCALMENTE estableciendo el rol antes de persistir
										String escalafon = elegido.getEscalafon();
										String tipo = partidoObj.getTipoPartido();
										java.time.LocalDateTime fecha = partidoObj.getFechaPartido();
										var tarifaOpt = tarifaService.buscarTarifaParaAsignacion(tipo, escalafon, fecha);
										if (tarifaOpt.isEmpty()) {
											System.out.println("⚠️ No hay tarifa vigente para rol " + rol + " y árbitro " + elegido.getEmail());
										} else {
											var tarifa = tarifaOpt.get();
											Asignacion asignacion = new Asignacion(partidoObj, elegido, tarifa);
											asignacion.setRolEspecifico(rol);
											asignacionService.guardar(asignacion);
											System.out.println("✅ Asignación por defecto creada: partidoId=" + partidoObj.getId() + ", arbitro=" + elegido.getEmail() + ", rol=" + rol);
										}
									} catch (Exception ex) {
										System.out.println("⚠️ No se pudo crear asignación para rol " + rol + ": " + ex.getMessage());
									}
								} else {
									System.out.println("ℹ️ No hay árbitros disponibles para asignar el rol: " + rol);
								}
					}
					}
				}
			}
		};
	}

}
