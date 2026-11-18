package com.javalovers.core.appuser.domain.dto.request;

import com.javalovers.core.appuser.domain.validation.PasswordPolicy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Payload para alteração de senha pelo próprio usuário autenticado.
 */
public record ChangePasswordRequestDTO(
        @NotBlank(message = "Senha atual é obrigatória")
        String currentPassword,

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
