package com.cloud.baowang.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;


/** 
 * RSA签名验签类 
 */  
public class RSASignature{  
      
    /** 
     * 签名算法 
     */  
    public static final String SIGN_ALGORITHMS = "SHA1WithRSA";
    private static final Logger log = LoggerFactory.getLogger(RSASignature.class);

    /** 
    * RSA签名 
    * @param content 待签名数据 
    * @param privateKey 商户私钥 
    * @param encode 字符集编码 
    * @return 签名值 
    */  
    public static String sign(String content, String privateKey, String encode){  
        try{  
            PKCS8EncodedKeySpec priPKCS8    = new PKCS8EncodedKeySpec( Base64.decode(privateKey) );   
            KeyFactory keyf                 = KeyFactory.getInstance("RSA");  
            PrivateKey priKey               = keyf.generatePrivate(priPKCS8);  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initSign(priKey);  
            signature.update( content.getBytes(encode));  
            byte[] signed = signature.sign();  
            return Base64.encode(signed);  
        } catch (Exception e){  
           log.error("RSA签名异常encode:{0}",e);
        }  
        return null;  
    }  
      
    public static String sign(String content, String privateKey){  
        try{  
            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec( Base64.decode(privateKey) );
            KeyFactory key = KeyFactory.getInstance("RSA");
            PrivateKey priKey = key.generatePrivate(priPKCS8);
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initSign(priKey);  
            signature.update( content.getBytes());  
            byte[] signed = signature.sign();  
            return Base64.encode(signed);  
        } catch (Exception e){
            log.error("RSA签名异常:{0}",e);
        }  
        return null;  
    }  
      
    /** 
    * RSA验签名检查 
    * @param content 待签名数据 
    * @param sign 签名值 
    * @param publicKey 分配给开发商公钥 
    * @param encode 字符集编码 
    * @return 布尔值 
    */  
    public static boolean doCheck(String content, String sign, String publicKey,String encode){  
        try{  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            byte[] encodedKey = Base64.decode(publicKey);  
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initVerify(pubKey);  
            signature.update( content.getBytes(encode) );
            return signature.verify( Base64.decode(sign) );
        } catch (Exception e){
            log.error("RSA验证签名失败encode:{0}",e);
        }  
        return false;  
    }  
      
    public static boolean doCheck(String content, String sign, String publicKey){  
        try{  
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");  
            byte[] encodedKey = Base64.decode(publicKey);  
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));  
            java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);  
            signature.initVerify(pubKey);  
            signature.update( content.getBytes() );
            return signature.verify( Base64.decode(sign) );
        } catch (Exception e){  
            log.error("RSA验证签名失败:{0}",e);
        }  
        return false;  
    }  
} 