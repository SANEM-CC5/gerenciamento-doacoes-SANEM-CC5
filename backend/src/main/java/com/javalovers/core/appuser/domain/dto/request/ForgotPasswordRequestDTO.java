package com.javalovers.core.appuser.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Payload para solicitação de recuperação de senha antes do login.
 */
public record ForgotPasswordRequestDTO(
        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail deve ter um formato válido")
        String email
) {
}
