package com.petstoreweb.petstore_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para las credenciales de inicio de sesión.
 * Incluye validaciones para asegurar que los campos sean proporcionados.
 */
public class LoginRequest {
    
    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;
    
    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres")
    private String password;

    // Getters y Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}