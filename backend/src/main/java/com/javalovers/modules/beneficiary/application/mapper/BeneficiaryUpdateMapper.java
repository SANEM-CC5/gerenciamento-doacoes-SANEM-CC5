package com.javalovers.modules.beneficiary.application.mapper;

import com.javalovers.modules.beneficiary.application.dto.request.BeneficiaryFormDTO;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryUpdateMapper {

    public void update(Beneficiary beneficiary, BeneficiaryFormDTO beneficiaryFormDTO) {

        beneficiary.setFullName(beneficiaryFormDTO.fullName());
        beneficiary.setCpf(beneficiaryFormDTO.cpf());
        beneficiary.setPhone(beneficiaryFormDTO.phone());
        beneficiary.setSocioeconomicData(beneficiaryFormDTO.socioeconomicData());
        beneficiary.setBeneficiaryStatus(beneficiaryFormDTO.beneficiaryStatus());
        beneficiary.setWithdrawalLimit(beneficiaryFormDTO.withdrawalLimit());
    }
}
