package com.cloud.baowang.common.core.utils;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.nacos.common.utils.StringUtils;
import com.cloud.baowang.common.core.enums.DeviceType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Enumeration;

@Slf4j
public class HttpHeaderUtil {

    public static Integer getDeviceType(HttpServletRequest request) {
        if (request == null) return null;

        //1、获取所有请求头名称
        Enumeration<String> headerNames = request.getHeaderNames();
        //2、遍历
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            //根据名称获取请求头
            String value = request.getHeader(name);
            log.info(name + "-----" + value);
        }

        String userAgent = request.getHeader("User-Agent");
        log.info("获取到的User-Agent为：{}", userAgent);
        return getDeviceType(userAgent);
    }

    public static Integer getDeviceType(final String userAgent) {
        DeviceType deviceTypeEnum = DeviceType.of(userAgent);
        return deviceTypeEnum.getCode();
    }

    public static boolean isMobile(final HttpServletRequest request) {
        boolean isMobile = false;
        if (ObjectUtil.isNotEmpty(request)) {
            UserAgent userAgent = UserAgentUtil.parse(request.getHeader("User-Agent"));
            isMobile = userAgent.isMobile();
        }
        return isMobile;
    }

    public static boolean isMobile(final String userAgentString) {
        boolean isMobile = false;
        if (StringUtils.isNotBlank(userAgentString)) {
            UserAgent userAgent = UserAgentUtil.parse(userAgentString);
            isMobile = userAgent.isMobile();
        }
        return isMobile;
    }

    public static String getDomain(final HttpServletRequest request) {
        if (request == null) return null;
        return request.getHeader("Referer");
    }
}
