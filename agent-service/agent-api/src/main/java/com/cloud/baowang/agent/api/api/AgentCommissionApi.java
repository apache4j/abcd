package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.api.vo.commission.front.*;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/20 10:11
 * @description:
 */

@FeignClient(contextId = "remoteAgentCommissionApi",value = ApiConstants.NAME)
@Tag(name = "RPC 服务 - 佣金相关 ")
public interface AgentCommissionApi {
    String PREFIX = ApiConstants.PREFIX+"/commission/api";

    @Operation(description = "佣金模拟器")
    @PostMapping(value = PREFIX + "/commissionImitate")
    BigDecimal commissionImitate(@RequestBody CommissionImitateCalcVO commissionImitateCalcVO);

    @Operation(description = "佣金说明")
    @PostMapping(value = PREFIX + "/getCommissionExplain")
    ResponseVO<AgentCommissionExplainVO> getCommissionExplain(@RequestParam("agentId") String agentId);

    @Operation(description = "获取佣金比例")
    @PostMapping(value = PREFIX + "/getRateDetail")
    ResponseVO<RateDetailVO> getRateDetail(@RequestParam("agentId") String agentId);

    @Operation(description = "获取有效活跃和有效新增人数")
    @PostMapping(value = PREFIX + "/getAgentActiveUserInfo")
    AgentActiveUserResponseVO getAgentActiveUserInfo(@RequestBody AgentActiveNumberReqVO reqVO);

    @Operation(description = "获取有效活跃和有效新增人数")
    @PostMapping(value = PREFIX + "/getAgentActiveUserInfoList")
    List<AgentActiveUserResponseVO> getAgentActiveUserInfoList(@RequestBody AgentActiveNumberReqVO reqVO);

    @Operation(summary = "佣金报表")
    @PostMapping(value = PREFIX +"/getReport")
    ResponseVO<FrontCommissionReportResVO> getCommissionReport(@RequestBody FrontCommissionReportReqVO reqVO);

    @Operation(summary = "获取佣金结算时间范围")
    @PostMapping(value = PREFIX +"/getAgentCommissionDate")
    AgentCommissionDate getAgentCommissionDate(@RequestParam("agentId") String agentId);

    @Operation(summary = "获取佣金结算时间范围每日有效流水")
    @PostMapping(value = PREFIX +"/getDayAgentCommissionDate")
    AgentCommissionDate getDayAgentCommissionDate(@RequestParam("agentId") String agentId);

    @Operation(summary = "佣金报表-佣金明细")
    @PostMapping(value =PREFIX + "/getReportDetail")
    ResponseVO<Page<SubCommissionGeneralVO>> getReportDetail(@RequestBody FrontCommissionDetailReqVO reqVO);

    @Operation(summary = "判断会员当期是否是有效活跃会员")
    @PostMapping(value = PREFIX +"/userActiveValidate")
    Boolean userActiveValidate(@RequestParam("userId") String userId);

    @Operation(summary = "获取当前用户佣金")
    @PostMapping(value = PREFIX + "/getCurrentCommissionPlain")
    ResponseVO<AgentCommissionPlanVO> getCurrentCommissionPlain(@RequestParam("agentId")String agentId);

    @Operation(summary = "直属会员佣金")
    @PostMapping(value = PREFIX + "/AgentCommissionReportQueryVO")
    ResponseVO<List<AgentCommissionReportVO>> AgentCommissionReportQueryVO(AgentCommissionReportQueryVO reqVO);

    @Operation(summary = "下级代理佣金")
    @PostMapping(value = PREFIX + "/getTeamCommissionReport")
    ResponseVO<List<AgentCommissionReportVO>> getTeamCommissionReport(AgentCommissionReportQueryVO reqVO);

    @Operation(summary = "下级代理佣金-列表")
    @PostMapping(value = PREFIX + "/getSubAgentCommission")
    ResponseVO<Page<AgentChildNodesCommissionVO>> getSubAgentCommission(AgentCommissionPageQueryVO reqVO);

    @Operation(summary = "下级代理佣金-详情")
    @PostMapping(value = PREFIX + "/subAgentCommissionDetail")
    ResponseVO<List<AgentCommissionReportVO>> subAgentCommissionDetail(AgentCommissionReportQueryVO reqVO);
}
