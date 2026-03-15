package com.cloud.baowang.agent.service.commission;

import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountVo;
import com.cloud.baowang.agent.api.vo.agent.commission.AgentValidAmountCommonVo;
import com.cloud.baowang.agent.api.vo.agent.commission.ValidAmountInfo;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionPlanTurnoverConfigVo;
import com.cloud.baowang.agent.api.vo.commission.CommissionPlanTurnoverConfigVO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.AgentInfoRelationPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionReviewRecordPO;
import com.cloud.baowang.agent.service.AgentInfoRelationService;
import com.cloud.baowang.agent.service.AgentInfoService;
import com.cloud.baowang.agent.service.rebate.AgentRebateConfigService;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.user.api.api.SiteUserLabelConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.SiteVipChangeRecordCnApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AgentCommissionDailyCalcService {

    private final AgentCommissionReviewService agentCommissionReviewService;

    private final AgentRebateConfigService agentRebateConfigService;

    private final AgentInfoApi agentInfoApi;

    private final SiteCurrencyInfoApi currencyInfoApi;

    private final UserInfoApi userInfoApi;

    private final SiteUserLabelConfigApi siteUserLabelConfigApi;

    private final SiteVipChangeRecordCnApi siteVipChangeRecordCnApi;

    private final AgentInfoService agentInfoService;

    private final AgentInfoRelationService agentRelationService;

    private final AgentCommissionVenueService agentCommissionVenueService;

    private final ReportUserRechargeApi reportUserRechargeApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    private final AgentCommissionPlanTurnoverService commissionPlanService;


    private String currencyCode;

    private Integer venueType;

    private long now;


    /**
     * 根据venueType生成
     *
     * @param rebateInitVO
     * @return
     */
    public void handleCalcAgentCommission(AgentValidAmountVo rebateInitVO) {
        Long startTime = rebateInitVO.getStartTime();
        Long endTime = rebateInitVO.getEndTime();
        String siteCode = rebateInitVO.getSiteCode();
        List<AgentValidAmountCommonVo> records = rebateInitVO.getRecords();
        Long flushTime = rebateInitVO.getFlushTime();
        now = System.currentTimeMillis();

        if (records.isEmpty()) {
            log.info("当前站点:{},日期:{}:无有效流水", siteCode, startTime);
            return;
        }
        long auditNum = agentCommissionReviewService.checkCommissionStatus(siteCode, startTime);
        if (auditNum >= 1) {
            log.info("当前站点:{},日期:{}佣金存在审核情况,不能再次计算", siteCode, startTime);
            return;
        }
        this.currencyCode = rebateInitVO.getCurrencyCode();
        this.venueType = rebateInitVO.getVenueType();
        if (flushTime != null) {
            // 佣金审核列表是叠加,计算是按照游戏大类,不能删
//            clearOldUserRebateInfo(rebateInitVO);
        }
        log.info(" handleCalcAgentCommission : startTime:{},endTime:{},siteCode:{}", startTime, endTime, siteCode);

        Map<String, BigDecimal> allFinalRate = currencyInfoApi.getAllFinalRate(siteCode);

        Map<String, AgentValidAmountCommonVo> agentMap = records.stream().collect(Collectors.toMap(
                        AgentValidAmountCommonVo::getAgentId,
                        Function.identity()));

        Set<String> agentIds = agentMap.keySet();
        //这里已经排序,根据distance降序(必须排)
        List<AgentInfoPO> agentInfos = agentInfoService.getByAgentIds(siteCode, agentIds);

        // agent-佣金 的map
        Map<String, ValidAmountInfo> validMap = new HashMap<>();

        for (AgentInfoPO agentInfo : agentInfos) {
            BigDecimal agentCommission = BigDecimal.ZERO;

            String agentId = agentInfo.getAgentId();
            AgentValidAmountCommonVo validAmountVo = agentMap.get(agentId);
            if (validAmountVo == null) {
                continue;
            }

            BigDecimal totalValid = validAmountVo.getValidAmount();
            String planCode = agentInfo.getPlanCode();
            // 直属方案（按直属流水）
            CommissionPlanTurnoverConfigVO selfPlan = commissionPlanService.getCommissionPlanForCalc(planCode, venueType, currencyCode, totalValid);

            if (currencyCode.equals("KVND")){
                log.info("agent_commission_plan_turnover - selfPlan:{}",selfPlan);
            }
            if (selfPlan == null || selfPlan.getRate() ==null) {
                log.info("统计佣金-直属方案为空 agentId={}, totalValid={}", agentId, totalValid);

                continue;
            }

            // 保证上级一定能拿到
            validMap.putIfAbsent(agentId, ValidAmountInfo.builder().amount(totalValid).plan(selfPlan).build());

            // 查直属下级
            List<AgentInfoRelationPO> childNodes = agentRelationService.selectByParentId(agentId);

            if (childNodes == null || childNodes.isEmpty()) {
                //  直属佣金按直属方案
                BigDecimal rate = selfPlan.getRate().divide(new BigDecimal(10000),6,RoundingMode.HALF_UP);
                agentCommission = validAmountVo.getValidAmount().multiply(rate);
                BigDecimal wtcAmount = buildWTCAmount(agentCommission, currencyCode, agentInfo.getSiteCode(), allFinalRate);
                //佣金报表要的
                agentCommissionVenueService.insertAgentVenueCommission(totalValid,agentInfo,now,startTime,endTime,wtcAmount,venueType, 0,rate,BigDecimal.ZERO);
                insertOrUpdate(wtcAmount, agentInfo, startTime, endTime);
                continue;
            }

            // 汇总团队流水（自己直属 + 所有直属下级的团队流水）
            BigDecimal teamValid = totalValid;
            for (AgentInfoRelationPO childNode : childNodes) {
                String childId = childNode.getDescendantAgentId();
                ValidAmountInfo childInfo = validMap.get(childId);
                if (childInfo == null) {
                    // 关键：这里不能影响父档位，你必须确认排序/数据完整
                    log.error("缺少childInfo会导致父档位错误! parentId={}, childId={}", agentId, childId);
                    continue; //
                }
                teamValid = teamValid.add(childInfo.getAmount());
            }

            // 父方案（按团队流水）
            CommissionPlanTurnoverConfigVO parentPlan = commissionPlanService.getCommissionPlanForCalc(planCode, venueType, currencyCode, totalValid);
            if (parentPlan == null) {
                log.info("统计佣金-父级方案为空 agentId={}, teamValid={}", agentId, teamValid);
                continue;
            }

            //  该代理的“团队流水+团队档位”
            validMap.put(agentId, ValidAmountInfo.builder().amount(teamValid).plan(parentPlan).build());

            // 父的直属佣金：按父团队档位
//            BigDecimal rate = calcVenueTypeRate(venueType, validAmountVo, parentPlan);
            BigDecimal rate = parentPlan.getRate();

            agentCommission = validAmountVo.getValidAmount().multiply(rate);
            // 梯度：只算直属下级团队流水 * (父rate - 子rate)
            for (AgentInfoRelationPO childNode : childNodes) {
                String childId = childNode.getDescendantAgentId();
                ValidAmountInfo childInfo = validMap.get(childId);
                if (childInfo == null || childInfo.getPlan() == null) {
                    log.error("child缺方案 不能参与父团队档位: parentId={}, childId={}", agentId, childId);
                    continue;
                }

                BigDecimal selfRate = childInfo.getPlan().getRate().divide(new BigDecimal(10000),6,RoundingMode.HALF_UP);

                BigDecimal diffRate = parentPlan.getRate().subtract(childInfo.getPlan().getRate())
                        .divide(new BigDecimal(10000),6,RoundingMode.HALF_UP);
                if (diffRate.compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                BigDecimal diffValidAmount = childInfo.getAmount().multiply(diffRate);
                BigDecimal diffWTCAmount = buildWTCAmount(diffValidAmount, currencyCode, agentInfo.getSiteCode(), allFinalRate);
                //佣金报表-明细
                agentCommissionVenueService.insertAgentVenueCommission(totalValid,agentInfo,now,startTime,endTime,diffWTCAmount,venueType,1,selfRate,diffRate);

                agentCommission = agentCommission.add(childInfo.getAmount().multiply(diffRate));
            }
            BigDecimal wtcAmount = buildWTCAmount(agentCommission, currencyCode, agentInfo.getSiteCode(), allFinalRate);
            insertOrUpdate(wtcAmount, agentInfo, startTime, endTime);
        }

    }




    public void insertOrUpdate(BigDecimal wtcAmount, AgentInfoPO agentInfo, long startTime, long endTime) {

        AgentCommissionReviewRecordPO exist = agentCommissionReviewService.checkAgentCommissionExist(agentInfo.getSiteCode(), agentInfo.getAgentId(), startTime);
        if (exist != null) {
            //单个场馆的叠加
            exist.setCommissionAmount(exist.getCommissionAmount().add(wtcAmount));
            agentCommissionReviewService.updateById(exist);
            return;
        }

        agentCommissionReviewService.insertOrUpdateAgentCommission(agentInfo,now,startTime,endTime,wtcAmount);

    }




    public void clearOldUserRebateInfo( AgentValidAmountVo rebateInitVO) {
        try {
            Long startTime = rebateInitVO.getStartTime();
            Long endTime = rebateInitVO.getEndTime();
            String timeZoneStr = rebateInitVO.getTimeZoneStr();
            String siteCode = rebateInitVO.getSiteCode();
            //不能删
//            agentCommissionReviewService.clearUserRebateRecord(startTime,endTime, timeZoneStr, siteCode);
//            agentCommissionVenueService.clearAgentVenueCommission(startTime,endTime, venueType, siteCode);
        } catch (Exception e) {
            log.error(" 清空佣金审核列表失败 : " + e.getMessage());
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
    }


    private BigDecimal buildWTCAmount(BigDecimal agentCommission, String currencyCode, String siteCode, Map<String, BigDecimal> allFinalRate) {
        BigDecimal rate = allFinalRate.get(currencyCode);
        if (rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
            log.error("siteCode:{}, currencyCode:{} rate invalid:{}", siteCode, currencyCode, rate);
            return BigDecimal.ZERO;
        }
        return AmountUtils.divide(agentCommission, rate, 4);
    }

}
