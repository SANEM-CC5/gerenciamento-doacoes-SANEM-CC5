package com.javalovers.core.item.domain.dto.response;

import com.javalovers.core.category.domain.dto.response.CategoryDTO;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode,
        CategoryDTO category,
        String size,
        String conservationState,
        String gender
) {
}
