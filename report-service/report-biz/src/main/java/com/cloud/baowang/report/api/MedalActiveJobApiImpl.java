package com.cloud.baowang.report.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.activity.api.api.task.TaskConfigApi;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.report.api.api.MedalActiveJobApi;
import com.cloud.baowang.report.po.ReportUserWinLosePO;
import com.cloud.baowang.report.service.ReportUserWinLoseService;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithDrawStaticReportApi;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@AllArgsConstructor
public class MedalActiveJobApiImpl implements MedalActiveJobApi {
    private final ReportUserWinLoseService reportUserWinLoseService;
    private final SiteApi siteApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private UserDepositWithDrawStaticReportApi userDepositWithDrawStaticReportApi;

    private final TaskConfigApi taskConfigApi;

    @Override
    public ResponseVO<Void> siteMedalActiveMonthJob(String siteCode) {
        ResponseVO<SiteVO> siteVOResponseVO = siteApi.getSiteInfo(siteCode);
        if (!siteVOResponseVO.isOk() || ObjUtil.isNull(siteVOResponseVO.getData())) {
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        SiteVO siteVO = siteVOResponseVO.getData();
        String timezone = siteVO.getTimezone();
        if(!StringUtils.hasText(timezone)){
            log.info("站点:{}对应的时区无数据",siteCode);
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        ArrayList<MedalAcquireReqVO> medals = Lists.newArrayList();
        // 孤勇者勋章 任意单个自然月全平台负盈利最多的一个人
        medals.add(medal1002Active(siteVO));

        // 功勋卓著 任意单个自然月全平台流水最多的一个人
        medals.add(medal1003Active(siteVO));


        if (CollUtil.isNotEmpty(medals)) {
            MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
            medalAcquireBatchReqVO.setSiteCode(siteCode);
            medalAcquireBatchReqVO.setMedalAcquireReqVOList(medals);
            KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
        } else {
            log.warn("上月无数据,siteCode:{}", siteCode);
        }
        //元宇宙大富翁
        userDepositWithDrawStaticReportApi.staticVirtualDepositMedal1016ByMonth(siteVO.getSiteCode(),siteVO.getTimezone());
        //富甲天下
        userDepositWithDrawStaticReportApi.staticVirtualDepositMedal1018ByMonth(siteVO.getSiteCode(),siteVO.getTimezone());
        //大老板
        userDepositWithDrawStaticReportApi.staticVirtualWithdrawMedal1019ByMonth(siteVO.getSiteCode(),siteVO.getTimezone());
        return ResponseVO.success();
    }

    @Override
    public ResponseVO<Void> siteMedalActiveWeekJob(String siteCode) {
        log.info(" 任务勋章发放周任务{}-", siteCode);
        taskConfigApi.processSendMealJob(siteCode);
        return ResponseVO.success();
    }


    private MedalAcquireReqVO medal1002Active(SiteVO siteVO) {
        String siteCode = siteVO.getSiteCode();
        String timezone = siteVO.getTimezone();
        Long firstDayMilli = TimeZoneUtils.getLastMonStartTimeInTimeZone(timezone);
        Long lastDayMilli = TimeZoneUtils.getLastMonEndTimeInTimeZone(timezone);
        log.info("孤勇者-[{}] 任务月周期{}-{}", siteCode, firstDayMilli, lastDayMilli);
        List<ReportUserWinLosePO> validAmountMaxPOs = reportUserWinLoseService.queryWinLoseAmountMaxByTime(firstDayMilli, lastDayMilli, siteCode);

        if (CollectionUtil.isEmpty(validAmountMaxPOs)) {
            log.info("孤勇者-[{}] 任务月周期 {}-{} 内无用户", siteCode, firstDayMilli, lastDayMilli);
            return null;
        }
        // 汇率
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        validAmountMaxPOs.sort(Comparator.comparing(e -> AmountUtils.divide(e.getBetWinLose().negate(), currency2Rate.getOrDefault(e.getMainCurrency(), BigDecimal.ONE))));
        ReportUserWinLosePO reportUserWinLosePO = validAmountMaxPOs.get(validAmountMaxPOs.size() - 1);
        MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
        medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1002.getCode());
        medalAcquireReqVO.setSiteCode(siteCode);
        medalAcquireReqVO.setUserAccount(reportUserWinLosePO.getUserAccount());
        medalAcquireReqVO.setUserId(reportUserWinLosePO.getUserId());
        return medalAcquireReqVO;
    }

    private MedalAcquireReqVO medal1003Active(SiteVO siteVO) {
        String siteCode = siteVO.getSiteCode();
        String timezone = siteVO.getTimezone();
        Long firstDayMilli = TimeZoneUtils.getLastMonStartTimeInTimeZone(timezone);
        Long lastDayMilli = TimeZoneUtils.getLastMonEndTimeInTimeZone(timezone);
        log.info("功勋卓著-[{}] 任务月周期{}-{}", siteCode, firstDayMilli, lastDayMilli);
        List<ReportUserWinLosePO> validAmountMaxPOs = reportUserWinLoseService.queryValidBetAmountMaxByTime(firstDayMilli, lastDayMilli, siteCode);

        if (CollectionUtil.isEmpty(validAmountMaxPOs)) {
            log.info("功勋卓著-[{}] 任务月周期 {}-{} 内无用户", siteCode, firstDayMilli, lastDayMilli);
            return null;
        }
        // 汇率
        Map<String, BigDecimal> currency2Rate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        validAmountMaxPOs.sort(Comparator.comparing(e -> AmountUtils.divide(e.getValidBetAmount(), currency2Rate.getOrDefault(e.getMainCurrency(), BigDecimal.ONE))));
        ReportUserWinLosePO reportUserWinLosePO = validAmountMaxPOs.get(validAmountMaxPOs.size() - 1);
        MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
        medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1003.getCode());
        medalAcquireReqVO.setSiteCode(siteCode);
        medalAcquireReqVO.setUserAccount(reportUserWinLosePO.getUserAccount());
        medalAcquireReqVO.setUserId(reportUserWinLosePO.getUserId());
        return medalAcquireReqVO;
    }
}
