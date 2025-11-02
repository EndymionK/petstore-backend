package com.petstoreweb.petstore_backend.repository;

import com.petstoreweb.petstore_backend.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    Optional<Producto> findByNombreAndProveedor_Id(String nombre, Integer proveedorId);
    Optional<Producto> findByNombreAndProveedor_IdAndActivoTrue(String nombre, Integer proveedorId);
    List<Producto> findByActivoTrue();
    Optional<Producto> findByCodigoAndActivoTrue(Integer codigo);
}

