package com.cloud.baowang.activity.service.consumer;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class ActivitySendListConsumer {

    private final SiteActivityOrderRecordService siteActivityOrderRecordService;
    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordV2Service;

    @KafkaListener(topics = TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, groupId = GroupConstants.SEND_USER_ACTIVITY_GROUP_LIST)
    public void toActivitySend(ActivitySendListMqVO activitySendMqVO, Acknowledgment ackItem) {
        log.info("收到发放礼包消息:{}", JSON.toJSON(activitySendMqVO));
        if (ObjectUtil.isEmpty(activitySendMqVO)) {
            log.info("收到发放礼包消息:{},缺少参数异常", activitySendMqVO);
            return;
        }
        List<ActivitySendMqVO> list = activitySendMqVO.getList();
        if (CollectionUtil.isEmpty(list)) {
            log.info("收到发放礼包消息:{},空消息", activitySendMqVO);
            return;
        }

        for (ActivitySendMqVO vo : list) {
            if (ObjectUtil.isEmpty(vo.getActivityTemplate())
                    || ObjectUtil.isEmpty(vo.getUserId())
                    || ObjectUtil.isEmpty(vo.getDistributionType())
                    || ObjectUtil.isEmpty(vo.getOrderNo())
                    || ObjectUtil.isEmpty(vo.getActivityId())
                    || ObjectUtil.isEmpty(vo.getActivityAmount())
            ) {
                log.info("收到发放礼包消息:{},缺少参数异常", activitySendMqVO);
                continue;
            }
            String lockKey = RedisConstants.getToSetSiteCodeKeyConstant(vo.getSiteCode(), String.format(RedisConstants.ACTIVITY_SEND_ORDER_NO, vo.getOrderNo()));
            String lock = RedisUtil.acquireImmediate(lockKey, 10L);
            try {

                if (StringUtils.isBlank(lock)) {
                    log.info("收到发放礼包消息:{},已被其他线程执行", vo.getOrderNo());
                    continue;
                }

                //发放礼包, 盘口分开
                if (vo.getHandicapMode()==0){
                    siteActivityOrderRecordService.addActivityOrderRecord(vo);
                }else {
                    siteActivityOrderRecordV2Service.addActivityOrderRecord(vo);
                }

                log.info("收到发放礼包消息:{},执行结束", vo.getOrderNo());


            } catch (Exception e) {
                log.error("收到发放礼包消息异常:{}", vo, e);
                throw new RuntimeException(e);
            } finally {
                if (StringUtils.isNotBlank(lock)) {
                    boolean release = RedisUtil.release(lockKey, lock);
                    log.info("收到发放礼包消息:{},执行结束,删除锁:{}", vo.getOrderNo(), release);
                }
            }
        }
        ackItem.acknowledge();

    }
}
