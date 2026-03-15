package com.cloud.baowang.agent.service.rebate;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.commission.front.AgentPersonDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionGroupReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.RebateBetDetailVO;
import com.cloud.baowang.agent.po.commission.AgentRebateFinalReportPO;
import com.cloud.baowang.agent.repositories.AgentRebateFinalReportRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
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
 * @createTime: 2024/10/21 14:15
 * @description:
 */
@Slf4j
@Service
@AllArgsConstructor
public class AgentRebateFinalReportService extends ServiceImpl<AgentRebateFinalReportRepository, AgentRebateFinalReportPO> {
    private AgentRebateFinalReportRepository reportRepository;

    public AgentRebateFinalReportPO getReportByAgentId(String agentId, Long endTime) {
        LambdaQueryWrapper<AgentRebateFinalReportPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AgentRebateFinalReportPO::getAgentId, agentId);
        queryWrapper.eq(AgentRebateFinalReportPO::getEndTime, endTime);

        return reportRepository.selectOne(queryWrapper);
    }

    public List<RebateBetDetailVO> getFinalReportListGroupAgentId(FrontCommissionGroupReqVO vo) {
        return reportRepository.getFinalReportListGroupAgentId(vo);
    }

    public List<AgentPersonDetailVO> getPersonAmountGroupAgentId(FrontCommissionGroupReqVO vo) {
        return reportRepository.getPersonAmountGroupAgentId(vo);
    }

    /**
     *
     * @param agentId
     * @param startTime
     * @param endTime
     * @param adjustAmount
     * @param commissionType 2: 有效流水返点 3:人头费
     */
    public void updateAfterAuditSuccess(String agentId, Long startTime, Long endTime,BigDecimal adjustAmount,String commissionType) {
        LambdaQueryWrapper<AgentRebateFinalReportPO> query = Wrappers.lambdaQuery();
        query.eq(AgentRebateFinalReportPO::getAgentId, agentId);
        query.eq(AgentRebateFinalReportPO::getStartTime, startTime);
        query.eq(AgentRebateFinalReportPO::getEndTime, endTime);
        AgentRebateFinalReportPO finalReportPO = this.baseMapper.selectOne(query);
        if (finalReportPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }
        finalReportPO.setStatus(AgentCommissionStatusEnum.RECEIVED.getCode());
        finalReportPO.setUpdatedTime(System.currentTimeMillis());
        BigDecimal ackAmount ;
        if (commissionType.equals(CommonConstant.business_two_str)){
            ackAmount = finalReportPO.getRebateAmount().add(adjustAmount);
            finalReportPO.setRebateAmount(ackAmount);
            finalReportPO.setRebateAdjustAmount(adjustAmount);
        }else if (commissionType.equals(CommonConstant.business_three_str)){
            ackAmount = finalReportPO.getNewUserAmount().add(adjustAmount);
            finalReportPO.setNewUserAmount(ackAmount);
            finalReportPO.setAdjustAmount(adjustAmount);
        }
        this.updateById(finalReportPO);
    }
}