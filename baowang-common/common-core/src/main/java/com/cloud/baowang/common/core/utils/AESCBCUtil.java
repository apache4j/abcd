package com.cloud.baowang.common.core.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Slf4j
public class AESCBCUtil {

    private static final String AES_ALGORITHM = "AES";
    private static final String AES_CBC_PADDING = "AES/CBC/PKCS5Padding";
    private static final int AES_KEY_SIZE = 128;
    private static final int INIT_VECTOR_LENGTH = 16;

    // 生成密钥
    public static SecretKey generateKey() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(AES_ALGORITHM);
            SecureRandom random = new SecureRandom();
            keyGen.init(AES_KEY_SIZE, random);
            SecretKey secretKey = keyGen.generateKey();
            log.info("Generated AES Key: {}", Base64.getEncoder().encodeToString(secretKey.getEncoded()));
            return secretKey;
        } catch (Exception ex) {
            log.error("Error generating AES key: {}", ex.getMessage());
        }
        return null;
    }


    /**
     * base64 密钥
     * @return
     */
    public static String generateKeyBase64() {
        SecretKey secretKey=generateKey();
        if(secretKey!=null){
            return Base64.getEncoder().encodeToString(secretKey.getEncoded());
        }
        return null;
    }


    // 加密
    public static String encrypt(String key, String initVector, String plainText) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(initVector));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(plainText.getBytes());
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            log.error("Encryption error: {}", ex.getMessage());
        }
        return null;
    }

    // 解密
    public static String decrypt(String key, String initVector, String encryptedText) {
        try {
            IvParameterSpec iv = new IvParameterSpec(Base64.getDecoder().decode(initVector));
            SecretKeySpec skeySpec = new SecretKeySpec(Base64.getDecoder().decode(key), AES_ALGORITHM);

            Cipher cipher = Cipher.getInstance(AES_CBC_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            byte[] original = cipher.doFinal(Base64.getDecoder().decode(encryptedText));

            return new String(original, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            log.error("Decryption error: {}", ex.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        SecretKey secretKey = generateKey();
        if (secretKey == null) {
            log.error("Failed to generate AES key.");
            return;
        }
        String key = Base64.getEncoder().encodeToString(secretKey.getEncoded()); // 密钥转换为Base64字符串
        String initVector = generateInitVector(); // 生成16位初始向量

        log.info("Original Initialization Vector: {}", new String(secretKey.getEncoded(), StandardCharsets.UTF_8));
        log.info("Original AES key: {}", new String(Base64.getDecoder().decode(initVector.getBytes(StandardCharsets.UTF_8))));
        log.info("AES Key: {}", key);
        log.info("Initialization Vector: {}", initVector);

        String originalText = "Hello, AES CBC Mode!";
        log.info("Original: {}", originalText);

        String encryptedText = encrypt(key, initVector, originalText);
        log.info("Encrypted: {}", encryptedText);

        String decryptedText = decrypt("NzN5TDhJTTJUTldrbHNRMQ==", "OUViRjZ6WDFVeWp4Wm9UbA==", "jIHyzXCwbsAw9QaN+kaL3cjUsXRtNoUzkGZK3/9UzTWeoWcPOUIBTI5QmMZg0lvGKOJIWhnsoOtPg9lxniCC2BOAItq7I+tFYnOBbcXy3OJlbMk4eVwV6q9VRSkUKdoV+7iLE3sVSvhk2T0MkaDdxXk+MHzRBbNNI/ZMZvPePdZGt2+XGq/Q2jNSJjHjYQa3eWP8OzDnezlNeTtoDrtBUBvvCs782RBwXbNAKFm0TWvgEduChmwCar7EcNR1p8ZM45kU7ioxNYZeulmrYbpUSyuK9lvWW5bMm7BuR5Waalh7T0uV8SE7a1J0M938+SHDO3XfeWeQ+hAZjmaMB3WBMb2IFu734ylDPhTsxD1oQFZfm/ihX3g6VzdhFxQs85RT");

        log.info("Decrypted: {}", decryptedText);
        System.out.println("签名key明文: IievfVcSC3RJt1A9 " + "签名keybase64密文:" + Base64.getEncoder().encodeToString("IievfVcSC3RJt1A9".getBytes())); // cG8yRU1LVmg5QTFPdmVhVQ==
        System.out.println("签名iv明文: eql4mFPwZxtMdIh9 " + "签名ivbase64密文:" + Base64.getEncoder().encodeToString("eql4mFPwZxtMdIh9".getBytes())); // Vm9VRXdONTAzSUFTOWxNag==
        System.out.println("返回加密key明文: 73yL8IM2TNWklsQ1 " + "返回加密keybase64密文:" + Base64.getEncoder().encodeToString("73yL8IM2TNWklsQ1".getBytes())); // NzN5TDhJTTJUTldrbHNRMQ==
        System.out.println("返回加密iv明文: 9EbF6zX1UyjxZoTl " + "返回加密ivbase64密文:" + Base64.getEncoder().encodeToString("9EbF6zX1UyjxZoTl".getBytes())); // OUViRjZ6WDFVeWp4Wm9UbA==
        String encryptedText2 = encrypt("cG8yRU1LVmg5QTFPdmVhVQ==", "cG8yRU1LVmg5QTFPdmVhVQ==", "sadhjahsd1哈哈哈哈");
        String decryptedText2 = decrypt("cG8yRU1LVmg5QTFPdmVhVQ==", "Vm9VRXdONTAzSUFTOWxNag==", "apIIUyEtcUwazUC+4tD6bMKtGQlhtIVlxd3y02Ilqqrd5vIWms3EQIkT0x2ghKSrmqeGarTbvNhUZ0xMNe77fuPVt8m0uQuGhBpbrTFEsqNzQchxIl7JNe+dWhjxs+5BifH7JDyaWjsyxr/Q8rMv/zGHTxdH/C9g6+HSkqr4ldwmyqfTiJRfiO5PZ8DsSKTwn9GUv5/GEV9FxDcYcSDwruI0IANyU8+y5tRE6k8FO38JQs2yYUVpEknQW/pumbAjMUD4F66HFzJC7OTw7Po+bYtwqaKs7jJWquX3mm5fX9wr9KbLdv0cp610Yh2q1PrljJmREowhcKh0+ERNIJIOQUF/fdUVVsO86YjnyRUkjEU1wRCA4T5oIJIj4ll39eZV");
        System.out.println(encryptedText2);
        System.out.println(decryptedText2);
    }

    private static String generateInitVector() {
        byte[] initVector = new byte[INIT_VECTOR_LENGTH];
        SecureRandom random = new SecureRandom();
        random.nextBytes(initVector);
        return Base64.getEncoder().encodeToString(initVector);
    }
}
