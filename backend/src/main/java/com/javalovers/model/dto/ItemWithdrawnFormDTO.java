package com.javalovers.model.dto;

public record ItemWithdrawnFormDTO(
        Long withdrawalId,
        Long itemId,
        Integer quantity
) {
}
