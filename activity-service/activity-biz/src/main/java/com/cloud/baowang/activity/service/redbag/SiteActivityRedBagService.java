package com.cloud.baowang.activity.service.redbag;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.constants.ActivityConstant;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.enums.redbag.RedBagSessionStatusEnum;
import com.cloud.baowang.activity.api.enums.redbag.RedBagTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityBaseRespVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagParticipateReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainClientInfoVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainConfigVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRankConfigVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRealTimeInfo;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRecordTotalVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSendRespVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSessionInfoVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementReqVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagSettlementVO;
import com.cloud.baowang.activity.api.vo.redbag.RedBagWinnerVO;
import com.cloud.baowang.activity.cache.redbag.RedBagRainIdCacheService;
import com.cloud.baowang.activity.cache.redbag.RedBagRainSessionCacheService;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityRedBagConfigPO;
import com.cloud.baowang.activity.po.SiteActivityRedBagPO;
import com.cloud.baowang.activity.po.SiteActivityRedBagRankConfigPO;
import com.cloud.baowang.activity.po.SiteActivityRedBagRecordPO;
import com.cloud.baowang.activity.po.SiteActivityRedBagSessionPO;
import com.cloud.baowang.activity.repositories.SiteActivityRedBagRepository;
import com.cloud.baowang.activity.service.ActivityJobComponent;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.common.core.constants.CacheConstants;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.ProbabilityGenerator;
import com.cloud.baowang.common.core.utils.ProbabilityRangeGenerator;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserWinLossReqVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserWinLossReqVO;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserRechargeResponseVO;
import com.cloud.baowang.report.api.vo.ReportUserRechargeUserRequestVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.websocket.api.constants.WsMessageConstant;
import com.cloud.baowang.websocket.api.enums.WSSubscribeEnum;
import com.cloud.baowang.websocket.api.vo.WSBaseResp;
import com.cloud.baowang.websocket.api.vo.WsMessageMqVO;
import com.cloud.baowang.websocket.api.vo.activity.redbag.ActivityRedBagRainEndDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.cloud.baowang.common.core.constants.RedisConstants.ACTIVITY_REDBAG_SESSION_OPERATE_SITE;
import static com.cloud.baowang.common.core.constants.RedisConstants.ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT;

/**
 * @author awei
 * @description 针对表【site_activity_red_bag(红包雨活动配置)】的数据库操作Service实现
 * @createDate 2024-09-12 18:56:06
 */
@Slf4j
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Service
@AllArgsConstructor
public class SiteActivityRedBagService extends ServiceImpl<SiteActivityRedBagRepository, SiteActivityRedBagPO> {
    private final SiteActivityRedBagRankConfigService rankConfigService;
    private final SiteActivityRedBagConfigService configService;
    private final SiteActivityRedBagSessionService sessionService;
    private final ActivityJobComponent jobComponent;
    private final SiteApi siteApi;
    private final SiteActivityBaseService baseService;
    private final UserInfoApi userInfoApi;
    private final SiteActivityRedBagRecordService redBagRecordService;
    private final RedBagRainSessionCacheService sessionCacheService;
    private final RedBagRainIdCacheService redBagRainIdCacheService;
    private final ReportUserRechargeApi reportUserRechargeApi;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final ReportUserVenueFixedWinLoseApi userVenueFixedWinLoseApi;
    private final VipRankApi vipRankApi;
    private final SiteActivityRedBagService _this;
    private final ActivityParticipateApi participateApi;


    @Transactional(rollbackFor = Exception.class)
    public void saveActivity(RedBagRainVO activity) {
        String baseId = activity.getBaseId();
        // 参数校验
        reaBagParamCheck(activity);
        String siteCode = CurrReqUtils.getSiteCode();
        // 保存
        SiteActivityRedBagPO redBagPO = new SiteActivityRedBagPO();
        BeanUtils.copyProperties(activity, redBagPO);
        redBagPO.setSessionStartTime(String.join(CommonConstant.COMMA, activity.getSessionTime().keySet()));
        redBagPO.setSessionEndTime(String.join(CommonConstant.COMMA, activity.getSessionTime().values()));
        redBagPO.setSiteCode(siteCode);
        redBagPO.setRankLimit(StrUtil.join(CommonConstant.COMMA, activity.getRankLimit()));
        save(redBagPO);
        List<SiteActivityRedBagConfigPO> configPOList = Lists.newArrayList();
        List<SiteActivityRedBagRankConfigPO> rankConfigPOList = Lists.newArrayList();
        activity.getConfigList().forEach(config -> {
            SiteActivityRedBagConfigPO configPO = new SiteActivityRedBagConfigPO();
            BeanUtils.copyProperties(config, configPO);
            configPO.setBaseId(baseId);
            configPO.setSiteCode(siteCode);
            configPOList.add(configPO);
            List<RedBagRainRankConfigVO> rankConfigList = config.getRankConfigList();
            rankConfigList.forEach(rankConfig -> {
                SiteActivityRedBagRankConfigPO rankConfigPO = new SiteActivityRedBagRankConfigPO();
                BeanUtils.copyProperties(rankConfig, rankConfigPO);
                rankConfigPO.setBaseId(baseId);
                rankConfigPO.setSiteCode(siteCode);
                rankConfigPO.setVipRankCode(config.getVipRankCode());
                rankConfigPOList.add(rankConfigPO);
            });
        });
        configService.saveBatch(configPOList);
        rankConfigService.saveBatch(rankConfigPOList);
    }

    /**
     * 红包雨参数校验
     *
     * @param activity
     */
    public void reaBagParamCheck(RedBagRainVO activity) {
        Map<String, String> sessionTimeMap = activity.getSessionTime();
        if (MapUtil.isEmpty(sessionTimeMap)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        AtomicReference<LocalTime> lastEndTime = new AtomicReference<>();
        AtomicReference<LocalTime> firstStartTime = new AtomicReference<>();
        AtomicReference<LocalTime> lasestEndTime = new AtomicReference<>();
        sessionTimeMap.forEach((start, end) -> {
            String[] startSplit = start.split(CommonConstant.COLON);
            String[] endSplit = end.split(CommonConstant.COLON);
            int startHour = Integer.parseInt(startSplit[0]);
            int startSecond = Integer.parseInt(startSplit[1]);
            int endHour = Integer.parseInt(endSplit[0]);
            int endSecond = Integer.parseInt(endSplit[1]);
            LocalTime startTime = LocalTime.of(startHour, startSecond);
            LocalTime endTime = LocalTime.of(endHour, endSecond);
            if (ObjUtil.isNull(firstStartTime.get())) {
                firstStartTime.set(startTime);
            }
            lasestEndTime.set(endTime);
            // 30分钟限制
            LocalTime lastEndTimeRef = lastEndTime.get();
            // 开始时间不能大于上次结束时间 + 30分钟
            if (ObjUtil.isNotNull(lastEndTimeRef)) {
                LocalTime lastEndTimeLimitRef = lastEndTimeRef.plusMinutes(30);
                if (startTime.isBefore(lastEndTimeLimitRef)) {
                    throw new BaowangDefaultException(ResultCode.RED_BAG_SESSION_TIME_AFTER_ERROR);
                }
            }
            lastEndTime.set(endTime);

            // 不得早于等于开始时间
            if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_SESSION_TIME_ERROR);
            }
        });
        // 第一场与最后一场也需间隔超过30分钟
        LocalTime firstTime = firstStartTime.get();
        LocalTime lasestTime = lasestEndTime.get();
        LocalDate now = LocalDate.now();
        LocalDateTime firstLocalDate = now.atTime(firstTime);
        LocalDateTime lasestLocalDate = now.atTime(lasestTime);
        LocalDateTime localDateTime = firstLocalDate.plusDays(1);
        if (localDateTime.isBefore(lasestLocalDate.plusMinutes(30))) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_SESSION_TIME_AFTER_ERROR);
        }
        // 提前时间不得早于600s
        if (ObjUtil.isNull(activity.getAdvanceTime()) || activity.getAdvanceTime() > 600) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_ADVANCE_TIME_ERROR);
        }
        // 红包最长掉落时间
        if (ObjUtil.isNull(activity.getDropTime()) || activity.getDropTime() > 30) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_DROP_TIME_ERROR);
        }
        // 段位限制不能为空
        if (CollUtil.isEmpty(activity.getRankLimit())) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_RANK_CONFIG_ERROR);
        }
        // 流水限制 && 存款金额限制
        if ((ObjUtil.isNull(activity.getBetAmount()) || activity.getBetAmount().compareTo(BigDecimal.ZERO) < 0) && (ObjUtil.isNull(activity.getDepositAmount()) || activity.getDepositAmount().compareTo(BigDecimal.ZERO) < 0)) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_BET_DEPOSIT_LIMIT_ERROR);
        }
        // 红包总金额限制
        if (ObjUtil.isNull(activity.getTotalAmount()) || activity.getTotalAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_TOTAL_AMOUNT_ERROR);
        }
        List<RedBagRainConfigVO> configList = activity.getConfigList();

        // 红包中奖设置不能为空
        if (CollUtil.isEmpty(configList)) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_HIT_CONFIG_ERROR);
        }
        if (configList.size() != activity.getRankLimit().size()) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_HIT_CONFIG_ERROR);
        }
        List<RedBagRainConfigVO> list = configList.stream().sorted(Comparator.comparing(RedBagRainConfigVO::getSort)).toList();
        list.forEach(config -> {
            Integer redBagMaximum = config.getRedBagMaximum();
            if (ObjUtil.isNull(redBagMaximum) || redBagMaximum <= 0) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_MAXIMUM_ERROR);
            }
            Integer amountType = config.getAmountType();
            RedBagTypeEnum redBagTypeEnum = RedBagTypeEnum.of(amountType);
            if (ObjUtil.isNull(redBagTypeEnum)) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_TYPE_ERROR);
            }

            List<RedBagRainRankConfigVO> rankConfigList = config.getRankConfigList();
            List<RedBagRainRankConfigVO> sortRankConfigList = rankConfigList.stream().sorted(Comparator.comparing(RedBagRainRankConfigVO::getSort)).toList();
            AtomicReference<BigDecimal> totalHitRate = new AtomicReference<>(BigDecimal.ZERO);
            sortRankConfigList.forEach(rankConfig -> {
                if (ObjUtil.isNull(rankConfig.getHitRate()) || rankConfig.getHitRate().compareTo(BigDecimal.ZERO) < 0) {
                    throw new BaowangDefaultException(ResultCode.RED_BAG_RANK_CONFIG_ERROR);
                }
                totalHitRate.set(totalHitRate.get().add(rankConfig.getHitRate()));
            });
            if (totalHitRate.get().compareTo(CommonConstant.DECIMAL_100) != 0) {
                throw new BaowangDefaultException(ResultCode.RED_BAG_RANK_HATE_ERROR);
            }
            switch (redBagTypeEnum) {
                case FIXED -> {
                    sortRankConfigList.forEach(rankConfig -> {
                        BigDecimal fixedAmount = rankConfig.getFixedAmount();
                        if (ObjUtil.isNull(fixedAmount) || rankConfig.getFixedAmount().compareTo(fixedAmount) < 0) {
                            throw new BaowangDefaultException(ResultCode.RED_BAG_AMOUNT_OVER_ZERO_ERROR);
                        }
                    });
                }
                case RANDOM -> {
                    AtomicReference<BigDecimal> lastRandomEndAmount = new AtomicReference<>();
                    sortRankConfigList.forEach(rankConfig -> {
                        BigDecimal lastEndAmount = lastRandomEndAmount.get();
                        if (ObjUtil.isNull(rankConfig.getRandomStartAmount())
                                || ObjUtil.isNull(rankConfig.getRandomEndAmount())
                                || (ObjUtil.isNotNull(lastEndAmount) && rankConfig.getRandomStartAmount().compareTo(lastEndAmount) < 0)
                                || rankConfig.getRandomStartAmount().compareTo(rankConfig.getRandomEndAmount()) > 0) {
                            throw new BaowangDefaultException(ResultCode.RED_BAG_RANDOM_AMOUNT_ERROR);
                        }
                        lastRandomEndAmount.set(rankConfig.getRandomEndAmount());
                    });
                }
            }
            config.setRankConfigList(sortRankConfigList);
        });
        activity.setConfigList(list);
    }

    public ResponseVO<RedBagRealTimeInfo> realTimeInfo(String siteCode) {
        List<String> processSessionId = redBagRainIdCacheService.get(siteCode + CommonConstant.COLON + ActivityConstant.ACTIVITY_REDBAG_REAL_TIME_SESSION_KEY);
        String sessionId = null;
        if (CollUtil.isNotEmpty(processSessionId)) {
            sessionId = processSessionId.get(CommonConstant.business_zero);
        }
        if (StrUtil.isBlank(sessionId)) {
            return ResponseVO.fail(ResultCode.RED_BAG_NO_OPEN_ERROR);
        }
        SiteActivityRedBagSessionPO sessionPO = sessionCacheService.getOrLoad(sessionId);
        if (ObjUtil.isNull(sessionPO)) {
            return ResponseVO.fail(ResultCode.SYSTEM_ERROR);
        }
        ActivityBaseRespVO respVO = baseService.queryActivityById(sessionPO.getBaseId(), sessionPO.getSiteCode());
        long nowTime = System.currentTimeMillis();
        Long showStartTime = respVO.getShowStartTime();
        Long showEndTime = respVO.getShowEndTime();
        if (showStartTime != null && nowTime < showStartTime) {
            log.info("红包雨活动:{},展示尚未开始",sessionPO.getBaseId());
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        if (showEndTime != null && nowTime > showEndTime) {
            log.info("红包雨活动:{},已结束展示",sessionPO.getBaseId());
            return ResponseVO.fail(ResultCode.ACTIVITY_HAS_END);
        }
        Long startTime = sessionPO.getStartTime();
        Long endTime = sessionPO.getEndTime();
        Integer dropTime = sessionPO.getDropTime();
        Integer advanceTime = sessionPO.getAdvanceTime();
        RedBagRealTimeInfo realTimeInfo = new RedBagRealTimeInfo();
        realTimeInfo.setRedbagSessionId(sessionPO.getSessionId());
        realTimeInfo.setStartTime(startTime);
        realTimeInfo.setEndTime(endTime);
        realTimeInfo.setDropTime(dropTime);
        long currentTimeMillis = System.currentTimeMillis();
        // 开始时间大于当前时间 未开始
        if (startTime > currentTimeMillis) {
            int seconds = Math.toIntExact((startTime - currentTimeMillis) / 1000) + 1;
            if (seconds < advanceTime) {
                advanceTime = seconds;
            }
        } else {
            // 开始时间小于等于当前时间 已开始
            advanceTime = CommonConstant.business_zero;
        }
        realTimeInfo.setAdvanceTime(advanceTime);
        return ResponseVO.success(realTimeInfo);
    }

    public RedBagRainClientInfoVO clientInfo(String siteCode,String timezone) {
        // 规则信息
        SiteActivityBasePO siteActivityBasePO = baseService.selectBySiteAndTem(siteCode, ActivityTemplateEnum.RED_BAG_RAIN.getType());
        if (ObjUtil.isNull(siteActivityBasePO)) {
            throw new BaowangDefaultException(ResultCode.RED_BAG_NO_OPEN_ERROR);
        }
        RedBagRainClientInfoVO clientInfoVO = new RedBagRainClientInfoVO();
        clientInfoVO.setRuleDesc(siteActivityBasePO.getActivityRuleI18nCode());
        clientInfoVO.setActivityNameI18nCode(siteActivityBasePO.getActivityNameI18nCode());
        clientInfoVO.setHeadPictureI18nCode(siteActivityBasePO.getHeadPictureI18nCode());
        clientInfoVO.setHeadPicturePcI18nCode(siteActivityBasePO.getHeadPicturePcI18nCode());
        // 站点时区
       // String timezone = CurrReqUtils.getTimezone();
        long currentMillis = System.currentTimeMillis();
        long timeMillis = currentMillis, currentTimeMillis = currentMillis;
        // 没有场次时未到开始时间 显示最近的场次
        if (timeMillis <= siteActivityBasePO.getActivityStartTime()) {
            SiteActivityRedBagSessionPO sessionPO = sessionService.getLatestBySiteCode(siteCode);
            if (ObjUtil.isNotNull(sessionPO)) {
                timeMillis = sessionPO.getStartTime();
            }
        }
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(timeMillis, timezone);
        // 当天全场次
        List<String> sessionIds = redBagRainIdCacheService.getOrLoad(siteCode + CommonConstant.COLON + TimeZoneUtils.formatLocalDateTime(now, TimeZoneUtils.patten_yyyyMMdd));
        Collection<SiteActivityRedBagSessionPO> sessionPOList = Lists.newArrayList();
        if (CollUtil.isNotEmpty(sessionIds)) {
            Map<String, SiteActivityRedBagSessionPO> sessionPOMap = sessionCacheService.batchGetOrLoad(sessionIds);
            sessionPOList = sessionPOMap.values().stream().sorted(Comparator.comparing(SiteActivityRedBagSessionPO::getStartTime)).toList();
        }
        List<RedBagSessionInfoVO> infoVOList = Lists.newArrayList();
        long advanceTime = 0L;
        boolean setAdvanceTime = false; // 倒计时设置
        boolean setType = false; // 客户端显示状态
        for (SiteActivityRedBagSessionPO sessionPO : sessionPOList) {
            Long sessionStartMillis = sessionPO.getStartTime();
            Long sessionEndMillis = sessionPO.getEndTime();
            RedBagSessionInfoVO infoVO = new RedBagSessionInfoVO();
            infoVO.setStartTime(sessionStartMillis);
            infoVO.setEndTime(sessionEndMillis);
            infoVO.setRedbagSessionId(sessionPO.getSessionId());
            infoVOList.add(infoVO);
            // 进行中
            if (currentTimeMillis < sessionEndMillis && currentTimeMillis >= sessionStartMillis) {
                infoVO.setStatus(RedBagSessionStatusEnum.PROGRESS.getStatus());
                clientInfoVO.setClientStatus(RedBagSessionStatusEnum.PROGRESS.getStatus());
                clientInfoVO.setRedbagSessionId(sessionPO.getSessionId());
                clientInfoVO.setDropTime(sessionPO.getDropTime());
                setType = true;
                // 取剩余结束时间
                advanceTime = Math.toIntExact((sessionEndMillis - currentTimeMillis) / 1000) + 1;
                setAdvanceTime = true;
                //未开始
            } else if (currentTimeMillis < sessionStartMillis) {
                infoVO.setStatus(RedBagSessionStatusEnum.NOT_START.getStatus());
                if (!setType) {
                    clientInfoVO.setClientStatus(RedBagSessionStatusEnum.NOT_START.getStatus());
                }
                if (!setAdvanceTime) {
                    // 取当场次时间
                    advanceTime = Math.toIntExact((sessionStartMillis - currentTimeMillis) / 1000) + 1;
                    clientInfoVO.setRedbagSessionId(sessionPO.getSessionId());
                    clientInfoVO.setDropTime(sessionPO.getDropTime());
                    setAdvanceTime = true;
                }
                // 已结束
            } else {
                infoVO.setStatus(RedBagSessionStatusEnum.END.getStatus());
                if (!setType) {
                    clientInfoVO.setClientStatus(RedBagSessionStatusEnum.END.getStatus());
                }
                if (!setAdvanceTime) {
                    // 取当场次时间
                    advanceTime = CommonConstant.business_negative1;
                    clientInfoVO.setRedbagSessionId(sessionPO.getSessionId());
                    clientInfoVO.setDropTime(sessionPO.getDropTime());
                }
            }
        }
        clientInfoVO.setSessionInfoList(infoVOList);
        clientInfoVO.setAdvanceTime(advanceTime);
        // 中奖名单
        List<RedBagWinnerVO> redBagWinner = sessionService.getRedBagWinner(siteCode);
        clientInfoVO.setWinnerList(redBagWinner);
        return clientInfoVO;
    }

    public ToActivityVO participate(RedBagParticipateReqVO vo) {
        String siteCode = vo.getSiteCode();
        String userId = vo.getUserId();
        String userAccount = vo.getUserAccount();
        String timezone = vo.getTimeZone();
        // 活动有效校验
        String sessionId = vo.getRedbagSessionId();
        if (StrUtil.isBlank(sessionId)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        if (StrUtil.isBlank(userId)) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}", sessionId, siteCode, userId);
        SiteActivityRedBagSessionPO sessionPO = sessionCacheService.getOrLoad(sessionId);
        if (ObjUtil.isNull(sessionPO)) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}场次为空", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.RED_BAG_SESSION_END_ERROR.getCode(), ResultCode.RED_BAG_SESSION_END_ERROR.getMessageCode());
        }
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < sessionPO.getStartTime()) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}时间未开始", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.RED_BAG_SESSION_NOT_START_ERROR.getCode(), ResultCode.RED_BAG_SESSION_NOT_START_ERROR.getMessageCode());
        }
        if (currentTimeMillis > sessionPO.getEndTime()) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}时间已过期", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.RED_BAG_SESSION_END_ERROR.getCode(), ResultCode.RED_BAG_SESSION_END_ERROR.getMessageCode());
        }
        // 奖池校验
        BigDecimal sessionAmount = RedisUtil.getValue(String.format(ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT, siteCode, sessionPO.getSessionId()));
        if (ObjUtil.isNull(sessionAmount)) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}奖池为空", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.RED_BAG_SESSION_END_ERROR.getCode(), ResultCode.RED_BAG_SESSION_END_ERROR.getMessageCode());
        }
        UserBaseReqVO userBaseReqVO = UserBaseReqVO.builder()
                .activityId(sessionPO.getBaseId())
                .siteCode(siteCode)
                .deviceType(vo.getReqDeviceType())
                .userAccount(userAccount)
                .userId(userId)
                .timezone(timezone)
                .applyFlag(true)
                .build();
        ResponseVO<ToActivityVO> toActivityResVO = participateApi.checkToActivity(userBaseReqVO);
        if (toActivityResVO.isOk()) {
            ToActivityVO activityVO = toActivityResVO.getData();
            if (!activityVO.getStatus().equals(ResultCode.SUCCESS.getCode())) {
                return activityVO;
            }
        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        // vip段位校验
        Integer vipRank = userInfoVO.getVipRank();
        String rankLimitConfig = sessionPO.getRankLimitConfig();
        List<RedBagRainConfigVO> redBagRainConfigVOS = JSON.parseArray(rankLimitConfig, RedBagRainConfigVO.class);
        if (redBagRainConfigVOS.stream().noneMatch(s -> s.getVipRankCode().equals(vipRank))) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}当前vip段位不符合参与条件", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.RED_BAG_VIP_RANK_LIMIT_ERROR.getCode(), ResultCode.RED_BAG_VIP_RANK_LIMIT_ERROR.getMessageCode());
        }
        // 已参与校验
        String sendTag = String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_SEND_TAG, siteCode, sessionId, userAccount);
        if (RedisUtil.isKeyExist(sendTag)) {
            log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}已参与校验", sessionId, siteCode, userId);
            return new ToActivityVO(ResultCode.REDBAG_SESSION_PARTICIPATED_ERROR.getCode(), ResultCode.REDBAG_SESSION_PARTICIPATED_ERROR.getMessageCode());
        }
        // 活动要求流水校验
        long dayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timezone);
        long dayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timezone);
        if (ObjUtil.isNotNull(sessionPO.getBetAmount()) && sessionPO.getBetAmount().compareTo(BigDecimal.ZERO) > 0) {
            ReportUserVenueBetsTopVO betsTopVO = userVenueFixedWinLoseApi.queryUserWinLossInfo(ReportUserWinLossReqVO.builder().siteCode(siteCode).userId(userId).startTime(dayStartTime).endTime(dayEndTime).build());
            if (ObjUtil.isNull(betsTopVO) || ObjUtil.isNull(betsTopVO.getBetAmount())) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}未达到参与红包雨流水金额要求", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getMessageCode());
            }
            BigDecimal betAmount = betsTopVO.getBetAmount();
            ResponseVO<BigDecimal> betBigDecimalResponseVO = currencyInfoApi.transferPlatToMainCurrency(PlatCurrencyFromTransferVO.builder().siteCode(siteCode).sourceAmt(sessionPO.getBetAmount()).targetCurrencyCode(betsTopVO.getCurrency()).build());
            BigDecimal mainBetAmount = betBigDecimalResponseVO.getData();
            if (ObjUtil.isNull(mainBetAmount)) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}mainBetAmount为空", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getMessageCode());
            }
            if (betAmount.compareTo(mainBetAmount) < 0) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}betAmount《 mainBetAmount", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_RUNWATER_AMOUNT_ERROR.getMessageCode());
            }
        }

        // 活动要求存款校验
        if (ObjUtil.isNotNull(sessionPO.getDepositAmount()) && sessionPO.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            ResponseVO<ReportUserRechargeResponseVO> userRechargeResponseVO = reportUserRechargeApi.queryRechargeAmountByUserId(ReportUserRechargeUserRequestVO.builder().startTime(dayStartTime).endTime(dayEndTime).userId(userId).build());
            ReportUserRechargeResponseVO data = userRechargeResponseVO.getData();
            if (ObjUtil.isNull(data) || ObjUtil.isNull(data.getRechargeAmount())) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}未达到参与红包雨存款金额要求", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getMessageCode());
            }
            BigDecimal rechargeAmount = data.getRechargeAmount();
            BigDecimal depositAmount = sessionPO.getDepositAmount();
            ResponseVO<BigDecimal> bigDecimalResponseVO = currencyInfoApi.transferPlatToMainCurrency(PlatCurrencyFromTransferVO.builder().siteCode(siteCode).sourceAmt(depositAmount).targetCurrencyCode(data.getCurrency()).build());
            BigDecimal mainAmount = bigDecimalResponseVO.getData();
            if (ObjUtil.isNull(mainAmount)) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}mainAmount 为空", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getMessageCode());
            }
            if (rechargeAmount.compareTo(mainAmount) < 0) {
                log.info("红包雨参与活动,sessionId:{},siteCode:{},userId:{}rechargeAmount《  mainAmount", sessionId, siteCode, userId);
                return new ToActivityVO(ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getCode(), ResultCode.REDBAG_INSUFFICIENT_DEPOSIT_AMOUNT_ERROR.getMessageCode());
            }
        }

        RedisUtil.setValue(String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_PARTICIPATE_TAG, siteCode, sessionId, userInfoVO.getUserAccount()), CommonConstant.business_one, CommonConstant.HOUR25_SECONDS);
        return new ToActivityVO(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessageCode());
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateActivity(RedBagRainVO activity) {
        // 参数校验
        reaBagParamCheck(activity);
        String siteCode = CurrReqUtils.getSiteCode();
        // 更新
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagPO::getSiteCode, siteCode)
                .eq(SiteActivityRedBagPO::getBaseId, activity.getBaseId())
                .set(SiteActivityRedBagPO::getSessionStartTime, String.join(CommonConstant.COMMA, activity.getSessionTime().keySet()))
                .set(SiteActivityRedBagPO::getSessionEndTime, String.join(CommonConstant.COMMA, activity.getSessionTime().values()))
                .set(SiteActivityRedBagPO::getBetAmount, activity.getBetAmount())
                .set(SiteActivityRedBagPO::getDepositAmount, activity.getDepositAmount())
                .set(SiteActivityRedBagPO::getTotalAmount, activity.getTotalAmount())
                .set(SiteActivityRedBagPO::getBetAmount, activity.getBetAmount())
                .set(SiteActivityRedBagPO::getAdvanceTime, activity.getAdvanceTime())
                .set(SiteActivityRedBagPO::getRankLimit, StrUtil.join(CommonConstant.COMMA, activity.getRankLimit()))
                .set(SiteActivityRedBagPO::getDropTime, activity.getDropTime())
                .update();
        List<SiteActivityRedBagConfigPO> configPOList = Lists.newArrayList();
        List<SiteActivityRedBagRankConfigPO> rankConfigPOList = Lists.newArrayList();
        activity.getConfigList().forEach(config -> {
            SiteActivityRedBagConfigPO configPO = new SiteActivityRedBagConfigPO();
            BeanUtils.copyProperties(config, configPO);
            configPO.setBaseId(activity.getBaseId());
            configPO.setSiteCode(siteCode);
            configPOList.add(configPO);
            List<RedBagRainRankConfigVO> rankConfigList = config.getRankConfigList();
            rankConfigList.forEach(rankConfig -> {
                SiteActivityRedBagRankConfigPO rankConfigPO = new SiteActivityRedBagRankConfigPO();
                BeanUtils.copyProperties(rankConfig, rankConfigPO);
                rankConfigPO.setBaseId(activity.getBaseId());
                rankConfigPO.setSiteCode(siteCode);
                rankConfigPO.setVipRankCode(config.getVipRankCode());
                rankConfigPOList.add(rankConfigPO);
            });
        });
        configService.deleteByBaseId(activity.getBaseId());
        configService.saveBatch(configPOList);
        rankConfigService.deleteByBaseId(activity.getBaseId());
        rankConfigService.saveBatch(rankConfigPOList);
    }

    public RedBagRainRespVO backendInfo(String baseId) {
        SiteActivityRedBagPO po = new LambdaQueryChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagPO::getBaseId, baseId)
                .one();
        return getRedBagRainRespVO(po);
    }

    /**
     * 解析全部信息
     *
     * @param po
     * @return
     */
    private RedBagRainRespVO getRedBagRainRespVO(SiteActivityRedBagPO po) {
        // 红包雨基本信息解析填充
        String baseId = po.getBaseId();
        RedBagRainRespVO respVO = new RedBagRainRespVO();
        BeanUtils.copyProperties(po, respVO);
        respVO.setRankLimit(List.of(po.getRankLimit().split(CommonConstant.COMMA)));
        String sessionStartTime = po.getSessionStartTime();
        String sessionEndTime = po.getSessionEndTime();
        List<String> startTimeList = List.of(sessionStartTime.split(CommonConstant.COMMA));
        List<String> endTimeList = List.of(sessionEndTime.split(CommonConstant.COMMA));
        LinkedHashMap<String, String> sessionTimeMap = IntStream.range(0, startTimeList.size())
                .mapToObj(i -> new AbstractMap.SimpleEntry<>(startTimeList.get(i), endTimeList.get(i)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (k1, k2) -> k2, LinkedHashMap::new));
        respVO.setSessionTime(sessionTimeMap);
        // 红包雨配置附表填充
        List<RedBagRainConfigVO> configList = Lists.newArrayList();
        List<SiteActivityRedBagConfigPO> configPOList = configService.getByBaseId(baseId);
        List<SiteActivityRedBagRankConfigPO> rankConfigPOList = rankConfigService.getByBaseId(baseId);
        ResponseVO<List<SiteVIPRankVO>> listResponseVO = vipRankApi.getVipRankListBySiteCode(po.getSiteCode());
        List<SiteVIPRankVO> vipRankInfo = listResponseVO.getData();
        Map<Integer, String> rankInfoMap = Optional.ofNullable(vipRankInfo).map(s -> s.stream().collect(Collectors.toMap(SiteVIPRankVO::getVipRankCode, SiteVIPRankVO::getVipRankNameI18nCode, (k1, k2) -> k2))).orElse(Maps.newHashMap());
        Map<Integer, List<SiteActivityRedBagRankConfigPO>> rankConfigMap = rankConfigPOList.stream().collect(Collectors.groupingBy(SiteActivityRedBagRankConfigPO::getVipRankCode));
        configPOList.forEach(config -> {
            RedBagRainConfigVO configVO = new RedBagRainConfigVO();
            BeanUtils.copyProperties(config, configVO);
            Integer vipRankCode = config.getVipRankCode();
            configVO.setVipRankName(rankInfoMap.get(vipRankCode));
            List<SiteActivityRedBagRankConfigPO> rankConfigList = rankConfigMap.get(vipRankCode);
            List<RedBagRainRankConfigVO> rankConfigVOList = Lists.newArrayList();
            rankConfigList.forEach(rankConfig -> {
                RedBagRainRankConfigVO rankConfigVO = new RedBagRainRankConfigVO();
                BeanUtils.copyProperties(rankConfig, rankConfigVO);
                rankConfigVOList.add(rankConfigVO);
            });
            configVO.setRankConfigList(rankConfigVOList);
            configList.add(configVO);
        });
        respVO.setConfigList(configList);
        return respVO;
    }

    public RedBagSettlementVO settlement(RedBagSettlementReqVO reqVO) {
        RedBagRecordTotalVO recordTotalVO = sessionService.sessionUserSettlement(reqVO.getSiteCode(), reqVO.getRedbagSessionId(), reqVO.getUserAccount(), reqVO.getUserId());
        return new RedBagSettlementVO(recordTotalVO.getTotalAmount(), recordTotalVO.getCount());
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(ActiveBaseOnOffVO vo) {
        // 删除xxl-job
        SiteActivityRedBagPO redBagPO = getRedBagPO(vo.getId());
        // DB delete
        new LambdaUpdateChainWrapper<>(baseMapper)
                .eq(SiteActivityRedBagPO::getBaseId, vo.getId())
                .remove();
        configService.deleteByBaseId(vo.getId());
        rankConfigService.deleteByBaseId(vo.getId());
        String startJobId = redBagPO.getStartJobId();
        String endJobId = redBagPO.getEndJobId();
        removeXxlJob(startJobId, endJobId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBasePO> siteActivityBasePOS) {
        if (CollectionUtil.isNotEmpty(siteActivityBasePOS)) {
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
        String siteCode = vo.getSiteCode();
        SiteActivityRedBagPO redBagPO = getRedBagPO(vo.getId());

        // 开启活动停止job
        // 启用
        if (vo.getStatus().equals(EnableStatusEnum.ENABLE.getCode())) {
            String siteTimezone = CurrReqUtils.getTimezone();
            LocalDateTime now = TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), siteTimezone);
            ActivityBaseRespVO respVO = baseService.queryActivityById(vo.getId(), vo.getSiteCode());
            Long activityStartTime = respVO.getActivityStartTime();
            Long activityEndTime = respVO.getActivityEndTime();
            LocalDateTime start = TimeZoneUtils.timeByTimeZone(activityStartTime, siteTimezone);
            LocalDateTime end = null;
            if (ObjUtil.isNotNull(activityEndTime)) {
                end = TimeZoneUtils.timeByTimeZone(activityEndTime, siteTimezone);
            }
            // 现在时间 在开始时间之后
            if (now.isAfter(start)) {
                start = now;
            }
            // 重新生成全部场次
            RedBagRainRespVO redBagRainRespVO = getRedBagRainRespVO(redBagPO);
            log.info("红包雨启用,重新生成全部场次,活动id:{},站点:{},start:{},end:{},配置:{}",vo.getId(), siteCode,start,end,redBagRainRespVO);
            sessionService.regenerateSession(vo.getId(), siteCode, start, end, redBagRainRespVO, siteTimezone);
            // 新增xxl-job
            List<String> startJobId = jobComponent.redBagStartCreate(redBagPO.getSessionStartTime(), now, redBagPO.getAdvanceTime(), siteTimezone);
            List<String> endJobId = jobComponent.redBagEndCreate(redBagPO.getSessionEndTime(), siteTimezone);
            ArrayList<String> jobIds = Lists.newArrayList(startJobId);
            jobIds.addAll(endJobId);
            log.info("红包雨启用,,活动id:{},站点:{},更新jobid:{}",vo.getId(), siteCode,jobIds);
            jobComponent.start(jobIds);
            // 更新jobid
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(SiteActivityRedBagPO::getSiteCode, siteCode)
                    .set(SiteActivityRedBagPO::getStartJobId, String.join(CommonConstant.COMMA, startJobId))
                    .set(SiteActivityRedBagPO::getEndJobId, String.join(CommonConstant.COMMA, endJobId))
                    .set(SiteActivityRedBagPO::getUpdatedTime, System.currentTimeMillis())
                    .set(SiteActivityRedBagPO::getUpdater, CurrReqUtils.getAccount())
                    .update();
        } else {
            // 存在活动中场次直接结算
            SiteActivityRedBagSessionPO sessionPO = sessionService.getBySiteCode(siteCode);
            if (ObjUtil.isNotNull(sessionPO)) {
                log.info("红包雨禁用,存在活动中场次直接结算:{},{}",siteCode,sessionPO);
                _this.activityRedBagEndPush(sessionPO.getSiteCode(), sessionPO.getEndTimeStr(), false);
            }
            log.info("红包雨禁用,删除未执行场次信息:{}",siteCode);
            // 删除未执行场次信息
            sessionService.delDayNotStart(siteCode);
            // 禁用
            // 删除xxl-job
            String startJobId = redBagPO.getStartJobId();
            String endJobId = redBagPO.getEndJobId();
            log.info("红包雨禁用,删除xxl-job:{}",siteCode);
            removeXxlJob(startJobId, endJobId);
            // 缓存清空
            log.info("红包雨禁用,缓存清空:{}", CacheConstants.ACTIVITY_REDBAG_RAIN_SESSION_ID_CACHE);
            redBagRainIdCacheService.clear();
            // 更新jobid
            log.info("红包雨禁用,更新jobId:{}为空",siteCode);
            new LambdaUpdateChainWrapper<>(baseMapper)
                    .eq(SiteActivityRedBagPO::getSiteCode, siteCode)
                    .set(SiteActivityRedBagPO::getStartJobId, null)
                    .set(SiteActivityRedBagPO::getEndJobId, null)
                    .set(SiteActivityRedBagPO::getUpdatedTime, System.currentTimeMillis())
                    .set(SiteActivityRedBagPO::getUpdater, CurrReqUtils.getAccount())
                    .update();
        }
    }

    private void removeXxlJob(String startJobId, String endJobId) {
        ArrayList<String> jobIds = Lists.newArrayList();
        if (StrUtil.isNotBlank(startJobId)) {
            List<String> startJobArr = Arrays.stream(startJobId.split(CommonConstant.COMMA)).toList();
            jobIds.addAll(startJobArr);
        }
        if (StrUtil.isNotBlank(endJobId)) {
            List<String> endJobArr = Arrays.stream(endJobId.split(CommonConstant.COMMA)).toList();
            jobIds.addAll(endJobArr);
        }
        if (CollUtil.isNotEmpty(jobIds)) {
            jobComponent.remove(jobIds);
        }
    }

    private SiteActivityRedBagPO getRedBagPO(String baseId) {
        return new LambdaQueryChainWrapper<>(baseMapper).eq(SiteActivityRedBagPO::getBaseId, baseId).one();
    }

    @DistributedLock(name = ACTIVITY_REDBAG_SESSION_OPERATE_SITE, unique = "#siteCode", fair = true, waitTime = 3, leaseTime = 60)
    public void activityRedBagStartPush(String siteCode, String timeStr) {
        log.info("红包雨开始场次推送siteCode:{},timeStr:{}", siteCode, timeStr);
        // 站点时区
        ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteCode);
        String timezone = siteInfo.getData().getTimezone();
        // startTime 计算
        long startTimeMills = startTimeCaculate(timeStr, timezone);
        // 之前存在进行中场次,异常情况导致未正常结束,在下一场次进行结束
        List<SiteActivityRedBagSessionPO> processList = sessionService.selectProcessList(siteCode);
        if (CollUtil.isNotEmpty(processList)) {
            processList.stream().filter(s -> !s.getStartTime().equals(startTimeMills)).forEach(
                    po -> {
                        log.info("红包雨开始场次,存在异常未正常结束场次,需要提前结束:{}",po);
                        endBySessionPo(siteCode, po, timezone, false);
                    }
            );
        }
        // 更新场次信息
        SiteActivityRedBagSessionPO sessionPO = sessionService.setSessionProcessStatus(siteCode, startTimeMills);
        if (ObjUtil.isNull(sessionPO)) {
            log.warn("红包雨开始场次信息不存在,siteCode:{},timeStr:{}", siteCode, timeStr);
            return;
        }
        log.info("红包雨开始场次推送siteCode:{},sessionId:{}", siteCode, sessionPO.getSessionId());
        // 奖池金额刷新
        RedisUtil.setValue(String.format(ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT, siteCode, sessionPO.getSessionId()), sessionPO.getTotalAmount(), CommonConstant.HOUR25_SECONDS);
        redBagRainIdCacheService.reload(getRedBagSessionIdCacheKey(siteCode));

        ResponseVO<RedBagRealTimeInfo> responseVO = realTimeInfo(siteCode);
        //红包雨查询成功才需要展示
        if(responseVO.isOk()){
            // ws信息推送
            WsMessageMqVO messageMqVO = new WsMessageMqVO();
            messageMqVO.setSiteCode(siteCode);
            messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.ACTIVITY_RED_BAG_RAIN.getTopic(), responseVO));
            KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
            log.info("红包雨开始场次推送siteCode:{},sessionId:{}", siteCode, sessionPO.getSessionId());
        }

    }

    public static long startTimeCaculate(String timeStr, String timezone) {
        String[] timeSpl = timeStr.split(CommonConstant.COLON);
        long currentTimeMillis = System.currentTimeMillis();
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timezone);

        LocalDateTime sessionStartLocalTime = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), Integer.parseInt(timeSpl[0]), Integer.parseInt(timeSpl[1]));

        long sessionStartMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionStartLocalTime, timezone);
        // 差距超过12小时 则取第二天时间查询
        if (Math.abs(sessionStartMillis - currentTimeMillis) >= CommonConstant.HOUR12_MILLISECONDS) {
            // 凌晨提前会跨天
            LocalDateTime sessionStartLocalTimePlusOneDay = sessionStartLocalTime.plusDays(1);
            sessionStartMillis = TimeZoneUtils.convertLocalDateTimeToMillis(sessionStartLocalTimePlusOneDay, timezone);
        }
        return sessionStartMillis;
    }

  /*  public static void main(String[] args) {
             System.err.println("startTime="+startTimeCaculate("13:00","UTC+8"));
    }*/

    private static String getRedBagSessionIdCacheKey(String siteCode) {
        return siteCode + CommonConstant.COLON + ActivityConstant.ACTIVITY_REDBAG_REAL_TIME_SESSION_KEY;
    }

    @DistributedLock(name = ACTIVITY_REDBAG_SESSION_OPERATE_SITE, unique = "#siteCode", fair = true, waitTime = 3, leaseTime = 60)
    public void activityRedBagEndPush(String siteCode, String timeStr, boolean recalculate) {
        log.info("红包雨结束场次推送siteCode:{},timeStr:{}", siteCode, timeStr);
        // 站点时区
        ResponseVO<SiteVO> siteInfo = siteApi.getSiteInfo(siteCode);
        String timezone = siteInfo.getData().getTimezone();
        LocalDateTime now = TimeZoneUtils.timeByTimeZone(System.currentTimeMillis(), timezone);
        SiteActivityRedBagSessionPO sessionPO = sessionService.getBySiteCodeAndDayTime(siteCode, TimeZoneUtils.formatLocalDateTime(now, TimeZoneUtils.patten_yyyyMMdd), timeStr);
        if (ObjUtil.isNull(sessionPO)) {
            log.warn("红包雨结束场次信息不存在,siteCode:{},timeStr:{}", siteCode, timeStr);
            return;
        }
        endBySessionPo(siteCode, sessionPO, timezone, recalculate);
    }

    private void endBySessionPo(String siteCode, SiteActivityRedBagSessionPO sessionPO, String timezone, boolean recalculate) {
        log.info("红包雨场次结束推送,siteCode:{},sessionId:{}", siteCode, sessionPO.getSessionId());
        // 场次关闭
        sessionService.setSessionEndStatus(sessionPO);
        // 清除实时场次session id 缓存信息
        redBagRainIdCacheService.evict(getRedBagSessionIdCacheKey(siteCode));
        // 活动结束直接关闭
        ActivityBaseRespVO respVO = baseService.queryActivityById(sessionPO.getBaseId(), sessionPO.getSiteCode());
        SiteActivityRedBagPO bagPO = getRedBagPO(sessionPO.getBaseId());
        Long activityEndTime = respVO.getActivityEndTime();
        LocalDateTime activityEndTimeLocal = null;
        if (ObjUtil.isNotNull(activityEndTime)) {
            activityEndTimeLocal = TimeZoneUtils.timeByTimeZone(activityEndTime, timezone);
        }
        // 最后一场次关闭
        if (ObjUtil.isNotNull(activityEndTime) && activityEndTime <= System.currentTimeMillis()) {
            // 清空定时任务
            removeXxlJob(bagPO.getStartJobId(), bagPO.getEndJobId());
            log.info("红包雨场次结束推送,siteCode:{},sessionId:{} 清空定时任务", siteCode, sessionPO.getSessionId());
        }
        // 最后一场次生成全部场次
        if (recalculate) {
            if (Objects.equals(sessionPO.getLatest(), CommonConstant.business_one)) {
                RedBagRainRespVO redBagRainRespVO = getRedBagRainRespVO(bagPO);
                Long endTime = sessionPO.getEndTime();
                LocalDateTime endDateTime = TimeZoneUtils.timeByTimeZone(endTime, timezone);
                log.info("红包雨场次结束推送,siteCode:{},sessionId:{} 最后一场次生成全部场次", siteCode, sessionPO.getSessionId());
                sessionService.regenerateSession(sessionPO.getBaseId(), siteCode, endDateTime, activityEndTimeLocal, redBagRainRespVO, timezone);
            }
        }
        // ws信息推送
        WsMessageMqVO messageMqVO = new WsMessageMqVO();
        messageMqVO.setSiteCode(siteCode);
        messageMqVO.setMessage(new WSBaseResp<>(WSSubscribeEnum.ACTIVITY_RED_BAG_RAIN_END.getTopic(), ResponseVO.success(new ActivityRedBagRainEndDto(sessionPO.getSessionId()))));
        KafkaUtil.send(WsMessageConstant.WS_MESSAGE_BROADCAST_TOPIC, messageMqVO);
        // 更新场次信息为已结算
        log.info("红包雨场次结束推送,siteCode:{},sessionId:{},更新场次信息为已结算", siteCode, sessionPO.getSessionId());
        sessionService.settledSession(sessionPO.getSessionId());
        // 清除缓存
        sessionCacheService.evict(sessionPO.getSessionId());
        // 批量结算
        batchSettlement(siteCode, sessionPO.getSessionId());
        log.info("红包雨场次结束推送,siteCode:{},sessionId:{}", siteCode, sessionPO.getSessionId());
    }

    /**
     * 批量结算
     *
     * @param siteCode
     * @param sessionId
     */
    private void batchSettlement(String siteCode, String sessionId) {
        log.info("红包雨批量会员金额结算,siteCode:{},sessionId:{}", siteCode, sessionId);
        List<SiteActivityRedBagRecordPO> recordPOS = redBagRecordService.selectBySessionIdByGroup(siteCode, sessionId);
        if (CollUtil.isNotEmpty(recordPOS)) {
            for (SiteActivityRedBagRecordPO po : recordPOS) {
                try {
                    sessionService.sessionUserSettlement(siteCode, sessionId, po.getUserAccount(), po.getUserId());
                } catch (Exception e) {
                    log.error("红包雨活动会员结算失败,siteCode:{},sessionId:{},userId:{},error:", siteCode, sessionId, po.getUserId(), e);
                }
            }
        }
    }


    public RedBagSendRespVO send(RedBagSendReqVO vo) {
        log.info("红包雨发送红包,siteCode:{},uid:{}", vo.getSiteCode(), vo.getUserId());
        String siteCode = vo.getSiteCode();
        String sessionId = vo.getRedbagSessionId();
        String userAccount = vo.getUserAccount();
        String userId = vo.getUserId();
        SiteActivityRedBagSessionPO sessionPO = sessionCacheService.getOrLoad(vo.getRedbagSessionId());
        if (ObjUtil.isNull(sessionPO)) {
            log.info("红包雨发送红包,场次不存在:{}", vo);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        // 已结算
        if (sessionPO.getSettled().equals(CommonConstant.business_one)) {
            log.info("红包雨发送红包,场次已结算:{}", vo);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        // 超时抢返回0 并不记录
        long currentTimeMillis = System.currentTimeMillis();
        if (currentTimeMillis < sessionPO.getStartTime() || currentTimeMillis > sessionPO.getEndTime()) {
            log.info("红包雨发送红包,时间无效:{}", vo);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        boolean allowedParticipate = RedisUtil.isKeyExist(String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_PARTICIPATE_TAG, siteCode, sessionId, userAccount));
        if (!allowedParticipate) {
            log.info("红包雨发送红包,校验未通过:{}", vo);
            throw new BaowangDefaultException(ResultCode.RED_BAG_SESSION_END_ERROR);
        }
        // 参与标记
        String sendTag = String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_SEND_TAG, siteCode, sessionId, userAccount);
        if (!RedisUtil.isKeyExist(sendTag)) {
            log.info("红包雨发送红包,参与标记不存在:{}", vo);
            RedisUtil.setValue(sendTag, CommonConstant.business_one, CommonConstant.HOUR25_SECONDS);
        }
        // 结算标记
        String settlmentTag = String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_SETTLEMENT_USER_TAG, siteCode, sessionId, userId);
        if (RedisUtil.isKeyExist(settlmentTag)) {
            log.warn("红包雨场次结算完成后继续抢红包,siteCode:{},sessionId:{},userId:{}", siteCode, sessionId, userId);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        // 奖池限制
        BigDecimal sessionAmount = RedisUtil.getValue(String.format(ACTIVITY_REDBAG_SESSION_TOTAL_AMOUNT, siteCode, sessionId));
        if (ObjUtil.isNull(sessionAmount) || sessionAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("红包雨发送红包,奖池限制:{}", vo);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        // 红包个数限制
        String rankLimitConfig = sessionPO.getRankLimitConfig();
        List<RedBagRainConfigVO> redBagRainConfigVOS = JSON.parseArray(rankLimitConfig, RedBagRainConfigVO.class);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        Integer vipRank = userInfoVO.getVipRank();
        Map<Integer, RedBagRainConfigVO> map = redBagRainConfigVOS.stream().collect(Collectors.toMap(RedBagRainConfigVO::getVipRankCode, p -> p, (k1, k2) -> k2));
        RedBagRainConfigVO configVO = map.get(vipRank);
        Integer amountType = configVO.getAmountType();
        RedBagTypeEnum redBagTypeEnum = RedBagTypeEnum.of(amountType);
        List<RedBagRainRankConfigVO> rankConfigList = configVO.getRankConfigList();
        Integer redBagMaximum = configVO.getRedBagMaximum();
        String sessionUserRedBagKey = String.format(RedisConstants.ACTIVITY_REDBAG_SESSION_USER_COUNT, siteCode, sessionId, userAccount);
        Long userRedbagCount = RedisUtil.getAtomicLong(sessionUserRedBagKey);
        // 超过段位上限返回0 也不记录
        if (userRedbagCount >= redBagMaximum) {
            log.info("红包雨发送红包,超过段位上限返回0:{}", vo);
            return new RedBagSendRespVO(BigDecimal.ZERO);
        }
        RedisUtil.atomicLongIncr(sessionUserRedBagKey, CommonConstant.HOUR25_SECONDS);
        // 金额计算
        BigDecimal amount = computerRedbagAmount(redBagTypeEnum, rankConfigList);
        // 奖池扣减
        Pair<BigDecimal, BigDecimal> amountPair = sessionService.prizePoolReduce(amount, siteCode, sessionId);
        // 存记录
        if (ObjUtil.isNotNull(amountPair.getValue()) && amountPair.getValue().compareTo(BigDecimal.ZERO) > 0) {
            log.info("红包雨发送红包,开始存记录:{}", vo);
            redBagRecordService.saveRecord(sessionPO, userInfoVO, amountPair);
        }
        return new RedBagSendRespVO(amountPair.getValue());
    }

    private static BigDecimal computerRedbagAmount(RedBagTypeEnum redBagTypeEnum, List<RedBagRainRankConfigVO> rankConfigList) {
        BigDecimal amount = BigDecimal.ZERO;
        switch (redBagTypeEnum) {
            case FIXED -> {
                ProbabilityGenerator<BigDecimal> generator = new ProbabilityGenerator<>();
                rankConfigList.forEach(s -> {
                    // 添加不同值及其概率
                    generator.addEntry(s.getFixedAmount(), s.getHitRate());
                });
                amount = generator.getRandomValue();
            }
            case RANDOM -> {
                ProbabilityRangeGenerator generator = new ProbabilityRangeGenerator();
                rankConfigList.forEach(s -> {
                    // 添加不同值及其概率
                    generator.addRange(s.getRandomStartAmount(), s.getRandomEndAmount(), s.getHitRate().divide(CommonConstant.DECIMAL_100, RoundingMode.HALF_UP));
                });
                amount = generator.getRandomValue();
            }
        }
        return amount;
    }
}




