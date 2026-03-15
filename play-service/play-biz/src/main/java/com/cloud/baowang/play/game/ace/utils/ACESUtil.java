package com.cloud.baowang.play.game.ace.utils;

import com.alibaba.fastjson2.JSON;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

public class ACESUtil {

    // DES 加密方法
    public static String encrypt(String qs, String encryptKey) throws Exception {
        if (encryptKey.length() != 8) {
            throw new IllegalArgumentException("DES key must be exactly 8 bytes");
        }
        SecretKeySpec keySpec = new SecretKeySpec(encryptKey.getBytes(StandardCharsets.UTF_8), "DES");
        IvParameterSpec ivSpec = new IvParameterSpec(encryptKey.getBytes(StandardCharsets.UTF_8));

        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        byte[] encrypted = cipher.doFinal(qs.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }

    // MD5 签名
    public static String md5(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] digest = md.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(digest);
    }

    // Byte[] 转 Hex 字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
