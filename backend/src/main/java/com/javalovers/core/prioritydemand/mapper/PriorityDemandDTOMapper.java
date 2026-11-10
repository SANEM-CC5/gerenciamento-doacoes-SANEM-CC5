package com.javalovers.core.prioritydemand.mapper;

import com.javalovers.core.item.domain.entity.Item;
import com.javalovers.core.prioritydemand.domain.dto.response.PriorityDemandDTO;
import com.javalovers.core.prioritydemand.domain.entity.PriorityDemand;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class PriorityDemandDTOMapper {

    /**
     * @param received total já recebido para a demanda (calculado no service)
     * @param today    data de referência para saber se o prazo venceu
     */
    public PriorityDemandDTO convert(PriorityDemand demand, long received, LocalDate today) {
        if (demand == null) return null;

        Item item = demand.getItem();
        long needed = demand.getQuantityNeeded() != null ? demand.getQuantityNeeded() : 0L;
        long remaining = Math.max(needed - received, 0L);
        boolean fulfilled = needed > 0 && received >= needed;
        int percent = needed > 0 ? (int) Math.min(100L, (received * 100L) / needed) : 0;
        boolean overdue = demand.isActive() && !fulfilled
                && demand.getDeadline() != null && demand.getDeadline().isBefore(today);

        return new PriorityDemandDTO(
                demand.getPriorityDemandId(),
                item != null ? item.getItemId() : null,
                item != null ? item.getDescription() : null,
                item != null ? item.getStockQuantity() : null,
                needed,
                received,
                remaining,
                percent,
                fulfilled,
                overdue,
                demand.getPriority(),
                demand.getJustification(),
                demand.getDeadline(),
                demand.getStatus(),
                demand.getCreatedBy() != null ? demand.getCreatedBy().getName() : null,
                demand.getCreatedAt(),
                demand.getUpdatedAt(),
                demand.getInactivatedAt()
        );
    }
}
