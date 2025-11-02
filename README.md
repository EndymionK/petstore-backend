# PetStore Backend API

Backend REST API para PetStoreWeb V2, desarrollado con Spring Boot, PostgreSQL y JWT Authentication.

## 🚀 Tecnologías

- **Java 21**
- **Spring Boot 3.5.5**
- **PostgreSQL** (via Supabase)
- **Spring Security** con JWT
- **JPA/Hibernate**
- **Maven**
- **Lombok**

## 📋 Características

- ✅ Autenticación JWT (JSON Web Tokens)
- ✅ Autorización por roles (ADMIN, USER, etc.)
- ✅ Control de sesiones único por usuario (máximo 1 sesión concurrente)
- ✅ Expiración automática por inactividad (15 minutos configurables)
- ✅ Bloqueo automático después de 3 intentos fallidos
- ✅ Validación de datos con Bean Validation
- ✅ Manejo global de excepciones
- ✅ Configuración segura con variables de entorno
- ✅ Tests unitarios con JUnit y Mockito

## 🔧 Instalación y Configuración

### Requisitos Previos

- Java 21 o superior
- Maven 3.6+ o superior
- PostgreSQL (o cuenta de Supabase)
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Pasos de Instalación

1. **Clonar el repositorio:**
   ```bash
   git clone <url-del-repositorio>
   cd petstore-backend
   ```

2. **Configurar Variables de Entorno:**
   
   Copia el archivo `env.example` y créalo como `.env` en la raíz del proyecto:
   ```bash
   # Windows
   copy env.example .env
   
   # Linux/Mac
   cp env.example .env
   ```
   
   Edita el archivo `.env` con tus credenciales:
   ```properties
   DB_URL=jdbc:postgresql://tu-host:5432/tu-base-de-datos
   DB_USERNAME=tu-usuario
   DB_PASSWORD=tu-contraseña
   
   # Genera una clave secreta segura para JWT:
   # openssl rand -base64 64
   JWT_SECRET_KEY=tu-clave-secreta-generada
   
   JWT_EXPIRATION_TIME=3600000
   SESSION_INACTIVITY_TIMEOUT=900000
   JPA_SHOW_SQL=true
   ```

3. **Compilar el proyecto:**
   ```bash
   mvn clean install
   ```

4. **Ejecutar la aplicación:**
   ```bash
   mvn spring-boot:run
   ```
   
   La aplicación estará disponible en: `http://localhost:8080`

## 🔐 Endpoints

### Autenticación

#### POST `/api/auth/login`
Inicia sesión y obtiene un token JWT.

**Request:**
```json
{
  "username": "admin",
  "password": "password123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Campos inválidos o vacíos
- `401 UNAUTHORIZED`: Credenciales incorrectas (HU-6.1-CA01: "Verifique usuario y contraseña")
- `423 LOCKED`: Cuenta bloqueada por múltiples intentos fallidos

#### POST `/api/auth/logout`
Cierra la sesión activa y invalida el token JWT actual.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "mensaje": "Sesión cerrada exitosamente"
}
```

**Nota:** Cuando un usuario inicia sesión en otro dispositivo/navegador, su sesión anterior se invalida automáticamente (HU-6.3-CA01).

### Inventario (Protegido - Requiere rol ADMIN)

#### GET `/api/inventory/test`
Endpoint de prueba para usuarios con rol ADMIN.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
"¡Acceso concedido al inventario! Eres un ADMIN."
```

#### POST `/api/inventory/productos`
Crea un nuevo producto en el inventario.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Request:**
```json
{
  "nombre": "Croquetas Premium",
  "cantidad": 100,
  "precio": 29.99,
  "idProveedor": 1
}
```

**Response (201 CREATED):**
```json
{
  "codigo": 1,
  "nombre": "Croquetas Premium",
  "stock": 100,
  "precio": 29.99,
  "proveedor": "PetFood Supply",
  "umbralMinimo": null,
  "stockBajo": false
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Datos inválidos o producto duplicado
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: No tienes permisos de ADMIN

#### GET `/api/inventory/productos`
Obtiene todos los productos activos del inventario.

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "codigo": 1,
    "nombre": "Croquetas Premium",
    "stock": 100,
    "precio": 29.99,
    "proveedor": "PetFood Supply",
    "umbralMinimo": null,
    "stockBajo": false
  },
  {
    "codigo": 2,
    "nombre": "Juguetes para Gatos",
    "stock": 50,
    "precio": 15.50,
    "proveedor": "PetToys Inc",
    "umbralMinimo": null,
    "stockBajo": false
  }
]
```

#### DELETE `/api/inventory/productos/{codigo}`
Elimina lógicamente un producto del inventario (HU-2.2).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
{
  "mensaje": "Producto eliminado exitosamente"
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Producto no encontrado o ya eliminado
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: No tienes permisos de ADMIN

**Nota:** El producto se elimina lógicamente (soft delete), permaneciendo en la base de datos con `activo = false` para mantener el historial (HU-2.2-CA04).

#### POST `/api/inventory/productos/{codigo}/aumentar-stock`
Aumenta el stock de un producto existente (HU-3.1).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Request:**
```json
{
  "cantidad": 50
}
```

**Response (200 OK):**
```json
{
  "codigo": 1,
  "nombre": "Croquetas Premium",
  "stock": 150,
  "precio": 29.99,
  "proveedor": "PetFood Supply",
  "umbralMinimo": 10,
  "stockBajo": false
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Cantidad inválida (debe ser mayor a 0) o producto no encontrado
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: No tienes permisos de ADMIN

#### POST `/api/inventory/productos/{codigo}/disminuir-stock`
Disminuye el stock de un producto existente (HU-3.1).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Request:**
```json
{
  "cantidad": 25
}
```

**Response (200 OK):**
```json
{
  "codigo": 1,
  "nombre": "Croquetas Premium",
  "stock": 125,
  "precio": 29.99,
  "proveedor": "PetFood Supply",
  "umbralMinimo": 10,
  "stockBajo": false
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Cantidad inválida, producto no encontrado o stock insuficiente
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: No tienes permisos de ADMIN

**Nota:** El stock se actualiza en tiempo real y se refleja inmediatamente en el listado del inventario (HU-3.1-CA03, CA04). Si intentas disminuir más stock del disponible, recibirás un error 400 con el stock actual disponible.

#### PUT `/api/inventory/productos/{codigo}/umbral-minimo`
Define o actualiza el umbral mínimo de existencias para un producto (HU-4.1-CA01).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

**Request:**
```json
{
  "umbralMinimo": 10
}
```

**Response (200 OK):**
```json
{
  "codigo": 1,
  "nombre": "Croquetas Premium",
  "stock": 100,
  "precio": 29.99,
  "proveedor": "PetFood Supply",
  "umbralMinimo": 10,
  "stockBajo": false
}
```

**Errores posibles:**
- `400 BAD_REQUEST`: Umbral inválido (debe ser >= 0) o producto no encontrado
- `401 UNAUTHORIZED`: Token inválido o expirado
- `403 FORBIDDEN`: No tienes permisos de ADMIN

#### GET `/api/inventory/productos/stock-bajo`
Obtiene todos los productos con stock bajo según su umbral mínimo configurado (HU-4.1-CA02).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "codigo": 1,
    "nombre": "Croquetas Premium",
    "stock": 8,
    "precio": 29.99,
    "proveedor": "PetFood Supply",
    "umbralMinimo": 10,
    "stockBajo": true
  },
  {
    "codigo": 2,
    "nombre": "Juguetes para Gatos",
    "stock": 5,
    "precio": 15.50,
    "proveedor": "PetToys Inc",
    "umbralMinimo": 10,
    "stockBajo": true
  }
]
```

**Nota:** 
- Solo se listan productos que tienen `umbralMinimo` configurado Y cuyo `stock <= umbralMinimo` (HU-4.1-CA02, CA03).
- La detección de stock bajo se actualiza automáticamente en tiempo real cada vez que se registra una venta o reposición (HU-4.1-CA04).

#### GET `/api/inventory/notificaciones`
Obtiene todas las notificaciones activas de bajo stock generadas automáticamente por el sistema (HU-4.2).

**Headers:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "idProducto": 1,
    "nombreProducto": "Croquetas Premium",
    "stockActual": 5,
    "umbralMinimo": 10,
    "fechaCreacion": "2025-11-02T14:30:00",
    "eliminada": false
  },
  {
    "id": 2,
    "idProducto": 2,
    "nombreProducto": "Juguetes para Gatos",
    "stockActual": 8,
    "umbralMinimo": 15,
    "fechaCreacion": "2025-11-02T14:25:00",
    "eliminada": false
  }
]
```

**Nota:**
- Las notificaciones se generan automáticamente cuando un producto cae por debajo de su umbral (HU-4.2-CA01, CA02).
- Las notificaciones son internas y se muestran al administrador dentro de la aplicación (HU-4.2-CA03).
- Cuando se repone stock por encima del umbral, las notificaciones del producto se marcan como eliminadas automáticamente (HU-4.2-CA04).
- Solo se devuelven notificaciones que no han sido eliminadas.

## 📁 Estructura del Proyecto

```
petstore-backend/
├── src/
│   ├── main/
│   │   ├── java/com/petstoreweb/petstore_backend/
│   │   │   ├── config/              # Configuraciones (Security, etc.)
│   │   │   ├── controller/          # Controladores REST
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── entity/              # Entidades JPA
│   │   │   ├── exception/           # Manejo de excepciones global
│   │   │   ├── repository/          # Repositorios JPA
│   │   │   ├── security/            # Filtros y listeners de seguridad
│   │   │   ├── service/             # Lógica de negocio
│   │   │   └── PetstoreBackendApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/                        # Tests unitarios
├── env.example                      # Plantilla de variables de entorno
├── .gitignore                       # Archivos ignorados por Git
├── pom.xml                          # Configuración Maven
└── README.md                        # Esta documentación
```

## 🛡️ Seguridad

### Características Implementadas

1. **JWT Authentication**: Tokens firmados y con expiración
2. **Role-Based Access Control (RBAC)**: Control de acceso por roles
3. **Session Management**: 
   - Máximo 1 sesión concurrente por usuario (HU-6.3-CA01)
   - Expiración automática por inactividad (15 minutos configurables) (HU-6.3-CA02)
   - Inicio de sesión en otro dispositivo invalida la sesión anterior
   - Tarea programada para limpiar sesiones expiradas
4. **Account Lockout**: Bloqueo automático después de 3 intentos fallidos
5. **Password Encoding**: Contraseñas hasheadas con BCrypt
6. **Global Exception Handling**: Manejo centralizado de errores
7. **Input Validation**: Validación de datos de entrada
8. **Confidentiality Protection (HU-6.1)**: 
   - Mensajes de error seguros que no revelan si un usuario existe
   - Respuesta genérica: "Verifique usuario y contraseña" para todos los errores de autenticación

### Roles Disponibles

- `ROLE_ADMIN`: Acceso completo al sistema
- `ROLE_USER`: Usuario estándar

## 🧪 Testing

Ejecutar todos los tests:
```bash
mvn test
```

Ejecutar un test específico:
```bash
mvn test -Dtest=AuthServiceTest
```

## 📊 Base de Datos

> **⚠️ IMPORTANTE:** Ejecutar el script `database_cleanup_script.sql` para crear el esquema completo con todas las relaciones correctas.

### Tabla: `tblUsuarios`

```sql
CREATE TABLE tblUsuarios (
    id_usuario INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_usuario VARCHAR(255) NOT NULL,
    usuario_pass VARCHAR(255) NOT NULL,  -- Debe estar hasheada con BCrypt
    rol_usuario VARCHAR(50) NOT NULL,    -- 'ADMIN', 'USER', etc.
    estado BOOLEAN NOT NULL DEFAULT true,
    estado_integrante BOOLEAN NOT NULL DEFAULT true,
    failed_attempts INTEGER DEFAULT 0,
    account_locked BOOLEAN DEFAULT false,
    lock_time TIMESTAMP WITH TIME ZONE
);
```

### Tabla: `tblProveedores`

```sql
CREATE TABLE tblProveedores (
    id_proveedor INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_proveedor VARCHAR(255) NOT NULL,
    telefono_proveedor VARCHAR(20) NOT NULL,
    email_proveedor VARCHAR(255) NOT NULL
);
```

### Tabla: `tblProductos`

```sql
CREATE TABLE tblProductos (
    codigo_producto INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nombre_producto VARCHAR(30) NOT NULL,
    stock_producto INTEGER NOT NULL,
    precio_producto NUMERIC(10,2) NOT NULL,
    id_proveedor INTEGER NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    umbral_minimo INTEGER,
    CONSTRAINT tblProductos_id_proveedor_fkey FOREIGN KEY (id_proveedor) 
        REFERENCES tblProveedores(id_proveedor) ON DELETE CASCADE
);
```

**Nota:** La columna `umbral_minimo` es opcional (NULL). Si está configurada, el sistema detecta automáticamente cuando el stock está por debajo del umbral (HU-4.1).

### Tabla: `tblSesiones`

```sql
CREATE TABLE tblSesiones (
    id_sesion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    token VARCHAR(500),
    fecha_inicio TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    ultima_actividad TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT true,
    CONSTRAINT tblSesiones_id_usuario_fkey FOREIGN KEY (id_usuario) 
        REFERENCES tblUsuarios(id_usuario) ON DELETE CASCADE
);
```

**Nota:** Control de sesiones únicas por usuario (HU-6.3).

### Tabla: `tblNotificaciones`

```sql
CREATE TABLE tblNotificaciones (
    id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_producto INTEGER NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    stock_actual INTEGER NOT NULL,
    umbral_minimo INTEGER NOT NULL,
    fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
    eliminada BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT tblNotificaciones_id_producto_fkey FOREIGN KEY (id_producto) 
        REFERENCES tblProductos(codigo_producto) ON DELETE CASCADE
);
```

**Nota:** Notificaciones automáticas de bajo stock (HU-4.2). Se eliminan lógicamente cuando el stock se repone.

### Tabla: `tblVentas` (Futuras implementaciones)

```sql
CREATE TABLE tblVentas (
    id_venta INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    fecha_venta TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    id_usuario INTEGER NOT NULL,
    CONSTRAINT tblVentas_id_usuario_fkey FOREIGN KEY (id_usuario) 
        REFERENCES tblUsuarios(id_usuario) ON DELETE RESTRICT
);
```

### Tabla: `tblReposiciones` (Futuras implementaciones)

```sql
CREATE TABLE tblReposiciones (
    id_reposicion INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    id_producto INTEGER NOT NULL,
    id_usuario INTEGER NOT NULL,
    fecha_reposicion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    cantidad_repuesta INTEGER NOT NULL,
    CONSTRAINT tblReposiciones_id_producto_fkey FOREIGN KEY (id_producto) 
        REFERENCES tblProductos(codigo_producto) ON DELETE RESTRICT,
    CONSTRAINT tblReposiciones_id_usuario_fkey FOREIGN KEY (id_usuario) 
        REFERENCES tblUsuarios(id_usuario) ON DELETE RESTRICT
);
```

### Hash de Contraseñas

Importante: Las contraseñas en la base de datos deben estar hasheadas con BCrypt.

Ejemplo:
```java
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashedPassword = encoder.encode("password123");
// Resultado: $2a$10$N9qo8uLOickgx2ZMRZoMye...
```

## 🔍 Debugging

Para ver logs SQL de Hibernate:
```properties
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

## 📝 Mejoras Recientes

### Versión 1.6.0

✅ **Notificaciones Automáticas (HU-4.2)**:
   - Generación automática de notificaciones al detectar bajo stock
   - Notificaciones internas para administradores
   - Eliminación automática de notificaciones al reponer stock
   - Persistencia de historial de notificaciones
   - Endpoint para consultar notificaciones activas

✅ **Detección de Umbral Mínimo (HU-4.1)**:
   - Configuración de umbral mínimo por producto
   - Detección automática de stock bajo
   - Actualización en tiempo real al cambiar stock
   - Listado de productos con stock bajo
   - Validación de umbral opcional

✅ **Actualización de Stock (HU-3.1)**:
   - Aumento de existencias por reposiciones
   - Disminución de existencias por ventas
   - Actualización en tiempo real del stock disponible
   - Validación de stock suficiente para disminuciones
   - Validación de cantidades positivas

✅ **Gestión de Inventario**:
   - Creación de productos (HU-2.1)
   - Consulta de productos activos
   - Eliminación lógica de productos (HU-2.2)
   - Soft delete para preservar historial (HU-2.2-CA04)
   - Validación de duplicados

### Versión 1.7.0 (Actual)

✅ **Confidencialidad del Inventario (HU-6.1)**: 
   - Mensajes de error seguros que no revelan si un usuario existe
   - Respuesta genérica: "Verifique usuario y contraseña" para todos los errores de autenticación
   - Protección contra enumeración de usuarios

### Versión 1.2.0

✅ **Sistema de Sesiones**: 
   - Máximo 1 sesión concurrente por usuario (HU-6.3-CA01)
   - Expiración automática por inactividad (HU-6.3-CA02)
   - Invalidación automática de sesiones anteriores
   - Endpoint de logout funcional
   - Tarea programada para limpiar sesiones expiradas

✅ **GlobalExceptionHandler**: Manejo centralizado de excepciones  
✅ **Bean Validation**: Validación automática de DTOs  
✅ **Variables de Entorno**: Configuración segura de credenciales  
✅ **JWT Configurable**: Clave secreta desde variables de entorno  
✅ **Mejor Documentación**: README completo y comentarios en código  

## 🚧 Próximas Mejoras

- [ ] Refresh tokens
- [ ] Registro de usuarios
- [ ] Recuperación de contraseña
- [ ] CORS configurado para frontend
- [ ] Rate limiting
- [ ] LoginAttemptService persistente (Redis)

## 👥 Contribuir

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo LICENSE.md para más detalles.

## 📧 Contacto

Para preguntas o soporte, por favor abre un issue en el repositorio.

---

**Desarrollado con ❤️ usando Spring Boot**

