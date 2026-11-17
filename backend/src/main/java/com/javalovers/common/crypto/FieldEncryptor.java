package com.javalovers.common.crypto;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

/**
 * RNF12 / LGPD (art. 46 e art. 6º, VII) – criptografia de campos pessoais.
 *
 * <ul>
 *   <li><b>Criptografia:</b> AES-256-GCM (confidencialidade + integridade), com IV aleatório de 12 bytes
 *       por valor. O mesmo CPF gera textos cifrados diferentes a cada gravação.</li>
 *   <li><b>Formato gravado:</b> {@code enc:v1:<base64(iv || ciphertext || tag)>}. O prefixo permite
 *       identificar dados ainda em texto puro (legado) e trocar de algoritmo no futuro (v2).</li>
 *   <li><b>Índice cego (blind index):</b> HMAC-SHA256 do documento normalizado. Permite buscar CPF exato
 *       e garantir unicidade sem guardar o CPF em claro.</li>
 * </ul>
 *
 * As chaves de criptografia e de índice são derivadas de uma única chave mestra (32 bytes),
 * de forma que comprometer o índice não revela a chave de criptografia.
 *
 * Classe sem dependência do Spring para poder ser testada isoladamente.
 */
public final class FieldEncryptor {

    public static final String PREFIX = "enc:v1:";

    private static final String CIPHER = "AES/GCM/NoPadding";
    private static final String HMAC = "HmacSHA256";
    private static final int IV_LENGTH = 12;
    private static final int TAG_LENGTH_BITS = 128;
    private static final int MIN_MASTER_KEY_BYTES = 32;

    private final SecretKeySpec encryptionKey;
    private final SecretKeySpec indexKey;
    private final SecureRandom random = new SecureRandom();

    public FieldEncryptor(byte[] masterKey) {
        if (masterKey == null || masterKey.length < MIN_MASTER_KEY_BYTES) {
            throw new IllegalArgumentException(
                    "A chave de criptografia deve ter pelo menos 32 bytes (256 bits). " +
                    "Gere uma com: openssl rand -base64 32");
        }
        this.encryptionKey = new SecretKeySpec(hmac(masterKey, "sanem:field-encryption:v1"), "AES");
        this.indexKey = new SecretKeySpec(hmac(masterKey, "sanem:blind-index:v1"), HMAC);
    }

    /** Cria a partir da chave em Base64 (formato de SANEM_ENCRYPTION_KEY). */
    public static FieldEncryptor fromBase64(String base64Key) {
        if (base64Key == null || base64Key.isBlank()) {
            throw new IllegalArgumentException("Chave de criptografia não informada.");
        }
        try {
            return new FieldEncryptor(Base64.getDecoder().decode(base64Key.trim()));
        } catch (IllegalArgumentException e) {
            if (e.getMessage() != null && e.getMessage().contains("32 bytes")) throw e;
            throw new IllegalArgumentException("A chave de criptografia não é um Base64 válido.", e);
        }
    }

    public static boolean isEncrypted(String value) {
        return value != null && value.startsWith(PREFIX);
    }

    /** Criptografa o valor. Nulo continua nulo; valor já criptografado não é cifrado de novo. */
    public String encrypt(String plainText) {
        if (plainText == null || isEncrypted(plainText)) return plainText;
        try {
            byte[] iv = new byte[IV_LENGTH];
            random.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            byte[] payload = ByteBuffer.allocate(iv.length + cipherText.length).put(iv).put(cipherText).array();
            return PREFIX + Base64.getEncoder().encodeToString(payload);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao criptografar dado pessoal.", e);
        }
    }

    /**
     * Descriptografa o valor. Valores sem o prefixo são considerados legado (texto puro, gravados antes
     * do RNF12) e devolvidos como estão, até serem migrados na inicialização da aplicação.
     */
    public String decrypt(String storedValue) {
        if (storedValue == null || !isEncrypted(storedValue)) return storedValue;
        try {
            byte[] payload = Base64.getDecoder().decode(storedValue.substring(PREFIX.length()));
            if (payload.length <= IV_LENGTH) {
                throw new IllegalStateException("Dado criptografado inválido (tamanho incorreto).");
            }
            ByteBuffer buffer = ByteBuffer.wrap(payload);
            byte[] iv = new byte[IV_LENGTH];
            buffer.get(iv);
            byte[] cipherText = new byte[buffer.remaining()];
            buffer.get(cipherText);
            Cipher cipher = Cipher.getInstance(CIPHER);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, new GCMParameterSpec(TAG_LENGTH_BITS, iv));
            return new String(cipher.doFinal(cipherText), StandardCharsets.UTF_8);
        } catch (AEADBadTagException e) {
            throw new IllegalStateException(
                    "Não foi possível descriptografar: chave incorreta ou dado adulterado.", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Dado criptografado inválido (Base64 corrompido).", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao descriptografar dado pessoal.", e);
        }
    }

    /** HMAC-SHA256 em hexadecimal (64 caracteres) do valor já normalizado. Nulo/vazio → nulo. */
    public String blindIndex(String normalizedValue) {
        if (normalizedValue == null || normalizedValue.isEmpty()) return null;
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(indexKey);
            return HexFormat.of().formatHex(mac.doFinal(normalizedValue.getBytes(StandardCharsets.UTF_8)));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao calcular índice de busca.", e);
        }
    }

    /**
     * Normaliza documento (CPF, CRNM, NIF): remove pontuação/espaços e usa maiúsculas.
     * "123.456.789-09" e "12345678909" geram o mesmo índice.
     */
    public static String normalizeDocument(String value) {
        if (value == null) return null;
        String normalized = value.replaceAll("[^0-9A-Za-z]", "").toUpperCase(Locale.ROOT);
        return normalized.isEmpty() ? null : normalized;
    }

    private static byte[] hmac(byte[] key, String label) {
        try {
            Mac mac = Mac.getInstance(HMAC);
            mac.init(new SecretKeySpec(key, HMAC));
            return mac.doFinal(label.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Falha ao derivar chaves de criptografia.", e);
        }
    }
}
