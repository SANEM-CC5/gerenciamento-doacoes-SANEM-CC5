package com.sanem.mapper;

import com.sanem.model.dto.ProfileFormDTO;
import com.sanem.model.entity.Profile;
import org.springframework.stereotype.Service;

@Service
public class ProfileUpdateMapper {

    public void update(Profile profile, ProfileFormDTO profileFormDTO){
        profile.setName(profileFormDTO.name());
        profile.setDescription(profileFormDTO.description());
    }

}
