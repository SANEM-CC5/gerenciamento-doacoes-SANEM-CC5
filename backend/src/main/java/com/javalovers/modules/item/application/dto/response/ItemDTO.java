package com.javalovers.modules.item.application.dto.response;

public record ItemDTO(
        Long itemId,
        String description,
        Long stockQuantity,
        String tagCode
)
{
}
