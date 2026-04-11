package com.example.backend.exception;

public class BeneficioNotFoundException extends RuntimeException {

    public BeneficioNotFoundException(Long id) {
        super("Benefício não encontrado com ID: " + id);
    }
}
