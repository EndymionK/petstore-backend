-- ============================================================================
-- SCRIPT DE LIMPIEZA Y CONSOLIDACIÓN DE BASE DE DATOS - PETSTORE BACKEND
-- VERSIÓN SEGURA (NO FALLA SI LAS TABLAS YA EXISTEN)
-- ============================================================================

-- PASO 1: ELIMINAR TABLAS DUPLICADAS Y OBJETOS OBSOLETOS
-- ============================================================================

DROP TABLE IF EXISTS public.tblnotificaciones CASCADE;
DROP TABLE IF EXISTS public.tblproductos CASCADE;
DROP TABLE IF EXISTS public.tblproveedores CASCADE;
DROP TABLE IF EXISTS public.tblsesiones CASCADE;

-- PASO 2: CREAR/ACTUALIZAR ESQUEMA CONSOLIDADO
-- ============================================================================

-- Tabla de Usuarios
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblusuarios') THEN
        CREATE TABLE public.tblUsuarios (
            id_usuario INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
            nombre_usuario VARCHAR(255) NOT NULL,
            usuario_pass VARCHAR(255) NOT NULL,
            rol_usuario VARCHAR(50) NOT NULL,
            estado BOOLEAN NOT NULL DEFAULT true,
            estado_integrante BOOLEAN NOT NULL DEFAULT true,
            failed_attempts INTEGER DEFAULT 0,
            account_locked BOOLEAN DEFAULT false,
            lock_time TIMESTAMP WITH TIME ZONE,
            CONSTRAINT tblUsuarios_pkey PRIMARY KEY (id_usuario)
        );
    END IF;
END $$;

-- Tabla de Proveedores
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblproveedores') THEN
        CREATE TABLE public.tblProveedores (
            id_proveedor INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
            nombre_proveedor VARCHAR(255) NOT NULL,
            telefono_proveedor VARCHAR(20) NOT NULL,
            email_proveedor VARCHAR(255) NOT NULL,
            CONSTRAINT tblProveedores_pkey PRIMARY KEY (id_proveedor)
        );
    END IF;
END $$;

-- Tabla de Productos
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblproductos') THEN
        CREATE TABLE public.tblProductos (
            codigo_producto INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
            nombre_producto VARCHAR(30) NOT NULL,
            stock_producto INTEGER NOT NULL,
            precio_producto NUMERIC(10,2) NOT NULL,
            id_proveedor INTEGER NOT NULL,
            activo BOOLEAN NOT NULL DEFAULT true,
            umbral_minimo INTEGER,
            CONSTRAINT tblProductos_pkey PRIMARY KEY (codigo_producto),
            CONSTRAINT tblProductos_id_proveedor_fkey FOREIGN KEY (id_proveedor) 
                REFERENCES public.tblProveedores(id_proveedor) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE
        );
    END IF;
END $$;

-- Tabla de Sesiones
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblsesiones') THEN
        CREATE TABLE public.tblSesiones (
            id_sesion BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
            id_usuario INTEGER NOT NULL,
            token VARCHAR(500),
            fecha_inicio TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
            ultima_actividad TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
            activo BOOLEAN NOT NULL DEFAULT true,
            CONSTRAINT tblSesiones_pkey PRIMARY KEY (id_sesion),
            CONSTRAINT tblSesiones_id_usuario_fkey FOREIGN KEY (id_usuario) 
                REFERENCES public.tblUsuarios(id_usuario) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE
        );
    END IF;
END $$;

-- Tabla de Notificaciones
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblnotificaciones') THEN
        CREATE TABLE public.tblNotificaciones (
            id_notificacion BIGINT GENERATED ALWAYS AS IDENTITY NOT NULL,
            id_producto INTEGER NOT NULL,
            nombre_producto VARCHAR(255) NOT NULL,
            stock_actual INTEGER NOT NULL,
            umbral_minimo INTEGER NOT NULL,
            fecha_creacion TIMESTAMP WITH TIME ZONE DEFAULT NOW() NOT NULL,
            eliminada BOOLEAN NOT NULL DEFAULT false,
            CONSTRAINT tblNotificaciones_pkey PRIMARY KEY (id_notificacion),
            CONSTRAINT tblNotificaciones_id_producto_fkey FOREIGN KEY (id_producto) 
                REFERENCES public.tblProductos(codigo_producto) 
                ON DELETE CASCADE 
                ON UPDATE CASCADE
        );
    END IF;
END $$;

-- Tabla de Ventas
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblventas') THEN
        CREATE TABLE public.tblVentas (
            id_venta INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
            fecha_venta TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
            id_usuario INTEGER NOT NULL,
            CONSTRAINT tblVentas_pkey PRIMARY KEY (id_venta),
            CONSTRAINT tblVentas_id_usuario_fkey FOREIGN KEY (id_usuario) 
                REFERENCES public.tblUsuarios(id_usuario) 
                ON DELETE RESTRICT 
                ON UPDATE CASCADE
        );
    END IF;
END $$;

-- Tabla de Reposiciones
DO $$ 
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_tables WHERE schemaname = 'public' AND tablename = 'tblreposiciones') THEN
        CREATE TABLE public.tblReposiciones (
            id_reposicion INTEGER GENERATED ALWAYS AS IDENTITY NOT NULL,
            id_producto INTEGER NOT NULL,
            id_usuario INTEGER NOT NULL,
            fecha_reposicion TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
            cantidad_repuesta INTEGER NOT NULL,
            CONSTRAINT tblReposiciones_pkey PRIMARY KEY (id_reposicion),
            CONSTRAINT tblReposiciones_id_producto_fkey FOREIGN KEY (id_producto) 
                REFERENCES public.tblProductos(codigo_producto) 
                ON DELETE RESTRICT 
                ON UPDATE CASCADE,
            CONSTRAINT tblReposiciones_id_usuario_fkey FOREIGN KEY (id_usuario) 
                REFERENCES public.tblUsuarios(id_usuario) 
                ON DELETE RESTRICT 
                ON UPDATE CASCADE
        );
    END IF;
END $$;

-- PASO 3: CREAR ÍNDICES (SEGURA PARA EJECUTAR MÚLTIPLES VECES)
-- ============================================================================

CREATE INDEX IF NOT EXISTS idx_tblUsuarios_nombre_usuario ON public.tblUsuarios(nombre_usuario);
CREATE INDEX IF NOT EXISTS idx_tblProductos_nombre ON public.tblProductos(nombre_producto);
CREATE INDEX IF NOT EXISTS idx_tblProductos_proveedor ON public.tblProductos(id_proveedor);
CREATE INDEX IF NOT EXISTS idx_tblProductos_activo ON public.tblProductos(activo);
CREATE INDEX IF NOT EXISTS idx_tblSesiones_usuario ON public.tblSesiones(id_usuario);
CREATE INDEX IF NOT EXISTS idx_tblSesiones_token ON public.tblSesiones(token) WHERE token IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_tblSesiones_activo ON public.tblSesiones(activo);
CREATE INDEX IF NOT EXISTS idx_tblNotificaciones_producto ON public.tblNotificaciones(id_producto);
CREATE INDEX IF NOT EXISTS idx_tblNotificaciones_eliminada ON public.tblNotificaciones(eliminada);
CREATE INDEX IF NOT EXISTS idx_tblVentas_usuario ON public.tblVentas(id_usuario);
CREATE INDEX IF NOT EXISTS idx_tblReposiciones_producto ON public.tblReposiciones(id_producto);

-- PASO 4: COMENTARIOS DE DOCUMENTACIÓN (SEGURO PARA EJECUTAR MÚLTIPLES VECES)
-- ============================================================================

COMMENT ON TABLE public.tblUsuarios IS 'Usuarios del sistema con autenticación y control de acceso';
COMMENT ON COLUMN public.tblUsuarios.usuario_pass IS 'Contraseña hasheada con BCrypt';
COMMENT ON COLUMN public.tblUsuarios.rol_usuario IS 'Rol del usuario: ADMIN o USER';
COMMENT ON COLUMN public.tblUsuarios.estado IS 'Estado general del usuario (activo/inactivo)';
COMMENT ON COLUMN public.tblUsuarios.account_locked IS 'Indica si la cuenta está bloqueada por intentos fallidos';

COMMENT ON TABLE public.tblProveedores IS 'Catálogo de proveedores de productos';
COMMENT ON TABLE public.tblProductos IS 'Productos del inventario con control de stock y umbrales';
COMMENT ON COLUMN public.tblProductos.activo IS 'Indica si el producto está activo (soft delete)';
COMMENT ON COLUMN public.tblProductos.umbral_minimo IS 'Umbral mínimo para generar alertas de bajo stock (HU-4.1)';

COMMENT ON TABLE public.tblSesiones IS 'Sesiones activas de usuarios (HU-6.3: una sesión por usuario)';
COMMENT ON COLUMN public.tblSesiones.activo IS 'Indica si la sesión está activa';
COMMENT ON COLUMN public.tblSesiones.ultima_actividad IS 'Última actividad para control de expiración';

COMMENT ON TABLE public.tblNotificaciones IS 'Notificaciones automáticas de bajo stock (HU-4.2)';
COMMENT ON COLUMN public.tblNotificaciones.eliminada IS 'Indica si la notificación fue eliminada (soft delete)';
COMMENT ON COLUMN public.tblNotificaciones.nombre_producto IS 'Nombre del producto al momento de generar la notificación (snapshot)';

COMMENT ON TABLE public.tblVentas IS 'Historial de ventas realizadas';
COMMENT ON TABLE public.tblReposiciones IS 'Historial de reposiciones de inventario';

-- ============================================================================
-- FIN DEL SCRIPT
-- ============================================================================

