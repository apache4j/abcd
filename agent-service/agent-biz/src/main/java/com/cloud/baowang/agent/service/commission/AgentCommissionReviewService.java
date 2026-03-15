package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.agent.api.enums.AgentCoinBalanceTypeEnum;
import com.cloud.baowang.agent.api.enums.AgentCoinRecordTypeEnum;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionReviewOrderStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.agent.api.vo.AdjustCommissionVO;
import com.cloud.baowang.agent.api.vo.AgentReviewOrderNumVO;
import com.cloud.baowang.agent.api.vo.StatusVO;
import com.cloud.baowang.agent.api.vo.agentCoin.AgentCoinAddVO;
import com.cloud.baowang.agent.api.vo.agentLogin.AgentLoginRecordPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.ReviewListVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.api.vo.label.AgentLabelVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.*;
import com.cloud.baowang.agent.repositories.AgentCommissionGrantRecordRepository;
import com.cloud.baowang.agent.repositories.AgentCommissionReviewRepository;
import com.cloud.baowang.agent.repositories.AgentCommissionVenueRepository;
import com.cloud.baowang.agent.service.AgentCommonCoinService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.AgentLabelService;
import com.cloud.baowang.agent.service.AgentLoginRecordService;
import com.cloud.baowang.agent.service.rebate.AgentRebateFinalReportService;
import com.cloud.baowang.agent.service.rebate.AgentRebateReportDetailService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.vo.StatusListVO;
import com.cloud.baowang.common.core.vo.base.CodeValueVO;
import com.cloud.baowang.common.core.vo.base.IdVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.data.transfer.i18n.util.I18nMessageUtil;
import com.cloud.baowang.system.api.api.SystemParamApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/10/24 22:06
 * @description: 佣金发放审核
 */
@Service
@Slf4j
@AllArgsConstructor
public class AgentCommissionReviewService extends ServiceImpl<AgentCommissionReviewRepository, AgentCommissionReviewRecordPO> {

    private final AgentCommissionReviewRepository reviewRepository;
    private final AgentInfoService agentInfoService;
    private final AgentCommissionFinalReportService finalReportService;
    private final AgentLoginRecordService agentLoginRecordService;
    private final AgentLabelService agentLabelService;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final SystemParamApi systemParamApi;
    private final AgentRebateFinalReportService agentRebateFinalReportService;
    private final AgentRebateReportDetailService agentRebateReportDetailService;
    //    private final AgentCommissionCoinService agentCommissionCoinService;
    private final AgentCommissionGrantRecordRepository grantRecordRepository;

    private final AgentCommonCoinService agentCommonCoinService;

    private final AgentCommissionVenueRepository commissionVenueRepository;

    public Page<AgentCommissionReviewVO> getReviewPage(CommissionReviewReq reviewReq) {
        String adminName = reviewReq.getAdminName();
        Page<AgentCommissionReviewVO> reportVOPage = new Page<>();

        Page<AgentCommissionReviewVO> page = new Page<>(reviewReq.getPageNumber(), reviewReq.getPageSize());
        List<Integer> statusList = new ArrayList<>();
        if (reviewReq.getReview() == 0) {
            //待一审
            statusList = Arrays.asList(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode(),
                    CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode());
        } else if (reviewReq.getReview() == 1) {
            //待二审
            statusList = Arrays.asList(CommissionReviewOrderStatusEnum.FIRST_REVIEW_APPROVED.getCode(),
                    CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode(), CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode());
        } else {
            return reportVOPage;
        }
        reviewReq.setOrderStatusList(statusList);
        reportVOPage = reviewRepository.getCommissionReviewPage(page, reviewReq, adminName, 0);


        if (reportVOPage != null && !reportVOPage.getRecords().isEmpty()) {
            List<String> agentIds = reportVOPage.getRecords().stream().map(AgentCommissionReviewVO::getAgentId).toList();
            List<AgentInfoVO> agentInfoVOList = agentInfoService.getByAgentIds(agentIds);
            Map<String, AgentInfoVO> agentMap = agentInfoVOList.stream().collect(Collectors.toMap(AgentInfoVO::getAgentId, Function.identity(), (u1, u2) -> u1));
            for (AgentCommissionReviewVO vo : reportVOPage.getRecords()) {
                // 账户状态
                AgentInfoVO agentInfoVO = agentMap.get(vo.getAgentId());
                if (agentInfoVO != null) {
                    vo.setAgentStatus(agentInfoVO.getStatus());
                } else {
                    log.info("根据agentId:{}查询不到代理信息", vo.getAgentId());
                }
                // 锁单人是否当前登录人 0否 1是
                // 前端先判断locker，再判断isLocker
                if (StrUtil.isNotEmpty(vo.getLocker())) {
                    if (vo.getLocker().equals(adminName)) {
                        vo.setIsLocker(CommonConstant.business_one);
                    } else {
                        vo.setIsLocker(CommonConstant.business_zero);
                    }
                }
                //操作文本
                //Integer reviewOperation = CommissionReviewOperationEnum.getCodeByOrderStatus(vo.getOrderStatus());
                vo.setReviewOperation(vo.getOrderStatus());
            }

        }

        return reportVOPage;
    }

    public ResponseVO<?> lockManualUp(StatusVO vo) {
        String adminName = vo.getOperatorName();
        // 获取参数
        String id = vo.getId();
        AgentCommissionReviewRecordPO upReview = this.getById(id);
        if (null == upReview) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        try {
            // 业务操作
            return lockOperate(vo, upReview, adminName);
        } catch (Exception e) {
            log.error("佣金审核-锁单/解锁error,审核单号:{},操作人:{}", upReview.getOrderNo(), adminName, e);
            return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
    }

    private ResponseVO<?> lockOperate(StatusVO vo, AgentCommissionReviewRecordPO upReview, String adminName) {
        Integer myLockStatus;
        Integer myOrderStatus;
        String locker;
        Long oneReviewStartTime;

        // 锁单状态 0未锁 1已锁
        if (CommonConstant.business_one.equals(vo.getStatus())) {
            // 开始锁单
            if (CommonConstant.business_one.equals(upReview.getLockStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }
            // 判断订单状态
            if (!CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode().equals(upReview.getOrderStatus())
                    && !CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode().equals(upReview.getOrderStatus())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }

            myLockStatus = CommonConstant.business_one;
            myOrderStatus = CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode();
            locker = adminName;
            oneReviewStartTime = System.currentTimeMillis();
        } else {
            log.info("{}:locker:{}, admin:{}", vo.getId(), upReview.getLocker(), adminName);
            if (CommonConstant.business_zero.equals(upReview.getLockStatus())) {
                return ResponseVO.success();
            }
            //判断锁定人和解锁人是不是同一个
            if (upReview.getLocker() != null && !adminName.equals(upReview.getLocker())) {
                return ResponseVO.fail(ResultCode.USER_REVIEW_LOCK_ERROR);
            }


            // 开始解锁
            myLockStatus = CommonConstant.business_zero;
            myOrderStatus = CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode();
            locker = "";
            oneReviewStartTime = null;
        }

        LambdaUpdateWrapper<AgentCommissionReviewRecordPO> lambdaUpdate = new LambdaUpdateWrapper<>();
        lambdaUpdate.eq(AgentCommissionReviewRecordPO::getId, vo.getId())
                .set(AgentCommissionReviewRecordPO::getLockStatus, myLockStatus)
                .set(AgentCommissionReviewRecordPO::getLocker, locker)
                .set(AgentCommissionReviewRecordPO::getOrderStatus, myOrderStatus)
                .set(AgentCommissionReviewRecordPO::getOneReviewStartTime, oneReviewStartTime)
                .set(AgentCommissionReviewRecordPO::getUpdater, adminName)
                .set(AgentCommissionReviewRecordPO::getUpdatedTime, System.currentTimeMillis());
        this.update(null, lambdaUpdate);
        return ResponseVO.success();
    }

    public AgentCommissionReviewDetailVO getAgentCommissionDetail(IdVO idVO) {
        AgentCommissionReviewRecordPO reviewRecordPO = this.getById(idVO.getId());
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reviewRecordPO.getAgentId());

        String timeZone = CurrReqUtils.getTimezone();

        AgentCommissionReviewDetailVO detailVO = new AgentCommissionReviewDetailVO();
        //代理基本信息
        AgentBaseInfoVO agentBaseInfoVO = new AgentBaseInfoVO();
        agentBaseInfoVO.setAgentAccount(agentInfoPO.getAgentAccount());
        agentBaseInfoVO.setAgentType(agentInfoPO.getAgentType());
        agentBaseInfoVO.setAgentCategory(agentInfoPO.getAgentCategory());
        agentBaseInfoVO.setRegisterTime(agentInfoPO.getRegisterTime());
        agentBaseInfoVO.setCurrency(CurrReqUtils.getPlatCurrencyCode());

        String labelIds = agentInfoPO.getAgentLabelId();
        String labelName = "";
        if (labelIds != null) {
            List<AgentLabelVO> labelVOS = agentLabelService.getListByIds(Arrays.stream(labelIds.split(",")).toList());
            List<String> names = labelVOS.stream().map(AgentLabelVO::getName).toList();
            labelName = String.join(",", names);
        }
        agentBaseInfoVO.setAgentLabelName(labelName);
        agentBaseInfoVO.setRegisterIp(agentInfoPO.getRegisterIp());

        AgentLoginRecordPageVO loginRecordPageVO = agentLoginRecordService.getLatestLoginRecord(agentInfoPO.getAgentId());
        if (ObjUtil.isNotNull(loginRecordPageVO)) {
            agentBaseInfoVO.setLastLoginIp(loginRecordPageVO.getLoginIp());
            agentBaseInfoVO.setLastLoginTime(loginRecordPageVO.getLoginTime());
        }

        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());

        agentBaseInfoVO.setPlanId(planVO.getId());
        agentBaseInfoVO.setPlanName(planVO.getPlanName());

        ResponseVO<List<CodeValueVO>> listResponseVO = systemParamApi.getSystemParamByType(CommonConstant.AGENT_USER_BENEFIT);
        List<CodeValueVO> benefitList = listResponseVO.getData();
        Map<String, String> map = benefitList.stream().collect(Collectors.toMap(CodeValueVO::getCode, CodeValueVO::getValue, (k1, k2) -> k2));
        agentBaseInfoVO.setUserBenefit(agentInfoPO.getUserBenefit());
        String[] benefitArr = agentInfoPO.getUserBenefit().split(",");
        List<String> benefitText = new ArrayList<>();
        for (String benefit : benefitArr) {
            String message = I18nMessageUtil.getI18NMessage(map.get(benefit));
            benefitText.add(message);
        }

        agentBaseInfoVO.setUserBenefitText(String.join(",", benefitText));
        BigDecimal commissionAmount = reviewRepository.getTotalCommissionByAgentId(agentInfoPO.getAgentId());
        commissionAmount = commissionAmount == null ? new BigDecimal("0.0000") : commissionAmount;
        agentBaseInfoVO.setCommissionTotal(commissionAmount);

        detailVO.setAgentBaseInfoVO(agentBaseInfoVO);

        //佣金账单信息
        if (CommissionTypeEnum.NEGATIVE.getCode().equals(reviewRecordPO.getCommissionType())) {
            AgentCommissionFinalReportPO finalReportPO = finalReportService.getById(reviewRecordPO.getReportId());
            CommissionBillVO commissionBillVO = new CommissionBillVO();
            String issueDay = DateUtil.format(new Date(reviewRecordPO.getEndTime()), "yyyy-MM-dd");
            commissionBillVO.setIssue(issueDay);
            commissionBillVO.setCommissionType(reviewRecordPO.getCommissionType());
            commissionBillVO.setSettleCycle(reviewRecordPO.getSettleCycle());
            commissionBillVO.setStartTime(reviewRecordPO.getStartTime());
            commissionBillVO.setEndTime(reviewRecordPO.getEndTime());
            commissionBillVO.setCurrency(reviewRecordPO.getCurrency());
            if (finalReportPO != null) {
                commissionBillVO.setActiveValidNumber(finalReportPO.getActiveNumber());
                commissionBillVO.setNewActiveNumber(finalReportPO.getNewValidNumber());
                commissionBillVO.setWinLossTotal(finalReportPO.getUserWinLossTotal());
                commissionBillVO.setValidBetAmount(finalReportPO.getValidBetAmount());
                commissionBillVO.setTransferAmount(finalReportPO.getTransferAmount());
                commissionBillVO.setVenueFee(finalReportPO.getVenueFee());
                commissionBillVO.setAccessFee(finalReportPO.getAccessFee());
                commissionBillVO.setLastMonthRemain(finalReportPO.getLastMonthRemain());
                commissionBillVO.setNetWinLoss(finalReportPO.getNetWinLoss());
                commissionBillVO.setAgentRate(finalReportPO.getAgentRate().toString());
                commissionBillVO.setApplyAmount(reviewRecordPO.getApplyAmount());
                commissionBillVO.setAdjustCommissionAmount(reviewRecordPO.getAdjustCommissionAmount());
                commissionBillVO.setCommissionAmount(reviewRecordPO.getCommissionAmount());
                commissionBillVO.setAdjustCommissionRemark(reviewRecordPO.getAdjustCommissionRemark());

            }
            detailVO.setCommissionBillVO(commissionBillVO);
        } else if (CommissionTypeEnum.REBATE.getCode().equals(reviewRecordPO.getCommissionType())) {
//            AgentRebateFinalReportPO rebateFinalReportPO = agentRebateFinalReportService.getById(reviewRecordPO.getReportId());
            RebateCommissionBillVO rebateCommissionBillVO = new RebateCommissionBillVO();
            String issueDay = DateUtil.format(new Date(reviewRecordPO.getEndTime()), "yyyy-MM-dd");
            rebateCommissionBillVO.setIssue(issueDay);
            rebateCommissionBillVO.setCommissionType(reviewRecordPO.getCommissionType());
            rebateCommissionBillVO.setSettleCycle(reviewRecordPO.getSettleCycle());
            rebateCommissionBillVO.setStartTime(reviewRecordPO.getStartTime());
            rebateCommissionBillVO.setEndTime(reviewRecordPO.getEndTime());
            rebateCommissionBillVO.setCurrency(reviewRecordPO.getCurrency());
            rebateCommissionBillVO.setCommissionAmount(reviewRecordPO.getCommissionAmount());
            rebateCommissionBillVO.setAdjustCommissionAmount(reviewRecordPO.getAdjustCommissionAmount());
            rebateCommissionBillVO.setAdjustCommissionRemark(reviewRecordPO.getAdjustCommissionRemark());
            rebateCommissionBillVO.setApplyAmount(reviewRecordPO.getApplyAmount());
            //有效流水
//            List<AgentRebateReportDetailPO> detailPOList = agentRebateReportDetailService.getRebateDetailByReportId(rebateFinalReportPO.getId());
//            BigDecimal validAmount = detailPOList.stream().map(AgentRebateReportDetailPO::getValidAmount).reduce(new BigDecimal("0.0000"), BigDecimal::add);
            rebateCommissionBillVO.setApplyAmount(reviewRecordPO.getApplyAmount());
            //以前字段保留,新加
            buildNewField(reviewRecordPO, rebateCommissionBillVO);

            detailVO.setRebateCommissionBillVO(rebateCommissionBillVO);

        } else {
            AgentRebateFinalReportPO rebateFinalReportPO = agentRebateFinalReportService.getById(reviewRecordPO.getReportId());
            PersonCommissionBillVO personCommissionBillVO = new PersonCommissionBillVO();
            String issueDay = DateUtil.format(new Date(reviewRecordPO.getEndTime()), "yyyy-MM-dd");
            personCommissionBillVO.setIssue(issueDay);
            personCommissionBillVO.setCommissionType(reviewRecordPO.getCommissionType());
            personCommissionBillVO.setSettleCycle(reviewRecordPO.getSettleCycle());
            personCommissionBillVO.setStartTime(reviewRecordPO.getStartTime());
            personCommissionBillVO.setEndTime(reviewRecordPO.getEndTime());
            personCommissionBillVO.setCurrency(reviewRecordPO.getCurrency());
            personCommissionBillVO.setCommissionAmount(reviewRecordPO.getCommissionAmount());
            personCommissionBillVO.setNewActiveNumber(rebateFinalReportPO.getNewValidNumber());
            personCommissionBillVO.setNewUserAmount(rebateFinalReportPO.getEveryUserAmount());
            personCommissionBillVO.setAdjustCommissionAmount(reviewRecordPO.getAdjustCommissionAmount());
            personCommissionBillVO.setAdjustCommissionRemark(reviewRecordPO.getAdjustCommissionRemark());
            personCommissionBillVO.setApplyAmount(reviewRecordPO.getApplyAmount());
            detailVO.setPersonCommissionBillVO(personCommissionBillVO);
        }


        CommissionReviewInfoVO commissionReviewInfoVO = new CommissionReviewInfoVO();
        commissionReviewInfoVO.setOneReviewer(reviewRecordPO.getOneReviewer());
        commissionReviewInfoVO.setSecondReviewer(reviewRecordPO.getSecondReviewer());

        Integer orderStatus = reviewRecordPO.getOrderStatus();
        Integer finalStatus = reviewRecordPO.getFinalStatus();
        if (finalStatus != null && finalStatus == 9) {
            commissionReviewInfoVO.setOneOrderStatus(3);
            commissionReviewInfoVO.setSecondOrderStatus(9);
        } else {
            if (orderStatus != null) {
                if (orderStatus == 3) {
                    commissionReviewInfoVO.setOneOrderStatus(3);
                    commissionReviewInfoVO.setSecondOrderStatus(3);
                }
                if (orderStatus == 4) {
                    commissionReviewInfoVO.setOneOrderStatus(4);
                    commissionReviewInfoVO.setSecondOrderStatus(null);
                }
                if (orderStatus == 8) {
                    commissionReviewInfoVO.setOneOrderStatus(4);
                    commissionReviewInfoVO.setSecondOrderStatus(4);
                }
            }
        }

        commissionReviewInfoVO.setOneReviewFinishTime(reviewRecordPO.getOneReviewFinishTime());
        commissionReviewInfoVO.setSecondReviewFinishTime(reviewRecordPO.getSecondReviewFinishTime());
        commissionReviewInfoVO.setOneReviewRemark(reviewRecordPO.getOneReviewRemark());
        commissionReviewInfoVO.setSecondReviewRemark(reviewRecordPO.getSecondReviewRemark());
        detailVO.setCommissionReviewInfoVO(commissionReviewInfoVO);

        return detailVO;
    }

    private void buildNewField(AgentCommissionReviewRecordPO reviewRecordPO, RebateCommissionBillVO rebateCommissionBillVO) {
        //下级代理数
        Long subAgents = commissionVenueRepository.selectSubAgents(reviewRecordPO.getAgentId(), reviewRecordPO.getSiteCode(),
                reviewRecordPO.getStartTime(), reviewRecordPO.getEndTime());
        rebateCommissionBillVO.setSubAgentNums(subAgents);

        //直属会员数
        Long dirUsers = commissionVenueRepository.selectDirUsers(reviewRecordPO.getAgentId(), reviewRecordPO.getSiteCode(),
                reviewRecordPO.getStartTime(), reviewRecordPO.getEndTime());
        rebateCommissionBillVO.setDirUserNums(dirUsers);

        //团队业绩
        BigDecimal teamCommission = commissionVenueRepository.selectTeamCommission(reviewRecordPO.getAgentId(), reviewRecordPO.getSiteCode(),
                reviewRecordPO.getStartTime(), reviewRecordPO.getEndTime());
        rebateCommissionBillVO.setTeamCommission(teamCommission);

        //直属会员业绩
        AgentCommissionVenueReportPO po = commissionVenueRepository.selectDirUserCommission(reviewRecordPO.getAgentId(), reviewRecordPO.getSiteCode(),
                reviewRecordPO.getStartTime(), reviewRecordPO.getEndTime());
        rebateCommissionBillVO.setTeamCommission(po.getCommissionAmount());

        //其他下级业绩
        BigDecimal otherLevelCommission = commissionVenueRepository.selectOtherLevelCommission(reviewRecordPO.getAgentId(), reviewRecordPO.getSiteCode(),
                reviewRecordPO.getStartTime(), reviewRecordPO.getEndTime());
        rebateCommissionBillVO.setOtherLevelAmount(otherLevelCommission);

    }


    /**
     * 一审成功 -> 待二审
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> oneReviewSuccessManualUp(ReviewListVO vo) {

        List<AgentCommissionReviewRecordPO> secondReviewList = orderStatusCheck(vo);
        boolean orderCheck = secondReviewList.stream()
                .allMatch(item -> CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode().equals(item.getOrderStatus()));
        if (!orderCheck) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        Long reviewTimme = System.currentTimeMillis();
        // 状态变更为待二审

        secondReviewList.forEach(item -> {
            item.setOneReviewFinishTime(reviewTimme);
            item.setOneReviewer(vo.getOperatorName());
            item.setOneReviewRemark(vo.getReviewRemark());
            item.setOrderStatus(CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode());
            item.setLockStatus(CommonConstant.business_zero);
            item.setLocker("");
            item.setUpdater(vo.getOperatorName());
            item.setUpdatedTime(reviewTimme);
        });
        boolean result = this.updateBatchById(secondReviewList);
        return ResponseVO.success(result);

//        //修改原始记录状态
//        finalReportService.updateAfterAuditSuccess(agentCommissionReviewRecordPO.getAgentId(),agentCommissionReviewRecordPO.getStartTime(),agentCommissionReviewRecordPO.getEndTime());
//        agentRebateFinalReportService.updateAfterAuditSuccess(agentCommissionReviewRecordPO.getAgentId(),agentCommissionReviewRecordPO.getStartTime(),agentCommissionReviewRecordPO.getEndTime());
//
//        //保存一份到佣金发放记录表
//        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSiteCode(upReview.getAgentAccount(), upReview.getSiteCode());
//        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoVO.getPlanCode());
//        AgentCommissionGrantRecordPO grantRecordPO = new AgentCommissionGrantRecordPO();
//        grantRecordPO.setAgentId(upReview.getAgentId());
//        grantRecordPO.setMerchantAccount(agentInfoVO.getMerchantAccount());
//        grantRecordPO.setMerchantName(agentInfoVO.getMerchantName());
//        grantRecordPO.setCommissionAmount(upReview.getCommissionAmount());
//        grantRecordPO.setAgentAccount(upReview.getAgentAccount());
//        grantRecordPO.setCommissionType(upReview.getCommissionType());
//        grantRecordPO.setAgentCategory(agentInfoVO.getAgentCategory());
//        grantRecordPO.setAgentType(agentInfoVO.getAgentType());
//        grantRecordPO.setRegisterTime(agentInfoVO.getRegisterTime());
//        grantRecordPO.setCurrency(upReview.getCurrency());
//        grantRecordPO.setGrantTime(reviewTimme);
//        grantRecordPO.setStartTime(upReview.getStartTime());
//        grantRecordPO.setEndTime(upReview.getEndTime());
//        grantRecordPO.setSettleCycle(upReview.getSettleCycle());
//        grantRecordPO.setReportId(upReview.getReportId());
//        grantRecordPO.setSettleCycle(upReview.getSettleCycle());
//        grantRecordPO.setPlanId(planVO.getId());
//        grantRecordPO.setSiteCode(upReview.getSiteCode());
//        grantRecordRepository.insert(grantRecordPO);
//        //给代理佣金钱包加额
//        String coinType = "";
//        if (CommissionTypeEnum.NEGATIVE.getCode().equals(upReview.getCommissionType())) {
//            coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.NEGATIVE_PROFIT_COMMISSION.getCode();
//        } else if (CommissionTypeEnum.REBATE.getCode().equals(upReview.getCommissionType())) {
//            coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.EFFECTIVE_TURNOVER_REBATE.getCode();
//        } else {
//            coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.CAPITATION_FEE.getCode();
//        }
//        AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
//        agentCoinAddVO.setSiteCode(upReview.getSiteCode());
//        agentCoinAddVO.setAgentAccount(agentInfoVO.getAgentAccount());
//        agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
//        agentCoinAddVO.setOrderNo(upReview.getOrderNo());
//        agentCoinAddVO.setCoinType(coinType);
//        agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode());
//        agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.INCOME.getCode());
//        agentCoinAddVO.setCoinValue(upReview.getCommissionAmount());
//        agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
//        agentCoinAddVO.setWithdrawFlag(CommonConstant.business_one);
//        agentCoinAddVO.setCoinTime(reviewTimme);
//        agentCoinAddVO.setRemark("代理佣金发放");
//        agentCoinAddVO.setAgentInfo(agentInfoVO);
//        agentCommissionCoinService.addCommissionCoin(agentCoinAddVO);
//
//
//
//        return ResponseVO.success();
    }

    /**
     * 合法性校验
     *
     * @param vo
     * @return
     */
    private List<AgentCommissionReviewRecordPO> orderStatusCheck(ReviewListVO vo) {
        List<String> ids = vo.getId();
        if (ids == null || ids.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<AgentCommissionReviewRecordPO> secondReviewList = reviewRepository.selectList(Wrappers.lambdaQuery(AgentCommissionReviewRecordPO.class)
                .in(AgentCommissionReviewRecordPO::getId, vo.getId()));
        if (secondReviewList.isEmpty() || vo.getId().size() != secondReviewList.size()) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        boolean lockerCheck = secondReviewList.stream().anyMatch(item -> !vo.getOperatorName().equals(item.getLocker()));
        if (lockerCheck) {
            throw new BaowangDefaultException(ResultCode.CURRENT_USER_CANT_OPERATION);
        }
        return secondReviewList;
    }

    /**
     * 一审拒绝
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> oneReviewFailManualUp(ReviewListVO vo) {
        List<AgentCommissionReviewRecordPO> secondReviewList = orderStatusCheck(vo);
        boolean orderCheck = secondReviewList.stream()
                .allMatch(item -> CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode().equals(item.getOrderStatus()));
        if (!orderCheck) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        Long reviewTime = System.currentTimeMillis();
        // 订单状态-拒绝
        secondReviewList.forEach(item -> {
            item.setOneReviewFinishTime(reviewTime);
            item.setOneReviewer(vo.getOperatorName());
            item.setOneReviewRemark(vo.getReviewRemark());
            item.setOrderStatus(CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode());
            item.setLockStatus(CommonConstant.business_zero);
            item.setLocker("");
            item.setUpdater(vo.getOperatorName());
            item.setUpdatedTime(reviewTime);
            item.setFinalStatus(CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode());
        });
        boolean result = this.updateBatchById(secondReviewList);
        return ResponseVO.success(result);
    }

    public Integer getUnreviewedRecordCount(String siteCode) {
        List<Integer> statusList = List.of(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode(),
                CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode());
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> query = Wrappers.lambdaQuery();
        query.in(AgentCommissionReviewRecordPO::getOrderStatus, statusList);
        query.eq(AgentCommissionReviewRecordPO::getSiteCode, siteCode);

        Long count = reviewRepository.selectCount(query);

        return count.intValue();
    }

    public ResponseVO<BigDecimal> calculateAgentCommission(CommissionReviewCalculateReq commissionReviewCalculateReq) {
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> query = Wrappers.lambdaQuery();
        query.eq(AgentCommissionReviewRecordPO::getOrderStatus, CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode());
        query.eq(AgentCommissionReviewRecordPO::getSiteCode, commissionReviewCalculateReq.getSiteCode());
        query.eq(AgentCommissionReviewRecordPO::getAgentAccount, commissionReviewCalculateReq.getAgentAccount());
        query.ge(AgentCommissionReviewRecordPO::getOneReviewFinishTime, commissionReviewCalculateReq.getAuditStartTime());
        query.le(AgentCommissionReviewRecordPO::getOneReviewFinishTime, commissionReviewCalculateReq.getAuditEndTime());
        List<AgentCommissionReviewRecordPO> agentCommissionReviewRecordPOS = this.baseMapper.selectList(query);
        if (CollectionUtils.isEmpty(agentCommissionReviewRecordPOS)) {
            return ResponseVO.success(BigDecimal.ZERO);
        }
        BigDecimal commissionAmount = agentCommissionReviewRecordPOS.stream().map(AgentCommissionReviewRecordPO::getCommissionAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseVO.success(commissionAmount);
    }

    public void deleteByReportId(String reportId) {
        reviewRepository.deleteByReportId(reportId);
    }

    /**
     * 查询-佣金审核-未审核数量角标
     *
     * @return
     */
    public AgentReviewOrderNumVO getNotReviewNum(String siteCode) {
        AgentReviewOrderNumVO vo = new AgentReviewOrderNumVO();
        List<Integer> statusList = List.of(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode(),
                CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode());
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> query = Wrappers.lambdaQuery();
        query.in(AgentCommissionReviewRecordPO::getOrderStatus, statusList);
        query.eq(AgentCommissionReviewRecordPO::getSiteCode, siteCode);

        Long count = reviewRepository.selectCount(query);
        vo.setNum(Integer.parseInt(count.toString()));
        vo.setRouter("/Funds/FundReview/CommissioReview");
        return vo;
    }

    /**
     * 二审 锁单,解锁
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> secondLockOrUnLock(StatusListVO vo) {
        List<String> ids = vo.getId();
        if (ids == null || ids.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        List<AgentCommissionReviewRecordPO> secondReviewList = reviewRepository.selectList(Wrappers.lambdaQuery(AgentCommissionReviewRecordPO.class)
                .in(AgentCommissionReviewRecordPO::getId, vo.getId()));
        if (secondReviewList.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        boolean lockerCheck = secondReviewList.stream()
                .anyMatch(item -> StringUtils.isNotEmpty(item.getLocker()) && !vo.getOperatorName().equals(item.getLocker()));

        if (lockerCheck) {
            throw new BaowangDefaultException(ResultCode.USER_REVIEW_LOCK_ERROR);
        }
        boolean orderCheck = secondReviewList.stream().anyMatch(item -> vo.getStatus().equals(item.getLockStatus()));
        if (orderCheck) {
            throw new BaowangDefaultException(ResultCode.WITHDRAW_HANDED);
        }

        Integer lockStatus, orderStatus;
        String locker;
        long updateTime;
        if (CommonConstant.business_zero.equals(vo.getStatus())) {
            //解锁
            lockStatus = CommonConstant.business_zero;
            updateTime = System.currentTimeMillis();
            orderStatus = CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode();
            locker = "";
        } else if (CommonConstant.business_one.equals(vo.getStatus())) {
            //锁单
            lockStatus = CommonConstant.business_one;
            updateTime = System.currentTimeMillis();
            orderStatus = CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode();
            locker = vo.getOperatorName();
        } else {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        secondReviewList.forEach(item -> {
            item.setLockStatus(lockStatus);
            item.setLocker(locker);
            item.setUpdatedTime(updateTime);
            item.setOrderStatus(orderStatus);
            item.setSecondReviewStartTime(updateTime);
        });
        boolean result = this.updateBatchById(secondReviewList);
        return ResponseVO.success(result);
    }

    /**
     * 二审成功 -> 订单最终态
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> secondReviewSuccess(ReviewListVO vo) {

        String adminName = CurrReqUtils.getAccount();
        String reviewRemark = vo.getReviewRemark();
        List<AgentCommissionReviewRecordPO> secondReviewList = orderStatusCheck(vo);
        boolean orderCheck = secondReviewList.stream()
                .allMatch(item -> CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode().equals(item.getOrderStatus()));
        if (!orderCheck) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        for (AgentCommissionReviewRecordPO upReview : secondReviewList) {
            // 状态审核成功
            AgentCommissionReviewRecordPO commissionReviewRecordPO = this.baseMapper.selectById(upReview.getId());
            if (commissionReviewRecordPO == null) {
                throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
            }
            Long reviewTime = System.currentTimeMillis();
            commissionReviewRecordPO.setSecondReviewFinishTime(reviewTime);
            commissionReviewRecordPO.setSecondReviewer(adminName);
            commissionReviewRecordPO.setSecondReviewRemark(reviewRemark);

            commissionReviewRecordPO.setOrderStatus(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode());
            commissionReviewRecordPO.setLockStatus(CommonConstant.business_zero);
            commissionReviewRecordPO.setLocker("");

            commissionReviewRecordPO.setUpdater(adminName);
            commissionReviewRecordPO.setUpdatedTime(reviewTime);

            commissionReviewRecordPO.setFinalStatus(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode());

            // commissionAmount(实际发放)=申请金额+调整金额
            commissionReviewRecordPO.setCommissionAmount(commissionReviewRecordPO.getApplyAmount().add(commissionReviewRecordPO.getAdjustCommissionAmount()));

            this.updateById(commissionReviewRecordPO);

            //修改原始记录状态
            String commissionType = commissionReviewRecordPO.getCommissionType();
            if (commissionType.equals(CommonConstant.business_one_str)) {
                //负盈利佣金
                finalReportService.updateAfterAuditSuccess(upReview.getAgentId(), upReview.getStartTime(), upReview.getEndTime(), commissionReviewRecordPO.getAdjustCommissionAmount());

            } else if (CommonConstant.business_two_str.equals(commissionType) ||
                    CommonConstant.business_three_str.equals(commissionType)) {
                agentRebateFinalReportService.updateAfterAuditSuccess(upReview.getAgentId(), upReview.getStartTime(), upReview.getEndTime(), commissionReviewRecordPO.getAdjustCommissionAmount(), commissionType);
            }

            //保存一份到佣金发放记录表
            AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSiteCode(upReview.getAgentAccount(), upReview.getSiteCode());
            AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoVO.getPlanCode());
            AgentCommissionGrantRecordPO grantRecordPO = new AgentCommissionGrantRecordPO();
            grantRecordPO.setAgentId(upReview.getAgentId());
            grantRecordPO.setMerchantAccount(agentInfoVO.getMerchantAccount());
            grantRecordPO.setMerchantName(agentInfoVO.getMerchantName());
            //申请+调整=发放
            grantRecordPO.setCommissionAmount(commissionReviewRecordPO.getCommissionAmount());
            grantRecordPO.setAdjustAmount(commissionReviewRecordPO.getAdjustCommissionAmount());
            grantRecordPO.setApplyAmount(commissionReviewRecordPO.getApplyAmount());

            grantRecordPO.setCommissionType(commissionType);
            grantRecordPO.setAgentAccount(upReview.getAgentAccount());
            grantRecordPO.setCommissionType(upReview.getCommissionType());
            grantRecordPO.setAgentCategory(agentInfoVO.getAgentCategory());
            grantRecordPO.setAgentType(agentInfoVO.getAgentType());
            grantRecordPO.setRegisterTime(agentInfoVO.getRegisterTime());
            grantRecordPO.setCurrency(upReview.getCurrency());
            grantRecordPO.setGrantTime(reviewTime);
            grantRecordPO.setStartTime(upReview.getStartTime());
            grantRecordPO.setEndTime(upReview.getEndTime());
            grantRecordPO.setSettleCycle(upReview.getSettleCycle());
            grantRecordPO.setReportId(upReview.getReportId());
            grantRecordPO.setSettleCycle(upReview.getSettleCycle());
            grantRecordPO.setPlanId(planVO.getId());
            grantRecordPO.setSiteCode(upReview.getSiteCode());
            grantRecordRepository.insert(grantRecordPO);
            //给代理佣金钱包加额
            String coinType = "";
            if (CommissionTypeEnum.NEGATIVE.getCode().equals(upReview.getCommissionType())) {
                coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.NEGATIVE_PROFIT_COMMISSION.getCode();
            } else if (CommissionTypeEnum.REBATE.getCode().equals(upReview.getCommissionType())) {
                coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.EFFECTIVE_TURNOVER_REBATE.getCode();
            } else {
                coinType = AgentCoinRecordTypeEnum.AgentCoinTypeEnum.CAPITATION_FEE.getCode();
            }
            AgentCoinAddVO agentCoinAddVO = new AgentCoinAddVO();
            agentCoinAddVO.setSiteCode(upReview.getSiteCode());
            agentCoinAddVO.setAgentAccount(agentInfoVO.getAgentAccount());
            agentCoinAddVO.setAgentWalletType(AgentCoinRecordTypeEnum.AgentWalletTypeEnum.COMMISSION_WALLET.getCode());
            agentCoinAddVO.setOrderNo(upReview.getOrderNo());
            agentCoinAddVO.setCoinType(coinType);
            agentCoinAddVO.setCustomerCoinType(AgentCoinRecordTypeEnum.AgentCustomerCoinTypeEnum.COMMISSION.getCode());
            agentCoinAddVO.setBalanceType(AgentCoinBalanceTypeEnum.INCOME.getCode());
            agentCoinAddVO.setCoinValue(commissionReviewRecordPO.getCommissionAmount());
            agentCoinAddVO.setBusinessCoinType(AgentCoinRecordTypeEnum.AgentBusinessCoinTypeEnum.AGENT_COMMISSION.getCode());
            agentCoinAddVO.setWithdrawFlag(CommonConstant.business_one);
            agentCoinAddVO.setCoinTime(reviewTime);
            agentCoinAddVO.setRemark("代理佣金发放");
            agentCoinAddVO.setAgentInfo(agentInfoVO);
            boolean addSuccess = agentCommonCoinService.agentCommonCommissionCoinAdd(agentCoinAddVO);
            log.info("佣金钱包加款状态" + addSuccess + " 订单号 :" + commissionReviewRecordPO.getOrderNo());
        }
        return ResponseVO.success();
    }

    /**
     * 二审拒绝
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> secondReviewRejected(ReviewListVO vo) {
        List<AgentCommissionReviewRecordPO> secondReviewList = orderStatusCheck(vo);

        boolean orderCheck = secondReviewList.stream()
                .allMatch(item -> CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode().equals(item.getOrderStatus()));
        if (!orderCheck) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        Long reviewTimme = System.currentTimeMillis();

        // 订单状态 -> 二审拒绝
        secondReviewList.forEach(item -> {
            item.setSecondReviewFinishTime(reviewTimme);
            item.setSecondReviewer(vo.getOperatorName());
            item.setSecondReviewRemark(vo.getReviewRemark());
            item.setOrderStatus(CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode());
            item.setLockStatus(CommonConstant.business_zero);
            item.setLocker("");
            item.setUpdater(vo.getOperatorName());
            item.setUpdatedTime(reviewTimme);
            item.setFinalStatus(CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode());
        });
        boolean result = this.updateBatchById(secondReviewList);
        return ResponseVO.success(result);
    }

    /**
     * 二审驳回
     *
     * @param vo
     * @return
     */
    public ResponseVO<?> secondReviewReturned(ReviewListVO vo) {
        List<AgentCommissionReviewRecordPO> secondReviewList = orderStatusCheck(vo);
        boolean orderCheck = secondReviewList.stream()
                .allMatch(item -> CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode().equals(item.getOrderStatus()));
        if (!orderCheck) {
            throw new BaowangDefaultException(ResultCode.REVIEW_STATUS_ERROR);
        }
        Long reviewTimme = System.currentTimeMillis();
        // 驳回 (回到一审)状态变为待审核
        secondReviewList.forEach(item -> {
            item.setSecondReviewFinishTime(reviewTimme);
            item.setSecondReviewer(vo.getOperatorName());
            item.setSecondReviewRemark(CommissionReviewOrderStatusEnum.SECOND_REVIEW_RETURNED.getName() + ":" + vo.getReviewRemark());
            item.setOrderStatus(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode());
            item.setLockStatus(CommonConstant.business_zero);
            item.setLocker("");
            item.setUpdater(vo.getOperatorName());
            item.setUpdatedTime(reviewTimme);
            item.setFinalStatus(CommissionReviewOrderStatusEnum.SECOND_REVIEW_RETURNED.getCode());
            item.setCommissionAmount(item.getApplyAmount());
            item.setAdjustCommissionAmount(BigDecimal.ZERO);
        });
        boolean result = this.updateBatchById(secondReviewList);
        return ResponseVO.success(result);
    }


    /**
     * 佣金调整
     *
     * @param vo
     * @return
     */
    public ResponseVO<Boolean> adjustCommission(AdjustCommissionVO vo) {
        AgentCommissionReviewRecordPO commissionReviewPO = this.baseMapper.selectOne(Wrappers.lambdaQuery(AgentCommissionReviewRecordPO.class)
                .eq(AgentCommissionReviewRecordPO::getId, vo.getId()));
        if (commissionReviewPO == null) {
            throw new BaowangDefaultException(ResultCode.NO_HAVE_DATA);
        }

        BigDecimal adjustAmount = vo.getAdjustCommissionAmount();

        if (adjustAmount.compareTo(BigDecimal.ZERO) == 0) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        BigDecimal applyAmount = commissionReviewPO.getApplyAmount();

        if (adjustAmount.compareTo(BigDecimal.ZERO) < 0) {
            if (adjustAmount.abs().compareTo(applyAmount) >= 0) {
                throw new BaowangDefaultException(ResultCode.RE_ENTER_AMOUNT);
            }
        }

        commissionReviewPO.setAdjustCommissionAmount(adjustAmount);
        BigDecimal commissionAmount = applyAmount.add(adjustAmount);
        commissionReviewPO.setCommissionAmount(commissionAmount);
        commissionReviewPO.setAdjustCommissionRemark(vo.getAdjustCommissionRemark());
        commissionReviewPO.setUpdater(commissionReviewPO.getUpdater());
        commissionReviewPO.setUpdater(vo.getOperatorName());
        commissionReviewPO.setUpdatedTime(System.currentTimeMillis());
        boolean result = this.updateById(commissionReviewPO);
        return ResponseVO.success(result);
    }


    public Long checkCommissionStatus(String siteCode, Long dayMillis) {
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentCommissionReviewRecordPO::getSiteCode, siteCode);
        wrapper.ge(AgentCommissionReviewRecordPO::getApplyTime, dayMillis);
        wrapper.le(AgentCommissionReviewRecordPO::getApplyTime, dayMillis);
        wrapper.in(AgentCommissionReviewRecordPO::getOrderStatus,
                List.of(CommonConstant.business_three, CommonConstant.business_five,
                        CommonConstant.business_two, CommonConstant.business_six, CommonConstant.business_seven));
        return this.baseMapper.selectCount(wrapper);
    }

    public void clearUserRebateRecord(Long startTime,Long endTime, String timeZoneStr, String siteCode) {
        LambdaUpdateWrapper<AgentCommissionReviewRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AgentCommissionReviewRecordPO::getSiteCode, siteCode);
        updateWrapper.ge(AgentCommissionReviewRecordPO::getStartTime, startTime);
        updateWrapper.le(AgentCommissionReviewRecordPO::getEndTime, endTime);
        this.baseMapper.delete(updateWrapper);
    }

    public AgentCommissionReviewRecordPO checkAgentCommissionExist(String siteCode, String agentId, Long apply_time) {
        LambdaQueryWrapper<AgentCommissionReviewRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AgentCommissionReviewRecordPO::getSiteCode, siteCode);
        wrapper.eq(AgentCommissionReviewRecordPO::getAgentId, agentId);
        wrapper.eq(AgentCommissionReviewRecordPO::getApplyTime, apply_time);
        return this.baseMapper.selectOne(wrapper);
    }

    public void insertOrUpdateAgentCommission(AgentInfoPO agentInfo, long now, long startTime, long endTime, BigDecimal wtcAmount) {
        List<AgentCommissionReviewRecordPO> commissionList = Lists.newArrayList();
        if (wtcAmount.compareTo(BigDecimal.ZERO) > 0) {
            AgentCommissionReviewRecordPO reviewRecordPO = new AgentCommissionReviewRecordPO();
            reviewRecordPO.setCreatedTime(now);
            reviewRecordPO.setAgentId(agentInfo.getAgentId());
            reviewRecordPO.setAgentName(agentInfo.getName());
            reviewRecordPO.setAgentAccount(agentInfo.getAgentAccount());
            reviewRecordPO.setAgentStatus(agentInfo.getStatus());
            reviewRecordPO.setCommissionType(CommissionTypeEnum.NEGATIVE.getCode());
            reviewRecordPO.setApplyAmount(wtcAmount);
            reviewRecordPO.setCommissionAmount(wtcAmount);
            reviewRecordPO.setStartTime(startTime);
            reviewRecordPO.setEndTime(endTime);
            reviewRecordPO.setSiteCode(agentInfo.getSiteCode());
            reviewRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
            reviewRecordPO.setApplyTime(now);
            reviewRecordPO.setSettleCycle(SettleCycleEnum.DAY.getCode());
            reviewRecordPO.setOrderNo(OrderUtil.getBatchNo("Y"));
            reviewRecordPO.setSettleStatus(0);
            reviewRecordPO.setLockStatus(0);
            reviewRecordPO.setOrderStatus(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode());
            commissionList.add(reviewRecordPO);
        }
        this.saveBatch(commissionList);
    }

}
