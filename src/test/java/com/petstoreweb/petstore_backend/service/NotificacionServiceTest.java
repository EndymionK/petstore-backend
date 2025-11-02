package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.entity.Notificacion;
import com.petstoreweb.petstore_backend.entity.Producto;
import com.petstoreweb.petstore_backend.repository.NotificacionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificacionServiceTest {

    @Mock
    private NotificacionRepository notificacionRepository;

    @InjectMocks
    private NotificacionService notificacionService;

    @Test
    void verificarYGenerarNotificacion_cuandoStockBajoYNoExisteNotificacion_debeCrearNotificacion() {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo(1);
        producto.setNombre("Producto Test");
        producto.setStock(5);
        producto.setUmbralMinimo(10);

        when(notificacionRepository.findByProductoActivo(1)).thenReturn(Optional.empty());
        when(notificacionRepository.save(any(Notificacion.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        notificacionService.verificarYGenerarNotificacion(producto);

        // Assert
        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionRepository, times(1)).save(captor.capture());
        
        Notificacion notificacionGuardada = captor.getValue();
        assertEquals(1, notificacionGuardada.getIdProducto());
        assertEquals("Producto Test", notificacionGuardada.getNombreProducto());
        assertEquals(5, notificacionGuardada.getStockActual());
        assertEquals(10, notificacionGuardada.getUmbralMinimo());
        assertFalse(notificacionGuardada.getEliminada());
    }

    @Test
    void verificarYGenerarNotificacion_cuandoStockBajoYExisteNotificacion_noDebeCrearDuplicado() {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo(1);
        producto.setNombre("Producto Test");
        producto.setStock(5);
        producto.setUmbralMinimo(10);

        Notificacion notificacionExistente = new Notificacion(1, "Producto Test", 5, 10);
        when(notificacionRepository.findByProductoActivo(1)).thenReturn(Optional.of(notificacionExistente));

        // Act
        notificacionService.verificarYGenerarNotificacion(producto);

        // Assert
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void verificarYGenerarNotificacion_cuandoStockNoEsBajo_noDebeCrearNotificacion() {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo(1);
        producto.setNombre("Producto Test");
        producto.setStock(15);
        producto.setUmbralMinimo(10);

        // Act
        notificacionService.verificarYGenerarNotificacion(producto);

        // Assert
        verify(notificacionRepository, never()).findByProductoActivo(anyInt());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void verificarYGenerarNotificacion_cuandoNoTieneUmbral_noDebeCrearNotificacion() {
        // Arrange
        Producto producto = new Producto();
        producto.setCodigo(1);
        producto.setNombre("Producto Test");
        producto.setStock(5);
        producto.setUmbralMinimo(null);

        // Act
        notificacionService.verificarYGenerarNotificacion(producto);

        // Assert
        verify(notificacionRepository, never()).findByProductoActivo(anyInt());
        verify(notificacionRepository, never()).save(any(Notificacion.class));
    }

    @Test
    void eliminarNotificacionesDeProducto_debeMarcarComoEliminadas() {
        // Arrange
        Integer idProducto = 1;

        // Act
        notificacionService.eliminarNotificacionesDeProducto(idProducto);

        // Assert
        verify(notificacionRepository, times(1)).marcarComoEliminadasPorProducto(idProducto);
    }

    @Test
    void obtenerNotificacionesActivas_debeRetornarSoloActivas() {
        // Arrange
        Notificacion notificacion1 = new Notificacion(1, "Producto 1", 5, 10);
        notificacion1.setId(1L);
        
        Notificacion notificacion2 = new Notificacion(2, "Producto 2", 8, 15);
        notificacion2.setId(2L);

        when(notificacionRepository.findByEliminadaFalseOrderByFechaCreacionDesc())
                .thenReturn(List.of(notificacion1, notificacion2));

        // Act
        var notificaciones = notificacionService.obtenerNotificacionesActivas();

        // Assert
        assertEquals(2, notificaciones.size());
        assertEquals(1, notificaciones.get(0).getId());
        assertEquals("Producto 1", notificaciones.get(0).getNombreProducto());
        assertEquals(2, notificaciones.get(1).getId());
        assertEquals("Producto 2", notificaciones.get(1).getNombreProducto());
    }

    @Test
    void obtenerNotificacionesActivas_cuandoNoHayNotificaciones_debeRetornarListaVacia() {
        // Arrange
        when(notificacionRepository.findByEliminadaFalseOrderByFechaCreacionDesc())
                .thenReturn(Collections.emptyList());

        // Act
        var notificaciones = notificacionService.obtenerNotificacionesActivas();

        // Assert
        assertTrue(notificaciones.isEmpty());
    }
}

