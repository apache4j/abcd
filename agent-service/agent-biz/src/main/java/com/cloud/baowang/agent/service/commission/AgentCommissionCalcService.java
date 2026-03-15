package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import com.cloud.baowang.agent.service.AgentDepositWithdrawService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.AgentCommissionStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionReviewOrderStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLoseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLossAmountReportVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/09/29 23:45
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentCommissionCalcService {

    private final AgentInfoService agentInfoService;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentCommissionLadderService agentCommissionLadderService;
    private final ReportUserWinLoseApi reportUserWinLoseApi;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final AgentCommissionReviewService agentCommissionReviewService;
    private final AgentCommissionReviewRecordService agentCommissionReviewRecordService;
   // private final OrderRecordApi orderRecordApi;
    private final AgentVenueRateService agentVenueRateService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final UserPlatformTransferApi userPlatformTransferApi;
    private final AgentDepositWithdrawService agentDepositWithdrawService;
    private final AgentCommissionFinalReportService finalReportService;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final AgentValidUserRecordService agentValidUserRecordService;


    private BigDecimal calcVenueFee(List<ReportAgentVenueWinLossVO> agentVenueWinLossVOList, Map<String, AgentVenueRateVO> venueRateMap, Map<String, BigDecimal> currencyRateMap) {
        BigDecimal venueFeeTotal = BigDecimal.ZERO;
        Map<String, BigDecimal> venueAmountMap = new HashMap<>();
        //有效流水
        Map<String, BigDecimal> validAmountMap = new HashMap<>();
        String siteCode="";
        for (ReportAgentVenueWinLossVO winLossVO : agentVenueWinLossVOList) {
            siteCode=winLossVO.getSiteCode();
//            log.info("场馆费计算,siteCode:{},配置费率结果:{}",siteCode,currencyRateMap);
            BigDecimal rate = currencyRateMap.get(winLossVO.getCurrency());
            if(rate==null || rate.compareTo(BigDecimal.ZERO)<=0){
                log.info("场馆费计算,siteCode:{},币种:{}没有配置费率",siteCode,winLossVO.getCurrency());
                continue;
            }
            //输赢
            BigDecimal winLossAmount=winLossVO.getUserWinLossAmount()==null?BigDecimal.ZERO:winLossVO.getUserWinLossAmount();
            BigDecimal winLossAmountWtc = winLossAmount.divide(rate,4,RoundingMode.DOWN);
            venueAmountMap.merge(winLossVO.getVenueCode(), winLossAmountWtc, BigDecimal::add);

            //有效流水汇总累计
            BigDecimal validAmount=winLossVO.getValidAmount()==null?BigDecimal.ZERO:winLossVO.getValidAmount();
            BigDecimal validAmountWtc = validAmount.divide(rate,4,RoundingMode.DOWN);
            validAmountMap.merge(winLossVO.getVenueCode(), validAmountWtc, BigDecimal::add);

        }

        for (String venueCode : venueAmountMap.keySet()) {
            BigDecimal winLossAmount = venueAmountMap.get(venueCode);
            BigDecimal validAmount = validAmountMap.get(venueCode);
            BigDecimal currentVenueFee=BigDecimal.ZERO;
            BigDecimal currentValidFee=BigDecimal.ZERO;
            AgentVenueRateVO rateVO = venueRateMap.get(venueCode);
            //负盈利费率
            BigDecimal venueProportion = new BigDecimal(rateVO == null || rateVO.getRate() == null ? "0" : rateVO.getRate());
            //有效流水费率
            BigDecimal validProportion = new BigDecimal(rateVO == null || rateVO.getValidRate() == null ? "0" : rateVO.getValidRate());
            log.info("场馆费计算siteCode:{},venueCode:{},输赢金额:{},负盈利佣金费率:{},有效流水:{},有效流水费率:{}",siteCode,venueCode,winLossAmount,venueProportion,validAmount,validProportion);
            if (winLossAmount.compareTo(BigDecimal.ZERO) < 0) {
                currentVenueFee=AmountUtils.multiplyPercent(winLossAmount,venueProportion);
            }
            if (validAmount.compareTo(BigDecimal.ZERO) > 0) {
                currentValidFee=AmountUtils.multiplyPercent(validAmount,validProportion);
            }
            BigDecimal currentVenueFeeTotal=currentVenueFee.abs().add(currentValidFee.abs());
            venueFeeTotal=venueFeeTotal.add(currentVenueFeeTotal);
            log.info("场馆费计算siteCode:{},venueCode:{},场馆费:{},",siteCode,venueCode,currentVenueFeeTotal);
        }
        return venueFeeTotal;
    }

    /**
     * 计算佣金
     */
    public void agentFinalCommissionGenerate(AgentCommissionCalcVO calcVO) {

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());
        log.info("负盈利佣金计算,站点:{},汇率结果:{}",calcVO.getSiteCode(),currencyRateMap);

        //ResponseVO<SiteCurrencyInfoRespVO> currencyRes =  siteCurrencyInfoApi.findPlatCurrencyNameBySiteCode(calcVO.getSiteCode());
       // SiteCurrencyInfoRespVO  currencyInfoRespVO = currencyRes.getData();

        List<AgentInfoPO> infoList = agentCommissionPlanService.getAgentBySiteCodeAndCycle(calcVO.getSiteCode(), calcVO.getSettleCycle());
        if (infoList != null && !infoList.isEmpty()) { // 这里只考虑有佣金方案的 不需要return update by xiaozhi 20250414
            infoList = infoList.stream().sorted(Comparator.comparing(AgentInfoPO::getLevel)).toList();

            //从最底层开始遍历
            for (AgentInfoPO infoPO : infoList) {
                //单位全部为平台币
                BigDecimal discountUsed = BigDecimal.ZERO; //会员已使用优惠
                BigDecimal userWinLossTotal = BigDecimal.ZERO; //会员总输赢
                BigDecimal validBetAmount = BigDecimal.ZERO; //会员总有效投注
                BigDecimal venueFee = BigDecimal.ZERO; //场馆费用
                BigDecimal depWithFee = BigDecimal.ZERO;  //存提手续费
                BigDecimal alreadyUseAmount = BigDecimal.ZERO; //已使用优惠
                BigDecimal beforeSettleAmount = BigDecimal.ZERO; //提前结算
                BigDecimal lastPlatNetWinLoss=BigDecimal.ZERO;//上期平台净输赢
                BigDecimal vipAmount = BigDecimal.ZERO;
                BigDecimal discountAmount = BigDecimal.ZERO;
                BigDecimal adjustAmount = BigDecimal.ZERO;//调整金额
                BigDecimal tipsAmount = BigDecimal.ZERO; //打赏金额
                BigDecimal betWinLoss = BigDecimal.ZERO; //会员输赢
                //获取总代ID
                String oneAgentId=getOneAgentId(infoPO,infoList);
                if(!StringUtils.hasText(oneAgentId)){
                    log.error("未获取到总代:{},请检查佣金方案配置",infoPO);
                    continue;
                }

                //所有下级代理ID
                List<String> agentIdList = agentInfoService.getSubAgentIdList(infoPO.getAgentId());

                //确定佣金方案和结算起始时间
                Long startTime = 0L;
                Long endTime = 0L;
                String zoneId = calcVO.getTimeZone();
                String planCode = "";

                //确定佣金方案
                planCode = infoPO.getPlanCode();

                if (calcVO.getIsManual() != null && calcVO.getIsManual() == 1) {
                    startTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getStartTime().trim(), zoneId);
                    endTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getEndTime().trim() + ".999", zoneId);
                } else {
                    if (calcVO.getSettleCycle().equals(SettleCycleEnum.WEEK.getCode())) {
                        startTime = TimeZoneUtils.getStartOfLastWeekInTimeZone(System.currentTimeMillis(), zoneId);
                        endTime = TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), zoneId);
                    } else {
                        startTime = TimeZoneUtils.getLastMonStartTimeInTimeZone(zoneId);
                        endTime = TimeZoneUtils.getLastMonEndTimeInTimeZone(zoneId);
                    }
                }

                log.info("结算 {}，时区：{} 时间 {} - {}", infoPO.getAgentAccount(), zoneId, startTime, endTime);

                //查询上一期的记录
                AgentCommissionFinalReportPO lastReportPO = finalReportService.getLatestReport(infoPO.getAgentId());

                //已审核的不能重新结算
                ReviewRecordReqVO reviewRecordReqVO = new ReviewRecordReqVO();
                reviewRecordReqVO.setAgentId(infoPO.getAgentId());
                reviewRecordReqVO.setStartTime(startTime);
                reviewRecordReqVO.setEndTime(endTime);
                reviewRecordReqVO.setCommissionTypeList(List.of(CommissionTypeEnum.NEGATIVE.getCode()));
                List<AgentCommissionReviewRecordPO> oldReviewPOList = agentCommissionReviewRecordService.getReviewRecordByReportId(reviewRecordReqVO);
                if (oldReviewPOList != null && !oldReviewPOList.isEmpty()) {
//                    CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode().equals(oldReviewPOList.get(0).getOrderStatus())
                    boolean b = oldReviewPOList.stream().anyMatch(item -> {
                        //只要不是待一审,都不让重新结算
                        return !CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode().equals(item.getOrderStatus());
                    });
                    if (b){
                        log.info("代理负盈利佣金本期有不在待审核的记录，禁止重新结算");
                        continue;
                    }

                }

                AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
                List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
                List<AgentVenueRateVO> venueRateVOList = agentVenueRateService.getListByPlanId(planVO.getId());
                Map<String, AgentVenueRateVO> venueRateMap = venueRateVOList.stream().collect(Collectors.toMap(AgentVenueRateVO::getVenueCode, p -> p, (k1, k2) -> k2));
                //会员盈亏统计
                ReportAgentWinLossParamVO param = new ReportAgentWinLossParamVO();
                param.setSiteCode(calcVO.getSiteCode());
                param.setAgentIds(agentIdList);
                param.setStartTime(startTime);
                param.setEndTime(endTime);
                List<ReportAgentWinLoseVO> agentWinLoseList = reportUserWinLoseApi.getUserWinLossByAgentIds(param);
                for (ReportAgentWinLoseVO agentWinLoseVO : agentWinLoseList) {
                    BigDecimal rate = currencyRateMap.get(agentWinLoseVO.getCurrency());
                    //打赏金额
                    tipsAmount=tipsAmount.add(AmountUtils.divide(agentWinLoseVO.getTipsAmount(), rate));
                    //用户投注输赢
                    betWinLoss=betWinLoss.add(AmountUtils.divide(agentWinLoseVO.getBetWinLose(), rate));
                    //平台总输赢=-（用户投注输赢 - 打赏金额）
                    userWinLossTotal = userWinLossTotal.add(AmountUtils.divide(agentWinLoseVO.getBetWinLose().subtract(agentWinLoseVO.getTipsAmount()), rate));
                    validBetAmount = validBetAmount.add(AmountUtils.divide(agentWinLoseVO.getValidAmount(), rate));
                    //已使用优惠=活动+vip发放的主货币+平台转主货币
                    alreadyUseAmount = alreadyUseAmount.add(AmountUtils.divide(agentWinLoseVO.getAlreadyUseAmount(), rate));
                    vipAmount = vipAmount.add(agentWinLoseVO.getVipAmount());
                    discountAmount = discountAmount.add(agentWinLoseVO.getActivityAmount());
                    //调整金额=主货币转平台币
                    adjustAmount=adjustAmount.add(AmountUtils.divide(agentWinLoseVO.getAdjustAmount(), rate));
                }

                //场馆盈亏统计
                List<ReportAgentVenueWinLossVO> agentVenueWinLossVOList = reportUserVenueWinLoseApi.queryAgentVenueWinLoss(param);
                if (ObjectUtil.isNotEmpty(agentVenueWinLossVOList)) {
                    //场馆费
                    log.info("场馆费计算agentId:{},agentAccount:{}",infoPO.getAgentId(),infoPO.getAgentAccount());
                    venueFee = calcVenueFee(agentVenueWinLossVOList, venueRateMap, currencyRateMap);
                }

                //平台币兑换金额
                discountUsed = discountUsed.add(alreadyUseAmount);

                //会员存取手续费
                ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
                paramVO.setStartTime(startTime);
                paramVO.setEndTime(endTime);
                paramVO.setAgentIds(agentIdList);
                List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);
                for (ReportUserAmountVO vo : depList) {
                    BigDecimal rate = currencyRateMap.get(vo.getCurrency());
                    BigDecimal fee = AmountUtils.divide(vo.getSettleFeeAmount(), rate);
                    depWithFee = depWithFee.add(fee);
                }

                //代理存提手续费
                AgentDepositWithdrawFeeVO feeVO = new AgentDepositWithdrawFeeVO();
                feeVO.setAgentIds(agentIdList);
                feeVO.setEndTime(endTime);
                feeVO.setStartTime(startTime);
                feeVO.setSiteCode(infoPO.getSiteCode());
                feeVO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
                List<AgentDepositWithFeeVO> agentDepFeeList = agentDepositWithdrawService.queryUserDepositWithdrawFee(feeVO);
                for (AgentDepositWithFeeVO withFeeVO : agentDepFeeList) {
                    BigDecimal rate = currencyRateMap.get(withFeeVO.getCurrencyCode());
                    BigDecimal fee = AmountUtils.divide(withFeeVO.getSettleFeeAmount(), rate);
                    depWithFee = depWithFee.add(fee);
                }

                //待冲正金额
                //查询上一期的待冲正金额
                if (lastReportPO != null) {
                    lastPlatNetWinLoss = lastReportPO.getNetWinLoss();
                }

                //有效活跃和有效新增
                AgentActiveUserReqVO activeUserReqVO = new AgentActiveUserReqVO();
                activeUserReqVO.setAgentIds(agentIdList);
                activeUserReqVO.setStartTime(startTime);
                activeUserReqVO.setEndTime(endTime);
                activeUserReqVO.setSiteCode(infoPO.getSiteCode());
                activeUserReqVO.setPlanCode(planCode);
                //先删除同一期的数据 可以重复计算
                log.info("有效活跃计算:{}",infoPO.getAgentId());
                agentValidUserRecordService.deleteHis(infoPO.getAgentId(), startTime, endTime, CommissionTypeEnum.NEGATIVE.getCode());
                AgentActiveUserResponseVO activeUserResponseVO = getAgentActiveUserInfo(infoPO.getAgentId(), activeUserReqVO, currencyRateMap, CommissionTypeEnum.NEGATIVE.getCode());

                //NOTE 上期平台净输赢<0 时 本期待冲正 = 上期平台净输赢
                BigDecimal currentRemainAmount = lastPlatNetWinLoss.compareTo(BigDecimal.ZERO) < 0 ?lastPlatNetWinLoss:BigDecimal.ZERO;

                //NOTE 平台净输赢 = 平台总输赢 - 会员已使用优惠 - 场馆费 - 存提手续费 + 待冲正金额
                //NOTE 平台净输赢 = 平台总输赢 - 已使用优惠 - 其他调整（会员资金调整中的其他调整）- 场馆费 - 存提手续费 + 待冲正金额  公式调整 2025-07-04
                BigDecimal userNetWinLossTotal = userWinLossTotal.negate() //用户投注输赢 - 打赏金额
                        .subtract(venueFee)//场馆费
                        .subtract(discountUsed)//会员已使用优惠
                        .subtract(depWithFee)//存提手续费
                        .subtract(adjustAmount)//其他调整
                        .add(currentRemainAmount);


                //NOTE 计算佣金比例
                BigDecimal rate = BigDecimal.ZERO;
                List<AgentCommissionLadderVO> rateList = ladderList.stream().filter(s ->
                        s.getActiveNumber() <= activeUserResponseVO.getActiveNumber()
                        && s.getNewValidNumber() <= activeUserResponseVO.getNewValidNumber()
                        && s.getWinLossAmount().compareTo(userNetWinLossTotal) <= 0
                        && s.getValidAmount().compareTo(activeUserResponseVO.getTotalValidAmount()) <= 0).toList();

                //NOTE 取满足条件下最大的比例
                Optional<AgentCommissionLadderVO> maxRate = rateList.stream().max(Comparator.comparingDouble(AgentCommissionLadderVO::getNumberRate));


                if (maxRate.isPresent() && userNetWinLossTotal.compareTo(BigDecimal.ZERO) > 0) {
                    rate = new BigDecimal(maxRate.get().getRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_DOWN);
                }

                //NOTE 负盈利佣金=（代理线）平台净输赢*佣金比例
                BigDecimal commissionAmount = BigDecimal.ZERO;
                if (userNetWinLossTotal.compareTo(BigDecimal.ZERO) > 0) {
                    commissionAmount = userNetWinLossTotal.multiply(rate);
                }

                AgentCommissionFinalReportPO monthReportPO = new AgentCommissionFinalReportPO();
                monthReportPO.setAgentId(infoPO.getAgentId());
                monthReportPO.setAgentAccount(infoPO.getAgentAccount());
                monthReportPO.setAgentType(infoPO.getAgentType());
                monthReportPO.setAgentLevel(infoPO.getLevel());
                monthReportPO.setStartTime(startTime);
                monthReportPO.setEndTime(endTime);
                monthReportPO.setSuperAgentId(infoPO.getParentId());
                monthReportPO.setOneAgentId(oneAgentId);
                monthReportPO.setSettleCycle(calcVO.getSettleCycle());
                monthReportPO.setAgentRate(rate.multiply(new BigDecimal("100")));
                monthReportPO.setCommissionAmount(commissionAmount);
                monthReportPO.setStatus(AgentCommissionStatusEnum.PENDING.getCode());
                monthReportPO.setActiveNumber(activeUserResponseVO.getActiveNumber());
                monthReportPO.setNewValidNumber(activeUserResponseVO.getNewValidNumber());
                monthReportPO.setUserWinLoss(userWinLossTotal);
                monthReportPO.setUserWinLossTotal(userWinLossTotal.negate());
                monthReportPO.setValidBetAmount(activeUserResponseVO.getTotalValidAmount());
                monthReportPO.setSiteCode(infoPO.getSiteCode());
                monthReportPO.setPlanCode(planCode);
                monthReportPO.setVenueFee(venueFee);
                monthReportPO.setAccessFee(depWithFee);
                monthReportPO.setEarlySettle(BigDecimal.ZERO);
                monthReportPO.setTransferAmount(discountUsed);
                monthReportPO.setNetWinLoss(userNetWinLossTotal);
                monthReportPO.setVipAmount(vipAmount);
                monthReportPO.setAdjustAmount(adjustAmount);
                monthReportPO.setLastMonthRemain(currentRemainAmount);
                monthReportPO.setDiscountAmount(discountAmount);
                monthReportPO.setTipsAmount(tipsAmount);
                monthReportPO.setBetWinLoss(betWinLoss);

                AgentCommissionFinalReportPO oldReportPO = finalReportService.getReportByAgentId(infoPO.getAgentId(), endTime);
                if (oldReportPO != null)  {
                    finalReportService.removeById(oldReportPO.getId());
                }
                finalReportService.save(monthReportPO);

                //保存有效新增会员记录, 只保存总代
                if (activeUserResponseVO.getValidUserIdList() != null
                        && !activeUserResponseVO.getValidUserIdList().isEmpty()) {
                    //先删除同一期的数据
                    agentValidUserRecordService.deleteHis(infoPO.getAgentId(), monthReportPO.getStartTime(), monthReportPO.getEndTime(), CommissionTypeEnum.NEGATIVE.getCode());
                    AgentValidUserRecordVO agentValidUserRecordVO = new AgentValidUserRecordVO();
                    BeanUtils.copyProperties(monthReportPO, agentValidUserRecordVO);
                    agentValidUserRecordVO.setReportId(monthReportPO.getId());
                    agentValidUserRecordVO.setCommissionType(CommissionTypeEnum.NEGATIVE.getCode());
                    agentValidUserRecordService.saveSettleUser(activeUserResponseVO.getValidUserIdList(), agentValidUserRecordVO);
                }

                if ((infoPO.getParentId() == null || infoPO.getLevel() == 1) && commissionAmount.compareTo(BigDecimal.ZERO) > 0) {
                    AgentCommissionReviewRecordPO reviewRecordPO = new AgentCommissionReviewRecordPO();
                    reviewRecordPO.setCreatedTime(System.currentTimeMillis());
                    reviewRecordPO.setAgentId(infoPO.getAgentId());
                    reviewRecordPO.setReportId(monthReportPO.getId());
                    reviewRecordPO.setAgentName(infoPO.getName());
                    reviewRecordPO.setAgentAccount(infoPO.getAgentAccount());
                    reviewRecordPO.setAgentStatus(infoPO.getStatus());
                    reviewRecordPO.setCommissionType(CommissionTypeEnum.NEGATIVE.getCode());
                    reviewRecordPO.setApplyAmount(commissionAmount);
                    reviewRecordPO.setCommissionAmount(commissionAmount);
                    reviewRecordPO.setStartTime(startTime);
                    reviewRecordPO.setEndTime(endTime);
                    reviewRecordPO.setSiteCode(infoPO.getSiteCode());
                    reviewRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                    reviewRecordPO.setApplyTime(System.currentTimeMillis());
                    reviewRecordPO.setSettleCycle(calcVO.getSettleCycle());
                    reviewRecordPO.setOrderNo(OrderUtil.getBatchNo("Y"));
                    reviewRecordPO.setSettleStatus(0);
                    reviewRecordPO.setLockStatus(0);
                    reviewRecordPO.setOrderStatus(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode());

                    if (oldReportPO != null) {
                        agentCommissionReviewService.deleteByReportId(oldReportPO.getId());
                        reviewRecordPO.setSettleStatus(1);
                        reviewRecordPO.setSettleTime(System.currentTimeMillis());
                    }
                    agentCommissionReviewService.save(reviewRecordPO);
                }
        }
        }
    }

    /**
     * 获取总代Id
     * @param agentInfoPO
     * @param agentInfoPOList
     * @return
     */
    private String getOneAgentId(AgentInfoPO agentInfoPO, List<AgentInfoPO> agentInfoPOList) {
        if(!StringUtils.hasText(agentInfoPO.getParentAccount())){
            return agentInfoPO.getAgentId();
        }
        String parentAgentIdPath=agentInfoPO.getPath();
        String[] agentIdArray=parentAgentIdPath.split(",");
        for(int i=0;i<agentIdArray.length-1;i++){
            String currentParentAgentId=agentIdArray[i];
            Optional<AgentInfoPO> currentAgentInfoOptional=agentInfoPOList.stream().filter(o->o.getAgentId().equals(currentParentAgentId)).findFirst();
            if(currentAgentInfoOptional.isEmpty()){
                log.error("获取总代错误:{},请检查佣金方案配置",currentParentAgentId);
                break;
            }
            if( !StringUtils.hasText(currentAgentInfoOptional.get().getParentAccount())){
                return currentAgentInfoOptional.get().getAgentId();
            }
        }
        return null;
    }

    public AgentActiveUserResponseVO getAgentActiveUserInfo(String agentId, AgentActiveUserReqVO reqVO, Map<String, BigDecimal> currencyRateMap, String commissionType) {
        Integer activeNumber = 0;  //有效活跃
        Integer newValidNumber = 0; //有效新增
        BigDecimal totalValidAmount = BigDecimal.ZERO;
        BigDecimal totalWinLoss = BigDecimal.ZERO;
        //汇总下级会员的存款金额
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        paramVO.setStartTime(reqVO.getStartTime());
        paramVO.setEndTime(reqVO.getEndTime());
        paramVO.setAgentIds(reqVO.getAgentIds());
        List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);

        //金额先转为平台币
        BigDecimal depositTotal = BigDecimal.ZERO;
        for (ReportUserAmountVO vo : depList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            vo.setRechargeAmount(AmountUtils.divide(vo.getRechargeAmount(), rate));
        }

        Map<String, BigDecimal> depositSum = depList.stream()
                .collect(Collectors.groupingBy(
                        ReportUserAmountVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, ReportUserAmountVO::getRechargeAmount, BigDecimal::add)
                ));


        //计算会员的流水
      //  List<UserBetAmountSumVO> betAmountList = orderRecordApi.getUserOrderAmountByAgent(paramVO);
        //提高查询效率 从注单查询改成会员盈亏查询
        UserWinLossAmountParamVO vo=new UserWinLossAmountParamVO();
        vo.setStartTime(reqVO.getStartTime());
        vo.setEndTime(reqVO.getEndTime());
        vo.setAgentIds(reqVO.getAgentIds());
        List<UserWinLossAmountReportVO> userWinLossAmountReportVOS=reportUserWinLoseApi.queryUserOrderAmountByAgent(vo);

        for (UserWinLossAmountReportVO userWinLossAmountReportVO : userWinLossAmountReportVOS) {
            BigDecimal rate = currencyRateMap.get(userWinLossAmountReportVO.getCurrency());
            userWinLossAmountReportVO.setValidAmount(AmountUtils.divide(userWinLossAmountReportVO.getValidAmount(), rate));
            userWinLossAmountReportVO.setWinLossAmount(AmountUtils.divide(userWinLossAmountReportVO.getWinLossAmount(), rate));
            totalWinLoss = totalWinLoss.add(userWinLossAmountReportVO.getWinLossAmount());
            totalValidAmount = totalValidAmount.add(userWinLossAmountReportVO.getValidAmount());
        }

        Map<String, BigDecimal> validAmountSum = userWinLossAmountReportVOS.stream()
                .collect(Collectors.groupingBy(
                        UserWinLossAmountReportVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, UserWinLossAmountReportVO::getValidAmount, BigDecimal::add)
                ));


        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(reqVO.getPlanCode());
        //有效活跃
        Map<String, BigDecimal> activeDepMap = depositSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(planVO.getActiveDeposit()) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, BigDecimal> activeBetMap = validAmountSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(planVO.getActiveBet()) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<String> commonKeys = new HashSet<>(activeDepMap.keySet());
        commonKeys.retainAll(activeBetMap.keySet());

        activeNumber = commonKeys.size();

        //有效新增
        Map<String, BigDecimal> newActiveDepMap = depositSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(planVO.getValidDeposit()) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Map<String, BigDecimal> newActiveBetMap = validAmountSum.entrySet()
                .stream()
                .filter(entry -> entry.getValue().compareTo(planVO.getValidBet()) >= 0)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        Set<String> validKeys = new HashSet<>(newActiveDepMap.keySet());
        validKeys.retainAll(newActiveBetMap.keySet());

        //排除历史期已经算为有效新增的会员
        List<String> notSettleList = agentValidUserRecordService.getNotSettleList(new ArrayList<>(validKeys), agentId, commissionType);
        newValidNumber = notSettleList.size();

        AgentActiveUserResponseVO responseVO = new AgentActiveUserResponseVO();
        responseVO.setActiveNumber(activeNumber);
        responseVO.setNewValidNumber(newValidNumber);
        responseVO.setTotalValidAmount(totalValidAmount);
        responseVO.setTotalWinLoss(totalWinLoss);
        responseVO.setValidUserIdList(notSettleList);

        return responseVO;
    }

}
