package com.javalovers.mapper;

import com.javalovers.model.dto.DonationDTO;
import com.javalovers.model.entity.Donation;
import com.javalovers.mapper.DonorDTOMapper;
import com.javalovers.mapper.AppUserDTOMapper;
import com.javalovers.model.dto.ItemDonatedDTO;
import com.javalovers.mapper.ItemDonatedDTOMapper;
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
