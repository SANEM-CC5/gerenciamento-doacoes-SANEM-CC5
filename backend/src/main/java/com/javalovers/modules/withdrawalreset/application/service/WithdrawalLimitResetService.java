package com.javalovers.modules.withdrawalreset.application.service;

import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import com.javalovers.modules.beneficiary.application.service.BeneficiaryService;
import com.javalovers.modules.withdrawalreset.domain.entity.WithdrawalLimitReset;
import com.javalovers.modules.withdrawalreset.infrastructure.persistence.repository.WithdrawalLimitResetRepository;
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