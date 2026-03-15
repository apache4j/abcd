package com.cloud.baowang.common.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@EnableConfigurationProperties
@Configuration
@ConfigurationProperties(prefix = "common.config")
@Data
public class CommonProperties {
    /**
     * 全局通用文件域名前缀
     */
    private String fileDomain;
    /**
     * 环境标识 blue/red
     */
    private String env;
}
