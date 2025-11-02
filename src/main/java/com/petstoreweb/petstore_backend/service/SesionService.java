package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.entity.Sesion;
import com.petstoreweb.petstore_backend.repository.SesionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para la gestión de sesiones de usuario.
 * Maneja la creación, validación y expiración de sesiones.
 */
@Service
public class SesionService {

    private final SesionRepository sesionRepository;

    @Value("${session.inactivity.timeout:900000}") // 15 minutos por defecto (900,000 ms)
    private long inactivityTimeout;

    public SesionService(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    /**
     * Crea o reactiva una sesión para un usuario.
     * Si existe una sesión activa para el usuario, la desactiva primero.
     *
     * @param idUsuario ID del usuario
     * @param token Token JWT de la sesión
     * @return La sesión creada
     */
    @Transactional
    public Sesion crearOActualizarSesion(Integer idUsuario, String token) {
        // Primero, desactivar todas las sesiones activas del usuario (HU-6.3-CA01)
        sesionRepository.desactivarTodasLasSesionesDeUsuario(idUsuario);

        // Crear nueva sesión
        Sesion sesion = new Sesion(idUsuario, token);
        return sesionRepository.save(sesion);
    }

    /**
     * Valida si una sesión es activa y no ha expirado por inactividad.
     *
     * @param token Token JWT de la sesión
     * @return true si la sesión es válida, false en caso contrario
     */
    public boolean esSesionValida(String token) {
        Optional<Sesion> sesionOpt = sesionRepository.findByTokenAndActivo(token, true);

        if (sesionOpt.isEmpty()) {
            return false;
        }

        Sesion sesion = sesionOpt.get();
        LocalDateTime limite = LocalDateTime.now().minusNanos(inactivityTimeout * 1_000_000L);

        // Verificar si la sesión ha expirado por inactividad (HU-6.3-CA02)
        if (sesion.getUltimaActividad().isBefore(limite)) {
            // Desactivar la sesión expirada
            sesion.desactivar();
            sesionRepository.save(sesion);
            return false;
        }

        return true;
    }

    /**
     * Actualiza la última actividad de una sesión.
     *
     * @param token Token JWT de la sesión
     */
    @Transactional
    public void actualizarActividad(String token) {
        sesionRepository.actualizarUltimaActividad(token, LocalDateTime.now());
    }

    /**
     * Cierra una sesión específica (logout).
     *
     * @param token Token JWT de la sesión
     */
    @Transactional
    public void cerrarSesion(String token) {
        sesionRepository.desactivarSesionPorToken(token);
    }

    /**
     * Obtiene una sesión por token.
     *
     * @param token Token JWT de la sesión
     * @return Sesión si existe, Optional.empty() si no
     */
    public Optional<Sesion> obtenerSesionPorToken(String token) {
        return sesionRepository.findByTokenAndActivo(token, true);
    }

    /**
     * Obtiene el tiempo de inactividad configurado en milisegundos.
     *
     * @return Tiempo de inactividad en milisegundos
     */
    public long getInactivityTimeout() {
        return inactivityTimeout;
    }
}

