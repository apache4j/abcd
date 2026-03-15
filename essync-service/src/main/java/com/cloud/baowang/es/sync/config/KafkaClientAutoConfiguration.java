package com.cloud.baowang.es.sync.config;


import com.alibaba.fastjson.JSON;
import com.cloud.baowang.es.sync.client.KafkaCanalClient;
import com.cloud.baowang.es.sync.handler.MessageHandler;
import com.cloud.baowang.es.sync.properties.CanalKafkaProperties;
import com.cloud.baowang.es.sync.properties.SyncThreadPoolProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;


@Configuration
@Slf4j
@EnableConfigurationProperties({CanalKafkaProperties.class, SyncThreadPoolProperties.class})
public class KafkaClientAutoConfiguration {
    private final CanalKafkaProperties canalKafkaProperties;
    private final SyncThreadPoolProperties syncThreadPoolProperties;


    public KafkaClientAutoConfiguration(CanalKafkaProperties canalKafkaProperties, SyncThreadPoolProperties syncThreadPoolProperties) {
        this.canalKafkaProperties = canalKafkaProperties;
        this.syncThreadPoolProperties = syncThreadPoolProperties;
    }


    @Bean(initMethod = "start", destroyMethod = "stop")
    public KafkaCanalClient kafkaCanalClient(MessageHandler<?> messageHandler) {
        log.info("ES  同步配置:{}", JSON.toJSONString(canalKafkaProperties));
        return KafkaCanalClient.builder().servers(canalKafkaProperties.getServer())
                .groupId(canalKafkaProperties.getGroupId())
                .topic(canalKafkaProperties.getDestination())
                .batchSize(canalKafkaProperties.getBatchSize())
                .messageHandler(messageHandler)
                .filter(canalKafkaProperties.getFilter())
                .timeout(canalKafkaProperties.getTimeout())
                .unit(canalKafkaProperties.getUnit())
                .build();
    }

    /**
     * 同步数据线程池
     * @return
     */
    // @Bean("syncDataThreadPoolExecutor")
    public ThreadPoolTaskExecutor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(syncThreadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(syncThreadPoolProperties.getMaxPoolSize());
        executor.setQueueCapacity(syncThreadPoolProperties.getQueueCapacity());
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("canal-client-sync-ThreadPool-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}
