package com.petstoreweb.petstore_backend.entity;

import jakarta.persistence.*; // Importante que sea de jakarta.persistence

/**
 * Esta clase es una entidad JPA que mapea a la tabla "tblUsuarios" en la base de datos.
 * Cada campo de la clase corresponde a una columna de la tabla.
 */
@Entity
@Table(name = "\"tblUsuarios\"", schema = "public")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "nombre_usuario", nullable = false)
    private String nombre;

    @Column(name = "usuario_pass", nullable = false)
    private String password;

    @Column(name = "rol_usuario", nullable = false)
    private String rol;

    @Column(name = "estado", nullable = true)
    private Boolean estado;



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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public Boolean isEstado() {
        return estado;
    }

    public void setEstado(Boolean estado) {
        this.estado = estado;
    }
}