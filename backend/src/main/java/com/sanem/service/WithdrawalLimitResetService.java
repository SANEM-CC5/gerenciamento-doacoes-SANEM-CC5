package com.sanem.service;

import com.sanem.model.entity.Beneficiary;
import com.sanem.service.BeneficiaryService;
import com.sanem.model.entity.WithdrawalLimitReset;
import com.sanem.repository.WithdrawalLimitResetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WithdrawalLimitResetService {

    private final WithdrawalLimitResetRepository withdrawalLimitResetRepository;
    private final BeneficiaryService beneficiaryService;

    public void resetLimitForBeneficiary(Long beneficiaryId) {
        Beneficiary beneficiary = beneficiaryService.getOrThrowException(beneficiaryId);

        WithdrawalLimitReset reset = WithdrawalLimitReset.builder()
                .beneficiary(beneficiary)
                .resetDate(new Date())
                .build();

        withdrawalLimitResetRepository.save(reset);
    }

    public Optional<Date> getLastResetDate(Long beneficiaryId) {
        return withdrawalLimitResetRepository.findLastResetDateByBeneficiaryId(beneficiaryId);
    }
}