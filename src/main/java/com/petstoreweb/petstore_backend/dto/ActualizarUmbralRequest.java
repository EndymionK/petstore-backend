package com.petstoreweb.petstore_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO para actualizar el umbral mínimo de un producto.
 * Incluye validaciones para asegurar que la cantidad sea válida.
 */
public class ActualizarUmbralRequest {

    @NotNull(message = "El umbral mínimo es obligatorio")
    @Min(value = 0, message = "El umbral mínimo no puede ser negativo")
    private Integer umbralMinimo;

    public ActualizarUmbralRequest() {
    }

    public ActualizarUmbralRequest(Integer umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }

    public Integer getUmbralMinimo() {
        return umbralMinimo;
    }

    public void setUmbralMinimo(Integer umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }
}

