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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoService {

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
        // Verificar si el producto ya existe (CA04)
        productoRepository.findByNombreAndProveedor_IdAndActivoTrue(request.getNombre(), request.getIdProveedor())
                .ifPresent(producto -> {
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
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public ProductoResponse aumentarStock(Integer codigo, ActualizarStockRequest request) {
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        int nuevoStock = producto.getStock() + request.getCantidad();
        producto.setStock(nuevoStock);
        
        Producto productoActualizado = productoRepository.save(producto);
        
        // HU-4.2-CA04: Si el stock se repuso por encima del umbral, eliminar notificaciones
        if (!productoActualizado.tieneStockBajo()) {
            notificacionService.eliminarNotificacionesDeProducto(codigo);
        }
        
        return convertirAProductoResponse(productoActualizado);
    }

    @Transactional
    public ProductoResponse disminuirStock(Integer codigo, ActualizarStockRequest request) {
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        int nuevoStock = producto.getStock() - request.getCantidad();
        
        // Validar que el nuevo stock no sea negativo
        if (nuevoStock < 0) {
            throw new IllegalArgumentException("No hay suficiente stock disponible. Stock actual: " + producto.getStock());
        }
        
        producto.setStock(nuevoStock);
        
        Producto productoActualizado = productoRepository.save(producto);
        
        // HU-4.2-CA01 y CA04: Verificar si hay stock bajo y generar notificación si es necesario
        notificacionService.verificarYGenerarNotificacion(productoActualizado);
        
        return convertirAProductoResponse(productoActualizado);
    }

    @Transactional
    public ProductoResponse actualizarUmbralMinimo(Integer codigo, ActualizarUmbralRequest request) {
        Producto producto = productoRepository.findByCodigoAndActivoTrue(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado o ya eliminado"));
        
        producto.setUmbralMinimo(request.getUmbralMinimo());
        
        Producto productoActualizado = productoRepository.save(producto);
        
        return convertirAProductoResponse(productoActualizado);
    }

    public List<ProductoResponse> obtenerProductosConStockBajo() {
        return productoRepository.findByActivoTrue().stream()
                .filter(Producto::tieneStockBajo) // CA02: Filtrar solo productos con stock bajo
                .map(producto -> convertirAProductoResponse(producto))
                .collect(Collectors.toList());
    }
}

