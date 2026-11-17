package com.javalovers.common.crypto;

import org.junit.jupiter.api.Test;

import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

/** RNF12 – criptografia de dados pessoais de beneficiários (LGPD art. 46). */
class FieldEncryptorTest {

    private static byte[] key(int seed) {
        byte[] k = new byte[32];
        for (int i = 0; i < k.length; i++) k[i] = (byte) (i + seed);
        return k;
    }

    private final FieldEncryptor encryptor = FieldEncryptor.fromBase64(Base64.getEncoder().encodeToString(key(0)));

    @Test
    void criptografaSemDeixarOCpfEmClaro() {
        String stored = encryptor.encrypt("87326891010");
        assertTrue(stored.startsWith(FieldEncryptor.PREFIX));
        assertFalse(stored.contains("87326891010"));
    }

    @Test
    void mesmoValorGeraCifrasDiferentes() {
        assertNotEquals(encryptor.encrypt("87326891010"), encryptor.encrypt("87326891010"));
    }

    @Test
    void descriptografaOValorOriginalComAcentos() {
        String original = "Mãe solteira, acamado, não trabalha";
        assertEquals(original, encryptor.decrypt(encryptor.encrypt(original)));
    }

    @Test
    void nuloContinuaNulo() {
        assertNull(encryptor.encrypt(null));
        assertNull(encryptor.decrypt(null));
    }

    @Test
    void naoCriptografaDuasVezes() {
        String stored = encryptor.encrypt("11911112222");
        assertEquals(stored, encryptor.encrypt(stored));
    }

    @Test
    void valorLegadoEmTextoPuroEDevolvidoComoEsta() {
        assertEquals("11911112222", encryptor.decrypt("11911112222"));
    }

    @Test
    void detectaDadoAdulterado() {
        char[] chars = encryptor.encrypt("87326891010").toCharArray();
        int i = chars.length - 3;
        chars[i] = chars[i] == 'A' ? 'B' : 'A';
        assertThrows(IllegalStateException.class, () -> encryptor.decrypt(new String(chars)));
    }

    @Test
    void chaveErradaNaoDescriptografa() {
        String stored = encryptor.encrypt("87326891010");
        FieldEncryptor other = new FieldEncryptor(key(7));
        assertThrows(IllegalStateException.class, () -> other.decrypt(stored));
    }

    @Test
    void indiceCegoEDeterministicoEDependeDaChave() {
        String index = encryptor.blindIndex("87326891010");
        assertEquals(index, encryptor.blindIndex("87326891010"));
        assertTrue(index.matches("[0-9a-f]{64}"));
        assertNotEquals(index, new FieldEncryptor(key(7)).blindIndex("87326891010"));
    }

    @Test
    void normalizaDocumentoComOuSemMascara() {
        assertEquals("87326891010", FieldEncryptor.normalizeDocument("873.268.910-10"));
        assertEquals("V123456X", FieldEncryptor.normalizeDocument("v123456-x"));
        assertNull(FieldEncryptor.normalizeDocument(" .- "));
        assertEquals(encryptor.blindIndex(FieldEncryptor.normalizeDocument("873.268.910-10")),
                encryptor.blindIndex(FieldEncryptor.normalizeDocument("87326891010")));
    }

    @Test
    void rejeitaChaveCurtaOuInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new FieldEncryptor(new byte[16]));
        assertThrows(IllegalArgumentException.class, () -> FieldEncryptor.fromBase64("nao-e-base64!!"));
        assertThrows(IllegalArgumentException.class, () -> FieldEncryptor.fromBase64(""));
    }
}
