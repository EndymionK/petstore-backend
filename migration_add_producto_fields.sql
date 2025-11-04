-- ====================================================================
--   MIGRACIÓN: Agregar campos imagen y descripción a tblProductos
--   Fecha: 2025-01-XX
--   Descripción: Agrega columnas para soportar imagen y descripción/características del producto
-- ====================================================================

-- Agregar columna imagen_producto
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'tblproductos' 
        AND column_name = 'imagen_producto'
    ) THEN
        ALTER TABLE public.tblProductos 
        ADD COLUMN imagen_producto VARCHAR(500);
        
        RAISE NOTICE 'Columna imagen_producto agregada exitosamente';
    ELSE
        RAISE NOTICE 'Columna imagen_producto ya existe';
    END IF;
END $$;

-- Agregar columna descripcion_producto
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'tblproductos' 
        AND column_name = 'descripcion_producto'
    ) THEN
        ALTER TABLE public.tblProductos 
        ADD COLUMN descripcion_producto TEXT;
        
        RAISE NOTICE 'Columna descripcion_producto agregada exitosamente';
    ELSE
        RAISE NOTICE 'Columna descripcion_producto ya existe';
    END IF;
END $$;

-- Verificación final
SELECT 
    column_name, 
    data_type, 
    character_maximum_length
FROM information_schema.columns
WHERE table_schema = 'public' 
AND table_name = 'tblproductos'
AND column_name IN ('imagen_producto', 'descripcion_producto')
ORDER BY column_name;

