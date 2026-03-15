package com.cloud.baowang.site.controller.agent;

import com.cloud.baowang.agent.api.api.AgentFinanceApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceVO;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("agent-finance/api")
@Tag(name = "代理-代理详情-财务信息")
public class AgentFinanceController {

    private final AgentFinanceApi agentFinanceApi;


    @PostMapping("getAgentFinanceInfo")
    @Operation(summary = "获取代理详情财务信息")
    public ResponseVO<AgentFinanceVO> getAgentFinanceInfo(@Valid @RequestBody AgentFinanceRequestVO agentFinanceRequestVO){
        agentFinanceRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        return agentFinanceApi.getAgentFinanceInfo(agentFinanceRequestVO);

    }

}
