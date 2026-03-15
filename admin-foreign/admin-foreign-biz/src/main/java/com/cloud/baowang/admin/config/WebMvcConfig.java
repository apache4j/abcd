package com.cloud.baowang.admin.config;

import com.cloud.baowang.admin.interceptor.MyHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截器配置
 *
 * @author qiqi
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 不需要拦截地址
     */
    public static final String[] excludeUrls = {"/business_admin_login/api/login", "/business_admin_login/api/adminLogout", "/v3/api-docs", "/refresh"};

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getHeaderInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(excludeUrls)
                .order(-10);
    }

    /**
     * 自定义请求头拦截器
     */
    public MyHeaderInterceptor getHeaderInterceptor() {
        return new MyHeaderInterceptor();
    }
}
