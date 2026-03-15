package com.cloud.baowang.common.feign.config;

import com.cloud.baowang.common.feign.interceptor.FeignClientInterceptor;
import feign.RequestInterceptor;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class FeignClientInterceptorConfig {
    @Bean
    public RequestInterceptor feignInterceptor() {
        return new FeignClientInterceptor();
    }

    @Bean
    public Encoder feignFormEncoder() {
        return new SpringFormEncoder(new SpringEncoder(() -> new HttpMessageConverters(messageConverters())));
    }


    private List<HttpMessageConverter<?>> messageConverters() {
        List<HttpMessageConverter<?>> converters = new ArrayList<>();
        converters.add(new MappingJackson2HttpMessageConverter());  // 处理 JSON
        converters.add(new FormHttpMessageConverter());  // 处理表单
        converters.add(new StringHttpMessageConverter());  // 处理字符串
        return converters;
    }
}
