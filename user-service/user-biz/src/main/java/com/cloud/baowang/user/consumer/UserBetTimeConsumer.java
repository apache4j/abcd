package com.cloud.baowang.user.consumer;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserLatestBetMqVO;
import com.cloud.baowang.user.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 会员
 */
@Slf4j
@Component
@AllArgsConstructor
public class UserBetTimeConsumer {

    private final UserInfoService userInfoService;

    /**
     * 会员更新最新下注时间
     *
     * @param userLatestBetMqVO
     * @param ackItem
     */
    @KafkaListener(topics = TopicsConstants.USER_LATEST_BET_QUEUE, groupId = GroupConstants.USER_BET_TIME_FLUSH)
    public void processUserBetTime(UserLatestBetMqVO userLatestBetMqVO, Acknowledgment ackItem) {
        log.info("收到会员最新下注时间更新mq:{}", JSON.toJSONString(userLatestBetMqVO));
        userInfoService.processUserBetTime(userLatestBetMqVO);
        ackItem.acknowledge();
    }
}
