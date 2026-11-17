package com.javalovers.common.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * RNF12 – Ponto único de acesso à criptografia de dados pessoais.
 *
 * A chave vem de {@code sanem.security.encryption-key} (variável SANEM_ENCRYPTION_KEY).
 * Sem chave a aplicação NÃO inicia: é preferível falhar a gravar dados pessoais em texto puro.
 *
 * O encriptador também fica disponível de forma estática para o {@link EncryptedStringConverter}
 * e para os callbacks da entidade, que são instanciados pelo Hibernate fora do Spring.
 */
@Component
public class DataEncryptionService {

    private static volatile FieldEncryptor current;

    private final FieldEncryptor encryptor;

    public DataEncryptionService(@Value("${sanem.security.encryption-key:}") String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalStateException(
                    "RNF12/LGPD: a variável SANEM_ENCRYPTION_KEY não foi configurada. " +
                    "Gere uma chave com 'openssl rand -base64 32' e informe-a no arquivo .env.");
        }
        this.encryptor = FieldEncryptor.fromBase64(base64Key);
        current = this.encryptor;
    }

    /** Acesso para código instanciado pelo Hibernate (converters e callbacks de entidade). */
    public static FieldEncryptor encryptor() {
        FieldEncryptor instance = current;
        if (instance == null) {
            throw new IllegalStateException("Serviço de criptografia ainda não inicializado.");
        }
        return instance;
    }

    public String encrypt(String plainText) {
        return encryptor.encrypt(plainText);
    }

    public String decrypt(String storedValue) {
        return encryptor.decrypt(storedValue);
    }

    /** Índice de busca do documento (CPF/CRNM/NIF) – aceita valor com ou sem máscara. */
    public String documentIndex(String document) {
        return encryptor.blindIndex(FieldEncryptor.normalizeDocument(document));
    }
}
