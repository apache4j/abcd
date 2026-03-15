package com.cloud.baowang.agent.controller;

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
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("agent-finance-report")
@Tag(name = "代理-财务报表")
public class AgentFinanceReportController {

    @Autowired
    AgentFinanceReportApi agentFinanceReportApi;

    @Operation(summary = "个人&团队财务-个人财物报表")
    @PostMapping("selfFinance")
    public ResponseVO<AgentFinanceResVO> selfFinance(@Valid @RequestBody AgentFinanceReqVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentFinanceReportApi.financeReport(vo);
    }

    @Operation(summary = "团队财务-明细")
    @PostMapping("teamFinance")
    public ResponseVO<Page<AgentFinanceCurrencyResVO>> teamFinance(@Valid @RequestBody AgentTeamFinanceReqVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentFinanceReportApi.financeReportTeamFinance(vo);
    }

    @Operation(summary = "平台费详情")
    @PostMapping("venueFeeInfo")
    public ResponseVO<List<AgentVenueFeeInfoResVO>> venueFeeInfo(@Valid @RequestBody AgentVenueFeeInfoReqVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentFinanceReportApi.venueFeeInfo(vo);
    }

    @Operation(summary = "存提手续费详情")
    @PostMapping("depositWithdrawFeeInfo")
    public ResponseVO<List<AgentDepositWithdrawFeeInfoResVO>> depositWithdrawFeeInfo(@Valid @RequestBody AgentDepositWithdrawFeeInfoReqVO vo) {
        vo.setAgentId(CurrReqUtils.getOneId());
        vo.setAgentAccount(CurrReqUtils.getAccount());
        vo.setSiteCode(CurrReqUtils.getSiteCode());
        return agentFinanceReportApi.depositWithdrawFeeInfo(vo);
    }

}
