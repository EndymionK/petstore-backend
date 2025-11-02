package com.petstoreweb.petstore_backend.dto;

import java.time.LocalDateTime;

/**
 * DTO para representar una notificación en las respuestas de la API.
 */
public class NotificacionResponse {
    private Long id;
    private Integer idProducto;
    private String nombreProducto;
    private Integer stockActual;
    private Integer umbralMinimo;
    private LocalDateTime fechaCreacion;
    private Boolean eliminada;

    public NotificacionResponse() {
    }

    public NotificacionResponse(Long id, Integer idProducto, String nombreProducto, 
                                Integer stockActual, Integer umbralMinimo, 
                                LocalDateTime fechaCreacion, Boolean eliminada) {
        this.id = id;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.stockActual = stockActual;
        this.umbralMinimo = umbralMinimo;
        this.fechaCreacion = fechaCreacion;
        this.eliminada = eliminada;
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

