package com.cloud.baowang.common.gateway.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.reactive.ServerHttpRequest;

import java.net.InetSocketAddress;
import java.util.regex.Pattern;

/**
 * IP工具类
 * @date 20-5-15 10:03
 */
@Slf4j
public class GatewayIpUtils {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";

    private static final String HEADER_PROXY_CLIENT_IP = "Proxy-Client-IP";
    private static final String HEADER_X_FORWARDED_FOR = "X-Forwarded-For";
    private static final String HEADER_WL_PROXY_CLIENT_IP = "WL-Proxy-Client-IP";

   /* public static void main(String[] args) {
        System.err.println(isValidIp("138.113.246.123"));
        System.err.println(isValidIp("bxss.me/t/xss.html?%00"));
        System.err.println(isValidIp("2001:0db8:85a3:0000:0000:8a2e:0370:7334"));
    }*/

    /**
     * 获取真实客户端IP
     * @param serverHttpRequest
     * @return
     */
    public static String getRealIpAddress(ServerHttpRequest serverHttpRequest) {
        String ipAddress=null;
        try {
            // 1.根据常见的代理服务器转发的请求ip存放协议，从请求头获取原始请求ip。值类似于203.98.182.163, 203.98.182.163
            ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_X_FORWARDED_FOR);
            log.info("gateway请求头 HEADER_X_FORWARDED_FOR ipAddress : {}", ipAddress);
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_PROXY_CLIENT_IP);
                log.debug("gateway请求头 HEADER_PROXY_CLIENT_IP ipAddress : {}", ipAddress);
            }
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = serverHttpRequest.getHeaders().getFirst(HEADER_WL_PROXY_CLIENT_IP);
                log.debug("gateway请求头 HEADER_WL_PROXY_CLIENT_IP ipAddress : {}", ipAddress);
            }
            // 2.如果没有转发的ip，则取当前通信的请求端的ip
            if (StrUtil.isBlank(ipAddress) || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                InetSocketAddress inetSocketAddress = serverHttpRequest.getRemoteAddress();
                if(inetSocketAddress != null) {
                    log.debug("gateway请求头 isLoopBackAddress : {}", inetSocketAddress.getAddress().isLoopbackAddress());
                    if(inetSocketAddress.getAddress().isLoopbackAddress()){
                        ipAddress=LOCALHOST;
                    }else {
                        ipAddress = inetSocketAddress.getAddress().getHostAddress();
                    }
                    log.debug("gateway请求头 getHostAddress ipAddress : {}", ipAddress);
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***"
            if (ipAddress != null) {
                ipAddress = ipAddress.split(SEPARATOR)[0].trim();
            }
            if (!isValidIp(ipAddress)) {
                log.error("gateway请求头,解析请求IP解析不合法,疑似XFF攻击:{}", ipAddress);
                ipAddress=null;
            }
        } catch (Exception e) {
            log.error("gateway请求头,解析请求IP失败", e);
            ipAddress = "";
        }
        log.info("gateway请求头,解析ip结果:{}",ipAddress);
        return ipAddress == null ? "" : ipAddress;
    }

    // 简单的 IPv4 + IPv6 校验正则
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}$"  // IPv4
                    + "|"
                    + "^[0-9a-fA-F:]+$"                // IPv6
    );

    private static boolean isValidIp(String ip) {
        if (ip == null || ip.isEmpty()) return false;
        if (!IP_PATTERN.matcher(ip).matches()) return false;

        // 检查 IPv4 段合法性
        if (ip.contains(".")) {
            String[] nums = ip.split("\\.");
            for (String num : nums) {
                int n = Integer.parseInt(num);
                if (n < 0 || n > 255) return false;
            }
        }
        return true;
    }
}
