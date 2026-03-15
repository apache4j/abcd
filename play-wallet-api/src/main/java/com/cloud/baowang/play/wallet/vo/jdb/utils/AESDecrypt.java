package com.cloud.baowang.play.wallet.vo.jdb.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
public class AESDecrypt {
    public static String decrypt(String data, String key, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key.getBytes(), "AES"),
                new IvParameterSpec(iv.getBytes()));
        String decryptData = new String(cipher.doFinal(Base64.decodeBase64(data)));
        return decryptData;
    }

    public static void main(String[] args) throws Exception {
//        String encryptData = "xruORLt4bcPcCQ-Y1hq_SB9FL0gq_cBhZeK5a829jhqi5v8jW_MEZ7JDpaLD5vsNguBBlGIKJ2SL_blzd7euC8DvlGtpJ5qcEFv8aWOtPsuJ6tjPaIXtV8eWs0pjdk5N";
//        String key = "9397edcba20ef10b"; // $ { KEY }
        String message = "gJa7yZGyrN8S8XTI4AiCfdWA8rMulyTou1LIwU_TLp8ki9Lylov6wm0YPmGPdsYUKRcpX8NNKKEl1skfcqNn16-yDfrypGkwORDT5tyBZoE6Gb7mPhXwJ-RtpkRBy_DKqNJBxH0ZtK2hWWEQ2Yl7515XPxaHwr5rQrM19YAn63lsTiAF6TL1a6VCHZeJyvBQ9tqlO1WNBwO_YNEV5WJjY2TanKmITIdB2N-r5DHDPCg";
        String key = "MF72v8599RpN1sPl";
        String iv = "0102030405060708"; // $ { IV }
        String decrypt = decrypt(message, key, iv);
        JSONObject jsonObject = JSON.parseObject(decrypt);
//        jsonObject.getInstant("action");
        System.out.println(jsonObject); // return “SampleData”
    }


}