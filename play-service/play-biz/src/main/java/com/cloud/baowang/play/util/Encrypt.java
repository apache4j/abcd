package com.cloud.baowang.play.util;

import cn.hutool.core.codec.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;



public class Encrypt {
	
	/**
	 * MD5 32位加密
	 * @param sourceStr
	 * @return
	 */
	public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes("UTF-8"));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
            
        } catch (NoSuchAlgorithmException e) {
           	e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
         return result;
    }
	
	
	/**
	 * AES加密
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESEncrypt(String value,String key)  {
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			byte[] raw = key.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
			String base64 = Base64.encode(encrypted);// 此处使用BASE64做转码
			return URLEncoder.encode(base64, "UTF-8");//URL加密
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * AES加密 不进行URLEncoder
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESUNURLEncrypt(String value,String key) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] raw = key.getBytes("UTF-8");
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(value.getBytes("UTF-8"));
		return Base64.encode(encrypted);// 此处使用BASE64做转码
	}

	/**
	 * AES 解密
	 * @param sSrc
	 * @return
	 * @throws Exception
	 */
	public static String AESDecrypt(String value,String key,boolean isDecodeURL) throws Exception {
		try {
			byte[] raw = key.getBytes("UTF-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec);
			if(isDecodeURL)	value = URLDecoder.decode(value, "UTF-8");
			byte[] encrypted1 = Base64.decode(value);// 先用base64解密
			byte[] original = cipher.doFinal(encrypted1);
			String originalString = new String(original, "UTF-8");
			return originalString;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		// 获取余额
		String param = "(s=6&account=Utest_96492948"+"&currency=USD)";
		System.out.println(AESEncrypt(param,"AE4E362C2D87074B"));
		String key = MD5("(715061488803043759A37001FD4BDECD63)");
		System.out.println(AESEncrypt(param,"AE4E362C2D87074B"));
		// 请求下注
		param = "param= (s=1002&account=Utest_96492948&orderId=10000197001010000000000" +
				"11111&gameNo=50-1656326282-794666128-1&kindId=3890" +
				"&money=1&currency=USD&roomMode=1)";
		System.out.println(AESEncrypt(param,"AE4E362C2D87074B"));


		param = "param= (s=1005&account=Utest_96492948&orderId=10000197001010000000000" +
				"11111&gameNo=50-1656326282-794666128-1&kindId=3890" +
				"&money=1&currency=USD&roomMode=1)";
		System.out.println(AESEncrypt(param,"AE4E362C2D87074B"));

	}

}
