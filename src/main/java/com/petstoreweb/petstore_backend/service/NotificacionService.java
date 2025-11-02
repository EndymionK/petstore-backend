package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.dto.NotificacionResponse;
import com.petstoreweb.petstore_backend.entity.Notificacion;
import com.petstoreweb.petstore_backend.entity.Producto;
import com.petstoreweb.petstore_backend.repository.NotificacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de notificaciones de bajo stock.
 * Genera y gestiona notificaciones automáticas cuando los productos
 * caen por debajo de su umbral mínimo.
 */
@Service
public class NotificacionService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionService.class);
    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    /**
     * Verifica si un producto tiene stock bajo y genera una notificación si es necesario.
     * 
     * @param producto El producto a verificar
     */
    @Transactional
    public void verificarYGenerarNotificacion(Producto producto) {
        // Solo generar notificación si el producto tiene umbral configurado y stock bajo
        if (producto.getUmbralMinimo() != null && producto.tieneStockBajo()) {
            // Verificar si ya existe una notificación activa para este producto
            notificacionRepository.findByProductoActivo(producto.getCodigo())
                    .ifPresentOrElse(
                            notificacion -> logger.debug("Ya existe una notificación activa para el producto {}", producto.getNombre()),
                            () -> {
                                // Crear nueva notificación (CA01 y CA02)
                                Notificacion notificacion = new Notificacion(
                                        producto.getCodigo(),
                                        producto.getNombre(),
                                        producto.getStock(),
                                        producto.getUmbralMinimo()
                                );
                                notificacionRepository.save(notificacion);
                                logger.info("Notificación generada para producto: {} (Stock: {}, Umbral: {})", 
                                        producto.getNombre(), producto.getStock(), producto.getUmbralMinimo());
                            }
                    );
        }
    }

    /**
     * Marca como eliminadas las notificaciones de un producto cuando se repone el stock.
     * 
     * @param idProducto ID del producto cuya notificación debe eliminarse
     */
    @Transactional
    public void eliminarNotificacionesDeProducto(Integer idProducto) {
        notificacionRepository.marcarComoEliminadasPorProducto(idProducto);
        logger.debug("Notificaciones eliminadas para el producto con ID: {}", idProducto);
    }

    /**
     * Obtiene todas las notificaciones activas.
     * 
     * @return Lista de notificaciones activas
     */
    public List<NotificacionResponse> obtenerNotificacionesActivas() {
        return notificacionRepository.findByEliminadaFalseOrderByFechaCreacionDesc()
                .stream()
                .map(this::convertirANotificacionResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Notificacion a un DTO NotificacionResponse.
     */
    private NotificacionResponse convertirANotificacionResponse(Notificacion notificacion) {
        return new NotificacionResponse(
                notificacion.getId(),
                notificacion.getIdProducto(),
                notificacion.getNombreProducto(),
                notificacion.getStockActual(),
                notificacion.getUmbralMinimo(),
                notificacion.getFechaCreacion(),
                notificacion.getEliminada()
        );
    }
}

