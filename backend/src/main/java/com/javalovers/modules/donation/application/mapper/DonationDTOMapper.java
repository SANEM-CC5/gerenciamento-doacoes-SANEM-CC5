package com.javalovers.modules.donation.application.mapper;

import com.javalovers.modules.donation.application.dto.response.DonationDTO;
import com.javalovers.modules.donation.domain.entity.Donation;
import com.javalovers.modules.donor.application.mapper.DonorDTOMapper;
import com.javalovers.modules.appuser.application.mapper.AppUserDTOMapper;
import com.javalovers.modules.itemdonated.application.dto.response.ItemDonatedDTO;
import com.javalovers.modules.itemdonated.application.mapper.ItemDonatedDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DonationDTOMapper {

    private final DonorDTOMapper donorDTOMapper;
    private final AppUserDTOMapper appUserDTOMapper;
    private final ItemDonatedDTOMapper itemDonatedDTOMapper;

    public DonationDTO convert(Donation donation) {
        if(donation == null) return null;

        List<ItemDonatedDTO> itemsDTO = null;
        if (donation.getItems() != null) {
            itemsDTO = donation.getItems().stream()
                    .map(itemDonatedDTOMapper::convert)
                    .toList();
        }

        return new DonationDTO(
                donation.getDonationId(),
                donation.getDonationDate(),
                appUserDTOMapper.convert(donation.getReceiverUser()),
                donorDTOMapper.convert(donation.getDonor()),
                itemsDTO
        );
    }

}
