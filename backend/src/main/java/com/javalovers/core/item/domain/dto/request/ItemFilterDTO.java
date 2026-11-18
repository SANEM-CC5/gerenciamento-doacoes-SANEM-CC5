package com.javalovers.core.item.domain.dto.request;

public record ItemFilterDTO(
        String searchTerm,
        Long minQuantity,
        Long maxQuantity,
        Boolean lowStock,
        Long categoryId,
        String category,
        String size,
        String conservationState,
        String gender,
        String type,
        String conditionStatus,
        String location
) {
    public static ItemFilterDTO empty() {
        return new ItemFilterDTO(null, null, null, null, null, null, null, null, null, null, null, null);
    }
}
