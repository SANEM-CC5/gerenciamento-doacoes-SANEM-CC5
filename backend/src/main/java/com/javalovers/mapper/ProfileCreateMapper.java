package com.javalovers.mapper;

import com.javalovers.model.dto.ProfileFormDTO;
import com.javalovers.model.entity.Profile;
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
