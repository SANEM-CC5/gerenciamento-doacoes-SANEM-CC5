package com.sanem.mapper;

import com.sanem.model.dto.CollectionPointFormDTO;
import com.sanem.model.entity.CollectionPoint;
import org.springframework.stereotype.Service;

@Service
public class CollectionPointUpdateMapper {

    public void update(CollectionPoint collectionPoint, CollectionPointFormDTO collectionPointFormDTO) {
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
    }
}
