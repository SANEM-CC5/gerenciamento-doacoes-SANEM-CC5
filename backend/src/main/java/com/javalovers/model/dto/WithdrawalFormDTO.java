package com.javalovers.model.dto;

import java.util.Date;
import java.util.List;

public record WithdrawalFormDTO(
        Date withdrawalDate,
        Long beneficiaryId,
        Long attendantUserId,
        List<WithdrawalItemDTO> items
) {
    public record WithdrawalItemDTO(
            Long itemId,
            Integer quantity
    ) {}
}
