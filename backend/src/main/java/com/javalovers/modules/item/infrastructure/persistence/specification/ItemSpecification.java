package com.javalovers.modules.item.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.item.domain.entity.Item;

public class ItemSpecification extends BaseSpecification<Item> {
    public ItemSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
