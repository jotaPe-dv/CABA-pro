package com.pagina.Caba.repository;

import com.pagina.Caba.model.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Long> {
    boolean existsByEmail(String email);

    Optional<Administrador> findByEmail(String email);

    Optional<Administrador> findByTelefono(String telefono);
    List<Administrador> findByCargoContainingIgnoreCase(String cargo);
    List<Administrador> findByPermisosEspecialesTrue();
    List<Administrador> findByActivoTrue();

    @Query("SELECT a FROM Administrador a WHERE a.ultimoAcceso >= :fechaLimite")
    List<Administrador> findAdministradoresActivosRecientes(LocalDateTime fechaLimite);

    @Query("SELECT COUNT(a) FROM Administrador a WHERE a.activo = true")
    long countAdministradoresActivos();

    @Query("SELECT COUNT(a) FROM Administrador a WHERE a.permisosEspeciales = true")
    long countAdministradoresConPermisosEspeciales();
}
