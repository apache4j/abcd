package com.cloud.baowang.common.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignLogConfiguration {

    @Bean
    Logger.Level feignLoggerLevel() {
        // return Logger.Level.HEADERS;
        return Logger.Level.BASIC;
    }

/*    @Bean
    feign.Logger feignLogger() {
        // return Logger.Level.HEADERS;
        return new FeignLogger();
    }*/
}
