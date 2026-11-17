package com.sanem.mapper;

import com.sanem.model.dto.CardFormDTO;
import com.sanem.model.entity.Card;
import org.springframework.stereotype.Service;

@Service
public class CardUpdateMapper {

    public void update(Card card, CardFormDTO cardFormDTO) {
        card.setUniqueNumber(cardFormDTO.uniqueNumber());
        card.setBeneficiaryId(cardFormDTO.beneficiaryId());
    }
}
