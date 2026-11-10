package com.javalovers.modules.beneficiary.application.mapper;

import com.javalovers.modules.beneficiary.application.dto.request.BeneficiaryFormDTO;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryCreateMapper {

    public Beneficiary convert(BeneficiaryFormDTO beneficiaryFormDTO) {
        Beneficiary beneficiary = new Beneficiary();
        beneficiary.setFullName(beneficiaryFormDTO.fullName());
        beneficiary.setCpf(beneficiaryFormDTO.cpf());
        beneficiary.setPhone(beneficiaryFormDTO.phone());
        beneficiary.setSocioeconomicData(beneficiaryFormDTO.socioeconomicData());
        beneficiary.setBeneficiaryStatus(beneficiaryFormDTO.beneficiaryStatus());
        beneficiary.setWithdrawalLimit(beneficiaryFormDTO.withdrawalLimit());

        return beneficiary;
    }
}
