package com.cloud.baowang.play.wallet.aspect;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LogAspect {

    @Pointcut("@within(com.cloud.baowang.play.wallet.annotations.LogExecution)")
    public void webLog() {}

    @Before("webLog()")
    public void doBefore(JoinPoint joinPoint) {
        // 开始打印请求日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        try{
            HttpServletRequest request = attributes.getRequest();
            // 打印请求 url
            log.info("LogAspect-URL={},Args={}", request.getRequestURI());
        }catch (Exception e){
            log.error("LogAspect-error", e);
        }
    }

    @Around("webLog()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        // 打印出参
        log.info("LogAspect-Time-Consuming={}ms,Response={}", System.currentTimeMillis() - startTime, new Gson().toJson(result));
        return result;
    }
}