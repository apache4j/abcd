package com.cloud.baowang.service.vendor.TopPay;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/3/5 14:20
 * @Version: V1.0
 **/
public class TopPaySignUtils {
    private static final Logger logger= LoggerFactory.getLogger(TopPaySignUtils.class);

    //签名算法名称
    private static final String RSA_KEY_ALGORITHM = "RSA";
    //标准签名算法名称
    private static final String RSA2_SIGNATURE_ALGORITHM = "SHA256withRSA";


    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSA_KEY_ALGORITHM);
        keyPairGen.initialize(2048); // 生成 2048 位密钥
        return keyPairGen.generateKeyPair();
    }


    /**
     * 签名
     *
     * @param signStr         待签名字符串
     * @param privateKeyPKCS8 私钥字符串PKCS8格式
     * @return base64字符串
     */
    public static String getSign(String signStr, String privateKeyPKCS8)  {
        try{
            privateKeyPKCS8 = privateKeyPKCS8.replace("-----BEGIN PRIVATE KEY-----", "").replace("-----END PRIVATE KEY-----", "").replace("\r", "").replace("\n", "").trim();
            privateKeyPKCS8 = privateKeyPKCS8.replace("-----BEGIN RSA PRIVATE KEY-----", "").replace("-----END RSA PRIVATE KEY-----", "").replace("\r", "").replace("\n", "").trim();
            String strSign = "";
            //初始化算法SHA256
            java.security.Signature signature = java.security.Signature.getInstance(RSA2_SIGNATURE_ALGORITHM);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyPKCS8));
            //初始化私钥+RSA
            KeyFactory keyFac = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
            PrivateKey privateKey = keyFac.generatePrivate(keySpec);
            signature.initSign(privateKey);
            //待签名字符串转byte数组使用UTF8
            byte[] msgBuf = signStr.getBytes(StandardCharsets.UTF_8);
            signature.update(msgBuf);
            byte[] byteSign = signature.sign();
            //签名值byte数组转字符串用BASE64
            strSign = Base64.encodeBase64String(byteSign);
            return strSign;
        }catch (Exception e){
            logger.error("签名异常:{0}",e);
            return null;
        }
    }

    public static String createStrParam(JSONObject json) {
        StringBuilder builder = new StringBuilder();
        List<String> listKey = new ArrayList<String>();
        listKey.addAll(json.keySet());
        Collections.sort(listKey);
        for (int i = 0; i < listKey.size(); i++) {
            char ch = ' ';
            if (i < listKey.size() - 1) {
                ch = '&';
            }
            Object object = json.get(listKey.get(i));
            if (object == null) {
                continue;
            }
            builder.append(listKey.get(i) + "=" + object + ch);
        }

        return builder.toString().trim();
    }



    /**
     * 签名
     *
     * @param originSignStr  待签名字符串
     * @param originSignVal  签名值
     * @param publicKeyPKCS8 公钥字符串PKCS8格式
     * @return base64字符串
     */
    public static boolean verifySign(String originSignStr,String originSignVal, String publicKeyPKCS8)  {
        try{
            publicKeyPKCS8 = publicKeyPKCS8.replace("-----BEGIN PUBLIC KEY-----", "").replace("-----END PUBLIC KEY-----", "").replace("\r", "").replace("\n", "").trim();
            publicKeyPKCS8 = publicKeyPKCS8.replace("-----BEGIN RSA PUBLIC KEY-----", "").replace("-----END RSA PUBLIC KEY-----", "").replace("\r", "").replace("\n", "").trim();
            //初始化算法SHA256
            java.security.Signature signature = java.security.Signature.getInstance(RSA2_SIGNATURE_ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyPKCS8));
            //初始化私钥+RSA
            KeyFactory keyFac = KeyFactory.getInstance(RSA_KEY_ALGORITHM);
            PublicKey publicKey = keyFac.generatePublic(keySpec);
            signature.initVerify(publicKey);
            //待签名字符串转byte数组使用UTF8
            byte[] msgBuf = originSignStr.getBytes(StandardCharsets.UTF_8);
            signature.update(msgBuf);
            return signature.verify(Base64.decodeBase64(originSignVal));
        }catch (Exception e){
            logger.error("签名异常:{0}",e);
            return false;
        }
    }

}
