package com.javalovers.model.dto;

import com.javalovers.model.dto.AppUserDTO;
import com.javalovers.model.dto.BeneficiaryDTO;
import com.javalovers.model.dto.ItemWithdrawnDTO;

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
