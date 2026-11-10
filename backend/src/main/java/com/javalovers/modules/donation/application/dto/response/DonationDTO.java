package com.javalovers.modules.donation.application.dto.response;

import com.javalovers.modules.donor.application.dto.response.DonorDTO;
import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;
import com.javalovers.modules.itemdonated.application.dto.response.ItemDonatedDTO;

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
