package com.javalovers.core.stockalert.domain.enums;

public enum StockAlertStatus {
    /** Saldo do item está igual ou abaixo do mínimo. */
    OPEN,
    /** Estoque foi reposto acima do mínimo (ou o alerta deixou de se aplicar). */
    RESOLVED
}
