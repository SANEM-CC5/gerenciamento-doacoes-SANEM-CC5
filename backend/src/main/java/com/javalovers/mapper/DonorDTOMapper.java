package com.javalovers.mapper;

import com.javalovers.model.dto.DonorDTO;
import com.javalovers.model.entity.Donor;
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
