package com.javalovers.modules.card.application.mapper;

import com.javalovers.modules.card.application.dto.request.CardFormDTO;
import com.javalovers.modules.card.domain.entity.Card;
import org.springframework.stereotype.Service;

@Service
public class CardUpdateMapper {

    public void update(Card card, CardFormDTO cardFormDTO) {
        card.setUniqueNumber(cardFormDTO.uniqueNumber());
        card.setBeneficiaryId(cardFormDTO.beneficiaryId());
    }
}
