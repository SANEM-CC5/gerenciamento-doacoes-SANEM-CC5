package com.sanem.mapper;

import com.sanem.model.dto.DonationFormDTO;
import com.sanem.model.entity.Donation;
import com.sanem.model.entity.Donor;
import com.sanem.model.entity.AppUser;
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
