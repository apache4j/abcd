package com.cloud.baowang.agent.service.commission;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionReportReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.CommissionDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionGroupReqVO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.repositories.AgentCommissionFinalReportRepository;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.agent.api.enums.commission.AgentCommissionStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/28 16:08
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentCommissionFinalReportService extends ServiceImpl<AgentCommissionFinalReportRepository, AgentCommissionFinalReportPO> {
    private AgentCommissionFinalReportRepository reportRepository;

    public AgentCommissionFinalReportPO getReportByAgentId(String agentId, Long endTime) {
        LambdaQueryWrapper<AgentCommissionFinalReportPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentCommissionFinalReportPO::getAgentId, agentId);
        queryWrapper.eq(AgentCommissionFinalReportPO::getEndTime, endTime);

        return reportRepository.selectOne(queryWrapper);
    }

    public List<AgentCommissionFinalReportPO> getCommissionFinalList(AgentCommissionReportReqVO reqVO) {
        return reportRepository.getCommissionFinalList(reqVO);
    }

    public AgentCommissionFinalReportPO getCommissionFinalByAgentId(AgentCommissionReportReqVO reqVO) {
        LambdaQueryWrapper<AgentCommissionFinalReportPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionFinalReportPO::getSiteCode, reqVO.getSiteCode());
        query.eq(AgentCommissionFinalReportPO::getAgentId, reqVO.getAgentId());
        query.eq(AgentCommissionFinalReportPO::getStartTime, reqVO.getStartTime());
        query.eq(AgentCommissionFinalReportPO::getEndTime, reqVO.getEndTime());
        return this.baseMapper.selectOne(query);
    }

    public AgentCommissionFinalReportPO getLatestReport(String agentId) {
        return reportRepository.getLatestReport(agentId);
    }

    public AgentCommissionFinalReportPO getReportByAgentIdAndTime(String agentId, Long endtime) {
        LambdaQueryWrapper<AgentCommissionFinalReportPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionFinalReportPO::getAgentId, agentId);
        query.eq(AgentCommissionFinalReportPO::getEndTime, endtime);
        return this.baseMapper.selectOne(query);
    }

    public  List<CommissionDetailVO> getFinalReportListGroupAgentId(FrontCommissionGroupReqVO vo) {
        return reportRepository.getFinalReportListGroupAgentId(vo);
    }

    public  List<CommissionDetailVO> getStatisticsByAgentId(FrontCommissionGroupReqVO vo) {
        return reportRepository.getStatisticsByAgentId(vo);
    }

    public AgentCommissionFinalReportPO getLatestReportByTime(FrontCommissionGroupReqVO vo) {
        return reportRepository.getLatestReportByTime(vo);
    }

    /**
     * 审核成功后 修改佣金报表这状态
     * @param agentId 总代
     * @param startTime 开始时间
     * @param endTime 结束时间
     */
    public void updateAfterAuditSuccess(String agentId, Long startTime, Long endTime,BigDecimal adjustAmount) {
        LambdaQueryWrapper<AgentCommissionFinalReportPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionFinalReportPO::getAgentId, agentId);
        query.eq(AgentCommissionFinalReportPO::getStartTime, startTime);
        query.eq(AgentCommissionFinalReportPO::getEndTime, endTime);
        AgentCommissionFinalReportPO commissionFinalReportPO = this.baseMapper.selectOne(query);
        if (commissionFinalReportPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        commissionFinalReportPO.setStatus(AgentCommissionStatusEnum.RECEIVED.getCode());
        commissionFinalReportPO.setUpdatedTime(System.currentTimeMillis());
        commissionFinalReportPO.setReviewAdjustAmount(adjustAmount);
        commissionFinalReportPO.setCommissionAmount(commissionFinalReportPO.getCommissionAmount().add(adjustAmount));
        this.updateById(commissionFinalReportPO);
    }



}
