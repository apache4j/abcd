package com.cloud.baowang.es.sync.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = SyncThreadPoolProperties.SYNC_THREAD_PREFIX)
public class SyncThreadPoolProperties {

    public static final String SYNC_THREAD_PREFIX = "sync-thread-pool";
    private Integer corePoolSize;
    private Integer maxPoolSize;
    private Integer queueCapacity;
}
