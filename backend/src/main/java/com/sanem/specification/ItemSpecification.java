package com.sanem.specification;

import com.sanem.specification.BaseSpecification;
import com.sanem.specification.SearchCriteria;
import com.sanem.model.entity.Item;

public class ItemSpecification extends BaseSpecification<Item> {
    public ItemSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
