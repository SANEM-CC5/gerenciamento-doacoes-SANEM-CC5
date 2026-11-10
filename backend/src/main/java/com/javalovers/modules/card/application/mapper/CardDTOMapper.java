package com.javalovers.modules.card.application.mapper;

import com.javalovers.modules.card.application.dto.response.CardDTO;
import com.javalovers.modules.card.domain.entity.Card;
import org.springframework.stereotype.Service;

@Service
public class CardDTOMapper {

    public CardDTO convert(Card card){
        if(card == null) return null;
        return new CardDTO(
                card.getCardId(),
                card.getUniqueNumber(),
                card.getBeneficiaryId(),
                card.getIssueDate()
        );
    }
}
