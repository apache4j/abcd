package com.cloud.baowang.agent.api.api;

import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceVO;
import com.cloud.baowang.agent.api.vo.agentreview.AddGeneralAgentVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "remoteAgentFinanceApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentFinance")
public interface AgentFinanceApi {
    String PREFIX = ApiConstants.PREFIX + "/agent-finance/api";


    @PostMapping(PREFIX + "/getAgentFinanceInfo")
    @Operation(summary = "代理详情财务信息")
    ResponseVO<AgentFinanceVO> getAgentFinanceInfo(@RequestBody AgentFinanceRequestVO agentFinanceRequestVO);

}
