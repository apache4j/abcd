package com.cloud.baowang.common.log.intreceptors;


import com.cloud.baowang.common.log.service.HttpRqRsLogService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.lang.reflect.Type;

@ControllerAdvice
@ConditionalOnClass(RequestBodyAdviceInterceptor.class)
public class RequestBodyAdviceInterceptor extends RequestBodyAdviceAdapter {

    private final HttpRqRsLogService httpRqRsLogService;
    private final HttpServletRequest request;

    public RequestBodyAdviceInterceptor(final HttpRqRsLogService httpRqRsLogService, final HttpServletRequest request) {
        this.httpRqRsLogService = httpRqRsLogService;
        this.request = request;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        httpRqRsLogService.logRequest(request, body);
        return super.afterBodyRead(body, inputMessage, parameter, targetType, converterType);
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
