package com.javalovers.modules.card.application.dto.request;

public record CardFormDTO(
        String uniqueNumber,
        Long beneficiaryId
) {
}
