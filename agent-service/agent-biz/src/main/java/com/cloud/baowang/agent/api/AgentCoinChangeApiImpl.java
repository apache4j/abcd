package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCoinChangeApi;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeDetailReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinChangeReqVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordDetailVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCustomerCoinRecordVO;
import com.cloud.baowang.agent.service.AgentCoinChangeService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@AllArgsConstructor
@Slf4j
public class AgentCoinChangeApiImpl implements AgentCoinChangeApi {

    private final AgentCoinChangeService agentCoinChangeService;

    @Override
    public ResponseVO<Page<AgentCustomerCoinRecordVO>> listAgentCustomerCoinRecord(AgentCoinChangeReqVO vo) {
        return agentCoinChangeService.listAgentCustomerCoinRecord(vo);
    }

    @Override
    public ResponseVO<AgentCustomerCoinRecordDetailVO> getCoinRecordDetail(AgentCoinChangeDetailReqVO vo) {
        return agentCoinChangeService.getCoinRecordDetail(vo);
    }
}
