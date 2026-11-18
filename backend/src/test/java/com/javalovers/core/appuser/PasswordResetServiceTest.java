package com.javalovers.core.appuser;

import com.javalovers.core.appuser.domain.dto.request.ResetPasswordRequestDTO;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.domain.entity.PasswordResetToken;
import com.javalovers.core.appuser.repository.AppUserRepository;
import com.javalovers.core.appuser.repository.PasswordResetTokenRepository;
import com.javalovers.core.appuser.service.PasswordResetDeliveryService;
import com.javalovers.core.appuser.service.PasswordResetService;
import com.javalovers.core.status.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private PasswordResetTokenRepository tokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private PasswordResetDeliveryService deliveryService;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private AppUser buildActiveUser() {
        AppUser user = new AppUser();
        user.setUserId(10L);
        user.setName("Atendente");
        user.setLogin("atendente");
        user.setEmail("atendente@sanem.org");
        user.setPasswordHash("old-hash");
        user.setStatus(Status.ACTIVE);
        return user;
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    @DisplayName("requestPasswordReset deve gerar token opaco, salvar hash e enviar instrução para usuário ativo")
    void testRequestPasswordResetActiveUser() {
        AppUser user = buildActiveUser();
        when(appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("atendente@sanem.org"))
                .thenReturn(Optional.of(user));

        passwordResetService.requestPasswordReset("  atendente@sanem.org  ");

        verify(tokenRepository).invalidateActiveTokensByUserId(eq(10L), any(LocalDateTime.class));

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository).save(tokenCaptor.capture());

        ArgumentCaptor<String> rawTokenCaptor = ArgumentCaptor.forClass(String.class);
        verify(deliveryService).sendPasswordResetEmail(eq(user), rawTokenCaptor.capture());

        PasswordResetToken saved = tokenCaptor.getValue();
        String rawToken = rawTokenCaptor.getValue();
        assertNotNull(rawToken);
        assertFalse(rawToken.isBlank());
        assertEquals(64, saved.getTokenHash().length());
        assertNotEquals(rawToken, saved.getTokenHash());
        assertTrue(saved.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("requestPasswordReset não deve gerar token nem lançar erro quando e-mail não existir ou estiver inativo")
    void testRequestPasswordResetUnknownOrInactiveUser() {
        when(appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("naoexiste@sanem.org"))
                .thenReturn(Optional.empty());

        passwordResetService.requestPasswordReset("naoexiste@sanem.org");

        AppUser inactive = buildActiveUser();
        inactive.setStatus(Status.INACTIVE);
        when(appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull("atendente@sanem.org"))
                .thenReturn(Optional.of(inactive));

        passwordResetService.requestPasswordReset("atendente@sanem.org");

        verify(tokenRepository, never()).save(any());
        verify(deliveryService, never()).sendPasswordResetEmail(any(), any());
    }

    @Test
    @DisplayName("isResetTokenValid deve rejeitar tokens expirados ou já utilizados")
    void testIsResetTokenValidChecksExpiryAndUsage() throws Exception {
        AppUser user = buildActiveUser();
        String rawToken = "sample-token-value";
        String hash = sha256(rawToken);

        PasswordResetToken expired = new PasswordResetToken();
        expired.setUser(user);
        expired.setTokenHash(hash);
        expired.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(tokenRepository.findByTokenHashWithUser(hash)).thenReturn(Optional.of(expired));

        assertFalse(passwordResetService.isResetTokenValid(rawToken));

        PasswordResetToken used = new PasswordResetToken();
        used.setUser(user);
        used.setTokenHash(hash);
        used.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        used.setUsedAt(LocalDateTime.now().minusMinutes(2));
        when(tokenRepository.findByTokenHashWithUser(hash)).thenReturn(Optional.of(used));

        assertFalse(passwordResetService.isResetTokenValid(rawToken));

        PasswordResetToken valid = new PasswordResetToken();
        valid.setUser(user);
        valid.setTokenHash(hash);
        valid.setExpiresAt(LocalDateTime.now().plusMinutes(20));
        when(tokenRepository.findByTokenHashWithUser(hash)).thenReturn(Optional.of(valid));

        assertTrue(passwordResetService.isResetTokenValid(rawToken));
    }

    @Test
    @DisplayName("resetPassword deve atualizar senha e invalidar o token após uso")
    void testResetPasswordSuccess() throws Exception {
        AppUser user = buildActiveUser();
        String rawToken = "valid-reset-token";
        String hash = sha256(rawToken);

        PasswordResetToken validToken = new PasswordResetToken();
        validToken.setTokenId(55L);
        validToken.setUser(user);
        validToken.setTokenHash(hash);
        validToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        when(tokenRepository.findByTokenHashWithUser(hash)).thenReturn(Optional.of(validToken));
        when(passwordEncoder.encode("NovaSenha@2026")).thenReturn("new-bcrypt-hash");

        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(
                rawToken,
                "NovaSenha@2026",
                "NovaSenha@2026"
        );

        passwordResetService.resetPassword(dto);

        assertEquals("new-bcrypt-hash", user.getPasswordHash());
        assertNotNull(validToken.getUsedAt());
        verify(appUserRepository).save(user);
        verify(tokenRepository).save(validToken);
    }

    @Test
    @DisplayName("resetPassword deve rejeitar quando confirmação não conferir")
    void testResetPasswordMismatchConfirmation() {
        ResetPasswordRequestDTO dto = new ResetPasswordRequestDTO(
                "any-token",
                "NovaSenha@2026",
                "OutraSenha@2026"
        );

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> passwordResetService.resetPassword(dto)
        );

        assertEquals("A confirmação da nova senha não confere.", ex.getMessage());
        verify(tokenRepository, never()).findByTokenHashWithUser(any());
    }
}
