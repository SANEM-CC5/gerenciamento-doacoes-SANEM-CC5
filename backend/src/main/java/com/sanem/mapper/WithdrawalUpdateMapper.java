package com.sanem.mapper;

import com.sanem.model.entity.AppUser;
import com.sanem.model.entity.Beneficiary;
import com.sanem.model.dto.WithdrawalFormDTO;
import com.sanem.model.entity.Withdrawal;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalUpdateMapper {

    public void update(Withdrawal withdrawal, WithdrawalFormDTO withdrawalFormDTO, Beneficiary beneficiary, AppUser attendantUser){
        withdrawal.setWithdrawalDate(withdrawalFormDTO.withdrawalDate());
        withdrawal.setBeneficiary(beneficiary);
        withdrawal.setAttendantUser(attendantUser);
    }

}
