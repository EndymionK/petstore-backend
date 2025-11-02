# Changelog

Todas las mejoras notables de este proyecto serán documentadas en este archivo.

## [1.1.0] - 2025-11-02

### ✨ Agregado
- **GlobalExceptionHandler**: Manejo centralizado de excepciones con respuestas HTTP apropiadas
  - Manejo de `BadCredentialsException` (401 Unauthorized)
  - Manejo de `UsernameNotFoundException` (404 Not Found)
  - Manejo de `LockedException` (423 Locked)
  - Manejo de `MethodArgumentNotValidException` (400 Bad Request)
  - Catch-all para excepciones no previstas (500 Internal Server Error)
  
- **Validación de DTOs**: Implementación de Bean Validation
  - Anotaciones `@NotBlank` y `@Size` en `LoginRequest`
  - Validación automática con `@Valid` en controladores
  - Mensajes de error personalizados en español
  
- **Configuración Segura**: Variables de entorno para credenciales
  - Credenciales de base de datos desde variables de entorno
  - Clave secreta JWT configurable (Base64)
  - Tiempo de expiración JWT configurable
  - Archivo `env.example` como plantilla
  - `.gitignore` actualizado para proteger archivos `.env`
  
- **JWT Mejorado**: Configuración dinámica
  - Clave secreta cargada desde `application.properties`
  - Fallback a clave generada si no está configurada
  - Mejor manejo de errores en decodificación
  
- **Documentación Completa**: README.md con instrucciones detalladas
  - Instrucciones de instalación paso a paso
  - Ejemplos de uso de endpoints
  - Estructura del proyecto documentada
  - Guía de configuración de base de datos
  - Información de seguridad y testing

### 🔧 Mejorado
- `AuthController`: Mejor documentación y uso de `@Valid`
- `LoginRequest`: Validaciones de negocio agregadas
- `JwtService`: Carga dinámica de configuración
- `application.properties`: Valores por defecto y comentarios
- Código en general: Mejor limpieza y comentarios

### 🐛 Corregido
- Importaciones duplicadas en `PetstoreBackendApplication.java`

### 📦 Dependencias
- Agregada: `spring-boot-starter-validation` para Bean Validation

---

## [1.0.0] - Versión Inicial

### ✨ Características Iniciales
- Spring Boot 3.5.5 con Java 21
- PostgreSQL via Supabase
- Autenticación JWT
- Spring Security configurado
- Endpoint de login (`/api/auth/login`)
- Endpoint protegido de inventario (`/api/inventory/test`)
- Bloqueo automático después de 3 intentos fallidos
- Tests unitarios básicos
- Mapeo JPA con entidades
- BCrypt para encoding de contraseñas

---

## Notas de Versionado

- **Major** (X.0.0): Cambios incompatibles con versiones anteriores
- **Minor** (0.X.0): Nuevas funcionalidades compatibles
- **Patch** (0.0.X): Correcciones de bugs compatibles

