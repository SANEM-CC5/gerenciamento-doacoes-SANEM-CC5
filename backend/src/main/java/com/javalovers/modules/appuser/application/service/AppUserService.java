package com.javalovers.modules.appuser.application.service;

import com.javalovers.shared.exception.EntityNotFoundException;
import com.javalovers.modules.appuser.application.dto.request.AppUserFormDTO;
import com.javalovers.modules.appuser.application.dto.request.AppUserUpdateDTO;
import com.javalovers.modules.appuser.application.dto.response.AppUserDTO;
import com.javalovers.modules.appuser.domain.entity.AppUser;
import com.javalovers.modules.appuser.application.mapper.AppUserCreateMapper;
import com.javalovers.modules.appuser.application.mapper.AppUserDTOMapper;
import com.javalovers.modules.appuser.application.mapper.AppUserUpdateMapper;
import com.javalovers.modules.appuser.infrastructure.persistence.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppUserCreateMapper appUserCreateMapper;
    private final AppUserDTOMapper appUserDTOMapper;
    private final AppUserUpdateMapper appUserUpdateMapper;
    private final AuthService authService;

    public AppUser generateUser(AppUserFormDTO appUserFormDTO) {
        return appUserCreateMapper.convert(appUserFormDTO);
    }

    public void save (AppUser user) {
        appUserRepository.save(user);
    }

    public AppUserDTO generateUserDTO(AppUser user) {
        return appUserDTOMapper.convert(user);
    }

    public AppUser getOrNull(Long id){
        return appUserRepository.findById(id).orElse(null);
    }

    public void updateUser(AppUser user, AppUserFormDTO appUserFormDTO) {
        appUserUpdateMapper.update(user, appUserFormDTO);
    }

    public void updateUser(AppUser user, AppUserUpdateDTO appUserUpdateDTO) {
        appUserUpdateMapper.update(user, appUserUpdateDTO);
    }

    public void delete(AppUser user) {
        AppUser usuarioLogado = authService.getCurrentUser();

        if (usuarioLogado.getUserId().equals(user.getUserId())) {
            throw new RuntimeException("Você não pode deletar sua própria conta");
        }

        try {
            appUserRepository.delete(user);
        } catch (Exception e) {
            throw new RuntimeException(
                "Não é possível excluir usuário vinculado a doações."
            );
        }
    }

    public List<AppUser> list() {
        return appUserRepository.findAll();
    }

    public Page<AppUser> list(Pageable pageable) {
        return appUserRepository.findAll(pageable);
    }

    public Page<AppUserDTO> generateUserDTOPage(Page<AppUser> userPage) {
        return userPage.map(this::generateUserDTO);
    }

    public List<AppUserDTO> generateUserDTOList(List<AppUser> userList) {
        return userList.stream().map(appUserDTOMapper::convert).toList();
    }

    public AppUser getOrThrowException(Long id) {
        return appUserRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User", id)
        );
    }

}