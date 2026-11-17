package com.sanem.mapper;

import com.sanem.model.dto.ProfileDTO;
import com.sanem.model.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileDTOMapper {

    public ProfileDTO convert(Profile profile) {
        if(profile == null) return null;
        return new ProfileDTO(
                profile.getProfileId(),
                profile.getDescription(),
                profile.getName()
        );
    }

}
