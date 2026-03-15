package com.cloud.baowang.site.config;

import com.cloud.baowang.site.interceptor.MyHeaderInterceptor;
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
    public static final String[] excludeUrls = {"/site_admin_login/api/login", "/v3/api-docs", "/refresh",
            "/site_admin_login/api/checkAccount",
            "/site_admin_login/api/getGoogleCode",
            "/site_admin_login/api/updatePassword",
            "/site_admin_login/api/adminLogout",
            "/common/siteInfo",
            "/site_admin_login/api/submitGoogleCode"};

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
