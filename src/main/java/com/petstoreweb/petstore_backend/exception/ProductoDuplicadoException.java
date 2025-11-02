package com.petstoreweb.petstore_backend.exception;

public class ProductoDuplicadoException extends RuntimeException {
    public ProductoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}

