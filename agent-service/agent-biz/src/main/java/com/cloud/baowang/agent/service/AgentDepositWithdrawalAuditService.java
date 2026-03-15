package com.cloud.baowang.agent.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.po.AgentDepositWithdrawalAuditPO;
import com.cloud.baowang.agent.repositories.AgentDepositWithdrawalAuditRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
@AllArgsConstructor
public class AgentDepositWithdrawalAuditService {

    private final AgentDepositWithdrawalAuditRepository agentDepositWithdrawalAuditRepository;

    public Map<String, List<AgentDepositWithdrawalAuditPO>> getAuditInfoMap(List<String> orderNoList) {
        Map<String, List<AgentDepositWithdrawalAuditPO>> auditInfoMap = new HashMap<>();
        if(!orderNoList.isEmpty()){
            LambdaQueryWrapper<AgentDepositWithdrawalAuditPO > lqw = new LambdaQueryWrapper<>();
            lqw.in(AgentDepositWithdrawalAuditPO::getOrderNo,orderNoList);
            List<AgentDepositWithdrawalAuditPO> list = agentDepositWithdrawalAuditRepository.selectList(lqw);
            auditInfoMap = list.stream()
                    .collect(Collectors.groupingBy(AgentDepositWithdrawalAuditPO::getOrderNo));
        }
        return auditInfoMap;
    }
}
