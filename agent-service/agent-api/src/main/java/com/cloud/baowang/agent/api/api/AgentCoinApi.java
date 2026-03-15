package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinBalanceVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentCoinApi", value = ApiConstants.NAME)
@Tag(name = "RPC 代理账变明细查询 服务")
public interface AgentCoinApi {

    String PREFIX = ApiConstants.PREFIX + "/agentCoin/api";


    @Operation(summary = "代理佣金钱包余额")
    @PostMapping(value = PREFIX + "/getCommissionCoinBalanceSite")
    ResponseVO<AgentCoinBalanceVO> getCommissionCoinBalanceSite(@RequestParam("agentAccount") String agentAccount,
                                                            @RequestParam("siteCode") String siteCode);


    @Operation(summary = "代理额度钱包余额")
    @PostMapping(value = PREFIX + "/getQuotaCoinBalanceSite")
    ResponseVO<AgentCoinBalanceVO> getQuotaCoinBalanceSite(@RequestParam("agentAccount") String agentAccount,
                                                       @RequestParam("siteCode") String siteCode);
}
