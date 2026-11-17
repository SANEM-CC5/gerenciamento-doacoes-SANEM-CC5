package com.javalovers.mapper;

import com.javalovers.model.dto.CardFormDTO;
import com.javalovers.model.entity.Card;
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
