package com.cloud.baowang.common.core.utils;

public class UserServerUtil {

    public static String getUserReviewOrderNo() {
        // 中控后台-新增会员-审核单号
        return "U" + SnowFlakeUtils.getSnowId();
    }

    public static String getEncryptPassword(String password, String salt) {
        String origin = password + salt;
        return MD5Util.MD5Encode(MD5Util.MD5Encode(origin));
    }
}
