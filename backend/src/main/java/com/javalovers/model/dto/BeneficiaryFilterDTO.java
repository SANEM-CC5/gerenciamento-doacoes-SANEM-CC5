package com.javalovers.model.dto;

import com.javalovers.model.enums.BeneficiaryStatus;

public record BeneficiaryFilterDTO (
        String fullName,
        String cpf,
        String phone,
        String socioeconomicData,
        BeneficiaryStatus beneficiaryStatus
){
}
