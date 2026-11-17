package com.sanem.model.dto;

import com.sanem.model.dto.DonorDTO;
import com.sanem.model.dto.AppUserDTO;
import com.sanem.model.dto.ItemDonatedDTO;

import java.util.Date;
import java.util.List;

public record DonationDTO(
        Long donationId,
        Date donationDate,
        AppUserDTO receiverUser,
        DonorDTO donor,
        List<ItemDonatedDTO> items
) {
}
