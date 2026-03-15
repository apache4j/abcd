package com.cloud.baowang.agent.service;

import com.cloud.baowang.agent.api.vo.commission.AgentCommissionExpectCalcVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanTurnoverConfigVo;
import com.cloud.baowang.agent.api.vo.commission.front.AgentPersonDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentRebateDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentVenueRebateVO;
import com.cloud.baowang.agent.api.vo.commission.front.CommissionDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.CommissionGeneralVO;
import com.cloud.baowang.agent.api.vo.commission.front.CommissionStatisticsReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.FrontCommissionGroupReqVO;
import com.cloud.baowang.agent.api.vo.commission.front.NegNotSettleReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.NegProfitInfo;
import com.cloud.baowang.agent.api.vo.commission.front.NegSettledReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.PersonGeneralVO;
import com.cloud.baowang.agent.api.vo.commission.front.PersonNotSettleReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.PersonProfitInfo;
import com.cloud.baowang.agent.api.vo.commission.front.PersonSettledReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.RebateBetDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.RebateNotSettleReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.RebateSettledReportVO;
import com.cloud.baowang.agent.api.vo.commission.front.SubCommissionGeneralVO;
import com.cloud.baowang.agent.api.vo.commission.front.ValidRebateGeneralVO;
import com.cloud.baowang.agent.api.vo.commission.front.ValidRebateInfo;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionExpectReportPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.service.commission.AgentCommissionExpectCalcService;
import com.cloud.baowang.agent.service.commission.AgentCommissionFinalReportService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.agent.service.rebate.AgentRebateConfigService;
import com.cloud.baowang.agent.service.rebate.AgentRebateExpectCalcService;
import com.cloud.baowang.agent.service.rebate.AgentRebateFinalReportService;
import com.cloud.baowang.agent.api.enums.commission.AgentCommissionStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionReviewOrderStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/12/04 17:13
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class CommissionStatisticsService {
    private final AgentInfoService agentInfoService;
    private final AgentCommissionExpectCalcService expectCalcService;
    private final AgentRebateExpectCalcService rebateExpectCalcService;
    private final AgentRebateFinalReportService agentRebateFinalReportService;
    private final AgentCommissionFinalReportService finalReportService;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentRebateConfigService agentRebateConfigService;
    /**
     * 获取某个时间范围内总代的负盈利佣金数据
     * @return
     */
    public NegProfitInfo getNegProfitInfoReport(CommissionStatisticsReqVO reqVO) {
        NegProfitInfo negProfitInfo = new NegProfitInfo();
        NegNotSettleReportVO notSettleReportVO = new NegNotSettleReportVO();
        NegSettledReportVO settledReportVO = new NegSettledReportVO();

        Long calcStartTime = reqVO.getCalcStartTime();
        Long calcEndTime = reqVO.getCalcEndTime();
        Long reportStartTime = reqVO.getReportStartTime();
        Long reportEndTime = reqVO.getReportEndTime();
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());

        BigDecimal notSettleCommission = new BigDecimal("0.0000");  //未发放佣金
        BigDecimal commissionTotal = new BigDecimal("0.0000"); //已发放佣金
        BigDecimal reviewAdjustAmount = new BigDecimal("0.0000"); //佣金调整金额
        Integer activeValidNumber = 0; //有效活跃
        Integer newActiveNumber = 0; //有效新增
        BigDecimal userWinLossTotal = new BigDecimal("0.0000"); //总输赢
        BigDecimal discountUsed = new BigDecimal("0.0000"); //已使用优惠
        BigDecimal lastMonthRemain = new BigDecimal("0.0000"); //待冲正金额
        BigDecimal accessFee = new BigDecimal("0.0000"); //总存取手续费
        BigDecimal venueFee = new BigDecimal("0.0000"); //场馆费
        BigDecimal agentRate = new BigDecimal("0.0000"); //返佣比例
        BigDecimal adjustAmount = new BigDecimal("0.0000"); //调整金额
        BigDecimal tipsAmount = new BigDecimal("0.0000"); //打赏金额
        BigDecimal betWinLoss = new BigDecimal("0.0000"); //会员输赢

        //当期是否有数据
        boolean hasExpectFlag=false;

        BigDecimal negCommissionAmount = new BigDecimal("0.0000");
        if (calcStartTime > 0 && calcEndTime > 0) {
            hasExpectFlag=true;
            AgentCommissionExpectCalcVO calcVO = new AgentCommissionExpectCalcVO();
            calcVO.setAgentIds(List.of(agentInfoPO.getAgentId()));
            calcVO.setSiteCode(agentInfoPO.getSiteCode());
            calcVO.setStartTime(calcStartTime);
            calcVO.setEndTime(calcEndTime);
            List<AgentCommissionExpectReportPO> expectReportPOList = expectCalcService.calcCommissionByTime(calcVO);

            AgentCommissionExpectReportPO expectReportPO = expectReportPOList.get(0);
            notSettleCommission = notSettleCommission.add(expectReportPO.getCommissionAmount());
            negCommissionAmount = negCommissionAmount.add(expectReportPO.getCommissionAmount());
            activeValidNumber += expectReportPO.getActiveNumber();
            newActiveNumber += expectReportPO.getNewValidNumber();
            userWinLossTotal = expectReportPO.getUserWinLossTotal().add(userWinLossTotal);
            discountUsed = expectReportPO.getTransferAmount().add(discountUsed);
            lastMonthRemain = expectReportPO.getLastMonthRemain().add(lastMonthRemain);
            accessFee = expectReportPO.getAccessFee().add(accessFee);
            venueFee = expectReportPO.getVenueFee().add(venueFee);
            agentRate = expectReportPO.getAgentRate();
            adjustAmount=expectReportPO.getAdjustAmount().add(adjustAmount);
            tipsAmount=expectReportPO.getTipsAmount().add(tipsAmount);
            betWinLoss=expectReportPO.getBetWinLoss().add(betWinLoss);
        }

        //统计当前周期外未审核的部分
        if (reportEndTime > 0 && reportStartTime > 0) {
            FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
            reportReqVO.setAgentAccount(agentInfoPO.getAgentAccount());
            reportReqVO.setSiteCode(reqVO.getSiteCode());
            reportReqVO.setStartTime(reportStartTime);
            reportReqVO.setEndTime(reportEndTime);
           /* reportReqVO.setStatusList(
                    List.of(CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode(),
                    CommissionReviewOrderStatusEnum.ONE_REVIEWING.getCode(),
                            CommissionReviewOrderStatusEnum.REVIEW_FAIL.getCode())
            );*/
            //是否是总代
            boolean ifTop1Agent=false;
            if(StringUtils.isEmpty(agentInfoPO.getParentAccount())){
                ifTop1Agent=true;
            }
            // 总代查询未发放的 子代不过滤
            if(ifTop1Agent){
                reportReqVO.setStatusList(List.of(AgentCommissionStatusEnum.PENDING.getCode()));
            }
            List<CommissionDetailVO> commissionDetailVOS = finalReportService.getFinalReportListGroupAgentId(reportReqVO);
            if (commissionDetailVOS != null && !commissionDetailVOS.isEmpty()) {
                CommissionDetailVO detailVO = commissionDetailVOS.get(0);
                notSettleCommission = notSettleCommission.add(detailVO.getCommissionAmount());
                negCommissionAmount = negCommissionAmount.add(detailVO.getCommissionAmount());
                accessFee = accessFee.add(detailVO.getAccessFee());
                activeValidNumber += detailVO.getActiveValidNumber();
                newActiveNumber += detailVO.getNewActiveNumber();
                discountUsed = discountUsed.add(detailVO.getDiscountUsed());
                userWinLossTotal = userWinLossTotal.add(detailVO.getUserWinLossTotal());
                venueFee = venueFee.add(detailVO.getVenueFee());
                adjustAmount=adjustAmount.add(detailVO.getAdjustAmount());
                tipsAmount=tipsAmount.add(detailVO.getTipsAmount());
                betWinLoss=betWinLoss.add(detailVO.getBetWinLoss());

                FrontCommissionGroupReqVO vo = new FrontCommissionGroupReqVO();
                vo.setAgentId(agentInfoPO.getAgentId());
                vo.setStartTime(reportStartTime);
                vo.setEndTime(reportEndTime);
               /* vo.setStatusList(
                        List.of(CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode(),
                                CommissionReviewOrderStatusEnum.ONE_REVIEWING.getCode(),
                                CommissionReviewOrderStatusEnum.REVIEW_FAIL.getCode())
                );*/
                // 总代查询未发放的 子代不过滤
                if(ifTop1Agent){
                    reportReqVO.setStatusList(List.of(AgentCommissionStatusEnum.PENDING.getCode()));
                }
                AgentCommissionFinalReportPO reportPO = finalReportService.getLatestReportByTime(vo);
                if (reportPO != null) {
                    if(!hasExpectFlag){
                        agentRate=reportPO.getAgentRate();
                        lastMonthRemain=reportPO.getLastMonthRemain();
                    }
                }
            }
        }


        CommissionGeneralVO commissionGeneralVO = new CommissionGeneralVO();
        notSettleReportVO.setNotSettleCommission(notSettleCommission);
        commissionGeneralVO.setCommissionAmount(negCommissionAmount);
        commissionGeneralVO.setSettleCycle(reqVO.getSettleCycle());
        commissionGeneralVO.setStartTime(reqVO.getStartTime());
        commissionGeneralVO.setEndTime(reqVO.getEndTime());
        commissionGeneralVO.setAgentRate(agentRate);
        commissionGeneralVO.setAccessFee(accessFee);
        commissionGeneralVO.setActiveValidNumber(activeValidNumber);
        commissionGeneralVO.setSettleCycle(reqVO.getSettleCycle());
        commissionGeneralVO.setDiscountUsed(discountUsed);
        commissionGeneralVO.setLastMonthRemain(lastMonthRemain);
        commissionGeneralVO.setNewActiveNumber(newActiveNumber);
        commissionGeneralVO.setUserWinLossTotal(userWinLossTotal);
        commissionGeneralVO.setVenueFee(venueFee);
        commissionGeneralVO.setAdjustAmount(adjustAmount);
        commissionGeneralVO.setTipsAmount(tipsAmount);
        commissionGeneralVO.setBetWinLoss(betWinLoss);
        notSettleReportVO.setCommissionGeneralVO(commissionGeneralVO);

        negProfitInfo.setNotSettleReportVO(notSettleReportVO);

        //已发放部分
        CommissionGeneralVO settleCommission = new CommissionGeneralVO();
        settleCommission.setSettleCycle(reqVO.getSettleCycle());
        settleCommission.setStartTime(reqVO.getStartTime());
        settleCommission.setEndTime(reqVO.getEndTime());
        settleCommission.setAgentRate(new BigDecimal("0.000"));

        if (reportEndTime > 0 && reportStartTime > 0) {
            FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
            reportReqVO.setAgentAccount(agentInfoPO.getAgentAccount());
            reportReqVO.setSiteCode(reqVO.getSiteCode());
            reportReqVO.setStartTime(reportStartTime);
            reportReqVO.setEndTime(reportEndTime);
            settleCommission.setSettleCycle(reqVO.getSettleCycle());
           // reportReqVO.setStatusList(List.of(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode()));

            //是否是总代
            boolean ifTop1Agent=false;
            if(StringUtils.isEmpty(agentInfoPO.getParentAccount())){
                ifTop1Agent=true;
            }
            // 总代查询未发放的 子代不过滤
            if(ifTop1Agent){
                reportReqVO.setStatusList(List.of(AgentCommissionStatusEnum.RECEIVED.getCode()));
            }
            List<CommissionDetailVO> commissionDetailVOS = finalReportService.getFinalReportListGroupAgentId(reportReqVO);
            if (commissionDetailVOS != null && commissionDetailVOS.size() >0) {
                CommissionDetailVO detailVO = commissionDetailVOS.get(0);
                commissionTotal = commissionTotal.add(detailVO.getCommissionAmount());
                reviewAdjustAmount=reviewAdjustAmount.add(detailVO.getReviewAdjustAmount());
                settleCommission.setCommissionAmount(commissionTotal);
                settleCommission.setAccessFee(detailVO.getAccessFee());
                settleCommission.setActiveValidNumber(detailVO.getActiveValidNumber());
                settleCommission.setDiscountUsed(detailVO.getDiscountUsed());
                settleCommission.setNewActiveNumber(detailVO.getNewActiveNumber());
                settleCommission.setUserWinLossTotal(detailVO.getUserWinLossTotal());
                settleCommission.setVenueFee(detailVO.getVenueFee());
                settleCommission.setAdjustAmount(detailVO.getAdjustAmount());
                settleCommission.setTipsAmount(detailVO.getTipsAmount());
                settleCommission.setBetWinLoss(detailVO.getBetWinLoss());

                FrontCommissionGroupReqVO vo = new FrontCommissionGroupReqVO();
                vo.setAgentId(agentInfoPO.getAgentId());
                vo.setStartTime(reportStartTime);
                vo.setEndTime(reportEndTime);
                vo.setStatusList(List.of(AgentCommissionStatusEnum.RECEIVED.getCode()));
                AgentCommissionFinalReportPO reportPO = finalReportService.getLatestReportByTime(vo);
                if (reportPO != null) {
                    settleCommission.setAgentRate(reportPO.getAgentRate());
                    settleCommission.setLastMonthRemain(reportPO.getLastMonthRemain());
                }
            }
        }
        settledReportVO.setReviewAdjustAmount(reviewAdjustAmount);
        settledReportVO.setCommissionTotal(commissionTotal);
        settledReportVO.setCommissionGeneralVO(settleCommission);
        negProfitInfo.setSettledReportVO(settledReportVO);

        return negProfitInfo;
    }

    /**
     * 获取某个时间范围内总代的返点数据
     * @return
     */
    public ValidRebateInfo getValidRebateInfoReport(CommissionStatisticsReqVO reqVO) {
        ValidRebateInfo validRebateInfo = new ValidRebateInfo();
        RebateNotSettleReportVO rebateNotSettleReportVO = new RebateNotSettleReportVO();
        RebateSettledReportVO rebateSettledReportVO = new RebateSettledReportVO();
        ValidRebateGeneralVO validRebateGeneralVO = new ValidRebateGeneralVO();
        ValidRebateGeneralVO settleValidRebateGeneralVO = new ValidRebateGeneralVO();

        Long calcStartTime = reqVO.getCalcStartTime();
        Long calcEndTime = reqVO.getCalcEndTime();
        Long reportStartTime = reqVO.getReportStartTime();
        Long reportEndTime = reqVO.getReportEndTime();
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());

        BigDecimal notSettleCommission = new BigDecimal("0.0000");  //未发放佣金
        BigDecimal commissionTotal = new BigDecimal("0.0000"); //已发放佣金
        BigDecimal reviewAdjustAmount = new BigDecimal("0.0000"); //返点调整金额

        validRebateGeneralVO.setStartTime(calcStartTime);
        validRebateGeneralVO.setEndTime(calcEndTime);
        validRebateGeneralVO.setCommissionAmount(notSettleCommission);
        validRebateGeneralVO.setSettleCycle(reqVO.getSettleCycle());

        //实时计算部分
        if (calcStartTime > 0 && calcEndTime > 0) {
            AgentCommissionExpectCalcVO calcVO = new AgentCommissionExpectCalcVO();
            calcVO.setAgentIds(List.of(agentInfoPO.getAgentId()));
            calcVO.setSiteCode(agentInfoPO.getSiteCode());
            calcVO.setStartTime(calcStartTime);
            calcVO.setEndTime(calcEndTime);
            List<AgentRebateDetailVO> rebateDetailVOList = rebateExpectCalcService.calcRebateByTime(calcVO);
            AgentRebateDetailVO agentRebateDetailVO = rebateDetailVOList.get(0);
            notSettleCommission = notSettleCommission.add(agentRebateDetailVO.getRebateAmount());
            Map<Integer, BigDecimal> betMap = agentRebateDetailVO.getDataList().stream()
                    .collect(Collectors.toMap(AgentVenueRebateVO::getVenueType, AgentVenueRebateVO::getValidAmount, (k1, k2) -> k2));
            validRebateGeneralVO.setCommissionAmount(notSettleCommission);
            validRebateGeneralVO.setChessAmount(betMap.get(VenueTypeEnum.CHESS.getCode()));
            validRebateGeneralVO.setCockfightAmount(betMap.get(VenueTypeEnum.COCKFIGHTING.getCode()));
            validRebateGeneralVO.setEsportsAmount(betMap.get(VenueTypeEnum.ELECTRONIC_SPORTS.getCode()));
            validRebateGeneralVO.setLiveAmount(betMap.get(VenueTypeEnum.SH.getCode()));
            validRebateGeneralVO.setLotteryAmount(betMap.get(VenueTypeEnum.ACELT.getCode()));
            validRebateGeneralVO.setSlotAmount(betMap.get(VenueTypeEnum.ELECTRONICS.getCode()));
            validRebateGeneralVO.setSportsAmount(betMap.get(VenueTypeEnum.SPORTS.getCode()));
            validRebateGeneralVO.setFishAmount(betMap.get(VenueTypeEnum.FISHING.getCode()));
            validRebateGeneralVO.setMarblesAmount(betMap.get(VenueTypeEnum.MARBLES.getCode()));
        }

        //统计除当前期以外的未发放部分
        FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
        reportReqVO.setAgentAccount(agentInfoPO.getAgentAccount());
        reportReqVO.setSiteCode(reqVO.getSiteCode());
        reportReqVO.setStartTime(reportStartTime);
        reportReqVO.setEndTime(reportEndTime);
        reportReqVO.setCommissionType(CommissionTypeEnum.REBATE.getCode());
        reportReqVO.setStatusList(List.of(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode(), CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode(),
                CommissionReviewOrderStatusEnum.FIRST_REVIEW_APPROVED.getCode(), CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode(),
                CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode(), CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode(),
                CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode()
                )
        );
        List<RebateBetDetailVO> rebateBetDetailVOS = agentRebateFinalReportService.getFinalReportListGroupAgentId(reportReqVO);

        if (rebateBetDetailVOS != null && rebateBetDetailVOS.size() > 0) {
            Map<Integer, RebateBetDetailVO> validMap = rebateBetDetailVOS.stream()
                    .collect(Collectors.toMap(RebateBetDetailVO::getVenueType, p -> p, (k1, k2) -> k2));
            //如果类型返水数据不存在, 补 BigDecimal.ZERO
            VenueTypeEnum.getList().forEach(venueTypeEnum -> {
                if (!validMap.containsKey(venueTypeEnum.getCode())){
                    validMap.put(venueTypeEnum.getCode(), RebateBetDetailVO.builder().validAmount(BigDecimal.ZERO).build());
                }
            });

            notSettleCommission = notSettleCommission.add(rebateBetDetailVOS.stream().map(RebateBetDetailVO::getCommissionAmount).reduce(new BigDecimal("0.0000"), BigDecimal::add));
            validRebateGeneralVO.setCommissionAmount(notSettleCommission);
            validRebateGeneralVO.setChessAmount(validRebateGeneralVO.getChessAmount().add(validMap.get(VenueTypeEnum.CHESS.getCode()).getValidAmount()));
            validRebateGeneralVO.setCockfightAmount(validRebateGeneralVO.getCockfightAmount().add(validMap.get(VenueTypeEnum.COCKFIGHTING.getCode()).getValidAmount()));
            validRebateGeneralVO.setEsportsAmount(validRebateGeneralVO.getEsportsAmount().add(validMap.get(VenueTypeEnum.ELECTRONIC_SPORTS.getCode()).getValidAmount()));
            validRebateGeneralVO.setLiveAmount(validRebateGeneralVO.getLiveAmount().add(validMap.get(VenueTypeEnum.SH.getCode()).getValidAmount()));
            validRebateGeneralVO.setLotteryAmount(validRebateGeneralVO.getLotteryAmount().add(validMap.get(VenueTypeEnum.ACELT.getCode()).getValidAmount()));
            validRebateGeneralVO.setSlotAmount(validRebateGeneralVO.getSlotAmount().add(validMap.get(VenueTypeEnum.ELECTRONICS.getCode()).getValidAmount()));
            validRebateGeneralVO.setSportsAmount(validRebateGeneralVO.getSportsAmount().add(validMap.get(VenueTypeEnum.SPORTS.getCode()).getValidAmount()));
            validRebateGeneralVO.setFishAmount(validRebateGeneralVO.getFishAmount().add(validMap.get(VenueTypeEnum.FISHING.getCode()).getValidAmount()));
            validRebateGeneralVO.setMarblesAmount(validRebateGeneralVO.getMarblesAmount().add(validMap.get(VenueTypeEnum.MARBLES.getCode()).getValidAmount()));
        }

        validRebateGeneralVO.setStartTime(reqVO.getStartTime());
        validRebateGeneralVO.setEndTime(reqVO.getEndTime());
        rebateNotSettleReportVO.setNotSettleCommission(notSettleCommission);
        rebateNotSettleReportVO.setValidRebateGeneralVO(validRebateGeneralVO);

        //统计已发放部分
        if (reportStartTime > 0 && reportEndTime > 0) {
            FrontCommissionGroupReqVO settleReqVO = new FrontCommissionGroupReqVO();
            settleReqVO.setAgentAccount(agentInfoPO.getAgentAccount());
            settleReqVO.setSiteCode(reqVO.getSiteCode());
            settleReqVO.setStartTime(reportStartTime);
            settleReqVO.setEndTime(reportEndTime);
            settleReqVO.setCommissionType(CommissionTypeEnum.REBATE.getCode());
            settleReqVO.setStatusList(List.of(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode()));
            List<RebateBetDetailVO> settleRebateDetailList = agentRebateFinalReportService.getFinalReportListGroupAgentId(settleReqVO);
            if (settleRebateDetailList != null && settleRebateDetailList.size() > 0) {
                Map<Integer, RebateBetDetailVO> validMap = settleRebateDetailList.stream()
                        .collect(Collectors.toMap(RebateBetDetailVO::getVenueType, p -> p, (k1, k2) -> k2));
                //如果类型返水数据不存在, 补 BigDecimal.ZERO
                VenueTypeEnum.getList().forEach(venueTypeEnum -> {
                    if (!validMap.containsKey(venueTypeEnum.getCode())){
                        validMap.put(venueTypeEnum.getCode(), RebateBetDetailVO.builder().validAmount(BigDecimal.ZERO).build());
                    }
                });

                //commissionTotal = commissionTotal.add(settleRebateDetailList.stream().map(RebateBetDetailVO::getCommissionAmount).reduce(new BigDecimal("0.0000"), BigDecimal::add));
                commissionTotal =settleRebateDetailList.get(0).getCommissionAmount();
                settleValidRebateGeneralVO.setCommissionAmount(commissionTotal);
                reviewAdjustAmount=settleRebateDetailList.get(0).getRebateAdjustAmount();
                settleValidRebateGeneralVO.setChessAmount(validMap.get(VenueTypeEnum.CHESS.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setCockfightAmount(validMap.get(VenueTypeEnum.COCKFIGHTING.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setEsportsAmount(validMap.get(VenueTypeEnum.ELECTRONIC_SPORTS.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setLiveAmount(validMap.get(VenueTypeEnum.SH.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setLotteryAmount(validMap.get(VenueTypeEnum.ACELT.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setSlotAmount(validMap.get(VenueTypeEnum.ELECTRONICS.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setSportsAmount(validMap.get(VenueTypeEnum.SPORTS.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setFishAmount(validMap.get(VenueTypeEnum.FISHING.getCode()).getValidAmount());
                settleValidRebateGeneralVO.setMarblesAmount(validMap.get(VenueTypeEnum.MARBLES.getCode()).getValidAmount());

            }
        }
        settleValidRebateGeneralVO.setStartTime(reqVO.getStartTime());
        settleValidRebateGeneralVO.setEndTime(reqVO.getEndTime());
        rebateSettledReportVO.setReviewAdjustAmount(reviewAdjustAmount);
        rebateSettledReportVO.setCommissionTotal(commissionTotal);
        rebateSettledReportVO.setValidRebateGeneralVO(settleValidRebateGeneralVO);
        validRebateInfo.setNotSettleReportVO(rebateNotSettleReportVO);
        validRebateInfo.setSettledReportVO(rebateSettledReportVO);

        return validRebateInfo;
    }

    /**
     * 获取某个时间范围内总代的人头费数据
     * @return
     */
    public PersonProfitInfo getPersonProfitInfoReport(CommissionStatisticsReqVO reqVO) {
        PersonProfitInfo personProfitInfo = new PersonProfitInfo();
        PersonNotSettleReportVO personNotSettleReportVO = new PersonNotSettleReportVO();
        PersonSettledReportVO personSettledReportVO = new PersonSettledReportVO();
        PersonGeneralVO notSettlePersonGeneralVO = new PersonGeneralVO();
        PersonGeneralVO settlePersonGeneralVO = new PersonGeneralVO();

        Long calcStartTime = reqVO.getCalcStartTime();
        Long calcEndTime = reqVO.getCalcEndTime();
        Long reportStartTime = reqVO.getReportStartTime();
        Long reportEndTime = reqVO.getReportEndTime();
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());
        List<String> agentIdList = agentInfoService.getSubAgentIdList(agentInfoPO.getAgentId());

        String planCode = agentInfoPO.getPlanCode();
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
        AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
        Integer settleCycle = rebateConfigVO.getSettleCycle();


        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(agentInfoPO.getSiteCode());
        if (calcStartTime > 0 && calcEndTime > 0) {
            AgentActiveUserReqVO activeUserReqVO = new AgentActiveUserReqVO();
            activeUserReqVO.setAgentIds(agentIdList);
            activeUserReqVO.setStartTime(calcStartTime);
            activeUserReqVO.setEndTime(calcEndTime);
            activeUserReqVO.setSiteCode(reqVO.getSiteCode());
            activeUserReqVO.setPlanCode(agentInfoPO.getPlanCode());
            AgentActiveUserResponseVO activeUserResponseVO = expectCalcService.getAgentActiveUserInfo(agentInfoPO.getAgentId(), activeUserReqVO, currencyRateMap);
            if (activeUserResponseVO != null) {
                notSettlePersonGeneralVO.setNewActiveNumber(activeUserResponseVO.getNewValidNumber());
                BigDecimal newUserAmount = rebateConfigVO.getNewUserAmount().multiply(new BigDecimal(activeUserResponseVO.getNewValidNumber())).setScale(4, RoundingMode.DOWN) ;
                notSettlePersonGeneralVO.setCommissionAmount(newUserAmount);
            }
        }

        //统计除当前期以为的未发放部分
        FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
        reportReqVO.setAgentId(agentInfoPO.getAgentId());
        reportReqVO.setSiteCode(reqVO.getSiteCode());
        reportReqVO.setStartTime(reportStartTime);
        reportReqVO.setEndTime(reportEndTime);
        reportReqVO.setCommissionType(CommissionTypeEnum.ADDING.getCode());
        reportReqVO.setStatusList(List.of(CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode(), CommissionReviewOrderStatusEnum.FIRST_REVIEW_IN_PROGRESS.getCode(),
                        CommissionReviewOrderStatusEnum.FIRST_REVIEW_APPROVED.getCode(), CommissionReviewOrderStatusEnum.FIRST_REVIEW_REJECTED.getCode(),
                        CommissionReviewOrderStatusEnum.PENDING_SECOND_REVIEW.getCode(), CommissionReviewOrderStatusEnum.SECOND_REVIEW_IN_PROGRESS.getCode(),
                        CommissionReviewOrderStatusEnum.SECOND_REVIEW_REJECTED.getCode()
                )
        );
        List<AgentPersonDetailVO> agentPersonDetailVOS = agentRebateFinalReportService.getPersonAmountGroupAgentId(reportReqVO);
        if (agentPersonDetailVOS != null && agentPersonDetailVOS.size() > 0) {
            AgentPersonDetailVO agentPersonDetailVO = agentPersonDetailVOS.get(0);
            notSettlePersonGeneralVO.setCommissionAmount(notSettlePersonGeneralVO.getCommissionAmount().add(agentPersonDetailVO.getCommissionAmount()));
            notSettlePersonGeneralVO.setNewActiveNumber(notSettlePersonGeneralVO.getNewActiveNumber() + agentPersonDetailVO.getNewActiveNumber());
        }

        personNotSettleReportVO.setNotSettleCommission(notSettlePersonGeneralVO.getCommissionAmount());
        notSettlePersonGeneralVO.setSettleCycle(reqVO.getSettleCycle());
        notSettlePersonGeneralVO.setStartTime(reqVO.getStartTime());
        notSettlePersonGeneralVO.setEndTime(reqVO.getEndTime());
        notSettlePersonGeneralVO.setNewUserAmount(rebateConfigVO.getNewUserAmount());
        personNotSettleReportVO.setPersonGeneralVO(notSettlePersonGeneralVO);

        //已结算部分
        if (reportStartTime > 0 && reportEndTime > 0) {
            FrontCommissionGroupReqVO settleReq = new FrontCommissionGroupReqVO();
            settleReq.setAgentId(agentInfoPO.getAgentId());
            settleReq.setSiteCode(reqVO.getSiteCode());
            settleReq.setStartTime(reportStartTime);
            settleReq.setEndTime(reportEndTime);
            settleReq.setCommissionType(CommissionTypeEnum.ADDING.getCode());
            settleReq.setStatusList(List.of(CommissionReviewOrderStatusEnum.REVIEW_SUCCESS.getCode()));
            List<AgentPersonDetailVO> settleDetailList = agentRebateFinalReportService.getPersonAmountGroupAgentId(settleReq);
            if (settleDetailList != null && settleDetailList.size() > 0) {
                AgentPersonDetailVO agentPersonDetailVO = settleDetailList.get(0);
                settlePersonGeneralVO.setCommissionAmount(agentPersonDetailVO.getCommissionAmount()==null?new BigDecimal("0.0000"):agentPersonDetailVO.getCommissionAmount().setScale(4, RoundingMode.DOWN));
                settlePersonGeneralVO.setReviewAdjustAmount(agentPersonDetailVO.getReviewAdjustAmount());
                settlePersonGeneralVO.setNewActiveNumber(agentPersonDetailVO.getNewActiveNumber());
            }
        }

        settlePersonGeneralVO.setSettleCycle(reqVO.getSettleCycle());
        settlePersonGeneralVO.setStartTime(reqVO.getStartTime());
        settlePersonGeneralVO.setEndTime(reqVO.getEndTime());

        settlePersonGeneralVO.setNewUserAmount(rebateConfigVO.getNewUserAmount()==null?new BigDecimal("0.0000"):rebateConfigVO.getNewUserAmount().setScale(4, RoundingMode.DOWN));
        personSettledReportVO.setPersonGeneralVO(settlePersonGeneralVO);
        personSettledReportVO.setCommissionTotal(settlePersonGeneralVO.getCommissionAmount());
        personSettledReportVO.setReviewAdjustAmount(settlePersonGeneralVO.getReviewAdjustAmount());

        personProfitInfo.setSettledReportVO(personSettledReportVO);
        personProfitInfo.setNotSettleReportVO(personNotSettleReportVO);

        return personProfitInfo;
    }

    /**
     * 子代获取佣金数据
     */
    public SubCommissionGeneralVO getSubCommissionGeneralVO(CommissionStatisticsReqVO reqVO) {
        Long calcStartTime = reqVO.getCalcStartTime();
        Long calcEndTime = reqVO.getCalcEndTime();
        Long reportStartTime = reqVO.getReportStartTime();
        Long reportEndTime = reqVO.getReportEndTime();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());
        List<String> agentIdList = agentInfoService.getSubAgentIdList(agentInfoPO.getAgentId());

        Integer activeValidNumber = 0;  //有效活跃
        Integer newActiveNumber = 0; //有效新增
        BigDecimal userWinLossTotal = new BigDecimal("0.0000"); //总输赢
        BigDecimal validAmount = new BigDecimal("0.0000"); //有效流水
        BigDecimal discountUsed = new BigDecimal("0.0000"); //已使用优惠
        BigDecimal accessFee = new BigDecimal("0.0000"); //总存取手续费
        BigDecimal venueFee = new BigDecimal("0.0000"); //场馆费
        BigDecimal discountAmount  = new BigDecimal("0.0000"); //活动优惠
        BigDecimal vipAmount  = new BigDecimal("0.0000"); //vip福利
        BigDecimal tipsAmount  = new BigDecimal("0.0000"); //打赏金额
        BigDecimal betWinLoss  = new BigDecimal("0.0000"); //会员输赢
        //先计算实时部分
        if (calcStartTime > 0 && calcEndTime > 0) {
            AgentCommissionExpectCalcVO calcVO = new AgentCommissionExpectCalcVO();
            calcVO.setAgentIds(List.of(agentInfoPO.getAgentId()));
            calcVO.setSiteCode(agentInfoPO.getSiteCode());
            calcVO.setStartTime(calcStartTime);
            calcVO.setEndTime(calcEndTime);
            List<AgentCommissionExpectReportPO> reportPOList = expectCalcService.calcCommissionByTime(calcVO);
            if (reportPOList != null && reportPOList.size() > 0) {
                AgentCommissionExpectReportPO reportPO = reportPOList.get(0);
                activeValidNumber += reportPO.getActiveNumber();
                newActiveNumber += reportPO.getNewValidNumber();
                userWinLossTotal = userWinLossTotal.add(reportPO.getUserWinLossTotal());
                validAmount = validAmount.add(reportPO.getValidBetAmount());
                discountUsed = discountUsed.add(reportPO.getTransferAmount());
                accessFee = accessFee.add(reportPO.getAccessFee());
                venueFee = venueFee.add(reportPO.getVenueFee());
                discountAmount = discountAmount.add(reportPO.getDiscountAmount());
                vipAmount = vipAmount.add(reportPO.getVipAmount());
                tipsAmount = tipsAmount.add(reportPO.getTipsAmount());
                betWinLoss = betWinLoss.add(reportPO.getBetWinLoss());
            }
        }

        //统计历史期的数据
        if (reportEndTime > 0 && reportStartTime > 0) {
            FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
            reportReqVO.setAgentAccount(agentInfoPO.getAgentAccount());
            reportReqVO.setSiteCode(reqVO.getSiteCode());
            reportReqVO.setStartTime(reportStartTime);
            reportReqVO.setEndTime(reportEndTime);
            List<CommissionDetailVO> list = finalReportService.getStatisticsByAgentId(reportReqVO);
            if (list != null && list.size() > 0) {
                CommissionDetailVO detailVO = list.get(0);
                activeValidNumber += detailVO.getActiveValidNumber();
                newActiveNumber += detailVO.getNewActiveNumber();
                userWinLossTotal = userWinLossTotal.add(detailVO.getUserWinLossTotal());
                validAmount = validAmount.add(detailVO.getValidBetAmount());
                discountUsed = discountUsed.add(detailVO.getDiscountUsed());
                accessFee = accessFee.add(detailVO.getAccessFee());
                venueFee = venueFee.add(detailVO.getVenueFee());
                discountAmount = discountAmount.add(detailVO.getDiscountAmount());
                vipAmount = vipAmount.add(detailVO.getVipAmount());
                tipsAmount = tipsAmount.add(detailVO.getTipsAmount());
                betWinLoss = betWinLoss.add(detailVO.getBetWinLoss());
            }
        }

        SubCommissionGeneralVO subCommissionGeneralVO = new SubCommissionGeneralVO();
        subCommissionGeneralVO.setAgentAccount(agentInfoPO.getAgentAccount());
        subCommissionGeneralVO.setVipAmount(vipAmount);
        subCommissionGeneralVO.setDiscountAmount(discountAmount);
        subCommissionGeneralVO.setUserWinLossTotal(userWinLossTotal);
        subCommissionGeneralVO.setVenueFee(venueFee);
        subCommissionGeneralVO.setValidAmount(validAmount);
        subCommissionGeneralVO.setAccessFee(accessFee);
        subCommissionGeneralVO.setDiscountUsed(discountUsed);
        subCommissionGeneralVO.setNewActiveNumber(newActiveNumber);
        subCommissionGeneralVO.setActiveValidNumber(activeValidNumber);
        subCommissionGeneralVO.setTipsAmount(tipsAmount);
        subCommissionGeneralVO.setBetWinLoss(betWinLoss);

        return subCommissionGeneralVO;
    }
}
