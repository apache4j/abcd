package com.cloud.baowang.play.game.fc.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class FcAESUtill {

    //AES 加密
    public static String aesEncrypt(String dataString, String appKey) throws Exception {
        Base64.Encoder encoder = Base64.getEncoder();
        SecretKeySpec keySpec = new SecretKeySpec(appKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        return encoder.encodeToString(cipher.doFinal(dataString.getBytes(StandardCharsets.UTF_8)));
    }
    //AES 解密
    public static String aesDecrypt(String dataString, String appKey) throws Exception {
        Base64.Decoder decoder = Base64.getDecoder();
        SecretKeySpec keySpec = new SecretKeySpec(appKey.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        return new String(cipher.doFinal(decoder.decode(dataString)));
    }
}
