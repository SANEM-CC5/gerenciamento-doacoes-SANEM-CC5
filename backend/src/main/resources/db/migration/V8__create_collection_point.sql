-- =====================================================================
-- V8: Pontos de coleta parceiros e sedes de entidades assistenciais
-- Usados para exibição em mapa interativo (RF12).
-- =====================================================================

CREATE TABLE IF NOT EXISTS collection_point (
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

-- Dados de exemplo (locais em Medianeira - PR). Coordenadas aproximadas.
INSERT INTO collection_point
    (name, type, street, number, neighborhood, city, state, zip_code, latitude, longitude, operating_hours, phone)
VALUES
    ('Sede SANEM', 'ASSISTANCE_ENTITY_HQ', 'Avenida Brasil', '1500', 'Centro', 'Medianeira', 'PR', '85884-000', -25.295100, -54.093900, 'Seg-Sex 08:00-18:00', '4535550001'),
    ('Ponto de Coleta Supermercado Central', 'PARTNER_COLLECTION_POINT', 'Rua Minas Gerais', '320', 'Centro', 'Medianeira', 'PR', '85884-010', -25.297800, -54.095400, 'Seg-Sab 08:00-20:00', '4535550002'),
    ('Ponto de Coleta Paroquia Sao Miguel', 'PARTNER_COLLECTION_POINT', 'Rua Santa Catarina', '745', 'Ipe', 'Medianeira', 'PR', '85887-000', -25.300500, -54.090200, 'Ter e Qui 14:00-17:00', '4535550003'),
    ('Entidade Assistencial Lar Esperanca', 'ASSISTANCE_ENTITY_HQ', 'Rua Parana', '210', 'Nova Medianeira', 'Medianeira', 'PR', '85887-100', -25.292300, -54.088700, 'Seg-Sex 08:00-17:00', '4535550004');
