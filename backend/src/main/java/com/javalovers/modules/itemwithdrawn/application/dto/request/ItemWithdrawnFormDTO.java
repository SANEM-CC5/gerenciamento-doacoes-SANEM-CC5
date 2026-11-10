package com.javalovers.modules.itemwithdrawn.application.dto.request;

public record ItemWithdrawnFormDTO(
        Long withdrawalId,
        Long itemId,
        Integer quantity
) {
}
