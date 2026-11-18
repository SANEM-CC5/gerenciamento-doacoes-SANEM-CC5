package com.javalovers.core.item.domain.dto.response;

import com.javalovers.core.category.domain.dto.response.CategoryDTO;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode,
        Long minimumStock,
        boolean lowStock,
        CategoryDTO category,
        String size,
        String conservationState,
        String gender,
        String type,
        String conditionStatus,
        String location
) {
}
