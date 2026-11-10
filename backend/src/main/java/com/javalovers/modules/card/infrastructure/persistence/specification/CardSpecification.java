package com.javalovers.modules.card.infrastructure.persistence.specification;

import com.javalovers.shared.specification.BaseSpecification;
import com.javalovers.shared.specification.SearchCriteria;
import com.javalovers.modules.card.domain.entity.Card;

public class CardSpecification extends BaseSpecification<Card> {
    public CardSpecification(SearchCriteria<?> searchCriteria) {super(searchCriteria);}
}
