package com.cloud.baowang.agent.api.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.enums.ApiConstants;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 15:09
 * @description:
 */
@FeignClient(contextId = "remoteAgentCommissionPlanApi", value = ApiConstants.NAME)
@Tag(name = "佣金方案服务 -AgentCommissionPlanApi")
public interface AgentCommissionPlanApi {

    String PREFIX = ApiConstants.PREFIX + "/commission_plan/api/";

    @Operation(summary = "佣金方案下拉框")
    @PostMapping(PREFIX + "getCommissionPlanSelect")
    List<CodeValueNoI18VO> getCommissionPlanSelect(@RequestParam("siteCode") String siteCode);

    /**
     *
     * @param siteCode
     * @param planCodes
     * @return
     */
    @Operation(summary = "根据站点code+方案code,批量获取对应佣金方案")
    @PostMapping(PREFIX + "getPlanBySiteAndCodes")
    List<AgentCommissionPlanVO> getPlanBySiteAndCodes(@RequestParam("siteCode") String siteCode, @RequestBody List<String> planCodes);

    @Operation(summary = "佣金方案分页查询-负盈利")
    @PostMapping(PREFIX + "getCommissionPlanPage")
    ResponseVO<Page<AgentCommissionPlanPageVO>> getCommissionPlanPage(@RequestBody CommissionPlanReqVO reqVO);

    @Operation(summary = "新增佣金方案-负盈利")
    @PostMapping(PREFIX + "addPlanInfo")
    ResponseVO<Void> addPlanInfo(@RequestBody AgentCommissionPlanAddVO addVO);

    @Operation(summary = "查看佣金方案-负盈利")
    @PostMapping(PREFIX + "getPlanInfo")
    ResponseVO<AgentCommissionPlanInfoVO> getPlanInfo(@RequestBody IdVO idVO);

    @Operation(summary = "根据planCode查看佣金方案")
    @PostMapping(PREFIX + "getPlanInfoByPlanCode")
    AgentCommissionPlanInfoVO getPlanInfoByPlanCode(@RequestParam("planCode") String planCode);

    @Operation(summary = "根据agentId查看佣金方案")
    @PostMapping(PREFIX + "getPlanInfoByAgentId")
    AgentCommissionPlanInfoVO getPlanInfoByAgentId(@RequestParam("agentId") String agentId);

    @Operation(summary = "查看代理人数")
    @PostMapping(PREFIX + "getAgentByPlan")
    ResponseVO<CommissionPlanAgentVO> getAgentByPlan(@RequestBody CommissionAgentReqVO reqVO);

    @Operation(summary = "删除方案")
    @PostMapping(PREFIX + "removePlanInfo")
    ResponseVO removePlanInfo(@RequestBody IdVO idVO);

    @Operation(summary = "编辑方案")
    @PostMapping(PREFIX + "editPlanInfo")
    ResponseVO editPlanInfo(@RequestBody AgentCommissionPlanInfoVO editInfo);

    @Operation(summary = "佣金方案分页查询")
    @PostMapping(PREFIX + "listAllCommissionPlan")
    ResponseVO<List<AgentCommissionPlanVO>> listAllCommissionPlan(@RequestParam("siteCode") String siteCode);


    @Operation(summary = "佣金方案分页查询-有效流水")
    @PostMapping(PREFIX + "planTurnoverPageList")
    ResponseVO<Page<CommissionPlanTurnoverPageListVO>> planTurnoverPageList(@RequestBody CommissionPlanTurnoverPageQueryVO reqVO);

    @Operation(summary = "佣金方案配置详情-有效流水")
    @PostMapping(PREFIX + "planTurnoverDetail")
    ResponseVO<CommissionPlanTurnoverDetailVO> planTurnoverDetail(@RequestParam("siteCode") String siteCode, @RequestParam("planCode") String planCode);

    @Operation(summary = "新增佣金方案-有效流水")
    @PostMapping(PREFIX + "addPlanTurnover")
    ResponseVO<Void> addPlanTurnover(@RequestBody CommissionPlanTurnoverAddVO addVO);

    @Operation(summary = "编辑方案-有效流水")
    @PostMapping(PREFIX + "editPlanTurnover")
    ResponseVO<Void> editPlanTurnover(@RequestBody CommissionPlanTurnoverUpdateVO updateVO);

    @Operation(summary = "删除方案-有效流水")
    @PostMapping(PREFIX + "removePlanTurnover")
    ResponseVO<Void> removePlanTurnover(@RequestParam("id") String id);

}
