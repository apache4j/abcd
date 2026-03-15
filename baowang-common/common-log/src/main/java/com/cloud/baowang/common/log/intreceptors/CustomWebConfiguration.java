package com.cloud.baowang.common.log.intreceptors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@ConditionalOnClass(CustomWebConfiguration.class)
public class CustomWebConfiguration implements WebMvcConfigurer {

    private final LogInterceptor logInterceptor;

    public CustomWebConfiguration(final LogInterceptor logInterceptor) {
        this.logInterceptor = logInterceptor;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor);
    }
}
