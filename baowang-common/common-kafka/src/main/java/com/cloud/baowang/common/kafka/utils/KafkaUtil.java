package com.cloud.baowang.common.kafka.utils;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.cloud.baowang.common.core.utils.SpringUtils;
import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;


@Component
@Slf4j
public class KafkaUtil implements ApplicationContextAware {

    private static KafkaTemplate<String, Object> kafkaTemplate;
    private static final Map<String, String> topicNameMap = Maps.newHashMap();

    @PostConstruct
    public void init() {
        BeanExpressionResolver resolver = new StandardBeanExpressionResolver();
        ConfigurableListableBeanFactory beanFactory = SpringUtils.beanFactory;
        BeanExpressionContext expressionContext = new BeanExpressionContext(beanFactory, null);

        // 解析表达式
        Field[] declaredFields = TopicsConstants.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            try {
                Object o = declaredField.get(null);
                String str = o.toString();
                String s = beanFactory.resolveEmbeddedValue(str);
                String value = Objects.requireNonNull(resolver.evaluate(s, expressionContext)).toString();
                topicNameMap.put(str, value);
                log.info("初始化kafka topic spel表达式,key:{},value:{}", str, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static String resolveActualTopic(String topic) {
        String actualTopic = topicNameMap.get(topic);
        if (StrUtil.isNotBlank(actualTopic)) {
            return actualTopic;
        }
        return topic;
    }

    public static <T extends MessageBaseVO> void send(String topic, T message) {
        String msgId = IdWorker.get32UUID();
        message.setMsgId(msgId);
        CompletableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(resolveActualTopic(topic), message);
        log.info("发送MQ消息 --> send topic:{},message:{},sendResult:{}", topic, JSON.toJSON(message), JSON.toJSON(sendResult));
    }

    public static <T extends MessageBaseVO> void send(String topic, String key, T message) {
        String msgId = IdWorker.get32UUID();
        message.setMsgId(msgId);
        CompletableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(resolveActualTopic(topic), key, message);
        log.info("发送MQ消息 --> send topic:{}, key:{}, message:{}", topic, JSON.toJSON(message), JSON.toJSON(sendResult));
    }

    public static <T extends MessageBaseVO> void send(String topic, Integer partition, String key, T message) {
        String msgId = IdWorker.get32UUID();
        message.setMsgId(msgId);
        CompletableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(resolveActualTopic(topic), partition, key, message);
        log.info("发送MQ消息 --> send topic:{}, partition:{}, key:{}, message:{}", topic, partition, JSON.toJSON(message), JSON.toJSON(sendResult));
    }

    public static <T extends MessageBaseVO> void send(String topic, Integer partition, Long timestamp, String key, T message) {
        String msgId = IdWorker.get32UUID();
        message.setMsgId(msgId);
        CompletableFuture<SendResult<String, Object>> sendResult = kafkaTemplate.send(resolveActualTopic(topic), partition, timestamp, key, message);
        log.info("发送MQ消息 --> send topic:{}, partition:{},timestamp:{}, key:{}, message:{}", topic, partition, timestamp, JSON.toJSON(message), JSON.toJSON(sendResult));
    }


    public KafkaTemplate<?, ?> getKafkaTemplate() {
        return kafkaTemplate;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        KafkaUtil.kafkaTemplate = applicationContext.getBean(KafkaTemplate.class);
    }
}
