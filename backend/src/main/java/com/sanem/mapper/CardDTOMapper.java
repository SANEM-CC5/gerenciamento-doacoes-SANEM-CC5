package com.sanem.mapper;

import com.sanem.model.dto.CardDTO;
import com.sanem.model.entity.Card;
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
