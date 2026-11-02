-- =====================================================================
-- Dados de exemplo (PostgreSQL / NeonDB)
-- Script de referência standalone. Em produção o seed é aplicado pela
-- Flyway (backend/src/main/resources/db/migration/V2__seed_data.sql).
-- Senhas dos usuários = '{Nome}@2026' (ex.: Isadora@2026).
-- =====================================================================

-- PERFIS
INSERT INTO profile (name, description) VALUES
    ('Administrator', 'Full system access'),
    ('Attendant', 'Performs registrations and withdrawals'),
    ('Evaluator', 'Approves or rejects beneficiaries');

-- USUÁRIOS (profile_id: 1=Administrator, 2=Attendant, 3=Evaluator)
INSERT INTO app_user (name, login, email, password_hash, status, profile_id) VALUES
    ('Isadora', 'isadora', 'isadora@gmail.com', '$2a$12$zM3oy9SL3q3hUZZomXMASOm1lRn.sSC85NxNnYDi.U0j2c0x3444u', 'ACTIVE', 1),
    ('Isis', 'isis', 'isis@gmail.com', '$2a$12$zA0uM7s0gZtskYelrtnmIeW9L3OMfx6Ixg09FpQCp1ZmhtdSideJa', 'ACTIVE', 2),
    ('Victor', 'victor', 'victor@gmail.com', '$2a$12$15TzGy3o9QVZX8xAXR2uTuaatS3XrAyEqSPu.Lo/G6kX9WtjEDY46', 'ACTIVE', 3),
    ('Layssa', 'layssa', 'layssa@gmail.com', '$2a$12$WFw8qRw4hqc.kQZiNNUUi..FWxi9cxsSA2e7GLHx3g7ott1Ds37li', 'ACTIVE', 1);

-- DOADORES
INSERT INTO donor (name, cpf_cnpj, contact, email) VALUES
    ('Supermercado Bom Preço', '62039518000196', '1140028922', 'contato@bompreco.com.br'),
    ('Atacadão da Família', '18581892000127', '1133334444', 'doacoes@atacadaofamilia.com'),
    ('Maria Rita Costa', '73020286050', '11988887777', 'mariarita.costa@gmail.com'),
    ('João Pedro Almeida', '87805843066', '11955554444', 'jpalmeida_doador@hotmail.com'),
    ('Igreja Esperança', '17168702000181', '1130302020', 'comunidade@igrejaesperanca.org'),
    ('Farmácia Saúde', '10318838000181', '1132221111', 'gerencia@farmaciasaude.com'),
    ('Carlos Eduardo Souza', '91956253050', '11977778888', 'cadu_souza@gmail.com'),
    ('Confecções Silva', '56788492000186', '1144445555', 'contato@confeccoessilva.com.br'),
    ('Ana Paula Mendes', '53850413004', '11966663333', 'aninha.mendes@yahoo.com'),
    ('Clube Lions Local', '74076734000190', '1139998888', 'diretoria@lionslocal.org');

-- BENEFICIÁRIOS
INSERT INTO beneficiary (full_name, cpf, phone, socioeconomic_data, beneficiary_status, approver_user_id, withdrawal_limit) VALUES
    ('Marta Ferreira', '87326891010', '11911112222', 'Mãe solteira, desempregada', 'APPROVED', 3, 10),
    ('José Roberto Dias', '54676836082', '11922223333', 'Idoso, recebe BPC', 'APPROVED', 1, 10),
    ('Aline Rodrigues', '22997902096', '11933334444', 'Renda menor que 1 salário', 'APPROVED', 4, 10),
    ('Fernando Castro', '53229628080', '11944445555', 'Acamado, esposa não trabalha', 'APPROVED', 3, 10),
    ('Luciana Martins', '06588315007', '11955556666', 'Pagando aluguel, sem emprego', 'APPROVED', 1, 10),
    ('Ricardo Gomes', '37288830004', '11966667777', 'Trabalhador informal', 'APPROVED', 4, 10),
    ('Teresa Nunes', '95160215069', '11977778888', 'Cuida de netos, sem renda', 'APPROVED', 3, 10),
    ('Gabriel Peixoto', '44829234008', '11988889999', 'Estudante em ocupação', 'PENDING', NULL, 10),
    ('Camila Vieira', '95844334078', '11999990000', 'Ajuda emergencial', 'PENDING', NULL, 10),
    ('Roberto Antunes', '58872974097', '11900001111', 'Renda superior ao limite', 'REJECTED', 3, 10);

-- ITENS
INSERT INTO item (description, stock_quantity, tag_code) VALUES
    ('Arroz Branco 5kg', 120, 'ETQ-ARR-001'),
    ('Feijão Carioca 1kg', 85, 'ETQ-FEI-001'),
    ('Macarrão Espaguete 500g', 150, 'ETQ-MAC-001'),
    ('Óleo de Soja 900ml', 90, 'ETQ-OLE-001'),
    ('Leite Integral 1L', 200, 'ETQ-LEI-001'),
    ('Sabonete em Barra 90g', 300, 'ETQ-SAB-001'),
    ('Creme Dental 90g', 250, 'ETQ-CRE-001'),
    ('Fralda Descartável Tam M', 45, 'ETQ-FRA-001');

-- DOAÇÕES
INSERT INTO donation (donation_id, donation_date, receiver_user_id, donor_id) VALUES
    (1, '2026-06-02 09:15:00', 1, 1),
    (2, '2026-06-05 14:30:00', 2, 2),
    (3, '2026-06-08 10:00:00', 3, 3),
    (4, '2026-06-12 11:45:00', 4, 4),
    (5, '2026-06-15 16:20:00', 1, 5),
    (6, '2026-06-18 08:50:00', 2, 6),
    (7, '2026-06-20 13:10:00', 3, 7),
    (8, '2026-06-25 15:00:00', 4, 8),
    (9, '2026-06-27 09:30:00', 2, 9),
    (10, '2026-06-28 14:00:00', 1, 10);

INSERT INTO item_donated (donation_id, item_id, quantity) VALUES
    (1, 1, 50), (1, 2, 50), (1, 3, 50),
    (2, 4, 30), (2, 5, 100),
    (3, 1, 10), (3, 2, 10),
    (4, 6, 100), (4, 7, 100),
    (5, 1, 20), (5, 3, 40),
    (6, 8, 50),
    (7, 2, 15), (7, 4, 15),
    (8, 6, 50), (8, 7, 50),
    (9, 5, 30),
    (10, 1, 40), (10, 2, 40);

INSERT INTO inventory_transaction (item_id, transaction_type, quantity, reference_id, created_at) VALUES
    (1, 'DONATION_IN', 50, 1, '2026-06-02 09:15:00'), (2, 'DONATION_IN', 50, 1, '2026-06-02 09:15:00'), (3, 'DONATION_IN', 50, 1, '2026-06-02 09:15:00'),
    (4, 'DONATION_IN', 30, 2, '2026-06-05 14:30:00'), (5, 'DONATION_IN', 100, 2, '2026-06-05 14:30:00'),
    (1, 'DONATION_IN', 10, 3, '2026-06-08 10:00:00'), (2, 'DONATION_IN', 10, 3, '2026-06-08 10:00:00'),
    (6, 'DONATION_IN', 100, 4, '2026-06-12 11:45:00'), (7, 'DONATION_IN', 100, 4, '2026-06-12 11:45:00'),
    (1, 'DONATION_IN', 20, 5, '2026-06-15 16:20:00'), (3, 'DONATION_IN', 40, 5, '2026-06-15 16:20:00'),
    (8, 'DONATION_IN', 50, 6, '2026-06-18 08:50:00'),
    (2, 'DONATION_IN', 15, 7, '2026-06-20 13:10:00'), (4, 'DONATION_IN', 15, 7, '2026-06-20 13:10:00'),
    (6, 'DONATION_IN', 50, 8, '2026-06-25 15:00:00'), (7, 'DONATION_IN', 50, 8, '2026-06-25 15:00:00'),
    (5, 'DONATION_IN', 30, 9, '2026-06-27 09:30:00'),
    (1, 'DONATION_IN', 40, 10, '2026-06-28 14:00:00'), (2, 'DONATION_IN', 40, 10, '2026-06-28 14:00:00');

-- RETIRADAS
INSERT INTO withdrawal (withdrawal_id, withdrawal_date, beneficiary_id, attendant_user_id) VALUES
    (1, '2026-06-03 09:00:00', 1, 2),
    (2, '2026-06-06 10:15:00', 2, 3),
    (3, '2026-06-09 14:20:00', 3, 4),
    (4, '2026-06-13 11:00:00', 4, 1),
    (5, '2026-06-16 15:45:00', 5, 2),
    (6, '2026-06-19 09:30:00', 6, 3),
    (7, '2026-06-22 13:10:00', 7, 4),
    (8, '2026-06-26 10:00:00', 1, 1),
    (9, '2026-06-29 16:00:00', 2, 2);

INSERT INTO item_withdrawn (withdrawal_id, item_id, quantity) VALUES
    (1, 1, 2), (1, 2, 2), (1, 3, 3),
    (2, 4, 1), (2, 5, 2), (2, 6, 4),
    (3, 1, 3), (3, 2, 3), (3, 8, 1),
    (4, 5, 3), (4, 7, 2), (4, 8, 2),
    (5, 1, 2), (5, 4, 1), (5, 6, 3),
    (6, 2, 2), (6, 3, 4), (6, 7, 2),
    (7, 1, 2), (7, 5, 4), (7, 6, 2),
    (8, 8, 2), (8, 3, 2),
    (9, 5, 2), (9, 7, 1);

INSERT INTO inventory_transaction (item_id, transaction_type, quantity, reference_id, created_at) VALUES
    (1, 'WITHDRAWAL_OUT', 2, 1, '2026-06-03 09:00:00'), (2, 'WITHDRAWAL_OUT', 2, 1, '2026-06-03 09:00:00'), (3, 'WITHDRAWAL_OUT', 3, 1, '2026-06-03 09:00:00'),
    (4, 'WITHDRAWAL_OUT', 1, 2, '2026-06-06 10:15:00'), (5, 'WITHDRAWAL_OUT', 2, 2, '2026-06-06 10:15:00'), (6, 'WITHDRAWAL_OUT', 4, 2, '2026-06-06 10:15:00'),
    (1, 'WITHDRAWAL_OUT', 3, 3, '2026-06-09 14:20:00'), (2, 'WITHDRAWAL_OUT', 3, 3, '2026-06-09 14:20:00'), (8, 'WITHDRAWAL_OUT', 1, 3, '2026-06-09 14:20:00'),
    (5, 'WITHDRAWAL_OUT', 3, 4, '2026-06-13 11:00:00'), (7, 'WITHDRAWAL_OUT', 2, 4, '2026-06-13 11:00:00'), (8, 'WITHDRAWAL_OUT', 2, 4, '2026-06-13 11:00:00'),
    (1, 'WITHDRAWAL_OUT', 2, 5, '2026-06-16 15:45:00'), (4, 'WITHDRAWAL_OUT', 1, 5, '2026-06-16 15:45:00'), (6, 'WITHDRAWAL_OUT', 3, 5, '2026-06-16 15:45:00'),
    (2, 'WITHDRAWAL_OUT', 2, 6, '2026-06-19 09:30:00'), (3, 'WITHDRAWAL_OUT', 4, 6, '2026-06-19 09:30:00'), (7, 'WITHDRAWAL_OUT', 2, 6, '2026-06-19 09:30:00'),
    (1, 'WITHDRAWAL_OUT', 2, 7, '2026-06-22 13:10:00'), (5, 'WITHDRAWAL_OUT', 4, 7, '2026-06-22 13:10:00'), (6, 'WITHDRAWAL_OUT', 2, 7, '2026-06-22 13:10:00'),
    (8, 'WITHDRAWAL_OUT', 2, 8, '2026-06-26 10:00:00'), (3, 'WITHDRAWAL_OUT', 2, 8, '2026-06-26 10:00:00'),
    (5, 'WITHDRAWAL_OUT', 2, 9, '2026-06-29 16:00:00'), (7, 'WITHDRAWAL_OUT', 1, 9, '2026-06-29 16:00:00');

-- CONFIGURAÇÃO DE LIMITE
INSERT INTO withdrawal_limit_config (monthly_item_limit, is_active) VALUES (10, TRUE);

-- PONTOS DE COLETA E SEDES (Medianeira - PR)
INSERT INTO collection_point
    (name, type, street, number, neighborhood, city, state, zip_code, latitude, longitude, operating_hours, phone)
VALUES
    ('Sede SANEM', 'ASSISTANCE_ENTITY_HQ', 'Avenida Brasil', '1500', 'Centro', 'Medianeira', 'PR', '85884-000', -25.295100, -54.093900, 'Seg-Sex 08:00-18:00', '4535550001'),
    ('Ponto de Coleta Supermercado Central', 'PARTNER_COLLECTION_POINT', 'Rua Minas Gerais', '320', 'Centro', 'Medianeira', 'PR', '85884-010', -25.297800, -54.095400, 'Seg-Sab 08:00-20:00', '4535550002'),
    ('Ponto de Coleta Paroquia Sao Miguel', 'PARTNER_COLLECTION_POINT', 'Rua Santa Catarina', '745', 'Ipe', 'Medianeira', 'PR', '85887-000', -25.300500, -54.090200, 'Ter e Qui 14:00-17:00', '4535550003'),
    ('Entidade Assistencial Lar Esperanca', 'ASSISTANCE_ENTITY_HQ', 'Rua Parana', '210', 'Nova Medianeira', 'Medianeira', 'PR', '85887-100', -25.292300, -54.088700, 'Seg-Sex 08:00-17:00', '4535550004');

-- AJUSTE DE SEQUENCES (tabelas com IDs inseridos explicitamente)
SELECT setval(pg_get_serial_sequence('donation', 'donation_id'), (SELECT MAX(donation_id) FROM donation));
SELECT setval(pg_get_serial_sequence('withdrawal', 'withdrawal_id'), (SELECT MAX(withdrawal_id) FROM withdrawal));
