
package com.cloud.baowang.report.consumer;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.UserRechargeWithdrawMqVO;
import com.cloud.baowang.report.service.ReportUserRechargeService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
@AllArgsConstructor
public class UserRechargeWithdrawConsumer {

    private final ReportUserRechargeService reportUserRechargeService;

    @KafkaListener(topics = TopicsConstants.USER_RECHARGE_WITHDRAW, groupId = GroupConstants.USER_RECHARGE_WITHDRAW_GROUP)
    public void rechargeWithdrawMessage(UserRechargeWithdrawMqVO vo, Acknowledgment ackItem) {

        if (null == vo) {
            log.error("会员累计存款-MQ队列-参数不能为空");
            return;
        }
        String jsonStr = JSON.toJSONString(vo);
        log.info("会员累计存款(==============MQ队列==============)参数:{}", jsonStr);

        long start = System.currentTimeMillis();
        if (Objects.isNull(vo)) {
            log.error("会员累计存款-MQ队列-JSON解析异常");
            return;
        }

        //更新累计存款
        reportUserRechargeService.addRechargeAmount(start,jsonStr,vo);
        // 更新状态
        ackItem.acknowledge();

    }



}

