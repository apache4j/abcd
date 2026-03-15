package com.cloud.baowang.common.core.utils;


import com.alibaba.nacos.api.utils.StringUtils;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class IPUtil {

    public static String getIp(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String ip = request.getHeader("X-Forwarded-For");
        log.info("请求头header : {}", request);
        log.info("请求头ip : {}", request.getHeader("X-Forwarded-For"));
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("http_client_ip");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
//        log.info("ip截取前={}",ip);
        // 如果是多级代理，那么取第一个ip为客户ip
        if (ip != null && ip.indexOf(",") != -1) {
            ip = ip.substring(0, ip.indexOf(",")).trim();
        }
//        log.info("ip截取后={}",ip);
        return ip;
    }

    /**
     * 把int->ip地址
     *
     * @param ipInt
     * @return String
     */
    public static String intToIp(int ipInt) {
        return String.valueOf((ipInt >> 24) & 0xff) + '.' +
                ((ipInt >> 16) & 0xff) + '.' +
                ((ipInt >> 8) & 0xff) + '.' + (ipInt & 0xff);
    }

    public static long ip2long(String ip) {
        if (StringUtils.isBlank(ip)) {
            return 0L;
        }
        String[] fields = ip.split("\\.");
        if (fields.length != 4) {
            return 0L;
        }

        long r = Long.parseLong(fields[0]) << 24; // 获取高8位
        r |= Long.parseLong(fields[1]) << 16;   // 获取中8位
        r |= Long.parseLong(fields[2]) << 8;    // 获取中8位
        r |= Long.parseLong(fields[3]);         // 获取低8位

        return r;
    }

    public static void main(String[] args) {
        System.err.println(validateIPv6("2a09:bac5:d46a:137d::1f1:1bb"));
    }


    public static boolean validateIPv6(final String ip) {
        String regex =
                "^(?:[0-9A-Fa-f]{1,4}:){7}[0-9A-Fa-f]{1,4}$"               // 完整 8 组
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,7}:$"                          // :: 在末尾
                        + "|^:(?::[0-9A-Fa-f]{1,4}){1,7}$"                          // :: 在开头
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,6}:[0-9A-Fa-f]{1,4}$"          // :: 在中间，替代 1 组
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,5}(?::[0-9A-Fa-f]{1,4}){1,2}$" // 替代 2 组
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,4}(?::[0-9A-Fa-f]{1,4}){1,3}$" // 替代 3 组
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,3}(?::[0-9A-Fa-f]{1,4}){1,4}$" // 替代 4 组
                        + "|^(?:[0-9A-Fa-f]{1,4}:){1,2}(?::[0-9A-Fa-f]{1,4}){1,5}$" // 替代 5 组
                        + "|^[0-9A-Fa-f]{1,4}:(?:(?::[0-9A-Fa-f]{1,4}){1,6})$"      // 替代 6 组
                        + "|^:(?:(?::[0-9A-Fa-f]{1,4}){1,7}|:)$";                   // :: 替代全部
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }



    // 校验

    private static final String IPV4_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";


    private static final Pattern IPV4_REGEX = Pattern.compile(IPV4_PATTERN);

    public static boolean validateIPv4(final String ip) {
        Matcher matcher = IPV4_REGEX.matcher(ip);
        return matcher.matches();
    }


    public static boolean validateIP(final String ip){
        return validateIPv4(ip) || validateIPv6(ip);
    }


    // 单个 IP 地址的正则表达式
    private static final String IP_REGEX =
            "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    public static void validateIPs(String input) {
        // 用逗号分隔 IP 地址
        String[] ipAddresses = input.split(CommonConstant.COMMA);

        // 校验每个 IP 地址
        for (String ip : ipAddresses) {
            if (!Pattern.matches(IP_REGEX, ip.trim())) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
        }
    }
}
