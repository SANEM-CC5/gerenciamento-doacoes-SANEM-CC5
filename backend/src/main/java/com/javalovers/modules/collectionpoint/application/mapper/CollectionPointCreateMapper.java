package com.javalovers.modules.collectionpoint.application.mapper;

import com.javalovers.modules.collectionpoint.application.dto.request.CollectionPointFormDTO;
import com.javalovers.modules.collectionpoint.domain.entity.CollectionPoint;
import org.springframework.stereotype.Service;

@Service
public class CollectionPointCreateMapper {

    public CollectionPoint convert(CollectionPointFormDTO collectionPointFormDTO) {
        CollectionPoint collectionPoint = new CollectionPoint();
        collectionPoint.setName(collectionPointFormDTO.name());
        collectionPoint.setType(collectionPointFormDTO.type());
        collectionPoint.setStreet(collectionPointFormDTO.street());
        collectionPoint.setNumber(collectionPointFormDTO.number());
        collectionPoint.setNeighborhood(collectionPointFormDTO.neighborhood());
        collectionPoint.setCity(collectionPointFormDTO.city());
        collectionPoint.setState(collectionPointFormDTO.state());
        collectionPoint.setZipCode(collectionPointFormDTO.zipCode());
        collectionPoint.setLatitude(collectionPointFormDTO.latitude());
        collectionPoint.setLongitude(collectionPointFormDTO.longitude());
        collectionPoint.setOperatingHours(collectionPointFormDTO.operatingHours());
        collectionPoint.setPhone(collectionPointFormDTO.phone());

        return collectionPoint;
    }
}
