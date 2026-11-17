package com.sanem.mapper;

import com.sanem.model.dto.BeneficiaryDTO;
import com.sanem.model.entity.Beneficiary;
import com.sanem.service.WithdrawalService;
import com.sanem.mapper.AppUserDTOMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class BeneficiaryDTOMapper {

    private final AppUserDTOMapper appUserDTOMapper;
    private final WithdrawalService withdrawalService;

    public BeneficiaryDTOMapper(AppUserDTOMapper appUserDTOMapper, @Lazy WithdrawalService withdrawalService) {
        this.appUserDTOMapper = appUserDTOMapper;
        this.withdrawalService = withdrawalService;
    }

    public BeneficiaryDTO convert(Beneficiary beneficiary){
        if(beneficiary == null) return null;

        int totalLimit = withdrawalService.getMonthlyLimit(beneficiary.getBeneficiaryId());
        int currentWithdrawals = withdrawalService.getItemsWithdrawnThisMonth(beneficiary.getBeneficiaryId());
        int remainingWithdrawals = Math.max(0, totalLimit - currentWithdrawals);

        return new BeneficiaryDTO(
                beneficiary.getBeneficiaryId(),
                beneficiary.getFullName(),
                beneficiary.getCpf(),
                beneficiary.getPhone(),
                beneficiary.getSocioeconomicData(),
                beneficiary.getBeneficiaryStatus(),
                appUserDTOMapper.convert(beneficiary.getApproverId()),
                totalLimit,
                currentWithdrawals,
                remainingWithdrawals
        );
    }
}
