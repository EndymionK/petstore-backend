package com.petstoreweb.petstore_backend.security;

import com.petstoreweb.petstore_backend.service.JwtService;
import com.petstoreweb.petstore_backend.service.SesionService;
import com.petstoreweb.petstore_backend.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final SesionService sesionService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsServiceImpl userDetailsService, 
                                   SesionService sesionService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.sesionService = sesionService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 1. Si no hay token o no empieza con "Bearer ", pasamos al siguiente filtro.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraemos el token (quitando el prefijo "Bearer ").
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // 3. Validar que la sesión sea activa antes de continuar (HU-6.3-CA01 y CA02)
        if (!sesionService.esSesionValida(jwt)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("{\"codigo\":\"SESION_INVALIDA\",\"mensaje\":\"La sesión ha expirado o no es válida\"}");
            return;
        }

        // 4. Si tenemos un usuario y aún no está autenticado en el contexto de seguridad...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 5. Si el token JWT es válido...
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Actualizar la última actividad de la sesión (HU-6.3-CA02)
                sesionService.actualizarActividad(jwt);
                
                // Creamos un objeto de autenticación y lo guardamos en el contexto.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}