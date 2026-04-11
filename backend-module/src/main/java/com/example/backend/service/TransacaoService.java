package com.example.backend.service;

import com.example.backend.dto.TransacaoResponseDTO;
import com.example.backend.entity.Transacao;
import com.example.backend.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;

    /** Extrato global: todas as transações, mais recentes primeiro. */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarTodas() {
        return transacaoRepository.findAllByOrderByCriadoEmDesc()
                .stream()
                .map(TransacaoResponseDTO::from)
                .toList();
    }

    /** Extrato de um benefício: transações onde ele é origem ou destino. */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDTO> listarPorBeneficio(Long beneficioId) {
        return transacaoRepository
                .findAllByFromIdOrToIdOrderByCriadoEmDesc(beneficioId, beneficioId)
                .stream()
                .map(TransacaoResponseDTO::from)
                .toList();
    }

    /** Recibo de uma transação específica. */
    @Transactional(readOnly = true)
    public TransacaoResponseDTO buscarPorId(Long id) {
        Transacao t = transacaoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Transação não encontrada com ID: " + id));
        return TransacaoResponseDTO.from(t);
    }
}
