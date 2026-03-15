package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceCurrencyResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentTeamFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoResVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@FeignClient(contextId = "remoteAgentFinanceReportApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - AgentFinanceReport")
public interface AgentFinanceReportApi {

    String PREFIX = ApiConstants.PREFIX + "/agent-finance-report/api";

    @PostMapping(PREFIX + "/getAgentFinanceInfo")
    @Operation(summary = "代理财务报表-个人财务")
    ResponseVO<AgentFinanceResVO> financeReport(@RequestBody AgentFinanceReqVO vo);

    @PostMapping(PREFIX + "/financeReportTeamFinance")
    @Operation(summary = "团队财务-明细")
    ResponseVO<Page<AgentFinanceCurrencyResVO>> financeReportTeamFinance(@RequestBody AgentTeamFinanceReqVO vo);

    @PostMapping(PREFIX + "/venueFeeInfo")
    @Operation(summary = "场馆费详情")
    ResponseVO<List<AgentVenueFeeInfoResVO>> venueFeeInfo(@RequestBody AgentVenueFeeInfoReqVO vo);

    @PostMapping(PREFIX + "/depositWithdrawFeeInfo")
    @Operation(summary = "存提手续费详情")
    ResponseVO<List<AgentDepositWithdrawFeeInfoResVO>> depositWithdrawFeeInfo(@RequestBody AgentDepositWithdrawFeeInfoReqVO vo);

}
