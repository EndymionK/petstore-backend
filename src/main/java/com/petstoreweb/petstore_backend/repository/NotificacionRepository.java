package com.petstoreweb.petstore_backend.repository;

import com.petstoreweb.petstore_backend.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    /**
     * Busca notificaciones activas (no eliminadas) ordenadas por fecha de creación descendente.
     */
    List<Notificacion> findByEliminadaFalseOrderByFechaCreacionDesc();

    /**
     * Busca una notificación activa para un producto específico.
     * Esto permite verificar si ya existe una notificación para un producto.
     */
    @Query("SELECT n FROM Notificacion n WHERE n.idProducto = :idProducto AND n.eliminada = false")
    java.util.Optional<Notificacion> findByProductoActivo(@Param("idProducto") Integer idProducto);

    /**
     * Marca todas las notificaciones de un producto como eliminadas.
     * Esto se usa cuando se repone el stock del producto.
     */
    @Modifying
    @Query("UPDATE Notificacion n SET n.eliminada = true WHERE n.idProducto = :idProducto AND n.eliminada = false")
    void marcarComoEliminadasPorProducto(@Param("idProducto") Integer idProducto);
}

