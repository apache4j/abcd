package com.cloud.baowang.common.kafka.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaProps {
    private List<Topic> topics;

    @Data
    public static class Topic{
        /**
         * topic 名称
         */
        private String topicName;
        /**
         * 分区数量
         * 分区数只能增加，不能减少
         */
        private int numPartitions;
        /**
         * 副本数
         * 副本数不能超过broker数量
         */
        private short replicationFactor;
    }



}
