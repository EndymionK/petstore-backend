package com.petstoreweb.petstore_backend.repository;

import com.petstoreweb.petstore_backend.entity.Sesion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Sesion.
 * Provee métodos para gestionar sesiones de usuario.
 */
@Repository
public interface SesionRepository extends JpaRepository<Sesion, Long> {

    /**
     * Busca una sesión activa por token.
     */
    Optional<Sesion> findByTokenAndActivo(String token, Boolean activo);

    /**
     * Busca todas las sesiones activas de un usuario.
     */
    List<Sesion> findByIdUsuarioAndActivo(Integer idUsuario, Boolean activo);

    /**
     * Desactiva todas las sesiones activas de un usuario.
     */
    @Modifying
    @Query("UPDATE Sesion s SET s.activo = false WHERE s.idUsuario = :idUsuario AND s.activo = true")
    void desactivarTodasLasSesionesDeUsuario(@Param("idUsuario") Integer idUsuario);

    /**
     * Desactiva una sesión específica por token.
     */
    @Modifying
    @Query("UPDATE Sesion s SET s.activo = false WHERE s.token = :token AND s.activo = true")
    void desactivarSesionPorToken(@Param("token") String token);

    /**
     * Busca sesiones inactivas o expiradas por tiempo de inactividad.
     */
    @Query("SELECT s FROM Sesion s WHERE s.activo = true AND s.ultimaActividad < :limiteTiempo")
    List<Sesion> findSesionesExpiradasPorInactividad(@Param("limiteTiempo") LocalDateTime limiteTiempo);

    /**
     * Actualiza la última actividad de una sesión.
     */
    @Modifying
    @Query("UPDATE Sesion s SET s.ultimaActividad = :fecha WHERE s.token = :token")
    void actualizarUltimaActividad(@Param("token") String token, @Param("fecha") LocalDateTime fecha);
}

