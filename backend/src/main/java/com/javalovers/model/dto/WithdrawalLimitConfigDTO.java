package com.javalovers.model.dto;

import java.util.Date;

public record WithdrawalLimitConfigDTO(
        Long configId,
        Integer monthlyItemLimit,
        Boolean isActive,
        Date createdAt,
        Date updatedAt
) {
}

