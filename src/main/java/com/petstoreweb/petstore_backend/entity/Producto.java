package com.petstoreweb.petstore_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Entity
@Table(name = "tblProductos", schema = "public")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_producto")
    private Integer codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 30, message = "El nombre no debe exceder los 30 caracteres")
    @Pattern(regexp = "^[A-Za-zÁÉÍÓÚáéíóúÑñ\\s]+$", message = "El nombre debe contener solo letras")
    @Column(name = "nombre_producto", nullable = false)
    private String nombre;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 1, message = "La cantidad debe ser mayor a 0")
    @Column(name = "stock_producto", nullable = false)
    private Integer stock;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Column(name = "precio_producto", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @NotNull(message = "El proveedor es obligatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor", nullable = false)
    private Proveedor proveedor;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "umbral_minimo")
    private Integer umbralMinimo;

    @Column(name = "imagen_producto", length = 500)
    private String imagen;

    @Column(name = "descripcion_producto", columnDefinition = "TEXT")
    private String descripcion;

    public Producto() {
    }

    public Producto(String nombre, Integer stock, BigDecimal precio, Proveedor proveedor) {
        this.nombre = nombre;
        this.stock = stock;
        this.precio = precio;
        this.proveedor = proveedor;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    /**
     * Verifica si el producto tiene stock bajo según su umbral mínimo.
     * 
     * @return true si el stock está por debajo o igual al umbral, false en caso contrario
     */
    public boolean tieneStockBajo() {
        if (umbralMinimo == null) {
            return false; // CA03: Si no tiene umbral configurado, no aplicar alerta
        }
        return stock <= umbralMinimo;
    }
}

