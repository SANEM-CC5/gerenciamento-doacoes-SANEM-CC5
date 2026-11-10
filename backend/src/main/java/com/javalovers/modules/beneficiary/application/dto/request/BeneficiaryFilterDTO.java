package com.javalovers.modules.beneficiary.application.dto.request;

import com.javalovers.modules.beneficiary.domain.enums.BeneficiaryStatus;

public record BeneficiaryFilterDTO (
        String fullName,
        String cpf,
        String phone,
        String socioeconomicData,
        BeneficiaryStatus beneficiaryStatus
){
}
