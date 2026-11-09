package com.javalovers.core.collectionpoint.domain.dto.request;

import com.javalovers.core.collectionpoint.domain.enums.CollectionPointType;

public record CollectionPointFilterDTO(
        String name,
        CollectionPointType type,
        String city,
        String neighborhood
) {
}
