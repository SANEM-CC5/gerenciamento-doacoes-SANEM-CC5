package com.javalovers.model.dto;

import com.javalovers.model.enums.BeneficiaryStatus;
import com.javalovers.model.dto.AppUserDTO;

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
