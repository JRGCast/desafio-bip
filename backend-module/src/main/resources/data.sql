INSERT INTO tb_beneficio (nome, descricao, vl_valor, ativo) 
SELECT 'Beneficio A', 'Descrição A', 1000.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_beneficio WHERE nome = 'Beneficio A');

INSERT INTO tb_beneficio (nome, descricao, vl_valor, ativo) 
SELECT 'Beneficio B', 'Descrição B', 500.00, TRUE
WHERE NOT EXISTS (SELECT 1 FROM tb_beneficio WHERE nome = 'Beneficio B');