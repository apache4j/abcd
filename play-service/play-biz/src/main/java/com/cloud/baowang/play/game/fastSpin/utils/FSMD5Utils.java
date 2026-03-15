package com.cloud.baowang.play.game.fastSpin.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FSMD5Utils {

    /**
     * 计算 MD5(data + key) 并返回16进制字符串
     * @param data 原始数据（byte数组）
     * @param key  密钥（byte数组）
     * @return MD5 摘要的 32 位十六进制字符串
     */
    public static String md5WithKey(byte[] data, byte[] key) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(data);
            md.update(key);
            byte[] digest = md.digest();
            return bytesToHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5算法不存在", e);
        }
    }

    // 字节数组转16进制字符串
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b & 0xff));
        }
        return sb.toString();
    }


    // 测试
    public static void main(String[] args) {
        String data = "{\"merchantCode\":\"WINTO\",\"serialNo\":\"20250717113849965172\"}";
        String key = "WINTOF0jXctegbeajLq4W";

        String md5Result = md5WithKey(data.getBytes(), key.getBytes());
        System.out.println("MD5(data + key) = " + md5Result);
    }
}
