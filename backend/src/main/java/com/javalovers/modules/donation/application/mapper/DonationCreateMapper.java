package com.javalovers.modules.donation.application.mapper;

import com.javalovers.modules.donation.application.dto.request.DonationFormDTO;
import com.javalovers.modules.donation.domain.entity.Donation;
import com.javalovers.modules.donor.domain.entity.Donor;
import com.javalovers.modules.appuser.domain.entity.AppUser;
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
