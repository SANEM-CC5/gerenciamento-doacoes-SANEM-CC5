package com.javalovers.mapper;

import com.javalovers.model.dto.AppUserDTO;
import com.javalovers.model.entity.AppUser;
import com.javalovers.model.dto.ProfileDTO;
import com.javalovers.mapper.ProfileDTOMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppUserDTOMapper {

    private final ProfileDTOMapper profileDTOMapper;

    public AppUserDTO convert(AppUser user) {
        if (user == null)
            return null;
        ProfileDTO profileDTO = user.getProfile() != null 
            ? profileDTOMapper.convert(user.getProfile()) 
            : null;
        return new AppUserDTO(
                user.getUserId(),
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getStatus(),
                profileDTO);
    }
}
