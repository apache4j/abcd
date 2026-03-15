package com.cloud.baowang.common.kafka.config;

import com.cloud.baowang.common.kafka.properties.KafkaProps;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaConsumerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.DefaultKafkaProducerFactoryCustomizer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.List;

@Configuration
@Slf4j
public class CustomKafkaPropsConfig {


    @Bean
    public DefaultKafkaProducerFactory<?, ?> kafkaProducerFactory(
            ObjectProvider<DefaultKafkaProducerFactoryCustomizer> customizers
            , KafkaProperties kafkaProperties) {
        // 强制加载 CustomPartitioner 类
        try {
            Class.forName("com.cloud.baowang.common.kafka.interceptor.CustomPartitioner");
        } catch (ClassNotFoundException e) {
            log.error("load class CustomPartitioner err",e);
        }
//        kafkaProperties.getProducer().getProperties().put(ProducerConfig.PARTITIONER_CLASS_CONFIG, "com.cloud.baowang.common.kafka.interceptor.CustomPartitioner");

        DefaultKafkaProducerFactory<?, ?> factory = new DefaultKafkaProducerFactory<>(
                kafkaProperties.buildProducerProperties());
        String transactionIdPrefix = kafkaProperties.getProducer().getTransactionIdPrefix();
        if (transactionIdPrefix != null) {
            factory.setTransactionIdPrefix(transactionIdPrefix);
        }
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    @Bean
    public DefaultKafkaConsumerFactory<?, ?> kafkaConsumerFactory(
            ObjectProvider<DefaultKafkaConsumerFactoryCustomizer> customizers,
            KafkaProperties kafkaProperties) {
        kafkaProperties.getConsumer().getProperties().put(JsonDeserializer.TRUSTED_PACKAGES, "com.cloud.baowang.*");


        DefaultKafkaConsumerFactory<Object, Object> factory = new DefaultKafkaConsumerFactory<>(
                kafkaProperties.buildConsumerProperties());
        customizers.orderedStream().forEach((customizer) -> customizer.customize(factory));
        return factory;
    }

    @Bean
    public KafkaAdmin.NewTopics  topics(KafkaProps kafkaProps){
        List<KafkaProps.Topic> topics = kafkaProps.getTopics();
        if(topics == null){
            return null;
        }
        int size = topics.size();
        NewTopic[] newTopics = new NewTopic[size];
        for (int i = 0; i < size; i++) {
            KafkaProps.Topic topic = topics.get(i);
            NewTopic newTopic = new NewTopic(topic.getTopicName(),topic.getNumPartitions(),topic.getReplicationFactor());
            newTopics[i] = newTopic;
        }
        return new KafkaAdmin.NewTopics(newTopics);
    }

}
