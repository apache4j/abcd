package com.cloud.baowang.common.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 放行白名单配置
 */
@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "ignore.agent")
public class AgentIgnoreAuthProperties {
    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> whites = new ArrayList<>();

}
