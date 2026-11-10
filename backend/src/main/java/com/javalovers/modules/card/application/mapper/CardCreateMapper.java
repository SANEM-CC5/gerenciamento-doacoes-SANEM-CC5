package com.javalovers.modules.card.application.mapper;

import com.javalovers.modules.card.application.dto.request.CardFormDTO;
import com.javalovers.modules.card.domain.entity.Card;
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
