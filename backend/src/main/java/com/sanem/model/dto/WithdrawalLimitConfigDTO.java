package com.sanem.model.dto;

import java.util.Date;

public record WithdrawalLimitConfigDTO(
        Long configId,
        Integer monthlyItemLimit,
        Boolean isActive,
        Date createdAt,
        Date updatedAt
) {
}

