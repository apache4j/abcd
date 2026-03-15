package com.cloud.baowang.user.service.site;

/**
 * @className: WebsiteRedisRole
 * @author: wade
 * @description: 流量统计key
 * @date: 30/10/24 18:31
 */
public abstract class WebsiteRedisRole {
    // PV 的 redis key（String 结构）直接是数字
    public static final String PREFIX_KEY_PV = "website:pv:%s:%s";
    // UV 的 redis key（String 结构）
    public static final String PREFIX_KEY_UV = "website:uv:%s:%s";
    // IP 的 redis key（Set 结构，保存所有 IP）
    public static final String PREFIX_KEY_IPS = "website:ips:%s:%s";

    /**
     * @param siteCode 站点
     * @return
     */
    public static String getPVKey(String siteCode, String timeType) {
        return String.format(PREFIX_KEY_PV, siteCode, timeType);
    }

    public static String getUVKey(String siteCode, String timeType) {
        return String.format(PREFIX_KEY_UV, siteCode, timeType);
    }

    public static String getIPSKey(String siteCode, String timeType) {
        return String.format(PREFIX_KEY_IPS, siteCode, timeType);
    }

}
