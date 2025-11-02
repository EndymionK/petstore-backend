package com.petstoreweb.petstore_backend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad que mapea la tabla tblSesiones en la base de datos.
 * Representa una sesión activa de un usuario en el sistema.
 */
@Entity
@Table(name = "tblSesiones", schema = "public")
public class Sesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sesion")
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Column(name = "token", length = 500)
    private String token;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "ultima_actividad", nullable = false)
    private LocalDateTime ultimaActividad;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    // Constructores
    public Sesion() {
    }

    public Sesion(Integer idUsuario, String token) {
        this.idUsuario = idUsuario;
        this.token = token;
        this.fechaInicio = LocalDateTime.now();
        this.ultimaActividad = LocalDateTime.now();
        this.activo = true;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(LocalDateTime ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    /**
     * Marca la sesión como inactiva y actualiza la última actividad.
     */
    public void desactivar() {
        this.activo = false;
    }

    /**
     * Actualiza la última actividad de la sesión.
     */
    public void actualizarActividad() {
        this.ultimaActividad = LocalDateTime.now();
    }
}

