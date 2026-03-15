package com.cloud.baowang.common.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.SecureRandom;

@Slf4j

public class DESUtil {
    // 字符编码
    public static final String CHARSET_UTF8 = "UTF-8";
    // 加密算法DES
    public static final String DES = "DES";
    // 电子密码本模式
    public static final String DES_ECB = "DES/ECB/PKCS5Padding";
    // 加密块链模式--推荐
    public static final String DES_CBC = "DES/CBC/PKCS5Padding";
    // 偏移变量，固定占8位字节
    private static final String IV_PARAMETER = "12345678";

    /**
     * 生成key
     *
     * @param password
     * @return
     * @throws Exception
     */
    private static Key generateKey(String password) throws Exception {
        DESKeySpec dks = new DESKeySpec(password.getBytes(StandardCharsets.UTF_8));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
        return keyFactory.generateSecret(dks);
    }

    /**
     * DES加密字符串--ECB格式
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data     待加密字符串
     * @return 加密后内容
     */
    public static String encryptECB(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(DES_ECB);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new SecureRandom());
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            return new String(Base64.encodeBase64(bytes));

        } catch (Exception e) {
            log.error("DES ECB模式加密异常,error:", e);
            return data;
        }
    }

    /**
     * DES解密字符串--ECB格式
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data     待解密字符串
     * @return 解密后内容
     */
    public static String decryptECB(String password, String data) throws Exception {
        Key secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance(DES_ECB);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new SecureRandom());
        return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }

    /**
     * DES加密字符串-CBC加密格式
     *
     * @param password 加密密码，长度不能够小于8位
     * @param data     待加密字符串
     * @return 加密后内容
     */
    public static String encryptCBC(String password, String data) {
        if (password == null || password.length() < 8) {
            throw new RuntimeException("加密失败，key不能小于8位");
        }
        if (data == null) {
            return null;
        }
        try {
            Key secretKey = generateKey(password);
            Cipher cipher = Cipher.getInstance(DES_CBC);
            IvParameterSpec spec = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
            byte[] bytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            // JDK1.8及以上可直接使用Base64，JDK1.7及以下可以使用BASE64Encoder
            return new String(Base64.encodeBase64(bytes));

        } catch (Exception e) {
            log.error("DES CBC模式加密异常,error:", e);
            return data;
        }
    }

    public static void main(String[] args) {
        System.out.println(DESUtil.encryptCBC("12341234", "cagent=81288128/\\\\\\\\/method=tc"));
        System.out.println(DESUtil.encryptECB("12341234", "cagent=81288128/\\\\\\\\/method=tc"));
    }

    /**
     * DES解密字符串--CBC格式
     *
     * @param password 解密密码，长度不能够小于8位
     * @param data     待解密字符串
     * @return 解密后内容
     */
    public static String decryptCBC(String password, String data) throws Exception {
        Key secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance(DES_CBC);
        IvParameterSpec spec = new IvParameterSpec(IV_PARAMETER.getBytes(StandardCharsets.UTF_8));
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        return new String(cipher.doFinal(Base64.decodeBase64(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
    }
}

