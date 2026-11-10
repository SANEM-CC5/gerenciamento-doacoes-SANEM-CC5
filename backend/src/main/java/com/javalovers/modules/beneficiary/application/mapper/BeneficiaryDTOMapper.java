package com.javalovers.modules.beneficiary.application.mapper;

import com.javalovers.modules.beneficiary.application.dto.response.BeneficiaryDTO;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import com.javalovers.modules.withdrawal.application.service.WithdrawalService;
import com.javalovers.modules.appuser.application.mapper.AppUserDTOMapper;
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
