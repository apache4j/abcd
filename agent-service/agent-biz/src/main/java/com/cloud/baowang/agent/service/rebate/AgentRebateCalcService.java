package com.cloud.baowang.agent.service.rebate;

import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import com.cloud.baowang.agent.po.commission.AgentRebateFinalReportPO;
import com.cloud.baowang.agent.po.commission.AgentRebateReportDetailPO;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.commission.*;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionReviewOrderStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.OrderUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.enums.UserManualUpReviewNumberEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: fangfei
 * @createTime: 2024/11/12 21:18
 * @description: 返点/人头费结算
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentRebateCalcService {
    private final AgentInfoService agentInfoService;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentRebateConfigService agentRebateConfigService;
    private final AgentRebateFinalReportService agentRebateFinalReportService;
    private final AgentRebateReportDetailService agentRebateReportDetailService;
    private final AgentCommissionCalcService agentCommissionCalcService;
    private final AgentCommissionReviewService agentCommissionReviewService;
    private final AgentCommissionReviewRecordService agentCommissionReviewRecordService;
    private final AgentValidUserRecordService agentValidUserRecordService;

    public Map<Integer, String> getRebateRateMap(AgentCommissionPlanTurnoverConfigVo rebateConfigVO) {
        Map<Integer, String> map = new HashMap<>();
        VenueTypeEnum[] types = VenueTypeEnum.values();
        for (VenueTypeEnum type : types) {
            if (type.getCode().equals(VenueTypeEnum.SPORTS.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getSportsRate());
            } else if (type.getCode().equals(VenueTypeEnum.SH.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getLiveRate());
            } else if (type.getCode().equals(VenueTypeEnum.CHESS.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getChessRate());
            } else if (type.getCode().equals(VenueTypeEnum.ELECTRONICS.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getSlotRate());
            } else if (type.getCode().equals(VenueTypeEnum.ACELT.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getLotteryRate());
            } else if (type.getCode().equals(VenueTypeEnum.COCKFIGHTING.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getCockfightRate());
            } else if (type.getCode().equals(VenueTypeEnum.ELECTRONIC_SPORTS.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getEsportsRate());
            }else if (type.getCode().equals(VenueTypeEnum.FISHING.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getFishRate());
            }else if (type.getCode().equals(VenueTypeEnum.MARBLES.getCode())) {
                map.put(type.getCode(), rebateConfigVO.getMarblesRebate());
            } else {
                //不存在默认为0
                map.put(type.getCode(), "0");
            }
        }

        return map;
    }

    public void agentRebateGenerate(AgentCommissionCalcVO calcVO) {
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());
        List<AgentInfoPO> infoList = agentCommissionPlanService.getAgentByRebateCycle(calcVO.getSiteCode(), calcVO.getSettleCycle());
        if(null != infoList && !infoList.isEmpty()){
            infoList = infoList.stream().sorted(Comparator.comparing(AgentInfoPO::getLevel)).collect(Collectors.toList());

            String zoneId = calcVO.getTimeZone();
            Long startTime = 0L;
            Long endTime = 0L;
            if (calcVO.getIsManual() != null && calcVO.getIsManual() == 1) {
                startTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getStartTime().trim(), zoneId);
                endTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getEndTime().trim() + ".999", zoneId);
            } else {
                if (calcVO.getSettleCycle().equals(SettleCycleEnum.DAY.getCode())) {
                    startTime = TimeZoneUtils.getStartOfYesterdayInTimeZone(System.currentTimeMillis(), zoneId);
                    endTime = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), zoneId);
                } else if (calcVO.getSettleCycle().equals(SettleCycleEnum.WEEK.getCode())) {
                    startTime = TimeZoneUtils.getStartOfLastWeekInTimeZone(System.currentTimeMillis(), zoneId);
                    endTime = TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), zoneId);
                } else {
                    startTime = TimeZoneUtils.getLastMonStartTimeInTimeZone(zoneId);
                    endTime = TimeZoneUtils.getLastMonEndTimeInTimeZone(zoneId);
                }
            }


            ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
            paramVO.setStartTime(startTime);
            paramVO.setEndTime(endTime);
            List<ReportAgentVenueStaticsVO> staticsList = reportUserVenueWinLoseApi.getUserVenueAmountGroupByAgent(paramVO);
            //全部转换为平台币
            for (ReportAgentVenueStaticsVO staticsVO : staticsList) {
                BigDecimal rate = currencyRateMap.get(staticsVO.getCurrency());
                BigDecimal validAmount = AmountUtils.divide(staticsVO.getValidBetAmount(), rate);
                BigDecimal winLossAmount = AmountUtils.divide(staticsVO.getWinLossAmount(), rate);
                staticsVO.setValidBetAmount(validAmount);
                staticsVO.setWinLossAmount(winLossAmount);
            }
            for (AgentInfoPO infoPO : infoList) {
                BigDecimal rebateAmountTotal = BigDecimal.ZERO;
                String agentId=infoPO.getAgentId();

                //获取总代ID
                String oneAgentId=getOneAgentId(infoPO);

                AgentInfoPO superPO = agentInfoService.getById(infoPO.getParentId() == null ? -9999L : infoPO.getParentId());
                List<String> agentIdList = agentInfoService.getSubAgentIdList(infoPO.getAgentId());

                //已审核的不能重新结算
                //已审核的不能重新结算
                ReviewRecordReqVO reviewRecordReqVO = new ReviewRecordReqVO();
                reviewRecordReqVO.setAgentId(infoPO.getAgentId());
                reviewRecordReqVO.setStartTime(startTime);
                reviewRecordReqVO.setEndTime(endTime);
                reviewRecordReqVO.setCommissionTypeList(List.of(CommissionTypeEnum.REBATE.getCode(), CommissionTypeEnum.ADDING.getCode()));
                List<AgentCommissionReviewRecordPO> oldReviewPOList = agentCommissionReviewRecordService.getReviewRecordByReportId(reviewRecordReqVO);
                if (oldReviewPOList != null && !oldReviewPOList.isEmpty()) {
                    boolean hasReview = false;
                    for (AgentCommissionReviewRecordPO review : oldReviewPOList) {
                        if (!CommissionReviewOrderStatusEnum.PENDING_FIRST_REVIEW.getCode().equals(review.getOrderStatus())) {
                            hasReview = true;
                            break;
                        }
                    }
                    if (hasReview) {
                        log.info("代理返点和人头费本期有不在待审核的记录，禁止重新结算");
                        continue;
                    }
                }



                String planCode = infoPO.getPlanCode();
                AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
                AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
                Map<Integer, String> rateMap = getRebateRateMap(rebateConfigVO);


                Map<Integer, BigDecimal> amountMap = staticsList.stream().filter(s -> agentIdList.contains(s.getAgentId()))
                        .collect(Collectors.groupingBy(
                                ReportAgentVenueStaticsVO::getVenueType,
                                Collectors.reducing(BigDecimal.ZERO, ReportAgentVenueStaticsVO::getValidBetAmount, BigDecimal::add)
                        ));

                String id = SnowFlakeUtils.getSnowId();
                List<AgentRebateReportDetailPO> detailPOList = new ArrayList<>();
                VenueTypeEnum[] types = VenueTypeEnum.values();
                for (VenueTypeEnum type : types) {
                    AgentRebateReportDetailPO detailPO = new AgentRebateReportDetailPO();
                    String rate = rateMap.get(type.getCode());
                    BigDecimal rateAmount =  BigDecimal.ZERO;
                    BigDecimal amount = amountMap.get(type.getCode());
                    if (amount == null) amount = BigDecimal.ZERO;
                    if (StringUtils.hasText(rate)) rateAmount = new BigDecimal(rate);
                    BigDecimal rebateAmount =AmountUtils.multiplyPercent(amount,rateAmount);;
                    rebateAmountTotal = rebateAmountTotal.add(rebateAmount);
                    detailPO.setValidAmount(amount);
                    detailPO.setVenueType(type.getCode());
                    detailPO.setRebateRate(rate);
                    detailPO.setRebateAmount(rebateAmount);
                    detailPO.setRebateReportId(id);
                    detailPOList.add(detailPO);
                    log.info("有效流水计算,代理:{},场馆类型:{},计算金额:{}",agentId,type,rebateAmount);
                }

                AgentRebateFinalReportPO finalReportPO = new AgentRebateFinalReportPO();
                finalReportPO.setId(id);
                finalReportPO.setAgentId(infoPO.getAgentId());
                finalReportPO.setAgentAccount(infoPO.getAgentAccount());
                finalReportPO.setAgentLevel(infoPO.getLevel());
                finalReportPO.setSuperAgentId(infoPO.getParentId());
                finalReportPO.setOneAgentId(oneAgentId);
                finalReportPO.setRebateAmount(rebateAmountTotal);
                finalReportPO.setAgentType(infoPO.getAgentType());
                finalReportPO.setPlanCode(planVO.getPlanCode());
                finalReportPO.setSiteCode(infoPO.getSiteCode());
                finalReportPO.setRiskLevelId(infoPO.getRiskLevelId());
                finalReportPO.setStartTime(startTime);
                finalReportPO.setEndTime(endTime);
                finalReportPO.setEveryUserAmount(rebateConfigVO.getNewUserAmount());
                finalReportPO.setSettleCycle(rebateConfigVO.getSettleCycle());
                if (superPO != null) {
                    finalReportPO.setSuperAgentId(superPO.getAgentId());
                }

                //计算有效新增、人头费
                AgentActiveUserReqVO reqVO = new AgentActiveUserReqVO();
                reqVO.setStartTime(startTime);
                reqVO.setEndTime(endTime);
                reqVO.setAgentIds(agentIdList);
                reqVO.setSiteCode(infoPO.getSiteCode());
                reqVO.setPlanCode(planCode);
                //先删除同一期的数据
                agentValidUserRecordService.deleteHis(infoPO.getAgentId(), finalReportPO.getStartTime(), finalReportPO.getEndTime(), CommissionTypeEnum.ADDING.getCode());
                AgentActiveUserResponseVO activeUserResponseVO = agentCommissionCalcService.getAgentActiveUserInfo(infoPO.getAgentId(), reqVO, currencyRateMap, CommissionTypeEnum.ADDING.getCode());
                BigDecimal newUserAmount = rebateConfigVO.getNewUserAmount().multiply(new BigDecimal(activeUserResponseVO.getNewValidNumber()));
                finalReportPO.setNewUserAmount(newUserAmount);
                finalReportPO.setNewValidNumber(activeUserResponseVO.getNewValidNumber());

                AgentRebateFinalReportPO oldReport = agentRebateFinalReportService.getReportByAgentId(infoPO.getAgentId(), endTime);
                if (oldReport != null) {
                    agentRebateFinalReportService.removeById(oldReport.getId());
                    agentRebateReportDetailService.deleteByReportId(oldReport.getId());
                }
                agentRebateFinalReportService.save(finalReportPO);
                agentRebateReportDetailService.saveBatch(detailPOList);

                //保存有效新增会员记录
                if (activeUserResponseVO.getValidUserIdList() != null && !activeUserResponseVO.getValidUserIdList().isEmpty()) {
                    //先删除同一期的数据
                    //agentValidUserRecordService.deleteHis(infoPO.getAgentId(), finalReportPO.getStartTime(), finalReportPO.getEndTime(), CommissionTypeEnum.ADDING.getCode());
                    AgentValidUserRecordVO agentValidUserRecordVO = new AgentValidUserRecordVO();
                    BeanUtils.copyProperties(finalReportPO, agentValidUserRecordVO);
                    agentValidUserRecordVO.setReportId(finalReportPO.getId());
                    agentValidUserRecordVO.setCommissionType(CommissionTypeEnum.ADDING.getCode());
                    agentValidUserRecordService.saveSettleUser(activeUserResponseVO.getValidUserIdList(), agentValidUserRecordVO);
                }

                if (infoPO.getParentId() == null || infoPO.getLevel() == 1) {
                    if (rebateAmountTotal.compareTo(BigDecimal.ZERO) > 0) {
                        AgentCommissionReviewRecordPO reviewRecordPO = new AgentCommissionReviewRecordPO();
                        reviewRecordPO.setAgentId(infoPO.getAgentId());
                        reviewRecordPO.setAgentName(infoPO.getName());
                        reviewRecordPO.setAgentAccount(infoPO.getAgentAccount());
                        reviewRecordPO.setReportId(finalReportPO.getId());
                        reviewRecordPO.setAgentStatus(infoPO.getStatus());
                        reviewRecordPO.setCommissionType(CommissionTypeEnum.REBATE.getCode());
                        reviewRecordPO.setCommissionAmount(rebateAmountTotal);
                        reviewRecordPO.setApplyAmount(rebateAmountTotal);
                        reviewRecordPO.setStartTime(startTime);
                        reviewRecordPO.setEndTime(endTime);
                        reviewRecordPO.setSiteCode(infoPO.getSiteCode());
                        reviewRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                        reviewRecordPO.setApplyTime(System.currentTimeMillis());
                        reviewRecordPO.setSettleCycle(rebateConfigVO.getSettleCycle());
                        reviewRecordPO.setOrderNo(OrderUtil.getBatchNo("Y"));
                        reviewRecordPO.setOrderStatus(UserManualUpReviewNumberEnum.WAIT_ONE_REVIEW.getCode());
                        reviewRecordPO.setSettleStatus(0);
                        reviewRecordPO.setLockStatus(0);

                        if (oldReport != null) {
                            agentCommissionReviewService.deleteByReportId(oldReport.getId());
                            reviewRecordPO.setSettleStatus(1);
                            reviewRecordPO.setSettleTime(System.currentTimeMillis());
                        }
                        agentCommissionReviewService.save(reviewRecordPO);
                    }

                    if (newUserAmount.compareTo(BigDecimal.ZERO) > 0) {
                        AgentCommissionReviewRecordPO reviewRecordPO = new AgentCommissionReviewRecordPO();
                        reviewRecordPO.setAgentId(infoPO.getAgentId());
                        reviewRecordPO.setAgentName(infoPO.getName());
                        reviewRecordPO.setAgentAccount(infoPO.getAgentAccount());
                        reviewRecordPO.setReportId(finalReportPO.getId());
                        reviewRecordPO.setAgentStatus(infoPO.getStatus());
                        reviewRecordPO.setCommissionType(CommissionTypeEnum.ADDING.getCode());
                        reviewRecordPO.setCommissionAmount(newUserAmount);
                        reviewRecordPO.setApplyAmount(newUserAmount);
                        reviewRecordPO.setStartTime(startTime);
                        reviewRecordPO.setEndTime(endTime);
                        reviewRecordPO.setSiteCode(infoPO.getSiteCode());
                        reviewRecordPO.setCurrency(CommonConstant.PLAT_CURRENCY_CODE);
                        reviewRecordPO.setApplyTime(System.currentTimeMillis());
                        reviewRecordPO.setSettleCycle(rebateConfigVO.getSettleCycle());
                        reviewRecordPO.setOrderNo(OrderUtil.getBatchNo("Y"));
                        reviewRecordPO.setOrderStatus(UserManualUpReviewNumberEnum.WAIT_ONE_REVIEW.getCode());
                        reviewRecordPO.setSettleStatus(0);
                        reviewRecordPO.setLockStatus(0);
                        if (oldReport != null) {
                            agentCommissionReviewService.deleteByReportId(oldReport.getId());
                            reviewRecordPO.setSettleStatus(1);
                            reviewRecordPO.setSettleTime(System.currentTimeMillis());
                        }
                        agentCommissionReviewService.save(reviewRecordPO);
                    }

                }

            }
        }
    }

    /**
     * 获取总代Id
     * @param agentInfoPO
     * @return
     */
    private String getOneAgentId(AgentInfoPO agentInfoPO) {
        if(!StringUtils.hasText(agentInfoPO.getParentAccount())){
            return agentInfoPO.getAgentId();
        }
        String parentAgentIdPath=agentInfoPO.getPath();
        List<AgentInfoVO> agentInfoVOS=agentInfoService.getByAgentIds(Arrays.stream(parentAgentIdPath.split(",")).toList());
        String[] agentIdArray=parentAgentIdPath.split(",");
        for(int i=0;i<agentIdArray.length-1;i++){
            String currentParentAgentId=agentIdArray[i];
            AgentInfoVO currentAgentInfo=agentInfoVOS.stream().filter(o->o.getAgentId().equals(currentParentAgentId)).findFirst().get();
            if(!StringUtils.hasText(currentAgentInfo.getParentAccount())){
                return currentAgentInfo.getAgentId();
            }
        }
        return null;

    }
}
