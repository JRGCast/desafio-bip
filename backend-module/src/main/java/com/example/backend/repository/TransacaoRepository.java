package com.example.backend.repository;

import com.example.backend.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    /** Todas as transações onde o benefício é origem ou destino, mais recentes primeiro. */
    List<Transacao> findAllByFromIdOrToIdOrderByCriadoEmDesc(Long fromId, Long toId);

    /** Extrato global — mais recentes primeiro. */
    List<Transacao> findAllByOrderByCriadoEmDesc();
}
