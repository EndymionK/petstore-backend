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

    @Min(value = 0, message = "El umbral mínimo debe ser mayor o igual a 0")
    private Integer umbralMinimo;

    @Size(max = 500, message = "La URL de imagen no debe exceder los 500 caracteres")
    private String imagen;

    @Size(max = 1000, message = "La descripción no debe exceder los 1000 caracteres")
    private String descripcion;

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

    public Integer getUmbralMinimo() {
        return umbralMinimo;
    }

    public void setUmbralMinimo(Integer umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}

