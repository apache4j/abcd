package com.cloud.baowang.activity.service.consumer;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.FreeGameSendStatusEnum;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameTriggerVO;
import com.cloud.baowang.activity.api.vo.ActivityFreeGameVO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityFreeGameRecordPO;
import com.cloud.baowang.activity.po.SiteActivityFreeWheelPO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.SiteActivityFreeWheelRepository;
import com.cloud.baowang.activity.service.SiteActivityFreeGameRecordService;
import com.cloud.baowang.activity.service.SiteActivityFreeWheelService;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.GroupConstants;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.play.api.api.third.PPGameApi;
import com.cloud.baowang.play.api.api.third.ThirdApi;
import com.cloud.baowang.play.api.enums.FreeGameChangeTypeEnum;
import com.cloud.baowang.play.api.vo.mq.FreeGameRecordTriggerVO;
import com.cloud.baowang.play.api.vo.mq.FreeGameRecordVO;
import com.cloud.baowang.play.api.vo.mq.PPFreeGameRecordReqVO;
import com.cloud.baowang.play.api.vo.pp.req.PPGameLimitReqVO;
import com.cloud.baowang.play.api.vo.pp.res.PPGameLimitCurrencyResVO;
import com.cloud.baowang.play.api.vo.pp.res.PPGameLimitResVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Slf4j
@Component
@AllArgsConstructor
public class ActivityFreeGameListener {

    private final static Integer DEFAULT_TIME_LIMIT = 30;
    private final UserInfoApi userInfoApi;
    private final SiteActivityFreeGameRecordService siteActivityFreeGameRecordService;
    private final ThirdApi thirdApi;
    private final SiteActivityBaseRepository siteActivityBaseRepository;
    private final SiteActivityFreeWheelService siteActivityFreeWheelService;
    private final SiteActivityFreeWheelRepository siteActivityFreeWheelRepository;
    private final PPGameApi ppGameApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteActivityFreeGameRecordService freeGameRecordService;


    @KafkaListener(topics = TopicsConstants.FREE_GAME, groupId = GroupConstants.FREE_GAME_GROUP)
    public void freeGameMessage(ActivityFreeGameTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("活动触发，添加免费旋转。接收到免费游戏旋转消息:{}", JSON.toJSONString(triggerVO));
        if (CollectionUtil.isEmpty(triggerVO.getFreeGameVOList())) {
            ackItem.acknowledge();
            return;
        }
        try {
            List<ActivityFreeGameVO> freeGameVOList = triggerVO.getFreeGameVOList();
            if (CollectionUtil.isEmpty(freeGameVOList)) {
                ackItem.acknowledge();
                return;
            }
            String gameIds = triggerVO.getFreeGameVOList().stream().map(ActivityFreeGameVO::getAccessParameters).collect(Collectors.joining(","));

            ActivityFreeGameVO freeGameVO = freeGameVOList.get(0);
            String siteCode = freeGameVO.getSiteCode();

            List<String> userIds = freeGameVOList.stream().map(ActivityFreeGameVO::getUserId).distinct().collect(Collectors.toList());
            List<UserInfoVO> userInfoVOs = userInfoApi.getByUserIds(userIds, siteCode);
            Map<String, UserInfoVO> userId2Info = userInfoVOs.stream().collect(Collectors.toMap(UserInfoVO::getUserId, e -> e));

            if (freeGameVO.getHandicapMode()==0){
                // 不同的站点也不可能发送消息
                Map<Object, Boolean> map = new HashMap<>();
                freeGameVOList = freeGameVOList.stream().filter(i -> map.putIfAbsent(i.getOrderNo(), Boolean.TRUE) == null).collect(Collectors.toList());
                List<String> orderNos = freeGameVOList.stream().map(ActivityFreeGameVO::getOrderNo).distinct().collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(orderNos)) {
                    List<SiteActivityFreeGameRecordPO> list = siteActivityFreeGameRecordService.list(Wrappers.<SiteActivityFreeGameRecordPO>lambdaQuery().select(SiteActivityFreeGameRecordPO::getOrderNo).in(SiteActivityFreeGameRecordPO::getOrderNo, orderNos));
                    if (CollectionUtil.isNotEmpty(list)) {
                        List<String> existOrderNo = list.stream().map(SiteActivityFreeGameRecordPO::getOrderNo).distinct().collect(Collectors.toList());
                        freeGameVOList.removeIf(e -> existOrderNo.contains(e.getOrderNo()));
                    }
                }
                if (CollectionUtil.isEmpty(freeGameVOList)) {
                    ackItem.acknowledge();
                    return;
                }
                // 领取限制校验
                String activityId = freeGameVO.getActivityId();
                SiteActivityBasePO siteActivityBasePO = siteActivityBaseRepository.selectById(activityId);
                if (Objects.isNull(siteActivityBasePO)) {
                    log.error("活动不存在，{}", freeGameVOList);
                    ackItem.acknowledge();
                    return;
                }
                // 活动参与条件校验
                joinActivityValid(siteActivityBasePO, freeGameVOList, userId2Info);
            }

            // 查询限制金额
            PPGameLimitReqVO req = new PPGameLimitReqVO();
            req.setGameIds(gameIds);
            req.setVenueCode(VenuePlatformConstants.PP);
            req.setSiteCode(siteCode);
            ResponseVO<List<PPGameLimitResVO>> limitGameLine = ppGameApi.getLimitGameLine(req);
            if (!limitGameLine.isOk()
                    || CollectionUtil.isEmpty(limitGameLine.getData())) {
                log.error("查询PP限注金额失败，{}", freeGameVOList);
                ackItem.acknowledge();
                return;
            }
            List<PPGameLimitResVO> ppgameLimitResVOS = limitGameLine.getData();
            //List<PPGameLimitCurrencyResVO> currencyGameLimits = limitGameLine.getData().get(0).getCurrencyGameLimits();

            // 查询汇率
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode); // 获取所有币种汇率
            List<SiteActivityFreeGameRecordPO> recordPOs = new ArrayList<>();
            for (ActivityFreeGameVO activityFreeGameTemp : freeGameVOList) {

                UserInfoVO userInfoVO = userId2Info.get(activityFreeGameTemp.getUserId());
                // 获取指定游戏
                PPGameLimitResVO ppGameLimitResVO = ppgameLimitResVOS.stream().filter(e -> ObjectUtil.equal(e.getGameId(),
                        activityFreeGameTemp.getAccessParameters())).findFirst().orElse(null);

                if (Objects.isNull(ppGameLimitResVO) || CollectionUtil.isEmpty(ppGameLimitResVO.getCurrencyGameLimits())) {
                    log.error("查询PP限注金额失败，{}", freeGameVOList);
                    ackItem.acknowledge();
                    continue;
                }
                List<PPGameLimitCurrencyResVO> currencyGameLimits = ppGameLimitResVO.getCurrencyGameLimits();
                // 获取指定金额的限注list
                Optional<PPGameLimitCurrencyResVO> first = currencyGameLimits.stream().filter(
                        e -> ObjectUtil.equal(e.getCurrency(), userInfoVO.getMainCurrency())).findFirst();
                if (first.isEmpty()) {
                    log.info("没有查询到该币种的限制投注金额");
                    continue;
                }

                BigDecimal userFinalRate = allFinalRate.get(userInfoVO.getMainCurrency());

                //NOTE 如果币种不是WTC， 汇率取 1
                if (!activityFreeGameTemp.getCurrencyCode().equals("WTC")){
                    userFinalRate = BigDecimal.ONE;
                }

                // 根据币种，获取限制投注金额
                BigDecimal betLimitAmount = getBetLimitAmount(first.get(), userFinalRate, activityFreeGameTemp.getBetLimitAmount());
                if (betLimitAmount.compareTo(BigDecimal.ZERO) == 0) {
                    log.info("没有查询到该币种的限制投注金额");
                    continue;
                }

                // 插入记录表
                SiteActivityFreeGameRecordPO recordPO = new SiteActivityFreeGameRecordPO();

                recordPO.setAcquireNum(activityFreeGameTemp.getAcquireNum());
                recordPO.setSiteCode(userInfoVO.getSiteCode());
                // 活动过添加
                recordPO.setType(FreeGameChangeTypeEnum.ACTIVITY_ADD.getCode());
                recordPO.setUserId(userInfoVO.getUserId());
                recordPO.setUserAccount(userInfoVO.getUserAccount());
                //  指定PP场馆
                recordPO.setVenueCode(VenuePlatformConstants.PP);
                String orderNo = activityFreeGameTemp.getOrderNo();
                recordPO.setOrderNo(orderNo);
                recordPO.setRemark("活动添加");
                recordPO.setCurrencyCode(userInfoVO.getMainCurrency());
                // 限制金额

                long now = System.currentTimeMillis();
                recordPO.setReceiveStartTime(now);
                long endTime = now + (DEFAULT_TIME_LIMIT * 24L * 3600 * 1000);
                // 默认一个月时间
                recordPO.setReceiveEndTime(endTime);

                recordPO.setBetLimitAmount(betLimitAmount);
                recordPO.setReceiveStatus(1);
                recordPO.setBalance(activityFreeGameTemp.getAcquireNum());
                recordPO.setTimeLimit(DEFAULT_TIME_LIMIT * 24);
                recordPO.setGameId(activityFreeGameTemp.getAccessParameters());
                Integer latestCount = 0;

                recordPO.setOrderTime(System.currentTimeMillis());
                recordPO.setBeforeNum(latestCount);
                recordPO.setCreator("system");
                recordPO.setUpdater("system");
                recordPO.setReceiveStatus(1);
                recordPO.setBetWinLose(BigDecimal.ZERO);
                //  @Schema(title = "旋转次数变化类型 1-活动，2-配置")
                recordPO.setOrderType(1);

                recordPO.setAfterNum(activityFreeGameTemp.getAcquireNum());
                recordPO.setSendStatus(FreeGameSendStatusEnum.SENDING.getType());
                recordPO.setWashRatio(activityFreeGameTemp.getWashRatio());
                recordPO.setActivityId(activityFreeGameTemp.getActivityId());
                recordPO.setActivityTemplate(activityFreeGameTemp.getActivityTemplate());
                recordPO.setActivityNo(activityFreeGameTemp.getActivityNo());
                log.info("活动插入记录:{}", JSONObject.toJSONString(recordPO));
                recordPOs.add(recordPO);
            }
            if (CollectionUtil.isNotEmpty(recordPOs)) {
                freeGameRecordService.saveBatch(recordPOs);
            }
            // 调三方
            freeGameRecordService.sendPPGiveFreeConfig(recordPOs);
            ackItem.acknowledge();
        } catch (Exception e) {
            log.error("接收到免费游戏旋转消息异常,消息：{}，e：", JSON.toJSONString(triggerVO), e);
        } finally {
            ackItem.acknowledge();
        }

    }

    /**
     * 根据币种获取最大投注金额
     *
     * @return 限制投注金额 (默认返回 BigDecimal.ZERO)
     */
    public BigDecimal getBetLimitAmount(PPGameLimitCurrencyResVO ppgameLimitCurrencyResVO, BigDecimal allFinalRate, BigDecimal betLimitAmount) {
        if (allFinalRate == null || betLimitAmount == null || allFinalRate.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // 计算最终的投注金额
        BigDecimal finalBetAmount = betLimitAmount.multiply(allFinalRate);
        // 比较最终投注金额与限制金额
        List<BigDecimal> betLimitAmountList = ppgameLimitCurrencyResVO.getBetPerLineScales().toJavaList(BigDecimal.class);
        if (betLimitAmountList.isEmpty()) {
            return BigDecimal.ZERO; // 没有配置限额
        }
        boolean matched = betLimitAmountList.stream()
                .anyMatch(limit -> limit.compareTo(finalBetAmount) == 0);
        // 匹配到
        if (matched) {
            return finalBetAmount;
        }
        //先从小到大排序
        List<BigDecimal> sorted = betLimitAmountList.stream().sorted().toList();
        // 小于最小值 → 取最小值
        if (finalBetAmount.compareTo(sorted.get(0)) < 0) {
            return sorted.get(0);
        }

        // 大于最大值 → 取最大值
        if (finalBetAmount.compareTo(sorted.get(sorted.size() - 1)) > 0) {
            return sorted.get(sorted.size() - 1);
        }
        // 查看是否，如果比最小的小，使用最小值，如果比最大的大，使用最大值，如果在大小之间，取小的那一个值.
        for (int i = 0; i < sorted.size() - 1; i++) {
            BigDecimal min = sorted.get(i);
            BigDecimal max = sorted.get(i + 1);
            if (finalBetAmount.compareTo(min) >= 0 && finalBetAmount.compareTo(max) < 0) {
                // 在区间内 → 取较小的那个值,离那个最接近， 取那个值，比方 【0.2 0.4】 如果是0.3，取0.2。就是如果跟 max min 差值一样，取最小值，如果跟max min 那个近取那个
                BigDecimal diffToMin = finalBetAmount.subtract(min).abs();
                BigDecimal diffToMax = finalBetAmount.subtract(max).abs();
                if (diffToMin.compareTo(diffToMax) <= 0) {
                    return min;
                }
                return max;
            }
        }

        // 理论不会走到这里，兜底返回最小值
        return sorted.get(0);

    }

    private void joinActivityValid(SiteActivityBasePO siteActivityBasePO, List<ActivityFreeGameVO> freeGameVOList, Map<String, UserInfoVO> userId2Info) {
        String activityId = siteActivityBasePO.getId();
        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchIp())) {
            // 同登录IP只能1次
            List<String> ips = Lists.newArrayList();
            for (ActivityFreeGameVO activityFreeGameVO : freeGameVOList) {
                String userId = activityFreeGameVO.getUserId();
                UserInfoVO userInfoVO = userId2Info.get(userId);
                String ip = userInfoVO.getLastLoginIp();
                if (StringUtils.isNotEmpty(ip)) {
                    ips.add(ip);
                }
            }
            List<SiteActivityFreeGameRecordPO> list = siteActivityFreeGameRecordService.list(Wrappers.<SiteActivityFreeGameRecordPO>lambdaQuery().select(SiteActivityFreeGameRecordPO::getIp).in(SiteActivityFreeGameRecordPO::getIp, ips).eq(SiteActivityFreeGameRecordPO::getActivityId, activityId));
            if (CollectionUtil.isNotEmpty(list)) {
                List<String> existIps = list.stream().map(SiteActivityFreeGameRecordPO::getIp).distinct().toList();
                freeGameVOList.removeIf(e -> existIps.contains(userId2Info.get(e.getUserId()).getLastLoginIp()));
            }
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchEmail())) {
            // 完成邮箱绑定才能参与
            freeGameVOList.removeIf(e -> userId2Info.get(e.getUserId()).getEmail() == null);
        }
        if (EnableStatusEnum.ENABLE.getCode().equals(siteActivityBasePO.getSwitchPhone())) {
            // 完成手机号绑定才能参与
            freeGameVOList.removeIf(e -> userId2Info.get(e.getUserId()).getPhone() == null);
        }
    }


    @KafkaListener(topics = TopicsConstants.FREE_GAME_RECORD, groupId = GroupConstants.FREE_GAME_RECORD_GROUP)
    public void freeGameChange(FreeGameRecordTriggerVO triggerVO, Acknowledgment ackItem) {
        log.info("接收到免费游戏旋转记录消息:{}", JSON.toJSONString(triggerVO));
        List<FreeGameRecordVO> freeGameVOList = triggerVO.getFreeGameVOList();
        if (CollectionUtil.isEmpty(freeGameVOList)) {
            ackItem.acknowledge();
            return;
        }

        List<String> orderNos = freeGameVOList.stream().map(FreeGameRecordVO::getOrderNo).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(orderNos)) {
            List<SiteActivityFreeGameRecordPO> list = siteActivityFreeGameRecordService.list(Wrappers.<SiteActivityFreeGameRecordPO>lambdaQuery().select(SiteActivityFreeGameRecordPO::getOrderNo).in(SiteActivityFreeGameRecordPO::getOrderNo, orderNos));
            if (CollectionUtil.isNotEmpty(list)) {
                List<String> existOrderNo = list.stream().map(SiteActivityFreeGameRecordPO::getOrderNo).distinct().collect(Collectors.toList());
                freeGameVOList.removeIf(e -> existOrderNo.contains(e.getOrderNo()));
            }
        }
        if (CollectionUtil.isEmpty(freeGameVOList)) {
            ackItem.acknowledge();
            return;
        }

        List<FreeGameRecordVO> errVOS = Lists.newArrayList();
        for (FreeGameRecordVO freeGameRecord : freeGameVOList) {
            RLock rLock = RedisUtil.getLock(RedisKeyTransUtil.getFreeGameRecordChange(freeGameRecord.getSiteCode(), freeGameRecord.getUserId()));
            try {
                if (!rLock.tryLock(2, TimeUnit.SECONDS)) {
                    // 任务抛出后其他继续执行
                    log.error("免费游戏账变处理获取锁失败，详情：{}", freeGameRecord);
                    errVOS.add(freeGameRecord);
                    continue;
                }
                SiteActivityFreeGameRecordPO recordPO = new SiteActivityFreeGameRecordPO();
                // 获取最新游戏记录，都是插入，因为每一次赠送都是独立的
                //List<SiteActivityFreeGameRecordPO> recordPOS = siteActivityFreeGameRecordService.getLatestUserRecord(Lists.newArrayList(freeGameRecord.getUserId()), freeGameRecord.getSiteCode(), freeGameRecord.getVenueCode());
                //int latestCount = getFreeGameCount(freeGameRecord.getSiteCode(), freeGameRecord.getUserId(), freeGameRecord.getVenueCode());
                Integer latestCount = 0;
                BeanUtil.copyProperties(freeGameRecord, recordPO);

                /*if (CollectionUtil.isNotEmpty(recordPOS)) {
                    latestCount = recordPOS.get(0).getAfterNum();
                }*/

                recordPO.setOrderTime(System.currentTimeMillis());
                recordPO.setBeforeNum(latestCount);
                //int afterNum = getAfterNum(freeGameRecord.getType(), latestCount, freeGameRecord.getAcquireNum());
                recordPO.setAfterNum(freeGameRecord.getAcquireNum());
                // 默认
                recordPO.setOrderType(FreeGameChangeTypeEnum.ACTIVITY_ADD.getCode());
                // 添加 流水倍数，
                UserInfoVO userInfoVO = userInfoApi.getUserInfoByUserId(recordPO.getUserId());

                log.info("免费游戏处理成功:{}", freeGameRecord);
                siteActivityFreeGameRecordService.save(recordPO);
                //siteActivityFreeGameRecordService.updateFreeGameRecord(freeGameRecord);
            } catch (Exception e) {
                log.error("免费游戏处理失败:{}", freeGameRecord);
                log.error("免费游戏账变处理获取锁失败", e);
                //  throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            } finally {
                if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
                //异常记录日志 排查问题修复 不需要重新发送
                ackItem.acknowledge();
            }
            // 本轮异常的任务投掷下轮继续执行
           /* if (CollectionUtil.isNotEmpty(errVOS)) {
                FreeGameRecordTriggerVO freeGameRecordTriggerVO = new FreeGameRecordTriggerVO();
                freeGameRecordTriggerVO.setFreeGameVOList(errVOS);
                KafkaUtil.send(TopicsConstants.FREE_GAME_RECORD, freeGameRecordTriggerVO);
            }*/
        }
    }

    private Integer getAfterNum(Integer type, Integer latestCount, Integer acquireNum) {
        FreeGameChangeTypeEnum freeGameType = FreeGameChangeTypeEnum.nameOfCode(type);
        return switch (freeGameType) {
            case USED -> latestCount - acquireNum;
            case ACTIVITY_ADD -> latestCount + acquireNum;
            case CONFIG_ADD -> latestCount + acquireNum;
        };
    }


    @KafkaListener(topics = TopicsConstants.FREE_GAME_RECORD_CONSUME, groupId = GroupConstants.FREE_GAME_CONSUME_RECORD_GROUP)
    public void freeGameConsume(PPFreeGameRecordReqVO sendVO, Acknowledgment ackItem) {
        log.info("免费旋转消费次数:{}", JSON.toJSONString(sendVO));
        try {
            // 免费旋转中奖或者消耗情况
            siteActivityFreeGameRecordService.updateFreeGameBalanceForPPConsume(sendVO);
        } catch (Exception e) {
            log.error("免费旋转消费次数，信息：{}", JSON.toJSONString(sendVO), e);
        } finally {
            ackItem.acknowledge();
        }
    }


}
