package com.cloud.baowang.user.config;

import com.cloud.baowang.user.intreceptors.LoginInfoInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 */
@Configuration
@EnableAsync
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 需要拦截地址
     */
    public static final String[] includeUrl = {"/user-info/api/getUserBalance", "/user-info/api/getIndexInfo"};

    /**
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/login/api/**", "/v3/api-docs", "/refresh"};


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
    }

    /**
     * 自定义请求头拦截器
     */
    public LoginInfoInterceptor getLoginInterceptor() {
        return new LoginInfoInterceptor();
    }
}
