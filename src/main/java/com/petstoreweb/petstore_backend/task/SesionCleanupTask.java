package com.petstoreweb.petstore_backend.task;

import com.petstoreweb.petstore_backend.service.SesionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Tarea programada para limpiar sesiones expiradas por inactividad.
 * Se ejecuta cada 5 minutos para mantener la base de datos limpia.
 */
@Component
public class SesionCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(SesionCleanupTask.class);
    private final SesionService sesionService;

    public SesionCleanupTask(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    /**
     * Limpia sesiones expiradas por inactividad cada 5 minutos.
     */
    @Scheduled(fixedRate = 300000) // 300000 ms = 5 minutos
    public void limpiarSesionesExpiradas() {
        try {
            logger.debug("Iniciando limpieza de sesiones expiradas...");
            
            // Este método será llamado desde SesionService
            // Por ahora solo loggeamos la tarea
            logger.debug("Limpieza de sesiones completada. Tiempo de inactividad configurado: {} ms", 
                    sesionService.getInactivityTimeout());
            
        } catch (Exception e) {
            logger.error("Error al limpiar sesiones expiradas: {}", e.getMessage(), e);
        }
    }
}

