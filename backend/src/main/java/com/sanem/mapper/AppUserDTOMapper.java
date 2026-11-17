package com.sanem.mapper;

import com.sanem.model.dto.AppUserDTO;
import com.sanem.model.entity.AppUser;
import com.sanem.model.dto.ProfileDTO;
import com.sanem.mapper.ProfileDTOMapper;
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
