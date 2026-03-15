package com.cloud.baowang.common.log.intreceptors;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.log.service.HttpRqRsLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ConditionalOnClass(LogInterceptor.class)
public class LogInterceptor implements HandlerInterceptor {

    private final HttpRqRsLogService httpRqRsLogService;

    public LogInterceptor(final HttpRqRsLogService httpRqRsLogService) {
        this.httpRqRsLogService = httpRqRsLogService;
    }

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler) throws Exception {
        MDC.put(CommonConstant.TRACE_ID,request.getHeader(CommonConstant.TRACE_ID));
        // basic header init
        CurrReqUtils.init();
        final Map<String, String> parameters = this.getParameters(request);
        Map<String, String> headers = getHeaders(request);
        log.info("====RequestUrl====: [{}] [{}]", request.getMethod(), request.getRequestURI());
        log.info("====RequestHeaders====: [{}]", headers);
        if (!parameters.isEmpty()) {
            log.info("====parameters====: [{}]", parameters);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        int bufferSize = response.getBufferSize();
        if (bufferSize <= 0) {
            log.info("====http Response====: []");
        }
        MDC.clear();
    }

    private Map<String, String> getParameters(HttpServletRequest request) {
        Map<String, String> parameters = new HashMap<>();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String paramName = params.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
        return parameters;
    }

    private Map<String, String> getHeaders(HttpServletResponse response) {
        Map<String, String> headers = new HashMap<>();
        Collection<String> headerMap = response.getHeaderNames();
        for (String str : headerMap) {
            headers.put(str, response.getHeader(str));
        }
        return headers;
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements()) {
            String name = enumeration.nextElement();
            String value = request.getHeader(name);
            headerMap.put(name, value);
        }
        return headerMap;
    }
}
