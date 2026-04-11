package com.example.backend.dto;

import com.example.backend.entity.StatusTransacao;
import com.example.backend.entity.Transacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransacaoResponseDTO(
        Long id,
        Long fromId,
        Long toId,
        String fromNome,
        String toNome,
        BigDecimal amount,
        BigDecimal fromValorAnterior,
        BigDecimal toValorAnterior,
        LocalDateTime criadoEm,
        StatusTransacao status
) {
    public static TransacaoResponseDTO from(Transacao t) {
        return new TransacaoResponseDTO(
                t.getId(),
                t.getFromId(),
                t.getToId(),
                t.getFromNome(),
                t.getToNome(),
                t.getAmount(),
                t.getFromValorAnterior(),
                t.getToValorAnterior(),
                t.getCriadoEm(),
                t.getStatus()
        );
    }
}
