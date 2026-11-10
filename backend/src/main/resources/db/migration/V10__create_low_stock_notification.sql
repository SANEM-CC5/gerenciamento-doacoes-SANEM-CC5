-- =====================================================================
-- V10: Alerta de estoque mínimo e notificações de usuários (RF19).
--
-- 1. item.minimum_stock  -> limite mínimo configurável por item
--    (NULL = item sem alerta configurado).
-- 2. stock_alert         -> registra cada vez que o saldo de um item
--    atinge ou fica abaixo do mínimo. Fica OPEN enquanto o saldo
--    permanecer <= mínimo e vira RESOLVED quando o estoque é reposto,
--    evitando notificações duplicadas a cada nova saída.
-- 3. notification        -> caixa de notificações por usuário
--    (uma linha por usuário autorizado notificado).
-- =====================================================================

ALTER TABLE item
    ADD COLUMN minimum_stock INT NULL DEFAULT NULL AFTER stock_quantity;

ALTER TABLE item
    ADD CONSTRAINT chk_item_minimum_stock CHECK (minimum_stock IS NULL OR minimum_stock >= 0);

CREATE TABLE stock_alert (
    stock_alert_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id INT NOT NULL,
    stock_quantity INT NOT NULL,
    minimum_stock INT NOT NULL,
    status ENUM('OPEN', 'RESOLVED') NOT NULL DEFAULT 'OPEN',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    resolved_at DATETIME(6) NULL,

    CONSTRAINT fk_stock_alert_item FOREIGN KEY (item_id) REFERENCES item(item_id)
        ON UPDATE RESTRICT ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_stock_alert_item_status ON stock_alert (item_id, status);

CREATE TABLE notification (
    notification_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    stock_alert_id BIGINT NULL,
    type ENUM('LOW_STOCK') NOT NULL,
    title VARCHAR(150) NOT NULL,
    message VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    read_at DATETIME(6) NULL,

    CONSTRAINT fk_notification_user FOREIGN KEY (user_id) REFERENCES app_user(user_id)
        ON UPDATE RESTRICT ON DELETE CASCADE,
    CONSTRAINT fk_notification_stock_alert FOREIGN KEY (stock_alert_id) REFERENCES stock_alert(stock_alert_id)
        ON UPDATE RESTRICT ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_notification_user_read ON notification (user_id, read_at);
