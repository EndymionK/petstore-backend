package com.petstoreweb.petstore_backend.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret.key:}")
    private String secretKeyString;

    @Value("${jwt.expiration.time:3600000}")
    private long expirationTime;

    /**
     * Obtiene la clave secreta para firmar los tokens JWT.
     * Si no hay clave configurada en application.properties, genera una nueva.
     */
    private Key getSigningKey() {
        if (secretKeyString == null || secretKeyString.isEmpty()) {
            // Generar una nueva clave si no está configurada (NO RECOMENDADO en producción)
            System.out.println("⚠️  ADVERTENCIA: No se ha configurado JWT_SECRET_KEY. Usando clave temporal.");
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
        
        try {
            // Decodificar la clave Base64
            byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception e) {
            System.err.println("Error al decodificar la clave JWT: " + e.getMessage());
            // Fallback a clave generada
            return Keys.secretKeyFor(SignatureAlgorithm.HS256);
        }
    }

    /**
     * Genera un token JWT para un usuario autenticado.
     */
    public String generateToken(Authentication authentication) {
        // Obtenemos los detalles del usuario que ha iniciado sesión.
        User userPrincipal = (User) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        // Construimos el token con el nombre de usuario, fecha de emisión,
        // fecha de expiración y lo firmamos con nuestra clave secreta.
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Valida si un token es correcto y le pertenece al usuario.
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Verifica si el token ha expirado.
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}