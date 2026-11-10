package com.javalovers.core.stockalert.domain.entity;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.stockalert.domain.enums.StockAlertStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Registro de que um item atingiu/ficou abaixo do estoque mínimo (RF19).
 * Existe no máximo um alerta OPEN por item: novas saídas enquanto o alerta
 * está aberto não geram novas notificações.
 */
@Entity
@Table(name = "stock_alert")
@Getter
@Setter
@NoArgsConstructor
public class StockAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_alert_id")
    private Long stockAlertId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    /** Saldo do item no momento em que o alerta foi disparado. */
    @Column(name = "stock_quantity", nullable = false)
    private Long stockQuantity;

    /** Estoque mínimo vigente no momento do disparo. */
    @Column(name = "minimum_stock", nullable = false)
    private Long minimumStock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockAlertStatus status = StockAlertStatus.OPEN;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public StockAlert(Item item) {
        this.item = item;
        this.stockQuantity = item.getStockQuantity() != null ? item.getStockQuantity() : 0L;
        this.minimumStock = item.getMinimumStock();
        this.status = StockAlertStatus.OPEN;
    }

    public void resolve() {
        this.status = StockAlertStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
    }

    public boolean isOpen() {
        return status == StockAlertStatus.OPEN;
    }
}
