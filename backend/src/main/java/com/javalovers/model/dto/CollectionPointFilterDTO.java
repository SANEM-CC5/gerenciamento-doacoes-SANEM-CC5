package com.javalovers.model.dto;

import com.javalovers.model.enums.CollectionPointType;

public record CollectionPointFilterDTO(
        String name,
        CollectionPointType type,
        String city,
        String neighborhood
) {
}
