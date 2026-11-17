package com.javalovers.model.dto;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode,
        Long minimumStock,
        boolean lowStock,
        String size,
        String gender,
        String type,
        String conditionStatus,
        String location
)
{
}
