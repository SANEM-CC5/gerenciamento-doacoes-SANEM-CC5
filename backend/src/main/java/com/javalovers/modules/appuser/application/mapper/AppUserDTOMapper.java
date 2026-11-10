package com.javalovers.modules.appuser.application.mapper;

import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;
import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.profile.application.dto.response.ProfileDTO;
import com.javalovers.modules.profile.application.mapper.ProfileDTOMapper;
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
