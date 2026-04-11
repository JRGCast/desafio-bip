package com.example.backend.dto;

import com.example.backend.entity.StatusTransacao;
import com.example.backend.entity.Transacao;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoResponseDTO {

    private Long id;
    private Long fromId;
    private Long toId;
    private String fromNome;
    private String toNome;
    private BigDecimal amount;
    private BigDecimal fromValorAnterior;
    private BigDecimal toValorAnterior;
    private LocalDateTime criadoEm;
    private StatusTransacao status;

    public static TransacaoResponseDTO from(Transacao t) {
        return TransacaoResponseDTO.builder()
                .id(t.getId())
                .fromId(t.getFromId())
                .toId(t.getToId())
                .fromNome(t.getFromNome())
                .toNome(t.getToNome())
                .amount(t.getAmount())
                .fromValorAnterior(t.getFromValorAnterior())
                .toValorAnterior(t.getToValorAnterior())
                .criadoEm(t.getCriadoEm())
                .status(t.getStatus())
                .build();
    }
}
