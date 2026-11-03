package com.javalovers.core.item.domain.dto.response;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode,
        String size,
        String gender,
        String type,
        String conditionStatus,
        String location
)
{
}
