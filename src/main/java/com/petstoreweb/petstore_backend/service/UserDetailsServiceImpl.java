package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.entity.Usuario;
import com.petstoreweb.petstore_backend.repository.UsuarioRepository;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final LoginAttemptService loginAttemptService; // <-- 1. Inyecta el servicio

    // 2. Actualiza el constructor
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository, LoginAttemptService loginAttemptService) {
        this.usuarioRepository = usuarioRepository;
        this.loginAttemptService = loginAttemptService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 3. ¡AÑADE ESTA VERIFICACIÓN ANTES DE TODO!
        if (loginAttemptService.isBlocked(username)) {
            // Si el usuario está bloqueado, lanzamos una excepción especial.
            throw new LockedException("La cuenta del usuario " + username + " está bloqueada.");
        }

        // El resto del método sigue igual
        Usuario usuario = usuarioRepository.findByNombre(username)
                .orElseThrow(() -> new UsernameNotFoundException("No se encontró el usuario: " + username));

        // DEBUG: Log para verificar rol en producción
        System.out.println("🔍 DEBUG - Usuario: " + usuario.getNombre());
        System.out.println("🔍 DEBUG - Rol desde BD: [" + usuario.getRol() + "]");
        System.out.println("🔍 DEBUG - Rol procesado: ROLE_" + usuario.getRol().toUpperCase());

        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toUpperCase()));
        
        System.out.println("🔍 DEBUG - Authorities: " + authorities);

        return new User(usuario.getNombre(), usuario.getPassword(), authorities);
    }
}