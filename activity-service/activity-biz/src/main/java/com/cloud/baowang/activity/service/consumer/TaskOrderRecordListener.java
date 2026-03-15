package com.cloud.baowang.activity.service.consumer;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.api.vo.task.TaskNoviceTriggerVO;
import com.cloud.baowang.activity.service.ActivitySpinWheelService;
import com.cloud.baowang.activity.service.SiteTaskOrderRecordService;
import com.cloud.baowang.user.api.vo.UserInfoVO;
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
public class TaskOrderRecordListener {

    private final SiteTaskOrderRecordService siteTaskOrderRecordService;

    private final ActivitySpinWheelService activitySpinWheelService;

    private final UserInfoApi userInfoApi;


    @KafkaListener(topics = TopicsConstants.TASK_NOVICE_ORDER_RECORD_TOPIC, groupId = GroupConstants.SITE_TASK_ORDER_RECORD_GROUP)
    public void noviceTask(TaskNoviceTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("接收到新人任务消息:{}", JSON.toJSONString(triggerVO));
        try {
            boolean flag = siteTaskOrderRecordService.process(triggerVO);
            if (!flag) {
                log.error("处理新人任务消息失败，信息：{}", JSON.toJSONString(triggerVO));
            }
        } catch (Exception e) {
            log.error("处理新人任务消息时发生异常，信息：{}", JSON.toJSONString(triggerVO), e);
        } finally {
            ackItem.acknowledge();
        }
    }

    @KafkaListener(topics = TopicsConstants.TASK_DAILY_WEEK_ORDER_RECORD_TOPIC, groupId = GroupConstants.SITE_TASK_DAILY_WEEK_RECORD_GROUP)
    public void DailyAndWeekTask(UserVenueWinLossSendVO sendVO, Acknowledgment ackItem) {
        log.info("每日任务/每周任务 触发任务消息:{}", JSON.toJSONString(sendVO));

        try {
            try {
                boolean flag = siteTaskOrderRecordService.processDailyAndWeek(sendVO);
                if (!flag) {
                    log.error("每日任务/每周任务出错了 信息：{}", JSON.toJSONString(sendVO));
                }
            } catch (Exception e) {
                log.error("处理每日任务/每周任务消息时发生异常，信息：{}", JSON.toJSONString(sendVO), e);
            }
            try {
                boolean flag = activitySpinWheelService.processBetAward(sendVO);
                if (!flag) {
                    log.error("处理转盘任务消息时失败 信息：{}", JSON.toJSONString(sendVO));
                }
            } catch (Exception e) {
                log.error("处理转盘任务消息时发生异常，信息：{}", JSON.toJSONString(sendVO), e);
            }

        } finally {
            ackItem.acknowledge();
        }
    }

    @KafkaListener(topics = TopicsConstants.MEMBER_RECHARGE, properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.MEMBER_RECHARGE_GROUP_TASK_DAILY)
    public void memberRechargeMessage(RechargeTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("接收到会员充值消息,处理每日任务存款活动，每周任务邀请活动:{}", JSON.toJSONString(triggerVO));
        // 判断消息是否合规
        if (triggerVO.getRechargeAmount() == null
                || triggerVO.getUserId() == null
                || triggerVO.getCurrencyCode() == null
                || triggerVO.getOrderNumber() == null) {
            log.error("每日存款处理存款失败，校验入参失败，参数缺少必要字段。充值信息：{}", JSON.toJSONString(triggerVO));
            ackItem.acknowledge();
            return;
        }
        try {
            boolean flag = siteTaskOrderRecordService.processDailyDepositTask(triggerVO);
            // 校验是否
            if (!flag) {
                log.error("每日存款任务处理失败，存款信息：{}", JSON.toJSONString(triggerVO));
            }
        } catch (Exception e) {
            log.error("每日存款任务处理失败，存款信息，信息：{}", JSON.toJSONString(triggerVO), e);
        }
        try {
            boolean flag2 = siteTaskOrderRecordService.processWeekDepositTask(triggerVO);
            if (!flag2) {
                log.error("每周邀请活动，存款任务处理失败，存款信息：{}", JSON.toJSONString(triggerVO));
            }
        } catch (Exception e) {
            log.error("每周邀请活动，存款任务处理失败，存款信息：{}", JSON.toJSONString(triggerVO), e);
        }
        ackItem.acknowledge();
    }


}
