package com.sanem.mapper;

import com.sanem.model.dto.DonorFormDTO;
import com.sanem.model.entity.Donor;
import org.springframework.stereotype.Service;

@Service
public class DonorUpdateMapper {

    public void update(Donor donor, DonorFormDTO donorFormDTO){
        donor.setName(donorFormDTO.name());
        donor.setCpfCnpj(donorFormDTO.cpfCnpj());
        donor.setContact(donorFormDTO.contact());
        donor.setEmail(donorFormDTO.email());
        
    }
}
