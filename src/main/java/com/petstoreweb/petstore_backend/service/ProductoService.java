package com.petstoreweb.petstore_backend.service;

import com.petstoreweb.petstore_backend.dto.ActualizarStockRequest;
import com.petstoreweb.petstore_backend.dto.ActualizarUmbralRequest;
import com.petstoreweb.petstore_backend.dto.CrearProductoRequest;
import com.petstoreweb.petstore_backend.dto.ProductoResponse;
import com.petstoreweb.petstore_backend.entity.Producto;
import com.petstoreweb.petstore_backend.entity.Proveedor;
import com.petstoreweb.petstore_backend.exception.ProductoDuplicadoException;
import com.petstoreweb.petstore_backend.repository.ProductoRepository;
import com.petstoreweb.petstore_backend.repository.ProveedorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);

    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final NotificacionService notificacionService;

    public ProductoService(ProductoRepository productoRepository, ProveedorRepository proveedorRepository, 
                           NotificacionService notificacionService) {
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.notificacionService = notificacionService;
    }

    @Transactional
    public ProductoResponse crearProducto(CrearProductoRequest request) {
        logger.info("📦 Intentando crear producto: {} del proveedor ID: {}", request.getNombre(), request.getIdProveedor());
        
        // Verificar si el producto ya existe (CA04)
        productoRepository.findByNombreAndProveedor_IdAndActivoTrue(request.getNombre(), request.getIdProveedor())
                .ifPresent(producto -> {
                    logger.warn("⚠️ Producto duplicado detectado: {} del proveedor ID: {}", request.getNombre(), request.getIdProveedor());
                    throw new ProductoDuplicadoException(
                            "Producto duplicado: ya existe un producto con ese nombre y proveedor."
                    );
                });

        // Obtener el proveedor
        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado"));

        // Crear el producto (el código se genera automáticamente)
        Producto producto = new Producto();
        producto.setNombre(request.getNombre());
        producto.setStock(request.getCantidad());
        producto.setPrecio(request.getPrecio());
        producto.setProveedor(proveedor);
        producto.setUmbralMinimo(request.getUmbralMinimo());
        producto.setImagen(request.getImagen());
        producto.setDescripcion(request.getDescripcion());

        // Guardar y retornar
        Producto productoGuardado = productoRepository.save(producto);
        
        logger.info("✅ Producto creado exitosamente: {} (Código: {}, Stock: {}, Precio: ${})", 
                    productoGuardado.getNombre(), 
                    productoGuardado.getCodigo(), 
                    productoGuardado.getStock(), 
                    productoGuardado.getPrecio());
        
        return convertirAProductoResponse(productoGuardado);
    }

    public List<ProductoResponse> obtenerTodosLosProductos() {
        return productoRepository.findByActivoTrue().stream()
                .map(producto -> convertirAProductoResponse(producto))
                .collect(Collectors.toList());
    }

    /**
     * Convierte una entidad Producto a un DTO ProductoResponse, incluyendo
     * la información de stock bajo basado en el umbral mínimo.
     */
    private ProductoResponse convertirAProductoResponse(Producto producto) {
        ProductoResponse response = new ProductoResponse(
                producto.getCodigo(),
                producto.getNombre(),
                producto.getStock(),
                producto.getPrecio(),
                producto.getProveedor().getNombre(),
                producto.getUmbralMinimo(),
                producto.tieneStockBajo() // CA02 y CA03: marca automáticamente como stock bajo
        );
        response.setImagen(producto.getImagen());
        response.setDescripcion(producto.getDescripcion());
        return response;
    }

    @Transactional
    public void eliminarProducto(Integer codigo) {
        logger.info("🗑️ Intentando eliminar producto con código: {}", codigo);
        
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        String nombreProducto = producto.getNombre();
        producto.setActivo(false);
        productoRepository.save(producto);
        
        logger.info("✅ Producto eliminado: {} (Código: {})", nombreProducto, codigo);
    }

    @Transactional
    public ProductoResponse aumentarStock(Integer codigo, ActualizarStockRequest request) {
        logger.info("📈 Aumentando stock del producto código: {} en {} unidades", codigo, request.getCantidad());
        
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        int stockAnterior = producto.getStock();
        int nuevoStock = producto.getStock() + request.getCantidad();
        producto.setStock(nuevoStock);
        
        Producto productoActualizado = productoRepository.save(producto);
        
        logger.info("✅ Stock aumentado: {} - Stock anterior: {}, Stock nuevo: {}", 
                    producto.getNombre(), stockAnterior, nuevoStock);
        
        // HU-4.2-CA04: Si el stock se repuso por encima del umbral, eliminar notificaciones
        if (!productoActualizado.tieneStockBajo()) {
            notificacionService.eliminarNotificacionesDeProducto(codigo);
            logger.info("🔔 Notificaciones eliminadas para producto código: {} (stock recuperado)", codigo);
        }
        
        return convertirAProductoResponse(productoActualizado);
    }

    @Transactional
    public ProductoResponse disminuirStock(Integer codigo, ActualizarStockRequest request) {
        logger.info("📉 Disminuyendo stock del producto código: {} en {} unidades", codigo, request.getCantidad());
        
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        int stockAnterior = producto.getStock();
        int nuevoStock = producto.getStock() - request.getCantidad();
        
        // Validar que el nuevo stock no sea negativo
        if (nuevoStock < 0) {
            logger.warn("⚠️ Stock insuficiente para producto código: {} - Stock actual: {}, Intentó disminuir: {}", 
                       codigo, producto.getStock(), request.getCantidad());
            throw new IllegalArgumentException("No hay suficiente stock disponible. Stock actual: " + producto.getStock());
        }
        
        producto.setStock(nuevoStock);
        
        Producto productoActualizado = productoRepository.save(producto);
        
        logger.info("✅ Stock disminuido: {} - Stock anterior: {}, Stock nuevo: {}", 
                    producto.getNombre(), stockAnterior, nuevoStock);
        
        // HU-4.2-CA01 y CA04: Verificar si hay stock bajo y generar notificación si es necesario
        notificacionService.verificarYGenerarNotificacion(productoActualizado);
        
        if (productoActualizado.tieneStockBajo()) {
            logger.warn("⚠️ ALERTA: Producto {} (código: {}) tiene stock bajo. Stock actual: {}, Umbral: {}", 
                       productoActualizado.getNombre(), codigo, nuevoStock, productoActualizado.getUmbralMinimo());
        }
        
        return convertirAProductoResponse(productoActualizado);
    }

    @Transactional
    public ProductoResponse actualizarUmbralMinimo(Integer codigo, ActualizarUmbralRequest request) {
        logger.info("🎯 Actualizando umbral mínimo del producto código: {} a {} unidades", codigo, request.getUmbralMinimo());
        
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        int umbralAnterior = producto.getUmbralMinimo();
        producto.setUmbralMinimo(request.getUmbralMinimo());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        logger.info("✅ Umbral actualizado: {} - Umbral anterior: {}, Umbral nuevo: {}", 
                    producto.getNombre(), umbralAnterior, request.getUmbralMinimo());
        
        return convertirAProductoResponse(productoActualizado);
    }

    public List<ProductoResponse> obtenerProductosConStockBajo() {
        return productoRepository.findByActivoTrue().stream()
                .filter(Producto::tieneStockBajo) // CA02: Filtrar solo productos con stock bajo
                .map(producto -> convertirAProductoResponse(producto))
                .collect(Collectors.toList());
    }
}

