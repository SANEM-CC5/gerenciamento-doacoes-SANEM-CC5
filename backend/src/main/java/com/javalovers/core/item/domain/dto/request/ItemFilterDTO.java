package com.javalovers.core.item.domain.dto.request;

public record ItemFilterDTO(
        String searchTerm,
        Long minQuantity,
        Long maxQuantity,
        Long categoryId,
        String category,
        String size,
        String conservationState,
        String gender
) {
}
