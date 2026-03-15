package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.enums.ActivityDailyEnum;
import com.cloud.baowang.activity.po.SiteActivityDailyRecordPO;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.play.api.api.venue.PlayVenueInfoApi;
import com.cloud.baowang.play.api.enums.venue.VenueTypeEnum;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.medal.MedalAcquireApi;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@AllArgsConstructor
public class ActivityVenueCodeMedalAwardService {

    private final SiteApi siteApi;

    private final MedalAcquireApi medalAcquireApi;

    private final SiteActivityDailyRecordService siteActivityDailyRecordService;


    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    private final UserInfoApi userInfoApi;

    private final PlayVenueInfoApi playVenueInfoApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    /**
     * ¬
     * 查询出 指定站点 指定场馆类型 连续3天排名前三的用户
     *
     * @param siteVO    站点
     * @param venueType 场馆类型
     * @param day       天数
     * @param ranking   名次
     * @return 用户信息
     */
    public List<SiteActivityDailyRecordPO> queryDailyRecordByVenueTypeTop(SiteVO siteVO, Integer venueType, Integer day, Integer ranking) {
        String timezone = siteVO.getTimezone();
        String siteCode = siteVO.getSiteCode();

        // 获取过去 day 天的开始时间戳
        List<Long> daysList = IntStream.rangeClosed(1, day)
                .mapToObj(i -> TimeZoneUtils.getStartOfDayInTimeZone(
                        TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), -i, timezone), timezone))
                .collect(Collectors.toList());

        // 构建查询条件
        LambdaQueryWrapper<SiteActivityDailyRecordPO> queryWrapper = Wrappers.<SiteActivityDailyRecordPO>lambdaQuery()
                .eq(SiteActivityDailyRecordPO::getVenueType, venueType)
                .eq(SiteActivityDailyRecordPO::getSiteCode, siteCode)
                .le(SiteActivityDailyRecordPO::getRanking, ranking)
                .eq(SiteActivityDailyRecordPO::getRole, ActivityDailyEnum.REAL_USER.getCode())
                .in(SiteActivityDailyRecordPO::getDay, daysList);

        // 查询符合条件的记录
        List<SiteActivityDailyRecordPO> records = siteActivityDailyRecordService.getBaseMapper().selectList(queryWrapper);

        // 如果没有记录，直接返回空列表
        if (CollectionUtil.isEmpty(records)) {
            return List.of();
        }

        // 统计用户数量并过滤出数量 >= day 的用户，并同时收集第一个记录
        Map<String, List<SiteActivityDailyRecordPO>> dailyMap = records.stream()
                .collect(Collectors.groupingBy(SiteActivityDailyRecordPO::getUserId));

        return dailyMap.values().stream()
                .filter(siteActivityDailyRecordPOS -> siteActivityDailyRecordPOS.size() >= day) // 只保留出现次数 >= day 的用户
                .map(siteActivityDailyRecordPOS -> siteActivityDailyRecordPOS.get(0)) // 提取每个用户的第一条记录
                .toList();
    }


    public void activityDailMedalAwardActive(Integer venueType) {
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> data = responseVO.getData();
        for (SiteVO siteVO : data) {
            activityDailMedalAwardActive(siteVO, venueType);
        }
    }

    /**
     * 开始发放每日竞赛勋章
     *
     * @param siteVO    知道你
     * @param venueType 场馆类型
     */
    public void activityDailMedalAwardActive(SiteVO siteVO, Integer venueType) {
        log.info("开始发放每日竞赛勋章:site:{},venueType:{}", siteVO.getSiteCode(), venueType);
        MedalCodeEnum medalCodeEnum = null;
        if (VenueTypeEnum.SH.getCode().equals(venueType)) {
            medalCodeEnum = MedalCodeEnum.MEDAL_1008;
        }

        if (VenueTypeEnum.ELECTRONICS.getCode().equals(venueType)) {
            medalCodeEnum = MedalCodeEnum.MEDAL_1009;
        }

        if (medalCodeEnum == null) {
            return;
        }

        MedalAcquireCondReqVO condReqVO = new MedalAcquireCondReqVO();
        condReqVO.setSiteCode(siteVO.getSiteCode());
        condReqVO.setMedalCodeEnum(medalCodeEnum);
        SiteMedalInfoRespVO siteMedalInfoRespVO = medalAcquireApi.findByMedalCode(condReqVO).getData();

        if (ObjectUtil.isEmpty(siteMedalInfoRespVO)) {
            log.info("查询每日竞赛勋章条件失败:site:{},venueType:{}", siteVO.getSiteCode(), venueType);
            return;
        }

        //天数
        String dayNum = siteMedalInfoRespVO.getCondNum1();
        if (ObjectUtil.isEmpty(dayNum) || Integer.parseInt(dayNum) < 0) {
            log.info("查询每日竞赛勋章条件失败:参数异常.site:{},venueType:{},condNum1:{}", siteVO.getSiteCode(), venueType, dayNum);
            return;
        }


        //名次
        String topNum = siteMedalInfoRespVO.getCondNum2();
        if (ObjectUtil.isEmpty(topNum) || Integer.parseInt(topNum) < 0) {
            log.info("查询每日竞赛勋章条件失败:参数异常.site:{},venueType:{},condNum2:{}", siteVO.getSiteCode(), venueType, topNum);
            return;
        }

        List<SiteActivityDailyRecordPO> recordPOS = queryDailyRecordByVenueTypeTop(siteVO, venueType, Integer.valueOf(dayNum), Integer.valueOf(topNum));
        MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
        medalAcquireBatchReqVO.setSiteCode(siteVO.getSiteCode());
        List<MedalAcquireReqVO> medalDetailList = Lists.newArrayList();
        for (SiteActivityDailyRecordPO dailyRecordPO : recordPOS) {
            MedalAcquireReqVO detail = new MedalAcquireReqVO();
            detail.setSiteCode(siteVO.getSiteCode());
            detail.setMedalCode(medalCodeEnum.getCode());
            detail.setUserId(dailyRecordPO.getUserId());
            detail.setUserAccount(dailyRecordPO.getUserAccount());
            medalDetailList.add(detail);
        }
        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalDetailList);

        if (CollectionUtil.isNotEmpty(medalDetailList)) {
            KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
            log.info("发放每日竞赛勋章奖励:site:{},venueType:{},medalCodeEnum:{}", siteVO.getSiteCode(), venueType, medalAcquireBatchReqVO);
        }
    }

    /**
     * 连续30个自然日均有投注，单日流水$1000以上,场馆类型
     */
    public void activityVenueCodeMedalAwardActive(Integer venueType) {
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> data = responseVO.getData();
        for (SiteVO siteVO : data) {
            activityVenueCodeMedalAwardSiteActive(siteVO, venueType);
        }
    }


    /**
     * 连续30个自然日均有投注，单日流水$1000以上,场馆类型
     *
     * @param siteVO    上班
     * @param venueType 场馆类型
     */
    public void activityVenueCodeMedalAwardSiteActive(SiteVO siteVO, Integer venueType) {

        MedalCodeEnum medalCodeEnum = null;

        if (VenueTypeEnum.SPORTS.getCode().equals(venueType)) {
            medalCodeEnum = MedalCodeEnum.MEDAL_1010;
        }

        if (VenueTypeEnum.ACELT.getCode().equals(venueType)) {
            medalCodeEnum = MedalCodeEnum.MEDAL_1011;
        }

        if (medalCodeEnum == null) {
            log.info("查询场馆连续30日,勋章条件失败:site:{},venueType:{},medalCodeEnum", siteVO.getSiteCode(), venueType);
            return;
        }

        if (ObjectUtil.isEmpty(siteVO.getTimezone())) {
            log.info("连续30个自然日均有投注:{},{},站点没有配置时区", siteVO.getSiteCode(), venueType);
            return;
        }

        log.info("{},siteCode:{},venueType:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType);

        MedalAcquireCondReqVO condReqVO = new MedalAcquireCondReqVO();
        condReqVO.setSiteCode(siteVO.getSiteCode());
        condReqVO.setMedalCodeEnum(medalCodeEnum);
        SiteMedalInfoRespVO siteMedalInfoRespVO = medalAcquireApi.findByMedalCode(condReqVO).getData();

        if (ObjectUtil.isEmpty(siteMedalInfoRespVO)) {
            log.info("{}:勋章条件查询失败:site:{},venueType:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType);
            return;
        }

        String condNum1 = siteMedalInfoRespVO.getCondNum1();
        if (ObjectUtil.isEmpty(condNum1) || Integer.parseInt(condNum1) < 0) {
            log.info("{}:勋章条件失败:site:{},venueType:{},condNum1:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType, condNum1);
            return;
        }

        String condNum2 = siteMedalInfoRespVO.getCondNum2();
        if (ObjectUtil.isEmpty(condNum2) || new BigDecimal(condNum2).compareTo(BigDecimal.ZERO) < 0) {
            log.info("{}:勋章条件失败:site:{},venueType:{},condNum2:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType, condNum2);
            return;
        }

        log.info("{},siteCode:{},venueType:{},获取勋章配置:day:{},amount:{}", medalCodeEnum.getName(),
                siteVO.getSiteCode(), venueType, siteMedalInfoRespVO.getCondNum1(), siteMedalInfoRespVO.getCondNum2());

        int dayNum = Integer.parseInt(condNum1);
        BigDecimal betAmount = new BigDecimal(condNum2);

        long start = TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), -dayNum, siteVO.getTimezone());
        long startTime = TimeZoneUtils.getStartOfDayInTimeZone(start, siteVO.getTimezone());

        //结束时间为昨天的开始时间
        long end = TimeZoneUtils.adjustTimestamp(System.currentTimeMillis(), -1, siteVO.getTimezone());
        long endTime = TimeZoneUtils.getStartOfDayInTimeZone(end, siteVO.getTimezone());


        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteVO.getSiteCode());
        if (ObjectUtil.isEmpty(currencyRateMap)) {
            log.info("{}: 汇率异常,siteCode:{},venueType:{},没有需要继续累计的", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType);
            return;
        }

        ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO.builder()
                .startDayMillisTime(startTime)
                .endDayMillisTime(endTime)
                .venueType(venueType)
                .countDay(dayNum)
                .siteCode(siteVO.getSiteCode())
                .build();
        log.info("{},siteCode:{},venueType:{},获取投注币种查询条件:{}", medalCodeEnum.getName(),
                siteVO.getSiteCode(), venueType, userTopReqVO);

        //查询出最近的 day 天有投注的用户币种
        List<String> currencyList = reportUserVenueFixedWinLoseApi.queryVenueDayCurrency(userTopReqVO);

        if (CollectionUtil.isEmpty(currencyList)) {
            log.info("{}:没有查到投注币种:siteCode:{},venueType:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType);
            return;
        }

        log.info("{},siteCode:{},venueType:{},获取投注币种查询结果:{}", medalCodeEnum.getName(),
                siteVO.getSiteCode(), venueType, currencyList);


        for (String currency : currencyList) {
            ReportUserTopReqVO userTopCurrencyReq = new ReportUserTopReqVO();
            userTopCurrencyReq.setCurrencyCode(currency);
            userTopCurrencyReq.setSiteCode(siteVO.getSiteCode());
            userTopCurrencyReq.setStartDayMillisTime(startTime);
            userTopCurrencyReq.setEndDayMillisTime(endTime);
            userTopCurrencyReq.setVenueType(venueType);
            userTopCurrencyReq.setCountDay(dayNum);
            BigDecimal rate = currencyRateMap.get(currency);
            if (ObjectUtil.isEmpty(rate) || rate.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{}:连续30个自然日均有投注:siteCode:{},venueType:{},currency:{},获取转换汇率失败", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType, currency);
                continue;
            }
            //  平台币种=转换成法币后的 条件值
            BigDecimal currencyAmount = AmountUtils.multiply(betAmount, rate);
            log.info("{}:连续30个自然日均有投注:币种转换:siteCode:{},venueType:{},cond:{},转成{}币:{}", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType, betAmount, currency, currencyAmount);
            if (ObjectUtil.isEmpty(currencyAmount) || currencyAmount.compareTo(BigDecimal.ZERO) <= 0) {
                log.info("{}:siteCode:{},venueType:{},currency:{},平台币转法币转换失败", medalCodeEnum.getName(), siteVO.getSiteCode(), venueType, currency);
                continue;
            }
            userTopCurrencyReq.setPlatformDayBetAmount(currencyAmount);
            venueCodeMedalByCurrency(medalCodeEnum, userTopCurrencyReq);
        }

    }

    /**
     * 处理 连续30个自然日均有投注 ,币种维度处理业务
     */
    private void venueCodeMedalByCurrency(MedalCodeEnum medalCodeEnum, ReportUserTopReqVO userTopCurrencyReq) {
        int pageNumber = 1;
        Integer pageSize = 100;
        userTopCurrencyReq.setPageSize(pageSize);
        boolean hasNext = true;
        while (hasNext) {
            userTopCurrencyReq.setPageNumber(pageNumber);
            Page<ReportUserVenueBetsTopVO> venueBetsTopVOPage = reportUserVenueFixedWinLoseApi.queryUserIdsByVenueDayAmount(userTopCurrencyReq);
            List<ReportUserVenueBetsTopVO> venueBetsTopList = venueBetsTopVOPage.getRecords();
            if (CollectionUtil.isNotEmpty(venueBetsTopList)) {
                List<String> userList = venueBetsTopList.stream().map(ReportUserVenueBetsTopVO::getUserId).toList();
                List<UserInfoVO> userInfoVOList = userInfoApi.getUserInfoByUserIds(userList);
                if (CollectionUtil.isNotEmpty(userInfoVOList)) {
                    Map<String, String> userCurrencyMap = userInfoVOList.stream().collect(Collectors.toMap(UserInfoVO::getUserId, UserInfoVO::getMainCurrency));
                    //执行发放奖励
                    sendActivityVenueCodeMedalAward(medalCodeEnum, userCurrencyMap, userTopCurrencyReq.getSiteCode(), userTopCurrencyReq.getVenueType());
                }
            }
            hasNext = venueBetsTopVOPage.hasNext();
            pageNumber++;
        }
    }


    /**
     * 发放场馆勋章
     *
     * @param toDayUserMap 用户信息
     * @param siteCode     站点
     * @param venueType    场馆类型
     */
    private void sendActivityVenueCodeMedalAward(MedalCodeEnum medalCodeEnum, Map<String, String> toDayUserMap, String siteCode, Integer venueType) {
        log.info("发放勋章:{},siteCode:{},venueType:{}", medalCodeEnum.getName(), siteCode, venueType);

        List<MedalAcquireReqVO> medalDetailList = Lists.newArrayList();
        MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
        medalAcquireBatchReqVO.setSiteCode(siteCode);

        for (Map.Entry<String, String> tmpMap : toDayUserMap.entrySet()) {
            MedalAcquireReqVO detail = new MedalAcquireReqVO();
            detail.setSiteCode(siteCode);
            detail.setMedalCode(medalCodeEnum.getCode());
            detail.setUserId(tmpMap.getKey());
            detail.setUserAccount(tmpMap.getValue());
            medalDetailList.add(detail);
        }

        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalDetailList);

        if (CollectionUtil.isNotEmpty(medalDetailList)) {
            KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
            log.info("发放场馆勋章奖励:site:{},venueType:{},medalCodeEnum:{}", siteCode, venueType, medalCodeEnum.getName());
        }
    }


}
