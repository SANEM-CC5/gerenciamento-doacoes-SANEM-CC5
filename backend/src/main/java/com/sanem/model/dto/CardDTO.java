package com.sanem.model.dto;

import java.util.Date;

public record CardDTO(
        Long cardId,
        String uniqueNumber,
        Long beneficiaryId,
        Date issueDate
) {
}
