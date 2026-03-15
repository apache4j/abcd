package com.cloud.baowang.agent.controller;

import com.cloud.baowang.agent.api.api.AgentCoinApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinDetailVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "代理账变明细查询")
@AllArgsConstructor
@RestController
@RequestMapping("/agent-coin/api")
public class AgentCoinController {

    private final AgentCoinApi agentCoinApi;

    @Operation(summary = "代理钱包余额信息")
    @PostMapping(value = "/getAgentCoinBalance")
    public ResponseVO<AgentCoinDetailVO> getAgentCoinBalance() {

        String agentAccount = CurrReqUtils.getAccount();
        String siteCode = CurrReqUtils.getSiteCode();
        AgentCoinDetailVO agentCoinDetailVO = new AgentCoinDetailVO();
        ResponseVO<AgentCoinBalanceVO> commissionCoinBalance = agentCoinApi.getCommissionCoinBalanceSite(agentAccount,siteCode);
        ResponseVO<AgentCoinBalanceVO> quotaCoinBalance = agentCoinApi.getQuotaCoinBalanceSite(agentAccount,siteCode);
        agentCoinDetailVO.setCommissionCoinBalance(commissionCoinBalance.getData());
        agentCoinDetailVO.setQuotaCoinBalance(quotaCoinBalance.getData());
        return ResponseVO.success(agentCoinDetailVO);
    }
}
