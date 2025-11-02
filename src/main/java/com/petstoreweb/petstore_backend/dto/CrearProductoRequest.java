package com.petstoreweb.petstore_backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class CrearProductoRequest {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 30, message = "El nombre no debe exceder los 30 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$", message = "El nombre debe contener solo letras")
    private String nombre;

    @NotNull(message = "La cantidad inicial es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    private Integer cantidad;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    private BigDecimal precio;

    @NotNull(message = "El proveedor es obligatorio")
    private Integer idProveedor;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }
}

