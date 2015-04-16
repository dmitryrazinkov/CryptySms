package com.quiet.cryptySms.crypt;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;

public class AES {
    private SecretKey encryptionKey;

    public void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        this.encryptionKey = keyGen.generateKey();
    }


    public String encrypt(String plainText) throws Exception {
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE);
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

        return android.util.Base64.encodeToString(encryptedBytes, android.util.Base64.DEFAULT);
    }

    public String decrypt(String encrypted) throws Exception {
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE);
        byte[] plainBytes = cipher.doFinal(android.util.Base64.decode(encrypted, android.util.Base64.DEFAULT));

        return new String(plainBytes);
    }

    private Cipher getCipher(int cipherMode)
            throws Exception {
        String encryptionAlgorithm = "AES";
        SecretKeySpec keySpecification = new SecretKeySpec(
                encryptionKey.getEncoded(), encryptionAlgorithm);
        Cipher cipher = Cipher.getInstance(encryptionAlgorithm);
        cipher.init(cipherMode, keySpecification);

        return cipher;
    }

    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }

    public void setEncryptionKey(SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }
}