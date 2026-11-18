package com.javalovers.core.appuser;

import com.javalovers.core.appuser.domain.dto.request.ChangePasswordRequestDTO;
import com.javalovers.core.appuser.domain.dto.response.AppUserDTO;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.mapper.AppUserCreateMapper;
import com.javalovers.core.appuser.mapper.AppUserDTOMapper;
import com.javalovers.core.appuser.mapper.AppUserUpdateMapper;
import com.javalovers.core.appuser.repository.AppUserRepository;
import com.javalovers.core.appuser.service.AppUserService;
import com.javalovers.core.appuser.service.AuthService;
import com.javalovers.core.profile.domain.dto.response.ProfileDTO;
import com.javalovers.core.status.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServicePasswordTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AppUserCreateMapper appUserCreateMapper;

    @Mock
    private AppUserDTOMapper appUserDTOMapper;

    @Mock
    private AppUserUpdateMapper appUserUpdateMapper;

    @Mock
    private AuthService authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AppUserService appUserService;

    private AppUser buildUser() {
        AppUser user = new AppUser();
        user.setUserId(1L);
        user.setName("Maria Silva");
        user.setLogin("maria");
        user.setEmail("maria@sanem.org");
        user.setPasswordHash("hashed-old");
        user.setStatus(Status.ACTIVE);
        return user;
    }

    @Test
    @DisplayName("getCurrentUserProfile deve retornar DTO do usuário autenticado")
    void testGetCurrentUserProfile() {
        AppUser user = buildUser();
        AppUserDTO expectedDto = new AppUserDTO(
                1L, "Maria Silva", "maria", "maria@sanem.org", Status.ACTIVE,
                new ProfileDTO(2L, "Attendant", "Atendente")
        );

        when(authService.getCurrentUser()).thenReturn(user);
        when(appUserDTOMapper.convert(user)).thenReturn(expectedDto);

        AppUserDTO result = appUserService.getCurrentUserProfile();

        assertNotNull(result);
        assertEquals("maria@sanem.org", result.email());
        verify(authService, times(1)).getCurrentUser();
    }

    @Test
    @DisplayName("changeCurrentUserPassword deve atualizar hash quando senha atual e confirmação forem válidas")
    void testChangeCurrentUserPasswordSuccess() {
        AppUser user = buildUser();
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                "OldPass@1",
                "NewPass@2",
                "NewPass@2"
        );

        when(authService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("OldPass@1", "hashed-old")).thenReturn(true);
        when(passwordEncoder.matches("NewPass@2", "hashed-old")).thenReturn(false);
        when(passwordEncoder.encode("NewPass@2")).thenReturn("hashed-new");

        appUserService.changeCurrentUserPassword(dto);

        assertEquals("hashed-new", user.getPasswordHash());
        verify(appUserRepository, times(1)).save(user);
    }

    @Test
    @DisplayName("changeCurrentUserPassword deve falhar quando senha atual estiver incorreta")
    void testChangeCurrentUserPasswordWrongCurrentPassword() {
        AppUser user = buildUser();
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                "WrongPass@1",
                "NewPass@2",
                "NewPass@2"
        );

        when(authService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("WrongPass@1", "hashed-old")).thenReturn(false);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> appUserService.changeCurrentUserPassword(dto)
        );

        assertEquals("Senha atual incorreta.", ex.getMessage());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("changeCurrentUserPassword deve falhar quando confirmação não conferir")
    void testChangeCurrentUserPasswordMismatchConfirmation() {
        AppUser user = buildUser();
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                "OldPass@1",
                "NewPass@2",
                "Different@3"
        );

        when(authService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("OldPass@1", "hashed-old")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> appUserService.changeCurrentUserPassword(dto)
        );

        assertEquals("A confirmação da nova senha não confere.", ex.getMessage());
        verify(appUserRepository, never()).save(any());
    }

    @Test
    @DisplayName("changeCurrentUserPassword deve falhar quando nova senha for igual à atual")
    void testChangeCurrentUserPasswordSameAsOld() {
        AppUser user = buildUser();
        ChangePasswordRequestDTO dto = new ChangePasswordRequestDTO(
                "OldPass@1",
                "OldPass@1",
                "OldPass@1"
        );

        when(authService.getCurrentUser()).thenReturn(user);
        when(passwordEncoder.matches("OldPass@1", "hashed-old")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> appUserService.changeCurrentUserPassword(dto)
        );

        assertEquals("A nova senha deve ser diferente da senha atual.", ex.getMessage());
        verify(appUserRepository, never()).save(any());
    }
}
