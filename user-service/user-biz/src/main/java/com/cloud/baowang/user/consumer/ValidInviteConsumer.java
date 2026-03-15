package com.cloud.baowang.user.consumer;

import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserLatestBetMqVO;
import com.cloud.baowang.common.kafka.vo.ValidInviteUserRechargeMqVO;
import com.cloud.baowang.user.service.SiteUserInviteRecordService;
import com.cloud.baowang.user.service.UserInfoService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * 会员
 */
@Slf4j
@Component
@AllArgsConstructor
public class ValidInviteConsumer {

    private final SiteUserInviteRecordService inviteRecordService;

    /**
     *  更新会员有效邀请信息
     * @param vo
     * @param ackItem
     */
    @KafkaListener(topics = TopicsConstants.VALID_INVITE_USER_RECHARGE, groupId = GroupConstants.VALID_INVITE_USER_RECHARGE_GROUP)
    public void processUserBetTime(ValidInviteUserRechargeMqVO vo, Acknowledgment ackItem) {
        log.info("更新会员有效邀请信息 {}", JSON.toJSONString(vo));
        if (null == vo) {
            log.error("更新会员有效邀请信息-MQ队列-参数不能为空");
            return;
        }
        try {
            inviteRecordService.updateDepositInfo(vo);
        } catch (Exception e) {
            log.error("更新会员有效邀请信息 error : "+e.getMessage());
        } finally {
            ackItem.acknowledge();
        }
    }
}
