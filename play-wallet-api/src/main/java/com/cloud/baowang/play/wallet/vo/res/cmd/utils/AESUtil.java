package com.cloud.baowang.play.wallet.vo.res.cmd.utils;

import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
@Slf4j
public class AESUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";
    private static final String AES = "AES";

    /**
     * AES加密
     * @param content 待加密内容
     * @param key 密钥
     * @return 加密后的Base64编码字符串
     * @throws Exception 加密异常
     */
    public static String encrypt(String content, String key) {
        try {
            String iv = new StringBuilder(key).reverse().toString();
            // 检查密钥和IV长度
            if (key == null ) {
                throw new BaowangDefaultException("CMD密钥不能为空");
            }
            // 创建密钥和IV
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            // 初始化加密器
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            // 执行加密
            byte[] encrypted = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            // 返回Base64编码的加密结果
            return Base64.getEncoder().encodeToString(encrypted);
        }
        catch (Exception e){
            log.info("CMD encrypt error:{}, content:{},key:{},",e.getMessage(),content,key);
            throw new BaowangDefaultException(e.getMessage());
        }
    }

    /**
     * AES解密
     * @param content 待解密内容(Base64编码)
     * @param key 密钥
     * @return 解密后的字符串
     * @throws Exception 解密异常
     */
    public static String decrypt(String content, String key)  {
        try {
            // 检查密钥和IV长度
            if (key == null ) {
                throw new BaowangDefaultException("CMD密钥不能为空");
            }
            String iv = new StringBuilder(key).reverse().toString();

            // 创建密钥和IV
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), AES);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));

            // 初始化解密器
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            // 执行解密
            byte[] decoded = Base64.getDecoder().decode(content);
            byte[] decrypted = cipher.doFinal(decoded);

            // 返回解密结果
            return new String(decrypted, StandardCharsets.UTF_8);
        }
        catch (Exception e){
            log.info("CMD decrypt error:{}, content:{},key:{},",e.getMessage(),content,key);
            throw new BaowangDefaultException(e.getMessage());
        }
    }

    public static void main(String[] args) throws Exception {
        // 示例用法
        String originalContent = "{\"StatusCode\":100,\"StatusMessage\":\"成功\",\"PackageId\":123,\"Balance\":100,12,\"DateReceived\":123,\"dateSent\":123}";
        String key = "1626509265594270"; // 32位密钥
        String iv = "0724955629056261"; // 16位IV
        System.out.println("原始内容: " + originalContent+ ",key:"+key + ",iv:"+key);

        // 加密
        String encrypted = encrypt(originalContent, key);
        System.out.println("加密后(Base64): " + encrypted);

        // 解密
        String decrypted = decrypt(encrypted, key);
        System.out.println("解密后: " + decrypted);
    }
}
