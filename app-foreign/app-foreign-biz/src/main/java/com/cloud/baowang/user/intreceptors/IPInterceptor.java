package com.cloud.baowang.user.intreceptors;

import com.cloud.baowang.common.core.utils.CurrReqUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * @className: PvInterceptor
 * @author: wade
 * @description: IP统计
 * @date: 30/10/24 18:35
 */
public class IPInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        /*if (!(handler instanceof IPInterceptor)) {
            return true;
        }
        String ip = CurrReqUtils.getReqIp();
        if (StringUtils.isBlank(ip)) {
            return true;
        }
        String siteCode = CurrReqUtils.getSiteCode();
        if (StringUtils.isBlank(siteCode)) {
            return true;
        }
        String timeZone = CurrReqUtils.getTimezone();
        if (StringUtils.isBlank(timeZone)) {
            return true;
        }*/
        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }
}
