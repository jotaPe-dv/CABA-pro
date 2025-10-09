package com.pagina.Caba.repository;

import com.pagina.Caba.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    
    // Buscar por email (unico)
    Optional<Usuario> findByEmail(String email);
    
    // Verificar si existe un email
    boolean existsByEmail(String email);
    
    // Buscar usuarios activos
    List<Usuario> findByActivoTrue();
    
    // Buscar usuarios inactivos
    List<Usuario> findByActivoFalse();
    
    // Buscar por nombre y apellido
    List<Usuario> findByNombreContainingIgnoreCaseAndApellidoContainingIgnoreCase(
        String nombre, String apellido);
    
    // Buscar usuarios registrados en un rango de fechas
    @Query("SELECT u FROM Usuario u WHERE u.fechaRegistro BETWEEN :fechaInicio AND :fechaFin")
    List<Usuario> findByFechaRegistroBetween(
        @Param("fechaInicio") LocalDateTime fechaInicio, 
        @Param("fechaFin") LocalDateTime fechaFin);
    
    // Buscar por tipo de usuario (ARBITRO o ADMINISTRADOR)
    @Query("SELECT u FROM Usuario u WHERE TYPE(u) = :tipoUsuario")
    List<Usuario> findByTipoUsuario(@Param("tipoUsuario") Class<? extends Usuario> tipoUsuario);
    
    // Contar usuarios activos
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.activo = true")
    long countUsuariosActivos();
    
    // Contar usuarios por tipo
    @Query("SELECT COUNT(u) FROM Usuario u WHERE TYPE(u) = :tipoUsuario")
    long countByTipoUsuario(@Param("tipoUsuario") Class<? extends Usuario> tipoUsuario);
}
