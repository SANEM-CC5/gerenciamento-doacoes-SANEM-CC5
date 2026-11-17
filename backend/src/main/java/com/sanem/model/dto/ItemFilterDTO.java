package com.sanem.model.dto;

public record ItemFilterDTO(
        String searchTerm,
        Long minQuantity,
        Long maxQuantity,
        Boolean lowStock,
        String size,
        String gender,
        String type,
        String conditionStatus,
        String location
) {
}
