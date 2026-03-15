package com.cloud.baowang.common.kafka.interceptor;

import com.cloud.baowang.common.core.annotations.KafkaPartitionClass;
import com.cloud.baowang.common.core.annotations.KafkaPartitionField;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.utils.Utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class CustomPartitioner implements Partitioner {
    private final ConcurrentMap<String, AtomicInteger> topicCounterMap = new ConcurrentHashMap<>();

    @Override
    public int partition(String topic, Object key, byte[] bytes, Object value, byte[] bytes1, Cluster cluster) {
        try {
            // 获取被自定义注解标记的字段的值
            Object partitionKey = null;
            Class<?> valClass = value.getClass();
            if (valClass.isAnnotationPresent(KafkaPartitionClass.class)) {
                Field[] fields = valClass.getDeclaredFields();
                for (Field field : fields) {
                    if (field.isAnnotationPresent(KafkaPartitionField.class)) {
                        field.setAccessible(true);
                        partitionKey = field.get(value);
                        break;
                    }
                }

                if (partitionKey != null) {
                    // 使用哈希值对分区数取模来确定分区
                    return Math.abs(partitionKey.hashCode()) % cluster.partitionCountForTopic(topic);
                }
            }
        } catch (IllegalAccessException e) {
            log.error("kafka消息分区失败，分区属性：{}", value, e);
        }

        // 如果没有找到自定义注解的值,则根据topic轮询
        return nextPartition(topic, cluster);
    }

    public int nextPartition(String topic, Cluster cluster) {
        List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
        int numPartitions = partitions.size();
        int nextValue = nextValue(topic);
        List<PartitionInfo> availablePartitions = cluster.availablePartitionsForTopic(topic);
        if (!availablePartitions.isEmpty()) {
            int part = Utils.toPositive(nextValue) % availablePartitions.size();
            return availablePartitions.get(part).partition();
        } else {
            // no partitions are available, give a non-available partition
            return Utils.toPositive(nextValue) % numPartitions;
        }
    }

    private int nextValue(String topic) {
        AtomicInteger counter = topicCounterMap.computeIfAbsent(topic, k -> new AtomicInteger(0));
        return counter.getAndIncrement();
    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> map) {

    }
}
