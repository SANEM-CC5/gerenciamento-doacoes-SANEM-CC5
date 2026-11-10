package com.javalovers.modules.donor.application.mapper;

import com.javalovers.modules.donor.application.dto.response.DonorDTO;
import com.javalovers.modules.donor.domain.entity.Donor;
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
