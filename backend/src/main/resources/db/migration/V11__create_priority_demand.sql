-- =====================================================================
-- V11: Demandas prioritárias por itens para doação (RF20).
--
-- A SANEM cadastra, consulta, atualiza e inativa demandas prioritárias,
-- sempre vinculadas a um item do estoque. O progresso ("recebido X de Y")
-- NÃO é armazenado: é calculado a partir das doações do item
-- (item_donated) registradas desde created_at até o fim do dia do prazo.
--
-- Obs.: a versão V10 está reservada para o RF19 (estoque mínimo), que
-- está em outra branch. Manter V11 evita conflito quando ambas forem
-- integradas à main.
-- =====================================================================

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
    CONSTRAINT chk_priority_demand_quantity CHECK (quantity_needed > 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_priority_demand_status_priority ON priority_demand (status, priority);
CREATE INDEX idx_priority_demand_item ON priority_demand (item_id);

-- Dados de exemplo: criadas em 01/06/2026 para que as doações de junho
-- (V7) já apareçam no progresso.
INSERT INTO priority_demand (item_id, quantity_needed, priority, justification, deadline, status, created_by_user_id, created_at)
SELECT i.item_id, 100, 'HIGH', 'Aumento de famílias com bebês atendidas neste semestre.', '2026-12-20', 'ACTIVE',
       (SELECT u.user_id FROM app_user u WHERE u.login = 'isadora' LIMIT 1), '2026-06-01 00:00:00'
FROM item i WHERE i.description = 'Fralda Descartável Tam M' AND i.deleted_at IS NULL LIMIT 1;

INSERT INTO priority_demand (item_id, quantity_needed, priority, justification, deadline, status, created_by_user_id, created_at)
SELECT i.item_id, 300, 'MEDIUM', 'Campanha de cestas básicas de fim de ano.', '2026-12-15', 'ACTIVE',
       (SELECT u.user_id FROM app_user u WHERE u.login = 'isadora' LIMIT 1), '2026-06-01 00:00:00'
FROM item i WHERE i.description = 'Leite Integral 1L' AND i.deleted_at IS NULL LIMIT 1;

INSERT INTO priority_demand (item_id, quantity_needed, priority, justification, deadline, status, created_by_user_id, created_at)
SELECT i.item_id, 50, 'LOW', 'Reposição de itens de higiene.', NULL, 'ACTIVE',
       (SELECT u.user_id FROM app_user u WHERE u.login = 'isadora' LIMIT 1), '2026-06-01 00:00:00'
FROM item i WHERE i.description = 'Creme Dental 90g' AND i.deleted_at IS NULL LIMIT 1;
