package com.javalovers.specification;

import com.javalovers.specification.BaseSpecification;
import com.javalovers.specification.SearchCriteria;
import com.javalovers.model.entity.Item;

public class ItemSpecification extends BaseSpecification<Item> {
    public ItemSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
