package com.cloud.baowang.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

import java.security.MessageDigest;
import java.util.Random;

@Slf4j
public class MD5Util {

    private static final String[] HEX_DIGITS = {"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0) {
            n += 256;
        }
        int d1 = n / 16;
        int d2 = n % 16;
        return HEX_DIGITS[d1] + HEX_DIGITS[d2];
    }

    public static String byteArrayToHexString(byte[] b) {
        StringBuilder stringBuilder = new StringBuilder();
        for (byte value : b) {
            stringBuilder.append(byteToHexString(value));
        }
        return stringBuilder.toString();
    }

    public static String MD5Encode(String origin) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return byteArrayToHexString(md.digest(origin.getBytes()));
        } catch (Exception e) {
            log.error("MD5加密error", e);
            return null;
        }
    }

    /**
     * @param text 明文
     * @param key  密钥
     * @return 密文
     */
    public static String md5(String text, String key) {
        // 加密后的字符串
        return DigestUtils.md5Hex(text + key);
    }

    // 不带秘钥加密
    public static String md5(String text) {
        // 加密后的字符串
        return DigestUtils.md5Hex(text);
    }

    /**
     * 生成15位 加密盐
     */
    public static String randomGen() {
        int place = 15;
        String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    /**
     * 生成7位 随机数
     */
    public static String random7Gen() {
        int place = 7;
        String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }
    /**
     * 生成6位 随机数
     */
    public static String random6Gen() {
        int place = 6;
        String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    public static String random6GenCap() {
        int place = 6;
        String base = "QAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }
        return sb.toString();
    }

    /** 会员邀请码 */
    public static String inviteCode(String siteName) {
        int place = 4;
        String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < place; i++) {
            sb.append(base.charAt(random.nextInt(base.length())));
        }

        return siteName.substring(0, 2).toUpperCase() + sb;
    }



    public static String encryptToMD5(String input)  {
        try {
            // 获取 MD5 MessageDigest 实例
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");

            // 将输入字符串转换为字节数组并进行加密
            byte[] digest = messageDigest.digest(input.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder hexString = new StringBuilder();
            for (byte b : digest) {
                // 将每个字节转为两位的十六进制形式，并添加到结果字符串中
                hexString.append(String.format("%02x", b));
            }

            return hexString.toString();
        }catch (Exception e){
            log.error("MD5加密异常", e);
            return null;
        }
    }

}
