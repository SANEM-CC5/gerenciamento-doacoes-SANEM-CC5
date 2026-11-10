package com.javalovers.modules.card.application.dto.request;

public record CardFilterDTO(
        String uniqueNumber,
        Long beneficiaryId
) {
}
