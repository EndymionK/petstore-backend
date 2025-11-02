package com.petstoreweb.petstore_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el stock de un producto.
 * Incluye validaciones para asegurar que la cantidad sea válida.
 */
public class ActualizarStockRequest {

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    public ActualizarStockRequest() {
    }

    public ActualizarStockRequest(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

