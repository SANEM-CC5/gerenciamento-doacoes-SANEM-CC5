package com.javalovers.core.prioritydemand.domain.dto.request;

import com.javalovers.core.prioritydemand.domain.enums.DemandPriority;
import com.javalovers.core.prioritydemand.domain.enums.DemandStatus;

public record PriorityDemandFilterDTO(
        DemandStatus status,
        DemandPriority priority,
        Long itemId,
        /** Busca pela descrição do item. */
        String searchTerm
) {
}
