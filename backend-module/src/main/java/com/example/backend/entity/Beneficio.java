package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "tb_beneficio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 255)
    private String descricao;

    @Column(name = "vl_valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    @Builder.Default
    private Boolean ativo = true;

    /**
     * Optimistic locking: o JPA inclui AND version = ? em cada UPDATE.
     * Se outro processo já modificou a linha, 0 rows affected →
     * OptimisticLockingFailureException → rollback automático.
     */
    @Version
    private Long version;
}
