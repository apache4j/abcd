package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionPlanApi;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanTurnoverService;
import com.cloud.baowang.common.core.vo.base.CodeValueNoI18VO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/20 15:12
 * @description:
 */
@Slf4j
@RestController
@AllArgsConstructor
public class AgentCommissionPlanApiImpl implements AgentCommissionPlanApi {
    private final AgentCommissionPlanService agentCommissionPlanService;

    private final AgentCommissionPlanTurnoverService planTurnoverService;

    @Override
    public List<CodeValueNoI18VO> getCommissionPlanSelect(String siteCode) {
        return agentCommissionPlanService.getCommissionPlanSelect(siteCode);
    }

    @Override
    public List<AgentCommissionPlanVO> getPlanBySiteAndCodes(String siteCode, List<String> planCodes) {

        return agentCommissionPlanService.getPlanBySiteAndCodes(siteCode, planCodes);
    }

    @Override
    public ResponseVO<Page<AgentCommissionPlanPageVO>> getCommissionPlanPage(CommissionPlanReqVO reqVO) {
        return ResponseVO.success(agentCommissionPlanService.getCommissionPlanPage(reqVO));
    }

    @Override
    public ResponseVO<Void> addPlanInfo(AgentCommissionPlanAddVO addVO) {
        return agentCommissionPlanService.addPlanInfo(addVO);
    }

    @Override
    public ResponseVO<AgentCommissionPlanInfoVO> getPlanInfo(IdVO idVO) {
        return ResponseVO.success(agentCommissionPlanService.getPlanInfo(idVO));
    }

    @Override
    public AgentCommissionPlanInfoVO getPlanInfoByPlanCode(String planCode) {
        return agentCommissionPlanService.getPlanInfoByPlanCode(planCode);
    }

    @Override
    public AgentCommissionPlanInfoVO getPlanInfoByAgentId(String agentId) {
        return agentCommissionPlanService.getPlanInfoByAgentId(agentId);
    }

    @Override
    public ResponseVO<CommissionPlanAgentVO> getAgentByPlan(CommissionAgentReqVO reqVO) {
        return ResponseVO.success(agentCommissionPlanService.getAgentByPlan(reqVO));
    }

    @Override
    public ResponseVO removePlanInfo(IdVO idVO) {
        return agentCommissionPlanService.removePlanInfo(idVO);
    }

    @Override
    public ResponseVO editPlanInfo(AgentCommissionPlanInfoVO editInfo) {
        agentCommissionPlanService.editPlanInfo(editInfo);
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<List<AgentCommissionPlanVO>> listAllCommissionPlan(String siteCode) {
        return ResponseVO.success(agentCommissionPlanService.listAllCommissionPlan(siteCode));
    }

    @Override
    public ResponseVO<Page<CommissionPlanTurnoverPageListVO>> planTurnoverPageList(CommissionPlanTurnoverPageQueryVO reqVO) {
        return planTurnoverService.planTurnoverPageList(reqVO);
    }

    @Override
    public ResponseVO<CommissionPlanTurnoverDetailVO> planTurnoverDetail(String siteCode, String planCode) {
        return planTurnoverService.planTurnoverDetail(siteCode, planCode);
    }

    @Override
    public ResponseVO<Void> addPlanTurnover(CommissionPlanTurnoverAddVO addVO) {
        return planTurnoverService.addPlanTurnover(addVO);
    }

    @Override
    public ResponseVO<Void> editPlanTurnover(CommissionPlanTurnoverUpdateVO updateVO) {
        return planTurnoverService.editPlanTurnover(updateVO);
    }

    @Override
    public ResponseVO<Void> removePlanTurnover(String id) {
        return planTurnoverService.removePlanTurnover(id);
    }

}
