package com.sanem.mapper;

import com.sanem.model.entity.AppUser;
import com.sanem.model.entity.Beneficiary;
import com.sanem.model.dto.WithdrawalFormDTO;
import com.sanem.model.entity.Withdrawal;
import org.springframework.stereotype.Service;

@Service
public class WithdrawalCreateMapper {

    public Withdrawal convert(WithdrawalFormDTO withdrawalFormDTO, Beneficiary beneficiary, AppUser attendantUser) {
        Withdrawal withdrawal = new Withdrawal();

        withdrawal.setWithdrawalDate(withdrawalFormDTO.withdrawalDate());
        withdrawal.setBeneficiary(beneficiary);
        withdrawal.setAttendantUser(attendantUser);

        return withdrawal;
    }

}
