package com.cloud.baowang.play.game.pp.utils;

import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

public class HashValidatorUtils {

    /**
     * 计算参数的哈希值（MD5）
     * @param params POST 参数（包含待验证的 key=value 对）
     * @param secret 密钥
     * @return 生成的哈希值（小写）
     */
    public static String calculateHash(Map<String, Object> params, String secret) {
        // 排除 "hash" 参数
        Map<String, Object> filteredParams = new TreeMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            if (!"hash".equalsIgnoreCase(entry.getKey())) {
                filteredParams.put(entry.getKey(), entry.getValue());
            }
        }

        // 构建字符串 key1=value1&key2=value2
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : filteredParams.entrySet()) {
            if (!sb.isEmpty()) {
                sb.append("&");
            }
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        // 追加 SECRET
        sb.append(secret);

        return md5(sb.toString());
    }

    /**
     * 验证 hash 是否有效
     * @param params 所有参数（包含 hash 字段）
     * @param secret 密钥
     * @return 验证是否通过
     */
    public static boolean isValidHash(Map<String, Object> params, String secret) {
        String expectedHash = params.get("hash").toString();
        if (expectedHash == null) {
            return false;
        }

        String calculatedHash = calculateHash(params, secret);
        return expectedHash.equalsIgnoreCase(calculatedHash);
    }

    /**
     * 获取 MD5 哈希
     */
    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes("UTF-8"));

            // 转为十六进制
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString().toLowerCase();
        } catch (Exception e) {
            throw new RuntimeException("MD5 计算失败", e);
        }
    }
}
