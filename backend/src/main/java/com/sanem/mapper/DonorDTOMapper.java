package com.sanem.mapper;

import com.sanem.model.dto.DonorDTO;
import com.sanem.model.entity.Donor;
import org.springframework.stereotype.Service;

@Service
public class DonorDTOMapper {

    public DonorDTO convert(Donor donor){
        if(donor == null) return null;
        return new DonorDTO(
                donor.getDonorId(),
                donor.getName(),
                donor.getCpfCnpj(),
                donor.getContact(),
                donor.getEmail()
        );
    }
}
