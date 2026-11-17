package com.sanem.model.dto;

import com.sanem.model.enums.BeneficiaryStatus;
import com.sanem.model.dto.AppUserDTO;

public record BeneficiaryDTO (
        Long beneficiaryId,
        String fullName,
        String cpf,
        String phone,
        String socioeconomicData,
        BeneficiaryStatus beneficiaryStatus,
        AppUserDTO approverId,
        Integer withdrawalLimit,
        Integer currentWithdrawalsThisMonth,
        Integer remainingWithdrawals
){
}
