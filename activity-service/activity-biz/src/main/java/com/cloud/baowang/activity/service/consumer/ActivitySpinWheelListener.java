package com.cloud.baowang.activity.service.consumer;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.activity.service.ActivitySpinWheelService;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 转盘活动监听每一次存款，当每笔存款大于等于指定的金额，获取抽奖次数
 */
@Slf4j
@Component
@AllArgsConstructor
public class ActivitySpinWheelListener {

    private final ActivitySpinWheelService activitySpinWheelService;


    private final UserInfoApi userInfoApi;


    @KafkaListener(topics = TopicsConstants.MEMBER_RECHARGE, properties = {"auto.offset.reset=latest"}, groupId = GroupConstants.MEMBER_RECHARGE_GROUP_SPIN_WHEEL)
    public void memberRechargeMessage(RechargeTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("接收到会员充值消息:{}", JSON.toJSONString(triggerVO));
        // 判断消息是否合规
        if (triggerVO.getRechargeAmount() == null
                || triggerVO.getUserId() == null
                || triggerVO.getCurrencyCode() == null
                || triggerVO.getOrderNumber() == null) {
            log.info("转盘活动处理存款失败，校验入参失败，参数缺少必要字段。充值信息：{}", JSON.toJSONString(triggerVO));
            ackItem.acknowledge();
            return;
        }
        //
        UserInfoVO byUserId = userInfoApi.getByUserId(triggerVO.getUserId());
        /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(byUserId.getAccountType())) {
            return;
        }*/
        Boolean aBoolean = activitySpinWheelService.checkTask(byUserId.getSuperAgentId());
        if (!aBoolean) {
            log.info("转盘活动处理存款成功，会员上级代理，标记不能参加活动。充值信息：{}", JSON.toJSONString(triggerVO));
            return;
        }

        boolean flag = activitySpinWheelService.validateAndReward(triggerVO);
        // 校验是否
        if (!flag) {
            log.error("转盘活动处理存款失败，存款信息：{}", JSON.toJSONString(triggerVO));
        }
        ackItem.acknowledge();
    }


}
