package com.javalovers.model.dto;

import com.javalovers.model.dto.DonorDTO;
import com.javalovers.model.dto.AppUserDTO;
import com.javalovers.model.dto.ItemDonatedDTO;

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
