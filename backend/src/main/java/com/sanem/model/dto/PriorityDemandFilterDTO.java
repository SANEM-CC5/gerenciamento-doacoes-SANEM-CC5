package com.sanem.model.dto;

import com.sanem.model.enums.DemandPriority;
import com.sanem.model.enums.DemandStatus;

public record PriorityDemandFilterDTO(
        DemandStatus status,
        DemandPriority priority,
        Long itemId,
        /** Busca pela descrição do item. */
        String searchTerm
) {
}
