package com.javalovers.core.item.domain.entity;

import com.javalovers.common.entity.SoftDeletable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "item")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Item implements SoftDeletable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @NotBlank
    @Column(name = "description")
    private String description;

    @NotNull
    @Column(name = "stock_quantity")
    private Long stockQuantity;

    /**
     * Estoque mínimo do item (RF19). Quando o saldo atingir ou ficar abaixo
     * deste valor, os usuários autorizados são notificados.
     * NULL = item sem alerta de estoque mínimo configurado.
     */
    @Column(name = "minimum_stock")
    private Long minimumStock;

    @Column(name = "tag_code", unique = true)
    private String tagCode;

    @Column(name = "size", length = 30)
    private String size;

    @Column(name = "gender", length = 30)
    private String gender;

    @Column(name = "type", length = 80)
    private String type;

    @Column(name = "condition_status", length = 40)
    private String conditionStatus;

    @Column(name = "location", length = 120)
    private String location;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** true quando há mínimo configurado e o saldo está igual ou abaixo dele. */
    public boolean isLowStock() {
        if (minimumStock == null) return false;
        long current = stockQuantity != null ? stockQuantity : 0L;
        return current <= minimumStock;
    }
}
