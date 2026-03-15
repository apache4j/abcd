package com.cloud.baowang.common.core.utils;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

/**
 * 谷歌身份认证工具
 */
public class GoogleAuthUtil {
    /**
     * 生成一个随机秘钥
     *
     * @return
     */
    public static String generateSecretKey() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();
        String keyStr = key.getKey();
        return keyStr;
    }

    /**
     * 不拋異常
     *
     * @param secret
     * @param code
     * @return
     */
    public static boolean checkCode(String secret, int code) {
        try {
            GoogleAuthenticator gAuth = new GoogleAuthenticator();
            return gAuth.authorize(secret, code);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

}
