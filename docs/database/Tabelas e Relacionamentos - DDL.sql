-- =====================================================================
-- DDL - Esquema real do sistema (MySQL 8.0)
-- Reflete a implementação atual do backend (entidades JPA + migrations
-- Flyway V2..V9). Scripts executáveis equivalentes:
--   db/script-create-tables.sql  (baseline)
--   backend/src/main/resources/db/migration/V2..V9  (evolução)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS sanem
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE sanem;

-- ==========================================
-- 1. USUÁRIOS E PERFIS
-- ==========================================
CREATE TABLE profile (
    profile_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    deleted_at TIMESTAMP(6) NULL,
    UNIQUE KEY uq_profile_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE app_user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    login VARCHAR(60) NOT NULL,
    email VARCHAR(160) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    profile_id INT NOT NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_app_user_profile FOREIGN KEY (profile_id) REFERENCES profile(profile_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE KEY uq_user_login (login),
    UNIQUE KEY uq_user_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 2. BENEFICIÁRIOS
-- Observação: o status INACTIVE (migration V9) permite inativar/bloquear
-- beneficiários aprovados sem removê-los. O soft delete usa deleted_at.
-- (Os campos address e current_withdrawals_this_month foram removidos nas
--  migrations V5 e V4, respectivamente.)
-- ==========================================
CREATE TABLE beneficiary (
    beneficiary_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(160) NOT NULL,
    cpf VARCHAR(14) NOT NULL,
    phone VARCHAR(120),
    socioeconomic_data TEXT,
    beneficiary_status ENUM('PENDING', 'APPROVED', 'REJECTED', 'INACTIVE') NOT NULL DEFAULT 'PENDING',
    approver_user_id INT,
    withdrawal_limit INT DEFAULT NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_beneficiary_approver FOREIGN KEY (approver_user_id) REFERENCES app_user(user_id)
        ON UPDATE RESTRICT ON DELETE SET NULL,
    UNIQUE KEY uq_beneficiary_cpf (cpf)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE card (
    card_id INT AUTO_INCREMENT PRIMARY KEY,
    unique_number VARCHAR(64) NOT NULL,
    beneficiary_id INT NOT NULL,
    issue_date TIMESTAMP(6) NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_card_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(beneficiary_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE KEY uq_card_number (unique_number),
    UNIQUE KEY uq_card_beneficiary (beneficiary_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE withdrawal_limit_reset (
    withdrawal_limit_reset_id INT AUTO_INCREMENT PRIMARY KEY,
    beneficiary_id INT NOT NULL,
    reset_date DATETIME NOT NULL,
    CONSTRAINT fk_withdrawal_limit_reset_beneficiary FOREIGN KEY (beneficiary_id)
        REFERENCES beneficiary(beneficiary_id),
    INDEX idx_withdrawal_limit_reset_beneficiary_date (beneficiary_id, reset_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 3. CATÁLOGO / ESTOQUE
-- ==========================================
CREATE TABLE category (
    category_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    deleted_at TIMESTAMP(6) NULL,
    UNIQUE KEY uq_category_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE item (
    item_id INT AUTO_INCREMENT PRIMARY KEY,
    description VARCHAR(200) NOT NULL,
    stock_quantity INT NOT NULL DEFAULT 0,
    tag_code VARCHAR(64),
    deleted_at TIMESTAMP(6) NULL,
    UNIQUE KEY uq_item_code (tag_code),
    CHECK (stock_quantity >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Livro-razão de movimentações de estoque (migration V3)
CREATE TABLE inventory_transaction (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    transaction_type ENUM('DONATION_IN', 'DONATION_REVERSAL', 'WITHDRAWAL_OUT', 'WITHDRAWAL_REVERSAL', 'MANUAL_ADJUSTMENT_IN', 'MANUAL_ADJUSTMENT_OUT') NOT NULL,
    quantity INT NOT NULL,
    reference_id INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaction_item FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 4. DOADORES E DOAÇÕES (ENTRADAS)
-- ==========================================
CREATE TABLE donor (
    donor_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(160) NOT NULL,
    cpf_cnpj VARCHAR(20),
    contact VARCHAR(160),
    email VARCHAR(300) DEFAULT NULL,     -- adicionado na migration V2
    deleted_at TIMESTAMP(6) NULL,
    UNIQUE KEY uq_donor_cpfcnpj (cpf_cnpj)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE donation (
    donation_id INT AUTO_INCREMENT PRIMARY KEY,
    donation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    receiver_user_id INT NOT NULL,
    donor_id INT,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_donation_receiver_user FOREIGN KEY (receiver_user_id) REFERENCES app_user(user_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_donation_donor FOREIGN KEY (donor_id) REFERENCES donor(donor_id)
        ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE item_donated (
    item_donated_id INT AUTO_INCREMENT PRIMARY KEY,
    donation_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_item_donated_donation FOREIGN KEY (donation_id) REFERENCES donation(donation_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_item_donated_item FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE KEY uk_item_donated (donation_id, item_id),
    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 5. RETIRADAS (SAÍDAS)
-- ==========================================
CREATE TABLE withdrawal (
    withdrawal_id INT AUTO_INCREMENT PRIMARY KEY,
    withdrawal_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    beneficiary_id INT NOT NULL,
    attendant_user_id INT NOT NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_withdrawal_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES beneficiary(beneficiary_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_withdrawal_attendant_user FOREIGN KEY (attendant_user_id) REFERENCES app_user(user_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE item_withdrawn (
    item_withdrawn_id INT AUTO_INCREMENT PRIMARY KEY,
    withdrawal_id INT NOT NULL,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT fk_item_withdrawn_withdrawal FOREIGN KEY (withdrawal_id) REFERENCES withdrawal(withdrawal_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_item_withdrawn_item FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    UNIQUE KEY uk_item_withdrawn (withdrawal_id, item_id),
    CHECK (quantity > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 6. CONFIGURAÇÃO DE LIMITES
-- ==========================================
CREATE TABLE withdrawal_limit_config (
    config_id INT AUTO_INCREMENT PRIMARY KEY,
    monthly_item_limit INT NOT NULL DEFAULT 10,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP(6) NULL,
    CONSTRAINT chk_monthly_limit_positive CHECK (monthly_item_limit > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 7. PONTOS DE COLETA E SEDES (RF12 - mapa interativo) - migration V8
-- Entidade independente (sem relacionamentos obrigatórios). Soft delete
-- via deleted_at. latitude/longitude alimentam o mapa (Leaflet/OSM).
-- ==========================================
CREATE TABLE collection_point (
    collection_point_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    type ENUM('PARTNER_COLLECTION_POINT', 'ASSISTANCE_ENTITY_HQ') NOT NULL,
    street VARCHAR(255),
    number VARCHAR(30),
    neighborhood VARCHAR(120),
    city VARCHAR(120),
    state VARCHAR(60),
    zip_code VARCHAR(15),
    latitude DOUBLE NOT NULL,
    longitude DOUBLE NOT NULL,
    operating_hours VARCHAR(255),
    phone VARCHAR(30),
    deleted_at TIMESTAMP(6) NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ==========================================
-- 8. ÍNDICES PARA OTIMIZAÇÃO DE RELATÓRIOS
-- ==========================================
CREATE INDEX idx_withdrawal_date ON withdrawal(withdrawal_date);
CREATE INDEX idx_donation_date ON donation(donation_date);
CREATE INDEX idx_collection_point_type ON collection_point(type);

-- ==========================================
-- 9. DEMANDAS PRIORITÁRIAS (RF20) - migration V11
-- Sempre vinculadas a um item. "Recebido X de Y" é calculado (não armazenado)
-- somando item_donated do item desde created_at até o fim do dia do prazo.
-- Inativar = status INACTIVE (o registro é mantido).
-- ==========================================
CREATE TABLE priority_demand (
    priority_demand_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    quantity_needed INT NOT NULL,
    priority ENUM('HIGH', 'MEDIUM', 'LOW') NOT NULL DEFAULT 'MEDIUM',
    justification VARCHAR(500) NULL,
    deadline DATE NULL,
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE',
    created_by_user_id INT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NULL,
    inactivated_at DATETIME(6) NULL,
    CONSTRAINT fk_priority_demand_item FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk_priority_demand_user FOREIGN KEY (created_by_user_id) REFERENCES app_user(user_id)
        ON UPDATE RESTRICT ON DELETE SET NULL,
    CONSTRAINT chk_priority_demand_quantity CHECK (quantity_needed > 0),
    INDEX idx_priority_demand_status_priority (status, priority),
    INDEX idx_priority_demand_item (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
