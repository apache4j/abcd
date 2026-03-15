package com.cloud.baowang.common.core.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
public class Encrypt {
	private Encrypt() {
	}

	/**
	 * MD5 32位加密
	 *
	 * @param sourceStr
	 * @return
	 */
	public static String md5(String sourceStr) {
		String result = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(sourceStr.getBytes(StandardCharsets.UTF_8));
			byte[] b = md.digest();
			int i;
			StringBuilder buf = new StringBuilder();
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
			log.error("MD5 error.", e);
		}
		return result;
	}

	/**
	 * AES加密 不进行URLEncoder
	 *
	 * @param value
	 * @param key
	 * @return
	 * @
	 */
	@SneakyThrows
	public static String aesUNURLEncrypt(String value, String key) {
		Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
		byte[] raw = key.getBytes(StandardCharsets.UTF_8);
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
		byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
		return Base64.getEncoder().encodeToString(encrypted);// 此处使用BASE64做转码
	}

	@SneakyThrows
	public static byte[] desEncrypt(byte[] plainText,String key){
		SecureRandom sr = new SecureRandom();
		DESKeySpec dks = new DESKeySpec(key.getBytes());
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey desKey = keyFactory.generateSecret(dks);
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, desKey, sr);
		byte[] data = plainText;
		return cipher.doFinal(data);
	}

	@SneakyThrows
	public static String agDesEncrypt(String input, String key)  {
		return Base64.getEncoder().encodeToString(desEncrypt(input.getBytes(),key)).replaceAll("\\s*", "");
	}

	/**
	 * PKCS5PADDING-128位AES加密，
	 *
	 * @param aesKey     aesKey
	 * @param aesIv      aesIv
	 * @param rowContent 待加密内容
	 * @return
	 */
	public static String aesEncrypt(String aesKey, String aesIv, String rowContent) {
		try {
			IvParameterSpec iv = generalAesIVSpec(aesIv);

			SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
			byte[] encrypted = cipher.doFinal(rowContent.getBytes(StandardCharsets.UTF_8));

			return Base64.getEncoder().encodeToString(encrypted);
		} catch (Exception ex) {
			log.error("encrypt error.aesKey:" + aesKey + ",aesIv:" + aesIv, ex);
		}
		return null;
	}

	/**
	 * @param aesIv aesIv
	 * @return
	 */
	private static IvParameterSpec generalAesIVSpec(String aesIv){
		SecureRandom random = new SecureRandom();
		byte[] bytesIV = new byte[16];
		random.nextBytes(bytesIV);
		bytesIV = aesIv.getBytes(StandardCharsets.UTF_8);
		return new IvParameterSpec(bytesIV);
	}

	/**
	 * PKCS5PADDING-128位AES解密，
	 *
	 * @param aesKey    aesKey
	 * @param aesIv     aesIv
	 * @param encrypted
	 * @return
	 */
	public static String aesDecrypt(String aesKey, String aesIv, String encrypted) {
		try {
			IvParameterSpec iv = generalAesIVSpec(aesIv);

			SecretKeySpec skeySpec = new SecretKeySpec(aesKey.getBytes(StandardCharsets.UTF_8), "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

			return new String(original, StandardCharsets.UTF_8);
		} catch (Exception ex) {
			log.warn("decrypt error.aesKey:" + aesKey + ",aesIv:" + aesIv, ex);
		}
		return null;
	}


	//私钥匙加密
	public static String sign(byte[] data, String privateKey) throws NoSuchAlgorithmException,
			InvalidKeySpecException, InvalidKeyException, SignatureException {
		byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initSign(priKey);
		signature.update(data);
		return new String(Base64.getEncoder().encode(signature.sign()));
	}

	public static String sha1sign(byte[] data, String privateKey) throws NoSuchAlgorithmException,
			InvalidKeySpecException, InvalidKeyException, SignatureException {
		byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey priKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initSign(priKey);
		signature.update(data);
		return new String(Base64.getEncoder().encode(signature.sign()));
	}

	//公钥匙验证
	@SneakyThrows
	public static boolean verify(byte[] data, String publicKey, String sign) {
		byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
		X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey2 = keyFactory.generatePublic(x509EncodedKeySpec);
		Signature signature = Signature.getInstance("MD5withRSA");
		signature.initVerify(publicKey2);
		signature.update(data);
		return signature.verify(Base64.getDecoder().decode(sign));
	}

	public static String rsaEncrypt(String data, String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
		byte[] keyBytes = Base64.getDecoder().decode(publicKey.getBytes());
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key key = keyFactory.generatePublic(keySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key);
		byte[] encrypted = cipher.doFinal(data.getBytes());
		return new String(Base64.getEncoder().encode(encrypted), StandardCharsets.UTF_8);
	}

	public static String rsaDecrypt(String cryptograph, String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException, InvalidKeyException {
		byte[] keyBytes = Base64.getDecoder().decode(privateKey.getBytes());
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		Key key = keyFactory.generatePrivate(keySpec);

		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, key);
		byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(cryptograph.getBytes()));
		return new String(decrypted);
	}

	/**
	 * 字符串SHA加密
	 *
	 * @param strText
	 * @param strType
	 * @return
	 */
	public static String sha(String strText, String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				byte[] byteBuffer = messageDigest.digest();

				// 將 byte 轉換爲 string
				StringBuilder strHexString = new StringBuilder();
				// 遍歷 byte buffer
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				log.error("sha Encryption Error", e);
			}
		}

		return strResult;
	}

	/**
	 * 利用java原生的类实现SHA256加密
	 *
	 * @param str 加密后的报文
	 * @return String
	 */
	public static String getSHA256(String str) {
		MessageDigest messageDigest;
		String encodestr = "";
		try {
			messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
			encodestr = byte2Hex(messageDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			log.error("getSHA256 error.",e);
		}
		return encodestr;
	}

	/**
	 * 将byte转为16进制
	 *
	 * @param bytes
	 * @return
	 */
	private static String byte2Hex(byte[] bytes) {
		StringBuilder stringBuffer = new StringBuilder();
		String temp = null;
		for (int i = 0; i < bytes.length; i++) {
			temp = Integer.toHexString(bytes[i] & 0xFF);
			if (temp.length() == 1) {
				//1得到一位的进行补0操作
				stringBuffer.append("0");
			}
			stringBuffer.append(temp);
		}
		return stringBuffer.toString();
	}

	public static String sha256Hmac(String message, String secret){
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			return byteArrayToHexString(sha256_HMAC.doFinal(message.getBytes()));
		}catch (Exception e){
			log.error("sha256Hmac error.",e);
		}
		return null;
	}
	private static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}




}
