package com.cloud.baowang.websocket.config;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "netty.thread.pool")
public class WsThreadPoolConfigProperties {
    @Schema(title = "netty-boss线程组大小")
    private Integer bossGroupSize;
    @Schema(title = "ws信息发送线程池核心线程数")
    private Integer wsCorePoolSize;
    @Schema(title = "ws信息发送线程池最大线程数")
    private Integer wsMaxPoolSize;
    @Schema(title = "ws信息发送线程池等待队列大小")
    private Integer wsQueueCapacity;
}
