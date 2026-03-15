package com.cloud.baowang.websocket.config;

import com.cloud.baowang.common.core.exceptions.BwThreadFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@AllArgsConstructor
public class ThreadPoolConfig {
    /**
     * websocket通信线程池
     */
    public static final String WS_EXECUTOR = "websocketExecutor";
    private final WsThreadPoolConfigProperties properties;


    @Bean(WS_EXECUTOR)
    public ThreadPoolTaskExecutor websocketExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getWsCorePoolSize());
        executor.setMaxPoolSize(properties.getWsMaxPoolSize());
        executor.setQueueCapacity(properties.getWsQueueCapacity());
        executor.setThreadNamePrefix("websocket-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new BwThreadFactory(executor));
        executor.initialize();
        return executor;
    }
}
