package com.example.backend.service;

import com.example.backend.dto.BeneficioRequestDTO;
import com.example.backend.dto.BeneficioResponseDTO;
import com.example.backend.dto.TransacaoResponseDTO;
import com.example.backend.dto.TransferenciaRequestDTO;
import com.example.backend.entity.Beneficio;
import com.example.backend.entity.StatusTransacao;
import com.example.backend.entity.Transacao;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.exception.SaldoInsuficienteException;
import com.example.backend.repository.BeneficioRepository;
import com.example.backend.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final TransacaoRepository transacaoRepository;

    // ─────────────────── CRUD ───────────────────

    @Transactional(readOnly = true)
    public List<BeneficioResponseDTO> listar() {
        return beneficioRepository.findAllByAtivoTrue()
                .stream()
                .map(BeneficioResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public BeneficioResponseDTO buscarPorId(Long id) {
        Beneficio b = beneficioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        return BeneficioResponseDTO.from(b);
    }

    @Transactional
    public BeneficioResponseDTO criar(BeneficioRequestDTO dto) {
        Beneficio novo = Beneficio.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .valor(dto.valor())
                .ativo(true)
                .build();
        return BeneficioResponseDTO.from(beneficioRepository.save(novo));
    }

    @Transactional
    public BeneficioResponseDTO atualizar(Long id, BeneficioRequestDTO dto) {
        Beneficio b = beneficioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        b.setNome(dto.nome());
        b.setDescricao(dto.descricao());
        b.setValor(dto.valor());
        return BeneficioResponseDTO.from(beneficioRepository.save(b));
    }

    @Transactional
    public void desativar(Long id) {
        Beneficio b = beneficioRepository.findByIdAndAtivoTrue(id)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
        b.setAtivo(false);
        beneficioRepository.save(b);
    }

    /**
     * Altera o status {@code ativo} via UPDATE direto, sem carregar a entidade.
     *
     * <p>Bypass intencional do {@code @Version}: idempotente e de baixo risco.
     * Retorna o DTO atualizado buscando apenas o necessário para confirmar a operação.
     *
     * @throws BeneficioNotFoundException se nenhuma linha for afetada (ID inexistente)
     */
    @Transactional
    public void alterarStatus(Long id, Boolean ativo) {
        int rowsAffected = beneficioRepository.updateAtivo(id, ativo);
        if (rowsAffected == 0) {
            throw new BeneficioNotFoundException(id);
        }
    }

    // ─────────────────── TRANSFERÊNCIA ───────────────────

    /**
     * Transfere valor entre dois benefícios com optimistic locking.
     *
     * <p>Fluxo:
     * <ol>
     *   <li>Valida entrada (amount positivo, IDs distintos).</li>
     *   <li>Carrega os dois benefícios do banco.</li>
     *   <li>Verifica saldo suficiente.</li>
     *   <li>Subtrai e adiciona os valores.</li>
     *   <li>Persiste via {@code save()} — o JPA executa
     *       {@code UPDATE ... WHERE id=? AND version=?}.
     *       Se version não bater → {@code OptimisticLockingFailureException} → rollback.</li>
     *   <li>Registra recibo imutável na tabela tb_transacao.</li>
     * </ol>
     */
    @Transactional
    public TransacaoResponseDTO transferir(TransferenciaRequestDTO dto) {
        // 1. Validações de entrada
        if (dto.amount() == null || dto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo");
        }
        if (dto.fromId().equals(dto.toId())) {
            throw new IllegalArgumentException("Benefício de origem e destino devem ser diferentes");
        }

        // 2. Busca — @Version ativo na entidade; sem lock explícito
        Beneficio from = beneficioRepository.findById(dto.fromId())
                .orElseThrow(() -> new BeneficioNotFoundException(dto.fromId()));
        Beneficio to = beneficioRepository.findById(dto.toId())
                .orElseThrow(() -> new BeneficioNotFoundException(dto.toId()));

        // 3. Validação de saldo
        if (from.getValor().compareTo(dto.amount()) < 0) {
            throw new SaldoInsuficienteException(from.getId(), from.getValor(), dto.amount());
        }

        // 4. Snapshots para o recibo
        BigDecimal fromAnterior = from.getValor();
        BigDecimal toAnterior   = to.getValor();

        // 5. Transferência
        from.setValor(from.getValor().subtract(dto.amount()));
        to.setValor(to.getValor().add(dto.amount()));

        // 6. Persist com optimistic locking:
        //    UPDATE tb_beneficio SET vl_valor=?, version=? WHERE id=? AND version=?
        //    Se version não bater → OptimisticLockingFailureException → 409
        beneficioRepository.saveAll(Arrays.asList(from, to));

        // 7. Registrar recibo imutável (criado_em atribuído pelo banco via DEFAULT NOW())
        Transacao recibo = Transacao.builder()
                .fromId(from.getId())
                .toId(to.getId())
                .amount(dto.amount())
                .fromNome(from.getNome())
                .toNome(to.getNome())
                .fromValorAnterior(fromAnterior)
                .toValorAnterior(toAnterior)
                .status(StatusTransacao.SUCESSO)
                .build();
        Transacao salvo = transacaoRepository.save(recibo);

        return TransacaoResponseDTO.from(salvo);
    }
}
