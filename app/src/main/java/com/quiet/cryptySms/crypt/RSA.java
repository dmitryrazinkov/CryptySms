package com.quiet.cryptySms.crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

public class RSA {
    private PrivateKey privateKey;

    private PublicKey publicKey;

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public void generateKey() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(512);
        KeyPair pair = generator.genKeyPair();
        privateKey = pair.getPrivate();
        publicKey = pair.getPublic();
    }

    public BigInteger getPublicExponent() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                RSAPublicKeySpec.class);
        return pub.getPublicExponent();
    }

    public BigInteger getPrivateExponent() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec pr = fact.getKeySpec(privateKey,
                RSAPrivateKeySpec.class);
        return pr.getPrivateExponent();
    }

    public BigInteger getModulus() throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory fact = KeyFactory.getInstance("RSA");
        RSAPrivateKeySpec pr = fact.getKeySpec(privateKey,
                RSAPrivateKeySpec.class);
        return pr.getModulus();
    }

    public byte[] strToByteArray(String s) {
        return s.getBytes(Charset.forName("UTF-8"));
    }

    public String byteArrayToString(byte[] bytes) {
        return new String(bytes);
    }

    public byte[] rsaEncrypt(byte[] data) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }

    public byte[] rsaDecrypt(byte[] data) throws InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException, BadPaddingException, IllegalBlockSizeException {

        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherData = cipher.doFinal(data);
        return cipherData;
    }

    public byte[] concatArray(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] c = new byte[aLen + bLen];
        System.arraycopy(a, 0, c, 0, aLen);
        System.arraycopy(b, 0, c, aLen, bLen);
        return c;
    }
}
