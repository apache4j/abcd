/**
 * @(#)AesUtil.java, 10月 12, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.common.core.utils;

/**
 * <h2></h2>
 *
 * @author wayne
 * date 2023/10/12
 */

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @Author JCccc
 * @Description 测试 网站 http://tool.chacuo.net/cryptaes
 * @Date 2021/9/15 10:06
 */
public class AesCommonUtil {
    private static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";

    private static final String key = "abgfderfgt326854";

    /**
     * 加密  key 需 16位
     *
     * @param content
     * @return
     */
    public static String encrypt(String content) {
        try {
            //获得密码的字节数组
            byte[] raw = key.getBytes();
            //根据密码生成AES密钥
            SecretKeySpec skey = new SecretKeySpec(raw, "AES");
            //根据指定算法ALGORITHM自成密码器
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥 16位
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            //获取加密内容的字节数组(设置为utf-8)不然内容中如果有中文和英文混合中文就会解密为乱码
            byte[] byte_content = content.getBytes(StandardCharsets.UTF_8);
            //密码器加密数据
            byte[] encode_content = cipher.doFinal(byte_content);
            //将加密后的数据转换为Base64编码的字符串返回
            return Base64.encodeBase64String(encode_content);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密  key 需 16位
     *
     * @param encryptStr
     * @param key
     * @return
     */
    public static String decrypt(String encryptStr) {
        try {
            //获得密码的字节数组
            byte[] raw = key.getBytes();
            //根据密码生成AES密钥
            SecretKeySpec skey = new SecretKeySpec(raw, "AES");
            //根据指定算法ALGORITHM自成密码器
            Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
            //初始化密码器，第一个参数为加密(ENCRYPT_MODE)或者解密(DECRYPT_MODE)操作，第二个参数为生成的AES密钥
            cipher.init(Cipher.DECRYPT_MODE, skey);
            //把密文字符串Base64转回密文字节数组
            byte[] encode_content = Base64.decodeBase64(encryptStr);
            //密码器解密数据
            byte[] byte_content = cipher.doFinal(encode_content);
            //将解密后的数据转换为字符串返回
            return new String(byte_content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        String originalString = "Hello, World!";

        // 加密
        String encryptedString = Base64.encodeBase64String(originalString.getBytes());
        System.out.println("加密后的字符串：" + encryptedString);

        // 解密
        byte[] decryptedBytes = Base64.decodeBase64(encryptedString);
        String decryptedString = new String(decryptedBytes);
        System.out.println("解密后的字符串：" + decryptedString);
    }


}
