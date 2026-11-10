package com.javalovers.modules.profile.application.mapper;

import com.javalovers.modules.profile.application.dto.request.ProfileFormDTO;
import com.javalovers.modules.profile.domain.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileUpdateMapper {

    public void update(Profile profile, ProfileFormDTO profileFormDTO){
        profile.setName(profileFormDTO.name());
        profile.setDescription(profileFormDTO.description());
    }

}
