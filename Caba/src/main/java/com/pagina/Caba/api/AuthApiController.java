package com.pagina.Caba.api;

import com.pagina.Caba.dto.ArbitroDto;
import com.pagina.Caba.dto.auth.AuthResponse;
import com.pagina.Caba.dto.auth.LoginRequest;
import com.pagina.Caba.dto.auth.RegisterRequest;
import com.pagina.Caba.model.Arbitro;
import com.pagina.Caba.model.EstadoAsignacion;
import com.pagina.Caba.security.jwt.JwtTokenProvider;
import com.pagina.Caba.service.ArbitroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * API REST para autenticación con JWT
 * 
 * @author CABA Pro Team
 * @version 1.0
 * @since 2025-11-10
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación JWT")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200"})
public class AuthApiController {

    private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private ArbitroService arbitroService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Login de usuario (árbitro)
     * 
     * @param loginRequest Credenciales de login
     * @return Token JWT y datos del árbitro
     */
    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", 
               description = "Autentica un usuario y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login exitoso"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Intento de login para: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String jwt = tokenProvider.generateToken(authentication);

            // Obtener datos del árbitro
            Arbitro arbitro = arbitroService.obtenerPorEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            ArbitroDto arbitroDto = convertirADto(arbitro);

            logger.info("Login exitoso para: {}", loginRequest.getEmail());

            return ResponseEntity.ok(AuthResponse.success(jwt, arbitroDto));

        } catch (BadCredentialsException e) {
            logger.error("Credenciales inválidas para: {}", loginRequest.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.error("Credenciales inválidas"));
        } catch (Exception e) {
            logger.error("Error en login: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.error("Error en el servidor"));
        }
    }

    /**
     * Registro de nuevo árbitro
     * 
     * @param registerRequest Datos del nuevo árbitro
     * @return Token JWT y datos del árbitro creado
     */
    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo árbitro",
               description = "Crea una cuenta de árbitro y retorna un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Registro exitoso"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya existe")
    })
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Intento de registro para: {}", registerRequest.getEmail());

            // Verificar si el email ya existe
            if (arbitroService.obtenerPorEmail(registerRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(AuthResponse.error("El email ya está registrado"));
            }

            // Crear nuevo árbitro
            Arbitro nuevoArbitro = new Arbitro();
            nuevoArbitro.setEmail(registerRequest.getEmail());
            nuevoArbitro.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            nuevoArbitro.setNombre(registerRequest.getNombre());
            nuevoArbitro.setApellido(registerRequest.getApellido());
            nuevoArbitro.setTelefono(registerRequest.getTelefono());
            nuevoArbitro.setDireccion(registerRequest.getDireccion());
            nuevoArbitro.setNumeroLicencia(registerRequest.getNumeroLicencia());
            nuevoArbitro.setEspecialidad(registerRequest.getEspecialidad());
            nuevoArbitro.setEscalafon(registerRequest.getEscalafon());
            nuevoArbitro.setDisponible(true);
            nuevoArbitro.setActivo(true);
            nuevoArbitro.setTarifaBase(BigDecimal.ZERO); // Se asignará después por el admin

            Arbitro arbitroCreado = arbitroService.crear(nuevoArbitro);

            // Generar token
            String jwt = tokenProvider.generateTokenFromUsername(arbitroCreado.getEmail());

            ArbitroDto arbitroDto = convertirADto(arbitroCreado);

            logger.info("Registro exitoso para: {}", registerRequest.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(AuthResponse.success(jwt, arbitroDto));

        } catch (Exception e) {
            logger.error("Error en registro: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AuthResponse.error("Error al crear la cuenta"));
        }
    }

    /**
     * Refrescar token JWT
     * 
     * @param authentication Autenticación actual
     * @return Nuevo token JWT
     */
    @PostMapping("/refresh")
    @Operation(summary = "Refrescar token",
               description = "Genera un nuevo token JWT para el usuario autenticado")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Token renovado"),
        @ApiResponse(responseCode = "401", description = "No autenticado")
    })
    public ResponseEntity<?> refreshToken(Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "No autenticado"));
            }

            String newToken = tokenProvider.generateToken(authentication);

            Map<String, String> response = new HashMap<>();
            response.put("token", newToken);
            response.put("tipo", "Bearer");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error al refrescar token: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al refrescar token"));
        }
    }

    /**
     * Logout (invalidar token en el cliente)
     * 
     * @return Mensaje de confirmación
     */
    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión",
               description = "Cierra la sesión del usuario (el token debe ser descartado en el cliente)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Logout exitoso")
    })
    public ResponseEntity<?> logout() {
        SecurityContextHolder.clearContext();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso");
        
        return ResponseEntity.ok(response);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Convierte una entidad Arbitro a DTO
     */
    private ArbitroDto convertirADto(Arbitro arbitro) {
        ArbitroDto dto = new ArbitroDto();
        dto.setId(arbitro.getId());
        dto.setEmail(arbitro.getEmail());
        dto.setNombre(arbitro.getNombre());
        dto.setApellido(arbitro.getApellido());
        dto.setTelefono(arbitro.getTelefono());
        dto.setDireccion(arbitro.getDireccion());
        dto.setNumeroLicencia(arbitro.getNumeroLicencia());
        dto.setEspecialidad(arbitro.getEspecialidad());
        dto.setEscalafon(arbitro.getEscalafon());
        dto.setTarifaBase(arbitro.getTarifaBase());
        dto.setDisponible(arbitro.getDisponible());
        dto.setActivo(arbitro.getActivo());
        dto.setFotoUrl(arbitro.getFotoUrl());

        // Estadísticas
        long totalAsignaciones = arbitro.getAsignaciones() != null ? arbitro.getAsignaciones().size() : 0;
        long pendientes = arbitro.getAsignaciones() != null ?
                arbitro.getAsignaciones().stream()
                        .filter(a -> a.getEstado() == EstadoAsignacion.PENDIENTE)
                        .count() : 0;
        long aceptadas = arbitro.getAsignaciones() != null ?
                arbitro.getAsignaciones().stream()
                        .filter(a -> a.getEstado() == EstadoAsignacion.ACEPTADA)
                        .count() : 0;

        dto.setTotalAsignaciones((int) totalAsignaciones);
        dto.setAsignacionesPendientes((int) pendientes);
        dto.setAsignacionesAceptadas((int) aceptadas);

        return dto;
    }
}
