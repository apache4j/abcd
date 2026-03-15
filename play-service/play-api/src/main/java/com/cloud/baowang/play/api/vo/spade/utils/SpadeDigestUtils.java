package com.cloud.baowang.play.api.vo.spade.utils;


import com.cloud.baowang.common.core.utils.MD5Util;

public class SpadeDigestUtils {


    public static String digest(String body, String key) {
        return MD5Util.md5(body + key);
    }
    public static boolean checkDigest(String body, String key, String digest) {
        String digestNew = digest(body, key);
        return digest.equals(digestNew);
    }
}
