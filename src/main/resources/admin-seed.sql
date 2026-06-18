-- ====================================================
-- MIGRAÇÃO: Separação de autenticação Cliente / Admin
-- Execute este script uma única vez no PostgreSQL
-- após subir a aplicação (o Hibernate cria as colunas)
-- ====================================================

-- As colunas abaixo são criadas automaticamente pelo Hibernate
-- ao subir a aplicação com ddl-auto=update.
-- Caso prefira criar manualmente antes de subir:
ALTER TABLE tb_cliente
    ADD COLUMN IF NOT EXISTS senha         VARCHAR(255),
    ADD COLUMN IF NOT EXISTS cpf           VARCHAR(20),
    ADD COLUMN IF NOT EXISTS primeiro_acesso BOOLEAN DEFAULT TRUE;

-- Garantir que clientes existentes (sem senha ainda) fiquem com primeiro_acesso=true
UPDATE tb_cliente
SET primeiro_acesso = TRUE
WHERE primeiro_acesso IS NULL;

-- ====================================================
-- TESTE: Criar um cliente de exemplo com senha
-- (já pronto para logar na plataforma)
-- ====================================================
INSERT INTO tb_cliente (nome_empresa, nome_responsavel, email, telefone, plano, status, data_cadastro, cpf, senha, primeiro_acesso)
VALUES ('Empresa Exemplo', 'Maria Silva', 'maria@exemplo.com', '(11) 99999-0000', 'PROFISSIONAL', 'ATIVO', CURRENT_DATE, '123.456.789-00', 'senha123', TRUE)
ON CONFLICT (email) DO NOTHING;

-- ====================================================
-- FLUXO ESPERADO após rodar este script:
--
-- 1. Suba a aplicação normalmente
-- 2. Acesse /login com:
--      E-mail: maria@exemplo.com
--      Senha:  senha123
-- 3. Sistema detecta primeiro_acesso=true
--    → Redireciona para /trocar-senha
-- 4. Cliente define nova senha
--    → primeiro_acesso vira false no banco
--    → Acesso liberado ao /dashboard
-- ====================================================
