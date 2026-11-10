package com.javalovers.modules.card.application.dto.response;

import java.util.Date;

public record CardDTO(
        Long cardId,
        String uniqueNumber,
        Long beneficiaryId,
        Date issueDate
) {
}
