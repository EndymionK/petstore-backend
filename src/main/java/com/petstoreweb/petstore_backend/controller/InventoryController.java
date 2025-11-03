package com.petstoreweb.petstore_backend.controller;

import com.petstoreweb.petstore_backend.dto.ActualizarStockRequest;
import com.petstoreweb.petstore_backend.dto.ActualizarUmbralRequest;
import com.petstoreweb.petstore_backend.dto.CrearProductoRequest;
import com.petstoreweb.petstore_backend.dto.ProductoResponse;
import com.petstoreweb.petstore_backend.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final ProductoService productoService;

    public InventoryController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> testAdminAccess() {
        // Este método solo será accesible si el usuario tiene el rol 'ADMIN'
        // y presenta un token JWT válido.
        return ResponseEntity.ok("¡Acceso concedido al inventario! Eres un ADMIN.");
    }

    @PostMapping("/productos")
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody CrearProductoRequest request) {
        ProductoResponse producto = productoService.crearProducto(request);
        return ResponseEntity.status(201).body(producto);
    }

    @GetMapping("/productos")
    public ResponseEntity<List<ProductoResponse>> obtenerTodosLosProductos() {
        List<ProductoResponse> productos = productoService.obtenerTodosLosProductos();
        return ResponseEntity.ok(productos);
    }

    @DeleteMapping("/productos/{codigo}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Integer codigo) {
        productoService.eliminarProducto(codigo);
        return ResponseEntity.ok().body("{\"mensaje\":\"Producto eliminado exitosamente\"}");
    }

    @PostMapping("/productos/{codigo}/aumentar-stock")
    public ResponseEntity<ProductoResponse> aumentarStock(@PathVariable Integer codigo, 
                                                          @Valid @RequestBody ActualizarStockRequest request) {
        ProductoResponse producto = productoService.aumentarStock(codigo, request);
        return ResponseEntity.ok(producto);
    }

    @PostMapping("/productos/{codigo}/disminuir-stock")
    public ResponseEntity<ProductoResponse> disminuirStock(@PathVariable Integer codigo, 
                                                           @Valid @RequestBody ActualizarStockRequest request) {
        ProductoResponse producto = productoService.disminuirStock(codigo, request);
        return ResponseEntity.ok(producto);
    }

    @PutMapping("/productos/{codigo}/umbral-minimo")
    public ResponseEntity<ProductoResponse> actualizarUmbralMinimo(@PathVariable Integer codigo, 
                                                                     @Valid @RequestBody ActualizarUmbralRequest request) {
        ProductoResponse producto = productoService.actualizarUmbralMinimo(codigo, request);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/productos/stock-bajo")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosConStockBajo() {
        List<ProductoResponse> productos = productoService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(productos);
    }
}
