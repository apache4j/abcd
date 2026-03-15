package com.cloud.baowang.user.config;

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
     * 公共/消息创建线程池
     */
    public static final String USER_NOTICE_EXECUTOR = "userNoticeExecutor";



    @Bean(USER_NOTICE_EXECUTOR)
    public ThreadPoolTaskExecutor userNoticeExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(16);
        executor.setQueueCapacity(500);
        executor.setKeepAliveSeconds(300);
        executor.setAllowCoreThreadTimeOut(true);
        executor.setThreadNamePrefix("user-notice-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setThreadFactory(new BwThreadFactory(executor));
        executor.initialize();
        return executor;
    }



}
