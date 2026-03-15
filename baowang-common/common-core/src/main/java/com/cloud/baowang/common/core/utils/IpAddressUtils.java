package com.cloud.baowang.common.core.utils;

import io.ipinfo.api.IPinfo;
import io.ipinfo.api.errors.RateLimitedException;
import io.ipinfo.api.model.IPResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IpAddressUtils {
    private static volatile IPinfo ipInfoInstance;

    private IpAddressUtils() {
        // 私有构造函数，防止外部实例化
    }

    public static IPinfo getIpInfoInstance() {
        if (ipInfoInstance == null) {
            synchronized (IpAddressUtils.class) {
                if (ipInfoInstance == null) {
//                    ipInfoInstance = new IPinfo.Builder().build();
                    ipInfoInstance = new IPinfo.Builder().setToken("a500d282ebb1bc").build();
                }
            }
        }
        return ipInfoInstance;
    }

    /**
     * IP 默认缓存一天
     * @param ip
     * @return
     */
    public static IPResponse queryIpRegion(String ip){
        try {
            return getIpInfoInstance().lookupIP(ip);
        } catch (Exception e) {
            log.error("IP info Exception", e);
        }
        return null;
    }

    public static void main(String[] args) throws RateLimitedException {
        System.out.println(queryIpRegion("1.0.3.255"));
    }
}
