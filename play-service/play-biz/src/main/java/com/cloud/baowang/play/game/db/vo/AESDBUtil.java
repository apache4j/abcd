package com.cloud.baowang.play.game.db.vo;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class AESDBUtil {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";

    public static String encrypt(String data, String key, String iv)  {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            return null;
        }
    }

    public static String decrypt(String base64Data, String key, String iv)  {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(base64Data);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        String key = "0YgqK1dymKV8y13p"; // 16字节
        String iv = "0102030405060708"; // 16字节
        String data = "vl04wQXnFkXGwMOVEC+RQt+2pgQii5inlZcrowVNR3Clk5Gkq38JATms+YrkwtUepip+dpO1QZQ+2DemlmplOkbmvRFngL/OR0kMjp0JD/0=";

//        String encrypted = encrypt(data, key, iv);
//        System.out.println("加密后: " + encrypted);


        String decrypted = decrypt(data, key, iv);
        System.out.println("解密后: " + decrypted);
    }
}

