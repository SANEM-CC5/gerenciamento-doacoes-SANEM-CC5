package com.javalovers.modules.collectionpoint.application.dto.request;

import com.javalovers.modules.collectionpoint.domain.enums.CollectionPointType;

public record CollectionPointFilterDTO(
        String name,
        CollectionPointType type,
        String city,
        String neighborhood
) {
}
