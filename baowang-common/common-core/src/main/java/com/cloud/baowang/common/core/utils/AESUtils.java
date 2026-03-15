package com.cloud.baowang.common.core.utils;

import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author: fangfei
 * @createTime: 2024/11/04 11:29
 * @description:
 */
public class AESUtils {
    private static final char[] HEX_DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final char[] HEX_DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    /**
     * 对key进行md5处理
     *
     * @param key
     * @return返回处理后的key必须为16位
     */
    public static String getKey(String key) {
        try {
            key = DigestUtils.sha1Hex(key).substring(0, 16);
            System.out.println("key = " + key);
            return key;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 加密方法
     *
     * @param key 加密的key，从后台获取
     * @param jsonData 加密数据的json串
     * @return 返回加密后的字符串
     */
    public static String encrypt(String key, String jsonData) {
        try {
            key = getKey(key);
            Charset CHARSET = StandardCharsets.UTF_8;
            String TANSFORMATION = "AES/ECB/PKCS5Padding";
            byte[] byData = jsonData.getBytes(CHARSET);
            byte[] byKey = key.getBytes(CHARSET);
            if (byData.length == 0 || byKey.length == 0) return null;
            SecretKey secureKey = new SecretKeySpec(byKey, "AES");
            Cipher cipher = Cipher.getInstance(TANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secureKey);
            return bytes2HexString(cipher.doFinal(byData), true);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密方法
     *
     * @param key 加密key
     * @param data 加密过的字符串
     * @return 返回解密后的字符串
     */
    public static String decrypt(String key, String data) {
        try {
            key = getKey(key);
            Charset CHARSET = StandardCharsets.UTF_8;
            String TANSFORMATION = "AES/ECB/PKCS5Padding";
            byte[] byData = hexString2Bytes(data);
            byte[] byKey = key.getBytes(CHARSET);
            if (byData.length == 0) return null;
            SecretKey secureKey = new SecretKeySpec(byKey, "AES");
            Cipher cipher = Cipher.getInstance(TANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secureKey);
            return new String(cipher.doFinal(byData), CHARSET);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] hexString2Bytes(String hexString) {
        if (hexString == null || hexString.length() == 0) return new byte[0];
        int len = hexString.length();
        if (len % 2 != 0) {
            hexString = "0" + hexString;
            len = len + 1;
        }
        char[] hexBytes = hexString.toUpperCase().toCharArray();
        byte[] ret = new byte[len >> 1];
        for (int i = 0; i < len; i += 2) {
            ret[i >> 1] = (byte) (hex2Dec(hexBytes[i]) << 4 | hex2Dec(hexBytes[i + 1]));
        }
        return ret;
    }

    private static int hex2Dec(final char hexChar) {
        if (hexChar >= '0' && hexChar <= '9') {
            return hexChar - '0';
        } else if (hexChar >= 'A' && hexChar <= 'F') {
            return hexChar - 'A' + 10;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static String bytes2HexString(final byte[] bytes, boolean isUpperCase) {
        if (bytes == null) return "";
        char[] hexDigits = isUpperCase ? HEX_DIGITS_UPPER : HEX_DIGITS_LOWER;
        int len = bytes.length;
        if (len <= 0) return "";
        char[] ret = new char[len << 1];
        for (int i = 0, j = 0; i < len; i++) {
            ret[j++] = hexDigits[bytes[i] >> 4 & 0x0f];
            ret[j++] = hexDigits[bytes[i] & 0x0f];
        }
        return new String(ret);
    }

    public static void main(String[] args) {
        String message = "{\"vipid\":\"0tBqRuOOE9h9NF7b\",\"name\":\"fangfei01\"}";
        String key = "9298054u55vrB7rXkB93q34u87ER5mq3";
        String encryptMsg = encrypt(key, message);
        System.out.println("加密信息");
        System.out.println(encryptMsg);
        String decryptedMsg = decrypt(key, encryptMsg);
        System.out.println("解密信息");
        System.out.println(decryptedMsg);
    }
}
