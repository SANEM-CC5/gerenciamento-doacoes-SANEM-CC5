package com.javalovers.modules.withdrawal.application.mapper;

import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.beneficiary.domain.entity.Beneficiary;
import com.javalovers.modules.withdrawal.application.dto.request.WithdrawalFormDTO;
import com.javalovers.modules.withdrawal.domain.entity.Withdrawal;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalUpdateMapper {

    public void update(Withdrawal withdrawal, WithdrawalFormDTO withdrawalFormDTO, Beneficiary beneficiary, AppUser attendantUser){
        withdrawal.setWithdrawalDate(withdrawalFormDTO.withdrawalDate());
        withdrawal.setBeneficiary(beneficiary);
        withdrawal.setAttendantUser(attendantUser);
    }

}
