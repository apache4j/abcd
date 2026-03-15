package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityDailyCompetitionPO;
import com.cloud.baowang.activity.po.SiteActivityDailyRecordPO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.repositories.SiteActivityDailyCompetitionRepository;
import com.cloud.baowang.activity.service.SiteActivityDailyRecordService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.vo.mq.DayVenueBetAmountVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.CurrencyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.*;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.api.ReportUserVenueWinLoseApi;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.user.api.api.SiteUserAvatarConfigApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.enums.ActivityReceiveStatusEnum;
import com.cloud.baowang.user.api.vo.userAvatar.SiteUserAvatarConfigRespVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
public class SiteActivityDetailV2Service {


    private final UserInfoApi userInfoApi;

    private final SiteActivityOrderRecordV2Service siteActivityOrderRecordService;

    private final ReportUserVenueWinLoseApi reportUserVenueWinLoseApi;

    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    private final SiteActivityDailyRecordService siteActivityDailyRecordService;

    private final SiteCurrencyInfoApi currencyInfoApi;

    private final SiteUserAvatarConfigApi siteUserAvatarConfigApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final ApplicationContext applicationContext;
    private final SiteActivityDailyCompetitionRepository siteActivityDailyCompetitionRepository;
    private final SiteActivityBaseService siteActivityBaseService;


    //免费旋转 || 指定日存款校验 周期性活动校验日期
    public Boolean getWeekConfigDetailCheck(String baseRespVO, String timeZone) {
        ActivityFreeWheelRespVO freeWheelRespVO = JSONObject.parseObject(baseRespVO, ActivityFreeWheelRespVO.class);
        String weekDays = freeWheelRespVO.getWeekDays();
        Integer dayOfWeek = DateUtils.getDayOfWeek(System.currentTimeMillis(), timeZone);
        //判断当天是否符合配置
        boolean matchFlag = weekDays.contains(String.valueOf(dayOfWeek));
        log.info("周期性活动校验日期:[{}],[{}],{},{}", weekDays, dayOfWeek, matchFlag, baseRespVO);
        return matchFlag;
    }

    @DistributedLock(name = RedisConstants.ACTIVITY_GET_BATCH_REWARD_LOCK, unique = "#userId", waitTime = 60, leaseTime = 180)
    public ActivityRewardVO getBatchActivityReward(String userId) {
        List<SiteActivityOrderRecordV2PO> list = siteActivityOrderRecordService.getBaseMapper()
                .selectList(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                        .eq(SiteActivityOrderRecordV2PO::getSiteCode, CurrReqUtils.getSiteCode())
                        .eq(SiteActivityOrderRecordV2PO::getUserId, userId)
                        .eq(SiteActivityOrderRecordV2PO::getReceiveStatus, ActivityReceiveStatusEnum.UN_RECEIVE.getCode())
                        .orderByDesc(SiteActivityOrderRecordV2PO::getCreatedTime)
                        .last(" limit 20 "));
        if (CollectionUtil.isEmpty(list)) {
            log.info("领取礼包异常:不存在礼包,userId:{}", CurrReqUtils.getOneId());
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        SiteActivityDetailV2Service siteActivityDetailService = applicationContext.getBean(SiteActivityDetailV2Service.class);
        for (SiteActivityOrderRecordV2PO activityOrderRecordPO : list) {
            siteActivityDetailService.getActivityReward(activityOrderRecordPO.getId(), userId);
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                log.info("领取礼金异常:等待时间");
            }
        }

        return ActivityRewardVO.builder()
                .status(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMessageCode())
                .build();
    }

    public ActivityRewardVO getActivityReward(String id, String userId) {
        String lock = String.format(RedisConstants.ACTIVITY_GET_REWARD_LOCK, id, userId);
        String lockCode = RedisUtil.acquireImmediate(lock, 100L);
        try {
            if (lockCode == null) {
                log.info("并发领取,重复执行,site:{},template:{}", id, userId);
                return ActivityRewardVO.builder()
                        .status(ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL.getCode())
                        .message(ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL.getMessageCode())
                        .build();
            }
            SiteActivityOrderRecordV2PO siteActivityOrderRecordPO = siteActivityOrderRecordService.getBaseMapper().selectOne(Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                    .eq(SiteActivityOrderRecordV2PO::getSiteCode, CurrReqUtils.getSiteCode())
                    .eq(SiteActivityOrderRecordV2PO::getId, id)
                    .eq(SiteActivityOrderRecordV2PO::getUserId, CurrReqUtils.getOneId()));
            if (ObjectUtil.isEmpty(siteActivityOrderRecordPO)) {
                log.info("领取礼包异常:[{}],不存在该礼包,userId:{}", id, CurrReqUtils.getOneId());
                throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
            }

            //已过期
            if (ActivityReceiveStatusEnum.EXPIRED.getCode().equals(siteActivityOrderRecordPO.getReceiveStatus())) {
                log.info("领取礼包异常:[{}],该礼包已过期,userId:{}", id, CurrReqUtils.getOneId());
                return ActivityRewardVO.builder()
                        .status(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED.getCode())
                        .message(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED.getMessageCode())
                        .build();
            }

            //已领取
            if (ActivityReceiveStatusEnum.RECEIVE.getCode().equals(siteActivityOrderRecordPO.getReceiveStatus())) {
                log.info("领取礼包异常:[{}],尝试重复领取,拦截,userId:{}", id, CurrReqUtils.getOneId());
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

            long nowTime = System.currentTimeMillis();

            if (ObjectUtil.isNotEmpty(siteActivityOrderRecordPO.getReceiveStartTime()) && siteActivityOrderRecordPO.getReceiveStartTime() > nowTime) {
                log.info("领取礼包异常:{},该礼包未到领取时间,userId:{}", id, CurrReqUtils.getOneId());
                return ActivityRewardVO.builder()
                        .status(ResultCode.ACTIVITY_NOT_YET_CLAIM_TIME.getCode())
                        .message(ResultCode.ACTIVITY_NOT_YET_CLAIM_TIME.getMessageCode())
                        .build();

            }

            if (ObjectUtil.isNotEmpty(siteActivityOrderRecordPO.getReceiveEndTime()) && siteActivityOrderRecordPO.getReceiveEndTime() < nowTime) {
                log.info("领取礼包异常:{},该礼包已过期,userId:{}", id, CurrReqUtils.getOneId());
                return ActivityRewardVO.builder()
                        .status(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED.getCode())
                        .message(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED.getMessageCode())
                        .build();
            }

            if (!siteActivityOrderRecordService.upActivityReward(id, ActivityClaimBehaviorEnum.USER_SELF_CLAIM)) {
                log.info("领取礼包异常:{},领取失败,userId:{}", id, CurrReqUtils.getOneId());
                return ActivityRewardVO.builder()
                        .status(ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL.getCode())
                        .message(ResultCode.ACTIVITY_NOT_YET_CLAIM_FAIL.getMessageCode())
                        .build();

            }
            return ActivityRewardVO.builder()
                    .status(ResultCode.SUCCESS.getCode())
                    .message(ResultCode.SUCCESS.getMessageCode())
                    .build();

        } catch (Exception e) {
            log.error("领取礼包异常异常", e);
            throw e;
        } finally {
            if (ObjectUtil.isNotEmpty(lockCode)) {
                boolean release = RedisUtil.release(lock, lockCode);
                log.info("领取礼包:{},执行结束,删除锁:{}", lock, release);
            }
        }
    }

    /**
     * 获取每日竞赛的场馆
     */
    public ActivityPartDailyCompletionVenueRespVO queryActivityDailyContestVenueCode(ActivityDailyCompetitionRespVO activityDailyCompetitionRespVO) {
        if (ObjectUtil.isEmpty(activityDailyCompetitionRespVO) || CollectionUtil.isEmpty(activityDailyCompetitionRespVO.getList())) {
            return null;
        }

        List<ActivityPartDailyCompletionVenueDetailRespVO> list = activityDailyCompetitionRespVO.getList().stream().map(x -> {
            return ActivityPartDailyCompletionVenueDetailRespVO.builder().activityName(x.getActivityNameI18nCode()).id(x.getId()).build();
        }).toList();

        return ActivityPartDailyCompletionVenueRespVO.builder().activityNameI18nCode(activityDailyCompetitionRespVO.getActivityNameI18nCode()).list(list).build();
    }

    public ActivityPartDailyCompletionVenueRespVO newQueryActivityDailyContestVenueCode() {
        List<SiteActivityBasePO> list = siteActivityBaseService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.DAILY_COMPETITION.getType())
                .eq(SiteActivityBasePO::getSiteCode, CurrReqUtils.getSiteCode()));
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        List<String> idList = Lists.newArrayList();
        for (SiteActivityBasePO item : list) {
            if (item.getStatus().equals(StatusEnum.OPEN.getCode())) {
                //展示时间开始 未到
                if (item.getShowStartTime() > System.currentTimeMillis()) {
                    continue;
                }

                //限时活动已过了展示期
                if (item.getActivityDeadline().equals(ActivityDeadLineEnum.LIMITED_TIME.getType())
                        && item.getShowEndTime() < System.currentTimeMillis()) {
                    continue;
                }

                idList.add(item.getId());
            }
        }


        if (CollectionUtil.isEmpty(idList)) {
            return null;
        }

        SiteActivityBasePO siteActivityBasePO =  list.get(0);

        List<SiteActivityDailyCompetitionPO> siteActivityDailyCompetitionPOList = siteActivityDailyCompetitionRepository.selectList(Wrappers.lambdaQuery(SiteActivityDailyCompetitionPO.class)
                .in(SiteActivityDailyCompetitionPO::getActivityId, idList));

        if (CollectionUtil.isEmpty(siteActivityDailyCompetitionPOList)) {
            return null;
        }

        List<ActivityPartDailyCompletionVenueDetailRespVO> resultList = siteActivityDailyCompetitionPOList.stream().map(x -> {
            return ActivityPartDailyCompletionVenueDetailRespVO.builder().activityName(x.getCompetitionI18nCode()).id(x.getId()).build();
        }).toList();


        return ActivityPartDailyCompletionVenueRespVO.builder().activityNameI18nCode(siteActivityBasePO.getActivityNameI18nCode()).list(resultList).build();


    }


    /**
     * 每日竞赛-用户当前排名
     */
    private void userRanking(ActivityPartDailyCompletionRespVO respVO) {

    }


    /**
     * 每日竞赛-活动时间
     */
    private void setSecond(ActivityPartDailyCompletionRespVO respVO) {

        // 站点时区
        String timezone = CurrReqUtils.getTimezone();

        long currentTimeMillis = System.currentTimeMillis();

        long endTime = TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timezone);

        //活动剩余秒数
        long second = (endTime - currentTimeMillis) / 1000;
        if (second <= 0) {
            second = 0;
        }
        respVO.setSecond(second);
    }

    /**
     * 获取上届冠军信息
     */
    private void setPrevious(ActivityDailyCompetitionDetailRespVO config, ActivityPartDailyCompletionRespVO result) {
        //获取出今天之前发放的第一名
        SiteActivityDailyRecordPO dailyRecordPO = siteActivityDailyRecordService.getBaseMapper()
                .selectOne(Wrappers.lambdaQuery(SiteActivityDailyRecordPO.class)
                        .eq(SiteActivityDailyRecordPO::getDailyId, config.getId())
                        .eq(SiteActivityDailyRecordPO::getSiteCode, CurrReqUtils.getSiteCode())
                        .eq(SiteActivityDailyRecordPO::getRanking, 1)
                        .lt(SiteActivityDailyRecordPO::getDay,
                                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone())
                        )
                        .orderByDesc(SiteActivityDailyRecordPO::getCreatedTime)
                        .last(" limit 1 ")
                );

        //没有上届冠军
        if (ObjectUtil.isEmpty(dailyRecordPO)) {
            return;
        }

        ActivityPartDailyPreviousRespVO respVO = ActivityPartDailyPreviousRespVO.builder()
                .build();
        BeanUtils.copyProperties(dailyRecordPO, respVO);

        String symbol = CurrReqUtils.getPlatCurrencySymbol();
        if (!dailyRecordPO.getAwardCurrency().equals(CommonConstant.PLAT_CURRENCY_CODE)) {
            CurrencyEnum currencyEnum = CurrencyEnum.nameOfCode(dailyRecordPO.getAwardCurrency());
            symbol = symbol == null ? null : currencyEnum.getSymbol();
        }
        respVO.setCurrencySymbol(symbol);


        //如果是真实用户
        if (dailyRecordPO.getRole().equals(ActivityDailyEnum.REAL_USER.getCode())) {
            //真实用户的头像
            UserInfoVO userInfoVO = userInfoApi.getByUserId(dailyRecordPO.getUserId());
            if (ObjectUtil.isNotEmpty(userInfoVO)) {
                respVO.setIcon(userInfoVO.getAvatar());
            }
        } else {
            String roleIcon = dailyRecordPO.getRoleIcon();
            if (ObjectUtil.isEmpty(roleIcon)) {
                //随机取一个头像
                SiteUserAvatarConfigRespVO siteUserAvatarConfigRespVO = siteUserAvatarConfigApi.getRandomUserAvatar(CurrReqUtils.getSiteCode());
                if (ObjectUtil.isNotEmpty(siteUserAvatarConfigRespVO)) {
                    roleIcon = siteUserAvatarConfigRespVO.getAvatarImageUrl();
                }
            }
            respVO.setIcon(roleIcon);
        }
        respVO.setActivityAmountPer(dailyRecordPO.getAwardPercentage());
        result.setPrevious(respVO);
    }


    /**
     * 获取奖池金额, 低池 + 场馆比例
     */
    private BigDecimal getTotalRewardsAmountById(String id, Long startTime, ActivityDailyCompetitionRespVO base) {
        ActivityDailyCompetitionDetailRespVO detailRespVO = getActivityDailyCompetitionById(id, base);

        if (detailRespVO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        return getTotalRewardsAmount(detailRespVO, CurrReqUtils.getSiteCode(), startTime, CurrReqUtils.getTimezone());
    }


    public BigDecimal getTotalRewardsAmount(ActivityDailyCompetitionDetailRespVO detailRespVO, String siteCode, Long startTime, String timeZone) {
        //底池
        BigDecimal initAmount = detailRespVO.getInitAmount();
        if (ObjectUtil.isEmpty(initAmount)) {
            initAmount = BigDecimal.ZERO;
        }

        //实际金额指定场馆总流水的
        BigDecimal venuePercentage = detailRespVO.getVenuePercentage();
        Long startDayTime = TimeZoneUtils.getStartOfDayInTimeZone(startTime, timeZone);
        List<String> venueCodeList = detailRespVO.getVenueCodeList();

        BigDecimal platAmount = BigDecimal.ZERO;

        if (venuePercentage.compareTo(BigDecimal.ZERO) > 0) {
            for (String venueCode : venueCodeList) {
                String key = String.format(RedisConstants.VENUE_SITE_DAY_TOTAL_BET_AMOUNT, venueCode, siteCode, startDayTime);
                DayVenueBetAmountVO dayVenueBetAmountVO = RedisUtil.getValue(key);
                if (ObjectUtil.isNotEmpty(dayVenueBetAmountVO)) {
                    BigDecimal betAmount = dayVenueBetAmountVO.getValidAmount();
                    if (ObjectUtil.isEmpty(betAmount) || betAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        continue;
                    }
                    platAmount = platAmount.add(betAmount);
                }
            }

            //查询某个场馆当总量如果没有值则从数据库中查询
            if (platAmount.compareTo(BigDecimal.ZERO) <= 0) {
                ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO
                        .builder()
                        .siteCode(siteCode)
                        .venueCodeList(venueCodeList)
                        .dayMillis(startDayTime)
                        .build();
                ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO = reportUserVenueFixedWinLoseApi.queryVenueBetsPlatBetAmountTotal(userTopReqVO);
                if (ObjectUtil.isNotEmpty(ReportUserVenueBetsTopVO) && ReportUserVenueBetsTopVO.getPlatValidAmount() != null &&
                        ReportUserVenueBetsTopVO.getPlatValidAmount().compareTo(BigDecimal.ZERO) > 0) {
                    platAmount = platAmount.add(ReportUserVenueBetsTopVO.getPlatValidAmount());
                }
            }

            //场馆总流水 > 0 && 场馆总流水的比例 > 0
            if (platAmount.compareTo(BigDecimal.ZERO) > 0) {
                platAmount = platAmount.multiply(venuePercentage).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);
            }
        }


        BigDecimal totalRewardsAmount = initAmount.add(platAmount);
        return totalRewardsAmount.setScale(2, RoundingMode.HALF_UP);
    }


    /**
     * 根据 每日竞赛 详情ID 查询出配置对象
     */
    private ActivityDailyCompetitionDetailRespVO getActivityDailyCompetitionById(String id, ActivityDailyCompetitionRespVO base) {
        if (ObjectUtil.isEmpty(base) || CollectionUtil.isEmpty(base.getList())) {
            return null;
        }
        List<ActivityDailyCompetitionDetailRespVO> list = base.getList();
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }

        Map<String, ActivityDailyCompetitionDetailRespVO> partDailyCompletionRespMap = list.stream().collect(Collectors
                .toMap(ActivityDailyCompetitionDetailRespVO::getId, ActivityPartDailyCompletionRespVO -> ActivityPartDailyCompletionRespVO));

        ActivityDailyCompetitionDetailRespVO detail = partDailyCompletionRespMap.get(id);
        if (ObjectUtil.isEmpty(detail)) {
            log.info("请求参数异常,id:{}", id);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        detail.setActivityRuleI18nCode(base.getActivityRuleI18nCode());
        return detail;
    }

    public ActivityPartDailyCompletionRespVO queryNotOpenActivityDailyContest(String id, ActivityDailyCompetitionRespVO base) {
        ActivityPartDailyCompletionRespVO result = ActivityPartDailyCompletionRespVO.builder().type(false).build();
        long nowTime = System.currentTimeMillis();
        //查显示时间,这个正常情况下不会是空,因为客户端调用这个接口的前提条件是有显示时间才可调的
        if (base != null && base.getShowStartTime() <= nowTime && base.getShowEndTime() > nowTime) {
            //计算出活动开始时间还差多少秒
            Long startTime = base.getActivityStartTime();
            long now = System.currentTimeMillis();
            long time = (startTime - now) / 1000;
            if (time <= 0) {
                time = 0L;
            }
            UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
            String userCurrencyCode = userInfoVO.getMainCurrency();
            String userCurrencySymbol = CurrencyEnum.symbolByCode(userCurrencyCode);
            ActivityPartDailyCompletionUserRespVO user = ActivityPartDailyCompletionUserRespVO
                    .builder()
                    .userAccount(CurrReqUtils.getAccount())//账号
                    .icon(userInfoVO.getAvatar())//头像
                    .lackBetAmount(BigDecimalUtils.formatFourKeep4Dec(BigDecimal.ZERO))//距离上榜投注金额
                    .currencySymbol(userCurrencySymbol)
                    .build();
            result.setSecond(time);
            result.setUser(user);
            result.setCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
            result.setActivityRule(base.getActivityRuleI18nCode());
            BigDecimal venueTotalAmount = getTotalRewardsAmountById(id, nowTime, base);
            result.setTotalRewardsAmount(venueTotalAmount);
        }
        return result;
    }

    private ReportUserVenueBetsTopVO queryUserVenueBetsByUserId(List<String> venueCodeList, String userId) {
        long currentTimeMillis = System.currentTimeMillis();
        String timezone = CurrReqUtils.getTimezone();

        ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO
                .builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .venueCodeList(venueCodeList)
                .userId(userId)
                .dayMillis(TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timezone))
                .build();

        return reportUserVenueFixedWinLoseApi.queryUserBetsPlatBetAmountTotal(userTopReqVO);
    }

    /**
     * 查询出当天的打码量,每日竞赛
     */
    private List<ReportUserVenueBetsTopVO> queryReportUserVenueBetsTopVO(List<String> venueCodeList) {
        long currentTimeMillis = System.currentTimeMillis();
        String timezone = CurrReqUtils.getTimezone();

        ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO
                .builder()
                .siteCode(CurrReqUtils.getSiteCode())
                .venueCodeList(venueCodeList)
                .dayMillis(TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timezone))
                .build();
        return reportUserVenueFixedWinLoseApi.queryUserBetsTopPlatBetAmount(userTopReqVO);
    }


    public ActivityPartDailyCompletionRespVO queryActivityDailyContest(String id, ActivityDailyCompetitionRespVO base) {
        ActivityDailyCompetitionDetailRespVO detailRespVO = getActivityDailyCompetitionById(id, base);
        String siteCode = CurrReqUtils.getSiteCode();

        if (detailRespVO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(CurrReqUtils.getOneId());
        String userCurrencyCode = userInfoVO.getMainCurrency();
        String userCurrencySymbol = CurrencyEnum.symbolByCode(userCurrencyCode);

        ActivityPartDailyCompletionRespVO respVO = ActivityPartDailyCompletionRespVO.builder()
                .id(detailRespVO.getId())
                .activityName(detailRespVO.getActivityNameI18nCode())
                .activityRule(detailRespVO.getActivityRuleI18nCode())
                .currencySymbol(CurrReqUtils.getPlatCurrencySymbol())
                .type(true)
                .build();

        //上届冠军
        setPrevious(detailRespVO, respVO);

        //活动时间,秒
        setSecond(respVO);

        //计算奖池信息
        BigDecimal venueTotalAmount = getTotalRewardsAmountById(id, System.currentTimeMillis(), base);
        respVO.setTotalRewardsAmount(venueTotalAmount);

        //前100排名
        List<ActivityPartUserRankingDailyRespVO> top100ResultList = getToDayRoleDailyRecord(id, base);

        respVO.setTime(TimeZoneUtils.formatLocalDateTime(TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), DateUtils.FULL_FORMAT_5));


        ActivityPartDailyCompletionUserRespVO resultUser = ActivityPartDailyCompletionUserRespVO.builder()
                .userAccount(CurrReqUtils.getAccount())
                .icon(userInfoVO.getAvatar())
                .currencySymbol(userCurrencySymbol)
                .build();

        //至少要配置机器人不能一条数据都没有
        if (CollectionUtil.isEmpty(top100ResultList)) {
            log.info("每日竞赛排行榜中一条数据都没有,不做任何处理直接返回");
            return respVO;
        }
        log.info("排名:{}", top100ResultList);

        //前10名排名
        List<ActivityPartUserRankingDailyRespVO> top50ResultList = top100ResultList.stream()
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getRanking))
                .limit(50)
                .toList();
        respVO.setList(top50ResultList);

        //用户是否排名前10
        List<ActivityPartUserRankingDailyRespVO> userRanking = top50ResultList.stream().filter(ActivityPartUserRankingDailyRespVO::getSpecialShow).toList();

        Map<Integer, ActivityPartUserRankingDailyRespVO> rankingMap = top100ResultList.stream()
                .collect(Collectors.toMap(ActivityPartUserRankingDailyRespVO::getRanking,
                        ActivityPartUserRankingDailyRespVO -> ActivityPartUserRankingDailyRespVO));


        Map<String, BigDecimal> rateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);

        if (CollectionUtil.isEmpty(rateMap)) {
            log.info("获取站点币种汇率转换异常:{}", siteCode);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }


        //排行榜第一名
        ActivityPartUserRankingDailyRespVO rangOne = rankingMap.get(1);

        //第一名用户如果是真实用户,并且是当前用户
        if (rangOne.getSpecialShow()) {
            toSetTop1(resultUser, rankingMap, userCurrencyCode, rateMap);
            log.info("排名计算:{},第1", CurrReqUtils.getOneId());
        } else if (CollectionUtil.isNotEmpty(userRanking)) {//用户在前50上榜名单中
            toSetTop50(resultUser, userRanking, rankingMap, userCurrencyCode, rateMap);
            log.info("排名计算:{},前50", CurrReqUtils.getOneId());
        } else {//用户不在上榜名单中
            toSetNotTop(resultUser, detailRespVO, top100ResultList, userInfoVO, rateMap);
            log.info("排名计算:{},未上榜", CurrReqUtils.getOneId());
        }

        //当所差金额为0当时候默认给0.1
        if (ObjectUtil.isNotEmpty(resultUser.getLackBetAmount()) && resultUser.getLackBetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            resultUser.setLackBetAmount(BigDecimalUtils.formatFourKeep4Dec(BigDecimal.valueOf(0.1)));
        }

        //用户当前排名
        respVO.setUser(resultUser);
        return respVO;
    }


    /**
     * 用户不是第一名 也不在 前10 则未上榜处理方法
     *
     * @param userResp         需要返回的对象
     * @param detailRespVO     场馆配置信息
     * @param top100ResultList 前100名
     * @param userInfoVO       用户对象
     * @param rateMap          汇率
     */
    private void toSetNotTop(ActivityPartDailyCompletionUserRespVO userResp, ActivityDailyCompetitionDetailRespVO detailRespVO,
                             List<ActivityPartUserRankingDailyRespVO> top100ResultList, UserInfoVO userInfoVO, Map<String, BigDecimal> rateMap) {
        //排行榜中都没有用户则默认给他 100+
        int ranking = top100ResultList.size() + 1;

        //排行榜差额
        BigDecimal lackBetAmount;

        //前50
        List<ActivityPartUserRankingDailyRespVO> top10ResultList = top100ResultList.stream()
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getRanking))
                .limit(50)
                .toList();

        //上榜名单中的最后一位
        ActivityPartUserRankingDailyRespVO lastUserRanking = top10ResultList.stream()
                .sorted(Comparator.comparingInt(ActivityPartUserRankingDailyRespVO::getRanking).reversed())
                .toList().get(0);

        //当前用户的打码量在前100中
        List<ActivityPartUserRankingDailyRespVO> top100UserList = top100ResultList.stream().filter(ActivityPartUserRankingDailyRespVO::getSpecialShow).toList();

        //用户的打码量
        BigDecimal userBetAmount = BigDecimal.ZERO;

        //用户的平台币打码量
        BigDecimal userPlatBetAmount = BigDecimal.ZERO;

        //排行榜中没有数据 单独去查用户的打码
        if (CollectionUtil.isEmpty(top100UserList)) {
            //并且当前用户不在上榜列表里面,就需要单独去在查一次用户的投注,并且计算出还差多少可以上榜
            ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO = queryUserVenueBetsByUserId(detailRespVO.getVenueCodeList(), userInfoVO.getUserId());
            if (ObjectUtil.isNotEmpty(ReportUserVenueBetsTopVO)) {
                userBetAmount = ReportUserVenueBetsTopVO.getBetAmount();
                userPlatBetAmount = ReportUserVenueBetsTopVO.getPlatBetAmount();
            }

        } else {
            //直接从前100当中取出当前用户的打码量
            ActivityPartUserRankingDailyRespVO userRankingDailyRespVO = top100UserList.get(0);
            userBetAmount = userRankingDailyRespVO.getBetAmount();
            userPlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(userRankingDailyRespVO.getPlatBetAmount());
            ranking = userRankingDailyRespVO.getRanking();
        }

        //当前用户的币种如果与上一位的排行榜用户的投注是相同 的币种,则不进行币种转换 直接用 相减
        if (userInfoVO.getMainCurrency().equals(lastUserRanking.getBetCurrencyCode())) {
            lackBetAmount = lastUserRanking.getBetAmount().subtract(userBetAmount);
        } else {
            //上一位用户的平台币投注额
            BigDecimal prePlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(lastUserRanking.getPlatBetAmount());
            //上一位排行榜的用户的投注 - 当前用户的投注 = 所差的打码
            BigDecimal lackPlatBetAmount = prePlatBetAmount.subtract(userPlatBetAmount);
            lackBetAmount = AmountUtils.multiply(lackPlatBetAmount, rateMap.get(userInfoVO.getMainCurrency()));
        }

        userResp.setRanking(ranking);
        userResp.setUserStatus(ActivityCompletionUserStatusTypeEnum.NOT_RANKED.getCode());
        userResp.setBetAmount(BigDecimalUtils.formatFourKeep4Dec(userBetAmount));
        userResp.setLackBetAmount(BigDecimalUtils.formatFourKeep4Dec(lackBetAmount));
    }


    /**
     * 用户不是排行榜中第一名,已上榜用户
     *
     * @param userResp         需要返回的数据
     * @param userRanking      当前用户在排行榜中的数据
     * @param rankingMap       排行榜列表
     * @param userCurrencyCode 当前用户的法币
     * @param rateMap          站点汇率配置
     */
    private void toSetTop50(ActivityPartDailyCompletionUserRespVO userResp,
                            List<ActivityPartUserRankingDailyRespVO> userRanking,
                            Map<Integer, ActivityPartUserRankingDailyRespVO> rankingMap, String userCurrencyCode, Map<String, BigDecimal> rateMap) {

        //排行榜差额
        BigDecimal lackBetAmount;

        //当前用户不是第一名,但是在排行榜中
        ActivityPartUserRankingDailyRespVO userRankingDailyRespVO = userRanking.get(0);


        Integer ranking = userRankingDailyRespVO.getRanking();

        //当前用户的平台币投注额
        BigDecimal userPlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(userRankingDailyRespVO.getPlatBetAmount());

        ActivityPartUserRankingDailyRespVO previousDaily = null;

        //如果用户的排名没超出10名则取出他的上一名的数据取出来去计算差额 = 进入上一名的上榜的差额
        previousDaily = rankingMap.get(ranking - 1);

        //当前用户的币种如果与上一位的排行榜用户的投注是相同 的币种,则不进行币种转换 直接用 相减
        if (userCurrencyCode.equals(previousDaily.getBetCurrencyCode())) {
            lackBetAmount = previousDaily.getBetAmount().subtract(userRankingDailyRespVO.getBetAmount());
        } else {
            //上一位用户的平台币投注额
            BigDecimal prePlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(previousDaily.getPlatBetAmount());
            //上一位排行榜的用户的投注 - 当前用户的投注 = 所差的打码
            BigDecimal lackPlatBetAmount = prePlatBetAmount.subtract(userPlatBetAmount);
            lackBetAmount = AmountUtils.multiply(lackPlatBetAmount, rateMap.get(userCurrencyCode));
        }

        userResp.setRanking(ranking);
        userResp.setUserStatus(ActivityCompletionUserStatusTypeEnum.RANKED.getCode());
        userResp.setBetAmount(BigDecimalUtils.formatFourKeep4Dec(userRankingDailyRespVO.getBetAmount()));
        userResp.setLackBetAmount(BigDecimalUtils.formatFourKeep4Dec(lackBetAmount));
    }

    /**
     * 排行榜第一名
     *
     * @param userResp         需要返回的数据
     * @param rankingMap       排行榜数据
     * @param userCurrencyCode 当前用户的法币
     * @param rateMap          站点汇率配置
     */
    private void toSetTop1(ActivityPartDailyCompletionUserRespVO userResp, Map<Integer, ActivityPartUserRankingDailyRespVO> rankingMap,
                           String userCurrencyCode, Map<String, BigDecimal> rateMap) {

        BigDecimal lackBetAmount = BigDecimal.ZERO;

        ActivityPartUserRankingDailyRespVO rangOne = rankingMap.get(1);
        //计算出第二名与第一名的差额
        ActivityPartUserRankingDailyRespVO rangTwo = rankingMap.get(2);
        if (rangTwo != null) {
            //计算出第二名的投注额的平台币
            BigDecimal twoPlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(rangTwo.getPlatBetAmount());

            //第二名投注的法币
            String rangTwoCurrency = rangTwo.getBetCurrencyCode();

            //如果第二名与第一名相差的货币是相同的货币 就不用转货币,而是直接用货币计算,因为大家都是相同的货币
            if (userCurrencyCode.equals(rangTwoCurrency)) {
                lackBetAmount = rangOne.getBetAmount().subtract(rangTwo.getBetAmount());
            } else {
                String oneBetCurrencyCode = rangOne.getBetCurrencyCode();
                BigDecimal oneRate = rateMap.get(oneBetCurrencyCode);//获取出第一名的货币币种
                if (oneRate == null || oneRate.compareTo(BigDecimal.ZERO) <= 0) {
                    log.info("每日竞赛:排行榜获取第一名的币种汇率异常,siteCode:{},currencyCode:{}", CurrReqUtils.getSiteCode(), oneBetCurrencyCode);
                    throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                }

                //计算出第一名的投注额的平台币
                BigDecimal onePlatBetAmount = BigDecimalUtils.formatFourKeep4Dec(rangOne.getPlatBetAmount());

                //第一名的平台币 减去 第二名的平台币 差额 = 查额,在将查额的平台币转成当前用户的法币
                BigDecimal lackPlatBetAmount = onePlatBetAmount.subtract(twoPlatBetAmount);

                //平台币转法币
                lackBetAmount = AmountUtils.multiply(lackPlatBetAmount, rateMap.get(userCurrencyCode));
            }
        }
        //用户打码量
        userResp.setBetAmount(BigDecimalUtils.formatFourKeep4Dec(rangOne.getBetAmount()));

        //当前用户排行状态
        userResp.setUserStatus(ActivityCompletionUserStatusTypeEnum.FIRST_PLACE.getCode());
        userResp.setRanking(1);
        //所差额
        userResp.setLackBetAmount(BigDecimalUtils.formatFourKeep4Dec(lackBetAmount));
    }


    /**
     * 将用户用户信息与机器人信息进行排行榜排序
     *
     * @param config           配置机器人数据
     * @param userVenueBetsTop 真实用户数据
     * @param siteCode         站点
     * @param totalAward       总奖池
     * @return 已经排序好的用户信息
     */
    public List<ActivityRankingDailyVO> updateRankings(ActivityDailyCompetitionDetailRespVO config,
                                                       List<ReportUserVenueBetsTopVO> userVenueBetsTop,
                                                       String siteCode, BigDecimal totalAward) {

        Map<String, BigDecimal> rateMap = currencyInfoApi.getAllFinalRate(siteCode);

        if (CollectionUtil.isEmpty(rateMap)) {
            return Lists.newArrayList();
        }

        //奖励设置排名配置
        List<SiteActivityDailyCompetitionDetail> activityDetail = config.getActivityDetail();

        //key = 排名 , value = 奖励配置
        Map<Integer, SiteActivityDailyCompetitionDetail> rankingMap = activityDetail.stream().collect
                (Collectors.toMap(SiteActivityDailyCompetitionDetail::getRanking, SiteActivityDailyCompetitionDetail -> SiteActivityDailyCompetitionDetail));


        //机器人配置
        List<ActivityDailyRobotRespVO> robotList = config.getRobotList();

        // 按 机器人流水 从大到小排序
        robotList = robotList.stream()
                .filter(vo -> vo.getPlatBetAmount() != null)
                .sorted(Comparator.comparing(ActivityDailyRobotRespVO::getPlatBetAmount).reversed())
                .toList();


        //真实用户从大到小排序
        userVenueBetsTop = userVenueBetsTop.stream()
                .filter(vo -> vo.getValidAmount() != null)
                .sorted(Comparator.comparing(ReportUserVenueBetsTopVO::getValidAmount).reversed())
                .toList();


        // 创建一个列表，用于存放所有用户的投注信息
        List<ActivityRankingDailyVO> combinedList = Lists.newArrayList();

        for (ActivityDailyRobotRespVO robot : robotList) {
            BigDecimal robotBetAmount = BigDecimal.ZERO;
            BigDecimal usdRate = rateMap.get(CurrencyEnum.USDT.getCode());


            //机器人初始化平台币转法币金额
            BigDecimal initRobotCurrencyBetAmount = BigDecimal.ZERO;

            //机器人的法币,平台币转USD
            if (ObjectUtil.isNotEmpty(usdRate) && usdRate.compareTo(BigDecimal.ZERO) > 0) {
                robotBetAmount = AmountUtils.multiply(robot.getPlatBetAmount(), usdRate);
            }

            //机器人的法币,平台币转USD
            if (ObjectUtil.isNotEmpty(usdRate) && usdRate.compareTo(BigDecimal.ZERO) > 0 && robot.getInitRobotBetAmount().compareTo(BigDecimal.ZERO) > 0) {
                initRobotCurrencyBetAmount = AmountUtils.multiply(robot.getInitRobotBetAmount(), usdRate);
            }

            //机器人数据
            ActivityRankingDailyVO newRobot = ActivityRankingDailyVO
                    .builder()
                    .ranking(robot.getRanking())
                    .userAccount(robot.getUserAccount())
                    .userId(robot.getUserAccount())
                    .betAmount(robotBetAmount)
                    .betSymbol(CurrencyEnum.symbolByCode(CurrencyEnum.USDT.getCode()))//机器人的法币投注币种固定为USD
                    .currencyCode(CurrencyEnum.USDT.getCode())//机器人的法币投注币种固定为USD
                    .platBetAmount(robot.getPlatBetAmount())
                    .platBetSymbol(Character.toString(CommonConstant.PLAT_FORM_SYMBOL))//平台币投注符号
                    .platCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE)//平台币投注币种
                    .type(true)
                    .robotId(robot.getRobotId())
                    .betGrowthPct(robot.getBetGrowthPct())
                    .edit(robot.getEdit())
                    .initRobotBetAmount(robot.getInitRobotBetAmount())
                    .initRobotCurrencyBetAmount(initRobotCurrencyBetAmount)
                    .maxRobotBetAmount(robot.getMaxRobotBetAmount())
                    .build();
            combinedList.add(newRobot);
        }


        //真实用户数据
        for (ReportUserVenueBetsTopVO betsTopVO : userVenueBetsTop) {
            ActivityRankingDailyVO newRobot = ActivityRankingDailyVO
                    .builder()
                    .currencyCode(betsTopVO.getCurrency())
                    .betSymbol(CurrencyEnum.symbolByCode(betsTopVO.getCurrency()))
                    .userAccount(betsTopVO.getUserAccount())
                    .userId(betsTopVO.getUserId())
                    .betAmount(betsTopVO.getValidAmount())
                    .platBetAmount(betsTopVO.getPlatValidAmount())
                    .platBetSymbol(Character.toString(CommonConstant.PLAT_FORM_SYMBOL))//平台币投注符号
                    .platCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE)//平台币投注币种
                    .type(false)
                    .build();
            combinedList.add(newRobot);
        }

        combinedList = combinedList.stream()
                .sorted(Comparator.comparing(ActivityRankingDailyVO::getPlatBetAmount).reversed())
                .limit(100)
                .toList();

        int ranking = 0;

        for (ActivityRankingDailyVO dailyVO : combinedList) {
            ranking++;
            dailyVO.setRanking(ranking);
            SiteActivityDailyCompetitionDetail siteActivityDailyCompetitionDetail = rankingMap.get(ranking);
            if (ObjectUtil.isEmpty(siteActivityDailyCompetitionDetail)) {
                continue;
            }

            String awardCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            String awardSymbol = Character.toString(CommonConstant.PLAT_FORM_SYMBOL);

            BigDecimal award;
            //百分比
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(config.getActivityDiscountType())) {
                BigDecimal activityAmountPer = siteActivityDailyCompetitionDetail.getActivityAmountPer();
                dailyVO.setActivityAmountPer(activityAmountPer);
                //计算出 百分比 打码量对应的奖励
                BigDecimal platAward = AmountUtils.multiplyPercent(totalAward, activityAmountPer);
                //机器人 投注金额跟奖金需要转USD
                if (dailyVO.getType()) {
                    awardCurrencyCode = CurrencyEnum.USDT.getCode();
                    awardSymbol = CurrencyEnum.symbolByCode(CurrencyEnum.USDT.getCode());
                    //平台币转法币
                    award = AmountUtils.multiply(platAward, rateMap.get(CurrencyEnum.USDT.getCode()));
                } else {
                    //百分比-不是机器人赠送的币种是法币
                    awardCurrencyCode = dailyVO.getCurrencyCode();
                    awardSymbol = CurrencyEnum.symbolByCode(dailyVO.getCurrencyCode());
                    //平台币转法币
                    award = AmountUtils.multiply(platAward, rateMap.get(awardCurrencyCode));
                }
            } else {
                award = siteActivityDailyCompetitionDetail.getActivityAmount();
            }

            dailyVO.setAwardAmount(award);
            dailyVO.setAwardCurrencyCode(awardCurrencyCode);
            dailyVO.setAwardSymbol(awardSymbol);
            dailyVO.setPlatBetAmount(dailyVO.getPlatBetAmount());
        }


        return combinedList;
    }


    public BigDecimal queryActivityDailyPrizePool(String id, ActivityDailyCompetitionRespVO base) {
        return getTotalRewardsAmountById(id, System.currentTimeMillis(), base);
    }

    public ActivityPartDailyRecordRespVO queryActivityDailyRecord(ActivityDailyContestReqVO activityDailyContestReqVO, ActivityDailyCompetitionRespVO base) {
        String id = activityDailyContestReqVO.getId();
        log.info("queryActivityDailyRecord传入参数:{}", activityDailyContestReqVO);
        LocalDateTime nowTime = TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone());
        //没有传时间则默认查当天的,当天的是计算出的，直接计算
        if (activityDailyContestReqVO.getDayTimeStamp() == null) {
            log.info("传入参数:{},nowTime:{}", activityDailyContestReqVO, nowTime);
            List<ActivityPartUserRankingDailyRespVO> list = getToDayRoleDailyRecord(id, base);
            String time = TimeZoneUtils.formatLocalDateTime(nowTime, DateUtils.FULL_FORMAT_5);
            return ActivityPartDailyRecordRespVO.builder().list(list).time(time).build();
        }

       /* String regex = "^\\d{4}-\\d{2}-\\d{2}$";
        if (!activityDailyContestReqVO.getDay().matches(regex)) {
            log.info("请求参数时间格式不正确:{}", activityDailyContestReqVO);
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }*/


        //获取出指定时区的当天
        String timeDay = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone());
        String reqTimeDay = TimeZoneUtils.getDayStringInTimeZone(activityDailyContestReqVO.getDayTimeStamp(), CurrReqUtils.getTimezone());
        log.info("传入参数:{},当天:{},nowTime:{},reqTimeDay:{}", activityDailyContestReqVO, timeDay, nowTime, reqTimeDay);
        //判断传入的时间是不是当天,如果是当天则重新调用该方法,不传时间参数,会默认查当天的记录
        if (timeDay.equals(reqTimeDay)) {
            log.info("传入的时间{}是当天{},查当天的记录", activityDailyContestReqVO, timeDay);
            return queryActivityDailyRecord(ActivityDailyContestReqVO.builder().id(activityDailyContestReqVO.getId()).build(), base);
        }


        //将传入的字符串格式的数据 转成 时间戳
        //long dayTime = TimeZoneUtils.convertToTimestamp(activityDailyContestReqVO.getDay() + " 00:00:00", CurrReqUtils.getTimezone(), TimeZoneUtils.patten_yyyyMMddHHmmss);
        long dayTime = activityDailyContestReqVO.getDayTimeStamp();
        //在将时间戳转成当天的开始时间戳
        Long day = TimeZoneUtils.getStartOfDayInTimeZone(dayTime, CurrReqUtils.getTimezone());

        List<SiteActivityDailyRecordPO> list = siteActivityDailyRecordService.getBaseMapper().selectList(Wrappers.lambdaQuery(SiteActivityDailyRecordPO.class)
                .eq(SiteActivityDailyRecordPO::getDailyId, activityDailyContestReqVO.getId())
                .eq(SiteActivityDailyRecordPO::getDay, day));
        List<ActivityPartUserRankingDailyRespVO> resultList = list.stream().map(x -> {
            ActivityPartUserRankingDailyRespVO vo = ActivityPartUserRankingDailyRespVO.builder().build();
            BeanUtils.copyProperties(x, vo);
            vo.setSpecialShow(x.getUserId().equals(CurrReqUtils.getOneId()) && x.getRole().equals(ActivityDailyEnum.REAL_USER.getCode()));
            vo.setBetCurrencyCode(x.getCurrency());
            vo.setBetCurrencySymbol(CurrencyEnum.symbolByCode(x.getCurrency()));
            vo.setAwardCurrencyCode(x.getAwardCurrency());
            if (CommonConstant.PLAT_CURRENCY_CODE.equals(x.getAwardCurrency())) {
                vo.setAwardCurrencySymbol(Character.toString(CommonConstant.PLAT_FORM_SYMBOL));
            } else {
                vo.setAwardCurrencySymbol(CurrencyEnum.symbolByCode(x.getAwardCurrency()));
            }

            return vo;
        }).toList();

        //从小到大排序
        resultList = resultList.stream()
                .filter(o -> o.getAwardAmount().compareTo(BigDecimal.ZERO) > 0) //奖励为0不显示
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getRanking))
                .collect(Collectors.toList());

        LocalDateTime resultStartTime = TimeZoneUtils.timeByTimeZone(dayTime, CurrReqUtils.getTimezone());
        String time = TimeZoneUtils.formatLocalDateTime(resultStartTime, DateUtils.FULL_FORMAT_5);
        return ActivityPartDailyRecordRespVO.builder().list(resultList).time(time).build();
    }


    /**
     * 获取出今天的排行榜信息
     */
    public List<ActivityPartUserRankingDailyRespVO> getToDayRoleDailyRecord(String id, ActivityDailyCompetitionRespVO base) {

        ActivityDailyCompetitionDetailRespVO detailRespVO = getActivityDailyCompetitionById(id, base);
        if (detailRespVO == null) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }


        String top100Key = String.format(RedisConstants.ACTIVITY_DAILY_TOP_100, CurrReqUtils.getSiteCode(),
                TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), CurrReqUtils.getTimezone()), base.getId(), id);
        log.info("按照top100Key:{}查询结果", top100Key);
        List<ActivityRankingDailyVO> list = RedisUtil.getList(top100Key);

        if (CollectionUtil.isEmpty(list)) {
            log.info("按照top100Key:{}查询结果为空", top100Key);
            //查询出当天排行前100用户
            List<ReportUserVenueBetsTopVO> userVenueBetsTop = queryReportUserVenueBetsTopVO(detailRespVO.getVenueCodeList());
            //获取出奖励的配置 底池 + 场馆池
            BigDecimal totalAward = getTotalRewardsAmount(detailRespVO, CurrReqUtils.getSiteCode(), System.currentTimeMillis(), CurrReqUtils.getTimezone());
            list = updateRankings(detailRespVO, userVenueBetsTop, CurrReqUtils.getSiteCode(), totalAward);

            if (CollectionUtil.isNotEmpty(list)) {
                RedisUtil.setList(top100Key, list, 5L, TimeUnit.MINUTES);
            }
        }

        List<ActivityPartUserRankingDailyRespVO> resultList = list.stream().map(x -> {
            ActivityPartUserRankingDailyRespVO respVO = ActivityPartUserRankingDailyRespVO.builder().build();
            BeanUtils.copyProperties(x, respVO);
            respVO.setSpecialShow(!x.getType() && x.getUserId().equals(CurrReqUtils.getOneId()));//当前登陆的用户是否上榜
            respVO.setAwardCurrencyCode(x.getAwardCurrencyCode());
            respVO.setAwardCurrencySymbol(x.getAwardSymbol());
            respVO.setBetCurrencySymbol(x.getBetSymbol());
            respVO.setBetCurrencyCode(x.getCurrencyCode());
            return respVO;
        }).toList();


        //将机器人数据与真实数据去碰撞,获取出最终的排序
        return resultList.stream()
                .filter(o -> o.getAwardAmount().compareTo(BigDecimal.ZERO) > 0)//只显示有奖励的
                .sorted(Comparator.comparing(ActivityPartUserRankingDailyRespVO::getRanking))
                .collect(Collectors.toList());
    }


}
