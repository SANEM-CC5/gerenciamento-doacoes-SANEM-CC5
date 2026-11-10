package com.javalovers.modules.profile.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.profile.domain.entity.Profile;

public class ProfileSpecification extends BaseSpecification<Profile> {
    public ProfileSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
