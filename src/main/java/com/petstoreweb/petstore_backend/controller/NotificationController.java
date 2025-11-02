package com.petstoreweb.petstore_backend.controller;

import com.petstoreweb.petstore_backend.dto.NotificacionResponse;
import com.petstoreweb.petstore_backend.service.NotificacionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de notificaciones de bajo stock.
 * Proporciona endpoints para consultar notificaciones internas (HU-4.2).
 */
@RestController
@RequestMapping("/api/inventory")
public class NotificationController {

    private final NotificacionService notificacionService;

    public NotificationController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    /**
     * Obtiene todas las notificaciones activas de bajo stock.
     * Estas son notificaciones internas que el administrador puede ver en la aplicación (HU-4.2-CA03).
     * 
     * @return Lista de notificaciones activas ordenadas por fecha de creación descendente
     */
    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionResponse>> obtenerNotificaciones() {
        List<NotificacionResponse> notificaciones = notificacionService.obtenerNotificacionesActivas();
        return ResponseEntity.ok(notificaciones);
    }
}

