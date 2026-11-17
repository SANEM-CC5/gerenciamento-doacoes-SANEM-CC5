package com.sanem.model.dto;

import com.sanem.model.dto.AppUserDTO;
import com.sanem.model.dto.BeneficiaryDTO;
import com.sanem.model.dto.ItemWithdrawnDTO;

import java.util.Date;
import java.util.List;

public record WithdrawalDTO(
        Long withdrawalId,
        Date withdrawalDate,
        BeneficiaryDTO beneficiary,
        AppUserDTO appUser,
        List<ItemWithdrawnDTO> items
) {
}
