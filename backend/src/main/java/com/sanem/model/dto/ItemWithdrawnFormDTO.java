package com.sanem.model.dto;

public record ItemWithdrawnFormDTO(
        Long withdrawalId,
        Long itemId,
        Integer quantity
) {
}
