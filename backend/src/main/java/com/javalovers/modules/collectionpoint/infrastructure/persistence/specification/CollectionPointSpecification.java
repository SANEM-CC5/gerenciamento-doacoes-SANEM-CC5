package com.javalovers.modules.collectionpoint.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.collectionpoint.domain.entity.CollectionPoint;

public class CollectionPointSpecification extends BaseSpecification<CollectionPoint> {
    public CollectionPointSpecification(SearchCriteria<?> searchCriteria) {
        super(searchCriteria);
    }
}
