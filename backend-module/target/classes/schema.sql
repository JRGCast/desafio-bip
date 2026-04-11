-- ============================================================
-- Schema: Desafio BIP
-- Convenções: tabelas com prefixo tb_, colunas de valor com vl_
-- ============================================================

CREATE TABLE IF NOT EXISTS tb_beneficio (
  id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  nome      VARCHAR(100)  NOT NULL,
  descricao VARCHAR(255),
  vl_valor  DECIMAL(15,2) NOT NULL,
  ativo     BOOLEAN       DEFAULT TRUE,
  version   BIGINT        DEFAULT 0
);

CREATE TABLE IF NOT EXISTS tb_transacao (
  id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  from_id          BIGINT        NOT NULL,
  to_id            BIGINT        NOT NULL,
  vl_amount        DECIMAL(15,2) NOT NULL,
  from_nome        VARCHAR(100),
  to_nome          VARCHAR(100),
  vl_from_anterior DECIMAL(15,2),
  vl_to_anterior   DECIMAL(15,2),
  criado_em        TIMESTAMP     NOT NULL DEFAULT NOW(),
  status           VARCHAR(20)   NOT NULL
);
