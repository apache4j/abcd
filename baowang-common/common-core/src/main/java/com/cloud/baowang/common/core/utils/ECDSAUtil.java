package com.cloud.baowang.common.core.utils;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.constants.WalletConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.TreeMap;

/**
 * @Desciption: 椭圆曲线签名算法 *  * 速度快 强度高 签名短 *  *
 * @Author: Fordø
 * @Date: 2024/3/21 14:03
 * @Version: V1.0
 **/
public class ECDSAUtil {

    private static final Logger logger= LoggerFactory.getLogger(ECDSAUtil.class);

/*    public static void main(String[] args) throws NoSuchAlgorithmException {
       // genKey();
        String originStr = "{\"Aaa\":\"1211\",\"BBB\":\"12222\",\"CHAIN_HEAD_SIGN\":\"SSSSSSSss\",\"HEAD_RANDOM\":\"vjOhRX\",\"HEAD_TIMESTAMP\":\"1711017855123\",\"chainType\":\"tron\",\"networkType\":\"trc20\",\"ownerUserId\":\"zhangsan\"}";
        String privateKey="MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCCg/2QFGR6j1YxmEL22ogtzbB0MtiNo+zpm8tJRSf71Vg==";
        String pubKey="MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE2yb4s1L8vr0h1kZn3GWn6U8RT8u9SVYis+d7dJ/usCMMvduhHXKstTLFZmYGaTV4OOhqtV+ShaKF4uElWTsgeQ==";
        String signVal=signStr(originStr,privateKey);
        System.out.println(signVal);
        System.out.println(verifySign(originStr,signVal,pubKey));

    }*/

    public static void genKey(){
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("EC");
            keyPairGenerator.initialize(256);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();
            ECPublicKey ecPublicKey = (ECPublicKey) keyPair.getPublic();
            ECPrivateKey ecPrivateKey = (ECPrivateKey) keyPair.getPrivate();
            String pubKey = byteArrayToHex(ecPublicKey.getEncoded());
            String privateKey = byteArrayToHex(ecPrivateKey.getEncoded());
            System.out.println("PUBKEY:" + pubKey);
            System.out.println("PRIVATEKEY:" + privateKey);
        }catch (Exception e){
            logger.error("公私钥创建失败:{0}",e);
        }
    }

    /**
     * 字节数组转base64字符串
     * @param originByte 原始字节数组
     * @return base64字符串
     */
    private static String byteArrayToHex(byte[] originByte){
       return Base64.getEncoder().encodeToString(originByte);
    }

    /**
     * base64字符串转字节数组
     * @param originBase64Str base64字符串
     * @return 字节数组
     */
    private static byte[] base64StrToByte(String originBase64Str){
       return Base64.getDecoder().decode(originBase64Str);
    }
    /**
     * @param timeStamp 时间戳
     * @param random 随机数
     * @param paramJson 请求参数
     * @param privateKeyStr 私钥
     * @return 签名结果
     */
    public  static String signParam(String timeStamp, String random, JSONObject paramJson, String privateKeyStr){
        try{
            TreeMap<String,Object> originMap=new TreeMap<String,Object>();
            originMap.put(WalletConstants.HEAD_TIMESTAMP,timeStamp);
            originMap.put(WalletConstants.HEAD_RANDOM,random);
            paramJson.entrySet().forEach((o->{originMap.put(o.getKey(),o.getValue());}));
            ObjectMapper objectMapper=new ObjectMapper();
            String originStr=objectMapper.writeValueAsString(originMap);
            return signStr(originStr,privateKeyStr);
        }catch ( Exception e){
            logger.error("JSON格式签名失败:{0}",e);
        }
        return null;
    }

    public  static String signParam(String timeStamp, String random, JSONArray paramJsonArray, String privateKeyStr){
        try{
            TreeMap<String,Object> originMap=new TreeMap<String,Object>();
            originMap.put(WalletConstants.HEAD_TIMESTAMP,timeStamp);
            originMap.put(WalletConstants.HEAD_RANDOM,random);
            originMap.put(WalletConstants.HEAD_BODY,paramJsonArray.toJSONString());
            ObjectMapper objectMapper=new ObjectMapper();
            String originStr=objectMapper.writeValueAsString(originMap);
            return signStr(originStr,privateKeyStr);
        }catch ( Exception e){
            logger.error("JSONArray格式签名失败:{0}",e);
        }
        return null;
    }

    /**
     *
     * @param originStr 原始字符串
     * @param privateKeyStr 私钥
     * @return 签名结果
     */
    public  static String signStr(String originStr,String privateKeyStr){
        try {
            // 2.执行签名
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(base64StrToByte(privateKeyStr));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withECDSA");
            signature.initSign(privateKey);
            signature.update(originStr.getBytes());
            byte[] sign = signature.sign();
            return byteArrayToHex(sign);
        } catch (Exception e) {
            logger.error("String格式签名失败:{0}",e);
        }
        return null;
    }

    /**
     * 签名验证
     * @param originStr 明文字符串
     * @param signVal 签名串
     * @param publicKeyStr 公钥
     * @return 校验结果
     */
    public  static boolean verifySign(String originStr,String signVal,String publicKeyStr) {
        try{
            // 验证签名
            logger.debug("明文字符串={}签名串={}",originStr,signVal);
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(base64StrToByte(publicKeyStr));
            KeyFactory  keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec);
            Signature signature = Signature.getInstance("SHA1withECDSA");
            signature.initVerify(publicKey);
            signature.update(originStr.getBytes());
            byte[] signByte=base64StrToByte(signVal);
            return signature.verify(signByte);
        }catch (Exception e){
            logger.error("String格式签名验证失败:{0}",e);
        }
        return false;
    }


    /**
     * 签名验证
     * @param paramJsonArray 明文字符串数组形式
     * @param signVal 签名串
     * @param publicKeyStr 公钥
     * @return 校验结果
     */
    public  static boolean verifySign(String timeStamp, String random, JSONArray paramJsonArray,String signVal,String publicKeyStr) {
        try{
            TreeMap<String,Object> originMap=new TreeMap<String,Object>();
            originMap.put(WalletConstants.HEAD_TIMESTAMP,timeStamp);
            originMap.put(WalletConstants.HEAD_RANDOM,random);
            originMap.put(WalletConstants.HEAD_BODY,paramJsonArray.toJSONString());
            ObjectMapper objectMapper=new ObjectMapper();
            String originStr=objectMapper.writeValueAsString(originMap);
            logger.debug("请求参数:{},signVal:{}",originStr,signVal);
            return verifySign(originStr,signVal,publicKeyStr);
        }catch (Exception e){
            logger.error("JSONArray格式签名验证失败:{0}",e);
        }
        return false;
    }


    /**
     * 签名验证
     * @param paramJson 明文字符串数组形式
     * @param signVal 签名串
     * @param publicKeyStr 公钥
     * @return 校验结果
     */
    public  static boolean verifySign(String timeStamp, String random, JSONObject paramJson,String signVal,String publicKeyStr) {
        try{
            TreeMap<String,Object> originMap=new TreeMap<String,Object>();
            originMap.put(WalletConstants.HEAD_TIMESTAMP,timeStamp);
            originMap.put(WalletConstants.HEAD_RANDOM,random);
            paramJson.entrySet().forEach((o->{originMap.put(o.getKey(),o.getValue());}));
            ObjectMapper objectMapper=new ObjectMapper();
            String originStr=objectMapper.writeValueAsString(originMap);
            return verifySign(originStr,signVal,publicKeyStr);
        }catch (Exception e){
            logger.error("JSON格式签名验证失败:{0}",e);
        }
        return false;
    }


    /**
     *
     * @param timeStamp
     * @param random
     * @param originParam
     * @param signVal
     * @param publicKeyStr
     * @return
     */
    public  static boolean verifySign(String timeStamp, String random, String originParam,String signVal,String publicKeyStr) {
        try{
            logger.info("timeStamp:{},random:{},signVal:{},originParam:{}",timeStamp,random,signVal,originParam);
            if(JSON.isValidArray(originParam)){
                return verifySign(timeStamp,random,JSON.parseArray(originParam),signVal,publicKeyStr);
            }
            if(JSON.isValid(originParam)){
                return verifySign(timeStamp,random,JSON.parseObject(originParam),signVal,publicKeyStr);
            }
            logger.info("非json或jsonArray格式,直接返回失败");
        }catch (Exception e){
            logger.error("签名验证失败:{0}",e);
        }
        return false;
    }


}

