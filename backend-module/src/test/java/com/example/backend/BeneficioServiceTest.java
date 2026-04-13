package com.example.backend;

import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.dto.TransacaoResponseDTO;
import com.example.backend.dto.TransferenciaRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.repository.TransacaoRepository;
import com.example.backend.service.BeneficioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BeneficioService — Testes Unitários")
class BeneficioServiceTest {

    @Mock private BeneficioRepository beneficioRepository;
    @Mock private TransacaoRepository transacaoRepository;
    @InjectMocks private BeneficioService beneficioService;

    private Beneficio beneficioA;
    private Beneficio beneficioB;

    @BeforeEach
    void setup() {
        beneficioA = Beneficio.builder()
                .id(1L).nome("Beneficio A").valor(new BigDecimal("1000.00")).ativo(true).version(0L).build();
        beneficioB = Beneficio.builder()
                .id(2L).nome("Beneficio B").valor(new BigDecimal("500.00")).ativo(true).version(0L).build();
    }

    // ─── CRUD ───────────────────────────────────────────────

    @Test
    @DisplayName("buscarPorId — lança exceção se não encontrado")
    void buscarPorId_naoEncontrado() {
        when(beneficioRepository.findByIdAndAtivoTrue(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> beneficioService.buscarPorId(99L))
                .isInstanceOf(BeneficioNotFoundException.class);
    }

    @Test
    @DisplayName("criar — persiste e retorna DTO")
    void criar_sucesso() {
        BeneficioRequestDTO req = new BeneficioRequestDTO("Novo", "Desc", new BigDecimal("200.00"));
        Beneficio saved = Beneficio.builder().id(3L).nome("Novo").descricao("Desc")
                .valor(new BigDecimal("200.00")).ativo(true).version(0L).build();
        when(beneficioRepository.save(any())).thenReturn(saved);

        var result = beneficioService.criar(req);

        assertThat(result.id()).isEqualTo(3L);
        assertThat(result.nome()).isEqualTo("Novo");
    }

    @Test
    @DisplayName("desativar — seta ativo=false")
    void desativar_sucesso() {
        when(beneficioRepository.findByIdAndAtivoTrue(1L)).thenReturn(Optional.of(beneficioA));
        when(beneficioRepository.save(any())).thenReturn(beneficioA);

        beneficioService.desativar(1L);

        assertThat(beneficioA.getAtivo()).isFalse();
        verify(beneficioRepository).save(beneficioA);
    }

    @Test
    @DisplayName("alterarStatus — UPDATE direto via @Query, retorna void")
    void alterarStatus_sucesso() {
        when(beneficioRepository.updateAtivo(1L, false)).thenReturn(1);

        beneficioService.alterarStatus(1L, false);

        verify(beneficioRepository).updateAtivo(1L, false);
    }

    @Test
    @DisplayName("alterarStatus — lança exceção se ID não encontrado")
    void alterarStatus_naoEncontrado() {
        when(beneficioRepository.updateAtivo(99L, false)).thenReturn(0);

        assertThatThrownBy(() -> beneficioService.alterarStatus(99L, false))
                .isInstanceOf(BeneficioNotFoundException.class);
    }

    // ─── TRANSFERÊNCIA ───────────────────────────────────────

    @Test
    @DisplayName("transferir — sucesso: valores atualizados e recibo salvo")
    void transferir_sucesso() {
        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(beneficioRepository.findById(2L)).thenReturn(Optional.of(beneficioB));
        when(beneficioRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("300.00"));
        TransacaoResponseDTO result = beneficioService.transferir(req);

        assertThat(beneficioA.getValor()).isEqualByComparingTo("700.00");
        assertThat(beneficioB.getValor()).isEqualByComparingTo("800.00");
        assertThat(result.amount()).isEqualByComparingTo("300.00");
        assertThat(result.fromValorAnterior()).isEqualByComparingTo("1000.00");
    }

    @Test
    @DisplayName("transferir — usa saveAll (batch) para optimistic locking")
    void transferir_saveAllChamado() {
        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(beneficioRepository.findById(2L)).thenReturn(Optional.of(beneficioB));
        when(beneficioRepository.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));
        when(transacaoRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("300.00"));
        beneficioService.transferir(req);

        verify(beneficioRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("transferir — saldo insuficiente lança SaldoInsuficienteException")
    void transferir_saldoInsuficiente() {
        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(beneficioRepository.findById(2L)).thenReturn(Optional.of(beneficioB));

        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("9999.00"));
        assertThatThrownBy(() -> beneficioService.transferir(req))
                .isInstanceOf(SaldoInsuficienteException.class)
                .hasMessageContaining("insuficiente");
    }

    @Test
    @DisplayName("transferir — IDs iguais lança IllegalArgumentException")
    void transferir_idsIguais() {
        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 1L, new BigDecimal("100.00"));
        assertThatThrownBy(() -> beneficioService.transferir(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("diferentes");
    }

    @Test
    @DisplayName("transferir — valor negativo lança IllegalArgumentException")
    void transferir_valorNegativo() {
        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("-50.00"));
        assertThatThrownBy(() -> beneficioService.transferir(req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("positivo");
    }

    @Test
    @DisplayName("transferir — origem não encontrada lança BeneficioNotFoundException")
    void transferir_origemNaoEncontrada() {
        when(beneficioRepository.findById(99L)).thenReturn(Optional.empty());
        TransferenciaRequestDTO req = new TransferenciaRequestDTO(99L, 2L, new BigDecimal("100.00"));
        assertThatThrownBy(() -> beneficioService.transferir(req))
                .isInstanceOf(BeneficioNotFoundException.class);
    }

    @Test
    @DisplayName("transferir — OptimisticLockingFailureException quando versão stale")
    void transferencia_versaoStale() {
        beneficioA.setVersion(5L);
        beneficioB.setVersion(5L);

        when(beneficioRepository.findById(1L)).thenReturn(Optional.of(beneficioA));
        when(beneficioRepository.findById(2L)).thenReturn(Optional.of(beneficioB));
        when(beneficioRepository.saveAll(anyList()))
                .thenThrow(new ObjectOptimisticLockingFailureException("Stale version", new RuntimeException()));

        TransferenciaRequestDTO req = new TransferenciaRequestDTO(1L, 2L, new BigDecimal("100.00"));
        assertThatThrownBy(() -> beneficioService.transferir(req))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class);
    }
}