package com.cloud.baowang.wallet.consumer;


import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserRebateRecordDetailsVO;
import com.cloud.baowang.common.kafka.vo.UserRebateRecordMqVO;
import com.cloud.baowang.wallet.api.vo.userwallet.SiteRebateRewardRecordVO;
import com.cloud.baowang.wallet.service.SiteRebateRewardRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.List;


@Slf4j
@Component
@AllArgsConstructor
public class UserRebateRecordConsumer {


    private final SiteRebateRewardRecordService userRebateRewardRecordService;


    @KafkaListener(topics = TopicsConstants.USER_REBATE_REWARD_TOPIC, groupId = GroupConstants.USER_REBATE_REWARD_GROUP)
    public void onUserRebateArrived(UserRebateRecordMqVO userRebateMqVo, Acknowledgment ackItem) {
        log.info("派发返水-MQ队列错，入参{}",userRebateMqVo);

        long start = System.currentTimeMillis();
        try {
            if (userRebateMqVo != null) {
                String siteCode = userRebateMqVo.getSiteCode();
                List<UserRebateRecordDetailsVO> rebateReq = userRebateMqVo.getUserRebateRecordList();
                if (rebateReq != null && !rebateReq.isEmpty()) {
                    List<SiteRebateRewardRecordVO> rebateList = ConvertUtil.convertListToList(rebateReq, new SiteRebateRewardRecordVO());
                    userRebateRewardRecordService.bachAddSiteRebateRewardRecordS(rebateList,siteCode);
                }
            }
        } catch (Exception e) {
            log.info("派发返水-MQ队列执行报错，报错信息{}", e.getMessage());
        } finally {
            log.info("派发流水,MQ队列-消息id:{},整体耗时:{}毫秒", userRebateMqVo.getMsgId(), System.currentTimeMillis() - start);
            ackItem.acknowledge();
        }
    }

}
