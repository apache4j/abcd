package com.cloud.baowang.common.log.intreceptors;


import com.cloud.baowang.common.log.service.HttpRqRsLogService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


@ControllerAdvice
@ConditionalOnClass(ResponseBodyAdviceInterceptor.class)
public class ResponseBodyAdviceInterceptor implements ResponseBodyAdvice<Object> {

    private final HttpRqRsLogService rqRsLogService;

    public ResponseBodyAdviceInterceptor(final HttpRqRsLogService rqRsLogService) {
        this.rqRsLogService = rqRsLogService;
    }


    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        rqRsLogService.displayResp(((ServletServerHttpRequest) request).getServletRequest(), ((ServletServerHttpResponse) response).getServletResponse(), body);
        return body;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }
}
