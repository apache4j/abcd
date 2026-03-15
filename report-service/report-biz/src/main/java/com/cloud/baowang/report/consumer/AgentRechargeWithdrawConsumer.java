
package com.cloud.baowang.report.consumer;


import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.vo.AgentRechargeWithdrawMqVO;
import com.cloud.baowang.report.service.ReportAgentRechargeWithdrawService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@Component
@AllArgsConstructor
public class AgentRechargeWithdrawConsumer {

    private final ReportAgentRechargeWithdrawService reportUserRechargeService;

    @KafkaListener(topics = TopicsConstants.AGENT_RECHARGE_WITHDRAW, groupId = GroupConstants.Agent_RECHARGE_WITHDRAW_GROUP)
    public void agentRechargeWithdrawMessage(AgentRechargeWithdrawMqVO vo, Acknowledgment ackItem) {

        if (null == vo) {
            log.error("代理累计存款-MQ队列-参数不能为空");
            return;
        }
        String jsonStr = JSON.toJSONString(vo);
        log.info("代理累计存款(==============MQ队列==============)参数:{}", jsonStr);

        long start = System.currentTimeMillis();
        if (Objects.isNull(vo)) {
            log.error("代理累计存款-MQ队列-JSON解析异常");
            return;
        }

        //更新累计存款
        reportUserRechargeService.addRechargeAmount(start,jsonStr,vo);
        // 更新状态
        ackItem.acknowledge();

    }



}

