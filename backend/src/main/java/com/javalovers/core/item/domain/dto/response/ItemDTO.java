package com.javalovers.core.item.domain.dto.response;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode,
<<<<<<< HEAD
        Long minimumStock,
        boolean lowStock
=======
        String size,
        String gender,
        String type,
        String conditionStatus,
        String location
>>>>>>> origin/main
)
{
}
