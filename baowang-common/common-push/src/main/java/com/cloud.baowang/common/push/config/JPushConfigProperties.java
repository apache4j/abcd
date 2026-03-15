package com.cloud.baowang.common.push.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @className: TaskConfigProperties
 * @author: wade
 * @description: 极光推送配置文件
 * @date: 30/9/24 19:46
 */
//@RefreshScope
//@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "push.config")
@Data
@Primary
public class JPushConfigProperties {

    /**
     * 极光平台应用的唯一标识
     */
    private String appKey = "e42fa535ddedc06fee94980b";

    /**
     * masterSecret密钥 与 appKey 配合使用达到鉴权的目的
     */
    private String masterSecret = "dcb4c3779fad53b65ac3af57";


    /**
     * 是否生产环境，该配置只对ios通知有效（true生产环境，false开发环境
     */
    private boolean apnsProduction = false;
    /**
     * base URL
     */
    //private String host = "https://push.api.engagelab.cc";
}
