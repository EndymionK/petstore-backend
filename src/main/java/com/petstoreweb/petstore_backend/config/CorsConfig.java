package com.petstoreweb.petstore_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir todos los patrones de Vercel y localhost
        // IMPORTANTE: Usar solo setAllowedOriginPatterns para que funcione con setAllowCredentials(true)
        configuration.setAllowedOriginPatterns(Arrays.asList(
            "https://paw-home-inventory-system.vercel.app",
            "https://*.vercel.app",  // Todas las previews de Vercel
            "http://localhost:*",     // Todos los puertos de localhost
            "http://127.0.0.1:*"      // Alternativa de localhost
        ));
        
        // Métodos HTTP permitidos
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Headers permitidos
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // Permitir credenciales (cookies, authorization headers)
        configuration.setAllowCredentials(true);
        
        // Exponer headers de respuesta
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Tiempo de cache de configuración CORS (1 hora)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
