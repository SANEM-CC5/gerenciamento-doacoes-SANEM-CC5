package com.sanem.mapper;

import com.sanem.model.dto.BeneficiaryFormDTO;
import com.sanem.model.entity.Beneficiary;
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
