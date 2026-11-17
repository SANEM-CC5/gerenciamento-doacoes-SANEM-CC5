package com.sanem.model.dto;

import com.sanem.model.enums.CollectionPointType;

public record CollectionPointFilterDTO(
        String name,
        CollectionPointType type,
        String city,
        String neighborhood
) {
}
