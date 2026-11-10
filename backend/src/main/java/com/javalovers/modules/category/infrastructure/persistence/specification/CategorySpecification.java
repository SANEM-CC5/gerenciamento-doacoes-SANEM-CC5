package com.javalovers.modules.category.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.category.domain.entity.Category;

public class CategorySpecification extends BaseSpecification<Category> {
    public CategorySpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
