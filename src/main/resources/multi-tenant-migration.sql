-- Script de migração para atribuir todos os registros órfãos ao Cliente "Administrador" ou ao primeiro cliente cadastrado (ID = 1).
-- Execute este script no seu banco de dados DEPOIS que o Spring Boot subir e criar as colunas `cliente_id` nas tabelas.

UPDATE atendimentos SET cliente_id = 1 WHERE cliente_id IS NULL;
UPDATE despesas SET cliente_id = 1 WHERE cliente_id IS NULL;
UPDATE profissionais SET cliente_id = 1 WHERE cliente_id IS NULL;
UPDATE servicos_tabela SET cliente_id = 1 WHERE cliente_id IS NULL;
UPDATE atendimento_diario_total SET cliente_id = 1 WHERE cliente_id IS NULL;

-- OPCIONAL: Para garantir que as constraints "unique" globais antigas não atrapalhem:
-- Você pode precisar dropar as constraints unique de `profissionais.nome` e `servicos_tabela.tag` manualmente
-- Exemplo no PostgreSQL:
-- ALTER TABLE profissionais DROP CONSTRAINT uk_nome_profissional;
-- ALTER TABLE servicos_tabela DROP CONSTRAINT uk_tag_servico;
