package com.javalovers.modules.item.application.dto.request;

public record ItemFilterDTO(
        String searchTerm,
        Long minQuantity,
        Long maxQuantity
) {
}
