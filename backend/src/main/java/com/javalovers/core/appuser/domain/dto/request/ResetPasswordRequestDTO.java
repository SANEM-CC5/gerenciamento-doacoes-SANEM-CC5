package com.javalovers.core.appuser.domain.dto.request;

import com.javalovers.core.appuser.domain.validation.PasswordPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Payload para conclusão da redefinição de senha antes do login.
 */
public record ResetPasswordRequestDTO(
        @NotBlank(message = "Token de recuperação é obrigatório")
        String token,

        @NotBlank(message = "Nova senha é obrigatória")
        @Pattern(
                regexp = PasswordPolicy.PASSWORD_REGEX,
                message = PasswordPolicy.PASSWORD_MESSAGE
        )
        String newPassword,

        @NotBlank(message = "Confirmação da nova senha é obrigatória")
        String confirmPassword
) {
}
