package com.javalovers.modules.beneficiary.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;

public class BeneficiarySpecification extends BaseSpecification<Beneficiary> {
    public BeneficiarySpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
