package com.cloud.baowang.common.sentinel.config;


import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.RequestOriginParser;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/**
 * sentinel限流返回
 */
@Component
public class DefaultRequestOriginParser implements RequestOriginParser {

    @Override
    public String parseOrigin(HttpServletRequest request) {
        return CurrReqUtils.getReqIp();
    }
}
