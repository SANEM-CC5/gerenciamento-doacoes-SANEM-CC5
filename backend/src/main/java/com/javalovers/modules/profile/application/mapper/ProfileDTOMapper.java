package com.javalovers.modules.profile.application.mapper;

import com.javalovers.modules.profile.application.dto.response.ProfileDTO;
import com.javalovers.modules.profile.domain.entity.Profile;
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
