package com.sanem.mapper;

import com.sanem.model.dto.ProfileFormDTO;
import com.sanem.model.entity.Profile;
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
