package com.sanem.mapper;

import com.sanem.model.dto.DonationDTO;
import com.sanem.model.entity.Donation;
import com.sanem.mapper.DonorDTOMapper;
import com.sanem.mapper.AppUserDTOMapper;
import com.sanem.model.dto.ItemDonatedDTO;
import com.sanem.mapper.ItemDonatedDTOMapper;
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
