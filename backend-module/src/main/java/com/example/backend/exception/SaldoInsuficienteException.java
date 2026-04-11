package com.example.backend.exception;

import java.math.BigDecimal;

public class SaldoInsuficienteException extends RuntimeException {

    public SaldoInsuficienteException(Long id, BigDecimal saldoAtual, BigDecimal valorRequerido) {
        super(String.format(
                "Benefício %d possui saldo R$ %.2f, insuficiente para transferir R$ %.2f",
                id, saldoAtual, valorRequerido));
    }
}
