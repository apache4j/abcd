package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentCoinApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.agent.service.AgentCommissionCoinService;
import com.cloud.baowang.agent.service.AgentQuotaCoinService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentCoinApiImpl implements AgentCoinApi {

    private final AgentCommissionCoinService agentCommissionCoinService;

    private final AgentQuotaCoinService agentQuotaCoinService;


    @Override
    public ResponseVO<AgentCoinBalanceVO> getCommissionCoinBalanceSite(String agentAccount, String siteCode) {
        AgentCoinBalanceVO agentCoinBalanceVO = agentCommissionCoinService.getCommissionCoinBalanceSite(agentAccount, siteCode);
        return ResponseVO.success(agentCoinBalanceVO);
    }



    @Override
    public ResponseVO<AgentCoinBalanceVO> getQuotaCoinBalanceSite(String agentAccount, String siteCode) {
        AgentCoinBalanceVO agentCoinBalanceVO = agentQuotaCoinService.getQuotaCoinBalanceSite(agentAccount, siteCode);
        return ResponseVO.success(agentCoinBalanceVO);
    }
}
