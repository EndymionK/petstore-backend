package com.petstoreweb.petstore_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que representa una notificación de bajo stock.
 * Se genera automáticamente cuando un producto cae por debajo de su umbral mínimo.
 */
@Entity
@Table(name = "tblNotificaciones", schema = "public")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Long id;

    @Column(name = "id_producto", nullable = false)
    private Integer idProducto;

    @Column(name = "nombre_producto", nullable = false)
    private String nombreProducto;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual;

    @Column(name = "umbral_minimo", nullable = false)
    private Integer umbralMinimo;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "eliminada", nullable = false)
    private Boolean eliminada = false;

    public Notificacion() {
    }

    public Notificacion(Integer idProducto, String nombreProducto, Integer stockActual, Integer umbralMinimo) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;
        this.umbralMinimo = umbralMinimo;
        this.fechaCreacion = LocalDateTime.now();
        this.eliminada = false;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getUmbralMinimo() {
        return umbralMinimo;
    }

    public void setUmbralMinimo(Integer umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Boolean getEliminada() {
        return eliminada;
    }

    public void setEliminada(Boolean eliminada) {
        this.eliminada = eliminada;
    }
}

