package com.javalovers.core.appuser.domain.validation;

/**
 * Constantes compartilhadas da política de senha forte do sistema.
 */
public final class PasswordPolicy {

    public static final String PASSWORD_REGEX =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    public static final String PASSWORD_MESSAGE =
            "A senha deve ter no mínimo 8 caracteres, uma letra maiúscula, uma minúscula, um número e um caractere especial (@$!%*?&)";

    private PasswordPolicy() {
    }
}
