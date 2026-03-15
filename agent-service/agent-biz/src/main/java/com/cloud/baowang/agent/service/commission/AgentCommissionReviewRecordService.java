package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCommissionInfoVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import com.cloud.baowang.agent.repositories.AgentCommissionReviewRepository;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.commission.CommissionReviewOrderStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/10/24 22:06
 * @description: 佣金审核记录
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentCommissionReviewRecordService extends ServiceImpl<AgentCommissionReviewRepository, AgentCommissionReviewRecordPO> {

    private final AgentCommissionReviewRepository reviewRepository;
    private final AgentInfoService agentInfoService;
    private final AgentCommissionReviewService agentCommissionReviewService;

    public Page<AgentCommissionReviewRecordVO> getReviewRecordPage(CommissionReviewReq reviewReq) {
        String adminName = reviewReq.getAdminName();
        Page<AgentCommissionReviewRecordVO> reportVOPage = new Page<>();

        Page<AgentCommissionReviewRecordVO> page = new Page<>(reviewReq.getPageNumber(), reviewReq.getPageSize());
        List<Integer> statusList = Arrays.asList(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode(),
                CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode(),
                CommissionReviewOrderStatusEnum.SECOND_REVIEW_RETURNED.getCode(),
                CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode());
        reviewReq.setOrderStatusList(statusList);
        reportVOPage = reviewRepository.getCommissionReviewRecordPage(page, reviewReq, adminName, 1);

        if (reportVOPage != null && !reportVOPage.getRecords().isEmpty()) {
            List<String> agentIds = reportVOPage.getRecords().stream().map(AgentCommissionReviewRecordVO::getAgentId).toList();
            List<AgentInfoVO> agentInfoVOList = agentInfoService.getByAgentIds(agentIds);
            Map<String, AgentInfoVO> agentMap = agentInfoVOList.stream().collect(Collectors.toMap(AgentInfoVO::getAgentId, Function.identity(), (u1, u2) -> u1));
            for (AgentCommissionReviewRecordVO vo : reportVOPage.getRecords()) {
                // 账户状态
                AgentInfoVO agentInfoVO = agentMap.get(vo.getAgentId());
                vo.setAgentStatus(agentInfoVO.getStatus());

                //操作文本
                vo.setReviewOperation(CommonConstant.business_ten);
                if ( null != vo.getOneReviewStartTime() && null != vo.getOneReviewFinishTime()){
                    vo.setFirstHandleTime( (vo.getOneReviewFinishTime()-vo.getOneReviewStartTime())/1000);
                }
                if ( null != vo.getSecondReviewFinishTime() && null != vo.getSecondReviewStartTime()){
                    vo.setSecondHandleTime( (vo.getSecondReviewFinishTime()-vo.getSecondReviewStartTime())/1000);
                }
                vo.setTimeUnit("秒");
            }
        }

        return reportVOPage;
    }

    public AgentCommissionReviewDetailVO getAgentCommissionRecordDetail(IdVO idVO) {
       return agentCommissionReviewService.getAgentCommissionDetail(idVO);
    }

    public Long getReviewRecordPageCount(CommissionReviewReq reviewReq) {
        Page<AgentCommissionReviewRecordVO> page = new Page<>(reviewReq.getPageNumber(), reviewReq.getPageSize());
        List<Integer> statusList = Arrays.asList(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode(),
                CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode(),CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode());
        reviewReq.setOrderStatusList(statusList);
        Integer count = reviewRepository.getCommissionReviewCount(reviewReq);
        return count.longValue();

    }

    public ResponseVO<BigDecimal> calculateAgentCommission(CommissionReviewCalculateReq commissionReviewCalculateReq) {
        return agentCommissionReviewService.calculateAgentCommission(commissionReviewCalculateReq);
    }

//    public List<AgentCommissionReviewRecordPO> getReviewRecordList(FrontCommissionReportReqVO req) {
//        List<Integer> statusList = Arrays.asList(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode(),
//                CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode(),
//                CommissionReviewOrderStatusEnum.ONE_REVIEWING.getCode());
//        LambdaQueryWrapper<AgentCommissionReviewRecordPO> query = Wrappers.lambdaQuery();
//        query.in(AgentCommissionReviewRecordPO::getOrderStatus, statusList);
//        query.eq(AgentCommissionReviewRecordPO::getSiteCode, req.getSiteCode());
//        query.eq(AgentCommissionReviewRecordPO::getAgentId, req.getAgentId());
//        query.eq(AgentCommissionReviewRecordPO::getCommissionType, req.getCommissionType());
//        query.ge(AgentCommissionReviewRecordPO::getOneReviewFinishTime, req.getStartTime());
//        query.le(AgentCommissionReviewRecordPO::getOneReviewFinishTime, req.getEndTime());
//        return this.baseMapper.selectList(query);
//    }

    public List<AgentCommissionReviewRecordPO> getReviewRecordByReportId(ReviewRecordReqVO reviewRecordReqVO) {
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> query = Wrappers.lambdaQuery();
        query.in(ObjectUtil.isNotEmpty(reviewRecordReqVO.getOrderStatusList()), AgentCommissionReviewRecordPO::getOrderStatus, reviewRecordReqVO.getOrderStatusList());
        query.eq(ObjectUtil.isNotEmpty(reviewRecordReqVO.getSiteCode()), AgentCommissionReviewRecordPO::getSiteCode, reviewRecordReqVO.getSiteCode());
        query.eq(ObjectUtil.isNotEmpty(reviewRecordReqVO.getAgentId()), AgentCommissionReviewRecordPO::getAgentId, reviewRecordReqVO.getAgentId());
        query.in(ObjectUtil.isNotEmpty(reviewRecordReqVO.getCommissionTypeList()), AgentCommissionReviewRecordPO::getCommissionType, reviewRecordReqVO.getCommissionTypeList());
        query.eq(ObjectUtil.isNotEmpty(reviewRecordReqVO.getOrderNo()), AgentCommissionReviewRecordPO::getOrderNo, reviewRecordReqVO.getOrderNo());
        query.eq(ObjectUtil.isNotEmpty(reviewRecordReqVO.getStartTime()), AgentCommissionReviewRecordPO::getStartTime, reviewRecordReqVO.getStartTime());
        query.eq(ObjectUtil.isNotEmpty(reviewRecordReqVO.getEndTime()), AgentCommissionReviewRecordPO::getEndTime, reviewRecordReqVO.getEndTime());

        return this.baseMapper.selectList(query);
    }

    public AgentCommissionInfoVO sumCommissionPayment(String agentId) {
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> lqw = new LambdaQueryWrapper<>();
        lqw.eq(AgentCommissionReviewRecordPO::getAgentId,agentId);
        List<Integer> statusList = new ArrayList<>();
        statusList.add(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode());
        statusList.add(CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode());
        statusList.add(CommissionReviewOrderStatusEnum.FIRST_REVIEW_APPROVED.getCode());
        statusList.add(CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode());
        statusList.add(CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode());
        lqw.in(AgentCommissionReviewRecordPO::getOrderStatus, statusList);
        List<AgentCommissionReviewRecordPO> commissionReviewRecordPOS = reviewRepository.selectList(lqw);
        AgentCommissionInfoVO agentCommissionInfoVO = new AgentCommissionInfoVO();
        if(CollectionUtil.isEmpty(commissionReviewRecordPOS)){
            agentCommissionInfoVO.setTotalReceivableCommission(BigDecimal.ZERO);
            agentCommissionInfoVO.setTotalNegativeProfitCommission(BigDecimal.ZERO);
            agentCommissionInfoVO.setTotalEffectiveTurnoverCommission(BigDecimal.ZERO);
            agentCommissionInfoVO.setTotalCapitationFeeCommission(BigDecimal.ZERO);
        }else{
            BigDecimal totalReceivableCommission = commissionReviewRecordPOS.stream().map(AgentCommissionReviewRecordPO::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalNegativeProfitCommission = commissionReviewRecordPOS.stream().filter(obj -> CommissionTypeEnum.NEGATIVE.getCode().equals(obj.getCommissionType())).map(AgentCommissionReviewRecordPO::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalEffectiveTurnoverCommission = commissionReviewRecordPOS.stream().filter(obj -> CommissionTypeEnum.REBATE.getCode().equals(obj.getCommissionType())).map(AgentCommissionReviewRecordPO::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalCapitationFeeCommission = commissionReviewRecordPOS.stream().filter(obj -> CommissionTypeEnum.ADDING.getCode().equals(obj.getCommissionType())).map(AgentCommissionReviewRecordPO::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            agentCommissionInfoVO.setTotalReceivableCommission(totalReceivableCommission);
            agentCommissionInfoVO.setTotalNegativeProfitCommission(totalNegativeProfitCommission);
            agentCommissionInfoVO.setTotalEffectiveTurnoverCommission(totalEffectiveTurnoverCommission);
            agentCommissionInfoVO.setTotalCapitationFeeCommission(totalCapitationFeeCommission);
        }

        return agentCommissionInfoVO;
    }
}

