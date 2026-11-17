package com.sanem.model.dto;

import com.sanem.model.enums.CollectionPointType;

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
