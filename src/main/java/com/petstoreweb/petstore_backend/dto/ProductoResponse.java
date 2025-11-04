package com.petstoreweb.petstore_backend.dto;

import java.math.BigDecimal;

public class ProductoResponse {
    private Integer codigo;
    private String nombre;
    private Integer stock;
    private BigDecimal precio;
    private String proveedorNombre;
    private Integer umbralMinimo;
    private Boolean stockBajo;
    private String imagen;
    private String descripcion;

    public ProductoResponse() {
    }

    public ProductoResponse(Integer codigo, String nombre, Integer stock, BigDecimal precio, String proveedorNombre) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.proveedorNombre = proveedorNombre;
    }

    public ProductoResponse(Integer codigo, String nombre, Integer stock, BigDecimal precio, String proveedorNombre, Integer umbralMinimo, Boolean stockBajo) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.proveedorNombre = proveedorNombre;
        this.umbralMinimo = umbralMinimo;
        this.stockBajo = stockBajo;
    }

    public ProductoResponse(Integer codigo, String nombre, Integer stock, BigDecimal precio, String proveedorNombre, Integer umbralMinimo, Boolean stockBajo, String imagen, String descripcion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.proveedorNombre = proveedorNombre;
        this.umbralMinimo = umbralMinimo;
        this.stockBajo = stockBajo;
        this.imagen = imagen;
        this.descripcion = descripcion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public Integer getUmbralMinimo() {
        return umbralMinimo;
    }

    public void setUmbralMinimo(Integer umbralMinimo) {
        this.umbralMinimo = umbralMinimo;
    }

    public Boolean getStockBajo() {
        return stockBajo;
    }

    public void setStockBajo(Boolean stockBajo) {
        this.stockBajo = stockBajo;
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

