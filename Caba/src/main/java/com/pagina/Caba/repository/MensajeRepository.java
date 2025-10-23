package com.pagina.Caba.repository;

import com.pagina.Caba.model.Mensaje;
import com.pagina.Caba.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    
    /**
     * Obtiene todos los mensajes entre dos usuarios (conversación completa)
     */
    @Query("SELECT m FROM Mensaje m WHERE " +
           "(m.remitente = :usuario1 AND m.destinatario = :usuario2) OR " +
           "(m.remitente = :usuario2 AND m.destinatario = :usuario1) " +
           "ORDER BY m.fechaEnvio ASC")
    List<Mensaje> findConversacionEntreUsuarios(
        @Param("usuario1") Usuario usuario1, 
        @Param("usuario2") Usuario usuario2
    );
    
    /**
     * Cuenta mensajes no leídos para un destinatario específico
     */
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.destinatario = :destinatario AND m.leido = false")
    long countMensajesNoLeidos(@Param("destinatario") Usuario destinatario);
    
    /**
     * Cuenta mensajes no leídos entre dos usuarios específicos
     */
    @Query("SELECT COUNT(m) FROM Mensaje m WHERE m.destinatario = :destinatario " +
           "AND m.remitente = :remitente AND m.leido = false")
    long countMensajesNoLeidosDeUsuario(
        @Param("destinatario") Usuario destinatario,
        @Param("remitente") Usuario remitente
    );
    
    /**
     * Obtiene los últimos N mensajes de una conversación
     */
    @Query("SELECT m FROM Mensaje m WHERE " +
           "(m.remitente = :usuario1 AND m.destinatario = :usuario2) OR " +
           "(m.remitente = :usuario2 AND m.destinatario = :usuario1) " +
           "ORDER BY m.fechaEnvio DESC")
    List<Mensaje> findUltimosMensajes(
        @Param("usuario1") Usuario usuario1,
        @Param("usuario2") Usuario usuario2
    );
    
    /**
     * Obtiene todos los usuarios con los que el usuario ha chateado
     */
    @Query("SELECT DISTINCT CASE " +
           "WHEN m.remitente = :usuario THEN m.destinatario " +
           "ELSE m.remitente END " +
           "FROM Mensaje m WHERE m.remitente = :usuario OR m.destinatario = :usuario")
    List<Usuario> findUsuariosConConversacion(@Param("usuario") Usuario usuario);
    
    /**
     * Marca todos los mensajes como leídos entre dos usuarios
     */
    @Query("UPDATE Mensaje m SET m.leido = true WHERE m.destinatario = :destinatario " +
           "AND m.remitente = :remitente AND m.leido = false")
    void marcarComoLeidosDeUsuario(
        @Param("destinatario") Usuario destinatario,
        @Param("remitente") Usuario remitente
    );
}