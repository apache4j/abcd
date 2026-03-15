package com.cloud.baowang.activity.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

/**
 * @className: TaskConfigProperties
 * @author: wade
 * @description: TODO
 * @date: 30/9/24 19:46
 */
@RefreshScope
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "task.config.expire")
@Data
public class TaskConfigProperties {

    /**
     * 任务过期时间
     */
    private Integer time = 24;

    /**
     * 任务领取过期时间
     */
    private Integer receiveTime = 24;
}
