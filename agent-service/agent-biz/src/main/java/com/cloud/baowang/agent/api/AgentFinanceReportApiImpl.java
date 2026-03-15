package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentFinanceReportApi;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceCurrencyResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentTeamFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoResVO;
import com.cloud.baowang.agent.service.AgentFinanceReportService;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class AgentFinanceReportApiImpl implements AgentFinanceReportApi {

    private final AgentFinanceReportService agentFinanceReportService;

    @Override
    public ResponseVO<AgentFinanceResVO> financeReport(AgentFinanceReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return ResponseVO.success(agentFinanceReportService.financeReport(vo));
    }

    @Override
    public ResponseVO<Page<AgentFinanceCurrencyResVO>> financeReportTeamFinance(AgentTeamFinanceReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return ResponseVO.success(agentFinanceReportService.financeReportTeamFinance(vo));
    }

    @Override
    public ResponseVO<List<AgentVenueFeeInfoResVO>> venueFeeInfo(AgentVenueFeeInfoReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return ResponseVO.success(agentFinanceReportService.venueFeeInfo(vo));
    }

    @Override
    public ResponseVO<List<AgentDepositWithdrawFeeInfoResVO>> depositWithdrawFeeInfo(AgentDepositWithdrawFeeInfoReqVO vo) {
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        return ResponseVO.success(agentFinanceReportService.depositWithdrawFeeInfo(vo));
    }
}
