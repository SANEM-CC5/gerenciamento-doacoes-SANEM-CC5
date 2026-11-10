package com.javalovers.core.prioritydemand.domain.dto.response;

import com.javalovers.core.prioritydemand.domain.enums.DemandPriority;
import com.javalovers.core.prioritydemand.domain.enums.DemandStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PriorityDemandDTO(
        Long priorityDemandId,
        Long itemId,
        String itemDescription,
        Long currentStock,
        Long quantityNeeded,
        /** Soma das doações do item desde createdAt até o fim do prazo. */
        Long quantityReceived,
        Long quantityRemaining,
        /** 0 a 100. */
        Integer progressPercent,
        /** true quando recebido >= necessário. */
        boolean fulfilled,
        /** true quando o prazo passou sem a demanda ser atendida. */
        boolean overdue,
        DemandPriority priority,
        String justification,
        LocalDate deadline,
        DemandStatus status,
        String createdByName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime inactivatedAt
) {
}
