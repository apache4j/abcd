package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentDepositWithdrawFeeInfoResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceCurrencyResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceResVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentTeamFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentVenueFeeInfoResVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanInfoVO;
import com.cloud.baowang.agent.api.vo.commission.CommissionVenueFeeVO;
import com.cloud.baowang.agent.api.vo.manualup.AgentManualUpDownVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.service.commission.AgentCommissionFinalReportService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager.AgentLowerLevelReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveNumberReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentDepositWithdrawFeeVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportRechargeAgentVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserRechargeAgentReqVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserRechargePayMethodAgentReqVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserRechargePayMethodAgentVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentReqVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentVO;
import com.cloud.baowang.report.api.vo.venuewinlose.ReportVenueWinLossAgentReqVO;
import com.cloud.baowang.report.api.vo.venuewinlose.ReportVenueWinLossAgentVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.SiteRechargeWayApi;
import com.cloud.baowang.wallet.api.api.SiteWithdrawWayApi;
import com.cloud.baowang.wallet.api.vo.recharge.SiteRechargeWayResVO;
import com.cloud.baowang.wallet.api.vo.withdraw.SiteWithdrawWayResVO;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AgentFinanceReportService {
    @Resource
    UserWinLoseApi userWinLoseApi;
    @Resource
    ReportUserVenueWinLoseApi userVenueWinLoseApi;
    @Resource
    ReportUserRechargeApi reportUserRechargeApi;
    @Resource
    AgentInfoService agentInfoService;
    @Resource
    SiteCurrencyInfoApi currencyInfoApi;
    @Resource
    AgentCommissionApi agentCommissionApi;
    @Resource
    SiteRechargeWayApi siteRechargeWayApi;
    @Resource
    SiteWithdrawWayApi siteWithdrawWayApi;
    @Resource
    AgentCommissionPlanService agentCommissionPlanService;
    @Resource
    AgentDepositWithdrawService agentDepositWithdrawService;
    @Resource
    AgentManualDownRecordService agentManualDownRecordService;
    @Resource
    PlayVenueInfoApi playVenueInfoApi;

    @Resource
    AgentCommissionFinalReportService finalReportService;


    /**
     * 总代才有净输赢，修改为直属于下级代理
     * 个人指的就是 直属会员
     *
     * @param vo
     * @return
     */
    public AgentFinanceResVO financeReport(AgentFinanceReqVO vo) {
        boolean isWtc = CommonConstant.PLAT_CURRENCY_CODE.equals(vo.getCurrencyCode());
        // 记录当前代理
        String currAgentId = vo.getAgentId();
        List<AgentInfoVO> allChildAgents = agentInfoService.findAllChildAgents(currAgentId);
        List<String> agentIds = allChildAgents.stream().map(AgentInfoVO::getAgentId).toList();
        // 币种vo集合
        // agentTeamMap 团队信息
        Map<String, AgentFinanceCurrencyResVO> agentTeamMap = Maps.newHashMap();
        // currAgentMap 个人信息
        Map<String, AgentFinanceCurrencyResVO> currAgentMap = Maps.newHashMap();
        // 汇率
        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        // 存取款  存提手续费
        // 存取款  存提手续费 会员
        ReportUserRechargeAgentReqVO rechargeAgentReqVO = new ReportUserRechargeAgentReqVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setSiteCode(vo.getSiteCode());
        if (!isWtc) rechargeAgentReqVO.setCurrency(vo.getCurrencyCode());
        // 查询时间范围内代理下会员存提总计
        ResponseVO<List<ReportRechargeAgentVO>> rechargeAgentResponseVO = reportUserRechargeApi.queryByTimeAndAgent(rechargeAgentReqVO);
        if (rechargeAgentResponseVO.isOk()) {
            List<ReportRechargeAgentVO> rechargeAgentVOS = rechargeAgentResponseVO.getData();
            Optional.ofNullable(rechargeAgentVOS).ifPresent(rechargeAgentVOList -> {
                agentFeeCaculate(rechargeAgentVOList, agentTeamMap, currAgentId, currAgentMap, allFinalRate, false, isWtc);
            });
        }
        // 存取款  存提手续费 代理
        AgentDepositWithdrawFeeVO feeVO = new AgentDepositWithdrawFeeVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setSiteCode(vo.getSiteCode())
                .setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        if (!isWtc) feeVO.setCurrency(vo.getCurrencyCode());
        List<ReportRechargeAgentVO> agentFeeList = agentDepositWithdrawService.queryAgentDepositWithdrawFee(feeVO);
        if (CollUtil.isNotEmpty(agentFeeList)) {
            agentFeeCaculate(agentFeeList, agentTeamMap, currAgentId, currAgentMap, allFinalRate, true, isWtc);
        }
        // 代理人工存取款
        if (isWtc) {
            List<AgentManualUpDownVO> list = agentManualDownRecordService.queryAgentDepositWithdraw(feeVO);
            list.forEach(manualVo -> {
                // 区分直属与团队
                if (currAgentId.equals(manualVo.getAgentId())) {
                    currAgentMap.putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                    AgentFinanceCurrencyResVO currResVO = currAgentMap.get(CommonConstant.PLAT_CURRENCY_CODE);
                    currResVO.setDepositAmount(currResVO.getDepositAmount().add(manualVo.getDepositeAmount()));
                    currResVO.setWithdrawAmount(currResVO.getWithdrawAmount().add(manualVo.getWithdrawAmount()));
                } else {
                    agentTeamMap.putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                    AgentFinanceCurrencyResVO currencyResVO = agentTeamMap.get(CommonConstant.PLAT_CURRENCY_CODE);
                    currencyResVO.setDepositAmount(currencyResVO.getDepositAmount().add(manualVo.getDepositeAmount()));
                    currencyResVO.setWithdrawAmount(currencyResVO.getWithdrawAmount().add(manualVo.getWithdrawAmount()));
                }
            });
        }

        // 场馆费 含有
        ReportVenueWinLossAgentReqVO venueWinLossAgentReqVO = new ReportVenueWinLossAgentReqVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setSiteCode(vo.getSiteCode());
        if (!isWtc) venueWinLossAgentReqVO.setCurrency(vo.getCurrencyCode());
        ResponseVO<List<ReportVenueWinLossAgentVO>> venueFeeResponseVO = userVenueWinLoseApi.queryByTimeAndAgent(venueWinLossAgentReqVO);
        AgentCommissionPlanInfoVO planInfoVO = agentCommissionPlanService.getPlanInfoByAgentId(agentIds.get(0));
        List<CommissionVenueFeeVO> venueFeeList = planInfoVO.getVenueFeeList();
        Map<String, CommissionVenueFeeVO> rateMap = Optional.ofNullable(venueFeeList)
                .map(s -> s.stream().collect(Collectors.toMap(CommissionVenueFeeVO::getVenueCode, p -> p, (k1, k2) -> k2)))
                .orElse(Maps.newHashMap());
        if (venueFeeResponseVO.isOk()) {
            List<ReportVenueWinLossAgentVO> venueFeeResponseVOS = venueFeeResponseVO.getData();
            Optional.ofNullable(venueFeeResponseVOS).ifPresent(venueWinLossAgentVOList -> {
                // 平台币种（如：CNY / USDT）对应的代理团队财务数据
                agentTeamMap.putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                // agentTeam 需要团队财务
                AgentFinanceCurrencyResVO agentTeam = agentTeamMap.get(CommonConstant.PLAT_CURRENCY_CODE);
                // ① 按【场馆】进行分组
                Map<String, List<ReportVenueWinLossAgentVO>> venueCodeGroupMap = venueWinLossAgentVOList.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getVenueCode));
                // 遍历每一个场馆
                venueCodeGroupMap.forEach((venueCode, gourpList) -> {
                    // ② 场馆内再按【币种】分组
                    Map<String, List<ReportVenueWinLossAgentVO>> currencyGroupList = gourpList.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getCurrency));
                    // 团队维度输赢（折算成平台币）
                    AtomicReference<BigDecimal> winLoseAmount = new AtomicReference<>(BigDecimal.ZERO);
                    // 团队维度输赢（折算成平台币）
                    AtomicReference<BigDecimal> currWinLoseAmount = new AtomicReference<>(BigDecimal.ZERO);
                    // 团队有效投注（平台币）包括自己的直属会员
                    AtomicReference<BigDecimal> validAmount = new AtomicReference<>(BigDecimal.ZERO);
                    // 当前代理有效投注（平台币） 只有自己的直属会员
                    AtomicReference<BigDecimal> currValidAmount = new AtomicReference<>(BigDecimal.ZERO);
                    AtomicReference<BigDecimal> currVenueFee = new AtomicReference<>(BigDecimal.ZERO);
                    AtomicReference<BigDecimal> venueFee = new AtomicReference<>(BigDecimal.ZERO);
                    // 遍历币种分组
                    currencyGroupList.forEach((currency, currGroupList) -> {
                        currGroupList.forEach(venueFeeVO -> {
                            BigDecimal rate = allFinalRate.get(currency);
                            BigDecimal amount = winLoseAmount.get();
                            BigDecimal validAmountRef = validAmount.get();
                            BigDecimal platAmount = AmountUtils.divide2(venueFeeVO.getWinLoseAmount(), rate);
                            // 累加团队数据
                            BigDecimal platValidAmount = AmountUtils.divide2(venueFeeVO.getValidBetAmount(), rate);

                            // 如果是当前代理，单独累加
                            if (currAgentId.equals(venueFeeVO.getAgentId())) {
                                currWinLoseAmount.set(currWinLoseAmount.get().add(platAmount));
                                currValidAmount.set(currValidAmount.get().add(platValidAmount));
                            } else {
                                winLoseAmount.set(amount.add(platAmount));
                                validAmount.set(validAmountRef.add(platValidAmount));
                            }
                        });
                    });
                    CommissionVenueFeeVO commissionVenueFeeVO = rateMap.get(venueCode);
                    if (winLoseAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                        venueFee.set(venueFee.get().add(winLoseAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getRate()))));
                    }
                    if (validAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                        venueFee.set(venueFee.get().add(validAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getValidRate()))));
                    }
                    if (currWinLoseAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                        currVenueFee.set(currWinLoseAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getRate())));
                        currAgentMap.putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                        AgentFinanceCurrencyResVO currAgent = currAgentMap.get(CommonConstant.PLAT_CURRENCY_CODE);
                        currAgent.setVenueFee(currAgent.getVenueFee().add(currVenueFee.get()));
                    }
                    if (currValidAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                        currVenueFee.set(currValidAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getValidRate())));
                        currAgentMap.putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                        AgentFinanceCurrencyResVO currAgent = currAgentMap.get(CommonConstant.PLAT_CURRENCY_CODE);
                        currAgent.setVenueFee(currAgent.getVenueFee().add(currVenueFee.get()));
                    }
                    agentTeam.setVenueFee(agentTeam.getVenueFee().add(venueFee.get()));
                    // 添加到 agentTeam
                });
            });
        }
        // 总输赢  vip福利 活动优惠 已使用优惠
        UserWinLoseAgentReqVO winLoseAgentReqVO = new UserWinLoseAgentReqVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setSiteCode(vo.getSiteCode());
        if (!isWtc) winLoseAgentReqVO.setCurrencyCode(vo.getCurrencyCode());
        // 调用用户会员盈亏统计接口
        ResponseVO<List<UserWinLoseAgentVO>> winLoseResponseVO = userWinLoseApi.queryByTimeAndAgent(winLoseAgentReqVO);
        if (winLoseResponseVO.isOk()) {
            List<UserWinLoseAgentVO> winLoseAgentVOS = winLoseResponseVO.getData();
            Optional.ofNullable(winLoseAgentVOS).ifPresent(winLoseAgentVOList -> {
                // ① 先按【代理ID】分组（区分不同代理团队）
                Map<String, List<UserWinLoseAgentVO>> map = winLoseAgentVOList.stream().collect(Collectors.groupingBy(UserWinLoseAgentVO::getAgentId));
                map.forEach((agentId, list) -> {
                    // 遍历每一个代理
                    Map<String, List<UserWinLoseAgentVO>> currencyGroupMap = list.stream().collect(Collectors.groupingBy(UserWinLoseAgentVO::getCurrency));
                    // ② 每个代理下，再按【币种】分组
                    currencyGroupMap.forEach((currency, gourpList) -> gourpList.forEach(winLoseVO -> {
                        agentTeamMap.putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                        // 根据币种
                        AgentFinanceCurrencyResVO agentTeam = agentTeamMap.get(currency);
                        BigDecimal winLoseAmount = winLoseVO.getWinLoseAmount();
                        BigDecimal vipAwardAmount = winLoseVO.getVipAwardAmount();
                        BigDecimal activityAwardAmount = winLoseVO.getActivityAwardAmount();
                        BigDecimal usedAmount = winLoseVO.getUsedAmount();
                        BigDecimal adjustAmount = winLoseVO.getAdjustAmount();
                        BigDecimal rebateAmount = winLoseVO.getRebateAmount();
                        BigDecimal tipsAmount = winLoseVO.getTipsAmount();
                        // 有效投注
                        BigDecimal validBetAmount = winLoseVO.getValidBetAmount();

                        if (currAgentId.equals(agentId)) {
                            currAgentMap.putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                            AgentFinanceCurrencyResVO currAgent = currAgentMap.get(currency);
                            currAgent.setTotalWinLoss(currAgent.getTotalWinLoss().add(winLoseAmount));
                            currAgent.setVipBenefits(currAgent.getVipBenefits().add(vipAwardAmount));
                            currAgent.setActivityDiscounts(currAgent.getActivityDiscounts().add(activityAwardAmount));
                            currAgent.setUsedDiscounts(currAgent.getUsedDiscounts().add(usedAmount));
                            currAgent.setAdjustAmount(currAgent.getAdjustAmount().add(adjustAmount));
                            currAgent.setRebateAmount(currAgent.getRebateAmount().add(rebateAmount));
                            currAgent.setTipsAmount(currAgent.getTipsAmount().add(tipsAmount));
                            // 统计到个人直属会员投注
                            currAgent.setValidBetAmount(currAgent.getValidBetAmount().add(validBetAmount));
                        }else {
                            agentTeam.setTotalWinLoss(agentTeam.getTotalWinLoss().add(winLoseAmount));
                            agentTeam.setVipBenefits(agentTeam.getVipBenefits().add(vipAwardAmount));
                            agentTeam.setActivityDiscounts(agentTeam.getActivityDiscounts().add(activityAwardAmount));
                            agentTeam.setUsedDiscounts(agentTeam.getUsedDiscounts().add(usedAmount));
                            agentTeam.setAdjustAmount(agentTeam.getAdjustAmount().add(adjustAmount));
                            agentTeam.setRebateAmount(agentTeam.getRebateAmount().add(rebateAmount));
                            agentTeam.setTipsAmount(agentTeam.getTipsAmount().add(tipsAmount));
                            // 团队投注包括直属
                            //agentTeam.setDirectUserValidBetAmount(agentTeam.getDirectUserValidBetAmount().add(validBetAmount));
                            agentTeam.setValidBetAmount(agentTeam.getValidBetAmount().add(validBetAmount));
                        }


                    }));
                });
            });
        }

        AgentFinanceCurrencyResVO current = new AgentFinanceCurrencyResVO();
        AgentFinanceCurrencyResVO team = new AgentFinanceCurrencyResVO();
        String targetCurrency = vo.getCurrencyCode();
        if (MapUtil.isNotEmpty(currAgentMap)) {
            currencyTrans(currAgentMap, allFinalRate, targetCurrency, current);

        }
        if (MapUtil.isNotEmpty(agentTeamMap)) {
            currencyTrans(agentTeamMap, allFinalRate, targetCurrency, team);
        }

        AgentFinanceResVO resVO = new AgentFinanceResVO();
        resVO.setCurrFinanceVO(current);
        resVO.setTeamFinanceVO(team);
        return resVO;
    }


    private static void agentFeeCaculate(List<ReportRechargeAgentVO> rechargeAgentVOList, Map<String, AgentFinanceCurrencyResVO> agentTeamMap, String currAgentId, Map<String, AgentFinanceCurrencyResVO> currAgentMap, Map<String, BigDecimal> allFinalRate, Boolean isAgent, boolean isWtc) {
        Map<String, List<ReportRechargeAgentVO>> map = rechargeAgentVOList.stream().collect(Collectors.groupingBy(ReportRechargeAgentVO::getAgentId));
        map.forEach((agentId, list) -> {
            Map<String, List<ReportRechargeAgentVO>> currencyGroupMap = list.stream().collect(Collectors.groupingBy(ReportRechargeAgentVO::getCurrency));
            currencyGroupMap.forEach((currency, gourpList) -> gourpList.forEach(rechargeAgentVO -> {
                BigDecimal rate = allFinalRate.get(currency);
                agentTeamMap.putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                AgentFinanceCurrencyResVO agentTeam = agentTeamMap.get(currency);
                BigDecimal rechargeAmount;
                BigDecimal withdrawAmount;
                if (isWtc) {
                    rechargeAmount = isAgent ? AmountUtils.multiply(rechargeAgentVO.getRechargeAmount(), rate) : rechargeAgentVO.getRechargeAmount();
                    withdrawAmount = isAgent ? AmountUtils.multiply(rechargeAgentVO.getWithdrawAmount(), rate) : rechargeAgentVO.getWithdrawAmount();
                } else {
                    rechargeAmount = isAgent ? rechargeAgentVO.getFiatRechargeAmount() : rechargeAgentVO.getRechargeAmount();
                    withdrawAmount = isAgent ? rechargeAgentVO.getFiatWithdrawAmount() : rechargeAgentVO.getWithdrawAmount();
                }
                BigDecimal settlementFeeAmount = rechargeAgentVO.getSettlementFeeAmount();
                if (currAgentId.equals(agentId)) {
                    currAgentMap.putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                    AgentFinanceCurrencyResVO currAgent = currAgentMap.get(rechargeAgentVO.getCurrency());
                    currAgent.setDepositAmount(currAgent.getDepositAmount().add(rechargeAmount));
                    currAgent.setWithdrawAmount(currAgent.getWithdrawAmount().add(withdrawAmount));
                    currAgent.setFeeAmount(currAgent.getFeeAmount().add(settlementFeeAmount));
                }
                agentTeam.setDepositAmount(agentTeam.getDepositAmount().add(rechargeAmount));
                agentTeam.setWithdrawAmount(agentTeam.getWithdrawAmount().add(withdrawAmount));
                agentTeam.setFeeAmount(agentTeam.getFeeAmount().add(settlementFeeAmount));
            }));
        });
    }

    private static void currencyTrans(Map<String, AgentFinanceCurrencyResVO> currAgentMap, Map<String, BigDecimal> allFinalRate, String targetCurrency, AgentFinanceCurrencyResVO current) {
        if (!StringUtils.hasText(targetCurrency) || MapUtil.isEmpty(currAgentMap)) {
            return;
        }
        currAgentMap.forEach((currency, financeVO) -> {
            BigDecimal vipBenefits = financeVO.getVipBenefits();
            BigDecimal activityDiscounts = financeVO.getActivityDiscounts();
            BigDecimal depositAmount = financeVO.getDepositAmount();
            BigDecimal withdrawAmount = financeVO.getWithdrawAmount();
            BigDecimal feeAmount = financeVO.getFeeAmount();
            BigDecimal usedDiscounts = financeVO.getUsedDiscounts();


            BigDecimal totalWinLoss = financeVO.getTotalWinLoss();
            BigDecimal venueFee = financeVO.getVenueFee();
            BigDecimal adjustAmount = financeVO.getAdjustAmount();
            BigDecimal tipsAmount = financeVO.getTipsAmount();
            // 汇率
            //总输赢 = -（用户投注输赢 - 打赏金额）
            BigDecimal newTotalWinLoss = financeVO.getTotalWinLoss().subtract(tipsAmount).negate();
            //净输赢 = 总输赢 - 已使用优惠 - 其他调整（会员资金调整中的其他调整）-场馆费- 存提手续费
            BigDecimal newNetWinLose = newTotalWinLoss.subtract(usedDiscounts)
                    .subtract(adjustAmount)
                    .subtract(venueFee)
                    .subtract(feeAmount);
            //
            BigDecimal validBetAmount = financeVO.getValidBetAmount();
            financeVO.setTotalWinLoss(newTotalWinLoss);
            financeVO.setNetWinLoss(newNetWinLose);
            BigDecimal rate = allFinalRate.get(currency);
            // 目标汇率
            BigDecimal targetRate = allFinalRate.get(targetCurrency);

            if (currency.equals(targetCurrency)) {
                // 目前币种一致 不转换
                current.setDepositAmount(current.getDepositAmount().add(depositAmount));
                current.setWithdrawAmount(current.getWithdrawAmount().add(withdrawAmount));
                current.setFeeAmount(current.getFeeAmount().add(feeAmount));
                current.setUsedDiscounts(current.getUsedDiscounts().add(usedDiscounts));
                current.setTotalWinLoss(current.getTotalWinLoss().add(newTotalWinLoss));
                current.setVenueFee(current.getVenueFee().add(venueFee.setScale(4, RoundingMode.DOWN)));
                current.setAdjustAmount(current.getAdjustAmount().add(adjustAmount));
                current.setNetWinLoss(current.getNetWinLoss().add(newNetWinLose));
                current.setVipBenefits(current.getVipBenefits().add(vipBenefits));
                current.setActivityDiscounts(current.getActivityDiscounts().add(activityDiscounts));
                current.setValidBetAmount(current.getValidBetAmount().add(validBetAmount));

            } else if (CommonConstant.PLAT_CURRENCY_CODE.equals(targetCurrency)) {
                // 平台币
                current.setDepositAmount(current.getDepositAmount().add(AmountUtils.divide(depositAmount, rate, 4)));
                current.setWithdrawAmount(current.getWithdrawAmount().add(AmountUtils.divide(withdrawAmount, rate, 4)));
                current.setFeeAmount(current.getFeeAmount().add(AmountUtils.divide(feeAmount, rate, 4)));
                current.setUsedDiscounts(current.getUsedDiscounts().add(AmountUtils.divide(usedDiscounts, rate, 4)));
                current.setTotalWinLoss(current.getTotalWinLoss().add(AmountUtils.divide(newTotalWinLoss, rate, 4)));
                current.setVenueFee(current.getVenueFee().add(venueFee.setScale(4, RoundingMode.DOWN)));
                current.setAdjustAmount(current.getAdjustAmount().add(AmountUtils.divide(adjustAmount, rate, 4)));
                current.setNetWinLoss(current.getNetWinLoss().add(AmountUtils.divide(newNetWinLose, rate, 4)));
                current.setVipBenefits(current.getVipBenefits().add(vipBenefits));
                current.setActivityDiscounts(current.getActivityDiscounts().add(activityDiscounts));
                current.setValidBetAmount(current.getValidBetAmount().add(AmountUtils.divide(validBetAmount, rate, 4)));

            } else {
                // 转换为目标币种
                current.setDepositAmount(current.getDepositAmount().add(AmountUtils.transfer(depositAmount, rate, targetRate)));
                current.setWithdrawAmount(current.getWithdrawAmount().add(AmountUtils.transfer(withdrawAmount, rate, targetRate)));
                current.setFeeAmount(current.getFeeAmount().add(AmountUtils.transfer(feeAmount, rate, targetRate)));
                current.setUsedDiscounts(current.getUsedDiscounts().add(AmountUtils.transfer(usedDiscounts, rate, targetRate)));
                current.setTotalWinLoss(current.getTotalWinLoss().add(AmountUtils.transfer(newTotalWinLoss, rate, targetRate)));
                current.setVenueFee(current.getVenueFee().add(AmountUtils.transfer(venueFee, rate, targetRate)));
                current.setAdjustAmount(current.getAdjustAmount().add(AmountUtils.transfer(adjustAmount, rate, targetRate)));
                current.setNetWinLoss(current.getNetWinLoss().add(AmountUtils.transfer(newNetWinLose, rate, targetRate)));
                current.setVipBenefits(current.getVipBenefits().add(vipBenefits));
                current.setActivityDiscounts(current.getActivityDiscounts().add(activityDiscounts));
                current.setValidBetAmount(current.getValidBetAmount().add(validBetAmount));

            }
            current.setRebateAmount(financeVO.getRebateAmount());

        });
    }

    public Page<AgentFinanceCurrencyResVO> financeReportTeamFinance(AgentTeamFinanceReqVO vo) {
        AgentInfoPO agentInfoVO = agentInfoService.getByAgentId(vo.getAgentId());
        if (ObjectUtil.isNull(agentInfoVO)) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        boolean isWtc = vo.getCurrencyCode().equals(CommonConstant.PLAT_CURRENCY_CODE);
        AgentLowerLevelReqVO build = AgentLowerLevelReqVO.builder().level(agentInfoVO.getLevel()).siteCode(vo.getSiteCode()).agentId(agentInfoVO.getAgentId()).agentAccount(vo.getAgentAccount()).build();
        Page<AgentInfoVO> agentInfoVOPage = agentInfoService.findAllChildAgentsByPage(new Page<>(vo.getPageNumber(), vo.getPageSize()), build, true);
        Page<AgentFinanceCurrencyResVO> result = new Page<>(agentInfoVOPage.getCurrent(), agentInfoVOPage.getSize(), agentInfoVOPage.getTotal());
        List<AgentInfoVO> records = agentInfoVOPage.getRecords();
        if (CollUtil.isEmpty(records)) {
            return result;
        }
        // 汇率
        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        List<String> agentIds = records.stream().map(AgentInfoVO::getAgentId).toList();
        // agentId  币种
        Map<String, Map<String, AgentFinanceCurrencyResVO>> resultMap = Maps.newHashMap();
        // 存取款  存提手续费
        // 存取款  存提手续费 会员
        ReportUserRechargeAgentReqVO rechargeAgentReqVO = new ReportUserRechargeAgentReqVO().setAgentIds(agentIds).setStartTime(vo.getStartTime()).setEndTime(vo.getEndTime()).setSiteCode(vo.getSiteCode());
        if (!isWtc) rechargeAgentReqVO.setCurrency(vo.getCurrencyCode());
        ResponseVO<List<ReportRechargeAgentVO>> rechargeAgentResponseVO = reportUserRechargeApi.queryByTimeAndAgent(rechargeAgentReqVO);
        if (rechargeAgentResponseVO.isOk()) {
            List<ReportRechargeAgentVO> rechargeAgentVOS = rechargeAgentResponseVO.getData();
            Optional.ofNullable(rechargeAgentVOS).ifPresent(rechargeAgentVOList -> agentFeeCaculatePage(rechargeAgentVOList, resultMap, allFinalRate, false, isWtc));
        }
        // 存取款  存提手续费 代理
        AgentDepositWithdrawFeeVO feeVO = new AgentDepositWithdrawFeeVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setSiteCode(vo.getSiteCode())
                .setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        if (!isWtc) feeVO.setCurrency(vo.getCurrencyCode());
        List<ReportRechargeAgentVO> agentFeeList = agentDepositWithdrawService.queryAgentDepositWithdrawFee(feeVO);
        if (CollUtil.isNotEmpty(agentFeeList)) {
            agentFeeCaculatePage(agentFeeList, resultMap, allFinalRate, true, isWtc);
        }
        // 代理人工存取款
        if (isWtc) {
            List<AgentManualUpDownVO> list = agentManualDownRecordService.queryAgentDepositWithdraw(feeVO);
            list.forEach(manualVo -> {
                String agentId = manualVo.getAgentId();
                resultMap.putIfAbsent(agentId, Maps.newHashMap());
                resultMap.get(agentId).putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                AgentFinanceCurrencyResVO financeResVO = resultMap.get(agentId).get(CommonConstant.PLAT_CURRENCY_CODE);
                financeResVO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                financeResVO.setAgentId(manualVo.getAgentId());
                financeResVO.setDepositAmount(financeResVO.getDepositAmount().add(manualVo.getDepositeAmount()));
                financeResVO.setWithdrawAmount(financeResVO.getWithdrawAmount().add(manualVo.getWithdrawAmount()));
            });
        }
        // 场馆费
        ReportVenueWinLossAgentReqVO venueWinLossAgentReqVO = new ReportVenueWinLossAgentReqVO().setAgentIds(agentIds).setStartTime(vo.getStartTime()).setEndTime(vo.getEndTime()).setSiteCode(vo.getSiteCode());
        if (!isWtc) venueWinLossAgentReqVO.setCurrency(vo.getCurrencyCode());
        // 会员盈亏查询
        ResponseVO<List<ReportVenueWinLossAgentVO>> venueFeeResponseVO = userVenueWinLoseApi.queryByTimeAndAgent(venueWinLossAgentReqVO);
        AgentCommissionPlanInfoVO planInfoVO = agentCommissionPlanService.getPlanInfoByAgentId(agentIds.get(0));
        List<CommissionVenueFeeVO> venueFeeList = planInfoVO.getVenueFeeList();
        Map<String, CommissionVenueFeeVO> rateMap = Optional.ofNullable(venueFeeList)
                .map(s -> s.stream().collect(Collectors.toMap(CommissionVenueFeeVO::getVenueCode, p -> p, (k1, k2) -> k2)))
                .orElse(Maps.newHashMap());
        if (venueFeeResponseVO.isOk()) {
            List<ReportVenueWinLossAgentVO> venueFeeResponseVOS = venueFeeResponseVO.getData();
            Optional.ofNullable(venueFeeResponseVOS).ifPresent(venueWinLossAgentVOList -> {
                Map<String, List<ReportVenueWinLossAgentVO>> map = venueWinLossAgentVOList.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getAgentId));
                map.forEach((agentId, list) -> {
                    resultMap.putIfAbsent(agentId, Maps.newHashMap());
                    resultMap.get(agentId).putIfAbsent(CommonConstant.PLAT_CURRENCY_CODE, new AgentFinanceCurrencyResVO());
                    AgentFinanceCurrencyResVO financeResVO = resultMap.get(agentId).get(CommonConstant.PLAT_CURRENCY_CODE);
                    Map<String, List<ReportVenueWinLossAgentVO>> venueCodeGroupMap = list.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getVenueCode));
                    venueCodeGroupMap.forEach((venueCode, gourpList) -> {
                        Map<String, List<ReportVenueWinLossAgentVO>> currencyGroupList = gourpList.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getCurrency));
                        AtomicReference<BigDecimal> winLoseAmount = new AtomicReference<>(BigDecimal.ZERO);
                        AtomicReference<BigDecimal> validAmount = new AtomicReference<>(BigDecimal.ZERO);
                        AtomicReference<BigDecimal> venueFee = new AtomicReference<>(BigDecimal.ZERO);
                        currencyGroupList.forEach((currency, currGroupList) -> {
                            currGroupList.forEach(venueFeeVO -> {
                                BigDecimal rate = allFinalRate.get(currency);
                                winLoseAmount.set(winLoseAmount.get().add(AmountUtils.divide2(venueFeeVO.getWinLoseAmount(), rate)));
                                validAmount.set(validAmount.get().add(AmountUtils.divide2(venueFeeVO.getValidBetAmount(), rate)));

                            });
                        });
                        CommissionVenueFeeVO commissionVenueFeeVO = rateMap.get(venueCode);
                        if (winLoseAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                            venueFee.set(venueFee.get().add(winLoseAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getRate()))));
                        }
                        if (validAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                            venueFee.set(venueFee.get().add(validAmount.get().divide(CommonConstant.DECIMAL_100).multiply(new BigDecimal(commissionVenueFeeVO.getValidRate()))));
                        }
                        financeResVO.setVenueFee(financeResVO.getVenueFee().add(venueFee.get()));
                    });
                });
            });
        }
        // 总输赢  vip福利 活动优惠 已使用优惠
        UserWinLoseAgentReqVO winLoseAgentReqVO = new UserWinLoseAgentReqVO().setAgentIds(agentIds).setStartTime(vo.getStartTime()).setEndTime(vo.getEndTime()).setSiteCode(vo.getSiteCode());
        if (!isWtc) winLoseAgentReqVO.setCurrencyCode(vo.getCurrencyCode());
        ResponseVO<List<UserWinLoseAgentVO>> winLoseResponseVO = userWinLoseApi.queryByTimeAndAgent(winLoseAgentReqVO);
        if (winLoseResponseVO.isOk()) {
            List<UserWinLoseAgentVO> winLoseAgentVOS = winLoseResponseVO.getData();
            Optional.ofNullable(winLoseAgentVOS).ifPresent(winLoseAgentVOList -> {
                Map<String, List<UserWinLoseAgentVO>> map = winLoseAgentVOList.stream().collect(Collectors.groupingBy(UserWinLoseAgentVO::getAgentId));
                map.forEach((agentId, list) -> {
                    Map<String, List<UserWinLoseAgentVO>> currencyGroupMap = list.stream().collect(Collectors.groupingBy(UserWinLoseAgentVO::getCurrency));
                    currencyGroupMap.forEach((currency, gourpList) -> gourpList.forEach(winLoseVO -> {
                        resultMap.putIfAbsent(agentId, Maps.newHashMap());
                        resultMap.get(agentId).putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                        AgentFinanceCurrencyResVO financeResVO = resultMap.get(agentId).get(currency);
                        BigDecimal winLoseAmount = winLoseVO.getWinLoseAmount();
                        BigDecimal tipsAmount = winLoseVO.getTipsAmount();
                        BigDecimal activityAwardAmount = winLoseVO.getActivityAwardAmount();
                        BigDecimal vipAwardAmount = winLoseVO.getVipAwardAmount();
                        BigDecimal usedAmount = winLoseVO.getUsedAmount();
                        BigDecimal adjustAmount = winLoseVO.getAdjustAmount();
                        BigDecimal rebateAmount = winLoseVO.getRebateAmount();
                        BigDecimal validBetAmount = winLoseVO.getValidBetAmount();


                        financeResVO.setActivityDiscounts(financeResVO.getActivityDiscounts().add(activityAwardAmount));
                        financeResVO.setVipBenefits(financeResVO.getVipBenefits().add(vipAwardAmount));
                        financeResVO.setUsedDiscounts(financeResVO.getUsedDiscounts().add(usedAmount));
                        financeResVO.setAdjustAmount(financeResVO.getAdjustAmount().add(adjustAmount));
                        financeResVO.setTotalWinLoss(financeResVO.getTotalWinLoss().add(winLoseAmount));
                        financeResVO.setRebateAmount(financeResVO.getRebateAmount().add(rebateAmount));
                        financeResVO.setTipsAmount(financeResVO.getTipsAmount().add(tipsAmount));
                        financeResVO.setValidBetAmount(financeResVO.getValidBetAmount().add(validBetAmount));

                    }));
                });
            });
        }
        String targetCurrency = vo.getCurrencyCode();
        List<AgentFinanceCurrencyResVO> list = Lists.newArrayList();
        AgentActiveNumberReqVO activeNumberReqVO = new AgentActiveNumberReqVO()
                .setIsDirect(true).setAgentIdList(agentIds).setStartTime(vo.getStartTime()).setEndTime(vo.getEndTime()).setSiteCode(vo.getSiteCode());
        List<AgentActiveUserResponseVO> agentActiveUserInfoList = agentCommissionApi.getAgentActiveUserInfoList(activeNumberReqVO);
        Map<String, AgentActiveUserResponseVO> activeUserMap = Optional.ofNullable(agentActiveUserInfoList).map(s -> s.stream().collect(Collectors.toMap(AgentActiveUserResponseVO::getAgentId, p -> p, (k1, k2) -> k2))).orElse(Maps.newHashMap());
        records.forEach(record -> {
            Map<String, AgentFinanceCurrencyResVO> agentTeamFinanceResVOMap = resultMap.get(record.getAgentId());
            AgentFinanceCurrencyResVO currencyResVO = new AgentFinanceCurrencyResVO();
            currencyResVO.setAgentAccount(record.getAgentAccount());
            currencyResVO.setAgentId(record.getAgentId());
            currencyResVO.setCurrency(targetCurrency);
            currencyTrans(agentTeamFinanceResVOMap, allFinalRate, targetCurrency, currencyResVO);
            AgentActiveUserResponseVO agentActiveUserResponseVO = activeUserMap.get(record.getAgentId());
            Optional.ofNullable(agentActiveUserResponseVO).ifPresent(s -> {
                currencyResVO.setActiveNum(s.getActiveNumber());
                currencyResVO.setValidNewNum(s.getNewValidNumber());
            });
            list.add(currencyResVO);
        });
        result.setRecords(list);
        return result;
    }


    private static void agentFeeCaculatePage(List<ReportRechargeAgentVO> agentFeeList, Map<String, Map<String, AgentFinanceCurrencyResVO>> resultMap, Map<String, BigDecimal> allFinalRate, boolean isAgent, boolean isWtc) {
        Map<String, List<ReportRechargeAgentVO>> map = agentFeeList.stream().collect(Collectors.groupingBy(ReportRechargeAgentVO::getAgentId));
        map.forEach((agentId, list) -> {
            Map<String, List<ReportRechargeAgentVO>> currencyGroupMap = list.stream().collect(Collectors.groupingBy(ReportRechargeAgentVO::getCurrency));
            currencyGroupMap.forEach((currency, gourpList) -> gourpList.forEach(rechargeAgentVO -> {
                resultMap.putIfAbsent(agentId, Maps.newHashMap());
                resultMap.get(agentId).putIfAbsent(currency, new AgentFinanceCurrencyResVO());
                AgentFinanceCurrencyResVO financeResVO = resultMap.get(agentId).get(currency);
                BigDecimal rate = allFinalRate.get(currency);
                BigDecimal rechargeAmount;
                BigDecimal withdrawAmount;
                if (isWtc) {
                    rechargeAmount = isAgent ? AmountUtils.multiply(rechargeAgentVO.getRechargeAmount(), rate) : rechargeAgentVO.getRechargeAmount();
                    withdrawAmount = isAgent ? AmountUtils.multiply(rechargeAgentVO.getWithdrawAmount(), rate) : rechargeAgentVO.getWithdrawAmount();
                } else {
                    rechargeAmount = isAgent ? rechargeAgentVO.getFiatRechargeAmount() : rechargeAgentVO.getRechargeAmount();
                    withdrawAmount = isAgent ? rechargeAgentVO.getFiatWithdrawAmount() : rechargeAgentVO.getWithdrawAmount();
                }

                BigDecimal settlementFeeAmount = rechargeAgentVO.getSettlementFeeAmount();
                financeResVO.setDepositAmount(financeResVO.getDepositAmount().add(rechargeAmount));
                financeResVO.setWithdrawAmount(financeResVO.getWithdrawAmount().add(withdrawAmount));
                financeResVO.setFeeAmount(financeResVO.getFeeAmount().add(settlementFeeAmount));
            }));
        });
    }

    public List<AgentVenueFeeInfoResVO> venueFeeInfo(AgentVenueFeeInfoReqVO vo) {
        boolean isWtc = vo.getCurrencyCode().equals(CommonConstant.PLAT_CURRENCY_CODE);
        /// 记录当前代理
        String currAgentId = vo.getAgentId();
        List<String> agentIds;
        if (vo.getSelf().equals(CommonConstant.business_one)) {
            agentIds = List.of(vo.getAgentId());
        } else {
            List<AgentInfoVO> allChildAgents = agentInfoService.findAllChildAgents(currAgentId);
            agentIds = allChildAgents.stream().map(AgentInfoVO::getAgentId).toList();
        }
        // 汇率
        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        List<AgentVenueFeeInfoResVO> resultList = Lists.newArrayList();
        String targetCurrency = vo.getCurrencyCode();
        // 场馆费
        ReportVenueWinLossAgentReqVO venueWinLossAgentReqVO = new ReportVenueWinLossAgentReqVO().setAgentIds(agentIds).setStartTime(vo.getStartTime()).setEndTime(vo.getEndTime()).setSiteCode(vo.getSiteCode());
        if (!isWtc) venueWinLossAgentReqVO.setCurrency(vo.getCurrencyCode());
        ResponseVO<List<ReportVenueWinLossAgentVO>> venueFeeResponseVO = userVenueWinLoseApi.queryByTimeAndAgent(venueWinLossAgentReqVO);
        //代理佣金方案
        AgentCommissionPlanInfoVO planInfoVO = agentCommissionPlanService.getPlanInfoByAgentId(agentIds.get(0));
        //场馆费率
        List<CommissionVenueFeeVO> venueFeeList = planInfoVO.getVenueFeeList();
        Map<String, CommissionVenueFeeVO> rateMap = Optional.ofNullable(venueFeeList)
                .map(s -> s.stream().collect(Collectors.toMap(CommissionVenueFeeVO::getVenueCode, p -> p, (k1, k2) -> k2)))
                .orElse(Maps.newHashMap());
        if (venueFeeResponseVO.isOk()) {
            List<ReportVenueWinLossAgentVO> venueFeeResponseVOS = venueFeeResponseVO.getData();
            Optional.ofNullable(venueFeeResponseVOS).ifPresent(venueWinLossAgentVOList -> {
                Map<String, List<ReportVenueWinLossAgentVO>> map = venueWinLossAgentVOList.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getVenueCode));
                ResponseVO<List<VenueInfoVO>> playVenueInfoRsp = playVenueInfoApi.venueInfoByCodeIds(venueWinLossAgentVOList.stream().map(ReportVenueWinLossAgentVO::getVenueCode).distinct().toList());
                List<VenueInfoVO> venueInfoRspData = playVenueInfoRsp.getData();
                //场馆名称取venuePlatformName
                Map<String, VenueInfoVO> venueInfoMap = venueInfoRspData.stream().collect(
                        Collectors.toMap(VenueInfoVO::getVenueCode, v -> v, (existing, replacement) -> existing));
                map.forEach((venueCode, list) -> {
                    CommissionVenueFeeVO commissionVenueFeeVO = rateMap.get(venueCode);
                    AgentVenueFeeInfoResVO feeInfoResVO = new AgentVenueFeeInfoResVO();
                    feeInfoResVO.setVenueCode(venueCode);
                    feeInfoResVO.setVenueRate(new BigDecimal(commissionVenueFeeVO.getRate()));
                    feeInfoResVO.setVenueValidRate(new BigDecimal(commissionVenueFeeVO.getValidRate()));
                    feeInfoResVO.setCurrency(targetCurrency);
                    Optional.ofNullable(venueInfoMap.get(venueCode))
                            .map(VenueInfoVO::getVenuePlatformName)
                            .ifPresent(feeInfoResVO::setVenuePlatformName);
                    resultList.add(feeInfoResVO);
                    AtomicReference<BigDecimal> validAmount = new AtomicReference<>(BigDecimal.ZERO);
                    Map<String, List<ReportVenueWinLossAgentVO>> currencyGroupMap = list.stream().collect(Collectors.groupingBy(ReportVenueWinLossAgentVO::getCurrency));
                    currencyGroupMap.forEach((currency, gourpList) -> {
                        gourpList.forEach(venueFeeVO -> {
                            BigDecimal winLoseAmount = venueFeeVO.getWinLoseAmount();
                            BigDecimal validBetAmount = venueFeeVO.getValidBetAmount();
                            BigDecimal rate = allFinalRate.get(currency);
                            if (targetCurrency.equals(currency)) {
                                // 目前币种一致 不转换
                                feeInfoResVO.setTotalWinLoseAmount(feeInfoResVO.getTotalWinLoseAmount().add(winLoseAmount));
                                validAmount.set(validAmount.get().add(validBetAmount));
                            } else if (targetCurrency.equals(CommonConstant.PLAT_CURRENCY_CODE)) {
                                // 平台币
                                BigDecimal winLossAmount = AmountUtils.divide2(winLoseAmount, rate);
                                validAmount.set(validAmount.get().add(AmountUtils.divide2(validBetAmount, rate)));
                                feeInfoResVO.setTotalWinLoseAmount(feeInfoResVO.getTotalWinLoseAmount().add(winLossAmount));
                            } else {
                                BigDecimal targetRate = allFinalRate.get(targetCurrency);
                                BigDecimal winLossAmount = AmountUtils.transfer2(winLoseAmount, rate, targetRate);
                                validAmount.set(validAmount.get().add(AmountUtils.transfer2(validBetAmount, rate, targetRate)));
                                // 转换为目标币种
                                feeInfoResVO.setTotalWinLoseAmount(feeInfoResVO.getTotalWinLoseAmount().add(winLossAmount));
                            }
                        });
                    });
                    feeInfoResVO.setVenueValid(validAmount.get());
                    BigDecimal winLossAmount = feeInfoResVO.getTotalWinLoseAmount();
                    //总输赢*负盈利费率 +有效流水*有效流水费率=场馆费
                    if (winLossAmount.compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal rate = feeInfoResVO.getVenueRate();
                        BigDecimal fee = winLossAmount.divide(CommonConstant.DECIMAL_100).multiply(rate).setScale(4, RoundingMode.DOWN);
                        feeInfoResVO.setFeeAmount(feeInfoResVO.getFeeAmount().add(fee));
                    }
                    if (validAmount.get().compareTo(BigDecimal.ZERO) > 0) {
                        BigDecimal rate = feeInfoResVO.getVenueValidRate();
                        BigDecimal fee = validAmount.get().divide(CommonConstant.DECIMAL_100).multiply(rate).setScale(4, RoundingMode.DOWN);
                        feeInfoResVO.setFeeAmount(feeInfoResVO.getFeeAmount().add(fee));
                    }
                });

            });
        }
        return resultList;
    }


    public List<AgentDepositWithdrawFeeInfoResVO> depositWithdrawFeeInfo(AgentDepositWithdrawFeeInfoReqVO vo) {
        boolean isWtc = vo.getCurrencyCode().equals(CommonConstant.PLAT_CURRENCY_CODE);
        // 记录当前代理
        String currAgentId = vo.getAgentId();
        List<String> agentIds = List.of(currAgentId);
        if (vo.getSelf().equals(CommonConstant.business_two)) {
            List<AgentInfoVO> allChildAgents = agentInfoService.findAllChildAgents(currAgentId);
            agentIds = allChildAgents.stream().map(AgentInfoVO::getAgentId).toList();
        }
        Map<String, ReportUserRechargePayMethodAgentVO> payMethodMap = Maps.newHashMap();
        String targetCurrency = vo.getCurrencyCode();
        // 汇率
        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(vo.getSiteCode());
        // 查询手续费与存/取款金额 会员
        ReportUserRechargePayMethodAgentReqVO rechargeAgentReqVO =
                new ReportUserRechargePayMethodAgentReqVO()
                        .setSiteCode(vo.getSiteCode())
                        .setAgentIds(agentIds)
                        .setType(vo.getType())
                        .setStartTime(vo.getStartTime())
                        .setEndTime(vo.getEndTime());
        if (!isWtc) rechargeAgentReqVO.setCurrency(vo.getCurrencyCode());
        ResponseVO<List<ReportUserRechargePayMethodAgentVO>> rechargeAgentResponseVO = reportUserRechargeApi.queryPayMethodByTimeAndAgent(rechargeAgentReqVO);
        if (rechargeAgentResponseVO.isOk()) {
            List<ReportUserRechargePayMethodAgentVO> data = rechargeAgentResponseVO.getData();
            BigDecimal targetRate = allFinalRate.get(targetCurrency);
            payMethodMap = data.stream().peek(s -> {
                BigDecimal amount = s.getAmount();
                String currency = s.getCurrency();
                BigDecimal currRate = allFinalRate.get(currency);
                BigDecimal settlementFeeAmount = s.getSettlementFeeAmount();
                if (targetCurrency.equals(currency)) {
                    // 目前币种一致 不转换
                } else if (targetCurrency.equals(CommonConstant.PLAT_CURRENCY_CODE)) {
                    // 平台币
                    s.setAmount(AmountUtils.divide(amount, currRate));
                    s.setSettlementFeeAmount(AmountUtils.divide(settlementFeeAmount, currRate));
                } else {
                    // 转换为目标币种
                    s.setAmount(AmountUtils.transfer(amount, currRate, targetRate));
                    s.setSettlementFeeAmount(AmountUtils.transfer(settlementFeeAmount, currRate, targetRate));
                }
            }).collect(Collectors.toMap(ReportUserRechargePayMethodAgentVO::getPayMethodId, p -> p, (k1, k2) -> k2));
        }
        // 存取款  存提手续费 代理
        AgentDepositWithdrawFeeVO feeVO = new AgentDepositWithdrawFeeVO()
                .setAgentIds(agentIds)
                .setStartTime(vo.getStartTime())
                .setEndTime(vo.getEndTime())
                .setType(vo.getType())
                .setSiteCode(vo.getSiteCode())
                .setStatus(DepositWithdrawalOrderStatusEnum.SUCCEED.getCode());
        if (!isWtc) feeVO.setCurrency(vo.getCurrencyCode());
        List<ReportRechargeAgentVO> agentFeeList = agentDepositWithdrawService.queryAgentDepositWithdrawFeeByWay(feeVO);
        if (CollUtil.isNotEmpty(agentFeeList)) {
            for (ReportRechargeAgentVO s : agentFeeList) {
                BigDecimal targetRate = allFinalRate.get(targetCurrency);
                BigDecimal amount;
                if (vo.getType().equals(CommonConstant.business_one)) {
                    if (isWtc) {
                        amount = s.getRechargeAmount();
                    } else {
                        amount = s.getFiatRechargeAmount();
                    }
                } else {
                    if (isWtc) {
                        amount = s.getWithdrawAmount();
                    } else {
                        amount = s.getFiatWithdrawAmount();
                    }
                }
                payMethodMap.putIfAbsent(s.getDepositWithdrawWayId(), new ReportUserRechargePayMethodAgentVO());
                ReportUserRechargePayMethodAgentVO methodAgentVO = payMethodMap.get(s.getDepositWithdrawWayId());
                String currency = s.getCurrency();
                BigDecimal currRate = allFinalRate.get(currency);
                BigDecimal settlementFeeAmount = s.getSettlementFeeAmount();
                if (targetCurrency.equals(currency)) {
                    // 目前币种一致 不转换
                    methodAgentVO.setAmount(methodAgentVO.getAmount().add(amount));
                    methodAgentVO.setSettlementFeeAmount(methodAgentVO.getSettlementFeeAmount().add(settlementFeeAmount));
                } else if (targetCurrency.equals(CommonConstant.PLAT_CURRENCY_CODE)) {
                    // 平台币
                    methodAgentVO.setAmount(methodAgentVO.getAmount().add(amount));
                    methodAgentVO.setSettlementFeeAmount(methodAgentVO.getSettlementFeeAmount().add(AmountUtils.divide(settlementFeeAmount, currRate)));
                }
            }
        }
        List<AgentDepositWithdrawFeeInfoResVO> resultList = Lists.newArrayList();
        Map<String, ReportUserRechargePayMethodAgentVO> finalPayMethodMap = payMethodMap;
        // 存款
        if (vo.getType().equals(CommonConstant.business_one)) {
            ResponseVO<List<SiteRechargeWayResVO>> rechargeResponseVO = siteRechargeWayApi.queryBySite();
            if (rechargeResponseVO.isOk()) {
                List<SiteRechargeWayResVO> data = rechargeResponseVO.getData();
                if (CollUtil.isNotEmpty(data)) {

                    resultList = data.stream().map(s -> {
                        AgentDepositWithdrawFeeInfoResVO feeInfoResVO = new AgentDepositWithdrawFeeInfoResVO();
                        String rechargeWayI18 = s.getRechargeWayI18();
                        feeInfoResVO.setPaymentMethodName(rechargeWayI18);
                        feeInfoResVO.setPaymentMethodName(rechargeWayI18);
                        feeInfoResVO.setCurrency(targetCurrency);
                        ReportUserRechargePayMethodAgentVO methodAgentVO = finalPayMethodMap.get(s.getPayMethodId());
                        Optional.ofNullable(methodAgentVO).ifPresent(o -> {
                            feeInfoResVO.setAmount(o.getAmount());
                            feeInfoResVO.setFeeAmount(o.getSettlementFeeAmount());
                        });
                        return feeInfoResVO;
                    }).toList();
                }
            }
        } else {
            ResponseVO<List<SiteWithdrawWayResVO>> withdrawResponseVO = siteWithdrawWayApi.queryBySite();
            if (withdrawResponseVO.isOk()) {
                List<SiteWithdrawWayResVO> data = withdrawResponseVO.getData();
                resultList = data.stream().map(s -> {
                    AgentDepositWithdrawFeeInfoResVO feeInfoResVO = new AgentDepositWithdrawFeeInfoResVO();
                    String withdrawWayI18 = s.getWithdrawWayI18();
                    feeInfoResVO.setPaymentMethodName(withdrawWayI18);
                    feeInfoResVO.setCurrency(targetCurrency);
                    ReportUserRechargePayMethodAgentVO methodAgentVO = finalPayMethodMap.get(s.getWithdrawId());
                    Optional.ofNullable(methodAgentVO).ifPresent(o -> {
                        feeInfoResVO.setAmount(o.getAmount());
                        feeInfoResVO.setFeeAmount(o.getSettlementFeeAmount());
                    });
                    return feeInfoResVO;
                }).toList();
            }
        }
        return resultList;
    }
}
