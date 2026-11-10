package com.javalovers.core.item.domain.dto.request;

public record ItemFilterDTO(
        String searchTerm,
        Long minQuantity,
        Long maxQuantity,
<<<<<<< HEAD
        Boolean lowStock
=======
        String size,
        String gender,
        String type,
        String conditionStatus,
        String location
>>>>>>> origin/main
) {
}
