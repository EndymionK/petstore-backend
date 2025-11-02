package com.petstoreweb.petstore_backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Manejador global de excepciones para toda la aplicación.
 * Intercepta las excepciones y las convierte en respuestas HTTP apropiadas.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones de credenciales inválidas (login fallido).
     * HU-6.1-CA01: No revelamos si el usuario existe para mantener la confidencialidad del inventario.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse(
                "CREDENCIALES_INVALIDAS",
                "Verifique usuario y contraseña",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja excepciones de usuario no encontrado.
     * HU-6.1-CA01: No revelamos si el usuario existe para mantener la confidencialidad del inventario.
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse(
                "CREDENCIALES_INVALIDAS",
                "Verifique usuario y contraseña",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Maneja excepciones de cuenta bloqueada (múltiples intentos fallidos).
     */
    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ErrorResponse> handleLockedException(LockedException ex) {
        ErrorResponse error = new ErrorResponse(
                "CUENTA_BLOQUEADA",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.LOCKED).body(error);
    }

    /**
     * Maneja excepciones de producto duplicado (CA04).
     */
    @ExceptionHandler(ProductoDuplicadoException.class)
    public ResponseEntity<ErrorResponse> handleProductoDuplicadoException(ProductoDuplicadoException ex) {
        ErrorResponse error = new ErrorResponse(
                "PRODUCTO_DUPLICADO",
                ex.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Maneja errores de validación (@Valid fallido en DTOs).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        Map<String, Object> response = new HashMap<>();
        response.put("codigo", "VALIDACION_FALLIDA");
        response.put("errores", errors);
        response.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Maneja cualquier otra excepción no contemplada (catch-all).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse error = new ErrorResponse(
                "ERROR_INTERNO",
                "Ha ocurrido un error inesperado. Por favor, contacte al administrador.",
                LocalDateTime.now()
        );
        
        // En producción, no deberíamos exponer el mensaje completo de la excepción
        // Aquí lo incluimos para facilitar el debugging durante el desarrollo
        System.err.println("Error no manejado: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Clase interna para representar respuestas de error de forma estructurada.
     */
    public static class ErrorResponse {
        private String codigo;
        private String mensaje;
        private LocalDateTime timestamp;

        public ErrorResponse(String codigo, String mensaje, LocalDateTime timestamp) {
            this.codigo = codigo;
            this.mensaje = mensaje;
            this.timestamp = timestamp;
        }

        // Getters y Setters
        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getMensaje() {
            return mensaje;
        }

        public void setMensaje(String mensaje) {
            this.mensaje = mensaje;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }
}

