package com.cloud.baowang.activity.config;

import com.cloud.baowang.common.core.exceptions.BwThreadFactory;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

@Component
@AllArgsConstructor
@EnableAsync
public class ThreadPoolConfig {
    /**
     * 红包发送线程池
     */
    public static final String REDBAG_EXECUTOR = "redbagSendExecutor";


    /**
     * 任务发送线程池
     */
    public static final String TASK_EXECUTOR = "taskSendExecutor";


    @Bean(REDBAG_EXECUTOR)
    public ThreadPoolTaskExecutor redbagSendExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(300);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("redbagsend-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new BwThreadFactory(executor));
        executor.initialize();
        return executor;
    }


    @Bean(TASK_EXECUTOR)
    public ThreadPoolTaskExecutor taskSendExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setThreadNamePrefix("tasksSend-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new BwThreadFactory(executor));
        executor.initialize();
        return executor;
    }
}
