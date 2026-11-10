package com.javalovers.modules.withdrawallimit.application.dto.response;

import java.util.Date;

public record WithdrawalLimitConfigDTO(
        Long configId,
        Integer monthlyItemLimit,
        Boolean isActive,
        Date createdAt,
        Date updatedAt
) {
}

