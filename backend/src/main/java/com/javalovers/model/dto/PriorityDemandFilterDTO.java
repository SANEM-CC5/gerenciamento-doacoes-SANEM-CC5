package com.javalovers.model.dto;

import com.javalovers.model.enums.DemandPriority;
import com.javalovers.model.enums.DemandStatus;

public record PriorityDemandFilterDTO(
        DemandStatus status,
        DemandPriority priority,
        Long itemId,
        /** Busca pela descrição do item. */
        String searchTerm
) {
}
