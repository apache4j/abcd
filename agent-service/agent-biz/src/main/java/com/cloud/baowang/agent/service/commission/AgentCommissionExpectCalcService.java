package com.cloud.baowang.agent.service.commission;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cloud.baowang.agent.api.vo.agent.winLoss.*;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionExpectReportPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.service.AgentDepositWithdrawService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.AgentCommissionStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseAgentApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueWinLossVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLoseVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserBetAmountSumVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserPlatformTransferApi;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.AgentDepositWithFeeVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/09/29 23:45
 * @description: 预期佣金结算
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentCommissionExpectCalcService {

    private final PlayVenueInfoApi playVenueInfoApi;
    private final AgentInfoService agentInfoService;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentCommissionLadderService agentCommissionLadderService;
    private final ReportUserWinLoseApi reportUserWinLoseApi;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final AgentValidUserRecordService agentValidUserRecordService;
    private final AgentVenueRateService agentVenueRateService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final UserPlatformTransferApi userPlatformTransferApi;
    private final AgentDepositWithdrawService agentDepositWithdrawService;
    private final AgentCommissionFinalReportService finalReportService;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final AgentCommissionExpectReportService expectReportService;
    private final SiteApi siteApi;
    private final ReportUserWinLoseAgentApi reportUserWinLoseAgentApi;

    private BigDecimal calcVenueFee(List<ReportAgentVenueWinLossVO> agentVenueWinLossVOList, Map<String, AgentVenueRateVO> venueRateMap, Map<String, BigDecimal> currencyRateMap) {
        BigDecimal venueFeeTotal = new BigDecimal("0.0000");

        Map<String, BigDecimal> venueAmountMap = new HashMap<>();
        //有效流水
        Map<String, BigDecimal> validAmountMap = new HashMap<>();
        for (ReportAgentVenueWinLossVO winLossVO : agentVenueWinLossVOList) {
            String siteCode=winLossVO.getSiteCode();
            BigDecimal rate = currencyRateMap.get(winLossVO.getCurrency());
            if(rate==null || rate.compareTo(BigDecimal.ZERO)<=0){
                log.info("场馆费预期计算,siteCode:{},币种:{}没有配置费率",siteCode,winLossVO.getCurrency());
                continue;
            }
            //输赢
            BigDecimal winLossAmount=winLossVO.getUserWinLossAmount()==null?new BigDecimal("0.0000"):winLossVO.getUserWinLossAmount();
            BigDecimal winLossAmountWtc = winLossAmount.divide(rate,4,RoundingMode.DOWN);
            venueAmountMap.merge(winLossVO.getVenueCode(), winLossAmountWtc, BigDecimal::add);

            //有效流水汇总累计
            BigDecimal validAmount=winLossVO.getValidAmount()==null?new BigDecimal("0.0000"):winLossVO.getValidAmount();
            BigDecimal validAmountWtc = validAmount.divide(rate,4,RoundingMode.DOWN);
            validAmountMap.merge(winLossVO.getVenueCode(), validAmountWtc, BigDecimal::add);

        }

        for (String venueCode : venueAmountMap.keySet()) {
            BigDecimal winLossAmount = venueAmountMap.get(venueCode);
            BigDecimal validAmount = validAmountMap.get(venueCode);
            AgentVenueRateVO rateVO = venueRateMap.get(venueCode);
            BigDecimal validFee = new BigDecimal("0.0000");
            BigDecimal venueFee = new BigDecimal("0.0000");
            BigDecimal currentVenueFee = new BigDecimal("0.0000");
            //负盈利佣金费率
            BigDecimal venueProportion = new BigDecimal(rateVO == null || rateVO.getRate() == null ? "0.0000" : rateVO.getRate());
            //有效流水费率
            BigDecimal validProportion = new BigDecimal(rateVO == null || rateVO.getValidRate() == null ? "0.0000" : rateVO.getValidRate());
            log.info("场馆代码:{},输赢金额:{},负盈利佣金费率:{},有效流水:{},有效流水费率:{}",venueCode,winLossAmount,venueProportion,validAmount,validFee);
            if (winLossAmount.compareTo(BigDecimal.ZERO) < 0) {
                venueFee=winLossAmount.multiply(venueProportion).divide(new BigDecimal(100),4,RoundingMode.DOWN);
            }
            if (validAmount.compareTo(BigDecimal.ZERO) > 0) {
                validFee=validAmount.multiply(validProportion).divide(new BigDecimal(100),4,RoundingMode.DOWN);
            }
            currentVenueFee=venueFee.abs().add(validFee.abs());
            log.info("场馆代码:{},场馆费金额:{}",venueCode,currentVenueFee);
            venueFeeTotal=venueFeeTotal.add(currentVenueFee);
        }
        return venueFeeTotal;
    }

    /**
     * 计算佣金
     */
    public void agentExpectCommissionGenerate(AgentCommissionCalcVO calcVO) {

        log.info("预期佣金开始结算，参数：{}", JSON.toJSONString(calcVO));
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());

        List<AgentInfoPO> infoList = agentInfoService.getAllBySiteCode(calcVO.getSiteCode());
        if (infoList == null || infoList.size() == 0) return;
        infoList = infoList.stream().sorted(Comparator.comparing(AgentInfoPO::getLevel)).collect(Collectors.toList());

        //从最底层开始遍历
        for (AgentInfoPO infoPO : infoList) {
            //if (!infoPO.getAgentAccount().equals("Asting006")) continue;
            //单位全部为平台币
            BigDecimal discountUsed = new BigDecimal("0.0000"); //会员已使用优惠
            BigDecimal userWinLossTotal = new BigDecimal("0.0000"); //会员总输赢
            BigDecimal validBetAmount = new BigDecimal("0.0000"); //会员总有效投注
            BigDecimal venueFee = new BigDecimal("0.0000"); //场馆费用
            BigDecimal depWithFee = new BigDecimal("0.0000");  //存提手续费
            BigDecimal alreadyUseAmount = new BigDecimal("0.0000"); //已使用优惠
            BigDecimal beforeSettleAmount = new BigDecimal("0.0000"); //提前结算
            BigDecimal lastRemainAmmount = new BigDecimal("0.0000"); //待冲正金额
            BigDecimal vipAmount = new BigDecimal("0.0000");
            BigDecimal discountAmount = new BigDecimal("0.0000");
            BigDecimal adjustAmount=BigDecimal.ZERO;//调整金额(其他调整)-平台币
            BigDecimal tipsAmount = BigDecimal.ZERO; //打赏金额
            BigDecimal betWinLoss = BigDecimal.ZERO; //会员输赢

            //所有下级代理ID
            List<String> agentIdList = agentInfoService.getSubAgentIdList(infoPO.getAgentId());

            //确定佣金方案和结算起始时间
            Long startTime = 0L;
            Long endTime = 0L;
            String zoneId = calcVO.getTimeZone();
            String planCode = "";
            //查询上一期的记录
            AgentCommissionFinalReportPO lastReportPO = finalReportService.getLatestReport(infoPO.getAgentId());

            //确定佣金方案,下级代理取总代的方案
            if (infoPO.getLevel() > 1) {
                String parentId = infoPO.getPath().split(",")[0];
                AgentInfoPO parentInfo = agentInfoService.getByAgentId(parentId);
                planCode = parentInfo.getPlanCode();
            } else {
                planCode = infoPO.getPlanCode();
            }

            AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);

            //NOTE 盈利阶梯分成
            List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
            //NOTE 代理佣金场馆费率
            List<AgentVenueRateVO> venueRateVOList = agentVenueRateService.getListByPlanId(planVO.getId());
            Map<String, AgentVenueRateVO> venueRateMap = venueRateVOList.stream().collect(Collectors.toMap(AgentVenueRateVO::getVenueCode, p -> p, (k1, k2) -> k2));
            Integer settleCycle = ladderList.get(0).getSettleCycle();

            if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
                startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
                endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
            } else {
                startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
                endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
            }

            log.info("预期佣金结算:{},时区：{} 时间:{} - {}", infoPO.getAgentAccount(), zoneId, startTime, endTime);
            //会员盈亏统计
            ReportAgentWinLossParamVO param = new ReportAgentWinLossParamVO();
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
                alreadyUseAmount = alreadyUseAmount.add(AmountUtils.divide(agentWinLoseVO.getAlreadyUseAmount(), rate));
                vipAmount = vipAmount.add(agentWinLoseVO.getVipAmount());
                discountAmount = discountAmount.add(agentWinLoseVO.getActivityAmount());
                adjustAmount=adjustAmount.add(agentWinLoseVO.getAdjustAmount());
            }

            //场馆盈亏统计
            List<ReportAgentVenueWinLossVO> agentVenueWinLossVOList = reportUserVenueWinLoseApi.queryAgentVenueWinLoss(param);
            if (ObjectUtil.isNotEmpty(agentVenueWinLossVOList)) {
                //场馆费
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
                lastRemainAmmount = lastReportPO.getLastMonthRemain();
            }

            //有效活跃和有效新增
            AgentActiveUserReqVO activeUserReqVO = new AgentActiveUserReqVO();
            activeUserReqVO.setAgentIds(agentIdList);
            activeUserReqVO.setStartTime(startTime);
            activeUserReqVO.setEndTime(endTime);
            activeUserReqVO.setSiteCode(infoPO.getSiteCode());
            activeUserReqVO.setPlanCode(planCode);
            AgentActiveUserResponseVO activeUserResponseVO = getAgentActiveUserInfo(infoPO.getAgentId(), activeUserReqVO, currencyRateMap);

            //平台净输赢 = 平台总输赢 - 会员已使用优惠 - 场馆费 - 存提手续费 + 待冲正金额
            //平台净输赢 = 平台总输赢 - 已使用优惠 - 其他调整（会员资金调整中的其他调整）- 场馆费 - 存提手续费 + 待冲正金额  公式调整 2025-07-04
            BigDecimal userNetWinLossTotal = userWinLossTotal.negate()
                    .subtract(venueFee)//场馆费
                    .subtract(discountUsed)//会员已使用优惠
                    .subtract(adjustAmount)//其他调整
                    .subtract(depWithFee)//存提手续费
                    .add(lastRemainAmmount);

            //计算佣金比例
            BigDecimal rate = new BigDecimal("0.0000");
            List<AgentCommissionLadderVO> rateList = ladderList.stream().filter(s -> s.getActiveNumber() <= activeUserResponseVO.getActiveNumber()
                    && s.getNewValidNumber() <= activeUserResponseVO.getNewValidNumber()
                    && s.getWinLossAmount().compareTo(userNetWinLossTotal) <= 0
                    && s.getValidAmount().compareTo(activeUserResponseVO.getTotalValidAmount()) <= 0).toList();
            Optional<AgentCommissionLadderVO> maxRate = rateList.stream()
                    .max(Comparator.comparingDouble(AgentCommissionLadderVO::getNumberRate));

            if (maxRate.isPresent() && userNetWinLossTotal.compareTo(BigDecimal.ZERO) > 0) {
                rate = new BigDecimal(maxRate.get().getRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_DOWN);
            }

            //负盈利佣金=会员净输赢*佣金比例
            BigDecimal commissionAmount = new BigDecimal("0.0000");
            if (userNetWinLossTotal.compareTo(BigDecimal.ZERO) > 0) {
                commissionAmount = userNetWinLossTotal.multiply(rate);
            }

            AgentCommissionExpectReportPO expectReportPO = new AgentCommissionExpectReportPO();
            expectReportPO.setAgentId(infoPO.getAgentId());
            expectReportPO.setAgentAccount(infoPO.getAgentAccount());
            expectReportPO.setAgentType(infoPO.getAgentType());
            expectReportPO.setAgentLevel(infoPO.getLevel());
            expectReportPO.setStartTime(startTime);
            expectReportPO.setEndTime(endTime);
            expectReportPO.setSettleCycle(settleCycle);
            expectReportPO.setAgentRate(rate.multiply(new BigDecimal("100")));
            expectReportPO.setCommissionAmount(commissionAmount);
            expectReportPO.setStatus(AgentCommissionStatusEnum.PENDING.getCode());
            expectReportPO.setActiveNumber(activeUserResponseVO.getActiveNumber());
            expectReportPO.setNewValidNumber(activeUserResponseVO.getNewValidNumber());
            expectReportPO.setAdjustAmount(adjustAmount);
            expectReportPO.setUserWinLoss(userWinLossTotal);
            expectReportPO.setUserWinLossTotal(userWinLossTotal.negate());
            expectReportPO.setValidBetAmount(activeUserResponseVO.getTotalValidAmount());
            expectReportPO.setSiteCode(infoPO.getSiteCode());
            expectReportPO.setPlanCode(planCode);
            expectReportPO.setVenueFee(venueFee);
            expectReportPO.setAccessFee(depWithFee);
            expectReportPO.setEarlySettle(new BigDecimal("0.0000"));
            expectReportPO.setTransferAmount(discountUsed);
            expectReportPO.setNetWinLoss(userNetWinLossTotal);
            expectReportPO.setVipAmount(vipAmount);
            expectReportPO.setLastMonthRemain(lastRemainAmmount);
            expectReportPO.setDiscountAmount(discountAmount);
            expectReportPO.setTipsAmount(tipsAmount);
            expectReportPO.setBetWinLoss(betWinLoss);

            //保存当前代理的数据
            LambdaUpdateWrapper<AgentCommissionExpectReportPO> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(AgentCommissionExpectReportPO::getAgentId, infoPO.getAgentId());
            updateWrapper.eq(AgentCommissionExpectReportPO::getEndTime, endTime);
            expectReportService.saveOrUpdate(expectReportPO, updateWrapper);
        }
    }

    public List<AgentCommissionExpectReportPO> calcCommissionByTime(AgentCommissionExpectCalcVO calcVO) {
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());

        List<AgentInfoVO> infoVOList = agentInfoService.getByAgentIds(calcVO.getAgentIds());
        SiteVO siteVO = siteApi.getSiteInfoByCode(calcVO.getSiteCode());
        List<AgentCommissionExpectReportPO> reportPOList = new ArrayList<>();
        for (AgentInfoVO infoVO : infoVOList) {
            //单位全部为平台币
            BigDecimal discountUsed = new BigDecimal("0.0000"); //会员已使用优惠
            BigDecimal userWinLossTotal = new BigDecimal("0.0000"); //会员总输赢
            BigDecimal validBetAmount = new BigDecimal("0.0000"); //会员总有效投注
            BigDecimal venueFee = new BigDecimal("0.0000"); //场馆费用
            BigDecimal depWithFee = new BigDecimal("0.0000");  //存提手续费
            BigDecimal alreadyUseAmount = new BigDecimal("0.0000"); //已使用优惠
            BigDecimal beforeSettleAmount = new BigDecimal("0.0000"); //提前结算
            BigDecimal lastRemainAmmount = new BigDecimal("0.0000"); //待冲正金额
            BigDecimal vipAmount = new BigDecimal("0.0000");
            BigDecimal discountAmount = new BigDecimal("0.0000");
            BigDecimal adjustAmount = BigDecimal.ZERO;//其他调整
            BigDecimal tipsAmount = BigDecimal.ZERO; //打赏金额
            BigDecimal betWinLoss = BigDecimal.ZERO; //会员输赢

            //所有下级代理ID
            List<String> agentIdList = agentInfoService.getSubAgentIdList(infoVO.getAgentId());

            //确定佣金方案和结算起始时间
            Long startTime = calcVO.getStartTime();
            Long endTime = calcVO.getEndTime();
            String zoneId = siteVO.getTimezone();
            String planCode = "";

            //确定佣金方案,下级代理取总代的方案
            if (infoVO.getLevel() > 1) {
                String parentId = infoVO.getPath().split(",")[0];
                AgentInfoPO parentInfo = agentInfoService.getByAgentId(parentId);
                planCode = parentInfo.getPlanCode();
            } else {
                planCode = infoVO.getPlanCode();
            }

            AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
            List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
            List<AgentVenueRateVO> venueRateVOList = agentVenueRateService.getListByPlanId(planVO.getId());
            Map<String, AgentVenueRateVO> venueRateMap = venueRateVOList.stream().collect(Collectors.toMap(AgentVenueRateVO::getVenueCode, p -> p, (k1, k2) -> k2));
            Integer settleCycle = ladderList.get(0).getSettleCycle();

            log.info("结算 {}，时区：{} 时间 {} - {}", infoVO.getAgentAccount(), zoneId, startTime, endTime);
            //会员盈亏统计
            ReportAgentWinLossParamVO param = new ReportAgentWinLossParamVO();
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
                log.info("预期场馆费计算,agentId:{},agentAccount:{}",infoVO.getAgentId(),infoVO.getAgentAccount());
                venueFee = calcVenueFee(agentVenueWinLossVOList, venueRateMap, currencyRateMap);
            }

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
            feeVO.setSiteCode(infoVO.getSiteCode());
            feeVO.setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
            List<AgentDepositWithFeeVO> agentDepFeeList = agentDepositWithdrawService.queryUserDepositWithdrawFee(feeVO);
            for (AgentDepositWithFeeVO withFeeVO : agentDepFeeList) {
                BigDecimal rate = currencyRateMap.get(withFeeVO.getCurrencyCode());
                BigDecimal fee = AmountUtils.divide(withFeeVO.getSettleFeeAmount(), rate);
                depWithFee = depWithFee.add(fee);
            }

            //查询上一期的记录
            AgentCommissionFinalReportPO lastReportPO = finalReportService.getLatestReport(infoVO.getAgentId());
            //待冲正金额
            //查询上一期的待冲正金额
            if (lastReportPO != null) {
                lastRemainAmmount = lastReportPO.getNetWinLoss().compareTo(BigDecimal.ZERO)>=0?BigDecimal.ZERO:lastReportPO.getNetWinLoss();
            }

            //有效活跃和有效新增
            AgentActiveUserReqVO activeUserReqVO = new AgentActiveUserReqVO();
            activeUserReqVO.setAgentIds(agentIdList);
            activeUserReqVO.setStartTime(startTime);
            activeUserReqVO.setEndTime(endTime);
            activeUserReqVO.setSiteCode(infoVO.getSiteCode());
            activeUserReqVO.setPlanCode(planCode);
            AgentActiveUserResponseVO activeUserResponseVO = getAgentActiveUserInfo(infoVO.getAgentId(), activeUserReqVO, currencyRateMap);

            //平台净输赢 = 平台总输赢 - 会员已使用优惠 -其他调整 - 场馆费 - 存提手续费  + 待冲正金额
            BigDecimal userNetWinLossTotal = userWinLossTotal.negate()
                    .subtract(discountUsed)
                    .subtract(adjustAmount)
                    .subtract(venueFee)
                    .subtract(depWithFee)
                    .add(lastRemainAmmount);
            log.info("平台总输赢:{},会员已使用优惠:{},其他调整:{},场馆费:{},存提手续费:{},待冲正金额:{}",
                    userWinLossTotal.negate(),discountUsed,adjustAmount,venueFee,depWithFee,lastRemainAmmount);

            //计算佣金比例
            BigDecimal rate = new BigDecimal("0.0000");
            List<AgentCommissionLadderVO> rateList = ladderList.stream().filter(s -> s.getActiveNumber() <= activeUserResponseVO.getActiveNumber()
                    && s.getNewValidNumber() <= activeUserResponseVO.getNewValidNumber()
                    && s.getWinLossAmount().compareTo(userNetWinLossTotal) <= 0
                    && s.getValidAmount().compareTo(activeUserResponseVO.getTotalValidAmount()) <= 0).toList();
            Optional<AgentCommissionLadderVO> maxRate = rateList.stream()
                    .max(Comparator.comparingDouble(AgentCommissionLadderVO::getNumberRate));

            if (maxRate.isPresent() && userNetWinLossTotal.compareTo(new BigDecimal("0.0000")) > 0) {
                rate = new BigDecimal(maxRate.get().getRate()).divide(new BigDecimal(100), 4, RoundingMode.HALF_DOWN);
            }

            //负盈利佣金=会员净输赢*佣金比例
            BigDecimal commissionAmount = new BigDecimal("0.0000");
            if (userNetWinLossTotal.compareTo(BigDecimal.ZERO) > 0) {
                commissionAmount = userNetWinLossTotal.multiply(rate).divide(BigDecimal.ONE,4, RoundingMode.DOWN);
            }

            AgentCommissionExpectReportPO expectReportPO = new AgentCommissionExpectReportPO();
            expectReportPO.setAgentId(infoVO.getAgentId());
            expectReportPO.setAgentAccount(infoVO.getAgentAccount());
            expectReportPO.setAgentType(infoVO.getAgentType());
            expectReportPO.setAgentLevel(infoVO.getLevel());
            expectReportPO.setStartTime(startTime);
            expectReportPO.setEndTime(endTime);
            expectReportPO.setSettleCycle(settleCycle);
            expectReportPO.setAgentRate(rate.multiply(new BigDecimal("100")));
            expectReportPO.setCommissionAmount(commissionAmount);
            expectReportPO.setStatus(AgentCommissionStatusEnum.PENDING.getCode());
            expectReportPO.setActiveNumber(activeUserResponseVO.getActiveNumber());
            expectReportPO.setNewValidNumber(activeUserResponseVO.getNewValidNumber());
            expectReportPO.setAdjustAmount(adjustAmount);
            expectReportPO.setUserWinLoss(userWinLossTotal);
            expectReportPO.setUserWinLossTotal(userWinLossTotal.negate());
            expectReportPO.setValidBetAmount(activeUserResponseVO.getTotalValidAmount());
            expectReportPO.setSiteCode(infoVO.getSiteCode());
            expectReportPO.setPlanCode(planCode);
            expectReportPO.setVenueFee(venueFee);
            expectReportPO.setAccessFee(depWithFee);
            expectReportPO.setEarlySettle(new BigDecimal("0.0000"));
            expectReportPO.setTransferAmount(discountUsed);
            expectReportPO.setNetWinLoss(userNetWinLossTotal);
            expectReportPO.setVipAmount(vipAmount);
            expectReportPO.setLastMonthRemain(lastRemainAmmount);
            expectReportPO.setDiscountAmount(discountAmount);
            expectReportPO.setTipsAmount(tipsAmount);
            expectReportPO.setBetWinLoss(betWinLoss);
            reportPOList.add(expectReportPO);
        }


        return reportPOList;
    }

    public AgentActiveUserResponseVO getAgentActiveUserInfo(String agentId, AgentActiveUserReqVO reqVO, Map<String, BigDecimal> currencyRateMap) {
        log.info("根据代理编号:{},获取活跃人数,参数:{}",agentId,reqVO);
        Integer activeNumber = 0;  //有效活跃
        Integer newValidNumber = 0; //有效新增
        BigDecimal totalValidAmount = new BigDecimal("0.0000");
        BigDecimal totalWinLoss = new BigDecimal("0.0000");
        //汇总下级会员的存款金额
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        paramVO.setStartTime(reqVO.getStartTime());
        paramVO.setEndTime(reqVO.getEndTime());
        paramVO.setAgentIds(reqVO.getAgentIds());
        List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);

        //金额先转为平台币
        BigDecimal depositTotal = new BigDecimal("0.0000");
        for (ReportUserAmountVO vo : depList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            vo.setRechargeAmount(AmountUtils.divide(vo.getRechargeAmount(), rate));
        }

        Map<String, BigDecimal> depositSum = depList.stream()
                .collect(Collectors.groupingBy(
                        ReportUserAmountVO::getUserId,
                        Collectors.reducing(new BigDecimal("0.0000"), ReportUserAmountVO::getRechargeAmount, BigDecimal::add)
                ));

        depositSum.forEach((k,v)->{ log.info("根据代理编号:{},获取活跃人数,会员:{}存款金额:{}",agentId,k,v); });
        //计算会员的流水
        //List<UserBetAmountSumVO> betAmountList = orderRecordApi.getUserOrderAmountByAgent(paramVO);
        log.info("根据代理编号:{},获取活跃人数参数:{}",agentId,paramVO);
        List<ReportUserBetAmountSumVO> betAmountList = reportUserWinLoseAgentApi.getWinLoseStatisticsByAgentIds(paramVO);
        for (ReportUserBetAmountSumVO vo : betAmountList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            vo.setValidAmount(AmountUtils.divide(vo.getValidAmount(), rate));
            vo.setWinLossAmount(AmountUtils.divide(vo.getWinLossAmount(), rate));
            totalWinLoss = totalWinLoss.add(vo.getWinLossAmount());
            totalValidAmount = totalValidAmount.add(vo.getValidAmount());
          //  log.info("根据代理编号:{},获取活跃人数,会员id:{},有效投注金额:{}",agentId,vo.getUserId(),vo);
        }

        Map<String, BigDecimal> validAmountSum = betAmountList.stream()
                .collect(Collectors.groupingBy(
                        ReportUserBetAmountSumVO::getUserId,
                        Collectors.reducing(new BigDecimal("0.0000"), ReportUserBetAmountSumVO::getValidAmount, BigDecimal::add)
                ));
        validAmountSum.forEach((k,v)->{ log.info("根据代理编号:{},获取活跃人数,会员:{},有效投注金额:{}",agentId,k,v); });

        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(reqVO.getPlanCode());
        log.info("根据代理编号:{},获取活跃人数,佣金方案信息:{}",agentId,planVO);
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
        log.info("根据代理编号:{},有效活跃人数:{}",agentId,activeNumber);
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

        log.info("根据代理编号:{},存款人数:{},投注人数{},汇总合并人数:{}",agentId,newActiveDepMap.size(),newActiveBetMap.size(),validKeys.size());

        //排除历史期已经算为有效新增的会员
        List<String> notSettleList = agentValidUserRecordService.getNotSettleList(new ArrayList<>(validKeys), agentId, CommissionTypeEnum.NEGATIVE.getCode());
        newValidNumber = notSettleList.size();

        AgentActiveUserResponseVO responseVO = new AgentActiveUserResponseVO();
        //有效活跃
        responseVO.setActiveNumber(activeNumber);
        //有效新增
        responseVO.setNewValidNumber(newValidNumber);
        responseVO.setTotalValidAmount(totalValidAmount);
        responseVO.setTotalWinLoss(totalWinLoss);
        log.info("根据代理编号:{},获取活跃人数结果:{}",agentId,responseVO);
        return responseVO;
    }


}
