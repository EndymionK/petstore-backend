package com.petstoreweb.petstore_backend.repository;

import com.petstoreweb.petstore_backend.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {
    Optional<Proveedor> findByNombre(String nombre);
}

