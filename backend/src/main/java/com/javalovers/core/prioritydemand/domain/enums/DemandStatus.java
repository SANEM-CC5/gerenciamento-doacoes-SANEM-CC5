package com.javalovers.core.prioritydemand.domain.enums;

/**
 * Situação cadastral da demanda (RF20). "Atendida" não é um status gravado:
 * é calculada pelo progresso (recebido >= necessário).
 */
public enum DemandStatus {
    ACTIVE,
    INACTIVE
}
