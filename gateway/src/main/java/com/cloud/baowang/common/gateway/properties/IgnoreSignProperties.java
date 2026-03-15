package com.cloud.baowang.common.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Data
@Configuration
@RefreshScope
@ConfigurationProperties(prefix = "ignore")
public class IgnoreSignProperties {

    /**
     * 放行白名单配置，网关不校验此处的白名单
     */
    private List<String> signUrl = new ArrayList<>();
}
