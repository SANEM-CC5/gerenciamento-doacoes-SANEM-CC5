package com.sanem.mapper;

import com.sanem.model.dto.DonorFormDTO;
import com.sanem.model.entity.Donor;
import org.springframework.stereotype.Service;

@Service
public class DonorCreateMapper {

    public Donor convert(DonorFormDTO donorFormDTO){
        Donor donor = new Donor();
        donor.setName(donorFormDTO.name());
        donor.setCpfCnpj(donorFormDTO.cpfCnpj());
        donor.setContact(donorFormDTO.contact());
        donor.setEmail(donorFormDTO.email());

        return donor;
    }
}
