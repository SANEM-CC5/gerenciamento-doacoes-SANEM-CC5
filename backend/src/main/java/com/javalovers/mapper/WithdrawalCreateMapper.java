package com.javalovers.mapper;

import com.javalovers.model.entity.AppUser;
import com.javalovers.model.entity.Beneficiary;
import com.javalovers.model.dto.WithdrawalFormDTO;
import com.javalovers.model.entity.Withdrawal;
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
