package com.javalovers.modules.profile.application.mapper;

import com.javalovers.modules.profile.application.dto.request.ProfileFormDTO;
import com.javalovers.modules.profile.domain.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileCreateMapper {

    public Profile convert(ProfileFormDTO profileFormDTO){
        Profile profile = new Profile();
        profile.setDescription(profileFormDTO.description());
        profile.setName(profileFormDTO.name());

        return profile;
    }
}
