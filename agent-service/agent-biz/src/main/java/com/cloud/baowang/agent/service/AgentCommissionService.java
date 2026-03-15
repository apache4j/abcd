package com.cloud.baowang.agent.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.agent.api.vo.agent.winLoss.*;
import com.cloud.baowang.agent.api.vo.agentinfo.AgentIdPageVO;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.agent.api.vo.commission.*;
import com.cloud.baowang.agent.api.vo.commission.front.*;
import com.cloud.baowang.agent.po.AgentCommissionPlanTurnoverConfigPO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionExpectReportPO;
import com.cloud.baowang.agent.po.commission.AgentCommissionFinalReportPO;
import com.cloud.baowang.agent.repositories.AgentCommissionPlanTurnoverConfigRepository;
import com.cloud.baowang.agent.service.commission.*;
import com.cloud.baowang.agent.service.rebate.AgentRebateConfigService;
import com.cloud.baowang.agent.service.rebate.AgentRebateExpectReportService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.agent.api.enums.SettleCycleEnum;
import com.cloud.baowang.agent.api.enums.commission.AgentCommissionStatusEnum;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.api.order.OrderRecordApi;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.vo.venue.VenueInfoVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseAgentApi;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.ReportAgentWinLossParamVO;
import com.cloud.baowang.report.api.vo.rechagerwithdraw.ReportUserAmountVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserBetAmountSumVO;
import com.cloud.baowang.report.api.vo.userwinlose.ReportUserWinLossParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseAgentReqVO;
import com.cloud.baowang.report.api.vo.userwinlose.UserWinLoseResponseVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.user.GetByUserAccountVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: fangfei
 * @createTime: 2024/06/19 17:22
 * @description:
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentCommissionService {

    private final PlayVenueInfoApi playVenueInfoApi;
    private final AgentInfoService agentInfoService;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final AgentCommissionExpectReportService agentCommissionExpectReportService;
    private final AgentRebateExpectReportService agentRebateExpectReportService;
    private final AgentCommissionLadderService agentCommissionLadderService;
    private final AgentRebateConfigService agentRebateConfigService;
    private final AgentVenueRateService agentVenueRateService;
    private final AgentCommissionFinalReportService finalReportService;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final OrderRecordApi orderRecordApi;
    private final UserInfoApi userInfoApi;
    private final SiteApi siteApi;
    private final AgentCommissionExpectCalcService expectCalcService;
    private final AgentValidUserRecordService agentValidUserRecordService;
    private final CommissionStatisticsService commissionStatisticsService;
    private final UserWinLoseApi userWinLoseApi;
    private final ReportUserWinLoseAgentApi reportUserWinLoseAgentApi;
    private final AgentCommissionPlanTurnoverConfigRepository agentCommissionPlanTurnoverConfigRepository;

    /**
     * 佣金模拟器
     */
    public BigDecimal commissionImitate(CommissionImitateCalcVO commissionImitateCalcVO) {
        if (commissionImitateCalcVO.getWinLossAmount().compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        AgentInfoVO agentInfoVO = agentInfoService.getByAgentAccountAndSiteCode(commissionImitateCalcVO.getAgentAccount(), commissionImitateCalcVO.getSiteCode());


        ResponseVO<VenueInfoVO> responseVO = playVenueInfoApi.venueInfoByVenueCode(commissionImitateCalcVO.getVenueCode(),"");
        VenueInfoVO venueInfoVO = responseVO.getData();

        //获取佣金佣金方案
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoVO.getPlanCode());

        //计算活跃人数

        //计算有效新增人数



        Random random = new Random();
        Integer amount = random.nextInt(1000);

        BigDecimal commission = new BigDecimal(amount);

        //先随机赋值
        return commission.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取代理当月佣金比例及返点比例
     * @param agentId
     */
    public RateDetailVO getRateDetail(String agentId) {
        RateDetailVO rateDetailVO = new RateDetailVO();
        AgentCommissionExpectVO agentCommissionExpectVO = agentCommissionExpectReportService.getLatestCommissionExpectReport(agentId);
        rateDetailVO.setAgentId(agentId);
        rateDetailVO.setAgentRate(agentCommissionExpectVO.getAgentRate());

        AgentRebateRateVO agentRebateRateVO = agentRebateExpectReportService.getLatestRebateDetail(agentId);
        rateDetailVO.setAgentRebateRateVO(agentRebateRateVO);

        return rateDetailVO;
    }

    /**
     * 获取佣金说明
     * @param agentId
     * @return
     */
    public AgentCommissionExplainVO getCommissionExplain(String agentId) {
        AgentCommissionExplainVO explainVO = new AgentCommissionExplainVO();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        PlanConfigVO planConfigVO = new PlanConfigVO();
        BeanUtils.copyProperties(planVO, planConfigVO);
        planConfigVO.setCurrencyUnit(CurrReqUtils.getPlatCurrencyName());
        explainVO.setPlanConfigVO(planConfigVO);

        //负盈利
        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
        FrontLadderConfigVO ladderConfig = new FrontLadderConfigVO();
        ladderConfig.setSettleCycle(ladderList.get(0).getSettleCycle());
        List<FrontLadderConfigDetailVO> ladderDetailList = new ArrayList<>();
        ladderList = ladderList.stream().sorted(Comparator.comparing(AgentCommissionLadderVO::getWinLossAmount)).collect(Collectors.toList());
        for (int i = 0; i <ladderList.size(); i++) {
            AgentCommissionLadderVO ladderVO = ladderList.get(i);
            FrontLadderConfigDetailVO detailVO = new FrontLadderConfigDetailVO();
            BigDecimal winLossAmountMin = ladderVO.getWinLossAmount();
            BigDecimal winLossAmountMax = ladderVO.getWinLossAmount();
            BigDecimal validAmountMin = ladderVO.getValidAmount();
            BigDecimal validAmountMax = ladderVO.getValidAmount();
            detailVO.setRate(ladderVO.getRate());
            detailVO.setLevelName(ladderVO.getLevelName());
            detailVO.setNewValidNumber(ladderVO.getNewValidNumber());
            detailVO.setActiveNumber(ladderVO.getActiveNumber());
            detailVO.setWinLossAmountMax(winLossAmountMax);
            detailVO.setWinLossAmountMin(winLossAmountMin);
            detailVO.setValidAmountMax(validAmountMax);
            detailVO.setValidAmountMin(validAmountMin);
            ladderDetailList.add(detailVO);
        }
        ladderConfig.setLadderConfigDetailVO(ladderDetailList);
        explainVO.setLadderConfig(ladderConfig);

        //场馆费率
        List<AgentVenueRateVO> rateList = agentVenueRateService.getListByPlanId(planVO.getId());
        if (!rateList.isEmpty()) {
            Map<String, VenueInfoVO> venueInfoVOMap = fixVenuePlatformName(rateList);
            List<CommissionVenueFeeVO> feeList = new ArrayList<>();
            rateList.forEach(r -> {
                CommissionVenueFeeVO feeVO = new CommissionVenueFeeVO();
                feeVO.setRate(r.getRate());
                feeVO.setValidRate(r.getValidRate());
                feeVO.setVenueCode(r.getVenueCode());
                // use name
                if (venueInfoVOMap != null) {
                    Optional.ofNullable(venueInfoVOMap.get(r.getVenueCode()))
                            .map(VenueInfoVO::getVenuePlatformName)
                            .ifPresent(feeVO::setVenuePlatformName);
                }
                feeList.add(feeVO);
            });
            explainVO.setVenueFeeList(feeList);
        }
        //流水返点/人头费
        FrontRebateConfigVO rebateConfig = new FrontRebateConfigVO();
        AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
        BeanUtils.copyProperties(rebateConfigVO, rebateConfig);
        explainVO.setRebateConfig(rebateConfig);
        // 1️⃣ 查询代理佣金方案下的流水配置
        String planCode = agentInfoPO.getPlanCode();
        List<AgentCommissionPlanTurnoverConfigPO> configList =
                agentCommissionPlanTurnoverConfigRepository.selectList(
                        new LambdaQueryWrapper<AgentCommissionPlanTurnoverConfigPO>()
                                .eq(AgentCommissionPlanTurnoverConfigPO::getPlanCode, planCode)
                );

        if (CollUtil.isEmpty(configList)) {
            explainVO.setValidBetAmountConfig(Collections.emptyList());
            return explainVO;
        }
        // 2️⃣ 提取配置中已有的币种
        Set<String> configCurrencies = configList.stream()
                .map(AgentCommissionPlanTurnoverConfigPO::getCurrency)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet());
        // 3️⃣ 按枚举顺序选择币种（优先 USD，其次其他）
        // 3️⃣ 按枚举顺序选择币种（优先 USDT，其次其他）
        String selectedCurrency;

        // 优先 USDT
        if (configCurrencies.contains(CurrencyEnum.USD.getCode())) {
            selectedCurrency = CurrencyEnum.USD.getCode();
        } else {
            // 没有 USD，就按枚举顺序选择第一个匹配的币种
            selectedCurrency = Arrays.stream(CurrencyEnum.values())
                    .map(CurrencyEnum::getCode)
                    .filter(configCurrencies::contains)
                    .findFirst()
                    .orElse(null);
        }

        if (selectedCurrency == null) {
            explainVO.setValidBetAmountConfig(Collections.emptyList());
            return explainVO;
        }
        // 4️⃣ 过滤当前选中的币种配置
        List<AgentCommissionPlanTurnoverConfigPO> selectedConfigs = configList.stream()
                .filter(e -> selectedCurrency.equals(e.getCurrency()))
                .toList();
        // 5️⃣ 获取站点所有币种汇率
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(agentInfoPO.getSiteCode());
        if (CollUtil.isEmpty(allFinalRate)) {
            explainVO.setValidBetAmountConfig(Collections.emptyList());
            return explainVO;
        }

        // 6️⃣ 计算平台币流水金额
        List<ValidBetAmountConfigVO> resultList = selectedConfigs.stream()
                .map(e -> {
                    ValidBetAmountConfigVO vo = new ValidBetAmountConfigVO();
                    BeanUtils.copyProperties(e, vo);

                    BigDecimal betAmount = vo.getBetAmount();
                    BigDecimal rate = allFinalRate.get(vo.getCurrency());

                    // 安全校验
                    if (betAmount == null || rate == null || rate.compareTo(BigDecimal.ZERO) <= 0) {
                        vo.setBetPlatAmount(BigDecimal.ZERO);
                    } else {
                        BigDecimal platBetAmount = betAmount.divide(
                                rate,
                                0,                     // 保留 0 位小数 = 整数
                                RoundingMode.DOWN      // 直接舍弃小数
                        );
                        vo.setBetPlatAmount(platBetAmount);
                    }
                    return vo;
                })
                .toList();

        explainVO.setValidBetAmountConfig(resultList);

        return explainVO;
    }

    public Map<String, VenueInfoVO> fixVenuePlatformName(List<AgentVenueRateVO>  records){
        ResponseVO<List<VenueInfoVO>> playVenueInfoRsp = playVenueInfoApi.venueInfoByCodeIds(records.stream().map(AgentVenueRateVO::getVenueCode).distinct().toList());
        if (!playVenueInfoRsp.isOk()){
            return null;
        }
        List<VenueInfoVO> venueInfoRspData = playVenueInfoRsp.getData();
        if (CollectionUtils.isEmpty(venueInfoRspData)){
            return null;
        }
        //场馆名称取venuePlatformName
        return venueInfoRspData.stream().collect(
                Collectors.toMap(VenueInfoVO::getVenueCode, v -> v, (k1, k2) -> k1));
    }

    public AgentCommissionDate getAgentCommissionDate(String agentId) {
        AgentCommissionDate agentCommissionDate = new AgentCommissionDate();
        agentCommissionDate.setAgentId(agentId);
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
        Integer settleCycle = ladderList.get(0).getSettleCycle();

        //确定时区
        SiteVO siteVO = siteApi.getSiteInfoByCode(agentInfoPO.getSiteCode());
        String timeZone = siteVO.getTimezone();

        if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
            agentCommissionDate.setCurrentStartTime(TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone));
            agentCommissionDate.setCurrentEndTime(TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timeZone));
            agentCommissionDate.setLastStartTime(TimeZoneUtils.getStartOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone));
            agentCommissionDate.setLastEndTime(TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone));
        } else if (settleCycle.equals(SettleCycleEnum.MONTH.getCode())) {
            agentCommissionDate.setCurrentStartTime(TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(),timeZone));
            agentCommissionDate.setCurrentEndTime(TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(),timeZone));
            agentCommissionDate.setLastStartTime(TimeZoneUtils.getLastMonStartTimeInTimeZone(timeZone));
            agentCommissionDate.setLastEndTime(TimeZoneUtils.getLastMonEndTimeInTimeZone(timeZone));
        }
        return agentCommissionDate;
    }

    public AgentCommissionDate getDayAgentCommissionDate(String agentId) {
        AgentCommissionDate agentCommissionDate = new AgentCommissionDate();
        agentCommissionDate.setAgentId(agentId);
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(agentId);
//        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
//        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
//        Integer settleCycle = ladderList.get(0).getSettleCycle();

        //确定时区
        SiteVO siteVO = siteApi.getSiteInfoByCode(agentInfoPO.getSiteCode());
        String timeZone = siteVO.getTimezone();
        agentCommissionDate.setCurrentStartTime(TimeZoneUtils.lastDayTimestamp(0, timeZone));
        agentCommissionDate.setCurrentEndTime(TimeZoneUtils.lastDayEndTimestamp(0, timeZone));
        agentCommissionDate.setLastStartTime(TimeZoneUtils.lastDayTimestamp(-1, timeZone));
        agentCommissionDate.setLastEndTime(TimeZoneUtils.lastDayEndTimestamp(-1, timeZone));
//        if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
//            agentCommissionDate.setCurrentStartTime(TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone));
//            agentCommissionDate.setCurrentEndTime(TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timeZone));
//            agentCommissionDate.setLastStartTime(TimeZoneUtils.getStartOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone));
//            agentCommissionDate.setLastEndTime(TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone));
//        } else if (settleCycle.equals(SettleCycleEnum.MONTH.getCode())) {
//            agentCommissionDate.setCurrentStartTime(TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(),timeZone));
//            agentCommissionDate.setCurrentEndTime(TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(),timeZone));
//            agentCommissionDate.setLastStartTime(TimeZoneUtils.getLastMonStartTimeInTimeZone(timeZone));
//            agentCommissionDate.setLastEndTime(TimeZoneUtils.getLastMonEndTimeInTimeZone(timeZone));
//        }
        return agentCommissionDate;
    }

    public AgentActiveUserResponseVO getAgentActiveUserInfo(AgentActiveNumberReqVO reqVO) {
        Integer activeNumber = 0; //有效活跃
        Integer newValidNumber = 0; //有效新增
        BigDecimal totalValidAmount = BigDecimal.ZERO;
        BigDecimal totalWinLoss  = BigDecimal.ZERO;
        String siteCode=reqVO.getSiteCode();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());
        if(StringUtils.isEmpty(siteCode)){
            siteCode=agentInfoPO.getSiteCode();
        }
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
        Integer settleCycle = ladderList.get(0).getSettleCycle();

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(agentInfoPO.getSiteCode());

        //所有下级代理ID
        List<String> agentIdList = new ArrayList<>();
        if (reqVO.getIsDirect() == null || !reqVO.getIsDirect()) {
            agentIdList = agentInfoService.getSubAgentIdList(agentInfoPO.getAgentId());
        } else {
            agentIdList = List.of(agentInfoPO.getAgentId());
        }


        //确定时区
        SiteVO siteVO = siteApi.getSiteInfoByCode(agentInfoPO.getSiteCode());
        String timeZone = siteVO.getTimezone();

        //拆分时间 当前周期开始时间
        Long startTime = 0L;
        //上个周期结束时间
        Long lastEndTime = 0L;

        //当前周期开始时间 结束时间
        Long calcStartTime = 0L;
        Long calcEndTime = 0L;
        //历史周期开始时间 结束时间
        Long reportStartTime = 0L;
        Long reportEndTime = 0L;

        if (settleCycle.equals(SettleCycleEnum.DAY.getCode())) {
            startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), timeZone);
        } else if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
            startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfLastWeekInTimeZone(System.currentTimeMillis(), timeZone);
        } else {
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
            lastEndTime = TimeZoneUtils.getLastMonEndTimeInTimeZone(timeZone);
        }

        log.info("lastEndTime:{},reqVO:{}",lastEndTime,reqVO);
        //默认按照传入时间戳 来计算
        reportStartTime = reqVO.getStartTime();
        reportEndTime = reqVO.getEndTime();
        if (lastEndTime < reqVO.getEndTime()) {
            //超出一个周期需要分开计算
            if (reqVO.getStartTime() <= lastEndTime) {
                calcStartTime = startTime;
                calcEndTime = reqVO.getEndTime();
                reportEndTime = lastEndTime;
            } else {
                calcStartTime = reqVO.getStartTime();
                calcEndTime = reqVO.getEndTime();
            }
        } else {
            reportEndTime = lastEndTime;
        }

        log.info("reportStartTime:{},reportEndTime:{}",lastEndTime,reqVO.getEndTime());

        //查询是否包含已结算数据
        AgentCommissionReportReqVO reportReqVO = new AgentCommissionReportReqVO();
        reportReqVO.setAgentId(reqVO.getAgentId());
        reportReqVO.setStartTime(reportStartTime);
        reportReqVO.setEndTime(reportEndTime);
        reportReqVO.setSiteCode(agentInfoPO.getSiteCode());

        List<AgentCommissionFinalReportPO> repostList = finalReportService.getCommissionFinalList(reportReqVO);
        if (repostList != null && repostList.size() > 0) {
            for (AgentCommissionFinalReportPO reportPO : repostList) {
                activeNumber += reportPO.getActiveNumber();
                newValidNumber += reportPO.getNewValidNumber();
                totalValidAmount = totalValidAmount.add(reportPO.getValidBetAmount());
                totalWinLoss = totalWinLoss.add(reportPO.getUserWinLoss());
            }
        }

        //实时计算当期的数据
        //汇总下级会员的存款金额
        Map<String, BigDecimal> depositSum = Maps.newHashMap();
        if(calcStartTime>0 && calcEndTime>=0){
            ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
            paramVO.setStartTime(calcStartTime);
            paramVO.setEndTime(calcEndTime);
            paramVO.setAgentIds(agentIdList);
            List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);

            //金额先转为平台币
            for (ReportUserAmountVO vo : depList) {
                BigDecimal rate = currencyRateMap.get(vo.getCurrency());
                vo.setRechargeAmount(AmountUtils.divide(vo.getRechargeAmount(), rate));
            }
            depositSum = depList.stream()
                    .collect(Collectors.groupingBy(
                            ReportUserAmountVO::getUserId,
                            Collectors.reducing(BigDecimal.ZERO, ReportUserAmountVO::getRechargeAmount, BigDecimal::add)
                    ));
        }




        //会员盈亏查询 有效投注、输赢
        UserWinLoseAgentReqVO vo=new UserWinLoseAgentReqVO();
        vo.setSiteCode(siteCode);
        vo.setStartTime(calcStartTime);
        vo.setEndTime(calcEndTime);
        vo.setAgentIds(agentIdList);
        List<UserWinLoseResponseVO> userWinLoseAgentVOS=userWinLoseApi.queryListByParam(vo);

        for (UserWinLoseResponseVO userWinLoseAgentVO : userWinLoseAgentVOS) {
            BigDecimal rate = currencyRateMap.get(userWinLoseAgentVO.getMainCurrency());
            userWinLoseAgentVO.setValidBetAmount(AmountUtils.divide(userWinLoseAgentVO.getValidBetAmount(), rate));
            userWinLoseAgentVO.setBetWinLose(AmountUtils.divide(userWinLoseAgentVO.getBetWinLose(), rate));
            totalWinLoss = totalWinLoss.add(userWinLoseAgentVO.getBetWinLose());
            totalValidAmount = totalValidAmount.add(userWinLoseAgentVO.getValidBetAmount());
        }

        Map<String, BigDecimal> validAmountSum = userWinLoseAgentVOS.stream()
                .collect(Collectors.groupingBy(
                        UserWinLoseResponseVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, UserWinLoseResponseVO::getValidBetAmount, BigDecimal::add)
                ));

        //计算会员的流水
        /*List<UserBetAmountSumVO> betAmountList = orderRecordApi.getUserOrderAmountByAgent(paramVO);

        for (UserBetAmountSumVO vo : betAmountList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            vo.setValidAmount(AmountUtils.divide(vo.getValidAmount(), rate));
            vo.setWinLossAmount(AmountUtils.divide(vo.getWinLossAmount(), rate));
            totalWinLoss = totalWinLoss.add(vo.getWinLossAmount());
            totalValidAmount = totalValidAmount.add(vo.getValidAmount());
        }

        Map<String, BigDecimal> validAmountSum = betAmountList.stream()
                .collect(Collectors.groupingBy(
                        UserBetAmountSumVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, UserBetAmountSumVO::getValidAmount, BigDecimal::add)
                ));*/

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

        activeNumber += commonKeys.size();

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
        List<String> notSettleList = agentValidUserRecordService.getNotSettleList(new ArrayList<>(validKeys), reqVO.getAgentId(), CommissionTypeEnum.NEGATIVE.getCode());
        newValidNumber = newValidNumber + notSettleList.size();

        AgentActiveUserResponseVO responseVO = new AgentActiveUserResponseVO();
        responseVO.setAgentId(reqVO.getAgentId());
        responseVO.setActiveNumber(activeNumber);
        responseVO.setNewValidNumber(newValidNumber);
        responseVO.setTotalValidAmount(totalValidAmount);
        responseVO.setTotalWinLoss(totalWinLoss);

        return responseVO;
    }

    public List<AgentActiveUserResponseVO> getAgentActiveUserInfoList(AgentActiveNumberReqVO reqVO) {
        //fixme
        List<AgentActiveUserResponseVO> responseList = new ArrayList<>();
        for (String agentId : reqVO.getAgentIdList()) {
            AgentActiveNumberReqVO activeNumberReqVO = new AgentActiveNumberReqVO();
            BeanUtils.copyProperties(reqVO, activeNumberReqVO);
            activeNumberReqVO.setAgentId(agentId);
            AgentActiveUserResponseVO responseVO = getAgentActiveUserInfo(activeNumberReqVO);
            responseList.add(responseVO);
        }

        return responseList;
    }

    public Boolean userActiveValidate(String userId) {
        GetByUserAccountVO userInfo = userInfoApi.getByUserInfoId(userId);
        if (userInfo == null || userInfo.getSuperAgentId() == null) return false;

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(userInfo.getSiteCode());

        //确定佣金方案
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(userInfo.getSuperAgentId());
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
        Integer settleCycle = ladderList.get(0).getSettleCycle();

        //确定周期
        Long startTime = 0L;
        Long endTime = 0L;

        String zoneId = CurrReqUtils.getTimezone();
        if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
            startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
            endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), zoneId);
        } else {
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
            endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), zoneId);
        }

        ReportUserWinLossParamVO userWinLossParamVO = new ReportUserWinLossParamVO();
        userWinLossParamVO.setStartTime(startTime);
        userWinLossParamVO.setEndTime(endTime);
        userWinLossParamVO.setUserId(userId);
        userWinLossParamVO.setAgentId(agentInfoPO.getAgentId());
        List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByUserId(userWinLossParamVO);

        //金额先转为平台币
        BigDecimal depositTotal = BigDecimal.ZERO;  //总存款
        for (ReportUserAmountVO vo : depList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            depositTotal = depositTotal.add(AmountUtils.divide(vo.getRechargeAmount(), rate));
        }

        //计算会员的流水
        BigDecimal totalValidAmount = BigDecimal.ZERO;  //总有效流水
       // List<UserBetAmountSumVO> betAmountList = orderRecordApi.getUserOrderAmountByUserId(userWinLossParamVO);
        List<ReportUserBetAmountSumVO> betAmountList = reportUserWinLoseAgentApi.getUserOrderAmountByUserId(userWinLossParamVO);
        for (ReportUserBetAmountSumVO vo : betAmountList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            totalValidAmount = totalValidAmount.add(AmountUtils.divide(vo.getValidAmount(), rate));
        }

        return depositTotal.compareTo(planVO.getActiveDeposit()) >= 0 && totalValidAmount.compareTo(planVO.getActiveBet()) >= 0;
    }

    /**
     *
     *
     查询参数:2024-10-10至 2025-01-15

     // 本期预估没有:  2025-02-01 至 2025-02-28
     上期: 2024-10-01至2025-01-31




     查询参数:2024-10-10至 2025-01-31

     // 本期预估没有:  2025-02-01 至 2025-02-28
     上期: 2024-10-01至2025-01-31

     查询参数:2025-01-01至 2025-01-31

     // 本期预估没有:  2025-02-01 至 2025-02-28
     上期: 2025-01-01至2025-01-31

     查询参数: 2025-01-01至2025-02-06
     本期:  2025-02-01 至 2025-02-06
     上期: 2025-01-01至2025-01-31

     查询参数: 2025-02-01至2025-02-06
     本期:  2025-02-01 至 2025-02-06
     // 上期没有: 2025-01-01至2025-01-31


     查询参数: 2025-01-11至2025-02-06
     本期:  2025-02-01 至 2025-02-06
     上期: 2025-01-01至2025-01-31

     * @param reqVO
     * @return
     */
    public Page<SubCommissionGeneralVO> getReportDetail(FrontCommissionDetailReqVO reqVO) {
        if (StringUtils.isNotBlank(reqVO.getAgentAccount())) {
            AgentInfoVO subInfo = agentInfoService.getByAgentAccountAndSiteCode(reqVO.getAgentAccount(), reqVO.getSiteCode());
            if (subInfo == null) {
                return null;
            }

            if (!Arrays.stream(subInfo.getPath().split(",")).toList().contains(reqVO.getAgentId())) {
                throw new BaowangDefaultException(ResultCode.INPUT_ACCOUNT_NOT_DOWN);
            }
        }

        //当前代理
        String currentId = reqVO.getAgentId();

        AgentInfoPO currentAgent = agentInfoService.getByAgentId(currentId);
        //获取直属下级
        List<String> idList = agentInfoService.getSubAgentIdDirectReportList(currentId);
        if(CollectionUtils.isEmpty(idList)){
            idList= Lists.newArrayList();
        }
        //包含当前代理
        idList.add(currentId);
       /* List<String> agentIdList = idList.stream().filter(agentId -> !agentId.equals(currentId)).toList();
        if (agentIdList == null || agentIdList.size() == 0) {
            //没有下级
            return new Page<>();
        }*/

        SiteVO siteVO = siteApi.getSiteInfoByCode(currentAgent.getSiteCode());
        String timeZone = siteVO.getTimezone();

        AgentIdPageVO agentIdPageVO = new AgentIdPageVO();
        agentIdPageVO.setPageNumber(reqVO.getPageNumber());
        agentIdPageVO.setPageSize(Integer.MAX_VALUE);
        agentIdPageVO.setAgentIdList(idList);
        agentIdPageVO.setAgentAccount(reqVO.getAgentAccount());
        Page<String> agentIdPage = agentInfoService.getAgentIdListPage(agentIdPageVO);
        List<SubCommissionGeneralVO> resultList = new ArrayList<>();
        Page<SubCommissionGeneralVO> page = new Page<>();
       // BeanUtils.copyProperties(agentIdPage, page);
        page.setPages(1);
        page.setCurrent(1);
        page.setTotal(1);
        page.setSize(1);
        //整个团队
        List<String> agentIds = null;
        if (!agentIdPage.getRecords().isEmpty()) {
            agentIds = agentIdPage.getRecords();
        }

        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(currentAgent.getPlanCode());
        List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
        Integer settleCycle = ladderList.get(0).getSettleCycle();
        //本期 开始时间 结束时间
        Long startTime = 0L;
        Long endTime = 0L;
        //上期 开始时间 结束时间
        Long lastStartTime = 0L;
        Long lastEndTime = 0L;

        //当期实际开始时间 结束时间
        Long calcStartTime = 0L;
        Long calcEndTime = 0L;
        //上个周期 开始时间 结束时间
        Long reportStartTime = 0L;
        Long reportEndTime = 0L;
        //计算实时统计的时间范围， 只算当前期以内的时间范围
        if (settleCycle.equals(SettleCycleEnum.DAY.getCode())) {
            startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfDayInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfDayInTimeZone(reqVO.getEndTime(), timeZone);
        } else if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
            startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfWeekInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfWeekInTimeZone(reqVO.getEndTime(), timeZone);
        } else {
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfMonthInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime=TimeZoneUtils.getEndOfMonthInTimeZone(reqVO.getEndTime(),timeZone);
        }
        //查询开始时间 小于等于 上个周期结束时间
        if (reqVO.getStartTime() <= lastEndTime) {
            reportStartTime= lastStartTime;
            reportEndTime= lastEndTime;
        }
        //查询结束时间 大于等于 当期开始时间
        if (reqVO.getEndTime() >= startTime) {
            //本期开始时间 结束时间
            calcStartTime=startTime;
            calcEndTime=endTime;
            if (endTime > reqVO.getEndTime()) {
                calcEndTime=reqVO.getEndTime();
            }
        }

        //当期预估的
        if (calcStartTime > 0 && calcEndTime > 0) {
            AgentCommissionExpectCalcVO calcVO = new AgentCommissionExpectCalcVO();
            calcVO.setAgentIds(agentIds);
            calcVO.setSiteCode(currentAgent.getSiteCode());
            calcVO.setStartTime(calcStartTime);
            calcVO.setEndTime(calcEndTime);
            List<AgentCommissionExpectReportPO> reportPOList = expectCalcService.calcCommissionByTime(calcVO);
            for (AgentCommissionExpectReportPO reportPO : reportPOList) {
                SubCommissionGeneralVO generalVO = new SubCommissionGeneralVO();
                generalVO.setAgentAccount(reportPO.getAgentAccount());
                generalVO.setStartTime(reportPO.getStartTime());
                generalVO.setEndTime(reportPO.getEndTime());
                generalVO.setActiveValidNumber(reportPO.getActiveNumber());
                generalVO.setNewActiveNumber(reportPO.getNewValidNumber());
                generalVO.setAccessFee(reportPO.getAccessFee());
                generalVO.setDiscountUsed(reportPO.getTransferAmount());
                generalVO.setVenueFee(reportPO.getVenueFee());
                generalVO.setNetWinLoss(reportPO.getNetWinLoss());
                generalVO.setValidAmount(reportPO.getValidBetAmount());
                generalVO.setUserWinLossTotal(reportPO.getUserWinLossTotal());
                generalVO.setDiscountAmount(reportPO.getDiscountAmount());
                generalVO.setVipAmount(reportPO.getVipAmount());
                resultList.add(generalVO);
            }
        }

        //计算不在当前周期的数据 之前未发放的
        FrontCommissionGroupReqVO reportReqVO = new FrontCommissionGroupReqVO();
        reportReqVO.setAgentIds(agentIds);
        reportReqVO.setAgentAccount(reqVO.getAgentAccount());
        reportReqVO.setSiteCode(reqVO.getSiteCode());
        reportReqVO.setStartTime(reportStartTime);
        reportReqVO.setEndTime(reportEndTime);

       /* reportReqVO.setStatusList(List.of(CommissionReviewOrderStatusEnum.WAIT_REVIEW.getCode(),
                CommissionReviewOrderStatusEnum.ONE_REVIEWING.getCode(),
                CommissionReviewOrderStatusEnum.REVIEW_FAIL.getCode()
                ));*/
        //是否是总代
        boolean ifTop1Agent=false;
        if(StringUtils.isEmpty(currentAgent.getParentAccount())){
            ifTop1Agent=true;
        }
        // 总代查询未发放的 子代不过滤
        if(ifTop1Agent){
            reportReqVO.setStatusList(List.of(AgentCommissionStatusEnum.PENDING.getCode()));
        }
        List<CommissionDetailVO> list = finalReportService.getFinalReportListGroupAgentId(reportReqVO);

        if (list != null && !list.isEmpty()) {
            Map<String, CommissionDetailVO> detailMap = list.stream().collect(Collectors.toMap(CommissionDetailVO::getAgentAccount, p -> p, (k1, k2) -> k2));
            if (!resultList.isEmpty()) {
                for (SubCommissionGeneralVO generalVO : resultList) {
                    CommissionDetailVO detailVO = detailMap.get(generalVO.getAgentAccount());
                    if(detailVO!=null){
                        generalVO.setActiveValidNumber(detailVO.getActiveValidNumber() + generalVO.getActiveValidNumber());
                        generalVO.setNewActiveNumber(detailVO.getNewActiveNumber() + generalVO.getNewActiveNumber());
                        generalVO.setAccessFee(detailVO.getAccessFee().add(generalVO.getAccessFee()));
                        generalVO.setDiscountUsed(detailVO.getDiscountUsed().add(generalVO.getDiscountUsed()));
                        generalVO.setVenueFee(detailVO.getVenueFee().add(generalVO.getVenueFee()));
                        generalVO.setNetWinLoss(detailVO.getNetWinLoss().add(generalVO.getNetWinLoss()));
                        generalVO.setValidAmount(detailVO.getValidBetAmount().add(generalVO.getValidAmount()));
                        generalVO.setUserWinLossTotal(detailVO.getUserWinLossTotal().add(generalVO.getUserWinLossTotal()));
                        generalVO.setDiscountAmount(detailVO.getDiscountAmount().add(generalVO.getDiscountAmount()));
                        generalVO.setVipAmount(detailVO.getVipAmount().add(generalVO.getVipAmount()));
                        generalVO.setVipAmount(detailVO.getReviewAdjustAmount().add(generalVO.getReviewAdjustAmount()));
                    }

                }
            } else {
                for (CommissionDetailVO detailVO : list) {
                    if(detailVO!=null){
                        SubCommissionGeneralVO generalVO = new SubCommissionGeneralVO();
                        generalVO.setAgentAccount(detailVO.getAgentAccount());
                        generalVO.setStartTime(calcStartTime);
                        generalVO.setEndTime(calcEndTime);
                        generalVO.setActiveValidNumber(detailVO.getActiveValidNumber());
                        generalVO.setNewActiveNumber(detailVO.getNewActiveNumber());
                        generalVO.setAccessFee(detailVO.getAccessFee());
                        generalVO.setDiscountUsed(detailVO.getDiscountUsed());
                        generalVO.setVenueFee(detailVO.getVenueFee());
                        generalVO.setNetWinLoss(detailVO.getNetWinLoss());
                        generalVO.setValidAmount(detailVO.getValidBetAmount());
                        generalVO.setUserWinLossTotal(detailVO.getUserWinLossTotal());
                        generalVO.setDiscountAmount(detailVO.getDiscountAmount());
                        generalVO.setVipAmount(detailVO.getVipAmount());
                        generalVO.setReviewAdjustAmount(detailVO.getReviewAdjustAmount());
                        resultList.add(generalVO);
                    }
                }
            }
        }
        page.setRecords(resultList);
        return page;
    }

    /**
     * 客户端佣金报表
     */
    public FrontCommissionReportResVO getClientCommissionReport(FrontCommissionReportReqVO reqVO) {
        FrontCommissionReportResVO reportResVO = new FrontCommissionReportResVO();
        NegProfitInfo negProfitInfo = new NegProfitInfo();
        ValidRebateInfo validRebateInfo = new ValidRebateInfo();
        PersonProfitInfo personProfitInfo = new PersonProfitInfo();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());
        String planCode = "";
        if (agentInfoPO.getLevel().equals(CommonConstant.business_one)) {
            planCode = agentInfoPO.getPlanCode();
        } else {
            String parentId = agentInfoPO.getPath().split(",")[0];
            AgentInfoPO parentInfo = agentInfoService.getByAgentId(parentId);
            planCode = parentInfo.getPlanCode();
        }

        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);
        Integer settleCycle = 0;
        if (reqVO.getCommissionType().equals(CommissionTypeEnum.NEGATIVE.getCode())) {
            List<AgentCommissionLadderVO> ladderList = agentCommissionLadderService.getListByPlanId(planVO.getId());
            settleCycle = ladderList.get(0).getSettleCycle();
        } else {
            AgentCommissionPlanTurnoverConfigVo rebateConfigVO = agentRebateConfigService.getConfigByPlanId(planVO.getId());
            settleCycle = rebateConfigVO.getSettleCycle();
        }

        //确定时区
        SiteVO siteVO = siteApi.getSiteInfoByCode(agentInfoPO.getSiteCode());
        String timeZone = siteVO.getTimezone();

        //本期 开始时间 结束时间
        Long startTime = 0L;
        Long endTime = 0L;
        //上期 开始时间 结束时间
        Long lastStartTime = 0L;
        Long lastEndTime = 0L;

        //当期实际开始时间 结束时间
        Long calcStartTime = 0L;
        Long calcEndTime = 0L;
        //上个周期 开始时间 结束时间
        Long reportStartTime = 0L;
        Long reportEndTime = 0L;
        //计算实时统计的时间范围， 只算当前期以内的时间范围
        if (settleCycle.equals(SettleCycleEnum.DAY.getCode())) {
            startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfDayInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfDayInTimeZone(reqVO.getEndTime(), timeZone);
        } else if (settleCycle.equals(SettleCycleEnum.WEEK.getCode())) {
            startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfWeekInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime = TimeZoneUtils.getEndOfWeekInTimeZone(reqVO.getEndTime(), timeZone);
        } else {
            startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
            endTime=TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(),timeZone);
            lastStartTime = TimeZoneUtils.getStartOfMonthInTimeZone(reqVO.getStartTime(), timeZone);
            lastEndTime=TimeZoneUtils.getEndOfMonthInTimeZone(reqVO.getEndTime(),timeZone);
        }
        //查询开始时间 小于等于 上个周期结束时间
        if (reqVO.getStartTime() <= lastEndTime) {
            reportStartTime= lastStartTime;
            reportEndTime= lastEndTime;
        }
        //查询结束时间 大于等于 当期开始时间
        if (reqVO.getEndTime() >= startTime) {
            //本期开始时间 结束时间
            calcStartTime=startTime;
            calcEndTime=endTime;
            if (endTime > reqVO.getEndTime()) {
                calcEndTime=reqVO.getEndTime();
            }
        }

        if (agentInfoPO.getLevel().equals(CommonConstant.business_one)) {
            CommissionStatisticsReqVO statisticsReqVO = new CommissionStatisticsReqVO();
            statisticsReqVO.setAgentId(agentInfoPO.getAgentId());
            statisticsReqVO.setCommissionType(reqVO.getCommissionType());
            statisticsReqVO.setSettleCycle(settleCycle);
            statisticsReqVO.setSiteCode(agentInfoPO.getSiteCode());
            statisticsReqVO.setReportStartTime(reportStartTime);
            statisticsReqVO.setReportEndTime(reportEndTime);
            statisticsReqVO.setCalcStartTime(calcStartTime);
            statisticsReqVO.setCalcEndTime(calcEndTime);
            statisticsReqVO.setStartTime(reqVO.getStartTime());
            statisticsReqVO.setEndTime(reqVO.getEndTime());
            // if (reqVO.getCommissionType().equals(CommissionTypeEnum.NEGATIVE.getCode())) {
                negProfitInfo = commissionStatisticsService.getNegProfitInfoReport(statisticsReqVO);
                reportResVO.setNegProfitInfo(negProfitInfo);
          //  } else if (reqVO.getCommissionType().equals(CommissionTypeEnum.REBATE.getCode())) {
                validRebateInfo = commissionStatisticsService.getValidRebateInfoReport(statisticsReqVO);
                reportResVO.setValidRebateInfo(validRebateInfo);
           // } else {
                personProfitInfo = commissionStatisticsService.getPersonProfitInfoReport(statisticsReqVO);
                reportResVO.setPersonProfitInfo(personProfitInfo);
           // }

            reportResVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            reportResVO.setIsGeneral(true);
        } else {

            CommissionStatisticsReqVO statisticsReqVO = new CommissionStatisticsReqVO();
            statisticsReqVO.setAgentId(agentInfoPO.getAgentId());
            statisticsReqVO.setCommissionType(reqVO.getCommissionType());
            statisticsReqVO.setSettleCycle(settleCycle);
            statisticsReqVO.setSiteCode(agentInfoPO.getSiteCode());
            statisticsReqVO.setReportStartTime(reportStartTime);
            statisticsReqVO.setReportEndTime(reportEndTime);
            statisticsReqVO.setCalcStartTime(calcStartTime);
            statisticsReqVO.setCalcEndTime(calcEndTime);
           // if (reqVO.getCommissionType().equals(CommissionTypeEnum.NEGATIVE.getCode())) {
                SubCommissionGeneralVO subCommissionGeneralVO = commissionStatisticsService.getSubCommissionGeneralVO(statisticsReqVO);
                negProfitInfo.setSubCommissionGeneralVO(subCommissionGeneralVO);
                reportResVO.setNegProfitInfo(negProfitInfo);
           // } else if (reqVO.getCommissionType().equals(CommissionTypeEnum.REBATE.getCode())) {
                 validRebateInfo = commissionStatisticsService.getValidRebateInfoReport(statisticsReqVO);
                 reportResVO.setValidRebateInfo(validRebateInfo);
           // } else {
                 personProfitInfo = commissionStatisticsService.getPersonProfitInfoReport(statisticsReqVO);
                 reportResVO.setPersonProfitInfo(personProfitInfo);
           // }
            reportResVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
            reportResVO.setIsGeneral(false);

        }

        return reportResVO;

    }

    public ResponseVO<AgentCommissionPlanVO> getCurrentCommissionPlain(String agentId) {
        AgentInfoPO agentInfoPO = agentInfoService.selectByAgentId(agentId);
        if(agentInfoPO == null){
          return ResponseVO.success();
        }
        String planCode = agentInfoPO.getPlanCode();
        return ResponseVO.success(agentCommissionPlanService.getPlanByPlanCode(planCode));
    }

    /**
     * 获取有效新增会员信息
     */
    public List<String> getActiveNewUserInfo(ActiveNumberPageReqVO reqVO) {
        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(reqVO.getAgentId());
        AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(agentInfoPO.getPlanCode());
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(agentInfoPO.getSiteCode());

        List<String> agentIdList = agentInfoService.getSubAgentIdList(reqVO.getAgentId());
        //汇总下级会员的存款金额
        ReportAgentWinLossParamVO paramVO = new ReportAgentWinLossParamVO();
        paramVO.setStartTime(reqVO.getStartTime());
        paramVO.setEndTime(reqVO.getEndTime());
        paramVO.setAgentIds(agentIdList);
        List<ReportUserAmountVO> depList = reportUserRechargeApi.getUserDepAmountByAgentIds(paramVO);

        //金额先转为平台币
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
        List<ReportUserBetAmountSumVO> betAmountList = reportUserWinLoseAgentApi.getWinLoseStatisticsByAgentIds(paramVO);
        for (ReportUserBetAmountSumVO vo : betAmountList) {
            BigDecimal rate = currencyRateMap.get(vo.getCurrency());
            vo.setValidAmount(AmountUtils.divide(vo.getValidAmount(), rate));
            vo.setWinLossAmount(AmountUtils.divide(vo.getWinLossAmount(), rate));
        }

        Map<String, BigDecimal> validAmountSum = betAmountList.stream()
                .collect(Collectors.groupingBy(
                        ReportUserBetAmountSumVO::getUserId,
                        Collectors.reducing(BigDecimal.ZERO, ReportUserBetAmountSumVO::getValidAmount, BigDecimal::add)
                ));

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
        if (validKeys.size() > 0) {
            return new ArrayList<>(validKeys);
        }

        return new ArrayList<>();
    }
}
