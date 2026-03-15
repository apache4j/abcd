package com.cloud.baowang.agent.api;

import com.cloud.baowang.agent.api.api.AgentClientHomeApi;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.service.AgentClientHomeService;
import com.cloud.baowang.agent.api.vo.agent.clienthome.DataCompareGraphParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.DataCompareGraphVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.GetHomeAgentInfoResponseVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.MonthClientAgentResponseVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SaveQuickEntryParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SelectQuickEntryParam;
import com.cloud.baowang.agent.api.vo.agent.clienthome.SelectQuickEntryResponse;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentClientHomeApiImpl implements AgentClientHomeApi {

    private final AgentClientHomeService agentClientHomeService;

    @Override
    public ResponseVO<GetHomeAgentInfoResponseVO> getHomeAgentInfo(CurrencyCodeReqVO currencyCodeReqVO) {
        return agentClientHomeService.getHomeAgentInfo(currencyCodeReqVO);
    }

    @Override
    public ResponseVO<MonthClientAgentResponseVO> monthStatistics(CurrencyCodeReqVO currencyCodeReqVO) {
        return agentClientHomeService.monthStatistics(currencyCodeReqVO);
    }

    @Override
    public ResponseVO<SelectQuickEntryResponse> selectQuickEntry(SelectQuickEntryParam vo) {
        return agentClientHomeService.selectQuickEntry(vo);
    }

    @Override
    public ResponseVO<?> saveQuickEntry(SaveQuickEntryParam vo) {
        return agentClientHomeService.saveQuickEntry(vo);
    }

    @Override
    public ResponseVO<DataCompareGraphVO> dataCompareGraph(DataCompareGraphParam vo) {
        return agentClientHomeService.dataCompareGraph(vo);
    }
}
