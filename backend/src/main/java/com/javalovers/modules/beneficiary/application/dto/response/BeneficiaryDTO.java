package com.javalovers.modules.beneficiary.application.dto.response;

import com.javalovers.modules.beneficiary.domain.enums.BeneficiaryStatus;
import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;

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
