package com.sanem.mapper;

import com.sanem.model.dto.AppUserFormDTO;
import com.sanem.model.dto.AppUserUpdateDTO;
import com.sanem.model.entity.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserUpdateMapper {

    private final PasswordEncoder passwordEncoder;

    public void update(AppUser user, AppUserFormDTO appUserFormDTO) {
        user.setName(appUserFormDTO.name());
        user.setLogin(appUserFormDTO.login());
        user.setEmail(appUserFormDTO.email());
        // Só atualiza a senha se uma nova senha foi fornecida
        if (appUserFormDTO.password() != null && !appUserFormDTO.password().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(appUserFormDTO.password()));
        }
        user.setStatus(appUserFormDTO.status());
        user.setProfile(appUserFormDTO.profile());
    }

    public void update(AppUser user, AppUserUpdateDTO appUserUpdateDTO) {
        user.setName(appUserUpdateDTO.name());
        user.setLogin(appUserUpdateDTO.login());
        user.setEmail(appUserUpdateDTO.email());
        // Só atualiza a senha se uma nova senha foi fornecida
        if (appUserUpdateDTO.password() != null && !appUserUpdateDTO.password().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(appUserUpdateDTO.password()));
        }
        user.setStatus(appUserUpdateDTO.status());
        user.setProfile(appUserUpdateDTO.profile());
    }
}
