package com.sanem.model.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

public record WithdrawalFilterDTO(
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
        String beneficiaryName,
        String attendantName,
        String itemName,
        Long beneficiaryId,
        Long attendantUserId
) {
}