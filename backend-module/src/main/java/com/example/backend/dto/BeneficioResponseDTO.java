package com.example.backend.dto;

import com.example.backend.entity.Beneficio;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficioResponseDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal valor;
    private Boolean ativo;
    private Long version;

    public static BeneficioResponseDTO from(Beneficio b) {
        return BeneficioResponseDTO.builder()
                .id(b.getId())
                .nome(b.getNome())
                .descricao(b.getDescricao())
                .valor(b.getValor())
                .ativo(b.getAtivo())
                .version(b.getVersion())
                .build();
    }
}
