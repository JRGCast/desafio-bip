package com.example.ejb;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.math.BigDecimal;

/**
 * Serviço EJB responsável pela transferência de valores entre Benefícios.
 *
 * <p>Correções aplicadas em relação ao código original com bug:
 * <ul>
 *   <li>Validação de entrada: amount positivo, IDs não nulos e distintos</li>
 *   <li>Validação de existência: verifica que os benefícios existem no banco</li>
 *   <li>Validação de saldo: impede saldo negativo na origem</li>
 *   <li>Optimistic locking via {@code @Version}: o JPA inclui {@code AND version = ?}
 *       em cada UPDATE, detectando conflito concorrente automaticamente e lançando
 *       {@link jakarta.persistence.OptimisticLockException} em caso de colisão.</li>
 *   <li>Transação explicitada com {@code REQUIRED}: garante rollback automático.</li>
 * </ul>
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BeneficioEjbService {

    @PersistenceContext
    private EntityManager em;

    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        // 1. Validações de entrada
        if (fromId == null || toId == null) {
            throw new IllegalArgumentException("IDs de origem e destino são obrigatórios");
        }
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Benefício de origem e destino devem ser diferentes");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da transferência deve ser positivo");
        }

        // 2. Busca as entidades (com @Version — optimistic locking ativo)
        Beneficio from = em.find(Beneficio.class, fromId);
        Beneficio to   = em.find(Beneficio.class, toId);

        if (from == null) {
            throw new IllegalArgumentException("Benefício de origem não encontrado: " + fromId);
        }
        if (to == null) {
            throw new IllegalArgumentException("Benefício de destino não encontrado: " + toId);
        }

        // 3. Validação de saldo
        if (from.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException(
                String.format("Saldo insuficiente: benefício %d possui R$ %.2f, mas a transferência requer R$ %.2f",
                    fromId, from.getValor(), amount)
            );
        }

        // 4. Transferência
        from.setValor(from.getValor().subtract(amount));
        to.setValor(to.getValor().add(amount));

        // 5. Merge — o JPA executa:
        //    UPDATE tb_beneficio SET vl_valor=?, version=version+1 WHERE id=? AND version=?
        //    Se version não bater → OptimisticLockException → rollback automático
        em.merge(from);
        em.merge(to);
    }
}
