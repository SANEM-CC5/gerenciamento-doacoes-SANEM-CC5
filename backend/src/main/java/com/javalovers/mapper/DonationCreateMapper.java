package com.javalovers.mapper;

import com.javalovers.model.dto.DonationFormDTO;
import com.javalovers.model.entity.Donation;
import com.javalovers.model.entity.Donor;
import com.javalovers.model.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DonationCreateMapper {

    public Donation convert(DonationFormDTO donationFormDTO, Donor donor, AppUser user) {

        Donation donation = new Donation();
        donation.setDonationDate(donationFormDTO.donationDate());
        donation.setDonor(donor);
        donation.setReceiverUser(user);

        return donation;
    }
}
