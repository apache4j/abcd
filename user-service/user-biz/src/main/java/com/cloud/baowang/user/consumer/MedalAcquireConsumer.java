package com.cloud.baowang.user.consumer;

import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.user.api.medal.MedalAcquireApiImpl;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @Desciption: 勋章获取消费者
 * @Author: Ford
 * @Date: 2024/10/8 11:39
 * @Version: V1.0
 * MedalAcquireReqVO medalAcquireReqVO
 **/
@Slf4j
@Component
@AllArgsConstructor
public class MedalAcquireConsumer {
    private final MedalAcquireApiImpl medalAcquireApiImpl;


    /**
     * 批量解锁勋章
     * @param medalAcquireBatchReqVO
     * @param ackItem
     */
    @KafkaListener(topics = TopicsConstants.MEDAL_ACQUIRE_QUEUE, groupId = GroupConstants.MEDAL_ACQUIRE_GROUP)
    public void unLockMedal(MedalAcquireBatchReqVO medalAcquireBatchReqVO, Acknowledgment ackItem) {
        log.info("站点:{}批量解锁消费者开始...",medalAcquireBatchReqVO.getSiteCode());
        for(MedalAcquireReqVO medalAcquireReqVO:medalAcquireBatchReqVO.getMedalAcquireReqVOList()){
            medalAcquireApiImpl.unLockMedal(medalAcquireReqVO);
        }
        ackItem.acknowledge();
    }
}
