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

@SpringBootApplication
public class CabaApplication {


	public static void main(String[] args) {
		SpringApplication.run(CabaApplication.class, args);
	}

	@Bean
	public CommandLineRunner initTestUsers(
		AdministradorRepository administradorRepository,
		ArbitroRepository arbitroRepository,
		PasswordEncoder passwordEncoder) {
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
		};
	}

}
