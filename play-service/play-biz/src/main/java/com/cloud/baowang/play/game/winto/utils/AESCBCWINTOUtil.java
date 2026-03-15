package com.cloud.baowang.play.game.winto.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

public class AESCBCWINTOUtil {

    public static String encrypt(String plaintext, String key) throws Exception {
        byte[] input = plaintext.getBytes(StandardCharsets.UTF_8);
        int blockSize = 16;


        // 手动填充 0 以满足 block size 要求
        int paddedLength = input.length;
        if (paddedLength % blockSize != 0) {
            paddedLength = ((paddedLength / blockSize) + 1) * blockSize;
        }
        byte[] paddedInput = Arrays.copyOf(input, paddedLength);


        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");


        // 随机IV
        byte[] iv = new byte[blockSize];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);


        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
        byte[] encrypted = cipher.doFinal(paddedInput);


        // 拼接IV + 加密内容
        byte[] result = new byte[iv.length + encrypted.length];
        System.arraycopy(iv, 0, result, 0, iv.length);
        System.arraycopy(encrypted, 0, result, iv.length, encrypted.length);


        return bytesToHex(result);
    }


    /**
     * AES CBC解密（解密后不去掉补0）
     */
    public static String decrypt(String hexData,String key) throws Exception {
        byte[] data = hexToBytes(hexData);
        int blockSize = 16;


        byte[] iv = Arrays.copyOfRange(data, 0, blockSize);
        byte[] encrypted = Arrays.copyOfRange(data, blockSize, data.length);


        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);


        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        byte[] decrypted = cipher.doFinal(encrypted);


        // 删除末尾的 0 填充字符
        int len = decrypted.length;
        int i = len;
        while (i > 0 && decrypted[i - 1] == 0) {
            i--;
        }
        return new String(decrypted, 0, i, StandardCharsets.UTF_8);
    }


    /**
     * byte数组转16进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }


    /**
     * 16进制字符串转byte数组
     */
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        if (len % 2 != 0) throw new IllegalArgumentException("Invalid hex string");
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return out;
    }
}
