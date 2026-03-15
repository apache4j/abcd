package com.cloud.baowang.agent.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.agent.api.api.AgentCommissionApi;
import com.cloud.baowang.agent.api.vo.CurrencyCodeReqVO;
import com.cloud.baowang.agent.api.vo.agent.clienthome.*;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceReqVO;
import com.cloud.baowang.agent.api.vo.agentFinanceReport.AgentFinanceResVO;
import com.cloud.baowang.agent.api.vo.agentreview.list.GetAllListVO;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionDate;
import com.cloud.baowang.agent.api.vo.commission.AgentCommissionExpectVO;
import com.cloud.baowang.agent.po.AgentHomeAllButtonEntrancePO;
import com.cloud.baowang.agent.po.AgentInfoPO;
import com.cloud.baowang.agent.service.commission.AgentCommissionExpectReportService;
import com.cloud.baowang.agent.service.commission.AgentCommissionPlanService;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.user.reponse.GetDirectUserListByAgentAndTimeResponse;
import com.cloud.baowang.user.api.vo.user.reponse.GetDirectUserListByAgentAndTimeVO;
import com.cloud.baowang.report.api.api.ReportUserWinLoseAgentApi;
import com.cloud.baowang.report.api.api.UserWinLoseApi;
import com.cloud.baowang.report.api.vo.agent.GetWinLoseStatisticsByAgentIdVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.agent.GetFirstDepositStatisticsByAgentIdVO;
import com.cloud.baowang.user.api.vo.agent.GetRegisterStatisticsByAgentIdVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.agent.GetDepositStatisticsByAgentIdVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 客户端代理首页 服务类
 *
 * @author kimi
 * @since 2024-06-17
 */
@AllArgsConstructor
@Slf4j
@Service
public class AgentClientHomeService {
    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final AgentInfoService agentInfoService;
    private final ReportUserWinLoseAgentApi reportUserWinLoseAgentApi;
    private final AgentHomeAllButtonEntranceService agentHomeAllButtonEntranceService;
    private final UserInfoApi userInfoApi;
    private final AgentCommissionExpectReportService agentCommissionExpectReportService;
    private final AgentFinanceReportService agentFinanceReportService;
    private final AgentCommissionPlanService agentCommissionPlanService;
    private final UserWinLoseApi userWinLoseApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final AgentCommissionApi agentCommissionApi;

    public ResponseVO<GetHomeAgentInfoResponseVO> getHomeAgentInfo(CurrencyCodeReqVO currencyCodeReqVO) {
        GetHomeAgentInfoResponseVO result = new GetHomeAgentInfoResponseVO();
        String currentId = currencyCodeReqVO.getAgentId();

        // 下级会员 (所有直属会员人数)
        result.setLowerLevelUserNumber(userInfoApi.getByAgentId(currentId));

        AgentInfoPO byId = agentInfoService.getByAgentId(currentId);
        result.setInviteCode(byId.getInviteCode());
        result.setAgentName(byId.getName());
        return ResponseVO.success(result);
    }

    public ResponseVO<MonthClientAgentResponseVO> monthStatistics(CurrencyCodeReqVO currencyCodeReqVO) {
        String currentId = currencyCodeReqVO.getAgentId();
        String siteCode=currencyCodeReqVO.getSiteCode();
        String currentAgent = currencyCodeReqVO.getCurrentAgent();
        String currencyCode = currencyCodeReqVO.getCurrencyCode();

        //获取当前代理结算周期
        AgentCommissionDate agentCommissionDate=agentCommissionApi.getDayAgentCommissionDate(currentId);
        Long currentStartTime=agentCommissionDate.getCurrentStartTime();
        Long currentEndTime=agentCommissionDate.getCurrentEndTime();

        Long beforeStartTime= agentCommissionDate.getLastStartTime();
        Long beforeEndTime= agentCommissionDate.getLastEndTime();

        MonthClientAgentResponseVO result = new MonthClientAgentResponseVO();
        result.setCurrencyCode(currencyCodeReqVO.getCurrencyCode());

        // 本期佣金比例(查询agent_commission_expect_report)
        //AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(currentId);
        //String planCode = agentInfoPO.getPlanCode();
       // AgentCommissionPlanVO planVO = agentCommissionPlanService.getPlanByPlanCode(planCode);

        log.info("本期开始日期:{},结束日期:{},currentId:{},siteCode:{},currencyCode:{}",currentStartTime,currentEndTime,currentId,siteCode,currencyCode);
        //同财务报表字段一致
        AgentFinanceResVO agentFinanceResVO = agentFinanceReportService.financeReport(
                AgentFinanceReqVO.builder()
                        .agentId(currentId)
                        .siteCode(siteCode)
                        .currencyCode(currencyCode)
                        .startTime(currentStartTime)
                        .endTime(currentEndTime)
                        .build());
        result.setWinOrLoss(agentFinanceResVO == null ? BigDecimal.ZERO : agentFinanceResVO.getTeamFinanceVO().getNetWinLoss());
        result.setMonthSugAgentValidBetAmount(agentFinanceResVO.getTeamFinanceVO().getValidBetAmount());
        result.setMonthDirectUserValidBetAmount(agentFinanceResVO.getCurrFinanceVO().getValidBetAmount());

        //本期有效新增
        AgentCommissionExpectVO one = agentCommissionExpectReportService.getLatestCommissionExpectReport(currentId);
        BigDecimal monthCommissionRatio = BigDecimal.ZERO;
        if (null != one) {
            monthCommissionRatio = one.getAgentRate();
        }
        result.setMonthCommissionRatio(monthCommissionRatio);
        // 本期有效新增会员、本期有效活跃会员、本期总流水
        if(one!=null){
            //有效新增会员
            result.setNewActiveUserNumber(one.getNewValidNumber() == null ? 0 : one.getNewValidNumber());
            //本期有效活跃会员
            result.setValidActiveUserNumber(one.getActiveNumber() == null ? 0 : one.getActiveNumber());
            //本期总流水
            result.setMonthValidBetAmount(one.getValidBetAmount() == null ? BigDecimal.ZERO : one.getValidBetAmount());
            //本期净输赢
            //result.setWinOrLoss(one.getNetWinLoss() == null ? BigDecimal.ZERO : one.getNetWinLoss());
        }

        // 本期净输赢(查询方法 同 财务报表-团队财务报表)
       /* AgentFinanceResVO agentFinanceResVO = agentFinanceReportService.financeReport(
                AgentFinanceReqVO.builder()
                        .agentId(currentId)
                        .siteCode(currencyCodeReqVO.getSiteCode())
                        .currencyCode(currencyCode)
                        .startTime(currentStartTime)
                        .endTime(currentEndTime)
                        .build());
        result.setWinOrLoss(agentFinanceResVO.getTeamFinanceVO().getNetWinLoss());
*/

       /* List<UserWinLoseAgentVO> userWinLoseAgentVOS=userWinLoseApi.queryByTimeAndAgent(UserWinLoseAgentReqVO.builder()
                .agentIds(Lists.newArrayList(currentId))
                .siteCode(currencyCodeReqVO.getSiteCode())
                .currencyCode(currencyCode)
                .startTime(currentStartTime)
                .endTime(currentEndTime)
                .build()).getData();
        result.setWinOrLoss(BigDecimal.ZERO);
        if(!CollectionUtils.isEmpty(userWinLoseAgentVOS)){
            result.setWinOrLoss(userWinLoseAgentVOS.get(0).getWinLoseAmount());
        }
*/
        // 下级会员 (包含所有下级会员)
        // 查询每个agentId的 所有下级代理id(包含代理自己)
      /*  Map<String, List<String>> allDownAgentId = agentInfoService.findAllDownAgentIdByIds(Lists.newArrayList(currentId));
        List<String> downAgentIds = allDownAgentId.get(currentId);
        Long downUser = userInfoApi.getCountByAgentIds(downAgentIds);
        result.setLowerLevelUserNumber(null == downUser ? 0 : downUser);*/

        // 查询agentId的 所有下级代理(包含代理自己)
       // List<GetAllListVO> allDownAgents = agentInfoService.findAllDownAgentById(currentId);
      //  List<String> allDownAgentAndSelfAccountId = allDownAgents.stream().map(GetAllListVO::getAgentId).collect(Collectors.toList());
       // List<String> allDownAgentAndSelf = allDownAgents.stream().map(GetAllListVO::getId).collect(Collectors.toList());

        Integer lowerLevelAgentNum=0;

        List<GetAllListVO>  agentAllList= agentInfoService.findAllDownAgentById(currentId);
        if(!CollectionUtils.isEmpty(agentAllList)){
            for(GetAllListVO getAllListVO:agentAllList){
                if(currentId.equals(getAllListVO.getParentId())
                        &&getAllListVO.getRegisterTime()>=currentStartTime
                        &&getAllListVO.getRegisterTime()<=currentEndTime
                ){
                    lowerLevelAgentNum++;
                }
            }
        }
        //新增下级 本期成功注册的直属下级代理人数
        result.setAddLowerLevelAgentNumber(lowerLevelAgentNum);
        //当前代理
        List<String> currentAgentIds=Lists.newArrayList(currentId);
        //  本期新注册 直属下级
        GetDirectUserListByAgentAndTimeVO vo = new GetDirectUserListByAgentAndTimeVO();
        vo.setRegisterTimeStart(currentStartTime);
        vo.setRegisterTimeEnd(currentEndTime);
        vo.setSuperAgentId(currentAgentIds);
        vo.setSiteCode(siteCode);
        GetDirectUserListByAgentAndTimeResponse response = userInfoApi.getDirectUserCountByAgentAndTime(vo);
        // 本期新注册-百分比
        GetDirectUserListByAgentAndTimeVO voLast = new GetDirectUserListByAgentAndTimeVO();
        voLast.setRegisterTimeStart(beforeStartTime);
        voLast.setRegisterTimeEnd(beforeEndTime);
        voLast.setSuperAgentId(currentAgentIds);
        voLast.setSiteCode(siteCode);
        GetDirectUserListByAgentAndTimeResponse responseLast = userInfoApi.getDirectUserCountByAgentAndTime(voLast);
        String format;
        // 1:上升 2:下降 3:为0不显示
        Integer addLowerLevelUserFlag;
        if (responseLast != null && 0 == responseLast.getNewUserNumber() && 0 != response.getNewUserNumber()) {
            format = "100%";
            addLowerLevelUserFlag = CommonConstant.business_one;
        } else if (responseLast != null && 0 == responseLast.getNewUserNumber() && 0 == response.getNewUserNumber()) {
            format = "0%";
            addLowerLevelUserFlag = CommonConstant.business_three;
        } else {
            int increase = response.getNewUserNumber() - responseLast.getNewUserNumber();
            BigDecimal div = NumberUtil.div(Integer.toString(increase), responseLast.getNewUserNumber().toString(), 2);
            format = NumberUtil.decimalFormat("#.##%", div);

            String substring = format.substring(0, format.length() - 1);
            if (new BigDecimal(substring).compareTo(BigDecimal.ZERO) > 0) {
                addLowerLevelUserFlag = CommonConstant.business_one;
            } else if (new BigDecimal(substring).compareTo(BigDecimal.ZERO) < 0) {
                addLowerLevelUserFlag = CommonConstant.business_two;
            } else {
                addLowerLevelUserFlag = CommonConstant.business_three;
            }
        }
        result.setAddLowerLevelUserNumberLast(responseLast.getNewUserNumber());
        result.setAddLowerLevelUserNumber(response.getNewUserNumber());
        result.setAddLowerLevelUserPercentage(format);
        result.setAddLowerLevelUserFlag(addLowerLevelUserFlag);
        // 首存人数
        result.setFirstDepositNumberLast(responseLast.getFirstDepositNumber());
        result.setFirstDepositNumber(response.getFirstDepositNumber());
        // 首存人数-百分比
        String firstDepositPercentage;
        // 1:上升 2:下降 3:为0不显示
        Integer firstDepositFlag;
        if (0 == responseLast.getFirstDepositNumber() && 0 != response.getFirstDepositNumber()) {
            firstDepositPercentage = "100%";
            firstDepositFlag = CommonConstant.business_one;
        } else if (0 == responseLast.getFirstDepositNumber() && 0 == response.getFirstDepositNumber()) {
            firstDepositPercentage = "0%";
            firstDepositFlag = CommonConstant.business_three;
        } else {
            int increase = response.getFirstDepositNumber() - responseLast.getFirstDepositNumber();
            BigDecimal div = NumberUtil.div(Integer.toString(increase), responseLast.getFirstDepositNumber().toString(), 2);
            firstDepositPercentage = NumberUtil.decimalFormat("#.##%", div);

            String substring = firstDepositPercentage.substring(0, firstDepositPercentage.length() - 1);
            if (new BigDecimal(substring).compareTo(BigDecimal.ZERO) > 0) {
                firstDepositFlag = CommonConstant.business_one;
            } else if (new BigDecimal(substring).compareTo(BigDecimal.ZERO) < 0) {
                firstDepositFlag = CommonConstant.business_two;
            } else {
                firstDepositFlag = CommonConstant.business_three;
            }
        }
        result.setFirstDepositPercentage(firstDepositPercentage);
        result.setFirstDepositFlag(firstDepositFlag);


        return ResponseVO.success(result);
    }

    public ResponseVO<SelectQuickEntryResponse> selectQuickEntry(SelectQuickEntryParam vo) {
        SelectQuickEntryResponse result = new SelectQuickEntryResponse();
        AgentInfoPO agentInfoPO = agentInfoService.selectByAgentId(vo.getCurrentId());
        if (null == agentInfoPO) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }

        // 首页功能
        List<SelectQuickEntryVO> quickEntry = Lists.newArrayList();
        // 全部功能
        List<AgentHomeAllButtonEntrancePO> allEntry;

        if (CommonConstant.business_one.equals(vo.getPcOrH5())) {
            // 1:PC端
            // 首页功能
            if (StrUtil.isNotEmpty(agentInfoPO.getHomeButtonEntrance())) {
                quickEntry = JSONArray.parseArray(agentInfoPO.getHomeButtonEntrance(), SelectQuickEntryVO.class);
            }
            // 全部功能
            allEntry = agentHomeAllButtonEntranceService
                    .lambdaQuery()
                    .eq(AgentHomeAllButtonEntrancePO::getPcOrH5, vo.getPcOrH5())
                    .eq(AgentHomeAllButtonEntrancePO::getSiteCode, vo.getSiteCode())
                    .list();
        } else {
            // 2:H5端
            // 首页功能
            if (StrUtil.isNotEmpty(agentInfoPO.getHomeButtonEntranceH5())) {
                quickEntry = JSONArray.parseArray(agentInfoPO.getHomeButtonEntranceH5(), SelectQuickEntryVO.class);
            }
            // 全部功能
            allEntry = agentHomeAllButtonEntranceService
                    .lambdaQuery()
                    .eq(AgentHomeAllButtonEntrancePO::getPcOrH5, vo.getPcOrH5())
                    .eq(AgentHomeAllButtonEntrancePO::getSiteCode, vo.getSiteCode())
                    .list();
        }

        // 首页功能
        result.setQuickEntry(quickEntry);
        // 全部功能
        result.setAllEntry(ConvertUtil.entityListToModelList(allEntry, SelectQuickEntryVO.class));
        return ResponseVO.success(result);
    }

    public ResponseVO<?> saveQuickEntry(SaveQuickEntryParam vo) {
        List<SelectQuickEntryVO> quickEntry = vo.getQuickEntry();
        Integer pcOrH5 = vo.getPcOrH5();

        AgentInfoPO agentInfoPO = agentInfoService.getByAgentId(vo.getCurrentId());
        if (null == agentInfoPO) {
            return ResponseVO.fail(ResultCode.DATA_NOT_EXIST);
        }
        if (CommonConstant.business_one.equals(pcOrH5)) {
            // 1:PC端
            agentInfoPO.setHomeButtonEntrance(JSONObject.toJSONString(quickEntry));
        } else {
            // 2:H5端
            agentInfoPO.setHomeButtonEntranceH5(JSONObject.toJSONString(quickEntry));
        }
        agentInfoService.updateById(agentInfoPO);
        return ResponseVO.success();
    }


    /**
     * 数据对比
     * @param vo 参数
     * @return 结果
     */
    public ResponseVO<DataCompareGraphVO> dataCompareGraph(DataCompareGraphParam vo) {
        Long time=vo.getTime();
        String siteCode=vo.getSiteCode();
        String currentId=vo.getCurrentId();
        String timeZoneId=vo.getTimeZoneId();
        String dbZone=timeZoneId.replace("UTC","").concat(":00");
        DataCompareGraphVO result = new DataCompareGraphVO();
        result.setCurrencyCode(vo.getCurrencyCode());
        //上个月开始时间 结束时间
        Long beforeMonthStartTime=DateUtils.getStartDayBeforeMonthTimestamp(timeZoneId);
        Long beforeMonthEndTime =DateUtils.getEndDayBeforeMonthTimestamp(timeZoneId);
        //本期开始时间 结束时间
        Long startTime=DateUtils.getStartDayMonthTimestamp(timeZoneId);
        Long endTime =DateUtils.getEndDayMonthTimestamp(timeZoneId);

        if(time!=null){
            //本月 开始时间 结束时间
            startTime=DateUtils.getStartDayMonthTimestamp(time,timeZoneId);
            endTime=DateUtils.getEndDayMonthTimestamp(time,timeZoneId);
            //上月 开始时间 结束时间
            beforeMonthStartTime=DateUtils.getStartDayBeforeMonthTimestamp(time,timeZoneId);
            beforeMonthEndTime =DateUtils.getEndDayBeforeMonthTimestamp(time,timeZoneId);
        }

        log.info("上个月时间:{}->{};本月时间:{}->{},币种:{},时区:{}",beforeMonthStartTime,beforeMonthEndTime, startTime,endTime,vo.getCurrencyCode(),timeZoneId);
        List<String> dayList = Lists.newArrayList();
        for (int i = 1; i < 32; i++) {
            dayList.add(String.format("%02d",i));
        }
        result.setDayList(dayList);
        //上期日期差距
        List<String> beforeBetweenDates = DateUtils.getBetweenDates(beforeMonthStartTime, beforeMonthEndTime,timeZoneId);
        //汇率
        Map<String, BigDecimal>  rateMap= siteCurrencyInfoApi.getAllFinalRate(siteCode);
        // 本期日期差距
        List<String> currentBetweenDate = DateUtils.getBetweenDates(startTime, System.currentTimeMillis(),timeZoneId);

        switch (vo.getType()) {
            case 1:
                // 1存款金额
                // 上月数据
                List<GetDepositStatisticsByAgentIdVO> depositStatisticsLast =
                        userDepositWithdrawApi.getDepositStatisticsByAgentId(
                                vo.getSiteCode(),
                                beforeMonthStartTime, beforeMonthEndTime,
                                currentId, CommonConstant.business_one,
                                dbZone,
                                vo.getCurrencyCode()
                        ).getData();
                // 本期数据
                List<GetDepositStatisticsByAgentIdVO> depositStatistics =
                        userDepositWithdrawApi.getDepositStatisticsByAgentId(
                                vo.getSiteCode(),
                                startTime,
                                endTime,
                                currentId,
                                CommonConstant.business_one,
                                dbZone,
                                vo.getCurrencyCode()
                        ).getData();
                // 上月存款数据 如果某天没有数据，强制设置为0
                List<BigDecimal> resultData=handleCompareMonthDataDeposit(beforeBetweenDates, depositStatisticsLast,vo.getCurrencyCode(),rateMap);
                result.setCompareMonthData(resultData);

                // 本期存款数据 如果某天没有数据，强制设置为0
                List<BigDecimal> currentResultData=handleCompareMonthDataDeposit(currentBetweenDate, depositStatistics,vo.getCurrencyCode(),rateMap);
                result.setCurrentMonthData(currentResultData);
                break;
            case 2:
                // 2取款金额
                // 上月数据
                List<GetDepositStatisticsByAgentIdVO> withdrawStatisticsLast =
                        userDepositWithdrawApi.getDepositStatisticsByAgentId(
                                vo.getSiteCode(),
                                beforeMonthStartTime,
                                beforeMonthEndTime,
                                currentId,
                                CommonConstant.business_two,
                                dbZone,
                                vo.getCurrencyCode()
                                ).getData();
                // 本期数据
                List<GetDepositStatisticsByAgentIdVO> withdrawStatistics =
                        userDepositWithdrawApi.getDepositStatisticsByAgentId(
                                vo.getSiteCode(),
                                startTime,
                                endTime,
                                currentId,
                                CommonConstant.business_two,
                                dbZone,
                                vo.getCurrencyCode()
                                ).getData();
                // 上月取款数据 如果某天没有数据，强制设置为0
                List<BigDecimal> withdrawData= handleCompareMonthDataWithdraw(beforeBetweenDates, withdrawStatisticsLast,vo.getCurrencyCode(),rateMap);
                result.setCompareMonthData(withdrawData);

                // 本期取款数据 如果某天没有数据，强制设置为0
                List<BigDecimal> currentWithdrawData=handleCompareMonthDataWithdraw( currentBetweenDate, withdrawStatistics,vo.getCurrencyCode(),rateMap);
                result.setCurrentMonthData(currentWithdrawData);

                break;
            case 3:
                // 3总输赢
                // 上月数据
                List<GetWinLoseStatisticsByAgentIdVO> winLoseStatisticsLast =
                        reportUserWinLoseAgentApi.getWinLoseStatisticsByAgentId(
                                beforeMonthStartTime, beforeMonthEndTime,
                                currentId,dbZone,vo.getCurrencyCode()
                        );
                // 本期数据
                List<GetWinLoseStatisticsByAgentIdVO> winLoseStatistics =
                        reportUserWinLoseAgentApi.getWinLoseStatisticsByAgentId(
                                startTime,
                                endTime,
                                currentId,
                                dbZone,vo.getCurrencyCode()
                        );
                // 上月总输赢数据 如果某天没有数据，强制设置为0
                List<BigDecimal> winLoseData=handleCompareMonthDataWinLose(beforeBetweenDates, winLoseStatisticsLast,vo.getCurrencyCode(),rateMap);
                result.setCompareMonthData(winLoseData);

                // 本期总输赢数据 如果某天没有数据，强制设置为0
                List<BigDecimal> winLoseDataCurrent=handleCompareMonthDataWinLose(currentBetweenDate,winLoseStatistics,vo.getCurrencyCode(),rateMap);
                result.setCurrentMonthData(winLoseDataCurrent);
                break;
            case 4:
                // 4新注册人数
                // 上月数据
                List<GetRegisterStatisticsByAgentIdVO> registerStatisticsLast =
                        userInfoApi.getRegisterStatisticsByAgentId( beforeMonthStartTime, beforeMonthEndTime, currentId,dbZone);
                // 本期数据
                List<GetRegisterStatisticsByAgentIdVO> registerStatistics =
                        userInfoApi.getRegisterStatisticsByAgentId(
                                startTime,
                                endTime,
                                currentId,
                                dbZone
                        );
                // 上月新注册人数数据 如果某天没有数据，强制设置为0
                List<BigDecimal> beforeData=handleCompareMonthDataRegister(beforeBetweenDates, registerStatisticsLast);
                result.setCompareMonthData(beforeData);
                // 本期新注册人数数据 如果某天没有数据，强制设置为0
                List<BigDecimal> currentData=handleCompareMonthDataRegister(currentBetweenDate,registerStatistics);
                result.setCurrentMonthData(currentData);
                break;
            case 5:
                // 5首存人数
                // 上月数据
                List<GetFirstDepositStatisticsByAgentIdVO> firstDepositStatisticsLast =
                        userInfoApi.getFirstDepositStatisticsByAgentId(beforeMonthStartTime, beforeMonthEndTime,  currentId,dbZone);
                // 本期数据
                List<GetFirstDepositStatisticsByAgentIdVO> firstDepositStatistics =
                        userInfoApi.getFirstDepositStatisticsByAgentId(
                                startTime,
                                endTime,
                                currentId,dbZone);
                // 上月首存人数数据 如果某天没有数据，强制设置为0
                List<BigDecimal> beforeDepositData=handleCompareMonthDataFirstDeposit( beforeBetweenDates, firstDepositStatisticsLast);
                result.setCompareMonthData(beforeDepositData);
                // 本期首存人数数据 如果某天没有数据，强制设置为0
                List<BigDecimal> currentDepositData=handleCompareMonthDataFirstDeposit(currentBetweenDate, firstDepositStatistics);
                result.setCurrentMonthData(currentDepositData);
                break;
        }
        return ResponseVO.success(result);
    }




    /**
     * 总输赢数据 如果某天没有数据，强制设置为0
     *
     * @param winLoseStatistics 总输赢数据
     */
    private List<BigDecimal> handleCompareMonthDataWinLose(List<String> betweenDates, List<GetWinLoseStatisticsByAgentIdVO> winLoseStatistics,String currencyCode,Map<String,BigDecimal> rateMap) {
        if(winLoseStatistics==null){
            winLoseStatistics=Lists.newArrayList();
        }
        List<GetWinLoseStatisticsByAgentIdVO> winLoseStatisticsResult=Lists.newArrayList();
        for (String betweenDate : betweenDates) {
            Optional<GetWinLoseStatisticsByAgentIdVO> getWinLoseStatisticsByAgentIdVOOptional=winLoseStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).findFirst();
            if (getWinLoseStatisticsByAgentIdVOOptional.isEmpty()) {
                GetWinLoseStatisticsByAgentIdVO record = new GetWinLoseStatisticsByAgentIdVO();
                record.setMyDay(betweenDate);
                record.setBetWinLose(BigDecimal.ZERO);
                winLoseStatisticsResult.add(record);
            }else {
                List<GetWinLoseStatisticsByAgentIdVO> getWinLoseStatisticsByAgentIdVOs = winLoseStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).toList();
                GetWinLoseStatisticsByAgentIdVO getWinLoseStatisticsByAgentIdVOResult=new GetWinLoseStatisticsByAgentIdVO();
                getWinLoseStatisticsByAgentIdVOResult.setMyDay(betweenDate);
                getWinLoseStatisticsByAgentIdVOResult.setCurrencyCode(currencyCode);
                BigDecimal betWinLoseAmount=BigDecimal.ZERO;
                for(GetWinLoseStatisticsByAgentIdVO getWinLoseStatisticsByAgentIdVO:getWinLoseStatisticsByAgentIdVOs){
                    if(CommonConstant.PLAT_CURRENCY_CODE.equals(currencyCode)){
                        BigDecimal finalRate=rateMap.get(getWinLoseStatisticsByAgentIdVO.getCurrencyCode());
                        BigDecimal finalAmount= AmountUtils.divide(getWinLoseStatisticsByAgentIdVO.getBetWinLose(),finalRate);
                        betWinLoseAmount=betWinLoseAmount.add(finalAmount);
                    }else {
                        betWinLoseAmount=betWinLoseAmount.add(getWinLoseStatisticsByAgentIdVO.getBetWinLose());
                    }
                }
                getWinLoseStatisticsByAgentIdVOResult.setBetWinLose(betWinLoseAmount);
                winLoseStatisticsResult.add(getWinLoseStatisticsByAgentIdVOResult);
            }
        }
        // 对withdrawStatisticsResult重新排序
        winLoseStatisticsResult = winLoseStatisticsResult.stream().sorted(Comparator.comparing(GetWinLoseStatisticsByAgentIdVO::getMyDay)).toList();
        return winLoseStatisticsResult.stream().map(GetWinLoseStatisticsByAgentIdVO::getBetWinLose).toList();
    }

    // ----------------------------------------------------------------

    /**
     * 存款数据 如果某天没有数据，强制设置为0
     *
     * @param betweenDates 日期
     * @param depositStatistics 存款数据
     */
    private List<BigDecimal>  handleCompareMonthDataDeposit(
                                               List<String> betweenDates,
                                               List<GetDepositStatisticsByAgentIdVO> depositStatistics,
                                               String currencyCode,
                                               Map<String, BigDecimal>  rateMap
                                               ) {
        if(depositStatistics==null){
            depositStatistics=Lists.newArrayList();
        }
        List<GetDepositStatisticsByAgentIdVO> depositStatisticsLastResult=Lists.newArrayList();
        for (String betweenDate : betweenDates) {
            Optional<GetDepositStatisticsByAgentIdVO> depositStatisticsByAgentIdVOOptional=depositStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).findFirst();
            if (depositStatisticsByAgentIdVOOptional.isEmpty()) {
                GetDepositStatisticsByAgentIdVO record = new GetDepositStatisticsByAgentIdVO();
                record.setMyDay(betweenDate);
                record.setDepositAmount(BigDecimal.ZERO);
                depositStatisticsLastResult.add(record);
            }else {
                List<GetDepositStatisticsByAgentIdVO> getDepositStatisticsByAgentIdVOs = depositStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).toList();
                GetDepositStatisticsByAgentIdVO getDepositStatisticsByAgentIdVOResult=new GetDepositStatisticsByAgentIdVO();
                getDepositStatisticsByAgentIdVOResult.setCurrencyCode(currencyCode);
                getDepositStatisticsByAgentIdVOResult.setMyDay(betweenDate);
                BigDecimal platAmount=BigDecimal.ZERO;
                for(GetDepositStatisticsByAgentIdVO getDepositStatisticsByAgentIdVO:getDepositStatisticsByAgentIdVOs){
                    if(CommonConstant.PLAT_CURRENCY_CODE.equals(currencyCode)) {
                        BigDecimal finalRate = rateMap.get(getDepositStatisticsByAgentIdVO.getCurrencyCode());
                        BigDecimal finalAmount = AmountUtils.divide(getDepositStatisticsByAgentIdVO.getDepositAmount(), finalRate);
                        platAmount=platAmount.add(finalAmount);
                    }else {
                        platAmount=platAmount.add(getDepositStatisticsByAgentIdVO.getDepositAmount());
                    }
                }
                getDepositStatisticsByAgentIdVOResult.setDepositAmount(platAmount);
                depositStatisticsLastResult.add(getDepositStatisticsByAgentIdVOResult);
            }
        }
        // 对 depositStatisticsLastResult 重新排序
        depositStatisticsLastResult = depositStatisticsLastResult.stream().sorted(Comparator.comparing(GetDepositStatisticsByAgentIdVO::getMyDay)).toList();
        return depositStatisticsLastResult.stream().map(GetDepositStatisticsByAgentIdVO::getDepositAmount).toList();
    }

    /**
     * 上月取款数据 如果某天没有数据，强制设置为0
     *
     * @param betweenDates 日期差距
     * @param withdrawStatistics 提现数据统计
     */
    private List<BigDecimal> handleCompareMonthDataWithdraw(
                                                            List<String> betweenDates,
                                                            List<GetDepositStatisticsByAgentIdVO> withdrawStatistics,
                                                            String currencyCode,
                                                            Map<String,BigDecimal> rateMap
                                                ) {
        if(withdrawStatistics==null){
            withdrawStatistics=Lists.newArrayList();
        }
        List<GetDepositStatisticsByAgentIdVO> withdrawStatisticsResult=Lists.newArrayList();
        for (String betweenDate : betweenDates) {
            Optional<GetDepositStatisticsByAgentIdVO> depositStatisticsByAgentIdVOOptional=withdrawStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).findFirst();
            if (depositStatisticsByAgentIdVOOptional.isEmpty()) {
                GetDepositStatisticsByAgentIdVO record = new GetDepositStatisticsByAgentIdVO();
                record.setMyDay(betweenDate);
                record.setWithdrawAmount(BigDecimal.ZERO);
                withdrawStatisticsResult.add(record);
            }else {
                List<GetDepositStatisticsByAgentIdVO> getDepositStatisticsByAgentIdVOs = withdrawStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).toList();
                GetDepositStatisticsByAgentIdVO getDepositStatisticsByAgentIdVOResult=new GetDepositStatisticsByAgentIdVO();
                getDepositStatisticsByAgentIdVOResult.setCurrencyCode(currencyCode);
                getDepositStatisticsByAgentIdVOResult.setMyDay(betweenDate);
                BigDecimal platAmount=BigDecimal.ZERO;
                for(GetDepositStatisticsByAgentIdVO getDepositStatisticsByAgentIdVO:getDepositStatisticsByAgentIdVOs){
                    if(CommonConstant.PLAT_CURRENCY_CODE.equals(currencyCode)) {
                        BigDecimal finalRate = rateMap.get(getDepositStatisticsByAgentIdVO.getCurrencyCode());
                        BigDecimal finalAmount = AmountUtils.divide(getDepositStatisticsByAgentIdVO.getWithdrawAmount(), finalRate);
                        platAmount=platAmount.add(finalAmount);
                    }else {
                        platAmount=platAmount.add(getDepositStatisticsByAgentIdVO.getWithdrawAmount());
                    }
                }
                getDepositStatisticsByAgentIdVOResult.setWithdrawAmount(platAmount);
                withdrawStatisticsResult.add(getDepositStatisticsByAgentIdVOResult);
            }
        }
        // 对 withdrawStatisticsResult 重新排序
        withdrawStatisticsResult = withdrawStatisticsResult.stream().sorted(Comparator.comparing(GetDepositStatisticsByAgentIdVO::getMyDay)).toList();
        return withdrawStatisticsResult.stream().map(GetDepositStatisticsByAgentIdVO::getWithdrawAmount).toList();
    }


    /**
     * 新注册人数数据 如果某天没有数据，强制设置为0
     *
     * @param betweenDates 日期差距
     * @param registerStatistics 注册人数数据
     */
    private List<BigDecimal> handleCompareMonthDataRegister(
                                                List<String> betweenDates,
                                                List<GetRegisterStatisticsByAgentIdVO> registerStatistics) {
        if(CollectionUtils.isEmpty(registerStatistics)){
            registerStatistics=Lists.newArrayList();
        }
        for (String betweenDate : betweenDates) {
            Optional<GetRegisterStatisticsByAgentIdVO>  getRegisterStatisticsByAgentIdVOOptional=registerStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).findFirst();
            boolean flag = getRegisterStatisticsByAgentIdVOOptional.isPresent();
            if (!flag) {
                GetRegisterStatisticsByAgentIdVO record = new GetRegisterStatisticsByAgentIdVO();
                record.setMyDay(betweenDate);
                record.setRegisterNumber(BigDecimal.ZERO);
                registerStatistics.add(record);
            }
        }
        // 对registerStatistics重新排序
        registerStatistics = registerStatistics.stream().sorted(Comparator.comparing(GetRegisterStatisticsByAgentIdVO::getMyDay)).toList();
        return registerStatistics.stream().map(GetRegisterStatisticsByAgentIdVO::getRegisterNumber).toList();
    }

    /**
     * 首存人数数据 如果某天没有数据，强制设置为0
     *
     * @param betweenDates 日期查询
     * @param firstDepositStatistics 首存人数
     */
    private List<BigDecimal> handleCompareMonthDataFirstDeposit(
                                                    List<String> betweenDates,
                                                    List<GetFirstDepositStatisticsByAgentIdVO> firstDepositStatistics) {
        if(CollectionUtils.isEmpty(firstDepositStatistics)){
            firstDepositStatistics=Lists.newArrayList();
        }
        for (String betweenDate : betweenDates) {
            boolean flag=firstDepositStatistics.stream().filter(o->o.getMyDay().equals(betweenDate)).findFirst().isEmpty();
            if (flag) {
                GetFirstDepositStatisticsByAgentIdVO record = new GetFirstDepositStatisticsByAgentIdVO();
                record.setMyDay(betweenDate);
                record.setFirstDepositNumber(BigDecimal.ZERO);
                firstDepositStatistics.add(record);
            }
        }
        // 对 firstDepositStatistics 重新排序
        firstDepositStatistics = firstDepositStatistics.stream().sorted(Comparator.comparing(GetFirstDepositStatisticsByAgentIdVO::getMyDay)).toList();
        return firstDepositStatistics.stream().map(GetFirstDepositStatisticsByAgentIdVO::getFirstDepositNumber).toList();
    }
}
