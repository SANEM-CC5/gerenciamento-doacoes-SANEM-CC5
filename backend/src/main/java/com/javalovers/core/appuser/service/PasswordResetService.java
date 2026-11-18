package com.javalovers.core.appuser.service;

import com.javalovers.core.appuser.domain.dto.request.ResetPasswordRequestDTO;
import com.javalovers.core.appuser.domain.entity.AppUser;
import com.javalovers.core.appuser.domain.entity.PasswordResetToken;
import com.javalovers.core.appuser.repository.AppUserRepository;
import com.javalovers.core.appuser.repository.PasswordResetTokenRepository;
import com.javalovers.core.status.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final AppUserRepository appUserRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetDeliveryService deliveryService;

    @Value("${app.password-reset.expiration-minutes:30}")
    private long expirationMinutes = 30;

    @Transactional
    public void requestPasswordReset(String email) {
        if (email == null || email.isBlank()) {
            return;
        }

        Optional<AppUser> userOpt = appUserRepository.findByEmailIgnoreCaseAndDeletedAtIsNull(email.trim());
        if (userOpt.isEmpty()) {
            return;
        }

        AppUser user = userOpt.get();
        if (user.getStatus() != Status.ACTIVE) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        tokenRepository.invalidateActiveTokensByUserId(user.getUserId(), now);

        String rawToken = generateOpaqueToken();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashToken(rawToken));
        resetToken.setExpiresAt(now.plusMinutes(expirationMinutes));
        resetToken.setCreatedAt(now);
        tokenRepository.save(resetToken);

        deliveryService.sendPasswordResetEmail(user, rawToken);
    }

    @Transactional(readOnly = true)
    public boolean isResetTokenValid(String rawToken) {
        return findValidToken(rawToken, LocalDateTime.now()).isPresent();
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (!dto.newPassword().equals(dto.confirmPassword())) {
            throw new IllegalArgumentException("A confirmação da nova senha não confere.");
        }

        LocalDateTime now = LocalDateTime.now();
        PasswordResetToken resetToken = findValidToken(dto.token(), now)
                .orElseThrow(() -> new IllegalArgumentException("Link de recuperação inválido ou expirado."));

        AppUser user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        appUserRepository.save(user);

        resetToken.setUsedAt(now);
        tokenRepository.save(resetToken);
    }

    private Optional<PasswordResetToken> findValidToken(String rawToken, LocalDateTime now) {
        if (rawToken == null || rawToken.isBlank()) {
            return Optional.empty();
        }

        String tokenHash = hashToken(rawToken.trim());
        return tokenRepository.findByTokenHashWithUser(tokenHash)
                .filter(token -> token.isValidAt(now))
                .filter(token -> {
                    AppUser user = token.getUser();
                    return user != null && user.getDeletedAt() == null && user.getStatus() == Status.ACTIVE;
                });
    }

    private String generateOpaqueToken() {
        byte[] randomBytes = new byte[TOKEN_BYTE_LENGTH];
        SECURE_RANDOM.nextBytes(randomBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes);
    }

    static String hashToken(String rawToken) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawToken.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível", e);
        }
    }
}
