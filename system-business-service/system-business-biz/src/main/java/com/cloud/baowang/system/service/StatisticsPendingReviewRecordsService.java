package com.cloud.baowang.system.service;

import com.cloud.baowang.agent.api.api.AgentInfoModifyReviewApi;
import com.cloud.baowang.agent.api.api.AgentManualDownApi;
import com.cloud.baowang.agent.api.api.AgentWithdrawReviewApi;
import com.cloud.baowang.system.api.vo.StatisticsPendingVO;
import com.cloud.baowang.user.api.enums.SiteTodoEnum;
import com.cloud.baowang.wallet.api.api.UserManualDownRecordApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawReviewApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsPendingReviewRecordsService {
    private final UserManualDownRecordApi userManualDownRecordApi;
    private final UserWithdrawReviewApi withdrawApi;
    private final AgentManualDownApi agentManualDownApi;
    private final AgentWithdrawReviewApi agentWithdrawReviewApi;
    private final AgentInfoModifyReviewApi agentInfoModifyReviewApi;

    public List<StatisticsPendingVO> getRecordsBySiteCode(String siteCode) {
        List<StatisticsPendingVO> result = new ArrayList<>();
        //会员提款
        StatisticsPendingVO userVo = new StatisticsPendingVO();
        userVo.setCode(SiteTodoEnum.USER_WITHDRAWAL_AUDIT.getCode());
        //会员人工加额
        StatisticsPendingVO userManVo = new StatisticsPendingVO();
        userManVo.setCode(SiteTodoEnum.USER_MANUAL_INCREASE_AUDIT.getCode());
        //代理提款
        StatisticsPendingVO agentVo = new StatisticsPendingVO();
        agentVo.setCode(SiteTodoEnum.AGENT_WITHDRAWAL_AUDIT.getCode());
        //代理人工加额
        StatisticsPendingVO agentManVo = new StatisticsPendingVO();
        agentManVo.setCode(SiteTodoEnum.AGENT_MANUAL_INCREASE_AUDIT.getCode());

        long userManualDownTotal = userManualDownRecordApi.getTotalPendingReviewBySiteCode(siteCode);
        userManVo.setTotal(userManualDownTotal);

        long userWithdrawTotal = withdrawApi.getTotalPendingReviewBySiteCode(siteCode);
        userVo.setTotal(userWithdrawTotal);

        long agentDownTotal = agentManualDownApi.getTotalPendingReviewBySiteCode(siteCode);
        agentManVo.setTotal(agentDownTotal);

        long agentWithdrawTotal = agentWithdrawReviewApi.getTotalPendingReviewBySiteCode(siteCode);
        agentVo.setTotal(agentWithdrawTotal);

        result.add(userVo);
        result.add(userManVo);
        result.add(agentVo);
        result.add(agentManVo);
        return result;
    }

    public StatisticsPendingVO getAgentInfoReviewRecord(String siteCode) {
        StatisticsPendingVO result = new StatisticsPendingVO();
        result.setCode(SiteTodoEnum.AGENT_ACCOUNT_MODIFY.getCode());
        long total = agentInfoModifyReviewApi.getAgentInfoReviewRecord(siteCode);
        result.setTotal(total);
        return result;
    }
}
