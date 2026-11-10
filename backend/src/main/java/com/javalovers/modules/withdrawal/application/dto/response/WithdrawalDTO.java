package com.javalovers.modules.withdrawal.application.dto.response;

import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;
import com.javalovers.modules.beneficiary.application.dto.response.BeneficiaryDTO;
import com.javalovers.modules.itemwithdrawn.application.dto.response.ItemWithdrawnDTO;

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
