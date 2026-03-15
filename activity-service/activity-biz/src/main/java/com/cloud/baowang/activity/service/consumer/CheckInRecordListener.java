package com.cloud.baowang.activity.service.consumer;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.api.vo.task.TaskNoviceTriggerVO;
import com.cloud.baowang.activity.service.ActivitySpinWheelService;
import com.cloud.baowang.activity.service.SiteActivityCheckInRecordService;
import com.cloud.baowang.activity.service.SiteTaskOrderRecordService;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 新人任务，触发新人奖励
 */
@Slf4j
@Component
@AllArgsConstructor
public class CheckInRecordListener {

    private final SiteActivityCheckInRecordService checkInRecordService;





    @KafkaListener(topics = TopicsConstants.TASK_DAILY_WEEK_ORDER_RECORD_TOPIC, groupId = GroupConstants.SITE_ACTIVITY_CHECK_IN_RECORD_GROUP)
    public void checkinBet(UserVenueWinLossSendVO sendVO, Acknowledgment ackItem) {
        log.info("签到活动 触发任务消息:{}", JSON.toJSONString(sendVO));

        try {
            try {
                boolean flag = checkInRecordService.processCheckinBet(sendVO);
                if (!flag) {
                    log.error("签到活动出错了 信息：{}", JSON.toJSONString(sendVO));
                }
            } catch (Exception e) {
                log.error("处理签到活动消息时发生异常，信息：{}", JSON.toJSONString(sendVO), e);
            }
        } finally {
            ackItem.acknowledge();
        }
    }

    @KafkaListener(topics = TopicsConstants.MEMBER_RECHARGE, properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.MEMBER_RECHARGE_GROUP_CHECK_IN_ACTIVITY)
    public void checkinMemberRechargeMessage(RechargeTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("接收到会员充值消息,处理签到活动存款活动:{}", JSON.toJSONString(triggerVO));
        // 判断消息是否合规
        if (triggerVO.getRechargeAmount() == null
                || triggerVO.getUserId() == null
                || triggerVO.getCurrencyCode() == null
                || triggerVO.getOrderNumber() == null) {
            log.warn("签到活动处理存款失败，校验入参失败，参数缺少必要字段。充值信息：{}", JSON.toJSONString(triggerVO));
            ackItem.acknowledge();
            return;
        }
        try {
            boolean flag = checkInRecordService.checkinMemberRechargeMessage(triggerVO);
            // 校验是否
            if (!flag) {
                log.error("签到活动任务处理失败，存款信息：{}", JSON.toJSONString(triggerVO));
            }
        } catch (Exception e) {
            log.error("签到活动处理失败，存款信息，信息：{}", JSON.toJSONString(triggerVO), e);
        }
        ackItem.acknowledge();
    }


}
