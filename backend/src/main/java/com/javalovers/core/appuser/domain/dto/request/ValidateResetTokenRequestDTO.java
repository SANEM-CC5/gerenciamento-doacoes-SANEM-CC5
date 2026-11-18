package com.javalovers.core.appuser.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Payload para validação prévia do token de redefinição de senha.
 */
public record ValidateResetTokenRequestDTO(
        @NotBlank(message = "Token de recuperação é obrigatório")
        String token
) {
}
