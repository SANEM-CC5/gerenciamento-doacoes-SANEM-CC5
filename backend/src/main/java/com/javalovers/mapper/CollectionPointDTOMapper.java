package com.javalovers.mapper;

import com.javalovers.model.dto.CollectionPointDTO;
import com.javalovers.model.entity.CollectionPoint;
import org.springframework.stereotype.Service;

@Service
public class CollectionPointDTOMapper {

    public CollectionPointDTO convert(CollectionPoint collectionPoint) {
        if (collectionPoint == null) return null;
        return new CollectionPointDTO(
                collectionPoint.getCollectionPointId(),
                collectionPoint.getName(),
                collectionPoint.getType(),
                collectionPoint.getStreet(),
                collectionPoint.getNumber(),
                collectionPoint.getNeighborhood(),
                collectionPoint.getCity(),
                collectionPoint.getState(),
                collectionPoint.getZipCode(),
                collectionPoint.getLatitude(),
                collectionPoint.getLongitude(),
                collectionPoint.getOperatingHours(),
                collectionPoint.getPhone()
        );
    }
}
