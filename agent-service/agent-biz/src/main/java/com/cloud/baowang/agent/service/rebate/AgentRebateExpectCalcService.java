package com.cloud.baowang.agent.service.rebate;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionCalcVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionExpectCalcVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanTurnoverConfigVo;
import com.cloud.baowang.agent.api.vo.commission.front.AgentRebateDetailVO;
import com.cloud.baowang.agent.api.vo.commission.front.AgentVenueRebateVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentRebateExpectReportPO;
import com.cloud.baowang.agent.po.commission.AgentRebateReportDetailPO;
import com.cloud.baowang.agent.repositories.AgentRebateExpectReportRepository;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.commission.AgentCommissionCalcService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserReqVO;
import com.cloud.baowang.agent.api.vo.agent.winLoss.AgentActiveUserResponseVO;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentVenueStaticsVO;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
public class AgentRebateExpectCalcService {
    private final AgentInfoService agentInfoService;
    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentRebateConfigService agentRebateConfigService;
    private final AgentRebateExpectReportService agentRebateExpectReportService;
    private final AgentRebateReportDetailService agentRebateReportDetailService;
    private final AgentCommissionCalcService agentCommissionCalcService;
    private final SiteApi siteApi;
    private final AgentRebateExpectReportRepository rebateExpectReportRepository;

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
            }else  {
                map.put(type.getCode(), "0");
            }
        }

        return map;
    }

    public void agentRebateExpectGenerate(AgentCommissionCalcVO calcVO) {

        log.info("预期返点开始结算，参数：{}", JSON.toJSONString(calcVO));
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());

        List<AgentInfoPO> infoList = agentInfoService.getAllBySiteCode(calcVO.getSiteCode());
        if (infoList == null || infoList.size() == 0) return;
        infoList = infoList.stream().sorted(Comparator.comparing(AgentInfoPO::getLevel)).collect(Collectors.toList());

        String zoneId = calcVO.getTimeZone();
        Long startTime = 0L;
        Long endTime = 0L;

        for (AgentInfoPO infoPO : infoList) {
            BigDecimal rebateAmountTotal = new BigDecimal("0.0000");

            AgentInfoPO superPO = agentInfoService.getById(infoPO.getParentId() == null ? -9999L : infoPO.getParentId());
            List<String> agentIdList = agentInfoService.getSubAgentIdList(infoPO.getAgentId());


            String planCode = infoPO.getPlanCode();
            AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
            AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
            Map<Integer, String> rateMap = getRebateRateMap(rebateConfigVO);
            Integer settleCycle = rebateConfigVO.getSettleCycle();

            if (calcVO.getIsManual() != null && calcVO.getIsManual() == 1) {
                startTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getStartTime().trim(), zoneId);
                endTime = TimeZoneUtils.convertStringToTimestamp(calcVO.getEndTime().trim() + ".999", zoneId);
            } else {
                if (settleCycle.equals(SettleCycleEnum.DAY.getCode())) {
                    startTime = TimeZoneUtils.getStartOfYesterdayInTimeZone(System.currentTimeMillis(), zoneId);
                    endTime = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), zoneId);
                } else if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
                    startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
                    endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
                } else {
                    startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
                    endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
                }
            }


            ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
            paramVO.setStartTime(startTime);
            paramVO.setEndTime(endTime);
            paramVO.setAgentIds(agentIdList);
            List<ReportAgentVenueStaticsVO> staticsList = reportUserVenueWinLoseApi.getUserVenueAmountByAgentIds(paramVO);
            //全部转换为平台币
            for (ReportAgentVenueStaticsVO staticsVO : staticsList) {
                BigDecimal rate = currencyRateMap.get(staticsVO.getCurrency());
                BigDecimal validAmount = AmountUtils.divide(staticsVO.getValidBetAmount(), rate);
                BigDecimal winLossAmount = AmountUtils.divide(staticsVO.getWinLossAmount(), rate);
                staticsVO.setValidBetAmount(validAmount);
                staticsVO.setWinLossAmount(winLossAmount);
            }

            Map<Integer, BigDecimal> amountMap = staticsList.stream()
                    .collect(Collectors.groupingBy(
                            ReportAgentVenueStaticsVO::getVenueType,
                            Collectors.reducing(new BigDecimal("0.0000"), ReportAgentVenueStaticsVO::getValidBetAmount, BigDecimal::add)
                    ));

            String id = SnowFlakeUtils.getSnowId();
            List<AgentRebateReportDetailPO> detailPOList = new ArrayList<>();
            VenueTypeEnum[] types = VenueTypeEnum.values();
            for (VenueTypeEnum type : types) {
                AgentRebateReportDetailPO detailPO = new AgentRebateReportDetailPO();
                String rate = rateMap.get(type.getCode());
                BigDecimal rateAmount = new BigDecimal("0.0000");
                BigDecimal amount = amountMap.get(type.getCode());
                if (amount == null) amount = new BigDecimal("0.0000");
                if (StringUtils.hasText(rate)) rateAmount = new BigDecimal(rate);
                BigDecimal rebateAmount =AmountUtils.multiplyPercent(amount,rateAmount);
                rebateAmountTotal = rebateAmountTotal.add(rebateAmount);
                detailPO.setValidAmount(amount);
                detailPO.setVenueType(type.getCode());
                detailPO.setRebateRate(rate);
                detailPO.setRebateAmount(rebateAmount);
                detailPO.setRebateReportId(id);
                detailPOList.add(detailPO);
            }

            AgentRebateExpectReportPO expectReportPO = new AgentRebateExpectReportPO();
            expectReportPO.setId(id);
            expectReportPO.setAgentId(infoPO.getAgentId());
            expectReportPO.setAgentAccount(infoPO.getAgentAccount());
            expectReportPO.setAgentLevel(infoPO.getLevel());
            expectReportPO.setRebateAmount(rebateAmountTotal);
            expectReportPO.setAgentType(infoPO.getAgentType());
            expectReportPO.setPlanCode(planVO.getPlanCode());
            expectReportPO.setSiteCode(infoPO.getSiteCode());
            expectReportPO.setRiskLevelId(infoPO.getRiskLevelId());
            expectReportPO.setStartTime(startTime);
            expectReportPO.setEndTime(endTime);
            expectReportPO.setEveryUserAmount(rebateConfigVO.getNewUserAmount());
            expectReportPO.setSettleCycle(rebateConfigVO.getSettleCycle());
            if (superPO != null) {
                expectReportPO.setSuperAgentId(superPO.getAgentId());
            }

            //计算有效新增、人头费
            AgentActiveUserReqVO reqVO = new AgentActiveUserReqVO();
            reqVO.setStartTime(startTime);
            reqVO.setEndTime(endTime);
            reqVO.setAgentIds(agentIdList);
            reqVO.setSiteCode(infoPO.getSiteCode());
            reqVO.setPlanCode(planCode);
            AgentActiveUserResponseVO activeUserResponseVO = agentCommissionCalcService.getAgentActiveUserInfo(infoPO.getAgentId(), reqVO, currencyRateMap, CommissionTypeEnum.ADDING.getCode());
            BigDecimal newUserAmount = rebateConfigVO.getNewUserAmount().multiply(new BigDecimal(activeUserResponseVO.getNewValidNumber())).setScale(4, RoundingMode.DOWN);
            expectReportPO.setNewUserAmount(newUserAmount);
            expectReportPO.setNewValidNumber(activeUserResponseVO.getNewValidNumber());


            LambdaQueryWrapper<AgentRebateExpectReportPO> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AgentRebateExpectReportPO::getAgentId, infoPO.getAgentId());
            queryWrapper.eq(AgentRebateExpectReportPO::getEndTime, endTime);
            AgentRebateExpectReportPO po = rebateExpectReportRepository.selectOne(queryWrapper);
            if (po == null) {
                agentRebateExpectReportService.save(expectReportPO);
                agentRebateReportDetailService.saveBatch(detailPOList);
            } else {
                agentRebateExpectReportService.removeById(po.getId());
                agentRebateReportDetailService.deleteByReportId(po.getId());
                agentRebateExpectReportService.save(expectReportPO);
                agentRebateReportDetailService.saveBatch(detailPOList);
            }

        }
    }

    public List<AgentRebateDetailVO> calcRebateByTime(AgentCommissionExpectCalcVO calcVO) {
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(calcVO.getSiteCode());

        List<AgentInfoVO> infoVOList = agentInfoService.getByAgentIds(calcVO.getAgentIds());

        SiteVO siteVO = siteApi.getSiteInfoByCode(calcVO.getSiteCode());

        String zoneId = siteVO.getTimezone();
        Long startTime = calcVO.getStartTime();
        Long endTime = calcVO.getEndTime();

        List<AgentRebateDetailVO> resultList = new ArrayList<>();
        for (AgentInfoVO infoVO : infoVOList) {
            BigDecimal rebateAmountTotal = new BigDecimal("0.0000");

            AgentInfoPO superPO = agentInfoService.getById(infoVO.getParentId() == null ? -9999L : infoVO.getParentId());
            List<String> agentIdList = agentInfoService.getSubAgentIdList(infoVO.getAgentId());


            String planCode = infoVO.getPlanCode();
            AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
            AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
            Map<Integer, String> rateMap = getRebateRateMap(rebateConfigVO);
            Integer settleCycle = rebateConfigVO.getSettleCycle();

            ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
            paramVO.setStartTime(startTime);
            paramVO.setEndTime(endTime);
            paramVO.setAgentIds(agentIdList);
            List<ReportAgentVenueStaticsVO> staticsList = reportUserVenueWinLoseApi.getUserVenueAmountByAgentIds(paramVO);
            //全部转换为平台币
            for (ReportAgentVenueStaticsVO staticsVO : staticsList) {
                BigDecimal rate = currencyRateMap.get(staticsVO.getCurrency());
                BigDecimal validAmount = AmountUtils.divide(staticsVO.getValidBetAmount(), rate);
                BigDecimal winLossAmount = AmountUtils.divide(staticsVO.getWinLossAmount(), rate);
                staticsVO.setValidBetAmount(validAmount);
                staticsVO.setWinLossAmount(winLossAmount);
            }

            Map<Integer, BigDecimal> amountMap = staticsList.stream().filter(o->o.getVenueType()!=null).filter(o->o.getValidBetAmount()!=null)
                    .collect(Collectors.groupingBy(
                            ReportAgentVenueStaticsVO::getVenueType,
                            Collectors.reducing(new BigDecimal("0.0000"), ReportAgentVenueStaticsVO::getValidBetAmount, BigDecimal::add)
                    ));

            String id = SnowFlakeUtils.getSnowId();
            List<AgentRebateReportDetailPO> detailPOList = new ArrayList<>();
            VenueTypeEnum[] types = VenueTypeEnum.values();
            for (VenueTypeEnum type : types) {
                AgentRebateReportDetailPO detailPO = new AgentRebateReportDetailPO();
                String rate = rateMap.get(type.getCode());
                BigDecimal amount = amountMap.get(type.getCode());
                BigDecimal rateDecimal=new BigDecimal("0.0000");
                if (amount == null) amount = new BigDecimal("0.0000");
                if (StringUtils.hasText(rate)) rateDecimal=new BigDecimal(rate);
                BigDecimal rebateAmount = AmountUtils.multiplyPercent(amount,rateDecimal);
                rebateAmountTotal = rebateAmountTotal.add(rebateAmount);
                detailPO.setValidAmount(amount);
                detailPO.setVenueType(type.getCode());
                detailPO.setRebateRate(rate);
                detailPO.setRebateAmount(rebateAmount);
                detailPO.setRebateReportId(id);
                log.info("场馆类型:{},有效流水返点结算金额:{}",type,rebateAmount);
                detailPOList.add(detailPO);
            }

            AgentRebateExpectReportPO expectReportPO = new AgentRebateExpectReportPO();
            expectReportPO.setId(id);
            expectReportPO.setAgentId(infoVO.getAgentId());
            expectReportPO.setAgentLevel(infoVO.getLevel());
            expectReportPO.setRebateAmount(rebateAmountTotal);
            expectReportPO.setAgentType(infoVO.getAgentType());
            expectReportPO.setPlanCode(planVO.getPlanCode());
            expectReportPO.setSiteCode(infoVO.getSiteCode());
            expectReportPO.setRiskLevelId(infoVO.getRiskLevelId());
            expectReportPO.setStartTime(startTime);
            expectReportPO.setEndTime(endTime);
            expectReportPO.setEveryUserAmount(rebateConfigVO.getNewUserAmount());
            expectReportPO.setSettleCycle(rebateConfigVO.getSettleCycle());
            if (superPO != null) {
                expectReportPO.setSuperAgentId(superPO.getAgentId());
            }

            //计算有效新增、人头费
            AgentActiveUserReqVO reqVO = new AgentActiveUserReqVO();
            reqVO.setStartTime(startTime);
            reqVO.setEndTime(endTime);
            reqVO.setAgentIds(agentIdList);
            reqVO.setSiteCode(infoVO.getSiteCode());
            reqVO.setPlanCode(planCode);
            AgentActiveUserResponseVO activeUserResponseVO = agentCommissionCalcService.getAgentActiveUserInfo(infoVO.getAgentId(), reqVO, currencyRateMap, CommissionTypeEnum.ADDING.getCode());
            BigDecimal newUserAmount = rebateConfigVO.getNewUserAmount().multiply(new BigDecimal(activeUserResponseVO.getNewValidNumber()));
            expectReportPO.setNewUserAmount(newUserAmount);

            AgentRebateDetailVO agentRebateDetailVO = new AgentRebateDetailVO();
            List<AgentVenueRebateVO> rebateList = new ArrayList<>();
            rebateList = ConvertUtil.entityListToModelList(detailPOList, AgentVenueRebateVO.class);
            agentRebateDetailVO.setAgentId(infoVO.getAgentId());
            agentRebateDetailVO.setRebateAmount(rebateAmountTotal);
            agentRebateDetailVO.setDataList(rebateList);
            resultList.add(agentRebateDetailVO);
        }

        return resultList;
    }
}
