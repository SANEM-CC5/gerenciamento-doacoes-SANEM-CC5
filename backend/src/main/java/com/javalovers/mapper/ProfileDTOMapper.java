package com.javalovers.mapper;

import com.javalovers.model.dto.ProfileDTO;
import com.javalovers.model.entity.Profile;
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
