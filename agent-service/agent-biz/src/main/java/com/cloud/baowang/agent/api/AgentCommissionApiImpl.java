package com.cloud.baowang.agent.api;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.api.vo.commission.front.AgentCommissionExplainVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionDetailReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionReportReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionReportResVO;
import com.cloud.baowang.agent.api.vo.commission.front.SubCommissionGeneralVO;
import com.cloud.baowang.agent.service.AgentCommissionService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.commission.AgentCommissionReviewRecordService;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.service.commission.AgentCommissionVenueService;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/06/20 10:16
 * @description:
 */
@Slf4j
@RestController
@Validated
@AllArgsConstructor
public class AgentCommissionApiImpl implements AgentCommissionApi {
    private final AgentCommissionService agentCommissionService;
    private final AgentInfoService agentInfoService;
    private final AgentCommissionReviewRecordService agentCommissionReviewRecordService;

    private final AgentCommissionVenueService agentCommissionVenueService;

    @Override
    public BigDecimal commissionImitate(CommissionImitateCalcVO commissionImitateCalcVO) {
        return agentCommissionService.commissionImitate(commissionImitateCalcVO);
    }

    @Override
    public ResponseVO<AgentCommissionExplainVO> getCommissionExplain(String agentId) {
        return ResponseVO.success(agentCommissionService.getCommissionExplain(agentId));
    }


    @Override
    public ResponseVO<RateDetailVO> getRateDetail(String agentId) {
        return ResponseVO.success(agentCommissionService.getRateDetail(agentId));
    }

    @Override
    public AgentActiveUserResponseVO getAgentActiveUserInfo(AgentActiveNumberReqVO reqVO) {
        return agentCommissionService.getAgentActiveUserInfo(reqVO);
    }

    @Override
    public List<AgentActiveUserResponseVO> getAgentActiveUserInfoList(AgentActiveNumberReqVO reqVO) {
        return agentCommissionService.getAgentActiveUserInfoList(reqVO);
    }

    /**
     * 客户端佣金报表
     */
    @Override
    public ResponseVO<FrontCommissionReportResVO> getCommissionReport(FrontCommissionReportReqVO reqVO) {
   return ResponseVO.success(agentCommissionService.getClientCommissionReport(reqVO));
    }

    @Override
    public AgentCommissionDate getAgentCommissionDate(String agentId) {
        return agentCommissionService.getAgentCommissionDate(agentId);
    }

    @Override
    public AgentCommissionDate getDayAgentCommissionDate(String agentId) {
        return agentCommissionService.getAgentCommissionDate(agentId);
    }

    @Override
    public ResponseVO<Page<SubCommissionGeneralVO>> getReportDetail(FrontCommissionDetailReqVO reqVO) {
        return ResponseVO.success(agentCommissionService.getReportDetail(reqVO));
    }

    @Override
    public Boolean userActiveValidate(String userId) {
        return agentCommissionService.userActiveValidate(userId);
    }

    @Override
    public ResponseVO<AgentCommissionPlanVO> getCurrentCommissionPlain(String agentId) {
        return agentCommissionService.getCurrentCommissionPlain(agentId);
    }

    @Override
    public ResponseVO<List<AgentCommissionReportVO>> AgentCommissionReportQueryVO(AgentCommissionReportQueryVO reqVO) {
        return ResponseVO.success(agentCommissionVenueService.getSelfCommissionReport(reqVO));
    }

    @Override
    public ResponseVO<List<AgentCommissionReportVO>> getTeamCommissionReport(AgentCommissionReportQueryVO reqVO) {
        return ResponseVO.success(agentCommissionVenueService.getTeamCommissionReport(reqVO));
    }

    @Override
    public ResponseVO<Page<AgentChildNodesCommissionVO>> getSubAgentCommission(AgentCommissionPageQueryVO reqVO) {
        return ResponseVO.success(agentCommissionVenueService.getSubAgentCommission(reqVO));
    }

    @Override
    public ResponseVO<List<AgentCommissionReportVO>> subAgentCommissionDetail(AgentCommissionReportQueryVO reqVO) {
        return ResponseVO.success(agentCommissionVenueService.subAgentCommissionDetail(reqVO));
    }
}
