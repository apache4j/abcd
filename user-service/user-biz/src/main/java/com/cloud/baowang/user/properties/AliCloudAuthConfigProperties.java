package com.cloud.baowang.user.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @className: TaskConfigProperties
 * @author: wade
 * @description: 阿里云服务配置文件
 * @date: 30/9/24 19:46
 */
@Configuration
@ConfigurationProperties(prefix = "aliyun.auth.config")
@Data
@Primary
public class AliCloudAuthConfigProperties {

    /**
     * 阿里云平台应用的唯一标识
     */
    private String accessKeyId = "LTAI5tPizTC6qHFUoX37aKDB";

    /**
     * masterSecret密钥 与 appKey 配合使用达到鉴权的目的
     */
    private String accessKeySecret = "LTAI5tPizTC6qHFUoX37aKDB";


    /**
     * 是否生产环境，该配置只对ios通知有效（true生产环境，false开发环境)
     */
    private boolean apnsProduction = false;

}
