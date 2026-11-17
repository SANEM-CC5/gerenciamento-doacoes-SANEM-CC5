package com.javalovers.mapper;

import com.javalovers.model.dto.CardFormDTO;
import com.javalovers.model.entity.Card;
import org.springframework.stereotype.Service;

@Service
public class CardUpdateMapper {

    public void update(Card card, CardFormDTO cardFormDTO) {
        card.setUniqueNumber(cardFormDTO.uniqueNumber());
        card.setBeneficiaryId(cardFormDTO.beneficiaryId());
    }
}
