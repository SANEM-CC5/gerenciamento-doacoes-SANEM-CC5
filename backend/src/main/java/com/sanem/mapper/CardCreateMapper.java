package com.sanem.mapper;

import com.sanem.model.dto.CardFormDTO;
import com.sanem.model.entity.Card;
import org.springframework.stereotype.Service;

@Service
public class CardCreateMapper {

    public Card convert(CardFormDTO cardFormDTO) {
        Card card = new Card();
        card.setUniqueNumber(cardFormDTO.uniqueNumber());
        card.setBeneficiaryId(cardFormDTO.beneficiaryId());

        return card;
    }

}
