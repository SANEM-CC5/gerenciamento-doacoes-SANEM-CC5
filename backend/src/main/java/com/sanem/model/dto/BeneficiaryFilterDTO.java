package com.sanem.model.dto;

import com.sanem.model.enums.BeneficiaryStatus;

public record BeneficiaryFilterDTO (
        String fullName,
        String cpf,
        String phone,
        String socioeconomicData,
        BeneficiaryStatus beneficiaryStatus
){
}
