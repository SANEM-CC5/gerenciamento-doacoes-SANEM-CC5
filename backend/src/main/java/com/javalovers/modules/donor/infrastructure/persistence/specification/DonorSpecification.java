package com.javalovers.modules.donor.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.donor.domain.entity.Donor;

public class DonorSpecification extends BaseSpecification<Donor> {
    public DonorSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
