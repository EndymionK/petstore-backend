package com.petstoreweb.petstore_backend.controller;

import com.petstoreweb.petstore_backend.dto.AuthResponse;
import com.petstoreweb.petstore_backend.dto.LoginRequest;
import com.petstoreweb.petstore_backend.service.AuthService;
import com.petstoreweb.petstore_backend.service.SesionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final SesionService sesionService;

    public AuthController(AuthService authService, SesionService sesionService) {
        this.authService = authService;
        this.sesionService = sesionService;
    }

    /**
     * Endpoint para iniciar sesión. Valida las credenciales y retorna un JWT.
     * 
     * @param loginRequest Credenciales del usuario (username y password)
     * @return Respuesta con el token JWT o un error apropiado
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        // Las excepciones son manejadas automáticamente por el GlobalExceptionHandler
        String token = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    /**
     * Endpoint para cerrar sesión. Invalida el token JWT actual.
     * 
     * @param request Request HTTP que contiene el token en el header
     * @return Respuesta de confirmación de cierre de sesión
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            sesionService.cerrarSesion(token);
        }
        
        return ResponseEntity.ok().body("{\"mensaje\":\"Sesión cerrada exitosamente\"}");
    }
}
