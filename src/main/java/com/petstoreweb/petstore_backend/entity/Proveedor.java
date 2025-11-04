package com.petstoreweb.petstore_backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "\"tblProveedores\"", schema = "public")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer id;

    @Column(name = "nombre_proveedor", nullable = false, length = 50)
    private String nombre;

    @Column(name = "telefono_proveedor", nullable = false, length = 50)
    private String telefono;

    @Column(name = "email_proveedor", nullable = false, length = 50)
    private String email;

    public Proveedor() {
    }

    public Proveedor(String nombre, String telefono, String email) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.email = email;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

