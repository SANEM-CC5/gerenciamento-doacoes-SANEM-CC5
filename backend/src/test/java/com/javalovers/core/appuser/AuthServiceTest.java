package com.javalovers.core.appuser;

import com.javalovers.common.security.JwtService;
import com.javalovers.core.appuser.domain.dto.request.LoginRequestDTO;
import com.javalovers.core.appuser.domain.dto.response.LoginResponseDTO;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.profile.domain.entity.Profile;
import com.javalovers.core.appuser.repository.AppUserRepository;
import com.javalovers.core.appuser.service.AuthService;
import com.javalovers.core.status.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AppUser activeUser;

    @BeforeEach
    void setUp() {
        Profile profile = new Profile();
        profile.setProfileId(1L);
        profile.setName("Administrator");

        activeUser = new AppUser();
        activeUser.setUserId(1L);
        activeUser.setName("Isadora");
        activeUser.setLogin("isadora");
        activeUser.setEmail("isadora@gmail.com");
        activeUser.setPasswordHash("hashed-password");
        activeUser.setStatus(Status.ACTIVE);
        activeUser.setProfile(profile);
    }

    @Test
    @DisplayName("authenticate deve permitir login pelo username")
    void testAuthenticateByUsername() {
        LoginRequestDTO request = new LoginRequestDTO("isadora", "Isadora@2026");

        when(appUserRepository.findByLoginAndDeletedAtIsNull("isadora")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("Isadora@2026", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("isadora")).thenReturn("mock-jwt-token");

        LoginResponseDTO response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Isadora", response.getName());
        assertEquals("isadora@gmail.com", response.getEmail());
        assertEquals(1L, response.getUserId());
    }

    @Test
    @DisplayName("authenticate deve permitir login pelo e-mail")
    void testAuthenticateByEmail() {
        LoginRequestDTO request = new LoginRequestDTO("isadora@gmail.com", "Isadora@2026");

        when(appUserRepository.findByLoginAndDeletedAtIsNull("isadora@gmail.com")).thenReturn(Optional.empty());
        when(appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("isadora@gmail.com")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("Isadora@2026", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken("isadora")).thenReturn("mock-jwt-token");

        LoginResponseDTO response = authService.authenticate(request);

        assertNotNull(response);
        assertEquals("mock-jwt-token", response.getToken());
        assertEquals("Isadora", response.getName());
    }

    @Test
    @DisplayName("authenticate deve falhar quando usuário ou e-mail não existirem")
    void testAuthenticateUserNotFound() {
        LoginRequestDTO request = new LoginRequestDTO("unknown", "Pass@123");

        when(appUserRepository.findByLoginAndDeletedAtIsNull("unknown")).thenReturn(Optional.empty());
        when(appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Usuário ou senha incorretos", ex.getMessage());
    }

    @Test
    @DisplayName("authenticate deve falhar quando a senha for incorreta")
    void testAuthenticateWrongPassword() {
        LoginRequestDTO request = new LoginRequestDTO("isadora", "Wrong@123");

        when(appUserRepository.findByLoginAndDeletedAtIsNull("isadora")).thenReturn(Optional.of(activeUser));
        when(passwordEncoder.matches("Wrong@123", "hashed-password")).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.authenticate(request));
        assertEquals("Usuário ou senha incorretos", ex.getMessage());
    }
}
