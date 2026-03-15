package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentFinanceApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceRequestVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentFinanceVO;
import com.cloud.baowang.agent.service.AgentFinanceService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Desciption:
 * @Author: qiqi
 **/
@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentFinanceApiImpl implements AgentFinanceApi {
    private final AgentFinanceService agentFinanceService;


    @Override
    public ResponseVO<AgentFinanceVO> getAgentFinanceInfo(AgentFinanceRequestVO agentFinanceRequestVO) {
        return agentFinanceService.getAgentFinanceInfo(agentFinanceRequestVO);
    }
}
