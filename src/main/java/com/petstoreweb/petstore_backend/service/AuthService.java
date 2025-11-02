package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.dto.LoginRequest;
import com.petstoreweb.petstore_backend.entity.Usuario;
import com.petstoreweb.petstore_backend.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final SesionService sesionService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, 
                       SesionService sesionService, UsuarioRepository usuarioRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.sesionService = sesionService;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public String authenticateUser(LoginRequest loginRequest) {
        // 1. Autenticar al usuario
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Generar el token JWT
        String token = jwtService.generateToken(authentication);

        // 3. Registrar o actualizar la sesión del usuario
        // Obtener el ID del usuario desde la base de datos
        User userPrincipal = (User) authentication.getPrincipal();
        Usuario usuario = usuarioRepository.findByNombre(userPrincipal.getUsername())
                .orElseThrow(() -> new IllegalStateException("Usuario no encontrado después de autenticación"));

        // Crear o actualizar la sesión (esto desactivará sesiones anteriores del usuario)
        sesionService.crearOActualizarSesion(usuario.getId(), token);

        return token;
    }
}