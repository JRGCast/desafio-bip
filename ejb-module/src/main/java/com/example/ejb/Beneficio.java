package com.example.ejb;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade JPA do EJB module.
 * O campo {@code version} ativa o optimistic locking automaticamente:
 * o JPA inclui {@code AND version = ?} em cada UPDATE.
 */
@Entity
@Table(name = "tb_beneficio")
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
    private Boolean ativo = true;

    @Version
    private Long version;

    public Long getId()             { return id; }
    public String getNome()         { return nome; }
    public String getDescricao()    { return descricao; }
    public BigDecimal getValor()    { return valor; }
    public Boolean getAtivo()       { return ativo; }
    public Long getVersion()        { return version; }

    public void setId(Long id)              { this.id = id; }
    public void setNome(String nome)        { this.nome = nome; }
    public void setDescricao(String d)      { this.descricao = d; }
    public void setValor(BigDecimal valor)  { this.valor = valor; }
    public void setAtivo(Boolean ativo)     { this.ativo = ativo; }
    public void setVersion(Long version)    { this.version = version; }
}
