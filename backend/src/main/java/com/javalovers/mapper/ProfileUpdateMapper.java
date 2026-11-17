package com.javalovers.mapper;

import com.javalovers.model.dto.ProfileFormDTO;
import com.javalovers.model.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileUpdateMapper {

    public void update(Profile profile, ProfileFormDTO profileFormDTO){
        profile.setName(profileFormDTO.name());
        profile.setDescription(profileFormDTO.description());
    }

}
