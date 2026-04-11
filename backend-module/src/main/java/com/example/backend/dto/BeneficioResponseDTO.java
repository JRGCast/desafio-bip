package com.example.backend.dto;

import com.example.backend.entity.Beneficio;

import java.math.BigDecimal;

public record BeneficioResponseDTO(
        Long id,
        String nome,
        String descricao,
        BigDecimal valor,
        Boolean ativo,
        Long version
) {
    public static BeneficioResponseDTO from(Beneficio b) {
        return new BeneficioResponseDTO(
                b.getId(),
                b.getNome(),
                b.getDescricao(),
                b.getValor(),
                b.getAtivo(),
                b.getVersion()
        );
    }
}
