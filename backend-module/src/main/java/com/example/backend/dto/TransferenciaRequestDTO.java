package com.example.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferenciaRequestDTO {

    @NotNull(message = "ID de origem é obrigatório")
    private Long fromId;

    @NotNull(message = "ID de destino é obrigatório")
    private Long toId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor de transferência deve ser maior que zero")
    private BigDecimal amount;
}
