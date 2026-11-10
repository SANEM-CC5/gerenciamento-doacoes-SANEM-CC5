-- =====================================================================
-- DML - Seed alinhado ao esquema real (MySQL 8.0)
-- Usa os nomes de tabelas/colunas efetivamente implementados.
<<<<<<< Updated upstream:docs/database/Seed Dados Tabelas - DML.sql
-- Seed executável equivalente: db/script-insert-values.sql
=======
-- O seed oficial da aplicação é executado pela migration:
-- backend/src/main/resources/db/seed/V2__seed_data.sql
>>>>>>> Stashed changes:docs/database/seed-data.sql
-- =====================================================================
USE sanem;

-- ==========================================
-- 1. PERFIS E USUÁRIOS
-- ==========================================
INSERT INTO profile (profile_id, name, description) VALUES
    (1, 'ADMINISTRATOR', 'Administrador do sistema'),
    (2, 'EMPLOYEE', 'Funcionário / atendente');

INSERT INTO app_user (user_id, name, login, email, password_hash, status, profile_id) VALUES
    (1, 'Carlos Silva', 'carlos.admin', 'carlos.admin@sanem.org', '$2b$10$ExemploHashSeguroAqui123', 'ACTIVE', 1),
    (2, 'Ana Souza', 'ana.func', 'ana.func@sanem.org', '$2b$10$ExemploHashSeguroAqui456', 'ACTIVE', 2);

-- ==========================================
-- 2. BENEFICIÁRIOS (diferentes status, incluindo INACTIVE)
-- ==========================================
INSERT INTO beneficiary (beneficiary_id, full_name, cpf, phone, socioeconomic_data, beneficiary_status, approver_user_id, withdrawal_limit) VALUES
    (1, 'Roberto Mendes', '11122233344', '45999991111', 'Desempregado há 6 meses, moradia alugada.', 'APPROVED', 1, 10),
    (2, 'Juliana Castro', '22233344455', '45988882222', 'Mãe solo de 5 filhos, trabalho informal.', 'APPROVED', 1, 15),
    (3, 'Fernando Gomes', '33344455566', '45977773333', 'Doença crônica na família.', 'PENDING', NULL, 10),
    (4, 'Marcos Antunes', '44455566677', '45966664444', 'Cadastro bloqueado por descumprimento das regras.', 'INACTIVE', 1, 10);

-- ==========================================
-- 3. CARTÕES (beneficiários aprovados)
-- ==========================================
INSERT INTO card (card_id, unique_number, beneficiary_id, issue_date) VALUES
    (1, 'CARD-0001', 1, '2026-03-21 08:00:00'),
    (2, 'CARD-0002', 2, '2026-03-26 08:00:00');

-- ==========================================
-- 4. CATEGORIAS E ITENS (ESTOQUE)
-- ==========================================
INSERT INTO category (category_id, name) VALUES
    (1, 'Roupas'), (2, 'Calçados'), (3, 'Utensílios Domésticos'), (4, 'Cama, Mesa e Banho');

INSERT INTO item (item_id, description, stock_quantity, tag_code) VALUES
    (1, 'Camiseta Básica M', 15, 'ITEM-0001'),
    (2, 'Calça Jeans 42', 5, 'ITEM-0002'),
    (3, 'Tênis Esportivo Infantil 32', 0, 'ITEM-0003'),
    (4, 'Prato Fundo de Vidro', 24, 'ITEM-0004'),
    (5, 'Cobertor Casal', 8, 'ITEM-0005');

-- ==========================================
-- 5. DOADORES E DOAÇÕES
-- ==========================================
INSERT INTO donor (donor_id, name, cpf_cnpj, contact, email) VALUES
    (1, 'Confecções Paraná Ltda', '99988877000199', '4535551234', 'contato@confeccoespr.com'),
    (2, 'Mariana Oliveira', '55544433322', '45999990000', 'mariana.oliveira@email.com');

INSERT INTO donation (donation_id, donation_date, receiver_user_id, donor_id) VALUES
    (1, '2026-03-28 10:00:00', 2, 1),
    (2, '2026-04-01 14:30:00', 2, 2);

INSERT INTO item_donated (item_donated_id, donation_id, item_id, quantity) VALUES
    (1, 1, 1, 20),
    (2, 1, 2, 5),
    (3, 2, 4, 30);

-- ==========================================
-- 6. RETIRADAS
-- ==========================================
INSERT INTO withdrawal (withdrawal_id, withdrawal_date, beneficiary_id, attendant_user_id) VALUES
    (1, '2026-04-03 11:00:00', 1, 2),
    (2, '2026-04-04 13:45:00', 2, 2);

INSERT INTO item_withdrawn (item_withdrawn_id, withdrawal_id, item_id, quantity) VALUES
    (1, 1, 1, 3),
    (2, 1, 4, 6),
    (3, 2, 1, 2),
    (4, 2, 5, 2);

-- ==========================================
-- 7. CONFIGURAÇÃO DE LIMITE
-- ==========================================
INSERT INTO withdrawal_limit_config (monthly_item_limit, is_active) VALUES (10, TRUE);

-- ==========================================
-- 8. PONTOS DE COLETA E SEDES (Medianeira - PR)
-- ==========================================
INSERT INTO collection_point
    (collection_point_id, name, type, street, number, neighborhood, city, state, zip_code, latitude, longitude, operating_hours, phone)
VALUES
    (1, 'Sede SANEM', 'ASSISTANCE_ENTITY_HQ', 'Avenida Brasil', '1500', 'Centro', 'Medianeira', 'PR', '85884-000', -25.295100, -54.093900, 'Seg-Sex 08:00-18:00', '4535550001'),
    (2, 'Ponto de Coleta Supermercado Central', 'PARTNER_COLLECTION_POINT', 'Rua Minas Gerais', '320', 'Centro', 'Medianeira', 'PR', '85884-010', -25.297800, -54.095400, 'Seg-Sab 08:00-20:00', '4535550002'),
    (3, 'Ponto de Coleta Paróquia São Miguel', 'PARTNER_COLLECTION_POINT', 'Rua Santa Catarina', '745', 'Ipê', 'Medianeira', 'PR', '85887-000', -25.300500, -54.090200, 'Ter e Qui 14:00-17:00', '4535550003'),
    (4, 'Entidade Assistencial Lar Esperança', 'ASSISTANCE_ENTITY_HQ', 'Rua Paraná', '210', 'Nova Medianeira', 'Medianeira', 'PR', '85887-100', -25.292300, -54.088700, 'Seg-Sex 08:00-17:00', '4535550004');
