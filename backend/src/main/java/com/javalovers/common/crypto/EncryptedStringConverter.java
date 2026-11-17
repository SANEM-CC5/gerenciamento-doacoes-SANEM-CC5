package com.javalovers.common.crypto;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * RNF12 – Converte automaticamente o campo: criptografa ao gravar no banco e
 * descriptografa ao ler. O restante do sistema (DTOs, telas, PDF da carteirinha)
 * continua trabalhando com o valor em claro, sem nenhuma alteração.
 *
 * Uso: {@code @Convert(converter = EncryptedStringConverter.class)} no atributo da entidade.
 */
@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return DataEncryptionService.encryptor().encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return DataEncryptionService.encryptor().decrypt(dbData);
    }
}
