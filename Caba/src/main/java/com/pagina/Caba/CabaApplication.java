package com.pagina.Caba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.pagina.Caba.model.Administrador;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.repository.AdministradorRepository;
import com.pagina.Caba.repository.ArbitroRepository;
import com.pagina.Caba.repository.TorneoRepository;
import com.pagina.Caba.repository.PartidoRepository;
import com.pagina.Caba.service.AsignacionService;
import com.pagina.Caba.model.Asignacion;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.Torneo;
import com.pagina.Caba.model.Partido;

@SpringBootApplication
public class CabaApplication {


	public static void main(String[] args) {
		SpringApplication.run(CabaApplication.class, args);
	}

	@Bean
	public CommandLineRunner initTestUsers(
		AdministradorRepository administradorRepository,
		ArbitroRepository arbitroRepository,
		PasswordEncoder passwordEncoder,
		TorneoRepository torneoRepository,
		PartidoRepository partidoRepository,
		AsignacionService asignacionService) {
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

			// ARBITRO
			if (!arbitroRepository.existsByEmail("arbitro@caba.com")) {
				Arbitro arbitro = new Arbitro(
					"Arbitro", "CABA", "arbitro@caba.com",
					passwordEncoder.encode("arbitro123"),
					"ARB12345",
					"3000000000",
					"Principal",
					"A",
					"https://ejemplo.com/foto.jpg"
				);
				arbitro.setActivo(true);
				arbitro.setDisponible(true);
				arbitroRepository.save(arbitro);
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

				// Verificar si ya existe un partido por defecto
				String equipoLocal = "Equipo Local Default";
				String equipoVisitante = "Equipo Visitante Default";
				var partidos = partidoRepository.findByTorneo(torneo);
				boolean existe = partidos.stream().anyMatch(p -> 
					equipoLocal.equals(p.getEquipoLocal()) && equipoVisitante.equals(p.getEquipoVisitante())
				);
				Partido partidoObj = null;
				if (!existe) {
					Partido partido = new Partido(
						equipoLocal,
						equipoVisitante,
						java.time.LocalDateTime.now().plusDays(7),
						"Estadio Central",
						torneo
					);
					partido.setTipoPartido("Amistoso");
					partido.setObservaciones("Partido creado por defecto al iniciar la aplicación");
					partidoObj = partidoRepository.save(partido);
				} else {
					// Obtener el partido existente que coincide con los equipos por defecto
					partidoObj = partidos.stream().filter(p ->
						equipoLocal.equals(p.getEquipoLocal()) && equipoVisitante.equals(p.getEquipoVisitante())
					).findFirst().orElse(partidos.get(0));
				}

				// Crear asignaciones por defecto: Principal, Auxiliar, Mesa si hay árbitros disponibles
				if (partidoObj != null) {
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
								Asignacion creada = asignacionService.crearAsignacion(partidoObj.getId(), elegido.getId(), partidoObj.getTipoPartido());
								// Asegurarnos de setear el rol específico y guardar
								creada.setRolEspecifico(rol);
								asignacionService.guardar(creada);
								System.out.println("✅ Asignación por defecto creada: partidoId=" + partidoObj.getId() + ", arbitro=" + elegido.getEmail() + ", rol=" + rol);
							} catch (Exception ex) {
								System.out.println("⚠️ No se pudo crear asignación para rol " + rol + ": " + ex.getMessage());
							}
						} else {
							System.out.println("ℹ️ No hay árbitros disponibles para asignar el rol: " + rol);
						}
					}
				}
			}
		};
	}

}
