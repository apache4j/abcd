package com.cloud.baowang.activity.service.redbag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.util.ObjUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.redbag.RedBagSessionStatusEnum;
import com.cloud.baowang.activity.api.vo.ActivityOrderRecordRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRecordTotalVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagWinnerVO;
import com.cloud.baowang.activity.cache.redbag.RedBagRainIdCacheService;
import com.cloud.baowang.activity.cache.redbag.RedBagRainSessionCacheService;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagSessionRepository;
import com.cloud.baowang.activity.service.SiteActivityOrderRecordService;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.SnowFlakeUtils;
import com.cloud.baowang.common.core.utils.SymbolUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.user.api.api.medal.MedalAcquireApi;
import com.cloud.baowang.user.api.api.medal.MedalAcquireRecordApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.cloud.baowang.common.core.constants.RedisConstants.ACTIVITY_REDBAG_SESSION_PRIZE_POOL_REDUCE_LOCK;
import static com.cloud.baowang.common.core.constants.RedisConstants.ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT;

/**
 * @author awei
 * @description 针对表【site_activity_red_bag_session(红包雨活动场次历史表)】的数据库操作Service实现
 * @createDate 2024-09-14 16:58:10
 */
@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityRedBagSessionService extends ServiceImpl<SiteActivityRedBagSessionRepository, SiteActivityRedBagSessionPO> {

    private final SiteActivityOrderRecordService orderRecordService;
    private final RedBagRainIdCacheService redBagRainIdCacheService;
    private final RedBagRainSessionCacheService sessionCacheService;
    private final SiteActivityRedBagRecordService redBagRecordService;
    private final MedalAcquireApi medalAcquireApi;
    private final MedalAcquireRecordApi medalAcquireRecordApi;
    private final SiteActivityBaseService baseService;

    /**
     * 查询进行中场次
     *
     * @param siteCode
     * @return
     */
    public SiteActivityRedBagSessionPO getBySiteCode(String siteCode) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                .one();
    }

    /**
     * 中奖名单
     *
     * @param siteCode
     * @return
     */
    public List<RedBagWinnerVO> getRedBagWinner(String siteCode) {
        SiteActivityRedBagSessionPO sessionPO = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.END.getStatus())
                .orderByDesc(SiteActivityRedBagSessionPO::getEndTime)
                .last(" limit 1")
                .one();
        List<RedBagWinnerVO> bagWinnerVOS = Lists.newArrayList();
        if (ObjUtil.isNotNull(sessionPO)) {
            List<ActivityOrderRecordRespVO> redBagWinner = orderRecordService.getRedBagWinner(siteCode, sessionPO.getSessionId());
            for (ActivityOrderRecordRespVO respVO : redBagWinner) {
                RedBagWinnerVO winnerVO = new RedBagWinnerVO();
                winnerVO.setRedBagAmount(respVO.getActivityAmount());
                winnerVO.setHitTime(respVO.getReceiveTime());
                winnerVO.setUserId(respVO.getUserId());
                winnerVO.setUserAccount(SymbolUtil.desensitize(respVO.getUserAccount()));
                bagWinnerVOS.add(winnerVO);
            }
        }
        return bagWinnerVOS;
    }

    /**
     * 根据站点与开始时间查询
     *
     * @param siteCode
     * @param startTimeList
     * @return
     */
    public List<SiteActivityRedBagSessionPO> getListBySiteCodeAndStartTime(String siteCode, List<Long> startTimeList) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .in(SiteActivityRedBagSessionPO::getStartTime, startTimeList)
                .list();
    }

    public List<SiteActivityRedBagSessionPO> getByStartTime(String siteCode, String startTimeStr, String lastStartTimeStr, String timezone) {
        String[] startTime = startTimeStr.split(CommonConstant.COLON);
        String[] lastStartTime = lastStartTimeStr.split(CommonConstant.COLON);
        // 时间转换
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);
        LocalDateTime sessionStartLocalTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Integer.parseInt(startTime[0]), Integer.parseInt(startTime[1]));
        LocalDateTime sessionEndLocalTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Integer.parseInt(lastStartTime[0]), Integer.parseInt(lastStartTime[1]));
        long sessionStartMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionStartLocalTime, timezone);
        long lastSessionStartMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionEndLocalTime, timezone);
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .ge(SiteActivityRedBagSessionPO::getStartTime, sessionStartMillis)
                .le(SiteActivityRedBagSessionPO::getStartTime, lastSessionStartMillis)
                .list();
    }


    public SiteActivityRedBagSessionPO setSessionProcessStatus(String siteCode, long startTimeMills) {

        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStartTime, startTimeMills)
                .set(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                .update();
        return getBySiteCode(siteCode);
    }

    /**
     * 每次生成两天的场次
     *
     * @param siteCode
     * @param start
     * @param respVO
     * @param timezone
     */
    @DistributedLock(name = RedisConstants.ACTIVITY_REDBAG_RESESSION_SITE, unique = "#siteCode", waitTime = 3, leaseTime = 30)
    @Transactional(rollbackFor = Exception.class)
    public void regenerateSession(String baseId, String siteCode, LocalDateTime start, LocalDateTime end, RedBagRainRespVO respVO, String timezone) {
        log.info("红包雨重新生成场次开始,siteCode:{}", siteCode);
        Map<String, String> sessionTimeMap = respVO.getSessionTime();
        List<SiteActivityRedBagSessionPO> sessionPOList = Lists.newArrayList();
        LocalDateTime now = LocalDateTime.now();
        if (start.isBefore(now) || start.equals(now)) {
            start = now;
        }
        // 每次生成两天的
        LocalDateTime nextDay = start.plusDays(1);
        for (LocalDateTime dateTime : List.of(start, nextDay)) {
            LocalDateTime finalStart = start;
            sessionTimeMap.forEach(
                    (startTimeStr, endTimeStr) -> {
                        String[] startTimeSpl = startTimeStr.split(CommonConstant.COLON);
                        String[] endTimeSpl = endTimeStr.split(CommonConstant.COLON);
                        LocalDateTime sessionStartLocalTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), Integer.parseInt(startTimeSpl[0]), Integer.parseInt(startTimeSpl[1]));
                        LocalDateTime sessionEndLocalTime = LocalDateTime.of(dateTime.getYear(), dateTime.getMonth(), dateTime.getDayOfMonth(), Integer.parseInt(endTimeSpl[0]), Integer.parseInt(endTimeSpl[1]));
                        // 指定生成时间节点早于生成场次开始时间就不生成
                        LocalDateTime minusSeconds = sessionStartLocalTime.minusSeconds(respVO.getAdvanceTime() + 1);
                        if (finalStart.isAfter(minusSeconds) || minusSeconds.equals(finalStart)) {
                            return;
                        }
                        if (ObjUtil.isNotNull(end)) {
                            if (finalStart.equals(end)) {
                                return;
                            }
                            if (end.isBefore(sessionEndLocalTime)) {
                                // 自动补0
                                endTimeStr = String.format("%02d:%02d", end.getHour(), end.getMinute());
                                sessionEndLocalTime = end;
                            }
                        }

                        long sessionStartMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionStartLocalTime, timezone);
                        long sessionEndMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionEndLocalTime, timezone);
                        SiteActivityRedBagSessionPO sessionPO = new SiteActivityRedBagSessionPO();
                        BeanUtils.copyProperties(respVO, sessionPO);
                        sessionPO.setSessionId(SnowFlakeUtils.genRedBagSessionId());
                        sessionPO.setBaseId(baseId);
                        sessionPO.setSiteCode(siteCode);
                        sessionPO.setDay(TimeZoneUtils.formatLocalDateTime(sessionStartLocalTime, TimeZoneUtils.patten_yyyyMMdd));
                        sessionPO.setStartTimeStr(startTimeStr);
                        sessionPO.setEndTimeStr(endTimeStr);
                        sessionPO.setStartTime(sessionStartMillis);
                        sessionPO.setEndTime(sessionEndMillis);
                        sessionPO.setStatus(RedBagSessionStatusEnum.NOT_START.getStatus());
                        sessionPO.setRankLimitConfig(JSON.toJSONString(respVO.getConfigList()));
                        sessionPO.setSettled(CommonConstant.business_zero);
                        sessionPO.setLatest(CommonConstant.business_zero);
                        //保证id不重复
                        sessionPO.setId(SnowFlakeUtils.getSnowIdBySelfCenterId(siteCode));
                        sessionPOList.add(sessionPO);
                    }
            );
        }
        List<SiteActivityRedBagSessionPO> list = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.NOT_START.getStatus())
                .list();
        if (CollUtil.isNotEmpty(list)) {
            List<String> sessionIds = list.stream().map(SiteActivityRedBagSessionPO::getId).toList();
            removeBatchByIds(sessionIds);
            // 缓存更新
            sessionCacheService.batchEvict(sessionIds);
        }
        // 当天如果包含最后一场次需要变更状态
        new LambdaUpdateChainWrapper<>(baseMapper)
                .ne(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.NOT_START.getStatus())
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getLatest, CommonConstant.business_one)
                .set(SiteActivityRedBagSessionPO::getLatest, CommonConstant.business_zero)
                .update();
        if (CollUtil.isNotEmpty(sessionPOList)) {
            // 最后一场次需要变更状态
            SiteActivityRedBagSessionPO sessionPO = sessionPOList.get(sessionPOList.size() - 1);
            sessionPO.setLatest(CommonConstant.business_one);
            saveBatch(sessionPOList);
            log.info("红包雨重新生成场次,最后一场变更状态:{}",sessionPO);
            sessionCacheService.batchReload(sessionPOList.stream().map(SiteActivityRedBagSessionPO::getSessionId).toList());
        }
        // 两天缓存session id 缓存刷新
        redBagRainIdCacheService.reload(siteCode + CommonConstant.COLON + TimeZoneUtils.formatLocalDateTime(start, TimeZoneUtils.patten_yyyyMMdd));
        redBagRainIdCacheService.reload(siteCode + CommonConstant.COLON + TimeZoneUtils.formatLocalDateTime(nextDay, TimeZoneUtils.patten_yyyyMMdd));
        log.info("红包雨重新生成场次结束,siteCode:{}", siteCode);
    }


    public SiteActivityRedBagSessionPO getBySiteAndSessionId(String siteCode, String sessionId) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getSessionId, sessionId)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                .one();
    }

    /**
     * 根据日期和站点查询站点红包雨场次
     *
     * @param siteCode
     * @param day
     * @return
     */
    public List<SiteActivityRedBagSessionPO> getBySiteCodeByDay(String siteCode, String day) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getDay, day)
                .orderByAsc(SiteActivityRedBagSessionPO::getStartTime)
                .list();
    }

    @Transactional(rollbackFor = Exception.class)
    public void delDayNotStart(String siteCode) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.NOT_START.getStatus())
                .remove();
    }

    public SiteActivityRedBagSessionPO getBySiteCodeAndDayTime(String siteCode, String day, String timeStr) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getDay, day)
                .eq(SiteActivityRedBagSessionPO::getEndTimeStr, timeStr)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                .one();
    }

    public void setSessionEndStatus(SiteActivityRedBagSessionPO sessionPO) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .set(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.END.getStatus())
                .eq(SiteActivityRedBagSessionPO::getId, sessionPO.getId())
                .update();
    }

    /**
     * 奖池扣减
     *
     * @param amount
     * @param siteCode
     * @param sessionId
     * @return Pair<奖池剩余金额, 实际扣减金额>
     */
    @DistributedLock(name = ACTIVITY_REDBAG_SESSION_PRIZE_POOL_REDUCE_LOCK, unique = "#siteCode +':'+ #sessionId", waitTime = 2, leaseTime = 5)
    public Pair<BigDecimal, BigDecimal> prizePoolReduce(BigDecimal amount, String siteCode, String sessionId) {
        BigDecimal prizePoolAmount = RedisUtil.getValue(String.format(ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT, siteCode, sessionId));
        if (ObjUtil.isNull(prizePoolAmount)) {
            log.info("奖金池为空:{},{}",siteCode,sessionId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        BigDecimal remaining = BigDecimal.ZERO;
        BigDecimal actualAmount;
        if (prizePoolAmount.compareTo(amount) > 0) {
            remaining = prizePoolAmount.subtract(amount);
            actualAmount = amount;
        } else {
            actualAmount = prizePoolAmount;
        }
        log.info("奖金池:{},{},剩余:{},实际扣减金额:{}",siteCode,sessionId,remaining,actualAmount);
        RedisUtil.setValue(String.format(ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT, siteCode, sessionId), remaining, CommonConstant.HOUR25_SECONDS);
        return new Pair<>(remaining, actualAmount);
    }

    /**
     * 红包雨单个会员金额结算
     *
     * @param siteCode  站点code
     * @param sessionId 场次id
     * @param userId    会员id
     * @return RedBagRecordTotalVO
     */
    @DistributedLock(name = RedisConstants.ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER, unique = "#siteCode + ':' + #sessionId + ':' + #userId", fair = true, waitTime = 2, leaseTime = 10)
    @Transactional(rollbackFor = Exception.class)
    public RedBagRecordTotalVO sessionUserSettlement(String siteCode, String sessionId, String userAccount, String userId) {
        log.info("红包雨单个会员金额结算,siteCode:{},sessionId:{},uid:{}", siteCode, sessionId, userId);
        // 已结算 重复结算
        SiteActivityRedBagSessionPO sessionPO = sessionCacheService.getOrLoad(sessionId);
        if (ObjUtil.isNull(sessionPO) || sessionPO.getSettled().equals(CommonConstant.business_one)) {
            log.info("红包雨单个会员金额已结算,直接返回结果:{}",userId);
            return redBagRecordService.selectTotalByUserSessionIdSettled(siteCode, sessionId, userId);
        }
        // 结算标记
        String settlementTag = String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER_TAG, siteCode, sessionId, userId);
        RedisUtil.setValue(settlementTag, CommonConstant.business_one, CommonConstant.HOUR25_SECONDS);
        // 查询未结算
        RedBagRecordTotalVO recordTotalVO = redBagRecordService.selectTotalByUserSessionIdUnsettle(siteCode, sessionId, userId);
        if (recordTotalVO.getCount() <= CommonConstant.business_zero) {
            log.info("红包雨单个会员:{},金额未结算:{}",userId,recordTotalVO);
            return recordTotalVO;
        }
        SiteActivityBasePO basePO = baseService.getById(sessionPO.getBaseId());
        log.info("红包雨单个会员金额更新发放状态:{}",userId);
        // 变更记录
        redBagRecordService.receiveSessionAward(siteCode, sessionId, userId);
        // 领取记录
        List<ActivitySendMqVO> activitySendMqVOList = Lists.newArrayList();
        ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
        activitySendMqVO.setOrderNo(OrderNoUtils.genOrderNo(userId, sessionId));
        activitySendMqVO.setSiteCode(siteCode);
        activitySendMqVO.setActivityTemplate(ActivityTemplateEnum.RED_BAG_RAIN.getType());
        activitySendMqVO.setActivityId(recordTotalVO.getBaseId());
        activitySendMqVO.setUserId(userId);
        activitySendMqVO.setDistributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode());
        activitySendMqVO.setActivityAmount(recordTotalVO.getTotalAmount());
        activitySendMqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        activitySendMqVO.setRedbagSessionId(sessionId);
        activitySendMqVO.setRunningWaterMultiple(basePO.getWashRatio());
        activitySendMqVO.setRunningWater(basePO.getWashRatio().multiply(recordTotalVO.getTotalAmount()).setScale(CommonConstant.business_four, RoundingMode.DOWN));
        activitySendMqVOList.add(activitySendMqVO);
        ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
        activitySendListMqVO.setList(activitySendMqVOList);
        //发送通知消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        // 会员红包领取n个 勋章发送
        redbagMedalSend(siteCode, userAccount, userId);
        return recordTotalVO;
    }

    private void redbagMedalSend(String siteCode, String userAcount, String userId) {
        try {
            ResponseVO<Long> medalSent = medalAcquireRecordApi.countByCond(MedalAcquireRecordCondReqVO.builder().medalCodeEnum(MedalCodeEnum.MEDAL_1005).siteCode(siteCode).userId(userId).build());
            if (medalSent.isOk()) {
                Long data = medalSent.getData();
                if (data > 0) {
                    // 包含勋章 直接 return
                    return;
                }
            }
            ResponseVO<SiteMedalInfoRespVO> medalInfo = medalAcquireApi.findByMedalCode(new MedalAcquireCondReqVO().setSiteCode(siteCode).setMedalCodeEnum(MedalCodeEnum.MEDAL_1005));
            if (medalInfo.isOk()) {
                String condNum1 = medalInfo.getData().getCondNum1();
                int count = Integer.parseInt(condNum1);
                // 次数校验
                Long redBagCount = redBagRecordService.selectRedBagCount(siteCode, userId);
                if (redBagCount >= count) {
                    // 发送红包雨勋章topic
                    MedalAcquireBatchReqVO build = MedalAcquireBatchReqVO.builder()
                            .siteCode(siteCode)
                            .medalAcquireReqVOList(
                                    List.of(MedalAcquireReqVO.builder().medalCode(MedalCodeEnum.MEDAL_1005.getCode())
                                            .siteCode(siteCode)
                                            .userId(userId)
                                            .userAccount(userAcount)
                                            .build()))
                            .build();
                    KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, build);
                }
            }
        } catch (Exception e) {
            log.error("发送红包雨勋章失败siteCode:{},userId:{},error:", siteCode, userId, e);
        }
    }

    public void settledSession(String sessionId) {
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSessionId, sessionId)
                .set(SiteActivityRedBagSessionPO::getSettled, CommonConstant.business_one)
                .set(SiteActivityRedBagSessionPO::getSettleTime, System.currentTimeMillis())
                .update();
    }

    /**
     * 未开始的场次里面 最近的一个
     * @param siteCode
     * @return
     */
    public SiteActivityRedBagSessionPO getLatestBySiteCode(String siteCode) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .select(SiteActivityRedBagSessionPO::getSessionId, SiteActivityRedBagSessionPO::getStartTime)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.NOT_START.getStatus())
                .orderByAsc(SiteActivityRedBagSessionPO::getStartTime)
                .last(" limit 1")
                .one();
    }

    public List<SiteActivityRedBagSessionPO> selectProcessList(String siteCode) {
        return new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagSessionPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagSessionPO::getStatus, RedBagSessionStatusEnum.PROGRESS.getStatus())
                .list();

    }
}




