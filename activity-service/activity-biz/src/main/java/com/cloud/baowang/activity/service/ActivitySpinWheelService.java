package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.config.ThreadPoolConfig;
import com.cloud.baowang.activity.po.*;
import com.cloud.baowang.activity.repositories.*;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ActivitySpinWheelReqVO;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.api.medal.MedalAcquireApi;
import com.cloud.baowang.user.api.api.medal.MedalAcquireRecordApi;
import com.cloud.baowang.user.api.api.vip.VipGradeApi;
import com.cloud.baowang.user.api.api.vip.VipRankApi;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireRecordCondReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.medal.SiteMedalInfoRespVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPGradeVO;
import com.cloud.baowang.user.api.vo.vip.SiteVIPRankVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyToTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @className: ActivitySpinWheelService
 * @author: wade
 * @description: 转盘活动
 * @date: 13/9/24 11:36
 */
@Slf4j
@Service
@AllArgsConstructor
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ActivitySpinWheelService {
    private final ActivitySpinWheelRepository activitySpinWheelRepository;
    private final SiteActivityRewardSpinWheelRepository rewardSpinWheelRepository;

    private final SiteActivityRewardVipGradeRepository vipGradeRepository;

    private final SiteActivityLotteryRecordRepository lotteryRecordRepository;

    private final SiteActivityLotteryBalanceRepository lotteryBalanceRepository;

    private final SiteActivityBaseRepository baseRepository;

    private final UserInfoApi userInfoApi;

    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    private final VipGradeApi vipGradeApi;

    private final VipRankApi vipRankApi;

    private final SiteActivityOrderRecordService orderRecordService;

    private final SiteApi siteApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final MedalAcquireRecordApi medalAcquireRecordApi;

    private final MedalAcquireApi medalAcquireApi;

    private final AgentInfoApi agentInfoApi;

    private final ActivitySpinWheelService _this;

    /**
     * 充值活动奖励转盘次数
     */
    public boolean validateAndReward(RechargeTriggerVO trigger) {
        boolean lock;
        RLock fairLock = RedisUtil.getFairLock(String.format(RedisConstants.ACTIVITY_SPIN_WHEEL_GET_REWARD_LOCK, trigger.getUserId()));
        try {
            log.info("转盘活动奖励：触发 ,参数:{}", JSONObject.toJSONString(trigger));
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                handleDepositReward(trigger);
                fairLock.unlock();

            }

        } catch (Exception e) {
            log.error("转盘活动奖励表发生异常", e);
            return false;
        } finally {
            if (fairLock.isLocked()) fairLock.unlock();
        }
        return true;
    }

    /**
     * 每笔存款触发
     *
     * @param trigger 触发参数
     */
    public void handleDepositReward(RechargeTriggerVO trigger) {
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(trigger.getSiteCode());
        // 根据站点编码（siteCode）获取有效的  活动基础配置信息。且在活动开始与结束时间
        if (siteActivityBasePO == null) {
            log.info("转盘活动没有配置");
            return;
        }
        // 用户信息
        UserInfoVO userInfoVO = getUserInfo(trigger.getUserId());
        /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(userInfoVO.getAccountType())) {
            return;
        }*/
        // 判断该用户是否有资格参与不判断
        // 获取该站点的时区
        String timeZone = siteApi.getSiteInfo(trigger.getSiteCode()).getData().getTimezone();
        // 根据配置查询奖励
        LambdaQueryWrapper<ActivitySpinWheelPO> spinWheelWrapper = new LambdaQueryWrapper<>();
        spinWheelWrapper.eq(ActivitySpinWheelPO::getSiteCode, trigger.getSiteCode());
        spinWheelWrapper.eq(ActivitySpinWheelPO::getBaseId, siteActivityBasePO.getId());
        ActivitySpinWheelPO activitySpinWheelPO = activitySpinWheelRepository.selectOne(spinWheelWrapper);
        // 金额转换
        BigDecimal depositTargetAmount = activitySpinWheelPO.getDepositAmount();
        if(depositTargetAmount == null){
            depositTargetAmount = BigDecimal.ZERO;
        }
        if (depositTargetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("转盘活动没有配置充值金额");
            return;
        }
        String currencyCode = trigger.getCurrencyCode();
        if (trigger.getRechargeAmount() == null) {
            log.info("会员充值金额为0，数据{}", JSONObject.toJSONString(trigger));
            return;
        }
        PlatCurrencyToTransferVO platCurrencyToTransferVO = PlatCurrencyToTransferVO.builder()
                .siteCode(trigger.getSiteCode()).sourceCurrencyCode(currencyCode).sourceAmt(trigger.getRechargeAmount()).build();
        ResponseVO<BigDecimal> bigDecimalResponseVO = siteCurrencyInfoApi.transferMainCurrencyToPlat(platCurrencyToTransferVO);
        if (!bigDecimalResponseVO.isOk()) {
            log.error("转盘活动获取汇率转换异常，{}", JSONObject.toJSONString(trigger));
            return;
        }
        BigDecimal platFormDepositAmount = bigDecimalResponseVO.getData();


        // 判断是是否达到了给奖励金额
        if (platFormDepositAmount == null
                || platFormDepositAmount.compareTo(depositTargetAmount) < 0) {
            log.info("转盘活动没有配置充值金额大于充值金额");
            return;
        }
        // 判断该活动是否给了奖励
        SiteActivityLotteryRecordPO lotteryRecord = getLotteryRecord(trigger.getOrderNumber());
        if (lotteryRecord != null) {
            log.info("该充值活动已经给了奖励，充值订单{}", trigger.getOrderNumber());
            return;
        }

        // 每日可量取上限次数
        Integer maxTimeType = activitySpinWheelPO.getMaxTimeType();
        // 配置的奖励次数
        Integer addConfigCount = activitySpinWheelPO.getDepositTimes() == null ? 0 : activitySpinWheelPO.getDepositTimes();
        if (addConfigCount == null || addConfigCount == 0) {
            log.info("该充值活动没有配置奖励，充值订单{}", trigger.getOrderNumber());
            return;
        }
        // 当天获取的奖励次数
        Integer rewardTodayCount = getTodayRewardCount(trigger.getUserId(), timeZone);
        // 获取配置的次数
        Integer maxLimitCount;
        Integer vipGradeCode = userInfoVO.getVipGradeCode() == null ? 0 : userInfoVO.getVipGradeCode();
        if (maxTimeType != null && maxTimeType.equals(CommonConstant.business_zero)) {
            maxLimitCount = activitySpinWheelPO.getMaxTimes();
        } else {
            maxLimitCount = getVipConfigCount(vipGradeCode, siteActivityBasePO.getId());
        }
        Integer insertCount = getInsertRewardCount(addConfigCount, rewardTodayCount, maxLimitCount);
        if (insertCount == 0) {
            log.info("该会员今天领取达到上线，充值订单{}", trigger.getOrderNumber());
            return;
        }
        // 查找余额表
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository.selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                .eq(SiteActivityLotteryBalancePO::getUserId, trigger.getUserId()));
        // 查找，如果没有，则生成一个记录
        if (lotteryBalancePO == null) {
            lotteryBalancePO = initLotteryUserInfo(userInfoVO);
        }
        //添加奖励，解锁
        SiteActivityLotteryRecordPO insertRecord = new SiteActivityLotteryRecordPO();
        insertRecord.setSiteCode(trigger.getSiteCode());
        insertRecord.setVipGradeCode(vipGradeCode);
        insertRecord.setVipRankCode(userInfoVO.getVipRank());
        insertRecord.setActivityId(siteActivityBasePO.getId());
        insertRecord.setActivityNo(siteActivityBasePO.getActivityNo());
        insertRecord.setActivityTemplate(siteActivityBasePO.getActivityTemplate());
        insertRecord.setActivityTemplateName(siteActivityBasePO.getActivityNameI18nCode());
        insertRecord.setPrizeSource(ActivityPrizeSourceEnum.DEPOSIT.getType());
        insertRecord.setOperationType(ActivityOperationTypeEnum.INCREASE.getType());
        insertRecord.setStartCount(lotteryBalancePO.getBalance());
        insertRecord.setRewardCount(insertCount);
        insertRecord.setUserId(trigger.getUserId());
        insertRecord.setUserAccount(trigger.getUserAccount());
        insertRecord.setAccountType(userInfoVO.getAccountType());
        insertRecord.setOrderNumber(trigger.getOrderNumber());
        insertRecord.setEndCount(lotteryBalancePO.getBalance() + insertCount);
        lotteryRecordRepository.insert(insertRecord);
        // 更新余额
        LambdaUpdateWrapper<SiteActivityLotteryBalancePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityLotteryBalancePO::getUserId, trigger.getUserId());
        updateWrapper.set(SiteActivityLotteryBalancePO::getUpdatedTime, System.currentTimeMillis());
        // 使用 SQL 表达式更新余额
        updateWrapper.setSql("balance = balance + " + insertCount);
        lotteryBalanceRepository.update(null, updateWrapper);
    }

    /*@Scheduled(cron = "0 40 11 * * ?")
    public void test(){
        handleJob("Vd438R");
    }*/

    /**
     * 定时任务处理 处理奖励，流水是否达到获取奖励， 当天跑昨天的数据
     */
    public void handleJob(String siteCode) {
        // 查询活动配置
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(siteCode);
        if (siteActivityBasePO == null) {
            return;
        }
        ActivitySpinWheelPO activitySpinWheelPO = getSiteActivitySpinWheelConfig(siteCode, siteActivityBasePO.getId());
        // 查询配置最大次数
        Map<Integer, SiteActivityRewardVipGradePO> vipGradePOMap = getVipGradePOMap(siteActivityBasePO.getId());
        // 查询个人，分页查询符合条件的个人
        ActivitySpinWheelReqVO reqVO = buildQueryReportWinLose(siteCode, activitySpinWheelPO.getBetAmount());
        BigDecimal platFormBetAmount = activitySpinWheelPO.getBetAmount();
        // 获取所有汇率 AmountUtils
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);

        // 初始化
        reqVO.setPageSize(1000); // 每次查询100个
        reqVO.setPageNumber(1);// 第一页开始
        boolean hasNextPage;
        do {
            // 查询个人，分页查询符合条件的个人
            // Page<ActivitySpinWheelResVO> resVOPage = reportUserVenueWinLoseApi.querySpinWheelUserList(reqVO);
            // 查询任务完成情况
            String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
            Long timeStart = TimeZoneUtils.getStartOfYesterdayInTimeZone(System.currentTimeMillis(), timeZone);
            Long timeEnd = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), timeZone);

            Page<ReportUserVenueBetsTopVO> venueBetsTopVOPage = queryUserBetsTop(siteCode, timeStart, timeEnd);
            for (ReportUserVenueBetsTopVO activitySpinWheelResVO : venueBetsTopVOPage.getRecords()) {
                // 处理每条记录
                BigDecimal finalRate = allFinalRate.get(activitySpinWheelResVO.getCurrency());
                if (finalRate == null) {
                    continue;
                }
                BigDecimal targetCurrencyAmount = AmountUtils.multiply(platFormBetAmount, finalRate);
                // 判断是否达标
                if (targetCurrencyAmount.compareTo(activitySpinWheelResVO.getValidAmount()) <= 0) {
                    log.info("转盘投注流水达标的用户{}", activitySpinWheelResVO.getUserId());
                    handleBetReward(activitySpinWheelResVO.getUserId(), vipGradePOMap, siteCode, activitySpinWheelPO, timeZone);
                }
            }
            hasNextPage = venueBetsTopVOPage.hasNext(); // 检查是否有下一页
            // 更新分页对象的当前页码
            reqVO.setPageNumber((int) venueBetsTopVOPage.getCurrent() + 1);
        } while (hasNextPage);


    }

    /**
     * 符合条件的 userId
     */
    private List<ReportUserVenueBetsTopVO> queryUserBetsTop(String siteCode, List<String> userIds, Long timeStart, long timeEnd) {
        ReportUserTopReqVO userTopReqVO = new ReportUserTopReqVO();
        userTopReqVO.setSiteCode(siteCode);
        userTopReqVO.setUserIdList(userIds);
        userTopReqVO.setStartTime(timeStart);
        userTopReqVO.setEndTime(timeEnd);
        Page<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOPage = reportUserVenueFixedWinLoseApi.queryUserBetsTop(userTopReqVO);
        return Optional.ofNullable(ReportUserVenueBetsTopVOPage)
                .map(Page::getRecords).filter(records -> !records.isEmpty())
                .orElse(Collections.emptyList());
    }


    /**
     * kafka 消息消费处理奖励，流水是否达到获取奖励， 当天跑昨天的数据
     */
    public boolean processBetAward(UserVenueWinLossSendVO sendVO) {
        if (CollectionUtil.isEmpty(sendVO.getVoList())) {
            return false;
        }
        List<String> siteCodes = sendVO.getVoList().stream().map(UserVenueWinLossMqVO::getSiteCode).distinct().collect(Collectors.toList());
        for (String siteCode : siteCodes) {
            // 查询活动配置
            SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(siteCode);
            if (siteActivityBasePO == null) {
                continue;
            }

            List<String> userIds = sendVO.getVoList().stream()
                    .filter(e -> e.getSiteCode().equals(siteCode))
                    .map(UserVenueWinLossMqVO::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            log.info("处理流程 转盘活动奖励次数{}", siteCode);
            //
            userIds = checkJoinTaskUserId(userIds);
            if (CollectionUtil.isEmpty(userIds)) {
                log.info("处理流程 转盘活动奖励次数,上级代理设置了不参与转盘奖励");
                continue;
            }
            log.info("处理流程 转盘活动奖励次数,userId:{}", JSONObject.toJSONString(userIds));
            // 获取时间
            String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
            Long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            Long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            ActivitySpinWheelPO activitySpinWheelPO = getSiteActivitySpinWheelConfig(siteCode, siteActivityBasePO.getId());
            // 查询配置最大次数
            Map<Integer, SiteActivityRewardVipGradePO> vipGradePOMap = getVipGradePOMap(siteActivityBasePO.getId());
            // 查询个人，分页查询符合条件的个人
            List<ReportUserVenueBetsTopVO> queryUserBetsTop = queryUserBetsTop(siteCode, userIds, timeStart, timeEnd);
            if (CollectionUtil.isEmpty(queryUserBetsTop)) {
                continue;
            }
            BigDecimal platFormBetAmount = activitySpinWheelPO.getBetAmount();
            // 获取所有汇率 AmountUtils
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
            for (ReportUserVenueBetsTopVO activitySpinWheelResVO : queryUserBetsTop) {
                // 处理每条记录
                BigDecimal finalRate = allFinalRate.get(activitySpinWheelResVO.getCurrency());
                if (finalRate == null) {
                    continue;
                }
                // 换算成当前用户的货币金额
                BigDecimal targetCurrencyAmount = AmountUtils.multiply(platFormBetAmount, finalRate);
                // 判断是否达标
                if (targetCurrencyAmount.compareTo(activitySpinWheelResVO.getValidAmount()) <= 0) {
                    log.info("转盘投注流水达标的用户{}", activitySpinWheelResVO.getUserId());
                    handleBetReward(activitySpinWheelResVO.getUserId(), vipGradePOMap, siteCode, activitySpinWheelPO, timeZone);
                }
            }
        }
        return true;
    }

    // 提取公共查询逻辑
    private Page<ReportUserVenueBetsTopVO> queryUserBetsTop(String siteCode, Long timeStart, long timeEnd) {
        ReportUserTopReqVO userTopReqVO = new ReportUserTopReqVO();
        userTopReqVO.setSiteCode(siteCode);
        userTopReqVO.setStartTime(timeStart);
        userTopReqVO.setEndTime(timeEnd);
        return reportUserVenueFixedWinLoseApi.queryUserBetsTop(userTopReqVO);
    }

    public Map<Integer, SiteActivityRewardVipGradePO> getVipGradePOMap(String baseId) {
        // 获取配置的次数
        LambdaQueryWrapper<SiteActivityRewardVipGradePO> rewardVipGradeWrapper = new LambdaQueryWrapper<>();
        rewardVipGradeWrapper.eq(SiteActivityRewardVipGradePO::getBaseId, baseId);
        List<SiteActivityRewardVipGradePO> pos = vipGradeRepository.selectList(rewardVipGradeWrapper);
        Map<Integer, SiteActivityRewardVipGradePO> vipGradePOMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(pos)) {
            vipGradePOMap = pos.stream().collect(Collectors
                    .toMap(SiteActivityRewardVipGradePO::getVipGradeCode, Function.identity(), (k1, k2) -> k2));
        }
        return vipGradePOMap;
    }

    private ActivitySpinWheelReqVO buildQueryReportWinLose(String siteCode, BigDecimal betAmount) {
        ActivitySpinWheelReqVO reqVO = new ActivitySpinWheelReqVO();
        reqVO.setSiteCode(siteCode);
        reqVO.setLimitAmount(betAmount);
        return reqVO;
    }

    /**
     * 处理当日投注流水,用户投注流水都达标了
     *
     * @param userId        用户id
     * @param vipGradePOMap // 获取配置的次数
     * @param siteCode      siteCode
     */
    @DistributedLock(name = RedisConstants.REWARD_SPIN_WHEEL_LOCK, unique = "#userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public void handleBetReward(String userId, Map<Integer, SiteActivityRewardVipGradePO> vipGradePOMap,
                                String siteCode, ActivitySpinWheelPO activitySpinWheelPO, String timeZone) {
        log.info("转盘送次数，流水触发奖励触发:{}", userId);
        String template = ActivityTemplateEnum.SPIN_WHEEL.getSerialNo();
        // 根据userId，当天时间，sitecode, 生成一个orderNumbber ，每天只会生成一个
        String orderNumber = generatorAmountOrderNumber(userId, template, timeZone);
        // 判断该活动今天是否给了奖励
        SiteActivityLotteryRecordPO lotteryRecordPO = getLotteryRecord(orderNumber);
        if (lotteryRecordPO != null) {
            log.info("转盘送次数，流水触发奖励今日已经发放{}", userId);
            return;
        }
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(siteCode);
        // 根据站点编码（siteCode）获取有效的  活动基础配置信息。且在活动开始与结束时间
        if (siteActivityBasePO == null) {
            log.info("转盘活动没有配置");
            return;
        }
        // 用户信息
        UserInfoVO userInfoVO = getUserInfo(userId);
        // 每日可量取上限次数类型
        Integer maxTimeType = activitySpinWheelPO.getMaxTimeType();
        // 配置的奖励次数
        Integer addConfigCount = activitySpinWheelPO.getBetTimes();
        if (addConfigCount == null || addConfigCount == 0) {
            log.info("转盘活动没有配置奖励次数0");
            return;
        }
        // 当天获取的奖励次数
        String timezone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
        Integer rewardTodayCount = getYesterdayRewardCount(userId, timezone);
        // 获取配置的次数
        Integer maxLimitCount;
        Integer vipGradeCode = userInfoVO.getVipGradeCode() == null ? 0 : userInfoVO.getVipGradeCode();
        if (maxTimeType != null && maxTimeType.equals(CommonConstant.business_zero)) {
            maxLimitCount = activitySpinWheelPO.getMaxTimes();
        } else {
            maxLimitCount = Optional.ofNullable(vipGradePOMap.get(vipGradeCode))
                    .map(SiteActivityRewardVipGradePO::getRewardCount)
                    .orElse(0);
        }
        Integer insertCount = getInsertRewardCount(addConfigCount, rewardTodayCount, maxLimitCount);
        if (insertCount == 0) {
            return;
        }
        // 查找余额表
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository.selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                .eq(SiteActivityLotteryBalancePO::getUserId, userId));
        // 查找，如果没有，则生成一个记录
        if (lotteryBalancePO == null) {
            lotteryBalancePO = initLotteryUserInfo(userInfoVO);
        }
        //添加奖励，解锁
        SiteActivityLotteryRecordPO insertRecord = new SiteActivityLotteryRecordPO();
        insertRecord.setSiteCode(siteCode);
        insertRecord.setActivityId(siteActivityBasePO.getId());
        insertRecord.setActivityNo(siteActivityBasePO.getActivityNo());
        insertRecord.setActivityTemplate(siteActivityBasePO.getActivityTemplate());
        insertRecord.setActivityTemplateName(siteActivityBasePO.getActivityNameI18nCode());
        insertRecord.setVipGradeCode(vipGradeCode);
        insertRecord.setVipRankCode(userInfoVO.getVipRank());
        insertRecord.setPrizeSource(ActivityPrizeSourceEnum.BET.getType());
        insertRecord.setOperationType(ActivityOperationTypeEnum.INCREASE.getType());
        insertRecord.setStartCount(lotteryBalancePO.getBalance());
        insertRecord.setRewardCount(insertCount);
        insertRecord.setEndCount(lotteryBalancePO.getBalance() + insertCount);
        insertRecord.setUserId(userId);
        insertRecord.setUserAccount(userInfoVO.getUserAccount());
        insertRecord.setAccountType(userInfoVO.getAccountType());
        insertRecord.setOrderNumber(orderNumber);
        lotteryRecordRepository.insert(insertRecord);
        log.info("转盘送次数，流水触发奖励：{}", JSONObject.toJSONString(insertRecord));
        // 更新余额
        updateUserBalance(userId, insertCount, true);
    }

    /**
     * vip晋级添加奖励次数
     */
    @DistributedLock(name = RedisConstants.REWARD_SPIN_WHEEL_LOCK, unique = "#vipUpRewardResVO.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class)
    @Async(ThreadPoolConfig.TASK_EXECUTOR)
    public void handleVipReward(VipUpRewardResVO vipUpRewardResVO) {
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(vipUpRewardResVO.getSiteCode());
        // 根据站点编码（siteCode）获取有效的  活动基础配置信息。且在活动开始与结束时间
        if (siteActivityBasePO == null) {
            log.info("转盘活动没有配置");
            return;
        }

        // 查找余额表
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository.selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                .eq(SiteActivityLotteryBalancePO::getUserId, vipUpRewardResVO.getUserId()));
        // 查找，如果没有，则生成一个记录
        UserInfoVO userInfoVO = userInfoApi.getByUserId(vipUpRewardResVO.getUserId());
        /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(userInfoVO.getAccountType())) {
            log.info("转盘活动没有配置,测试账号不发送转盘次数,userId:{}", userInfoVO.getUserAccount());
            return;
        }*/
        if (lotteryBalancePO == null) {
            lotteryBalancePO = initLotteryUserInfo(userInfoVO);
        }
        //添加奖励，解锁
        SiteActivityLotteryRecordPO insertRecord = new SiteActivityLotteryRecordPO();
        insertRecord.setSiteCode(vipUpRewardResVO.getSiteCode());
        insertRecord.setVipGradeCode(vipUpRewardResVO.getVipGradeCode());
        insertRecord.setVipRankCode(vipUpRewardResVO.getVipRankCode());
        insertRecord.setPrizeSource(ActivityPrizeSourceEnum.VIP.getType());
        insertRecord.setOperationType(ActivityOperationTypeEnum.INCREASE.getType());
        insertRecord.setStartCount(lotteryBalancePO.getBalance());
        insertRecord.setRewardCount(vipUpRewardResVO.getRewardCounts());
        insertRecord.setEndCount(lotteryBalancePO.getBalance() + vipUpRewardResVO.getRewardCounts());
        insertRecord.setUserId(vipUpRewardResVO.getUserId());
        insertRecord.setUserAccount(vipUpRewardResVO.getUserAccount());
        insertRecord.setAccountType(userInfoVO.getAccountType());
        insertRecord.setOrderNumber(vipUpRewardResVO.getOrderNumber());
        insertRecord.setActivityId(siteActivityBasePO.getId());
        insertRecord.setActivityNo(siteActivityBasePO.getActivityNo());
        insertRecord.setActivityTemplate(siteActivityBasePO.getActivityTemplate());
        insertRecord.setActivityTemplateName(siteActivityBasePO.getActivityNameI18nCode());
        lotteryRecordRepository.insert(insertRecord);
        // 更新余额
        updateUserBalance(vipUpRewardResVO.getUserId(), vipUpRewardResVO.getRewardCounts(), true);
    }

    /**
     * 签到活动添加晋级添加奖励次数
     */
    @DistributedLock(name = RedisConstants.REWARD_SPIN_WHEEL_LOCK, unique = "#vipUpRewardResVO.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    @Transactional(rollbackFor = Exception.class)
    @Async(ThreadPoolConfig.TASK_EXECUTOR)
    public void handleCheckInReward(VipUpRewardResVO vipUpRewardResVO) {
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(vipUpRewardResVO.getSiteCode());
        // 根据站点编码（siteCode）获取有效的  活动基础配置信息。且在活动开始与结束时间
        if (siteActivityBasePO == null) {
            log.info("转盘活动没有配置");
            return;
        }

        // 查找余额表
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository.selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                .eq(SiteActivityLotteryBalancePO::getUserId, vipUpRewardResVO.getUserId()));
        // 查找，如果没有，则生成一个记录
        UserInfoVO userInfoVO = userInfoApi.getByUserId(vipUpRewardResVO.getUserId());
        /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(userInfoVO.getAccountType())) {
            log.info("转盘活动没有配置,测试账号不发送转盘次数,userId:{}", userInfoVO.getUserAccount());
            return;
        }*/
        if (lotteryBalancePO == null) {
            lotteryBalancePO = initLotteryUserInfo(userInfoVO);
        }
        //添加奖励，解锁
        SiteActivityLotteryRecordPO insertRecord = new SiteActivityLotteryRecordPO();
        insertRecord.setSiteCode(vipUpRewardResVO.getSiteCode());
        insertRecord.setVipGradeCode(vipUpRewardResVO.getVipGradeCode());
        insertRecord.setVipRankCode(vipUpRewardResVO.getVipRankCode());
        insertRecord.setPrizeSource(ActivityPrizeSourceEnum.CHECKIN.getType());
        insertRecord.setOperationType(ActivityOperationTypeEnum.INCREASE.getType());
        insertRecord.setStartCount(lotteryBalancePO.getBalance());
        insertRecord.setRewardCount(vipUpRewardResVO.getRewardCounts());
        insertRecord.setEndCount(lotteryBalancePO.getBalance() + vipUpRewardResVO.getRewardCounts());
        insertRecord.setUserId(vipUpRewardResVO.getUserId());
        insertRecord.setUserAccount(vipUpRewardResVO.getUserAccount());
        insertRecord.setAccountType(userInfoVO.getAccountType());
        insertRecord.setOrderNumber(vipUpRewardResVO.getOrderNumber());
        insertRecord.setActivityId(siteActivityBasePO.getId());
        insertRecord.setActivityNo(siteActivityBasePO.getActivityNo());
        insertRecord.setActivityTemplate(siteActivityBasePO.getActivityTemplate());
        insertRecord.setActivityTemplateName(siteActivityBasePO.getActivityNameI18nCode());
        lotteryRecordRepository.insert(insertRecord);
        // 更新余额
        updateUserBalance(vipUpRewardResVO.getUserId(), vipUpRewardResVO.getRewardCounts(), true);
    }


    /**
     * 更新抽奖次数余额表
     *
     * @param userId      用户ID
     * @param insertCount 更改次数
     * @param flag        增加还是修改
     */
    public void updateUserBalance(String userId, Integer insertCount, boolean flag) {
        LambdaUpdateWrapper<SiteActivityLotteryBalancePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteActivityLotteryBalancePO::getUserId, userId);
        updateWrapper.set(SiteActivityLotteryBalancePO::getUpdatedTime, System.currentTimeMillis());
        // 使用 SQL 表达式更新余额
        if (flag) {
            updateWrapper.setSql("balance = balance + " + insertCount);
        } else {
            updateWrapper.setSql("balance = balance - " + insertCount);
        }
        log.info("更改转盘结果userId:{},奖励次数:{}", userId, insertCount);
        lotteryBalanceRepository.update(null, updateWrapper);
    }

    public SiteActivityLotteryRecordPO getLotteryRecord(String orderNumber) {
        LambdaQueryWrapper<SiteActivityLotteryRecordPO> lotteryRecordWrapper = new LambdaQueryWrapper<>();
        lotteryRecordWrapper.eq(SiteActivityLotteryRecordPO::getOrderNumber, orderNumber);
        return lotteryRecordRepository.selectOne(lotteryRecordWrapper);
    }

    /**
     * 生成唯一的订单号。
     * <p>
     * 订单号的格式通常包含站点代码、用户ID、活动基础ID以及当前日期，确保每个订单号是唯一的。
     *
     * @param userId 用户的唯一标识符，用于生成订单号的组成部分。
     * @param baseId 活动基础ID，用于关联具体的活动或业务逻辑。
     * @return 返回生成的唯一订单号，格式为：站点代码 + 用户ID + 活动基础ID + 当前日期。
     */
    private String generatorAmountOrderNumber(String userId, String baseId, String timeZone) {
        String dateStr = TimeZoneUtils.formatTimestampToTimeZoneYyyyMMdd(System.currentTimeMillis(), timeZone);
        //String dateStr = DateUtils.formatToDateTime(System.currentTimeMillis(), DateUtils.yyyyMMdd);
        return OrderNoUtils.genOrderNo(userId, baseId, dateStr);
    }


    /**
     * 根据站点编码（siteCode）获取有效的  活动基础配置信息。且在活动开始与结束时间
     *
     * @param siteCode 站点编码，用于过滤站点特定的活动配置。
     * @return SiteActivityBasePO 返回符合条件的站点活动基础配置对象，或 null 如果未找到匹配的记录。
     */
    public SiteActivityBasePO getSiteActivityBasePO(String siteCode) {
        LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode)
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.SPIN_WHEEL.getType())
                //活动是永久的
                //.ge(,SiteActivityBasePO::getActivityEndTime, System.currentTimeMillis())
                .le(SiteActivityBasePO::getActivityStartTime, System.currentTimeMillis());
        SiteActivityBasePO record = baseRepository.selectOne(queryWrapper);
        if (record == null) {
            return null;
        }
        if (Objects.equals(record.getActivityDeadline(), ActivityDeadLineEnum.LONG_TERM.getType())) {
            return record;
        } else {
            return record.getActivityEndTime() > System.currentTimeMillis() ? record : null;
        }
    }

    /**
     * 根据站点编码（siteCode）获取有效的  活动基础配置信息，
     *
     * @param siteCode 站点编码，用于过滤站点特定的活动配置。
     * @return SiteActivityBasePO 返回符合条件的站点活动基础配置对象，或 null 如果未找到匹配的记录。
     */
    public SiteActivityBasePO getSiteActivityBasePORecord(String siteCode) {
        LambdaQueryWrapper<SiteActivityBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode)
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode())
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.SPIN_WHEEL.getType());

        return baseRepository.selectOne(queryWrapper);
    }


    /**
     * 获取站点对应的转盘活动配置。
     * <p>
     * 根据站点代码（siteCode）和基础活动ID（baseId），从数据库中查询对应的转盘活动配置。
     *
     * @param siteCode 站点代码，用于区分不同站点的活动配置。
     * @param baseId   活动基础ID，用于定位具体的转盘活动。
     * @return 返回对应站点和活动ID的转盘活动配置，如果未找到则返回 null。
     */
    public ActivitySpinWheelPO getSiteActivitySpinWheelConfig(String siteCode, String baseId) {
        LambdaQueryWrapper<ActivitySpinWheelPO> spinWheelWrapper = new LambdaQueryWrapper<>();
        spinWheelWrapper.eq(ActivitySpinWheelPO::getSiteCode, siteCode);
        spinWheelWrapper.eq(ActivitySpinWheelPO::getBaseId, baseId);
        return activitySpinWheelRepository.selectOne(spinWheelWrapper);
    }

    /**
     * 初始化用户抽奖余额信息。
     * <p>
     * 根据传入的用户信息 VO 对象，创建并初始化用户的抽奖余额信息记录。
     * 初始化的记录包括用户账号、用户ID、站点代码，以及初始抽奖余额（设置为 0）。
     * 同时会设置创建时间和更新时间，并将该记录插入到抽奖余额表中。
     *
     * @param userInfoVO 用户信息对象，包含用户账号、用户ID和站点代码等信息。
     * @return 返回已插入数据库的用户抽奖余额信息对象。
     */
    public SiteActivityLotteryBalancePO initLotteryUserInfo(UserInfoVO userInfoVO) {
        SiteActivityLotteryBalancePO insert = new SiteActivityLotteryBalancePO();
        insert.setUserAccount(userInfoVO.getUserAccount());
        insert.setUserId(userInfoVO.getUserId());
        insert.setSiteCode(userInfoVO.getSiteCode());
        insert.setBalance(CommonConstant.business_zero);
        insert.setCreatedTime(System.currentTimeMillis());
        insert.setUpdatedTime(System.currentTimeMillis());
        lotteryBalanceRepository.insert(insert);
        return insert;
    }

    /**
     * 根据userId 获取用户信息
     */
    public UserInfoVO getUserInfo(String userId) {
        return userInfoApi.getByUserId(userId);
    }

    /**
     * 今天指定用户vip 取奖励次数
     */
    public Integer getVipConfigCount(Integer vipGradeCode, String baseId) {
        vipGradeCode = vipGradeCode == null ? 0 : vipGradeCode;
        LambdaQueryWrapper<SiteActivityRewardVipGradePO> rewardVipGradeWrapper = new LambdaQueryWrapper<>();
        rewardVipGradeWrapper.eq(SiteActivityRewardVipGradePO::getBaseId, baseId);
        rewardVipGradeWrapper.eq(SiteActivityRewardVipGradePO::getVipGradeCode, vipGradeCode);
        List<SiteActivityRewardVipGradePO> pos = vipGradeRepository.selectList(rewardVipGradeWrapper);
        return pos.get(0).getRewardCount();
    }

    /**
     * 获取应该添加记录表的奖励数
     * 配置奖励数+当天获取的奖励次数 <= 每日限制次数
     * 如果限制配置奖励数+当天获取的奖励次数 小于，则添加就是配置奖励数
     * 如果限制配置奖励数+当天获取的奖励次数 大于，则添加就是每日限制次数-当天获取的奖励次数
     * 如果当天奖励等于每日限制次数，就返回0
     *
     * @param addConfigCount   配置的奖励数
     * @param rewardTodayCount 当天已经获取的奖励次数
     * @param maxLimitCount    每日奖励次数的限制
     * @return 应该添加的奖励数
     */
    public Integer getInsertRewardCount(Integer addConfigCount, Integer rewardTodayCount, Integer maxLimitCount) {
        // 如果当天奖励次数已经达到每日限制，返回0
        if (rewardTodayCount >= maxLimitCount) {
            return 0;
        }
        // 计算应该添加的奖励数
        int remainingLimit = maxLimitCount - rewardTodayCount;
        return Math.min(addConfigCount, remainingLimit);
    }

    /**
     * 获取今天指定用户的奖励次数。
     * <p>
     * 该方法根据传入的用户ID，查询该用户在当天（从今天开始时间到今天结束时间）的奖励记录。
     * 通过过滤操作类型为"1"的记录（表示获取奖励的操作），计算该用户今天的总奖励次数。
     *
     * @param userId 指定的用户ID，用于查询该用户的奖励记录。
     * @return 返回该用户今天的总奖励次数。如果没有记录则返回0。
     */
    public Integer getTodayRewardCount(String userId, String timeZone) {
        long startTimestamp = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);  // 获取今天的开始时间
        long endTimestamp = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);      // 获取今天的结束时间 (指定时区)


        LambdaQueryWrapper<SiteActivityLotteryRecordPO> lotteryRecordWrapper = new LambdaQueryWrapper<>();
        lotteryRecordWrapper.ge(SiteActivityLotteryRecordPO::getCreatedTime, startTimestamp);  // 查询创建时间 >= 今天开始时间
        lotteryRecordWrapper.le(SiteActivityLotteryRecordPO::getCreatedTime, endTimestamp);    // 查询创建时间 <= 今天结束时间
        lotteryRecordWrapper.eq(SiteActivityLotteryRecordPO::getUserId, userId);              // 查询指定用户的记录
        lotteryRecordWrapper.in(SiteActivityLotteryRecordPO::getPrizeSource, Arrays.asList(ActivityPrizeSourceEnum.DEPOSIT.getType(), ActivityPrizeSourceEnum.BET.getType()));

        List<SiteActivityLotteryRecordPO> lotteryRecordPOs = lotteryRecordRepository.selectList(lotteryRecordWrapper);

        // 如果记录为空，返回0
        if (CollectionUtil.isEmpty(lotteryRecordPOs)) {
            return CommonConstant.business_zero;
        }

        // 计算总奖励次数（只计算操作类型为 "1" 的记录）
        return lotteryRecordPOs.stream()
                .filter(e -> ObjectUtil.equals(e.getOperationType(), CommonConstant.business_one))
                .mapToInt(SiteActivityLotteryRecordPO::getRewardCount)
                .sum();
    }

    /**
     * 获取昨天指定用户的奖励次数。
     * <p>
     * 该方法根据传入的用户ID，查询该用户在当天（从今天开始时间到今天结束时间）的奖励记录。
     * 通过过滤操作类型为"1"的记录（表示获取奖励的操作），计算该用户今天的总奖励次数。
     *
     * @param userId 指定的用户ID，用于查询该用户的奖励记录。
     * @return 返回该用户昨天的总奖励次数。如果没有记录则返回0。
     */
    public Integer getYesterdayRewardCount(String userId, String timeZone) {
        long startTimestamp = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);  // 获取今天的开始时间
        long endTimestamp = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);      // 获取今天的结束时间 (指定时区)


        LambdaQueryWrapper<SiteActivityLotteryRecordPO> lotteryRecordWrapper = new LambdaQueryWrapper<>();
        lotteryRecordWrapper.ge(SiteActivityLotteryRecordPO::getCreatedTime, startTimestamp);  // 查询创建时间 >= 今天开始时间
        lotteryRecordWrapper.le(SiteActivityLotteryRecordPO::getCreatedTime, endTimestamp);    // 查询创建时间 <= 今天结束时间
        lotteryRecordWrapper.eq(SiteActivityLotteryRecordPO::getUserId, userId);              // 查询指定用户的记录
        lotteryRecordWrapper.in(SiteActivityLotteryRecordPO::getPrizeSource, Arrays.asList(ActivityPrizeSourceEnum.DEPOSIT.getType(), ActivityPrizeSourceEnum.BET.getType()));

        List<SiteActivityLotteryRecordPO> lotteryRecordPOs = lotteryRecordRepository.selectList(lotteryRecordWrapper);

        // 如果记录为空，返回0
        if (CollectionUtil.isEmpty(lotteryRecordPOs)) {
            return CommonConstant.business_zero;
        }

        // 计算总奖励次数（只计算操作类型为 "1" 的记录）
        return lotteryRecordPOs.stream()
                .filter(e -> ObjectUtil.equals(e.getOperationType(), CommonConstant.business_one))
                .mapToInt(SiteActivityLotteryRecordPO::getRewardCount)
                .sum();
    }


    /**
     * 获取活动详情，包括以下信息：
     * 1. 活动奖励配置
     * 2. 当前用户的 VIP 及段位信息
     * 3. 用户的抽奖次数
     * 4. 抽奖总奖金：
     * 1. （初始值 + 所有会员抽奖总额）
     *
     * @param requestVO 请求参数，包括站点编码和用户ID
     * @return 包含活动详情的响应对象
     */
    public ResponseVO<ActivitySpinWheelAppRespVO> detail(ActivitySpinWheelAppReqVO requestVO) {
        // 获取活动基础信息
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePORecord(requestVO.getSiteCode());
        if (siteActivityBasePO == null) {
            // 如果活动不存在，直接返回成功的空响应
            return ResponseVO.success();
        }
        ActivitySpinWheelAppRespVO result = new ActivitySpinWheelAppRespVO();

        BeanUtils.copyProperties(siteActivityBasePO, result);
        // 设置 statusFlag
        long currentTime = System.currentTimeMillis();
        if (result.getShowStartTime() <= currentTime && result.getShowEndTime() >= currentTime) {
            result.setShowFlag(Boolean.TRUE);
        } else {
            result.setShowFlag(Boolean.FALSE);
        }
        // 判断
        // 判断活动是否在开启时间范围内
        if (Objects.equals(result.getActivityDeadline(), ActivityDeadLineEnum.LIMITED_TIME.getType())) {
            result.setEnable(currentTime >= result.getActivityStartTime() && currentTime <= result.getActivityEndTime());
        } else result.setEnable(currentTime >= result.getActivityStartTime());
        if (!result.getEnable()) {
            // 当前时间不在有效时间之内 1.活动尚未开始 2.活动已经结束当前时间不在有效时间之内 1.活动尚未开始 2.活动已经结束
            if (currentTime < result.getActivityStartTime()) {
                result.setEnableFlag(CommonConstant.business_one);
            } else {
                result.setEnableFlag(CommonConstant.business_two);
            }

        }
        // 获取活动转盘配置，如果转盘配置为空，继续返回成功的空响应
        ActivitySpinWheelPO activitySpinWheelPO = getSiteActivitySpinWheelConfig(siteActivityBasePO.getSiteCode(), siteActivityBasePO.getId());
        // 获取活动奖励配置列表
        List<SiteActivityRewardSpinWheelPO> rewardSpinWheelPOs = getSiteActivityRewardSpinWheel(siteActivityBasePO.getSiteCode(), siteActivityBasePO.getId());
        // 转换活动奖励配置为响应对象列表
        List<SiteActivityRewardSpinAPPResponseVO> resultRewardSpinResponseVOs = ConvertUtil.entityListToModelList(rewardSpinWheelPOs, SiteActivityRewardSpinAPPResponseVO.class);
        // 将奖励按段位分类存放
        List<SiteActivityRewardSpinAPPResponseVO> bronze = new ArrayList<>();
        List<SiteActivityRewardSpinAPPResponseVO> silver = new ArrayList<>();
        List<SiteActivityRewardSpinAPPResponseVO> gold = new ArrayList<>();
        for (SiteActivityRewardSpinAPPResponseVO responseVO : resultRewardSpinResponseVOs) {
            // 根据奖励的段位类型进行分类
            if (ActivityRewardRankEnum.BRONZE.getType().equals(responseVO.getRewardRank())) {
                bronze.add(responseVO);
            } else if (ActivityRewardRankEnum.SILVER.getType().equals(responseVO.getRewardRank())) {
                silver.add(responseVO);
            } else if (ActivityRewardRankEnum.GOLD.getType().equals(responseVO.getRewardRank())) {
                gold.add(responseVO);
            }
        }
        // 设置各个段位的奖励
        result.setBronze(bronze);
        result.setSilver(silver);
        result.setGold(gold);

        // 获取站点下各个段位的最低VIP等级配置
        List<SiteVIPRankVO> siteVIPRankVOS = vipRankApi.getVipRankListBySiteCode(requestVO.getSiteCode()).getData();
        List<SiteVIPRankResVO> resultVipRanks = siteVIPRankVOS.stream().filter(e ->
                        ActivityRewardRankEnum.BRONZE.getType().equals(e.getVipRankCode())
                                || ActivityRewardRankEnum.SILVER.getType().equals(e.getVipRankCode())
                                || ActivityRewardRankEnum.GOLD.getType().equals(e.getVipRankCode()))
                .map(e -> {
                    SiteVIPRankResVO resVO = new SiteVIPRankResVO();
                    BeanUtils.copyProperties(e, resVO);
                    return resVO;
                })
                .collect(Collectors.toList());
        result.setVipRankConfig(resultVipRanks);

        //1.抽奖总奖金 1.（初始值+所有会员抽奖总额）2.总额是彩金还是本金加彩金
        ResponseVO<BigDecimal> totalResponse = orderRecordService.getActivityTotalAmount(siteActivityBasePO.getActivityTemplate(), requestVO.getSiteCode());
        BigDecimal total = Optional.ofNullable(totalResponse)
                .filter(ResponseVO::isOk)
                .map(ResponseVO::getData)
                .orElse(BigDecimal.ZERO);
        // 将总奖金加上活动的初始金额，并保留两位小数
        result.setTotalAmount(NumberUtil.round(total.add(activitySpinWheelPO.getInitAmount()), 2));
        if (StringUtils.isBlank(requestVO.getUserId())) {
            return ResponseVO.success(result);
        }
        //2.当前用户的vip以及段位
        UserInfoVO userInfoVO = userInfoApi.getByUserId(requestVO.getUserId());
        assemblyActivitySpinWheelAppRespVO(result, userInfoVO);
        //3.抽奖次数
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository
                .selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                        .eq(SiteActivityLotteryBalancePO::getUserId, requestVO.getUserId()));
        // 如果没有抽奖余额记录，初始化该用户的抽奖余额信息
        if (lotteryBalancePO == null) {
            lotteryBalancePO = initLotteryUserInfo(userInfoVO);
        }
        result.setBalanceCount(lotteryBalancePO.getBalance());

        return ResponseVO.success(result);
    }

    /**
     * 该方法处理用户参与转盘抽奖活动的核心逻辑，主要包括以下步骤：
     * <p>
     * 1. 抽奖资格验证：
     * - 检查用户邮箱和手机号码是否已绑定。
     * - 验证用户的IP地址是否已绑定到其他账户，避免同一IP重复参与抽奖。
     * - 示例：
     * 用户A使用IP地址A成功领奖。
     * 用户B使用IP地址B成功领奖。
     * 用户A再次使用IP地址B尝试领奖将失败，因为该IP已被用户B使用过领奖。
     * 但用户A可以使用IP地址A继续领奖并成功。
     * <p>
     * 2. 使用 Redis 公平锁（Fair Lock）：
     * - 确保在多用户并发参与抽奖的场景下，操作是线程安全的，防止同时访问导致的数据冲突。
     * <p>
     * 3. 剩余抽奖次数验证：
     * - 检查用户是否仍有足够的抽奖机会，防止超出限制。
     * <p>
     * 4. 奖品抽取：
     * - 根据系统配置的奖品概率，从奖品池中随机抽取一个奖品。
     * <p>
     * 5. 更新用户数据：
     * - 减少用户的剩余抽奖次数，确保数据的一致性。
     * <p>
     * 6. 奖品分发：
     * - 将用户抽中的奖品信息准备好，并发送至消息队列，供后续的发奖或通知处理。
     * <p>
     * 7. 解锁操作：
     * - 无论抽奖操作成功或失败，都会保证释放Redis公平锁，防止锁死或资源被占用。
     *
     * @param requestVO 包含用户参与活动的上下文信息（如用户ID、站点代码等）。
     * @return ResponseVO<SiteActivityRewardSpinResponseVO> 返回抽奖结果，包含用户所获得的奖品信息。
     * @throws BaowangDefaultException 如果用户没有足够的抽奖次数，或在处理过程中出现其他异常。
     */
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<SiteActivityRewardSpinAPPResponseVO> prizeResult(ActivitySpinWheelAppReqVO requestVO) {
        // 1.判断是否有资格参与，手机号码与邮箱是否绑定， ip判断逻辑
        SiteActivityBasePO siteActivityBasePO = getSiteActivityBasePO(requestVO.getSiteCode());

        boolean lock = false;
        SiteActivityRewardSpinWheelPO prizeOne = null;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.ACTIVITY_SPIN_WHEEL_LOCK_KEY +
                requestVO.getUserId());
        try {
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                ActivitySpinWheelPrizeResultAppRespVO result = new ActivitySpinWheelPrizeResultAppRespVO();
                // 获取用户的抽奖次数，确保用户有足够的机会参与活动
                SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository
                        .selectOne(new LambdaQueryWrapper<SiteActivityLotteryBalancePO>()
                                .eq(SiteActivityLotteryBalancePO::getUserId, requestVO.getUserId()));
                if (lotteryBalancePO.getBalance() <= 0) {
                    return ResponseVO.fail(ResultCode.ACTIVITY_NOT_HAVE_PRIZE_TIME);
                }
                // 抽奖等级与当前会员等级进行判断
                // 获取站点下各个段位的最低VIP等级配置
                SiteVIPRankVO siteVIPRankVOConfig = vipRankApi.getVipRankListBySiteCodeAndCode(requestVO.getSiteCode(), requestVO.getVipRankCode()).getData();
                // 查询该用户的等级
                UserInfoVO byUserId = userInfoApi.getByUserId(requestVO.getUserId());
                if (siteVIPRankVOConfig.getMinVipGrade() > byUserId.getVipGradeCode()) {
                    return ResponseVO.fail(ResultCode.INSUFFICIENT_VIP_LEVEL);
                }

                // 获取当前活动及用户VIP等级的奖品列表
                List<SiteActivityRewardSpinWheelPO> rewardSpinWheelPOs = getSiteActivityRewardSpinWheel(
                        siteActivityBasePO.getSiteCode(), siteActivityBasePO.getId(), requestVO.getVipRankCode());
                // 根据奖品配置随机选择一个奖品
                prizeOne = drawLotteryWinner(rewardSpinWheelPOs);
                result.setRewardSpinWheelConfig(ConvertUtil.entityToModel(prizeOne, SiteActivityRewardSpinAPPResponseVO.class));
                // 更新抽奖次数
                updateUserBalance(requestVO.getUserId(), 1, Boolean.FALSE);
                // 准备发送奖励信息到消息队列
                String today = String.valueOf(System.currentTimeMillis());
                ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
                String orderNo = OrderNoUtils.genOrderNo(requestVO.getUserId(), ActivityTemplateEnum.SPIN_WHEEL.getSerialNo(), today);
                activitySendMqVO.setOrderNo(orderNo);
                activitySendMqVO.setSiteCode(requestVO.getSiteCode());
                activitySendMqVO.setUserId(requestVO.getUserId());
                activitySendMqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
                activitySendMqVO.setActivityTemplate(siteActivityBasePO.getActivityTemplate());
                activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
                // 结束时间，写死3天
                //activitySendMqVO.setReceiveEndTime(DateUtils.addDay(new Date(System.currentTimeMillis()), 3).getTime());
                activitySendMqVO.setDistributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode());
                activitySendMqVO.setActivityAmount(prizeOne.getPrizeAmount());
                activitySendMqVO.setRewardRank(prizeOne.getRewardRank());
                activitySendMqVO.setPrizeType(prizeOne.getPrizeType());
                activitySendMqVO.setPrizeName(prizeOne.getPrizeName());
                activitySendMqVO.setActivityId(siteActivityBasePO.getId());
                //平台币转法币
                ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO =
                        siteCurrencyInfoApi.transferToMainCurrency(PlatCurrencyFromTransferVO.builder()
                                .siteCode(requestVO.getSiteCode()).sourceAmt(prizeOne.getPrizeAmount())
                                .targetCurrencyCode(byUserId.getMainCurrency()).build());
                if (!siteCurrencyConvertRespVOResponseVO.isOk()) {
                    log.error("平台币转法币转换异常:{}", JSONObject.toJSONString(siteCurrencyConvertRespVOResponseVO));
                    return ResponseVO.fail(ResultCode.RECEIVE_FAIL_DESCRIPTION);
                }
                BigDecimal mainCurrencyAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
                BigDecimal runningWater = AmountUtils.multiply(mainCurrencyAmount, siteActivityBasePO.getWashRatio());
                activitySendMqVO.setRunningWater(runningWater);
                activitySendMqVO.setRunningWaterMultiple(siteActivityBasePO.getWashRatio());
                //activitySendMqVO.setPrizeType(prizeOne.getPrizeType());
                // 将奖励信息添加到消息队列并发送
                ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
                activitySendListMqVO.setList(CollectionUtil.toList(activitySendMqVO));
                KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
                // 发送勋章 参与轮盘旋转总计200次
                _this.handleSendMedal(requestVO.getUserId(), requestVO.getUserAccount(), requestVO.getSiteCode());
                fairLock.unlock();
                return ResponseVO.success(ConvertUtil.entityToModel(prizeOne, SiteActivityRewardSpinAPPResponseVO.class));
            } else {
                log.warn("未能领取转盘活动奖励，用户:{}", requestVO.getUserId());
                throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
            }

        } catch (Exception e) {
            log.error("领取转盘活动奖励发生异常", e);
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        } finally {
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    /**
     * MEDAL_1006("1006","旋转之星", "轮盘旋转 n 次，达到n次后 立即解锁",2),
     *
     * @param userId 用户id
     */
    @Async(ThreadPoolConfig.TASK_EXECUTOR)
    public void handleSendMedal(String userId, String userAccount, String siteCode) {
        log.info("旋转此处发放勋章,userId:{},userAccount:{}.", userId, userAccount);
        // 查询该用户是否得到这个勋章
        ResponseVO<Long> longResponseVO = medalAcquireRecordApi.countByCond(MedalAcquireRecordCondReqVO.builder()
                .userId(userId)
                .siteCode(siteCode)
                .medalCodeEnum(MedalCodeEnum.MEDAL_1006)
                .build());
        if (!longResponseVO.isOk()) {
            log.error("查询用户勋章失败:{}", userId);
            return;
        }
        if (longResponseVO.getData() >= 1L) {
            log.error("用户已经获取勋章:{}", userId);
            return;
        }
        MedalAcquireCondReqVO acquireCondReqVO = new MedalAcquireCondReqVO();
        acquireCondReqVO.setSiteCode(siteCode);
        acquireCondReqVO.setMedalCodeEnum(MedalCodeEnum.MEDAL_1006);
        ResponseVO<SiteMedalInfoRespVO> byMedalCode = medalAcquireApi.findByMedalCode(acquireCondReqVO);
        if (!byMedalCode.isOk()) {
            log.error("查询用户勋章获取条件失败:{}", userId);
            return;
        }
        int tarCount = Integer.parseInt(byMedalCode.getData().getCondNum1());
        if (tarCount < 1) {
            log.error("用户勋章未配置:{}", userId);
            return;
        }
        // 查询旋转次数
        Integer countGet = lotteryRecordRepository.getTotalRewardCountByUserId(userId);
        log.info("旋转此处发放勋章,查询旋转次数:{}.", countGet);
        countGet = countGet == null ? 0 : countGet;
        if (countGet < tarCount) {
            log.info("用户未达到勋章获取条件:{}", userId);
            return;
        }
        LambdaQueryWrapper<SiteActivityLotteryBalancePO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityLotteryBalancePO::getUserId, userId);
        SiteActivityLotteryBalancePO lotteryBalancePO = lotteryBalanceRepository.selectOne(lambdaQueryWrapper);
        if (countGet - lotteryBalancePO.getBalance() >= tarCount) {
            log.info("旋转此处发放勋章,发放勋章:{}.", userAccount);
            medalAcquireApi.unLockMedal(MedalAcquireReqVO.builder()
                    .siteCode(siteCode)
                    .userId(userId)
                    .userAccount(userAccount)
                    .medalCode(MedalCodeEnum.MEDAL_1006.getCode())
                    .build());
        }
    }


    static String json = "{ \"bronze\": [\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 1,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品1\",\n" +
            "                \"prizeAmount\": 5.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 2,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品2\",\n" +
            "                \"prizeAmount\": 6.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 3,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品3\",\n" +
            "                \"prizeAmount\": 7.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 4,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品4\",\n" +
            "                \"prizeAmount\": 8.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 5,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品5\",\n" +
            "                \"prizeAmount\": 9.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 6,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品6\",\n" +
            "                \"prizeAmount\": 10.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 7,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品7\",\n" +
            "                \"prizeAmount\": 11.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 8,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品8\",\n" +
            "                \"prizeAmount\": 12.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 9,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品9\",\n" +
            "                \"prizeAmount\": 13.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 10,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品10\",\n" +
            "                \"prizeAmount\": 14.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 11,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品11\",\n" +
            "                \"prizeAmount\": 15.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 12,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品12\",\n" +
            "                \"prizeAmount\": 16.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 13,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品13\",\n" +
            "                \"prizeAmount\": 17.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 14,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品14\",\n" +
            "                \"prizeAmount\": 18.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 15,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品15\",\n" +
            "                \"prizeAmount\": 19.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 6.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            },\n" +
            "            {\n" +
            "                \"siteCode\": \"10002\",\n" +
            "                \"rewardRank\": 0,\n" +
            "                \"rewardRankName\": null,\n" +
            "                \"prizeLevel\": 16,\n" +
            "                \"prizeType\": \"1\",\n" +
            "                \"prizeTypeName\": null,\n" +
            "                \"prizeName\": \"奖品16\",\n" +
            "                \"prizeAmount\": 20.00,\n" +
            "                \"prizePictureUrl\": \"http://api.btstu.cn/sjbz/?lx=meizi\",\n" +
            "                \"probability\": 4.00,\n" +
            "                \"baseId\": \"1834875296983887874\"\n" +
            "            }\n" +
            "        ]}";
    /*public static void main(String[] args) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // Parse JSON to List<SiteActivityRewardSpinWheelPO>
            List<SiteActivityRewardSpinWheelPO> prizeConfigList = objectMapper.readValue(
                    objectMapper.readTree(json).get("bronze").toString(),
                    new TypeReference<List<SiteActivityRewardSpinWheelPO>>() {}
            );

            // Output the list to check the result
            for (SiteActivityRewardSpinWheelPO prizeConfig : prizeConfigList) {
               // System.out.println(prizeConfig.getPrizeName());
            }
            for (int i = 0; i < 120; i++) {
                SiteActivityRewardSpinWheelPO result = drawLotteryWinner(prizeConfigList);
                System.out.println(result.getPrizeName()+" : "+result.getPrizeAmount());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }*/

    /**
     * 游戏抽奖的算法
     *
     * @param prizeConfigList 配置奖品list
     * @return 返回中奖结果
     */
    public SiteActivityRewardSpinWheelPO drawLotteryWinner(List<SiteActivityRewardSpinWheelPO> prizeConfigList) {
        if (CollectionUtil.isEmpty(prizeConfigList)) {
            throw new BaowangDefaultException(ResultCode.SPIN_WHEEL_NOT_PRIZE_CONFIG);
        }
        Random random = new Random();
        BigDecimal winningProbability = BigDecimal.valueOf(random.nextDouble()).multiply(BigDecimal.valueOf(100)); // 生成 [0.0, 100.0) 的随机BigDecimal数
        BigDecimal cumulativeProbability = BigDecimal.ZERO;

        for (SiteActivityRewardSpinWheelPO prizeConfig : prizeConfigList) {
            if (prizeConfig == null || prizeConfig.getProbability() == null) {
                continue;
            }
            cumulativeProbability = cumulativeProbability.add(prizeConfig.getProbability());
            if (winningProbability.compareTo(cumulativeProbability) < 0) {
                return prizeConfig; // 返回中奖奖品
            }
        }
        // 如果没有中奖，返回一个默认的未中奖奖品配置
        return prizeConfigList.get(0); // 默认返回最后一个奖品配置

    }

    private void assemblyActivitySpinWheelAppRespVO(ActivitySpinWheelAppRespVO result, UserInfoVO userInfoVO) {
        result.setVipRankCode(userInfoVO.getVipRank());
        result.setVipGradeCode(userInfoVO.getVipGradeCode());
        SiteVIPGradeVO siteVIPGradeVO = vipGradeApi.queryVIPGradeByGrade(String.valueOf(userInfoVO.getVipGradeCode()));
        result.setVipGradeCodeName(siteVIPGradeVO.getVipGradeName());
        ResponseVO<SiteVIPRankVO> responseVO = vipRankApi.getVipRankListBySiteCodeAndCode(userInfoVO.getSiteCode(), userInfoVO.getVipRank());
        if (responseVO.isOk()) {
            result.setVipRankCodeName(responseVO.getData().getVipRankName());
        }

    }

    public List<SiteActivityRewardSpinWheelPO> getSiteActivityRewardSpinWheel(String siteCode, String baseId) {
        LambdaQueryWrapper<SiteActivityRewardSpinWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityRewardSpinWheelPO::getBaseId, baseId);
        lambdaQueryWrapper.eq(SiteActivityRewardSpinWheelPO::getSiteCode, siteCode);
        return rewardSpinWheelRepository.selectList(lambdaQueryWrapper);
    }

    public List<SiteActivityRewardSpinWheelPO> getSiteActivityRewardSpinWheel(String siteCode, String baseId, Integer vipRankCode) {
        LambdaQueryWrapper<SiteActivityRewardSpinWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityRewardSpinWheelPO::getBaseId, baseId);
        lambdaQueryWrapper.eq(SiteActivityRewardSpinWheelPO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(ObjectUtil.isNotEmpty(vipRankCode), SiteActivityRewardSpinWheelPO::getRewardRank, vipRankCode);
        return rewardSpinWheelRepository.selectList(lambdaQueryWrapper);
    }

    /**
     * 当前用户:{}上级代理:是否设置会员福利不能参与任务
     *
     * @param agentId 代理id
     * @return 能参加任务返回true，否则返回fasle
     */
    public Boolean checkTask(String agentId) {
        if (org.springframework.util.StringUtils.hasText(agentId)) {
            AgentInfoVO agentInfoVO = agentInfoApi.getByAgentId(agentId);
            String userBenefit = agentInfoVO.getUserBenefit();
            if (org.springframework.util.StringUtils.hasText(userBenefit) && !userBenefit.contains(AgentUserBenefitEnum.SPIN_REWARD.getCode().toString())) {
                return false;
            }
            return true;
        }
        return true;

    }

    /**
     * 当前用户:{}上级代理:是否设置会员福利不能参与任务
     *
     * @param userIds 代理id
     * @return 能参加任务返回true，否则返回fasle
     */
    public List<String> checkJoinTaskUserId(List<String> userIds) {
        List<String> results = new ArrayList<>();
        for (String userId : userIds) {
            UserInfoVO byUserId = userInfoApi.getByUserId(userId);
            /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(byUserId.getAccountType())) {
                continue;
            }*/
            if (byUserId != null) {
                String agentId = byUserId.getSuperAgentId();
                if (checkTask(agentId)) {
                    results.add(userId);
                }
            }
        }
        return results;

    }
}
