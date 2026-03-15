package com.cloud.baowang.common.core.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class DESCBCUtil {

    public static String encrypt(String data, String encryptKey) throws Exception {
        // 取前8字节作为DES密钥
        byte[] ENCRYPT_KEY = encryptKey.substring(0, 8).getBytes(StandardCharsets.US_ASCII);

        // 初始化 IV，CBC 模式需要 IV，长度必须为8字节
        IvParameterSpec iv = new IvParameterSpec(ENCRYPT_KEY); // 也可以使用固定或随机的IV

        // 生成密钥对象
        DESKeySpec desKeySpec = new DESKeySpec(ENCRYPT_KEY);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // CBC 模式 + PKCS5Padding
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // 加密
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * 解
     * @param data
     * @param encryptKey
     * @return
     * @throws Exception
     */
    public static String decrypt(String data, String encryptKey) throws Exception {

        // 取前8字节作为DES密钥
        byte[] ENCRYPT_KEY = encryptKey.substring(0, 8).getBytes(StandardCharsets.US_ASCII);

        // 初始化 IV，CBC 模式需要 IV，长度必须为8字节
        IvParameterSpec iv = new IvParameterSpec(ENCRYPT_KEY); // 与加密时的IV相同

        // 生成密钥对象
        DESKeySpec desKeySpec = new DESKeySpec(ENCRYPT_KEY);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);

        // CBC 模式 + PKCS5Padding
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        // Base64解码
        byte[] encryptedBytes = Base64.getDecoder().decode(data);
        // 解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    public static void main(String[] args) {
        try {

            String encryptKey = "g9G16nTs";



//            String encData = "username=Utest12325220&currency=USD&amount=10035.00&txnid=2360123&gametype=bac&txn_reverse_id=1958765&timestamp=2024-12-31 13:29:39.196&gameid=173562297354&hostid=901&retry=0&gamecancel=0";
//            encData = encrypt(encData,encryptKey);
//            encData = URLEncoder.encode(encData, StandardCharsets.UTF_8);
//            System.err.println(encData);

//            String decData = URLDecoder.decode(encData, StandardCharsets.UTF_8);
            String decData = decrypt("82XpXB7luiCwcJC/ZHTF502b9NNE9lrpz4U+Gs07M6uPGcNyRz4qQV9A4GiB+FNPpMj2enXFonWnK6xg0t02kgXm9598xvKyEre6mV0p7gAGwcCjyoQe0q/z31doIukL783p+Z+zylU080jcPCVOX+fH7Wu7Sh9nJazuFIu23sSobg9Vdi6fupjOe+hALCCvYKRLXlpAtNipQeDDCwul0w==",encryptKey);
            System.err.println(decData);

        }catch (Exception e){
            System.err.println(e);
        }

    }


}
