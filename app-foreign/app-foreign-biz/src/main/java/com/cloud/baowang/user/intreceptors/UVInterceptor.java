package com.cloud.baowang.user.intreceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.AsyncHandlerInterceptor;

/**
 * @className: PvInterceptor
 * @author: wade
 * @description: UV统计
 * @date: 30/10/24 18:35
 */
public class UVInterceptor implements AsyncHandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        return AsyncHandlerInterceptor.super.preHandle(request, response, handler);
    }
}
