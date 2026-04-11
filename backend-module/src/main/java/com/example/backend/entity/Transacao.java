package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_transacao")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_id", nullable = false)
    private Long fromId;

    @Column(name = "to_id", nullable = false)
    private Long toId;

    @Column(name = "vl_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** Snapshot do nome da origem no momento da transferência. */
    @Column(name = "from_nome")
    private String fromNome;

    /** Snapshot do nome do destino no momento da transferência. */
    @Column(name = "to_nome")
    private String toNome;

    @Column(name = "vl_from_anterior", precision = 15, scale = 2)
    private BigDecimal fromValorAnterior;

    @Column(name = "vl_to_anterior", precision = 15, scale = 2)
    private BigDecimal toValorAnterior;

    /**
     * Timestamp registrado pelo banco via DEFAULT NOW().
     * insertable = false: Java nunca envia o valor no INSERT.
     * updatable  = false: Java nunca sobrescreve em UPDATE.
     */
    @Column(name = "criado_em", nullable = false, insertable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private StatusTransacao status;
}
