package com.cloud.baowang.es.sync.properties;


import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = CanalKafkaProperties.CANAL_PREFIX)
public class CanalKafkaProperties {

    public static final String CANAL_PREFIX = "canal-protocol";

    private String server;

    private Integer partition;

    private String groupId;
    // 库名
    private String destination;

    private String filter = StringUtils.EMPTY;

    private Integer batchSize = 1;

    private Long timeout = 500L;

    private TimeUnit unit = TimeUnit.MILLISECONDS;

    private Boolean isAsync = false;

}

