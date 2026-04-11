package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record TransferenciaRequestDTO(
        @NotNull(message = "ID de origem é obrigatório")
        Long fromId,

        @NotNull(message = "ID de destino é obrigatório")
        Long toId,

        @NotNull(message = "Valor é obrigatório")
        @DecimalMin(value = "0.01", message = "Valor de transferência deve ser maior que zero")
        BigDecimal amount
) {}
