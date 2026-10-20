package com.javalovers.core.collectionpoint.domain.dto.response;

import com.javalovers.core.collectionpoint.domain.enums.CollectionPointType;

public record CollectionPointDTO(
        Long collectionPointId,
        String name,
        CollectionPointType type,
        String street,
        String number,
        String neighborhood,
        String city,
        String state,
        String zipCode,
        Double latitude,
        Double longitude,
        String operatingHours,
        String phone
) {
}
