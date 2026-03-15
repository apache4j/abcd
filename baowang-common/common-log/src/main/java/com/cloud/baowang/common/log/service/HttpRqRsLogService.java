package com.cloud.baowang.common.log.service;

import cn.hutool.core.util.ObjectUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class HttpRqRsLogService {

    public void logRequest(final HttpServletRequest request, Object body) {
        if (ObjectUtil.isNotNull(body)) {
            try {
                log.info("====body====: [{}]", new ObjectMapper().writeValueAsString(body));
            } catch (JsonProcessingException e) {
                log.info("convert request body to string fail : {}", e.getMessage(), e);
            }
        }

    }

    public void displayResp(HttpServletRequest request, HttpServletResponse response, Object body) {
        StringBuilder respMessage = new StringBuilder();
        try {
            respMessage.append("[").append(new ObjectMapper().writeValueAsString(body)).append("]");
        } catch (JsonProcessingException e) {
            log.info("convert response body to string fail : {}", e.getMessage(), e);
        }
        String reqUrl=request.getRequestURI();
        log.info("reqUrl:{}",reqUrl);
        if(reqUrl.endsWith("/actuator/prometheus")){
            //监控日志不打印
            return;
        }
        log.info("====http Response====: {}", respMessage);
    }

}
