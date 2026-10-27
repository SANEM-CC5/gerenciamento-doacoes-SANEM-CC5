-- =====================================================================
-- V9: Adiciona o status INACTIVE ao beneficiário.
-- Permite bloquear/inativar um beneficiário aprovado sem removê-lo
-- (o soft delete via deleted_at continua existindo).
-- =====================================================================

ALTER TABLE beneficiary
    MODIFY COLUMN beneficiary_status
        ENUM('PENDING', 'APPROVED', 'REJECTED', 'INACTIVE') NOT NULL DEFAULT 'PENDING';
