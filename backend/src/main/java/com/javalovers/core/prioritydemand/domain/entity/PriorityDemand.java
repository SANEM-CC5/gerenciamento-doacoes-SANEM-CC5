package com.javalovers.core.prioritydemand.domain.entity;

import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.prioritydemand.domain.enums.DemandPriority;
import com.javalovers.core.prioritydemand.domain.enums.DemandStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Demanda prioritária por um item para doação (RF20).
 * Inativar não remove o registro: apenas altera o status para INACTIVE.
 */
@Entity
@Table(name = "priority_demand")
@Getter
@Setter
@NoArgsConstructor
public class PriorityDemand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "priority_demand_id")
    private Long priorityDemandId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "quantity_needed", nullable = false)
    private Long quantityNeeded;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private DemandPriority priority = DemandPriority.MEDIUM;

    @Column(name = "justification", length = 500)
    private String justification;

    @Column(name = "deadline")
    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DemandStatus status = DemandStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "created_by_user_id")
    private AppUser createdBy;

    /** Início da contagem do progresso: só doações a partir daqui contam. */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "inactivated_at")
    private LocalDateTime inactivatedAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = DemandStatus.ACTIVE;
        if (priority == null) priority = DemandPriority.MEDIUM;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return status == DemandStatus.ACTIVE;
    }

    public void inactivate() {
        status = DemandStatus.INACTIVE;
        inactivatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        status = DemandStatus.ACTIVE;
        inactivatedAt = null;
    }
}
