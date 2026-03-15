package com.cloud.baowang.user.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.cloud.baowang.agent.api.api.*;
import com.cloud.baowang.agent.api.vo.commission.CommissionGranRecordReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositReviewPageReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithDrawSumReqVO;
import com.cloud.baowang.agent.api.vo.depositWithdraw.AgentDepositWithdrawSumRespVO;
import com.cloud.baowang.agent.api.vo.site.AgentDataOverviewResVo;
import com.cloud.baowang.agent.api.vo.withdraw.AgentWithdrawManualPageReqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.wallet.api.enums.usercoin.DepositWithdrawalOrderCustomerStatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseAgentApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.site.GetWinLoseStatisticsBySiteCodeVO;
import com.cloud.baowang.report.api.vo.site.SiteReportUserVenueStaticsVO;
import com.cloud.baowang.report.api.vo.user.ReportUserWinLossRebateParamVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseVO;
import com.cloud.baowang.system.api.api.StatisticsPendingReviewRecordsApi;
import com.cloud.baowang.system.api.api.member.BusinessAdminApi;
import com.cloud.baowang.system.api.api.member.BusinessMenuApi;
import com.cloud.baowang.system.api.vo.StatisticsPendingVO;
import com.cloud.baowang.system.api.vo.business.BusinessUserMenuRespVO;
import com.cloud.baowang.system.api.vo.business.SiteSelectQuickEntryResponse;
import com.cloud.baowang.system.api.vo.member.BusinessAdminVO;
import com.cloud.baowang.user.api.enums.SiteTodoEnum;
import com.cloud.baowang.user.api.vo.agent.SiteSelectQuickEntryParam;
import com.cloud.baowang.user.api.vo.site.*;
import com.cloud.baowang.user.enums.SiteDataCompareGraphEnum;
import com.cloud.baowang.user.repositories.UserInfoRepository;
import com.cloud.baowang.user.repositories.UserLoginInfoRepository;
import com.cloud.baowang.wallet.api.api.*;
import com.cloud.baowang.wallet.api.vo.platformCoinAdjust.UserPlatformCoinManualUpRecordVO;
import com.cloud.baowang.wallet.api.vo.site.GetAllArriveAmountBySiteCodeResponseVO;
import com.cloud.baowang.wallet.api.vo.site.GetDepositStatisticsBySiteCodeVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserManualDepositPageReqVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawManualPageReqVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static com.cloud.baowang.user.enums.SiteDataCompareGraphEnum.*;

/**
 * @className: SiteHomeStatisticsService
 * @author: wade
 * @description: 站点首页统计服务类
 * @date: 12/8/24 10:55
 */
@Service
@AllArgsConstructor
@Slf4j
public class SiteHomeStatisticsService {
    private final UserInfoRepository userInfoRepository;

    private final UserLoginInfoRepository userLoginInfoRepository;

    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final ReportUserWinLoseAgentApi reportUserWinLoseAgentApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final UserReviewService userReviewService;

    private final UserAccountUpdateReviewService accountUpdateReviewService;

    private final StatisticsPendingReviewRecordsApi statisticsPendingReviewRecordsApi;

    private final AgentCommissionReviewApi agentCommissionReviewApi;

    private AgentReviewApi agentReviewApi;

    private final AgentInfoApi agentInfoApi;

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final ReportUserRechargeApi reportUserRechargeApi;

    private final UserManualDepositApi userManualDepositApi;
    private final UserWithdrawManualRecordApi userWithdrawManualRecordApi;
    private final AgentDepositReviewApi agentDepositReviewApi;
    private final AgentWithdrawManualRecordApi agentWithdrawManualRecordApi;


    private final BusinessMenuApi businessMenuApi;

    private final BusinessAdminApi businessAdminApi;

    private final AgentDepositWithdrawApi agentDepositWithdrawApi;
    private final AgentCommissionGrantApi agentCommissionGrantApi;
    private final UserPlatformCoinManualDownRecordApi platformCoinManualDownRecordApi;


    /**
     *
     *      *  1. 新增代理 ==  A
     *
     *         2. 新增加转化会员 ==
     *
     *         3. 登录人数 ==
     *      *
     *      *  4. 代理存款人数 ++   A
     *      *
     *      *  5. 代理提款人数 ++   A
     */
    public UserDataOverviewRespVo userDataOverview(UserDataOverviewResVo vo) {
        UserDataOverviewRespVo result = new UserDataOverviewRespVo();

        paramLast(vo);

        // 新增会员
        UserDataOverviewRespVo userDataVo = userInfoRepository.getSiteStatistics(vo.getSiteCode(), vo.getStartTime(), vo.getEndTime());
        UserDataOverviewRespVo userDataVoLast = userInfoRepository.getSiteStatistics(vo.getSiteCode(), vo.getStartTimeLast(), vo.getEndTimeLast());



        //2. 新增加转化会员
        Integer newAddRechargeUser = userDataVo.getNewAddRechargeUser();
        Integer newAddRechargeUserLast = userDataVoLast.getNewAddRechargeUser();

        BigDecimal newAddRechargeUserTemp = BigDecimal.valueOf(newAddRechargeUserLast);
        if (newAddRechargeUserLast==0){
            newAddRechargeUserTemp = BigDecimal.ONE;
        }
        result.setNewAddRechargeUser(userDataVo.getNewAddRechargeUser());
        result.setNewAddRechargeUserComparePer(BigDecimal.valueOf(newAddRechargeUser-newAddRechargeUserLast).multiply(BigDecimal.valueOf(100)).divide(newAddRechargeUserTemp, 2, RoundingMode.DOWN));

        // 3. 登录人数
        Integer userLastLongin = userInfoRepository.getSiteUserLastLongin(vo.getSiteCode(), vo.getStartTime(), vo.getEndTime());

        Integer userLastLonginLast = userInfoRepository.getSiteUserLastLongin(vo.getSiteCode(), vo.getStartTimeLast(), vo.getEndTimeLast());


        Integer userLastLonginTemp = userLastLonginLast;
        if (userLastLonginLast==0){
            userLastLonginTemp = 1;
        }


        result.setLoginUser(userLastLongin);
        result.setLoginUserComparePer(BigDecimal.valueOf(userLastLongin-userLastLonginLast).multiply(BigDecimal.valueOf(100)).divide(BigDecimal.valueOf(userLastLonginTemp), 2, RoundingMode.DOWN));

        getDepositCount(result, vo.getSiteCode(), vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast());
        getWithdrawCount(result, vo.getSiteCode(), vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast());

        getNewAgentCount(result, vo.getSiteCode(), vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast());

        return result;
    }

    /**
     *  6. 代理存款金额 ++   A
     *
     *  7. 代理提款金额 ++   A
     *
     *  8. 已经发放代理佣金 ++  A
     *
     */
    public AgentDataOverviewRespVo agentDataOverview(UserDataOverviewResVo vo) {
        paramLast(vo);
        String siteCode = vo.getSiteCode();

        AgentDataOverviewRespVo result = AgentDataOverviewRespVo.builder().build();

        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode());
        vo.setCurrencyCode(null);
        getDepositAmount(allFinalRate, result, siteCode, vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast(),vo.getCurrencyCode());
        getWithdrawAmount(allFinalRate, result, siteCode, vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast(),vo.getCurrencyCode());
        getCommissionAmount(result, siteCode, vo.getStartTime(), vo.getEndTime(), vo.getStartTimeLast(), vo.getEndTimeLast());

        result.setCurrencyCode("WTC");

        return result;
    }


    private void paramLast(UserDataOverviewResVo vo){

        String timeZone = vo.getTimeZone();
        String dbZone = TimeZoneUtils.getTimeZoneUTC(timeZone);

        if (vo.getStartTime()==null){
            vo.setStartTime(System.currentTimeMillis()- 6*24*60*60*1000);
        }
        long startTime =  TimeZoneUtils.getStartOfDayInTimeZone(vo.getStartTime(), timeZone);

        if (vo.getEndTime()==null || vo.getEndTime() > TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(),timeZone)){
            vo.setEndTime(System.currentTimeMillis());
        }
        long endTime = TimeZoneUtils.getEndOfDayInTimeZone(vo.getEndTime(), timeZone);;

        long endTimeLast = startTime - 1;
        long betweenDay = DateUtil.betweenDay(new DateTime(startTime), new DateTime(endTime), false);
        long startTimeLast = startTime - ((long)  24 *60 * 60 * 1000 * (betweenDay+1));

        vo.setTimeZone(dbZone);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        vo.setStartTimeLast(startTimeLast);
        vo.setEndTimeLast(endTimeLast);
    }

    private void getNewAgentCount(UserDataOverviewRespVo result,String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast){
        // 1. 新增代理
        Long newAgents = agentInfoApi.getNewAgents(AgentDataOverviewResVo.builder().siteCode(siteCode).startTime(startTime.toString()).endTime(endTime.toString()).build());
        Long newAgentsLast = agentInfoApi.getNewAgents(AgentDataOverviewResVo.builder().siteCode(siteCode).startTime(startTimeLast.toString()).endTime(endTimeLast.toString()).build());
        BigDecimal newAgentsLastTemp = BigDecimal.valueOf(newAgentsLast);
        if (newAgentsLast==0){
            newAgentsLastTemp = BigDecimal.ONE;
        }
        BigDecimal divide = BigDecimal.valueOf(newAgents - newAgentsLast).multiply(BigDecimal.valueOf(100)).divide(newAgentsLastTemp, 2, RoundingMode.DOWN);
        result.setNewAgentCount(newAgents);
        result.setNewAgentCountComparePer(divide);

    }


    private void getDepositAmount(Map<String, BigDecimal> finalRateMap, AgentDataOverviewRespVo result, String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast, String currencyCode){
        AgentDepositWithDrawSumReqVO reqDepositVO = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).currencyCode(currencyCode).depositOrWithDraw(1).build();
        List<AgentDepositWithdrawSumRespVO> agentDepositSumRespVOS = agentDepositWithdrawApi.queryAgentReportAmountGroupBy(reqDepositVO);

        AgentDepositWithDrawSumReqVO reqDepositVOLast = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).currencyCode(currencyCode).depositOrWithDraw(1).build();
        List<AgentDepositWithdrawSumRespVO> agentDepositSumRespVOSLast = agentDepositWithdrawApi.queryAgentReportAmountGroupBy(reqDepositVOLast);

        AtomicReference<BigDecimal> depositSumAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> depositSumAmountLast = new AtomicReference<>(BigDecimal.ZERO);
        for (AgentDepositWithdrawSumRespVO item : agentDepositSumRespVOS) {
            BigDecimal rate;
            if (StrUtil.isEmpty(item.getCurrencyCode()) || !item.getCurrencyCode() .equals("WTC")){
                if (null == finalRateMap.get(item.getCurrencyCode())) {
                    throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                }
                rate = finalRateMap.get(item.getCurrencyCode());
            } else {
                rate = BigDecimal.ONE;
            }
            depositSumAmount.updateAndGet(v -> v.add(item.getApplyAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
        }
        for (AgentDepositWithdrawSumRespVO item : agentDepositSumRespVOSLast) {
            BigDecimal rate;
            if (StrUtil.isEmpty(item.getCurrencyCode()) || !item.getCurrencyCode() .equals("WTC")){
                if (null == finalRateMap.get(item.getCurrencyCode())) {
                    throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                }
                rate = finalRateMap.get(item.getCurrencyCode());
            } else {
                rate = BigDecimal.ONE;
            }
            depositSumAmountLast.updateAndGet(v -> v.add(item.getApplyAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
        }
        BigDecimal depositSum = depositSumAmountLast.get();
        if (depositSumAmountLast.get().compareTo(BigDecimal.ZERO)<=0){
            depositSum = BigDecimal.ONE;
        }
        result.setAgentRechargeAmount(depositSumAmount.get());
        result.setAgentRechargeAmountComparePer(depositSumAmount.get().subtract(depositSumAmountLast.get()).multiply(BigDecimal.valueOf(100)).divide(depositSum, 2, RoundingMode.DOWN));
    }

    private void getDepositCount(UserDataOverviewRespVo result, String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast){
        AgentDepositWithDrawSumReqVO reqDepositVO = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).depositOrWithDraw(1).build();
        AgentDepositWithdrawSumRespVO agentDepositWithdrawSumRespVO = agentDepositWithdrawApi.queryAgentReportCountGroupBy(reqDepositVO);

        AgentDepositWithDrawSumReqVO reqDepositVOLast = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).depositOrWithDraw(1).build();
        AgentDepositWithdrawSumRespVO agentDepositWithdrawSumRespVOLast = agentDepositWithdrawApi.queryAgentReportCountGroupBy(reqDepositVOLast);

        BigDecimal depositCount = agentDepositWithdrawSumRespVO.getAgentCount();
        BigDecimal depositCountLast = agentDepositWithdrawSumRespVOLast.getAgentCount();

        BigDecimal depositCountTemp = depositCountLast;
        if (depositCountLast.compareTo(BigDecimal.ZERO)<=0){
            depositCountTemp = BigDecimal.ONE;
        }
        result.setAgentRechargeCount(depositCount);
        result.setAgentRechargeCountComparePer(depositCount.subtract(depositCountLast).multiply(BigDecimal.valueOf(100)).divide(depositCountTemp, 2, RoundingMode.DOWN));
    }

    private void getWithdrawAmount(Map<String, BigDecimal> finalRateMap, AgentDataOverviewRespVo result, String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast, String currencyCode){
        AgentDepositWithDrawSumReqVO reqWithdrawVO = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).currencyCode(currencyCode).depositOrWithDraw(7).build();
        List<AgentDepositWithdrawSumRespVO> agentWithdrawSumRespVOS = agentDepositWithdrawApi.queryAgentReportAmountGroupBy(reqWithdrawVO);

        AgentDepositWithDrawSumReqVO reqWithdrawVOLast = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).currencyCode(currencyCode).depositOrWithDraw(7).build();
        List<AgentDepositWithdrawSumRespVO> agentWithdrawSumRespVOSLast = agentDepositWithdrawApi.queryAgentReportAmountGroupBy(reqWithdrawVOLast);

        AtomicReference<BigDecimal> withdrawSumAmount = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> withdrawSumAmountLast = new AtomicReference<>(BigDecimal.ZERO);
        for (AgentDepositWithdrawSumRespVO item : agentWithdrawSumRespVOS) {
            BigDecimal rate;
            if (StrUtil.isEmpty(item.getCurrencyCode()) || !item.getCurrencyCode() .equals("WTC")){
                if (null == finalRateMap.get(item.getCurrencyCode())) {
                    throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                }
                rate = finalRateMap.get(item.getCurrencyCode());
            } else {
                rate = BigDecimal.ONE;
            }
            withdrawSumAmount.updateAndGet(v -> v.add(item.getApplyAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
        }
        for (AgentDepositWithdrawSumRespVO item : agentWithdrawSumRespVOSLast) {
            BigDecimal rate;
            if (StrUtil.isEmpty(item.getCurrencyCode()) || !item.getCurrencyCode() .equals("WTC")){
                if (null == finalRateMap.get(item.getCurrencyCode())) {
                    throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                }
                rate = finalRateMap.get(item.getCurrencyCode());
            } else {
                rate = BigDecimal.ONE;
            }
            withdrawSumAmountLast.updateAndGet(v -> v.add(item.getApplyAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
        }
        BigDecimal withdrawSum = withdrawSumAmountLast.get();
        if (withdrawSumAmountLast.get().compareTo(BigDecimal.ZERO)<=0){
            withdrawSum = BigDecimal.ONE;
        }
        result.setAgentWithdrawAmount(withdrawSumAmount.get());
        result.setAgentWithdrawAmountComparePer(withdrawSumAmount.get().subtract(withdrawSumAmountLast.get()).multiply(BigDecimal.valueOf(100)).divide(withdrawSum, 2, RoundingMode.DOWN));
    }

    private void getWithdrawCount(UserDataOverviewRespVo result, String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast){
        AgentDepositWithDrawSumReqVO reqDepositVO = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).depositOrWithDraw(7).build();
        AgentDepositWithdrawSumRespVO agentDepositWithdrawSumRespVO = agentDepositWithdrawApi.queryAgentReportCountGroupBy(reqDepositVO);

        AgentDepositWithDrawSumReqVO reqWithdrawVOLast = AgentDepositWithDrawSumReqVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).depositOrWithDraw(7).build();
        AgentDepositWithdrawSumRespVO agentDepositWithdrawSumRespVOLast = agentDepositWithdrawApi.queryAgentReportCountGroupBy(reqWithdrawVOLast);

        BigDecimal withdrawCount = agentDepositWithdrawSumRespVO.getAgentCount();
        BigDecimal withdrawCountLast = agentDepositWithdrawSumRespVOLast.getAgentCount();

        BigDecimal withdrawCountTemp = withdrawCountLast;
        if (withdrawCountLast.compareTo(BigDecimal.ZERO)<=0){
            withdrawCountTemp = BigDecimal.ONE;
        }
        result.setAgentWithdrawCount(withdrawCount);
        result.setAgentWithdrawCountComparePer(withdrawCount.subtract(withdrawCountLast).multiply(BigDecimal.valueOf(100)).divide(withdrawCountTemp, 2, RoundingMode.DOWN));
    }

    private void getCommissionAmount(AgentDataOverviewRespVo result, String siteCode, Long startTime,Long endTime,Long startTimeLast,Long endTimeLast){

        CommissionGranRecordReqVO reqDepositVO = CommissionGranRecordReqVO.builder().siteCode(siteCode).grantStartTime(startTime).grantEndTime(endTime).build();
        BigDecimal commissionAmount = agentCommissionGrantApi.agentCommissionSum(reqDepositVO);

        CommissionGranRecordReqVO reqWithdrawVOLast = CommissionGranRecordReqVO.builder().siteCode(siteCode).grantStartTime(startTimeLast).grantEndTime(endTimeLast).build();
        BigDecimal commissionAmountLast = agentCommissionGrantApi.agentCommissionSum(reqWithdrawVOLast);

        BigDecimal commissionAmountTemp = commissionAmountLast;
        if (commissionAmountLast.compareTo(BigDecimal.ZERO)<=0){
            commissionAmountTemp = BigDecimal.ONE;
        }
        result.setAgentCommissionAmount(commissionAmount);
        result.setAgentCommissionAmountComparePer(commissionAmount.subtract(commissionAmountLast).multiply(BigDecimal.valueOf(100)).divide(commissionAmountTemp, 2, RoundingMode.DOWN));
    }




    public ResponseVO<SiteDataCompareGraphVO> dataCompareGraph(SiteDataCompareGraphParam vo) {
        String siteCode = vo.getSiteCode();
        String timeZone = vo.getTimeZone();
        String dbZone = TimeZoneUtils.getTimeZoneUTC(timeZone);
        SiteDataCompareGraphVO result = new SiteDataCompareGraphVO();
        result.setCurrencyCode(vo.getCurrencyCode());

        SiteDataCompareGraphVO resultLast = new SiteDataCompareGraphVO();
        resultLast.setCurrencyCode(vo.getCurrencyCode());

        if (vo.getStartTime()==null){
            vo.setStartTime(System.currentTimeMillis()- 6*24*60*60*1000);
        }
        long startTime =  TimeZoneUtils.getStartOfDayInTimeZone(vo.getStartTime(), timeZone);

        if (vo.getEndTime()==null || vo.getEndTime() > TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(),timeZone)){
            vo.setEndTime(System.currentTimeMillis());
        }
        long endTime = TimeZoneUtils.getEndOfDayInTimeZone(vo.getEndTime(), timeZone);;


        List<String> dataXList = Lists.newArrayList();
        List<String> betweenDatesCurrent = TimeZoneUtils.getBetweenDates(startTime, endTime, timeZone);
        List<String> betweenDatesCurrentMMDD = getBetweenDatesMMDD(startTime, endTime, timeZone);

        long betweenDay = DateUtil.betweenDay(new DateTime(startTime), new DateTime(endTime), false);
        for (int i = 1; i <= betweenDay+1; i++) {
            dataXList.add(i + "");
        }

        long endTimeLast = startTime - 1;

        long startTimeLast = startTime - ((long)  24 *60 * 60 * 1000 * (betweenDay+1) );

        List<String> betweenDatesCurrentLast = TimeZoneUtils.getBetweenDates(startTimeLast, endTimeLast, timeZone);




        result.setDayList(betweenDatesCurrentMMDD);
        // 查询汇率
        String platCurrencyName = CurrReqUtils.getPlatCurrencyCode(); // 获取平台币种名称


        result.setPlatCurrencyName(platCurrencyName);
        switch (SiteDataCompareGraphEnum.nameOfCode(vo.getDataType().toString())) {
            case NEW_REGISTERED:
                List<GetRegisterStatisticsBySiteCodeVO> registerStatistics = getRegisterStatistics(vo.getTimeType(), startTime, endTime, siteCode, dbZone);
                //上期
                List<GetRegisterStatisticsBySiteCodeVO> registerStatisticsLast = getRegisterStatistics(vo.getTimeType(), startTimeLast, endTimeLast, siteCode, dbZone);
                // 本月新注册人数数据 如果某天没有数据，强制设置为0
                handleCurrentMonthDataRegister(result, registerStatistics, betweenDatesCurrent);
                //计算平均值
                BigDecimal registers = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal registersLast = registerStatisticsLast.stream().map(GetRegisterStatisticsBySiteCodeVO::getRegisterNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAverageValue(registers.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(registers.setScale(0, RoundingMode.DOWN));
                //计算环比上期 comparedPreviousPeriod
                BigDecimal value1 = registersLast;
                if (registersLast.compareTo(BigDecimal.ZERO)==0){
                    value1 = BigDecimal.ONE;
                }
                BigDecimal multiply = (registers.subtract(registersLast)).multiply(BigDecimal.valueOf(100)).divide(value1, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(multiply.toString());

                break;
            case FIRST_DEPOSIT:
                List<GetFirstDepositStatisticsBySiteCodeVO> firstDepositStatistics = getFirstDepositStatistic(startTime, endTime, siteCode, dbZone);
                // 本月首存人数数据 如果某天没有数据，强制设置为0
                handleCurrentMonthDataFirstDeposit(result, firstDepositStatistics, betweenDatesCurrent);
                //上期
                List<GetFirstDepositStatisticsBySiteCodeVO> firstDepositStatisticLast = getFirstDepositStatistic(startTimeLast, endTimeLast, siteCode, dbZone);
                //计算平均值
                BigDecimal  firstDepositTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal firstDepositTotalLast = firstDepositStatisticLast.stream().map(GetFirstDepositStatisticsBySiteCodeVO::getFirstDepositNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAverageValue(firstDepositTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(firstDepositTotal.setScale(0, RoundingMode.DOWN));
                //计算环比上期 comparedPreviousPeriod
                BigDecimal value2 = firstDepositTotalLast;
                if (firstDepositTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value2 = BigDecimal.ONE;
                }
                BigDecimal  firstDepositComparedPreviousPeriod = (firstDepositTotal.subtract(firstDepositTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value2, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(firstDepositComparedPreviousPeriod.toString());

                break;
            case LOGIN_COUNT:

                List<GetStatisticsBySiteCodeVO> logStatistics = getLogStatisticsByTimeType(vo.getTimeType(), startTime, endTime, siteCode, dbZone);
                handleCurrentMonthDataLog(result, logStatistics, betweenDatesCurrent);
                //上期
                List<GetStatisticsBySiteCodeVO> logStatisticsLast = getLogStatisticsByTimeType(vo.getTimeType(), startTimeLast, endTimeLast, siteCode, dbZone);
                //计算平均值
                BigDecimal  logStatisticsTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal logStatisticsTotalLast = logStatisticsLast.stream().map(GetStatisticsBySiteCodeVO::getStatisticNumber).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAverageValue(logStatisticsTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(logStatisticsTotal.setScale(0, RoundingMode.DOWN));
                //计算环比上期 comparedPreviousPeriod
                BigDecimal value3 = logStatisticsTotalLast;
                if (logStatisticsTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value3 = BigDecimal.ONE;
                }
                BigDecimal  logStatisticsComparedPreviousPeriod = (logStatisticsTotal.subtract(logStatisticsTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value3, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(logStatisticsComparedPreviousPeriod.toString());

                break;
            case DEPOSIT_AMOUNT:
                List<GetDepositStatisticsBySiteCodeVO> depositStatistics = getDepositStatistics(startTime, endTime, siteCode, dbZone, CommonConstant.business_one, vo.getCurrencyCode());
                handleCurrentMonthDataDeposit(result, depositStatistics, betweenDatesCurrent, vo);
                //上期
                List<GetDepositStatisticsBySiteCodeVO> depositStatisticsLast = getDepositStatistics(startTimeLast, endTimeLast,  siteCode, dbZone, CommonConstant.business_one, vo.getCurrencyCode());

                handleCurrentMonthDataDeposit(resultLast, depositStatisticsLast, betweenDatesCurrentLast,vo);

                //计算平均值
                BigDecimal  depositStatisticsTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal  depositStatisticsTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                result.setAverageValue(depositStatisticsTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(depositStatisticsTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value4 = depositStatisticsTotalLast;
                if (depositStatisticsTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value4 = BigDecimal.ONE;
                }
                BigDecimal  depositStatisticsComparedPreviousPeriod = (depositStatisticsTotal.subtract(depositStatisticsTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value4, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(depositStatisticsComparedPreviousPeriod.toString());

                break;
            case NUMBER_OF_DEPOSITORS:
                List<GetDepositStatisticsBySiteCodeVO> depositUserCount = getDepositWithdrawnUserCount(startTime, endTime, siteCode, dbZone, CommonConstant.business_one, vo.getCurrencyCode());
                handleDepositAndWithdrawnUserCount(result, depositUserCount, betweenDatesCurrent, vo);
                //上期
                List<GetDepositStatisticsBySiteCodeVO> depositUserCountLast = getDepositWithdrawnUserCount(startTimeLast, endTimeLast,  siteCode, dbZone, CommonConstant.business_one, vo.getCurrencyCode());
                //计算平均值
                BigDecimal  depositUserCountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal depositUserCountTotalLast = depositUserCountLast.stream().map(GetDepositStatisticsBySiteCodeVO::getUserCount).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAverageValue(depositUserCountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(depositUserCountTotal.setScale(0, RoundingMode.DOWN));


                //计算环比上期 comparedPreviousPeriod
                BigDecimal value8 = depositUserCountTotalLast;

                if (depositUserCountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value8 = BigDecimal.ONE;
                }
                BigDecimal  depositUserCountComparedPreviousPeriod = (depositUserCountTotal.subtract(depositUserCountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value8, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(depositUserCountComparedPreviousPeriod.toString());

                break;

            case WITHDRAWAL_AMOUNT:

                List<GetDepositStatisticsBySiteCodeVO> withdrawStatistics = getDepositStatistics(startTime, endTime, siteCode, dbZone, CommonConstant.business_two, vo.getCurrencyCode());
                // 本月取款数据 如果某天没有数据，强制设置为0
                handleCurrentMonthDataWithdraw(result, withdrawStatistics, betweenDatesCurrent, vo);
                //上期
                List<GetDepositStatisticsBySiteCodeVO> withdrawStatisticsLast = getDepositStatistics(startTimeLast, endTimeLast, siteCode, dbZone, CommonConstant.business_two, vo.getCurrencyCode());
                //计算平均值
                handleCurrentMonthDataWithdraw(resultLast, withdrawStatisticsLast, betweenDatesCurrentLast,vo);


                BigDecimal  withdrawStatisticsTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal  withdrawStatisticsTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                result.setAverageValue(withdrawStatisticsTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(withdrawStatisticsTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod

                BigDecimal value5 = withdrawStatisticsTotalLast;
                if (withdrawStatisticsTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value5 = BigDecimal.ONE;
                }
                BigDecimal  withdrawStatisticsComparedPreviousPeriod = (withdrawStatisticsTotal.subtract(withdrawStatisticsTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value5, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(withdrawStatisticsComparedPreviousPeriod.toString());

                break;
            case NUMBER_OF_WITHDRAWALS:
                List<GetDepositStatisticsBySiteCodeVO> withdrawnUserCount = getDepositWithdrawnUserCount(startTime, endTime, siteCode, dbZone, CommonConstant.business_two, vo.getCurrencyCode());
                handleDepositAndWithdrawnUserCount(result, withdrawnUserCount, betweenDatesCurrent, vo);
                //上期
                List<GetDepositStatisticsBySiteCodeVO> withdrawnUserCountLast = getDepositWithdrawnUserCount(startTimeLast, endTimeLast,  siteCode, dbZone, CommonConstant.business_two, vo.getCurrencyCode());
                //计算平均值
                BigDecimal  withdrawnUserCountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal withdrawnUserCountTotalLast = withdrawnUserCountLast.stream().map(GetDepositStatisticsBySiteCodeVO::getUserCount).reduce(BigDecimal.ZERO, BigDecimal::add);
                result.setAverageValue(withdrawnUserCountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(withdrawnUserCountTotal.setScale(0, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value9 = withdrawnUserCountTotalLast;
                if (withdrawnUserCountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value9 = BigDecimal.ONE;
                }
                BigDecimal  withdrawnUserCountComparedPreviousPeriod = (withdrawnUserCountTotal.subtract(withdrawnUserCountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value9, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(withdrawnUserCountComparedPreviousPeriod.toString());

                break;

            case NUMBER_OF_BETTORS:

                ReportUserWinLossRebateParamVO userBetCount = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> userBetNumByDay = reportUserVenueWinLoseApi.getUserBetNumByDay(userBetCount);

                ReportUserWinLossRebateParamVO userBetCountLast = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> userBetNumByDayLast = reportUserVenueWinLoseApi.getUserBetNumByDay(userBetCountLast);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleUserBetNumByDay(result, userBetNumByDay, betweenDatesCurrent, NUMBER_OF_BETTORS);

                //计算平均值
                BigDecimal  userBetNumTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal userBetNumTotalLast = userBetNumByDayLast.stream().map(SiteReportUserVenueStaticsVO::getUserCount).reduce(BigDecimal.ZERO, BigDecimal::add);;

                result.setAverageValue(userBetNumTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(userBetNumTotal.setScale(0, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value7 = userBetNumTotalLast;
                if (userBetNumTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value7 = BigDecimal.ONE;
                }
                BigDecimal  userBetNumComparedPreviousPeriod = (userBetNumTotal.subtract(userBetNumTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value7, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(userBetNumComparedPreviousPeriod.toString());

                break;

            case NUMBER_OF_VALID_BETS:

                ReportUserWinLossRebateParamVO validBets = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> validBetsList = reportUserVenueWinLoseApi.getUserBetNumByDay(validBets);

                ReportUserWinLossRebateParamVO validBetsLast = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> validBetsListLast = reportUserVenueWinLoseApi.getUserBetNumByDay(validBetsLast);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleUserBetNumByDay(result, validBetsList, betweenDatesCurrent,NUMBER_OF_VALID_BETS);

                //计算平均值
                BigDecimal  validBetsTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal validBetsTotalLast = validBetsListLast.stream().map(SiteReportUserVenueStaticsVO::getBetCount).reduce(BigDecimal.ZERO, BigDecimal::add);

                result.setAverageValue(validBetsTotal.divide(BigDecimal.valueOf(dataXList.size()), 2, RoundingMode.DOWN).toString());
                result.setTotal(validBetsTotal.setScale(0, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value13 = validBetsTotalLast;
                if (validBetsTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value13 = BigDecimal.ONE;
                }
                BigDecimal  validBetsComparedPreviousPeriod = (validBetsTotal.subtract(validBetsTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value13, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(validBetsComparedPreviousPeriod.toString());

                break;
            case EFFECTIVE_BET_AMOUNT:

                ReportUserWinLossRebateParamVO validBetAmount = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).currencyCode(vo.getCurrencyCode()).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> validBetAmountList = reportUserVenueWinLoseApi.getDailyCurrencyAmount(validBetAmount);

                ReportUserWinLossRebateParamVO validBetAmountLast = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).currencyCode(vo.getCurrencyCode()).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> validBetAmountListLast = reportUserVenueWinLoseApi.getDailyCurrencyAmount(validBetAmountLast);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleBetAmountAndWinLoseAmountByDay(result, validBetAmountList, betweenDatesCurrent,EFFECTIVE_BET_AMOUNT);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleBetAmountAndWinLoseAmountByDay(resultLast, validBetAmountListLast, betweenDatesCurrentLast,EFFECTIVE_BET_AMOUNT);
                //计算平均值
                BigDecimal  validBetAmountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal  validBetAmountTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                result.setAverageValue(validBetAmountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(validBetAmountTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value14 = validBetAmountTotalLast;
                if (validBetAmountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value14 = BigDecimal.ONE;
                }
                BigDecimal  validBetAmountComparedPreviousPeriod = (validBetAmountTotal.subtract(validBetAmountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value14, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(validBetAmountComparedPreviousPeriod.toString());
                break;


            case GAME_WINS_LOSSES:

                ReportUserWinLossRebateParamVO gameWinLoseAmount = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> gameWinLoseAmountList = reportUserVenueWinLoseApi.getDailyCurrencyAmount(gameWinLoseAmount);

                ReportUserWinLossRebateParamVO gameWinLoseAmountLast = ReportUserWinLossRebateParamVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).timezone(dbZone).build();
                List<SiteReportUserVenueStaticsVO> gameWinLoseAmountLastListLast = reportUserVenueWinLoseApi.getDailyCurrencyAmount(gameWinLoseAmountLast);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleBetAmountAndWinLoseAmountByDay(result, gameWinLoseAmountList, betweenDatesCurrent,GAME_WINS_LOSSES);
                handleBetAmountAndWinLoseAmountByDay(resultLast, gameWinLoseAmountLastListLast, betweenDatesCurrentLast,GAME_WINS_LOSSES);

                //计算平均值
                BigDecimal  gameWinLoseAmountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal gameWinLoseAmountTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                result.setAverageValue(gameWinLoseAmountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(gameWinLoseAmountTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value10 = gameWinLoseAmountTotalLast;
                if (gameWinLoseAmountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value10 = BigDecimal.ONE;
                }
                BigDecimal  gameWinLoseAmountComparedPreviousPeriod = (gameWinLoseAmountTotal.subtract(gameWinLoseAmountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value10, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(gameWinLoseAmountComparedPreviousPeriod.toString());
                break;

            case PLATFORM_WINS_LOSSES:

                DailyWinLoseVO dailyWinLoseVO = DailyWinLoseVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).currencyCode(vo.getCurrencyCode()).timezone(dbZone).build();

                List<DailyWinLoseResponseVO> platWinLoseAmount = reportUserWinLoseApi.dailyWinLoseCurrency(dailyWinLoseVO);

                DailyWinLoseVO dailyWinLoseVOLast = DailyWinLoseVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).currencyCode(vo.getCurrencyCode()).timezone(dbZone).build();
                List<DailyWinLoseResponseVO> platWinLoseAmountLast = reportUserWinLoseApi.dailyWinLoseCurrency(dailyWinLoseVOLast);

                handleUserWinLoseAmountByDay(result, platWinLoseAmount, betweenDatesCurrent,PLATFORM_WINS_LOSSES);
                handleUserWinLoseAmountByDay(resultLast, platWinLoseAmountLast, betweenDatesCurrentLast,PLATFORM_WINS_LOSSES);
                //计算平均值
                BigDecimal  platWinLoseAmountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal  platWinLoseAmountTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                result.setAverageValue(platWinLoseAmountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(platWinLoseAmountTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value12 = platWinLoseAmountTotalLast;
                if (platWinLoseAmountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value12 = BigDecimal.ONE;
                }
                BigDecimal  platWinLoseAmountComparedPreviousPeriod = (platWinLoseAmountTotal.subtract(platWinLoseAmountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value12, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(platWinLoseAmountComparedPreviousPeriod.toString());
                break;

            case PLATFORM_NET_WINS_LOSSES:

                DailyWinLoseVO dailyNetWinLoseVO = DailyWinLoseVO.builder().siteCode(siteCode).startTime(startTime).endTime(endTime).timezone(dbZone).build();

                List<DailyWinLoseResponseVO> platNetWinLoseAmount = reportUserWinLoseApi.dailyWinLoseCurrency(dailyNetWinLoseVO);

                DailyWinLoseVO dailyNetWinLoseVOLast = DailyWinLoseVO.builder().siteCode(siteCode).startTime(startTimeLast).endTime(endTimeLast).timezone(dbZone).build();
                List<DailyWinLoseResponseVO> platNetWinLoseAmountLast = reportUserWinLoseApi.dailyWinLoseCurrency(dailyNetWinLoseVOLast);

                // 本月取款数据 如果某天没有数据，强制设置为0
                handleUserWinLoseAmountByDay(result, platNetWinLoseAmount, betweenDatesCurrent,PLATFORM_NET_WINS_LOSSES);
                handleUserWinLoseAmountByDay(resultLast, platNetWinLoseAmountLast, betweenDatesCurrentLast,PLATFORM_NET_WINS_LOSSES);

                //计算平均值
                BigDecimal  platNetWinLoseAmountTotal = result.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
                BigDecimal  platNetWinLoseAmountTotalLast = resultLast.getCurrentData().stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO);

                result.setAverageValue(platNetWinLoseAmountTotal.divide(BigDecimal.valueOf(dataXList.size()), 4, RoundingMode.DOWN).toString());
                result.setTotal(platNetWinLoseAmountTotal.setScale(4, RoundingMode.DOWN));

                //计算环比上期 comparedPreviousPeriod
                BigDecimal value11 = platNetWinLoseAmountTotalLast;
                if (platNetWinLoseAmountTotalLast.compareTo(BigDecimal.ZERO)==0){
                    value11 = BigDecimal.ONE;
                }
                BigDecimal  platNetWinLoseAmountComparedPreviousPeriod = (platNetWinLoseAmountTotal.subtract(platNetWinLoseAmountTotalLast)).multiply(BigDecimal.valueOf(100)).divide(value11, 2, RoundingMode.UP);
                result.setComparedPreviousPeriod(platNetWinLoseAmountComparedPreviousPeriod.toString());
                break;
        }


        if (StrUtil.isEmpty(vo.getCurrencyCode())){
            result.setCurrencyCode("WTC");
        }else {
            result.setCurrencyCode(vo.getCurrencyCode());
        }
        result.setCompareData(resultLast.getCurrentData());
        return ResponseVO.success(result);
    }


    /**
     * 方法用于获取指定时间范围的数据¬
     * type 1存款 2取款
     */
    private List<GetWinLoseStatisticsBySiteCodeVO> getWinLoseStatistics(int timeType, long startTime, long endTime, String siteCode, String dbZone, String currencyCode) {
        currencyCode = currencyCode.equals(CurrReqUtils.getPlatCurrencyName()) ? null : currencyCode;
        //if (timeType == 1) {
           // return reportUserWinLoseAgentApi.getWinLoseStatisticsBySiteCodeHour(startTime, endTime, siteCode, dbZone, currencyCode);
        //} else if (timeType == 2) {
            return reportUserWinLoseAgentApi.getWinLoseStatisticsBySiteCode(startTime, endTime, siteCode, dbZone, currencyCode);
        //} else {
           // return reportUserWinLoseAgentApi.getWinLoseStatisticsBySiteCodeMonth(startTime, endTime, siteCode, dbZone, currencyCode);
        //}

    }

    /**
     * 把不同币种，都转换为平台币
     *
     * @return
     */
    private List<GetWinLoseStatisticsBySiteCodeVO> calculateWinLoseStatistics(List<GetWinLoseStatisticsBySiteCodeVO> records, List<String> betweenDate, Map<String, BigDecimal> allFinalRate) {
        List<GetWinLoseStatisticsBySiteCodeVO> results = new ArrayList<>();
        for (String statistics : betweenDate) {
            GetWinLoseStatisticsBySiteCodeVO result = new GetWinLoseStatisticsBySiteCodeVO();
            result.setMyDay(statistics);
            AtomicReference<BigDecimal> amountAll = new AtomicReference<>(BigDecimal.ZERO);
            for (GetWinLoseStatisticsBySiteCodeVO record : records) {
                // 找到这一天，转换为平台币
                if (statistics.equals(record.getMyDay())) {
                    BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
                    if (rate == null) {
                        throw new BaowangDefaultException("汇率未配置，货币是:" + record.getCurrencyCode());
                    }
                    amountAll.updateAndGet(v -> v.add(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                }
            }
            result.setBetWinLose(amountAll.get());
            results.add(result);
        }
        return results;
    }

    /**
     * 把不同币种，都转换为平台币
     *
     * @return
     */
    private List<GetDepositStatisticsBySiteCodeVO> calculateWithdrawStatistics(List<GetDepositStatisticsBySiteCodeVO> records, List<String> betweenDate, Map<String, BigDecimal> allFinalRate) {
        List<GetDepositStatisticsBySiteCodeVO> results = new ArrayList<>();
        for (String statistics : betweenDate) {
            GetDepositStatisticsBySiteCodeVO result = new GetDepositStatisticsBySiteCodeVO();
            result.setMyDay(statistics);
            AtomicReference<BigDecimal> amountAll = new AtomicReference<>(BigDecimal.ZERO);
            for (GetDepositStatisticsBySiteCodeVO record : records) {
                // 找到这一天，转换为平台币
                if (statistics.equals(record.getMyDay())) {
                    BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
                    if (rate == null) {
                        throw new BaowangDefaultException("汇率未配置，货币是:" + record.getCurrencyCode());
                    }
                    amountAll.updateAndGet(v -> v.add(record.getWithdrawAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                }
            }
            result.setWithdrawAmount(amountAll.get());
            results.add(result);
        }
        return results;
    }

    /**
     * 把不同币种，都转换为平台币
     *
     * @return
     */
    private List<GetDepositStatisticsBySiteCodeVO> calculateDepositStatistics(List<GetDepositStatisticsBySiteCodeVO> records, List<String> betweenDate, Map<String, BigDecimal> allFinalRate) {
        List<GetDepositStatisticsBySiteCodeVO> results = new ArrayList<>();
        for (String statistics : betweenDate) {
            GetDepositStatisticsBySiteCodeVO result = new GetDepositStatisticsBySiteCodeVO();
            result.setMyDay(statistics);
            AtomicReference<BigDecimal> amountAll = new AtomicReference<>(BigDecimal.ZERO);
            for (GetDepositStatisticsBySiteCodeVO record : records) {
                // 找到这一天，转换为平台币
                if (statistics.equals(record.getMyDay())) {
                    BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
                    if (rate == null) {
                        //todo
                        throw new BaowangDefaultException("汇率未配置，货币是:" + record.getCurrencyCode());
                    }
                    amountAll.updateAndGet(v -> v.add(record.getDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));

                }
            }
            result.setDepositAmount(amountAll.get());
            results.add(result);
        }
        return results;
    }

    /**
     * 本月存款数据 如果某天没有数据，强制设置为0
     *
     * @param result            结果
     * @param depositStatistics 查询的结果
     */
    private void handleCurrentMonthDataDeposit(SiteDataCompareGraphVO result, List<GetDepositStatisticsBySiteCodeVO> depositStatistics, List<String> betweenDate, SiteDataCompareGraphParam vo) {
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode()); // 获取所有币种汇率
        // 对depositStatistics 进行聚和，同一天的数据，进行聚合
        List<GetDepositStatisticsBySiteCodeVO> newDepositStatistics = new ArrayList<>();


        for (String date : betweenDate) {
            boolean flag = false;
            if (CollectionUtils.isEmpty(depositStatistics)) {
                newDepositStatistics = Lists.newArrayList();
            } else {
                AtomicReference<BigDecimal> depositAmount = new AtomicReference<>(BigDecimal.ZERO);
                for (GetDepositStatisticsBySiteCodeVO item : depositStatistics) {
                    if (date.equals(item.getMyDay())) {
                        // 不止一个，有多个，需要进行聚合
                        BigDecimal rate;;
                        if (StrUtil.isEmpty(result.getCurrencyCode()) || !result.getCurrencyCode().equals(item.getCurrencyCode())) {
                            if (null == allFinalRate.get(item.getCurrencyCode())) {
                                throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                            }
                            rate = allFinalRate.get(item.getCurrencyCode());
                        } else {
                            rate = BigDecimal.ONE;
                        }
                        // 如果需要指定币种
                        if (StringUtils.hasText(vo.getCurrencyCode()) && !vo.getCurrencyCode().equals(item.getCurrencyCode())) {
                            continue;
                        }
                        depositAmount.updateAndGet(v -> v.add(item.getDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                        flag = true;
                    }
                }
                //
                if (flag) {
                    GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                    record.setMyDay(date);
                    record.setDepositAmount(depositAmount.get().setScale(4, RoundingMode.DOWN));
                    newDepositStatistics.add(record);
                }

            }

            if (!flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setDepositAmount(BigDecimal.ZERO);
                newDepositStatistics.add(record);
            }
        }
        // 对depositStatistics重新排序
        newDepositStatistics = newDepositStatistics.stream().sorted(Comparator.comparingInt(GetDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = newDepositStatistics.stream().map(GetDepositStatisticsBySiteCodeVO::getDepositAmount).toList();
        result.setCurrentData(currentMonthData);
    }

    /**
     *   存取用户人数
     */
    private void handleDepositAndWithdrawnUserCount(SiteDataCompareGraphVO result, List<GetDepositStatisticsBySiteCodeVO> depositStatistics, List<String> betweenDate, SiteDataCompareGraphParam vo) {

        if (CollectionUtils.isEmpty(depositStatistics)) {
            depositStatistics = Lists.newArrayList();
        }
        for (String dateStr : betweenDate) {
            boolean flag = false;

            for (GetDepositStatisticsBySiteCodeVO itemLast : depositStatistics) {
                if (dateStr.equals(itemLast.getMyDay())) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(dateStr);
                record.setUserCount(BigDecimal.ZERO);
                depositStatistics.add(record);
            }
        }
        // 对depositStatistics重新排序
        List<GetDepositStatisticsBySiteCodeVO> list = depositStatistics.stream().sorted(Comparator.comparingInt(GetDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = list.stream().map(GetDepositStatisticsBySiteCodeVO::getUserCount).toList();
        result.setCurrentData(currentMonthData);
    }


    /**
     * 上月存款数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param betweenDates
     * @param depositStatisticsLast
     */
    private void handleCompareMonthDataDeposit(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetDepositStatisticsBySiteCodeVO> depositStatisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            if (CollectionUtils.isEmpty(depositStatisticsLast)) {
                depositStatisticsLast = Lists.newArrayList();
            } else {
                for (GetDepositStatisticsBySiteCodeVO itemLast : depositStatisticsLast) {
                    if (betweenDate.equals(itemLast.getMyDay())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setDepositAmount(BigDecimal.ZERO);
                depositStatisticsLast.add(record);
            }
        }
        // 对depositStatisticsLast重新排序
        depositStatisticsLast = depositStatisticsLast.stream().sorted(Comparator.comparingInt(GetDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = depositStatisticsLast.stream().map(GetDepositStatisticsBySiteCodeVO::getDepositAmount).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 上月取款数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param betweenDates
     * @param withdrawStatisticsLast
     */
    private void handleCompareMonthDataWithdraw(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetDepositStatisticsBySiteCodeVO> withdrawStatisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            if (CollectionUtils.isEmpty(withdrawStatisticsLast)) {
                withdrawStatisticsLast = Lists.newArrayList();
            } else {
                for (GetDepositStatisticsBySiteCodeVO itemLast : withdrawStatisticsLast) {
                    if (betweenDate.equals(itemLast.getMyDay())) {
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setWithdrawAmount(BigDecimal.ZERO);
                withdrawStatisticsLast.add(record);
            }
        }
        // 对withdrawStatisticsLast重新排序
        withdrawStatisticsLast = withdrawStatisticsLast.stream().sorted(Comparator.comparingInt(GetDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = withdrawStatisticsLast.stream().map(GetDepositStatisticsBySiteCodeVO::getWithdrawAmount).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 本月取款数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param withdrawStatistics
     */
    private void handleCurrentMonthDataWithdraw(SiteDataCompareGraphVO result, List<GetDepositStatisticsBySiteCodeVO> withdrawStatistics, List<String> betweenDate, SiteDataCompareGraphParam vo) {
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode()); // 获取所有币种汇率
        // 对depositStatistics 进行聚和，同一天的数据，进行聚合
        List<GetDepositStatisticsBySiteCodeVO> newWithdrawStatistics = new ArrayList<>();
        if (CollectionUtils.isEmpty(withdrawStatistics)) {
            newWithdrawStatistics = Lists.newArrayList();
        }
        for (String date : betweenDate) {
            boolean flag = false;
            AtomicReference<BigDecimal> withdrawAmount = new AtomicReference<>(BigDecimal.ZERO);
            for (GetDepositStatisticsBySiteCodeVO item : withdrawStatistics) {
                if (date.equals(item.getMyDay())) {
                    // 不止一个，有多个，需要进行聚合
                    BigDecimal rate;
                    if (StrUtil.isEmpty(result.getCurrencyCode()) || !result.getCurrencyCode().equals(item.getCurrencyCode())) {
                        if (null == allFinalRate.get(item.getCurrencyCode())) {
                            throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                        }
                        rate = allFinalRate.get(item.getCurrencyCode());
                    } else {
                        rate = BigDecimal.ONE;
                    }
                    // 如果需要指定币种
                    if (StringUtils.hasText(vo.getCurrencyCode()) && !vo.getCurrencyCode().equals(item.getCurrencyCode())) {
                        continue;
                    }
                    withdrawAmount.updateAndGet(v -> v.add(item.getWithdrawAmount().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));

                    flag = true;

                }
            }
            //
            if (flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setWithdrawAmount(withdrawAmount.get().setScale(4, RoundingMode.DOWN));
                newWithdrawStatistics.add(record);
            }
            if (!flag) {
                GetDepositStatisticsBySiteCodeVO record = new GetDepositStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setWithdrawAmount(BigDecimal.ZERO);
                newWithdrawStatistics.add(record);
            }
        }
        // 对withdrawStatistics重新排序
        newWithdrawStatistics = newWithdrawStatistics.stream().sorted(Comparator.comparingInt(GetDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = newWithdrawStatistics.stream().map(GetDepositStatisticsBySiteCodeVO::getWithdrawAmount).toList();
        result.setCurrentData(currentMonthData);
    }

    /**
     *  处理投注人数和投注注单数量
     */
    private void handleUserBetNumByDay(SiteDataCompareGraphVO result, List<SiteReportUserVenueStaticsVO> list, List<String> betweenDate, SiteDataCompareGraphEnum graphEnum) {

        if (CollectionUtils.isEmpty(list)) {
            list = Lists.newArrayList();
        }
        for (String date : betweenDate) {
            boolean flag = false;
            for (SiteReportUserVenueStaticsVO item : list) {
                if (date.equals(item.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                SiteReportUserVenueStaticsVO record = new SiteReportUserVenueStaticsVO();
                record.setMyDay(date);
                record.setDayMillis(DateUtil.parse(date).getTime());
                record.setUserCount(BigDecimal.ZERO);
                record.setBetCount(BigDecimal.ZERO);
                list.add(record);
            }
        }
        // 对firstDepositStatistics重新排序
        List<SiteReportUserVenueStaticsVO> sortList = list.stream().sorted(Comparator.comparingLong(SiteReportUserVenueStaticsVO::getDayMillis)).toList();
        List<BigDecimal> currentMonthData = new ArrayList<>();
        switch (graphEnum){
            case NUMBER_OF_BETTORS -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getUserCount).toList();
            case NUMBER_OF_VALID_BETS -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getBetCount).toList();
        }
        result.setCurrentData(currentMonthData);
    }

    /**
     *  处理投注金额和输赢金额
     */
    private void handleBetAmountAndWinLoseAmountByDay(SiteDataCompareGraphVO result, List<SiteReportUserVenueStaticsVO> list, List<String> betweenDate, SiteDataCompareGraphEnum graphEnum) {

        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode()); // 获取所有币种汇率
        // 对depositStatistics 进行聚和，同一天的数据，进行聚合
        List<SiteReportUserVenueStaticsVO> newList = new ArrayList<>();
        if (CollectionUtils.isEmpty(list)) {
            list = Lists.newArrayList();
        }
        for (String date : betweenDate) {
            AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);
            for (SiteReportUserVenueStaticsVO item : list) {
                if (date.equals(item.getMyDay())) {

                    BigDecimal rate;
                    if (StrUtil.isEmpty(result.getCurrencyCode()) || !result.getCurrencyCode().equals(item.getCurrencyCode())) {
                        if (null == allFinalRate.get(item.getCurrencyCode())) {
                            throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                        }
                        rate = allFinalRate.get(item.getCurrencyCode());
                    } else {
                        rate = BigDecimal.ONE;
                    }

                    if (graphEnum==EFFECTIVE_BET_AMOUNT){
                        amount.updateAndGet(v -> v.add(item.getValidAmount().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                    }else if (graphEnum==GAME_WINS_LOSSES){
                        amount.updateAndGet(v -> v.add(item.getWinLoseAmount().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                    }
                }
            }
            SiteReportUserVenueStaticsVO temp = new SiteReportUserVenueStaticsVO();
            temp.setMyDay(date);
            temp.setDayMillis(DateUtil.parse(date).getTime());
            temp.setValidAmount(amount.get());
            temp.setWinLoseAmount(amount.get());
            newList.add(temp);
        }
        // 重新排序
        List<SiteReportUserVenueStaticsVO> sortList = newList.stream().sorted(Comparator.comparingLong(SiteReportUserVenueStaticsVO::getDayMillis)).toList();
        List<BigDecimal> currentMonthData = new ArrayList<>();
        switch (graphEnum){
            case EFFECTIVE_BET_AMOUNT -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getValidAmount).toList();
            case GAME_WINS_LOSSES -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getWinLoseAmount).toList();
        }
        result.setCurrentData(currentMonthData);
    }

    /**
     *  处理投注金额和输赢金额
     */
    private void handleUserWinLoseAmountByDay(SiteDataCompareGraphVO result, List<DailyWinLoseResponseVO> list, List<String> betweenDate, SiteDataCompareGraphEnum graphEnum) {

        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode()); // 获取所有币种汇率
        // 对depositStatistics 进行聚和，同一天的数据，进行聚合
        List<SiteReportUserVenueStaticsVO> newList = new ArrayList<>();
        if (list==null) {
            list = Lists.newArrayList();
        }
        for (String date : betweenDate) {
            AtomicReference<BigDecimal> amount = new AtomicReference<>(BigDecimal.ZERO);
            for (DailyWinLoseResponseVO item : list) {
                if (date.equals(item.getDayStr())) {
                    BigDecimal rate;
                    if (StrUtil.isEmpty(result.getCurrencyCode()) || !result.getCurrencyCode().equals(item.getMainCurrency())) {
                        if (null == allFinalRate.get(item.getMainCurrency())) {
                            throw new BaowangDefaultException("汇率未配置，货币是:" + item.getMainCurrency());
                        }
                        rate = allFinalRate.get(item.getMainCurrency());
                    } else {
                        rate = BigDecimal.ONE;
                    }
                    if (graphEnum==PLATFORM_NET_WINS_LOSSES){
                        amount.updateAndGet(v -> v.add(item.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                    }else if (graphEnum==PLATFORM_WINS_LOSSES){
                        amount.updateAndGet(v -> v.add(item.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));
                    }
                }
            }
            SiteReportUserVenueStaticsVO temp = new SiteReportUserVenueStaticsVO();
            temp.setMyDay(date);
            temp.setDayMillis(DateUtil.parse(date).getTime());
            temp.setPlatWinLose(amount.get());
            temp.setPlatNetWinLose(amount.get());
            newList.add(temp);
        }
        // 重新排序
        List<SiteReportUserVenueStaticsVO> sortList = newList.stream().sorted(Comparator.comparingLong(SiteReportUserVenueStaticsVO::getDayMillis)).toList();
        List<BigDecimal> currentMonthData = new ArrayList<>();
        switch (graphEnum){
            case PLATFORM_WINS_LOSSES -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getPlatWinLose).toList();
            case PLATFORM_NET_WINS_LOSSES -> currentMonthData = sortList.stream().map(SiteReportUserVenueStaticsVO::getPlatNetWinLose).toList();
        }
        result.setCurrentData(currentMonthData);
    }


    /**
     * 上月总输赢数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param betweenDates
     * @param winLoseStatisticsLast
     */
    private void handleCompareMonthDataWinLose(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetWinLoseStatisticsBySiteCodeVO> winLoseStatisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            if (CollectionUtils.isEmpty(winLoseStatisticsLast)) {
                winLoseStatisticsLast = Lists.newArrayList();
            } else {
                for (GetWinLoseStatisticsBySiteCodeVO itemLast : winLoseStatisticsLast) {
                    if (betweenDate.equals(itemLast.getMyDay())) {
                        flag = true;
                        break;
                    }
                }
            }

            if (!flag) {
                GetWinLoseStatisticsBySiteCodeVO record = new GetWinLoseStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setBetWinLose(BigDecimal.ZERO);
                winLoseStatisticsLast.add(record);
            }
        }
        // 对winLoseStatisticsLast重新排序
        winLoseStatisticsLast = winLoseStatisticsLast.stream().sorted(Comparator.comparingInt(GetWinLoseStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = winLoseStatisticsLast.stream().map(GetWinLoseStatisticsBySiteCodeVO::getBetWinLose).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 本月总输赢数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param winLoseStatistics
     */
    private void handleCurrentMonthDataWinLose(SiteDataCompareGraphVO result, List<GetWinLoseStatisticsBySiteCodeVO> winLoseStatistics, List<String> betweenDate, SiteDataCompareGraphParam vo) {
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(CurrReqUtils.getSiteCode()); // 获取所有币种汇率
        // 对depositStatistics 进行聚和，同一天的数据，进行聚合
        List<GetWinLoseStatisticsBySiteCodeVO> newWinLoseStatistics = new ArrayList<>();
        for (String date : betweenDate) {
            boolean flag = false;
            if (CollectionUtils.isEmpty(winLoseStatistics)) {
                newWinLoseStatistics = Lists.newArrayList();
            } else {
                AtomicReference<BigDecimal> betWinLose = new AtomicReference<>(BigDecimal.ZERO);
                for (GetWinLoseStatisticsBySiteCodeVO item : winLoseStatistics) {
                    if (date.equals(item.getMyDay())) {
                        // 不止一个，有多个，需要进行聚合
                        BigDecimal rate;
                        if (vo.getConvertPlatCurrency()) {
                            if (null == allFinalRate.get(item.getCurrencyCode())) {
                                throw new BaowangDefaultException("汇率未配置，货币是:" + item.getCurrencyCode());
                            }
                            rate = allFinalRate.get(item.getCurrencyCode());
                        } else {
                            rate = BigDecimal.ONE;
                        }
                        // 如果需要指定币种
                        if (StringUtils.hasText(vo.getCurrencyCode()) && !vo.getCurrencyCode().equals(item.getCurrencyCode())) {
                            continue;
                        }
                        betWinLose.updateAndGet(v -> v.add(item.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.DOWN)));

                        flag = true;

                    }
                }
                if (flag) {
                    GetWinLoseStatisticsBySiteCodeVO record = new GetWinLoseStatisticsBySiteCodeVO();
                    record.setMyDay(date);
                    record.setBetWinLose(betWinLose.get().setScale(2, RoundingMode.DOWN));
                    newWinLoseStatistics.add(record);
                }
            }

            if (!flag) {
                GetWinLoseStatisticsBySiteCodeVO record = new GetWinLoseStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setBetWinLose(BigDecimal.ZERO);
                newWinLoseStatistics.add(record);
            }
        }
        // 对winLoseStatistics重新排序
        newWinLoseStatistics = newWinLoseStatistics.stream().sorted(Comparator.comparingInt(GetWinLoseStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = newWinLoseStatistics.stream().map(GetWinLoseStatisticsBySiteCodeVO::getBetWinLose).toList();
        result.setCurrentData(currentMonthData);
    }

    /**
     * 方法用于获取指定时间范围的数据¬
     * type 1存款 2取款
     */
    private List<GetDepositStatisticsBySiteCodeVO> getDepositStatistics(long startTime, long endTime, String siteCode, String dbZone, Integer type, String currency) {

        return userDepositWithdrawApi.getDepositStatisticsBySiteCode(siteCode, startTime, endTime, type, dbZone, currency).getData();
    }

    /**
     * 方法用于获取指定时间范围的数据¬
     * type 1存款 2取款
     */
    private List<GetDepositStatisticsBySiteCodeVO> getDepositWithdrawnUserCount(long startTime, long endTime, String siteCode, String dbZone, Integer type, String currency) {
        return userDepositWithdrawApi.getDepositWithdrawnUserCountBySiteCode(siteCode, startTime, endTime, type, dbZone, currency).getData();
    }

    /**
     * 方法用于获取指定时间范围的数据¬
     */
    private List<GetRegisterStatisticsBySiteCodeVO> getRegisterStatistics(int timeType, long startTime, long endTime, String siteCode, String dbZone) {
        return userInfoRepository.getRegisterStatisticsBySiteCode(startTime, endTime, siteCode, dbZone);
    }

    /**
     * 方法用于获取指定时间范围的数据¬
     */
    private List<GetFirstDepositStatisticsBySiteCodeVO> getFirstDepositStatistic(long startTime, long endTime, String siteCode, String dbZone) {
        return userInfoRepository.getFirstDepositStatisticsBySiteCode(startTime, endTime, siteCode, dbZone);
    }

    /**
     * 方法用于获取指定时间范围的数据¬
     */
    private List<GetStatisticsBySiteCodeVO> getLogStatisticsByTimeType(int timeType, long startTime, long endTime, String siteCode, String dbZone) {

       return userLoginInfoRepository.getLogStatisticsBySiteCode(startTime, endTime, siteCode, dbZone);
    }

    /**
     * 处理当前数据
     */
    private void handleCurrentMonthDataLog(SiteDataCompareGraphVO result, List<GetStatisticsBySiteCodeVO> statistics, List<String> betweenDate) {
        for (String date : betweenDate) {
            boolean flag = false;
            for (GetStatisticsBySiteCodeVO item : statistics) {
                if (date.equals(item.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetStatisticsBySiteCodeVO record = new GetStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setStatisticNumber(BigDecimal.ZERO);
                statistics.add(record);
            }
        }
        // 对firstDepositStatistics重新排序
        statistics = statistics.stream().sorted(Comparator.comparingInt(GetStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = statistics.stream().map(GetStatisticsBySiteCodeVO::getStatisticNumber).toList();
        result.setCurrentData(currentMonthData);
    }

    /**
     * 处理比较数据
     *
     * @param result
     * @param betweenDates
     * @param statisticsLast
     */
    private void handleCompareMonthDataLog(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetStatisticsBySiteCodeVO> statisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            for (GetStatisticsBySiteCodeVO itemLast : statisticsLast) {
                if (betweenDate.equals(itemLast.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetStatisticsBySiteCodeVO record = new GetStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setStatisticNumber(BigDecimal.ZERO);
                statisticsLast.add(record);
            }
        }
        // 对firstDepositStatisticsLast重新排序
        statisticsLast = statisticsLast.stream().sorted(Comparator.comparingInt(GetStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = statisticsLast.stream().map(GetStatisticsBySiteCodeVO::getStatisticNumber).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 本月首存人数数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param firstDepositStatistics
     */
    private void handleCurrentMonthDataFirstDeposit(SiteDataCompareGraphVO result, List<GetFirstDepositStatisticsBySiteCodeVO> firstDepositStatistics, List<String> betweenDate) {
        for (String date : betweenDate) {
            boolean flag = false;
            for (GetFirstDepositStatisticsBySiteCodeVO item : firstDepositStatistics) {
                if (date.equals(item.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetFirstDepositStatisticsBySiteCodeVO record = new GetFirstDepositStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setFirstDepositNumber(BigDecimal.ZERO);
                firstDepositStatistics.add(record);
            }
        }
        // 对firstDepositStatistics重新排序
        firstDepositStatistics = firstDepositStatistics.stream().sorted(Comparator.comparingInt(GetFirstDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = firstDepositStatistics.stream().map(GetFirstDepositStatisticsBySiteCodeVO::getFirstDepositNumber).toList();
        result.setCurrentData(currentMonthData);
    }

    /**
     * 上月首存人数数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param betweenDates
     * @param firstDepositStatisticsLast
     */
    private void handleCompareMonthDataFirstDeposit(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetFirstDepositStatisticsBySiteCodeVO> firstDepositStatisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            for (GetFirstDepositStatisticsBySiteCodeVO itemLast : firstDepositStatisticsLast) {
                if (betweenDate.equals(itemLast.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetFirstDepositStatisticsBySiteCodeVO record = new GetFirstDepositStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setFirstDepositNumber(BigDecimal.ZERO);
                firstDepositStatisticsLast.add(record);
            }
        }
        // 对firstDepositStatisticsLast重新排序
        firstDepositStatisticsLast = firstDepositStatisticsLast.stream().sorted(Comparator.comparingInt(GetFirstDepositStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = firstDepositStatisticsLast.stream().map(GetFirstDepositStatisticsBySiteCodeVO::getFirstDepositNumber).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 上月新注册人数数据 如果某天没有数据，强制设置为0
     *
     * @param result
     * @param betweenDates
     * @param registerStatisticsLast
     */
    private void handleCompareMonthDataRegister(SiteDataCompareGraphVO result, List<String> betweenDates, List<GetRegisterStatisticsBySiteCodeVO> registerStatisticsLast) {
        for (String betweenDate : betweenDates) {
            boolean flag = false;
            for (GetRegisterStatisticsBySiteCodeVO itemLast : registerStatisticsLast) {
                if (betweenDate.equals(itemLast.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetRegisterStatisticsBySiteCodeVO record = new GetRegisterStatisticsBySiteCodeVO();
                record.setMyDay(betweenDate);
                record.setRegisterNumber(BigDecimal.ZERO);
                registerStatisticsLast.add(record);
            }
        }
        // 对registerStatisticsLast重新排序
        registerStatisticsLast = registerStatisticsLast.stream().sorted(Comparator.comparingInt(GetRegisterStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> compareMonthData = registerStatisticsLast.stream().map(GetRegisterStatisticsBySiteCodeVO::getRegisterNumber).toList();
        result.setCompareData(compareMonthData);
    }

    /**
     * 本月新注册人数数据 如果某天没有数据，强制设置为0
     *
     * @param result             设置结果
     * @param registerStatistics 统计本次数据
     * @param betweenDate        每天/每月/每日
     */
    private void handleCurrentMonthDataRegister(SiteDataCompareGraphVO result, List<GetRegisterStatisticsBySiteCodeVO> registerStatistics, List<String> betweenDate) {
        for (String date : betweenDate) {
            boolean flag = false;
            for (GetRegisterStatisticsBySiteCodeVO item : registerStatistics) {
                if (date.equals(item.getMyDay())) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                GetRegisterStatisticsBySiteCodeVO record = new GetRegisterStatisticsBySiteCodeVO();
                record.setMyDay(date);
                record.setRegisterNumber(BigDecimal.ZERO);
                registerStatistics.add(record);
            }
        }
        // 对registerStatistics重新排序
        registerStatistics = registerStatistics.stream().sorted(Comparator.comparingInt(GetRegisterStatisticsBySiteCodeVO::getMyDayOrder)).toList();
        List<BigDecimal> currentMonthData = registerStatistics.stream().map(GetRegisterStatisticsBySiteCodeVO::getRegisterNumber).toList();
        result.setCurrentData(currentMonthData);
    }

    public ResponseVO<SiteTodoDataResVO> getSiteTodo(String siteCode) {
        SiteTodoDataResVO siteTodoDataResVO = new SiteTodoDataResVO();
        //新增代理审核 数量
        Map<String, Long> agentStaticMap = agentReviewApi.getNotReviewNumMap(siteCode).getData();
        siteTodoDataResVO.setNewAgentAudit(agentStaticMap.get("0"));
        //siteTodoDataResVO.setAgentAccountModify(agentStaticMap.get("2"));
        //代理账户修改审核 数量
        siteTodoDataResVO.setNewUserAudit(userReviewService.getReviewCount(siteCode));
        siteTodoDataResVO.setUserAccountModify(accountUpdateReviewService.getNumber(siteCode).getNum());
        List<StatisticsPendingVO> statisticsPendingVOS = statisticsPendingReviewRecordsApi.getRecordsBySiteCode(siteCode);
        for (StatisticsPendingVO vo : statisticsPendingVOS) {
            if (SiteTodoEnum.USER_WITHDRAWAL_AUDIT.getCode().equals(vo.getCode())) {
                siteTodoDataResVO.setUserWithdrawalAudit(vo.getTotal().intValue());
            } else if (SiteTodoEnum.USER_MANUAL_INCREASE_AUDIT.getCode().equals(vo.getCode())) {
                siteTodoDataResVO.setUserManualIncreaseAudit(vo.getTotal().intValue());
            } else if (SiteTodoEnum.AGENT_WITHDRAWAL_AUDIT.getCode().equals(vo.getCode())) {
                siteTodoDataResVO.setAgentWithdrawalAudit(vo.getTotal().intValue());
            } else if (SiteTodoEnum.AGENT_MANUAL_INCREASE_AUDIT.getCode().equals(vo.getCode())) {
                siteTodoDataResVO.setAgentManualIncreaseAudit(vo.getTotal().intValue());
            }
        }
        Integer unreviewedRecordCount = agentCommissionReviewApi.getUnreviewedRecordCount(siteCode);
        siteTodoDataResVO.setCommissionAudit(unreviewedRecordCount);
        // 代理信息修改审核
        StatisticsPendingVO agentInfoReviewRecord = statisticsPendingReviewRecordsApi.getAgentInfoReviewRecord(siteCode);
        siteTodoDataResVO.setAgentAccountModify(agentInfoReviewRecord.getTotal());

        //平台币上分审核
        UserPlatformCoinManualUpRecordVO  temp = UserPlatformCoinManualUpRecordVO.builder().siteCode(siteCode).build();
        siteTodoDataResVO.setPlatformCoinIncreaseAudit(platformCoinManualDownRecordApi.getUpRecordTodoCount(temp).intValue());

        //1. 会员人工存款审核
        UserManualDepositPageReqVO userDepositVO = new UserManualDepositPageReqVO();
        userDepositVO.setSiteCode(siteCode);
        ResponseVO<Long> userDepositResVO = userManualDepositApi.userManualDepositCount(userDepositVO);
        if (userDepositResVO.isOk()){
            siteTodoDataResVO.setUserManualDepositAudit(userDepositResVO.getData().intValue());
        }
        //2. 会员人工提款审核
        UserWithdrawManualPageReqVO userWithdrawVO = new UserWithdrawManualPageReqVO();
        userWithdrawVO.setSiteCode(siteCode);
        userWithdrawVO.setCustomerStatusList(List.of(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode()));
        //NOTE 只能查询自己的
        userWithdrawVO.setOperator(CurrReqUtils.getAccount());
        ResponseVO<Long> userWithdrawResVO = userWithdrawManualRecordApi.withdrawalManualRecordPageCount(userWithdrawVO);
        if (userDepositResVO.isOk()){
            siteTodoDataResVO.setUserManualWithdrawAudit(userWithdrawResVO.getData().intValue());
        }
        //3. 代理人工存款审核
        AgentDepositReviewPageReqVO agentDepositVO = new AgentDepositReviewPageReqVO();
        agentDepositVO.setSiteCode(siteCode);
        ResponseVO<Long> agentDepositResVO = agentDepositReviewApi.depositReviewCount(agentDepositVO);
        if (agentDepositResVO.isOk()){
            siteTodoDataResVO.setAgentManualDepositAudit(agentDepositResVO.getData().intValue());
        }
        //4. 代理人工提款审核
        AgentWithdrawManualPageReqVO agentWithdrawVO = new AgentWithdrawManualPageReqVO();
        agentWithdrawVO.setSiteCode(siteCode);
        agentWithdrawVO.setCustomerStatusList(List.of(DepositWithdrawalOrderCustomerStatusEnum.PENDING.getCode()));
        ResponseVO<Long> agentWithdrawResVO = agentWithdrawManualRecordApi.withdrawalManualRecordPageCount(agentWithdrawVO);
        if (agentDepositResVO.isOk()){
            siteTodoDataResVO.setAgentManualWithdrawAudit(agentWithdrawResVO.getData().intValue());
        }

        return ResponseVO.success(siteTodoDataResVO);

    }

    /**
     * 从存取款表获取 存取款
     * 从会员盈亏宝贝获取
     */
    public ResponseVO<SiteDataWinLossResVO> siteDataWinLoss(UserDataOverviewResVo vo) {
        // 查询汇率
        String platCurrencyName = CurrReqUtils.getPlatCurrencyName(); // 获取平台币种名称
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(vo.getSiteCode()); // 获取所有币种汇率
        SiteDataWinLossResVO result = new SiteDataWinLossResVO();
        AtomicReference<BigDecimal> profitAndLoss = new AtomicReference<>(BigDecimal.ZERO);
        AtomicReference<BigDecimal> betWinLose = new AtomicReference<>(BigDecimal.ZERO);
        // 用户充值总额
        AtomicReference<BigDecimal> depositAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 用户总提款
        AtomicReference<BigDecimal> withdrawAmount = new AtomicReference<>(BigDecimal.ZERO);
        // 没有分货币查询所有
        List<GetAllArriveAmountBySiteCodeResponseVO> allArriveAmountBySiteCode = userDepositWithdrawApi
                .getAllArriveAmountBySiteCode(vo.getSiteCode(), Long.valueOf(vo.getStartTime()), Long.valueOf(vo.getEndTime()));
        if (vo.getConvertPlatCurrency()) {
            // 平台币展示
            for (GetAllArriveAmountBySiteCodeResponseVO record : allArriveAmountBySiteCode) {
                BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
                if (rate == null) {
                    // continue;
                    throw new BaowangDefaultException("用户充提,汇率未配置，货币是:" + record.getCurrencyCode());
                }
                depositAmount.updateAndGet(v -> v.add(record.getDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                withdrawAmount.updateAndGet(v -> v.add(record.getWithdrawAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            }
        } else {
            // 主货币展示，累加指定的货币
            for (GetAllArriveAmountBySiteCodeResponseVO record : allArriveAmountBySiteCode) {
                if (vo.getCurrencyCode().equals(record.getCurrencyCode())) {
                    depositAmount.updateAndGet(v -> v.add(record.getDepositAmount()));
                    withdrawAmount.updateAndGet(v -> v.add(record.getWithdrawAmount()));

                }
            }
        }
        //净输赢 = 游戏负盈利(平台游戏输赢) - 充提手续费 - 已使用优惠
        List<GetWinLoseStatisticsBySiteCodeVO> winLoseAndProfitAndLossStatisticsBySiteCode = reportUserWinLoseAgentApi
                .getWinLoseAndProfitAndLossStatisticsBySiteCode(Long.valueOf(vo.getStartTime()), Long.valueOf(vo.getEndTime()), vo.getSiteCode());
        if (vo.getConvertPlatCurrency()) {
            for (GetWinLoseStatisticsBySiteCodeVO record : winLoseAndProfitAndLossStatisticsBySiteCode) {
                BigDecimal rate = allFinalRate.get(record.getCurrencyCode());
                if (rate == null) {
                    //   continue;
                  throw new BaowangDefaultException("净输赢,汇率未配置,货币是:" + record.getCurrencyCode());
                }
                profitAndLoss.updateAndGet(v -> v.add(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                betWinLose.updateAndGet(v -> v.add(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
            }
        } else {
            for (GetWinLoseStatisticsBySiteCodeVO record : winLoseAndProfitAndLossStatisticsBySiteCode) {
                if (vo.getCurrencyCode().equals(record.getCurrencyCode())) {
                    BigDecimal rate = BigDecimal.ONE;
                    profitAndLoss.updateAndGet(v -> v.add(record.getProfitAndLoss().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                    betWinLose.updateAndGet(v -> v.add(record.getBetWinLose().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)));
                }
            }
        }

        result.setProfitAndLoss(profitAndLoss.get().setScale(2, RoundingMode.DOWN));
        result.setBetWinLose(betWinLose.get().setScale(2, RoundingMode.DOWN));
        result.setDepositAmount(depositAmount.get().setScale(2, RoundingMode.DOWN));
        result.setWithdrawAmount(withdrawAmount.get().setScale(2, RoundingMode.DOWN));
        if (vo.getConvertPlatCurrency()) {
            result.setPlatCurrencyName(platCurrencyName);
        } else {
            result.setPlatCurrencyName(vo.getCurrencyCode());
        }
        return ResponseVO.success(result);
    }


    public ResponseVO<SiteSelectQuickEntryResponse> selectQuickEntry(SiteSelectQuickEntryParam vo) {
        SiteSelectQuickEntryResponse siteSelectQuickEntryResponse = new SiteSelectQuickEntryResponse();
        List<BusinessUserMenuRespVO> businessUserMenuRespVOS = businessMenuApi.listAllMenuByAdminId(vo.getAdminId());
        siteSelectQuickEntryResponse.setAllEntry(businessUserMenuRespVOS);
        BusinessAdminVO businessAdminVO = businessAdminApi.getBusinessAdminById(vo.getAdminId());
        siteSelectQuickEntryResponse.setQuickEntry(Lists.newArrayList());
        if (StringUtils.hasText(businessAdminVO.getHomeQuickButton())) {
            siteSelectQuickEntryResponse.setQuickEntry(JSON.parseArray(businessAdminVO.getHomeQuickButton(), BusinessUserMenuRespVO.class));
        }
        return ResponseVO.success(siteSelectQuickEntryResponse);
    }


    public ResponseVO<List<IPTop10ResVO>> getDomainNameRanking(IPTop10ReqVO vo) {
        // 按照ip地址归属统计前10
        List<IPTop10ResVO> resultList = userLoginInfoRepository.getDomainNameRanking(vo.getStartTime(), vo.getEndTime(), vo.getSiteCode());
        return ResponseVO.success(resultList);
    }

    public ResponseVO<List<VisitFromResVO>> getVisitFrom(IPTop10ReqVO vo) {
        // 按照ip地址归属统计前10
        List<VisitFromResVO> resultList = userLoginInfoRepository.getVisitFrom(vo.getStartTime(), vo.getEndTime(), vo.getSiteCode());
        return ResponseVO.success(resultList);
    }

    public ResponseVO<List<IPTop10ResVO>> getVisitFromByIp(IPTop10ReqVO vo) {
        // 按照ip地址归属统计
        List<IPTop10ResVO> resultList = userLoginInfoRepository.getVisitFromByIp(vo.getStartTime(), vo.getEndTime(), vo.getSiteCode());
        return ResponseVO.success(resultList);
    }

    /**
     * 获取两个日期之间的所有日期(格式:yyyy-MM-dd)（考虑时区）
     * 如果开始与结束时间一致，则返回开始时间的天数
     * 如果结束时间小于开始时间，则返回为空 []
     *
     * @param startTime 起始时间戳（毫秒）
     * @param endTime   结束时间戳（毫秒）
     * @param timeZone  时区ID（例如 "Asia/Shanghai"）
     * @return 日期列表
     */
    public static List<String> getBetweenDatesMMDD(Long startTime, Long endTime, String timeZone) {
        List<String> result = new ArrayList<>();
        try {
            // 创建指定时区的DateTimeFormatter
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd");

            // 将时间戳转换为指定时区的 ZonedDateTime
            ZonedDateTime startDate = Instant.ofEpochMilli(startTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);  // 清除时间部分，确保从当天开始

            ZonedDateTime endDate = Instant.ofEpochMilli(endTime)
                    .atZone(ZoneId.of(timeZone))
                    .truncatedTo(ChronoUnit.DAYS);  // 清除时间部分，确保从当天结束

            // 循环添加日期
            while (!startDate.isAfter(endDate)) {
                result.add(startDate.format(formatter));  // 格式化为 yyyy-MM-dd
                startDate = startDate.plusDays(1);        // 增加一天
            }
        } catch (Exception e) {
            log.error("getBetweenDatesMMDD",e);
        }
        return result;
    }


}
