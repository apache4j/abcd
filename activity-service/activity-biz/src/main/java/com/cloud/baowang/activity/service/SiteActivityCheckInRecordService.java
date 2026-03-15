package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.*;
import com.cloud.baowang.activity.repositories.*;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.dto.ActivityConfigDTO;
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
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossMqVO;
import com.cloud.baowang.common.kafka.vo.UserVenueWinLossSendVO;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.play.api.enums.venue.VenuePlatformConstants;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.request.UserTopReqVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.recharge.PlatCurrencyFromTransferVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyConvertRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityCheckInRecordService extends ServiceImpl<SiteCheckInRecordRepository, SiteCheckInRecordPO> {

    private final SiteActivityBaseRepository baseRepository;

    private final SiteActivityCheckInRepository checkInRepository;


    private final UserDepositWithdrawApi userDepositWithdrawApi;

    private final SiteCurrencyInfoApi siteCurrencyInfoApi;

    private final UserInfoApi userInfoApi;

    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    private final ActivitySpinWheelService activitySpinWheelService;

    private final AgentInfoApi agentInfoApi;

    private final SiteApi siteApi;

    private final SiteActivityMakeupCountRecordRepository makeupCountRecordRepository;

    private final SiteMakeupCountBalanceRepository makeupCountBalanceRepository;

    /**
     * 判断是否应展示补签按钮（手指图标）
     *
     * @param isTodaySigned       今天是否已签到
     * @param isTodayMakeupSigned 今天是否未补签（true 表示未补签）
     * @param makeupCount         用户当前已补签次数
     * @param makeupLimit         系统配置的最大补签次数
     * @param currentMonthRecords 当前月的签到记录列表
     * @return true 表示应展示补签图标
     */
    public static boolean shouldShowMakeupFlag(boolean isTodaySigned,
                                               boolean isTodayMakeupSigned,
                                               int makeupCount,
                                               int makeupLimit,
                                               List<CheckInRecordVO> currentMonthRecords) {
        if (!isTodaySigned || !isTodayMakeupSigned) {
            return false;
        }

        if (makeupCount >= makeupLimit) {
            return false;
        }

        return currentMonthRecords.stream()
                .anyMatch(r -> ObjectUtil.equals(r.getStatus(), "1")); // 存在漏签
    }

    /**
     * 统计补签次数（记录日期与创建日期不一致）
     *
     * @param records  签到记录列表
     * @param timeZone 时区
     * @return 补签次数
     */
    public static int countMakeupCheckIns(List<SiteCheckInRecordPO> records, String timeZone) {
        if (records == null || records.isEmpty() || CollectionUtil.isEmpty(records)) {
            return 0;
        }
        return (int) records.stream()
                .filter(e -> {
                    String dayStrRecord = e.getDayStr();
                    String dayStrCreate = TimeZoneUtils.convertUtcStartOfDayToLocalDate(e.getCreatedTime(), timeZone);
                    // 如果 dayStr 和实际创建日期不同，说明是补签（因为记录标的日期 ≠ 实际日期），则补签次数 +1
                    return !ObjectUtil.equals(dayStrRecord, dayStrCreate);
                })
                .count();
    }

    /**
     * 统计签到记录中在指定时间范围内的数量
     *
     * @param records     签到记录列表
     * @param startMillis 开始时间（包含）
     * @param endMillis   结束时间（包含）
     * @return 符合条件的记录数量
     */
    public static int countRecordsInRange(List<SiteCheckInRecordPO> records, long startMillis, long endMillis) {
        if (records == null || records.isEmpty() || CollectionUtil.isEmpty(records)) {
            return 0;
        }
        return (int) records.stream()
                .filter(e -> e.getDayMillis() >= startMillis && e.getDayMillis() <= endMillis)
                .count();
    }

    public ResponseVO<CheckInRecordRespVO> checkInRecord(UserBaseReqVO build) {
        CheckInRecordRespVO respVO = new CheckInRecordRespVO();
        long currentTime = System.currentTimeMillis();
        String timeZone = build.getTimezone();
        long monthStart = TimeZoneUtils.getStartOfMonthInTimeZone(currentTime, timeZone);
        long monthEnd = TimeZoneUtils.getEndOfMonthInTimeZone(currentTime, timeZone);
        long lastMonthStart = TimeZoneUtils.getLastMonStartTimeInTimeZone(timeZone);
        long lastMonthEnd = TimeZoneUtils.getLastMonEndTimeInTimeZone(timeZone);
        // 获取签到配置
        List<SiteCheckInRecordPO> siteCheckInRecordPOS = new ArrayList<>();
        Boolean isLogin = false;
        if (!ObjectUtil.isEmpty(build.getUserId())) {
            siteCheckInRecordPOS = queryCheckInRecords(build.getSiteCode(), build.getUserId(), 1, lastMonthStart, monthEnd);
            isLogin = true;
        }
        String todayStr = TimeZoneUtils.convertUtcStartOfDayToLocalDate(currentTime, timeZone);
        // 按照时间区分
        // 分组记录，key 为 yyyy-MM-dd，方便后续比对
        Map<String, SiteCheckInRecordPO> recordMap = siteCheckInRecordPOS.stream().collect(Collectors.toMap(r -> TimeZoneUtils.convertUtcStartOfDayToLocalDate(r.getDayMillis(), timeZone), Function.identity(), (r1, r2) -> r1));
        // 获取签到配置
        ActivityConfigDTO baseCheckInConfig = getBaseCheckInConfig(build.getSiteCode());
        if (baseCheckInConfig == null) {
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        SiteActivityCheckInPO siteActivityCheckInPO = baseCheckInConfig.getCheckInPO();
        if (siteActivityCheckInPO == null) {
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        //
        if (siteActivityCheckInPO.getMakeBetAmount() == null) {
            siteActivityCheckInPO.setMakeBetAmount(BigDecimal.ZERO);
        }
        if (siteActivityCheckInPO.getMakeupLimit() == null) {
            siteActivityCheckInPO.setMakeupLimit(0);
        }
        if (siteActivityCheckInPO.getMakeDepositAmount() == null) {
            siteActivityCheckInPO.setMakeDepositAmount(BigDecimal.ZERO);
        }
        Boolean makeupFlagStatus = true;
        if (siteActivityCheckInPO.getMakeupLimit().compareTo(0) == 0
                && siteActivityCheckInPO.getMakeDepositAmount().compareTo(BigDecimal.ZERO) == 0
                && siteActivityCheckInPO.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0) {
            siteActivityCheckInPO.setMakeupLimit(0);
            makeupFlagStatus = false;
            respVO.setMakeupFlagStatus(makeupFlagStatus);
        } else {
            respVO.setMakeupFlagStatus(makeupFlagStatus);
        }
        List<CheckInRewardConfigVO> weekRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardWeek(), CheckInRewardConfigVO.class);
        List<CheckInRewardConfigVO> monthRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardMonth(), CheckInRewardConfigVO.class);
        List<CheckInRewardConfigRespVO> totalRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardTotal(), CheckInRewardConfigRespVO.class);
        totalRewardConfigs.forEach(vo -> {
            // 月累计设置为3
            vo.setRewardFrom(3);
        });
        Map<Integer, CheckInRewardConfigVO> iconMap = new HashMap<>();
        weekRewardConfigs.stream().forEach(vo -> {
            iconMap.put(vo.getCode(), vo);
        });
        List<CheckInRecordVO> currentMonth = generateMonthlyCheckInRecords(monthStart, monthEnd, timeZone, recordMap, todayStr, siteActivityCheckInPO, isLogin, iconMap, makeupFlagStatus);
        List<CheckInRecordVO> lastMonth = generateMonthlyCheckInRecords(lastMonthStart, lastMonthEnd, timeZone, recordMap, todayStr, siteActivityCheckInPO, isLogin, iconMap, makeupFlagStatus);

        currentMonth.forEach(vo -> {
            if (vo.getDayStr().equals(todayStr)) {
                if (vo.getStatus().equals("2")) {
                    respVO.setIsSignedToday(true);
                }
            }
        });
        // 配置额外奖励
        prependEmptyRecords(currentMonth);
        prependEmptyRecords(lastMonth);
        respVO.setCurrentMonth(currentMonth);
        respVO.setLastMonth(lastMonth);
        respVO.setCurrentDate(TimeZoneUtils.getCurrentMonth(todayStr, timeZone));
        respVO.setLastDate(TimeZoneUtils.getPreviousMonth(todayStr, timeZone));
        // 也需要获取实际补签次数，配置的总补签次数-已经补签的
        Integer makeupLimit = siteActivityCheckInPO.getMakeupLimit();
        List<SiteCheckInRecordPO> currentMonthRecord = siteCheckInRecordPOS.stream()
                .filter(e -> e.getDayMillis() >= monthStart && e.getDayMillis() <= monthEnd)
                .collect(Collectors.toList());
        // 实际补签次数
        Integer makeupAlreadyUseCount;
        if (makeupFlagStatus) {
            makeupAlreadyUseCount = countMakeupCheckIns(currentMonthRecord, timeZone);
        } else {
            makeupAlreadyUseCount = 0;
        }
        // 获取补签次数
        if (makeupFlagStatus) {
            if (siteActivityCheckInPO.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0 && siteActivityCheckInPO.getMakeDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
                // 如果没有补签条件，则直接给补签次数，无需查询余额
                Long yesterday = TimeZoneUtils.getStartOfYesterdayInTimeZone(currentTime, timeZone);
                // 统计本月从 monthStart 到昨天为止，用户成功签到的天数
                long matchedCount = siteCheckInRecordPOS.stream()
                        .filter(e -> e.getDayMillis() >= monthStart && e.getDayMillis() <= yesterday)
                        .count();
                int count = (int) matchedCount;
                //  // 获取从月初到昨天为止的所有日历天数（字符串列表），用于计算应该签到的总天数
                List<String> days = TimeZoneUtils.getBetweenDates(monthStart, yesterday, timeZone);
                // 补签数量 = 应签到天数 - 实际签到天数，且最小为 0（避免负数）
                int makeupCount = Math.max(0, days.size() - count);
                // 补签次数不得超过剩余的补签上限
                int makeupCountLimit = Math.max(0, makeupLimit - makeupAlreadyUseCount);
                makeupCount = Math.min(makeupCount, makeupCountLimit);
                respVO.setMakeupCount(makeupCount);
                // 配置的补签次数-实际获取的补签次数差值（如果没有配置补签条件，则是配置最大补签次数-实际补签次数），-是否获取补签值达到上限
            /*respVO.setIsMakeupLimitReached(makeupLimit - makeupRecordCounts <= 0 ? true : false);
            respVO.setMakeupAlreadyReceiveCount(makeupRecordCounts);*/


            } else {
                // 有补签条件
                respVO.setMakeupCount(getMakeupCount(build.getSiteCode(), build.getUserId(), timeZone));
                // 配置的补签次数-实际获取的补签次数差值（如果没有配置补签条件，则是配置最大补签次数-实际补签次数），-是否获取补签值达到上限
                // 9. 查询用户本月已补签次数 修改为英使用补签次数
                //int makeupCount = queryUserMonthlyMakeupCount(build.getUserId(), build.getSiteCode(), monthStart, monthEnd);
                //  查询已经使用的补签次数
            /*int makeupCountUse = queryUserMonthlyMakeupCountUse(build.getUserId(), build.getSiteCode(), monthStart, monthEnd);
            respVO.setIsMakeupLimitReached(makeupLimit - makeupCount <= 0 ? true : false);
            respVO.setMakeupAlreadyReceiveCount(makeupCount);
            respVO.setMakeupAlreadyUseCount(makeupCount);*/
            }
        } else {
            respVO.setMakeupCount(0);
        }


        int month = TimeZoneUtils.getMonthOfYearInTimeZone(currentTime, timeZone);
        CheckInRewardConfigRespVO currentMonthConfig = null;
        //int lastMon = (month == 1) ? 12 : month - 1;
        for (CheckInRewardConfigVO configVO : monthRewardConfigs) {
            if (Objects.equals(configVO.getCode(), month)) {

                currentMonthConfig = BeanUtil.copyProperties(configVO, CheckInRewardConfigRespVO.class);
                currentMonthConfig.setRewardFrom(2);
            }
            // 两个都找到就可以 break
            if (currentMonthConfig != null) {
                break;
            }

        }
        int daysBetweenInclusive = TimeZoneUtils.getDaysBetweenInclusive(monthStart, monthEnd, timeZone);
        currentMonthConfig.setDayLimit(daysBetweenInclusive);
        totalRewardConfigs.add(currentMonthConfig);
        respVO.setTotalRewardConfigs(totalRewardConfigs);
        // 倒计时
        long todayEndTime = TimeZoneUtils.getEndOfDayInTimeZone(currentTime, timeZone);
        long todayStartTime = TimeZoneUtils.getStartOfDayInTimeZone(currentTime, timeZone);
        respVO.setRemainingTime((todayEndTime - currentTime) / 1000);
        // // 在补签记录中查找“今天”有签到但不是“今日正常签到”的记录
        // 今天是否补签过，如果没有补签，则显示补签按钮，如果有了补签，都是false
        Optional<SiteCheckInRecordPO> first = siteCheckInRecordPOS.stream()
                .filter(e -> e.getCreatedTime() >= todayStartTime
                        && e.getCreatedTime() <= todayEndTime
                        && e.getDayMillis() != todayStartTime).findFirst();
        if (first.isPresent()) {
            // 今天
            respVO.setIsMakeupFlag(true);
        }

        // 如果今天没补签过，查找本月中第一个未签到（状态为 "1"）的日期，标记为可补签（isShowMakeupFlag = true）
        // 添加如果今天签到了才展示补签按钮 -ok
        // 如果今天没签到，就不显示 -- ok
        // 当天补签后，就不展示补签按钮 -ok
        // 签到成功之后，补签次数达到后台配置限制，不管有没有漏签，不显示补签按钮
        // 签到成功之后，有漏签天数，则显示手指
        //
        /**
         * 四个条件：
         * 1。今天是否签到成功 isMakeupFlag，签到-展示，没有签到-不展示；
         * 2。当天是否补签-isSignedToday， 补签-不展示，没有补签-展示
         * 3。是否有漏签天数 -temp， 有漏签-展示，没有漏签-不展示
         * 4。是否补签次数达到后台配置限制 makeupCount-实际补签次数，siteActivityCheckInPO.getMakeupLimit() -配置补签次数
         *   达到-不展示，没有达到-展示
         *   综合逻辑：
         * - 如果今天未签到，直接不展示；
         * - 如果今天已签到但补签次数已达上限，不展示；
         * - 如果今天已签到，补签次数未达上限，但没有漏签，不展示；
         * - 如果今天已签到，补签次数未达上限，有漏签，且今天已补签，不展示；
         * - 如果今天已签到，有漏签，且今天未补签，获取补签次数未达上限，已经使用肯定也没有达到上限,展示图标。
         * - 如果今天已签到，有漏签，且今天未补签，获取补签次数达上限，已经使用肯定也没有达到上限,展示图标。
         * - 如果今天已签到，有漏签，且今天未补签，获取补签次数达上限，已经使用也达到上限,不用展示图标。
         */
        boolean showFlag = shouldShowMakeupFlag(
                respVO.getIsSignedToday(),
                !respVO.getIsMakeupFlag(),  // 注意：这里传的是“未补签”状态
                makeupAlreadyUseCount,//已经获取的补签次数
                siteActivityCheckInPO.getMakeupLimit(),
                respVO.getCurrentMonth()
        );
        if (showFlag) {
            for (CheckInRecordVO temp : respVO.getCurrentMonth()) {
                if (ObjectUtil.equals(temp.getStatus(), "1")) {
                    temp.setIsShowMakeupFlag(true);
                    break;
                }
            }
        }
        // 补充添加活动信息
        SiteActivityBasePO basePO = baseCheckInConfig.getBasePO();
        respVO.setUserType(ActivityUserTypeEnum.ALL_USER.getCode());
        respVO.setActivityDeadline(basePO.getActivityDeadline());
        respVO.setActivityStartTime(basePO.getActivityStartTime());
        respVO.setActivityEndTime(basePO.getActivityEndTime());
        respVO.setActivityRuleI18nCode(basePO.getActivityRuleI18nCode());
        respVO.setActivityDescI18nCode(basePO.getActivityDescI18nCode());
        respVO.setActivityIntroduceI18nCode(basePO.getActivityIntroduceI18nCode());


        return ResponseVO.success(respVO);

    }

    /**
     * 查询签到记录列表
     *
     * @param siteCode   站点编码
     * @param userId     用户ID
     * @param rewardType 奖励类型
     * @param startTime  开始时间戳
     * @param endTime    结束时间戳
     * @return 签到记录列表
     */
    public List<SiteCheckInRecordPO> queryCheckInRecords(
            String siteCode,
            String userId,
            Integer rewardType,
            Long startTime,
            Long endTime) {
        LambdaQueryWrapper<SiteCheckInRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteCheckInRecordPO::getSiteCode, siteCode)
                .eq(SiteCheckInRecordPO::getUserId, userId)
                .eq(SiteCheckInRecordPO::getRewardType, rewardType)
                .ge(SiteCheckInRecordPO::getDayMillis, startTime)
                .le(SiteCheckInRecordPO::getDayMillis, endTime);

        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 在列表前插入指定数量的空 CheckInRecordVO 对象作为占位符，
     * 插入数量根据列表中第一个元素的 weekNum 值决定。
     *
     * @param records 当前月的签到记录列表，列表中每条记录包含星期几信息（weekNum）
     */
    public void prependEmptyRecords(List<CheckInRecordVO> records) {
        if (records == null || records.isEmpty()) {
            return; // 如果为空则不处理
        }

        // 排序确保列表首位是该月最早的一天
        records.sort(Comparator.comparing(CheckInRecordVO::getDayStr));

        CheckInRecordVO first = records.get(0);
        int weekNum = first.getWeekNum(); // 获取第一个记录是星期几（0=周日，1=周一...）
        if (weekNum == 7) {
            return;
        }
        // 向列表前插入 weekNum 个空记录作为占位
        for (int i = 0; i < weekNum; i++) {
            records.add(i, new CheckInRecordVO());
        }
    }


    private Integer getMakeupCount(String siteCode, String userId, String timeZone) {
        long monthStart = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
        LambdaQueryWrapper<SiteMakeupCountBalancePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteMakeupCountBalancePO::getSiteCode, siteCode);
        queryWrapper.eq(SiteMakeupCountBalancePO::getUserId, userId);
        queryWrapper.eq(SiteMakeupCountBalancePO::getMonthMillis, monthStart);
        queryWrapper.last(" limit 1 ");
        SiteMakeupCountBalancePO siteMakeupCountBalancePO = makeupCountBalanceRepository.selectOne(queryWrapper);
        if (siteMakeupCountBalancePO == null) {
            return 0;
        }
        return siteMakeupCountBalancePO.getBalance();

    }

    /**
     * 生成指定时区、指定时间段内的每日签到记录列表。
     *
     * @param startMonth 起始时间戳（毫秒），通常为该月的第一天 00:00:00
     * @param endMonth   结束时间戳（毫秒），通常为该月的最后一天 23:59:59
     * @param timezone   时区 ID，例如 "Asia/Shanghai"
     * @param signedMap  已签到记录映射，key 为日期字符串（格式：yyyy-MM-dd），value 为签到记录实体
     * @return 指定月份内的每日签到记录列表，每条记录包含日期和签到状态（1-已签到，0-未签到）
     */
    public List<CheckInRecordVO> generateMonthlyCheckInRecords(long startMonth,
                                                               long endMonth,
                                                               String timezone,
                                                               Map<String, SiteCheckInRecordPO> signedMap,
                                                               String todayStr, SiteActivityCheckInPO siteActivityCheckInPO,
                                                               Boolean isLogin,
                                                               Map<Integer, CheckInRewardConfigVO> iconMap,
                                                               Boolean makeupFlagStatus) {
        List<CheckInRecordVO> list = new ArrayList<>();

        // 获取起始和结束的 LocalDate
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDate startDate = Instant.ofEpochMilli(startMonth).atZone(zoneId).toLocalDate();
        LocalDate endDate = Instant.ofEpochMilli(endMonth).atZone(zoneId).toLocalDate();

        // 按天循环
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            String dayStr = date.toString(); // yyyy-MM-dd

            CheckInRecordVO vo = new CheckInRecordVO();
            vo.setDayStr(dayStr);
            long time = date.atStartOfDay(zoneId).toInstant().toEpochMilli();
            if (!isLogin) {
                vo.setStatus("4");
            } else {
                if (dayStr.compareTo(todayStr) < 0) {
                    if (signedMap.containsKey(dayStr)) {
                        //2-已签到（今天之前已经签到，包括今天签到）
                        vo.setStatus("2");
                    } else {
                        //1-签到过期(今天以前的未签到)
                        if (makeupFlagStatus) {
                            vo.setStatus("1");
                        } else {
                            vo.setStatus("5");
                        }

                    }
                } else if (dayStr.compareTo(todayStr) == 0) {
                    vo.setIsTodayFlag(true);
                    if (signedMap.containsKey(dayStr)) {
                        //2-已签到（今天之前已经签到，包括今天签到）
                        vo.setStatus("2");
                    } else {
                        //3-可签到 （今天签到状态
                        vo.setStatus("3");
                    }

                } else {
                    // 之后的显示
                    vo.setStatus("4");
                }
            }
            Boolean isSunday = TimeZoneUtils.isSundayInTimeZone(dayStr, timezone);
            Boolean isLastDayOfMonth = TimeZoneUtils.isLastDayOfMonthInTimeZone(dayStr, timezone);
            if (isSunday || isLastDayOfMonth) {
                vo.setIsRoundFlag(true);
            }
            if (vo.getStatus().equals("3") || vo.getStatus().equals("4")) {
                CheckInRewardResultVO checkInRewardResultVO = calculateRewardAmount(time, timezone, siteActivityCheckInPO);

                vo.setAcquireAmount(checkInRewardResultVO.getAcquireAmount());
                vo.setAcquireFreeNum(checkInRewardResultVO.getAcquireFreeNum());
                vo.setAcquireSpinNum(checkInRewardResultVO.getAcquireSpinNum());

            }
            SiteCheckInRecordPO recordPO = signedMap.get(dayStr);
            if (recordPO != null) {
                vo.setRewardTypeCode(recordPO.getRewardTypeCode());
                //vo.setIconPic(iconMap.get(recordPO.getRewardTypeCode()));
            }
            vo.setWeekNum(TimeZoneUtils.getDayOfWeekInTimeZone(dayStr, timezone));
            CheckInRewardConfigVO rewardConfigVO = iconMap.get(vo.getWeekNum());
            // 设置图片
            String rewardType = rewardConfigVO.getRewardType();
            if (rewardType.equals(CheckInRewardTypeEnum.AMOUNT.getType())) {
                vo.setIconPic(siteActivityCheckInPO.getAmountPic());
                vo.setAcquireAmount(rewardConfigVO.getAcquireAmount());
            } else if (rewardType.equals(CheckInRewardTypeEnum.SPIN_WHEEL.getType())) {
                vo.setIconPic(siteActivityCheckInPO.getSpinWheelPic());
                vo.setAcquireSpinNum(rewardConfigVO.getAcquireNum());
            } else if (rewardType.equals(CheckInRewardTypeEnum.FREE_WHEEL.getType())) {
                vo.setIconPic(siteActivityCheckInPO.getFreeWheelPic());
                vo.setAcquireFreeNum(rewardConfigVO.getAcquireNum());
            }
            // 每天的奖励

            list.add(vo);
        }

        return list;
    }

    /**
     * 获取有效的签到活动配置
     *
     * @param siteCode 站点编码
     * @return Pair对象，left 是活动基础配置，right 是签到配置
     */
    public SiteActivityCheckInPO getValidCheckInConfig(String siteCode) {

        long current = System.currentTimeMillis();

        LambdaQueryWrapper<SiteActivityBasePO> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        baseWrapper.eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.CHECKIN.getType());
        baseWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        baseWrapper.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        baseWrapper.last(" limit 1 ");
        SiteActivityBasePO activityBasePO = baseRepository.selectOne(baseWrapper);
        if (activityBasePO == null) {
            return null;
        }

        // 活动有效性判断
        if (Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), activityBasePO.getActivityDeadline())) {
            if (current < activityBasePO.getActivityStartTime()) {
                return null;
            }
        }

        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), activityBasePO.getActivityDeadline())) {
            if (current < activityBasePO.getActivityStartTime() || current > activityBasePO.getActivityEndTime()) {
                return null;
            }
        }

        LambdaQueryWrapper<SiteActivityCheckInPO> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(SiteActivityCheckInPO::getBaseId, activityBasePO.getId());
        checkInWrapper.eq(SiteActivityCheckInPO::getSiteCode, activityBasePO.getSiteCode());
        checkInWrapper.last(" limit 1 ");
        SiteActivityCheckInPO siteActivityCheckInPO = checkInRepository.selectOne(checkInWrapper);
        if (siteActivityCheckInPO == null) {
            return null;
        }

        return siteActivityCheckInPO;
    }

    /**
     * 获取有效的签到活动配置
     *
     * @param siteCode 站点编码
     * @return Pair对象，left 是活动基础配置，right 是签到配置
     */
    public ActivityConfigDTO getBaseCheckInConfig(String siteCode) {
        ActivityConfigDTO activityConfigDTO = new ActivityConfigDTO();
        long current = System.currentTimeMillis();

        LambdaQueryWrapper<SiteActivityBasePO> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(SiteActivityBasePO::getSiteCode, siteCode);
        baseWrapper.eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.CHECKIN.getType());
        baseWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        baseWrapper.eq(SiteActivityBasePO::getDeleteFlag, 1);
        baseWrapper.last(" limit 1 ");
        SiteActivityBasePO activityBasePO = baseRepository.selectOne(baseWrapper);
        if (activityBasePO == null) {
            return null;
        }

        // 活动有效性判断
        if (Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), activityBasePO.getActivityDeadline())) {
            if (current < activityBasePO.getActivityStartTime()) {
                return null;
            }
        }

        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), activityBasePO.getActivityDeadline())) {
            if (current < activityBasePO.getActivityStartTime() || current > activityBasePO.getActivityEndTime()) {
                return null;
            }
        }

        LambdaQueryWrapper<SiteActivityCheckInPO> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(SiteActivityCheckInPO::getBaseId, activityBasePO.getId());
        checkInWrapper.eq(SiteActivityCheckInPO::getSiteCode, activityBasePO.getSiteCode());
        checkInWrapper.last(" limit 1 ");
        SiteActivityCheckInPO siteActivityCheckInPO = checkInRepository.selectOne(checkInWrapper);
        if (siteActivityCheckInPO == null) {
            return null;
        }
        activityConfigDTO.setBasePO(activityBasePO);
        activityConfigDTO.setCheckInPO(siteActivityCheckInPO);
        return activityConfigDTO;

    }

    /**
     * 查询用户在指定时间段内的签到记录（用于判断是否已签到）
     *
     * @param siteCode    站点编码
     * @param userId      用户ID
     * @param startMillis 起始时间（包含）
     * @param endMillis   结束时间（包含）
     * @return 当天的签到记录列表
     */
    public List<SiteCheckInRecordPO> getTodayCheckInRecord(String siteCode, String userId, Long startMillis, Long endMillis) {
        LambdaQueryWrapper<SiteCheckInRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteCheckInRecordPO::getSiteCode, siteCode);
        queryWrapper.eq(SiteCheckInRecordPO::getUserId, userId);
        queryWrapper.ge(SiteCheckInRecordPO::getDayMillis, startMillis);
        queryWrapper.le(SiteCheckInRecordPO::getDayMillis, endMillis);
        return this.baseMapper.selectList(queryWrapper);
    }

    @DistributedLock(name = RedisConstants.ACTIVITY_CHECK_IN_APP_LOCK_KEY, unique = "#build.userId", waitTime = RedisLockConstants.WAIT_TIME, leaseTime = RedisLockConstants.UNLOCK_TIME)
    public ResponseVO<CheckInRewardResultRespVO> checkIn(UserBaseReqVO build) {
        // 校验时间是否是当前时间
        long currentTime = System.currentTimeMillis();
        String timeZone = build.getTimezone();
        // 获取奖励的时间，如果是补签的，则是补签的当天开始时间
        Long timeStartToday = TimeZoneUtils.getStartOfDayInTimeZone(currentTime, timeZone);
        Long timeEndToday = TimeZoneUtils.getEndOfDayInTimeZone(currentTime, timeZone);
        Long monthStart = TimeZoneUtils.getStartOfMonthInTimeZone(currentTime, timeZone);
        long monthEnd = TimeZoneUtils.getEndOfMonthInTimeZone(currentTime, timeZone);
        String dateTime = TimeZoneUtils.convertUtcStartOfDayToLocalDate(timeStartToday, timeZone);
        // 那天的签到
        String dayStr = build.getDateStr();
        Long yesterday = TimeZoneUtils.getStartOfYesterdayInTimeZone(currentTime, timeZone);
        List<String> days = TimeZoneUtils.getBetweenDates(monthStart, yesterday, timeZone);
        // 是否今天奖励，ture：是，false：补签
        boolean isTodayReward = true;
        // 0. 获取配置
        Long current = System.currentTimeMillis();
        LambdaQueryWrapper<SiteActivityBasePO> baseWrapper = new LambdaQueryWrapper<>();
        baseWrapper.eq(SiteActivityBasePO::getSiteCode, build.getSiteCode());
        baseWrapper.eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.CHECKIN.getType());
        baseWrapper.eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        baseWrapper.eq(SiteActivityBasePO::getDeleteFlag, EnableStatusEnum.ENABLE.getCode());
        baseWrapper.last(" limit 1 ");
        SiteActivityBasePO activityBasePO = baseRepository.selectOne(baseWrapper);
        if (activityBasePO == null) {
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        //限时活动判断逻辑
        if (Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), activityBasePO.getActivityDeadline())) {
            if (!(current >= activityBasePO.getActivityStartTime())) {
                log.info("限时活动判断逻辑,当前时间不再活动有效时间之内");
                return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
            }
        }

        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), activityBasePO.getActivityDeadline())) {
            if (!(current >= activityBasePO.getActivityStartTime() && current <= activityBasePO.getActivityEndTime())) {
                log.info("长期,获取签到详情,当前时间不再活动有效时间之内");
                return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
            }
        }
        LambdaQueryWrapper<SiteActivityCheckInPO> checkInWrapper = new LambdaQueryWrapper<>();
        checkInWrapper.eq(SiteActivityCheckInPO::getBaseId, activityBasePO.getId());
        checkInWrapper.eq(SiteActivityCheckInPO::getSiteCode, activityBasePO.getSiteCode());
        checkInWrapper.last(" limit 1 ");
        SiteActivityCheckInPO siteActivityCheckInPO = checkInRepository.selectOne(checkInWrapper);
        // 获取这个月签到记录-只统计周签到记录
        List<SiteCheckInRecordPO> currentMonthRecords = queryCheckInRecords(build.getSiteCode(), build.getUserId(), 1, monthStart, monthEnd);

        // 是否需要补签条件
        boolean isNotHasMakeup = false;
        if (siteActivityCheckInPO.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0 && siteActivityCheckInPO.getMakeDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
            isNotHasMakeup = true;
        }
        if (siteActivityCheckInPO == null) {
            return ResponseVO.fail(ResultCode.ACTIVITY_NOT_OPEN);
        }
        if (siteActivityCheckInPO.getMakeBetAmount() == null) {
            siteActivityCheckInPO.setMakeBetAmount(BigDecimal.ZERO);
        }
        if (siteActivityCheckInPO.getMakeupLimit() == null) {
            siteActivityCheckInPO.setMakeupLimit(0);
        }
        if (siteActivityCheckInPO.getMakeDepositAmount() == null) {
            siteActivityCheckInPO.setMakeDepositAmount(BigDecimal.ZERO);
        }
        Boolean makeupFlagStatus = true;
        if (siteActivityCheckInPO.getMakeupLimit().compareTo(0) == 0
                && siteActivityCheckInPO.getMakeDepositAmount().compareTo(BigDecimal.ZERO) == 0
                && siteActivityCheckInPO.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0) {
            siteActivityCheckInPO.setMakeupLimit(0);
            makeupFlagStatus = false;

        }
        UserInfoVO userInfoVO = userInfoApi.getByUserId(build.getUserId());
        if (!dateTime.equals(dayStr)) {
            if (!days.contains(dayStr)) {
                log.info("补签日期错误");
                return ResponseVO.fail(ResultCode.PARAM_ERROR);
            }
            // 1。当日未签到，2。如果没有补签次数， 则提示先签到，后补签。
            Long finalTimeStartToday = timeStartToday;
            Optional<SiteCheckInRecordPO> first = currentMonthRecords.stream()
                    .filter(record -> record.getDayMillis().equals(finalTimeStartToday)).findFirst();
            Integer makeupCount = getMakeupCount(build.getSiteCode(), build.getUserId(), timeZone);
            if (!first.isPresent()) {
                // 当日未签到。校验是否有签到次数. 当有补签条件
                if (makeupCount <= 0 && !isNotHasMakeup) {
                    return ResponseVO.fail(ResultCode.CHECKIN_NOT_MEET_CURRENT_TODAY);
                }
                // 如果没有补签条件，直接补签
            }
            isTodayReward = false;
            int makeupRecordCount = countMakeupCheckIns(currentMonthRecords, timeZone);
            //实际签到次数
            if (makeupRecordCount >= siteActivityCheckInPO.getMakeupLimit()) {
                return ResponseVO.fail(ResultCode.CHECKIN_NOT_MAKEUP_LIMIT);
            }
            // 判断是否有补签次数
            if (!isNotHasMakeup) {
                if (makeupCount <= 0) {
                    log.info("没有补签次数，判断是否需要补签次数，上面已经判断了，现在需要判断如何获取补签次数");
                    //if (!checkMakeupDepositCondition(userInfoVO, timeStartToday, timeEndToday, siteActivityCheckInPO)) {
                    //    return ResponseVO.fail(ResultCode.DEPOSIT_NOT_MEET);
                    //}
                    //// 有效投注，补签只需要是否有补签次数即可
                    //if (!checkMakeupBetCondition(userInfoVO, timeStartToday, timeEndToday, siteActivityCheckInPO)) {
                    //    return ResponseVO.fail(ResultCode.BET_NOT_MEET);
                    //}
                    // 再次判断实际
                    return ResponseVO.fail(ResultCode.CHECKIN_NOT_MAKEUP_LIMIT_REQ);
                }
            }
            // 转换当天奖励的时间
            timeStartToday = TimeZoneUtils.getStartOfDayTimestamp(dayStr, timeZone);
            timeEndToday = TimeZoneUtils.getEndOfDayTimestamp(dayStr, timeZone);

        }

        // 就是指定时间那天是否签到
        // 查询当天是否已经签到
        List<SiteCheckInRecordPO> list = getTodayCheckInRecord(build.getSiteCode(), build.getUserId(), timeStartToday, timeEndToday);
        if (CollectionUtil.isNotEmpty(list)) {
            return ResponseVO.fail(ResultCode.CHECKIN_OUT_ALREADY);
        }

        // 1. 判断今天是否完成
        // 今天存储，补签只需要是否有补签次数即可
        if (isTodayReward && !checkDepositCondition(userInfoVO, timeStartToday, timeEndToday, siteActivityCheckInPO)) {
            return ResponseVO.fail(ResultCode.DEPOSIT_NOT_MEET);
        }
        // 有效投注，补签只需要是否有补签次数即可
        if (isTodayReward && !checkBetCondition(userInfoVO, timeStartToday, timeEndToday, siteActivityCheckInPO)) {
            return ResponseVO.fail(ResultCode.BET_NOT_MEET);
        }
        // 2. 计算奖励，返回前端进行汇总
        CheckInRewardResultVO checkInRewardResultVO = new CheckInRewardResultVO();
        // 插入签到记录
        List<CheckInRewardResultVO> rewardResultVOS = calculateRewardAmountForCheckIn(timeStartToday, timeZone, siteActivityCheckInPO, build);
        for (CheckInRewardResultVO rewardResultVO : rewardResultVOS) {
            checkInRewardResultVO.setAcquireAmount(checkInRewardResultVO.getAcquireAmount().add(rewardResultVO.getAcquireAmount() != null ? rewardResultVO.getAcquireAmount() : BigDecimal.ZERO));
            checkInRewardResultVO.setAcquireFreeNum(checkInRewardResultVO.getAcquireFreeNum() + (rewardResultVO.getAcquireFreeNum() != null ? rewardResultVO.getAcquireFreeNum() : 0));
            checkInRewardResultVO.setAcquireSpinNum(checkInRewardResultVO.getAcquireSpinNum() + (rewardResultVO.getAcquireSpinNum() != null ? rewardResultVO.getAcquireSpinNum() : 0));
            List<CheckInRewardFreeGamePPDTO> freeGamePPDTOList = checkInRewardResultVO.getFreeGamePPDTOList();
            if (CollectionUtil.isEmpty(freeGamePPDTOList)) {
                freeGamePPDTOList = new ArrayList<>();
            }
            if (CollectionUtil.isNotEmpty(rewardResultVO.getFreeGamePPDTOList())) {
                freeGamePPDTOList.addAll(rewardResultVO.getFreeGamePPDTOList());
            }
            checkInRewardResultVO.setFreeGamePPDTOList(freeGamePPDTOList);
        }
        String today = TimeZoneUtils.convertUtcStartOfDayToLocalDate(timeStartToday, timeZone);
        // 一天多次发放
        String orderNo = OrderNoUtils.genOrderNo(userInfoVO.getUserId(), ActivityTemplateEnum.CHECKIN.getSerialNo(), today);
        if (checkInRewardResultVO.getAcquireAmount().compareTo(BigDecimal.ZERO) > 0) {
            // 3.发放奖励
            // 准备发送奖励信息到消息队列 一天只能产生一条
            // String today = String.valueOf(System.currentTimeMillis());
            ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
            activitySendMqVO.setOrderNo(orderNo);
            activitySendMqVO.setSiteCode(userInfoVO.getSiteCode());
            activitySendMqVO.setUserId(userInfoVO.getUserId());
            activitySendMqVO.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            activitySendMqVO.setActivityTemplate(ActivityTemplateEnum.CHECKIN.getType());
            activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
            // 结束时间，写死3天
            //activitySendMqVO.setReceiveEndTime(DateUtils.addDay(new Date(System.currentTimeMillis()), 3).getTime());
            activitySendMqVO.setDistributionType(ActivityDistributionTypeEnum.IMMEDIATE.getCode());
            activitySendMqVO.setActivityAmount(checkInRewardResultVO.getAcquireAmount());
            //activitySendMqVO.setRewardRank(prizeOne.getRewardRank());
            //activitySendMqVO.setPrizeType(prizeOne.getPrizeType());
            //activitySendMqVO.setPrizeName(prizeOne.getPrizeName());
            activitySendMqVO.setActivityId(activityBasePO.getId());
            //平台币转法币
            ResponseVO<SiteCurrencyConvertRespVO> siteCurrencyConvertRespVOResponseVO = siteCurrencyInfoApi.transferToMainCurrency(PlatCurrencyFromTransferVO.builder().siteCode(userInfoVO.getSiteCode()).sourceAmt(checkInRewardResultVO.getAcquireAmount()).targetCurrencyCode(userInfoVO.getMainCurrency()).build());
            if (!siteCurrencyConvertRespVOResponseVO.isOk()) {
                log.error("平台币转法币转换异常:{}", JSONObject.toJSONString(siteCurrencyConvertRespVOResponseVO));
                return ResponseVO.fail(ResultCode.RECEIVE_FAIL_DESCRIPTION);
            }
            BigDecimal mainCurrencyAmount = siteCurrencyConvertRespVOResponseVO.getData().getTargetAmount();
            BigDecimal runningWater = AmountUtils.multiply(mainCurrencyAmount, activityBasePO.getWashRatio());
            activitySendMqVO.setRunningWater(runningWater);
            activitySendMqVO.setRunningWaterMultiple(activityBasePO.getWashRatio());
            //activitySendMqVO.setPrizeType(prizeOne.getPrizeType());
            // 将奖励信息添加到消息队列并发送
            ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
            activitySendListMqVO.setList(CollectionUtil.toList(activitySendMqVO));
            // 发送
            log.error("签到发放奖励:{}", JSONObject.toJSONString(activitySendListMqVO));
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }

        // 赠送转盘次数
        if (checkInRewardResultVO.getAcquireSpinNum() > 0) {

            log.info("签到奖励转盘次数:{}", JSONObject.toJSONString(checkInRewardResultVO));
            VipUpRewardResVO vipUpRewardResVO = VipUpRewardResVO.builder().userId(userInfoVO.getUserId()).userAccount(userInfoVO.getUserAccount()).siteCode(userInfoVO.getSiteCode()).vipGradeCode(userInfoVO.getVipGradeCode()).vipRankCode(userInfoVO.getVipRank()).rewardCounts(checkInRewardResultVO.getAcquireSpinNum()).orderNumber(orderNo).build();
            activitySpinWheelService.handleCheckInReward(vipUpRewardResVO);
        }

        // 4.插入奖励表
        List<SiteCheckInRecordPO> inRecordPOS = buildCheckInRecords(
                rewardResultVOS,
                timeStartToday,
                timeZone,
                userInfoVO,
                orderNo
        );
        if (!CollectionUtil.isEmpty(inRecordPOS)) {
            this.saveBatch(inRecordPOS);
        }
        // 5.减少补签次数
        if (!isTodayReward && !isNotHasMakeup) {
            LambdaUpdateWrapper<SiteMakeupCountBalancePO> makeupCountBalanceWrapper = new LambdaUpdateWrapper<>();
            makeupCountBalanceWrapper.eq(SiteMakeupCountBalancePO::getUserId, userInfoVO.getUserId());
            makeupCountBalanceWrapper.eq(SiteMakeupCountBalancePO::getSiteCode, userInfoVO.getSiteCode());
            makeupCountBalanceWrapper.eq(SiteMakeupCountBalancePO::getMonthMillis, monthStart);
            makeupCountBalanceWrapper.setSql("balance = balance -1 ");
            makeupCountBalanceRepository.update(null, makeupCountBalanceWrapper);
        }
        CheckInRewardResultRespVO checkInRewardResultRespVO = new CheckInRewardResultRespVO();
        // 返回结果
        for (CheckInRewardResultVO rewardResultVO : rewardResultVOS) {
            if (rewardResultVO.getRewardFrom() == 1) {
                checkInRewardResultRespVO.setDailyReward(rewardResultVO);
            } else if (rewardResultVO.getRewardFrom() == 2) {
                checkInRewardResultRespVO.setMonthReward(rewardResultVO);
            } else if (rewardResultVO.getRewardFrom() == 3) {
                checkInRewardResultRespVO.setTotalReward(rewardResultVO);
            }
        }
        // 赠送免费旋转次数
        if (checkInRewardResultVO.getAcquireFreeNum() > 0) {
            List<CheckInRewardFreeGamePPDTO> freeGamePPDTOList = checkInRewardResultVO.getFreeGamePPDTOList();
            if (freeGamePPDTOList != null && freeGamePPDTOList.size() > 0) {
                int i = 0;
                for (CheckInRewardFreeGamePPDTO freeGamePPDTO : freeGamePPDTOList) {
                    List<ActivityFreeGameVO> activityFreeGameVOS = new ArrayList<>();
                    ActivityFreeGameTriggerVO activityFreeGameTriggerVO = new ActivityFreeGameTriggerVO();
                    ActivityFreeGameVO activityFreeGameVO = new ActivityFreeGameVO();

                    activityFreeGameVO.setOrderNo(orderNo + "_" + i++);
                    activityFreeGameVO.setSiteCode(userInfoVO.getSiteCode());
                    activityFreeGameVO.setUserId(userInfoVO.getUserId());
                    activityFreeGameVO.setCurrencyCode(userInfoVO.getMainCurrency());
                    activityFreeGameVO.setActivityId(activityBasePO.getId());
                    activityFreeGameVO.setActivityNo(activityBasePO.getActivityNo());
                    activityFreeGameVO.setActivityTemplate(ActivityTemplateEnum.CHECKIN.getType());
                    activityFreeGameVO.setActivityTemplateName(ActivityTemplateEnum.CHECKIN.getName());
                    activityFreeGameVO.setAcquireNum(freeGamePPDTO.getAcquireNum());
                    // todo 添加四个参数
                    activityFreeGameVO.setVenueCode(VenuePlatformConstants.PP);
                    activityFreeGameVO.setAccessParameters(freeGamePPDTO.getAccessParameters());
                    activityFreeGameVO.setBetLimitAmount(freeGamePPDTO.getBetLimitAmount());
                    activityFreeGameVO.setWashRatio(activityBasePO.getWashRatio());
                    activityFreeGameVOS.add(activityFreeGameVO);
                    activityFreeGameTriggerVO.setFreeGameVOList(activityFreeGameVOS);
                    log.info("签到发放免费旋转：免费旋转开始发放:{}", activityFreeGameTriggerVO);
                    log.info("站点:{}免费旋转开始 赠送免费旋转次数:{}", userInfoVO.getSiteCode(), activityFreeGameVOS.size());
                    KafkaUtil.send(TopicsConstants.FREE_GAME, activityFreeGameTriggerVO);
                }

            }

        }
        return ResponseVO.success(checkInRewardResultRespVO);
    }

    /**
     * 构建签到奖励记录列表
     *
     * @param rewardResultVOS 签到奖励结果列表
     * @param timeStartToday  当天UTC开始毫秒值
     * @param timeZone        当前时区
     * @param userInfoVO      用户信息
     * @param orderNo         奖励发放订单号
     * @return List<SiteCheckInRecordPO>
     */
    private List<SiteCheckInRecordPO> buildCheckInRecords(List<CheckInRewardResultVO> rewardResultVOS,
                                                          long timeStartToday,
                                                          String timeZone,
                                                          UserInfoVO userInfoVO,
                                                          String orderNo) {
        List<SiteCheckInRecordPO> inRecordPOS = new ArrayList<>(rewardResultVOS.size());
        for (CheckInRewardResultVO rewardResultVO : rewardResultVOS) {
            SiteCheckInRecordPO insertPO = new SiteCheckInRecordPO();

            // 时间相关
            insertPO.setDayMillis(timeStartToday); // UTC起始时间戳
            insertPO.setDayStr(TimeZoneUtils.convertUtcStartOfDayToLocalDate(timeStartToday, timeZone)); // 当地日期字符串
            insertPO.setCreatedTime(System.currentTimeMillis());
            insertPO.setUpdatedTime(System.currentTimeMillis());

            // 用户信息
            insertPO.setUserId(userInfoVO.getUserId());
            insertPO.setUserAccount(userInfoVO.getUserAccount());
            insertPO.setMainCurrency(userInfoVO.getMainCurrency());
            insertPO.setAgentId(userInfoVO.getSuperAgentId());
            insertPO.setAgentAccount(userInfoVO.getSuperAgentAccount());
            insertPO.setAccountType(userInfoVO.getAccountType());

            // 活动 & 奖励信息
            insertPO.setStatus(1);
            insertPO.setSiteCode(userInfoVO.getSiteCode());
            insertPO.setVipGradeCode(userInfoVO.getVipGradeCode());
            insertPO.setVipRankCode(userInfoVO.getVipRank());

            insertPO.setRemark(String.format("转盘次数:%d,免费旋转次数:%d,奖励金额:%f WTC",
                    rewardResultVO.getAcquireSpinNum(),
                    rewardResultVO.getAcquireFreeNum(),
                    rewardResultVO.getAcquireAmount()));

            // 构造业务唯一ID（可考虑是否改名）
            insertPO.setUserId(userInfoVO.getUserId());
            insertPO.setOrderNo(orderNo + rewardResultVO.getRewardType() + rewardResultVO.getRewardFrom() + rewardResultVO.getRewardTypeCode());
            insertPO.setRewardType(rewardResultVO.getRewardFrom());
            insertPO.setRewardTypeCode(rewardResultVO.getRewardTypeCode());

            inRecordPOS.add(insertPO);
        }
        return inRecordPOS;
    }

    /**
     * 计算用户签到奖励金额
     *
     * @param timeStart             当天开始时间戳（毫秒）
     * @param siteActivityCheckInPO 签到活动配置信息
     * @return 返回计算后的奖励金额信息
     */
    private CheckInRewardResultVO calculateRewardAmount(Long timeStart, String timezone, SiteActivityCheckInPO siteActivityCheckInPO) {


        CheckInRewardResultVO checkInRewardResultVO = new CheckInRewardResultVO();
        // 获取今天的日期（从时间戳获取日期）
        ZoneId zoneId = ZoneId.of(timezone);
        LocalDate today = Instant.ofEpochMilli(timeStart).atZone(zoneId).toLocalDate();

        // 获取今天是周几（1=周一，7=周日）
        int dayOfWeek = today.getDayOfWeek().getValue();

        // 累计奖励是否可获取
        boolean isTotalReward = false;
        // 计算累计奖励的逻辑, 当月累计获取奖励天数


        // 获取今天是否是月末（最后一天）
        boolean isEndOfMonth = today.lengthOfMonth() == today.getDayOfMonth();


        // 根据签到活动配置信息计算奖励
        BigDecimal rewardAmount = BigDecimal.ZERO;
        //JSON.parseArray(checkInPO.getRewardMonth(), CheckInRewardConfigVO.class)
        List<CheckInRewardConfigVO> weekRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardWeek(), CheckInRewardConfigVO.class);
        List<CheckInRewardConfigVO> monthRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardMonth(), CheckInRewardConfigVO.class);

        // 今天的奖励
        Integer day = TimeZoneUtils.getDayOfWeekInTimeZone(timeStart, timezone);
        if (day >= 1 && day <= 7) {
            CheckInRewardConfigVO checkInRewardConfigVO = weekRewardConfigs.stream().filter(config -> config.getCode() == day).findFirst().orElse(null);
            processReward(checkInRewardConfigVO, checkInRewardResultVO, 1);

        }
        // 计算奖励金额的逻辑，可以根据星期、是否月末等条件调整
        if (isTotalReward && false) {
            // 如果今天是周日，增加周日奖励
            CheckInRewardConfigVO sundayRewardConfig = weekRewardConfigs.stream().filter(config -> config.getCode() == 8).findFirst().orElse(null);
            log.info("今天是周日，给定奖励");
            processReward(sundayRewardConfig, checkInRewardResultVO, 2);

        }

        if (isEndOfMonth && false) {
            // 如果今天是月末，增加月末奖励
            int month = TimeZoneUtils.getMonthOfYearInTimeZone(timeStart, timezone);
            // 如果今天是周日，增加周日奖励
            CheckInRewardConfigVO mothReward = monthRewardConfigs.stream().filter(config -> config.getCode() == month).findFirst().orElse(null);
            log.info("今天是月末，给定奖励");
            processReward(mothReward, checkInRewardResultVO, 3);
        }
        // 设置奖励金额并返回结果

        return checkInRewardResultVO;


    }

    /**
     * 判断是否可领取的总奖励配置（如果未领取则返回 null）
     *
     * @param totalRewardConfigs 阶梯奖励配置列表（已按 code 升序排序）
     * @param rewardList         已领取奖励记录列表
     * @param weekRewardCount    本周累计签到天数
     * @return 已领取的奖励配置对象，若未领取则返回 null
     */
    public CheckInRewardConfigVO getReceivedTotalReward(List<CheckInRewardConfigVO> totalRewardConfigs,
                                                        List<SiteCheckInRecordPO> rewardList,
                                                        int weekRewardCount) {
        for (int i = 0; i < totalRewardConfigs.size(); i++) {
            CheckInRewardConfigVO currentConfig = totalRewardConfigs.get(i);
            int dayLimit = currentConfig.getDayLimit();

            boolean isLast = (i == totalRewardConfigs.size() - 1);
            boolean reachedCurrent = weekRewardCount >= dayLimit;
            boolean notBeyondNext = !isLast && weekRewardCount < totalRewardConfigs.get(i + 1).getDayLimit();

            if ((reachedCurrent && (isLast || notBeyondNext)) &&
                    !hasReceivedTotalReward(rewardList, currentConfig.getCode())) {
                return currentConfig;
            }
        }
        return null;
    }

    /**
     * 判断是否已领取指定编号的总奖励（rewardType 为 3）。
     *
     * @param rewardList 奖励记录列表，包含已领取的奖励信息
     * @param code       奖励配置编号（RewardTypeCode），用于匹配对应的奖励记录
     * @return 如果已领取该奖励则返回 true，否则返回 false
     */
    private boolean hasReceivedTotalReward(List<SiteCheckInRecordPO> rewardList, int code) {
        return rewardList.stream()
                .anyMatch(item -> item.getRewardType() == 3 && item.getRewardTypeCode() == code);
    }

    /**
     * 判断是否已领取指定编号的总奖励（rewardType 为 2）。
     *
     * @param rewardList 奖励记录列表，包含已领取的奖励信息
     * @param code       奖励配置编号（RewardTypeCode），用于匹配对应的奖励记录
     * @return 如果已领取该奖励则返回 true，否则返回 false
     */
    private boolean hasReceivedMonthReward(List<SiteCheckInRecordPO> rewardList, int code) {
        return rewardList.stream()
                .anyMatch(item -> item.getRewardType() == 3 && item.getRewardTypeCode() == code);
    }

    /**
     * 查询指定用户在指定月份内的有效签到奖励记录列表
     *
     * @param userId     用户ID
     * @param siteCode   站点编码
     * @param monthStart 月初时间（含）
     * @param monthEnd   月末时间（含）
     * @return 满足条件的签到奖励记录列表
     */
    public List<SiteCheckInRecordPO> getUserMonthlyCheckInRewards(String userId, String siteCode, long monthStart, long monthEnd) {
        LambdaQueryWrapper<SiteCheckInRecordPO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteCheckInRecordPO::getUserId, userId);
        wrapper.eq(SiteCheckInRecordPO::getSiteCode, siteCode);
        wrapper.eq(SiteCheckInRecordPO::getStatus, 1);
        wrapper.between(SiteCheckInRecordPO::getDayMillis, monthStart, monthEnd);

        return this.baseMapper.selectList(wrapper);
    }

    /**
     * 计算用户签到奖励金额,领取奖励
     * 每周签到奖励（每日）
     * 月末奖励（整月奖励）
     * 累计奖励（根据签到次数判断）
     *
     * @param timeStart             当天开始时间戳（毫秒）
     * @param siteActivityCheckInPO 签到活动配置信息
     * @return 返回计算后的奖励金额信息
     */
    private List<CheckInRewardResultVO> calculateRewardAmountForCheckIn(Long timeStart, String timezone, SiteActivityCheckInPO siteActivityCheckInPO, UserBaseReqVO userBaseReqVO) {
        List<CheckInRewardResultVO> rewardResultVOS = new ArrayList<>(16);
        Long monthStartTime = TimeZoneUtils.getStartOfMonthInTimeZone(timeStart, timezone);
        Long monthEndTime = TimeZoneUtils.getEndOfMonthInTimeZone(timeStart, timezone);

        // 根据签到活动配置信息计算奖励
        List<CheckInRewardConfigVO> weekRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardWeek(), CheckInRewardConfigVO.class);
        List<CheckInRewardConfigVO> monthRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardMonth(), CheckInRewardConfigVO.class);
        List<CheckInRewardConfigVO> totalRewardConfigs = JSON.parseArray(siteActivityCheckInPO.getRewardTotal(), CheckInRewardConfigVO.class);
        // 获取是否可获取累计奖励
        // 获取这个所有奖励
        List<SiteCheckInRecordPO> rewardList = getUserMonthlyCheckInRewards(userBaseReqVO.getUserId(), siteActivityCheckInPO.getSiteCode(), monthStartTime, monthEndTime);
        // 这个月周奖励,因为今天也会签到，加上今天
        int weekRewardCount = (int) rewardList.stream().filter(item -> item.getRewardType() == 1).count() + 1;
        // 按照code的升序排序
        totalRewardConfigs.sort(Comparator.comparing(CheckInRewardConfigVO::getCode));
        // 判断是否可获取累计奖励
        CheckInRewardConfigVO totalReward = getReceivedTotalReward(totalRewardConfigs, rewardList, weekRewardCount);

        // 获取今天是否是月末（最后一天）
        // 今天的奖励
        Integer day = TimeZoneUtils.getDayOfWeekInTimeZone(timeStart, timezone);
        if (day >= 1 && day <= 7) {
            CheckInRewardConfigVO checkInRewardConfigVO = weekRewardConfigs.stream().filter(config -> config.getCode() == day).findFirst().orElse(null);
            processRewardCheckIn(checkInRewardConfigVO, rewardResultVOS, 1, day);

        }
        // 计算奖励金额的逻辑，可以根据星期、是否月末等条件调整
        // 是否可获取累计奖励
        if (totalReward != null) {
            processRewardCheckIn(totalReward, rewardResultVOS, 3, totalReward.getCode());

        }

        // 全月奖励，由于有补签，所以需要判断整个月的奖励奖励+1，rewardType1-周奖励，2-月奖励，3-累计奖励
        List<SiteCheckInRecordPO> list = rewardList.stream().filter(item -> item.getRewardType() == 1).collect(Collectors.toList());
        // 如果是 2月，平年是28天，则有27条记录，闰年是29天，则有28条记录
        // 如果是 1 3 5 7 8 10 12 ，一个月是31天，则有30条记录
        // 如果是 2 4 6 8 9 11 12 ，一个月是30天，则有29条记录
        // 判断这些list大小。
        // month 是代表是那个月
        int dayCount = TimeZoneUtils.getDaysInMonth(timeStart, timezone);
        if (CollectionUtils.isNotEmpty(list) && list.size() >= dayCount - 1) {
            int month = TimeZoneUtils.getMonthOfYearInTimeZone(timeStart, timezone);
            // 判断这个月是否获取，如果获取，则不能获取
            boolean hasReceived = hasReceivedMonthReward(rewardList, month);
            if (!hasReceived) {
                CheckInRewardConfigVO mothReward = monthRewardConfigs.stream().filter(config -> config.getCode() == month).findFirst().orElse(null);
                mothReward.setDayLimit(dayCount);
                log.info("今天是月末，给定奖励");
                processRewardCheckIn(mothReward, rewardResultVOS, 2, mothReward.getCode());

            }
        }
        // 设置奖励金额并返回结果
        return rewardResultVOS;


    }


    /**
     * 处理并计算用户的签到奖励
     * 根据奖励配置的不同类型，更新用户的奖励金额、免费轮次、旋转轮盘次数等。
     *
     * @param rewardConfig          奖励配置对象
     * @param checkInRewardResultVO 用户签到奖励结果对象
     */
    private void processReward(CheckInRewardConfigVO rewardConfig, CheckInRewardResultVO checkInRewardResultVO, Integer rewardType) {
        if (rewardConfig == null || checkInRewardResultVO == null) {
            return;
        }

        // 如果奖励类型是金额，累加到奖励金额
        if (CheckInRewardTypeEnum.AMOUNT.getType().equals(rewardConfig.getRewardType())) {
            BigDecimal currentAmount = checkInRewardResultVO.getAcquireAmount();
            if (currentAmount == null) {
                currentAmount = BigDecimal.ZERO;
            }
            BigDecimal rewardAmount = rewardConfig.getAcquireAmount() == null ? BigDecimal.ZERO : rewardConfig.getAcquireAmount();
            checkInRewardResultVO.setAcquireAmount(currentAmount.add(rewardAmount));
            CheckInRewardResultVO rewardResultVO = new CheckInRewardResultVO();
            rewardResultVO.setAcquireAmount(rewardAmount);
            rewardResultVO.setRewardType(CheckInRewardTypeEnum.AMOUNT.getType());
            /*if (rewardResultVOS != null) {
                rewardResultVOS.add(rewardResultVO);
            }*/
        }
        // 如果奖励类型是免费轮次，累加到免费轮次数量
        else if (CheckInRewardTypeEnum.FREE_WHEEL.getType().equals(rewardConfig.getRewardType())) {
            int currentFreeNum = checkInRewardResultVO.getAcquireFreeNum() == null ? 0 : checkInRewardResultVO.getAcquireFreeNum();
            int rewardNum = rewardConfig.getAcquireNum() == null ? 0 : rewardConfig.getAcquireNum();
            checkInRewardResultVO.setAcquireFreeNum(currentFreeNum + rewardNum);
            CheckInRewardResultVO rewardResultVO = new CheckInRewardResultVO();
            rewardResultVO.setAcquireFreeNum(rewardNum);
            rewardResultVO.setRewardType(CheckInRewardTypeEnum.FREE_WHEEL.getType());

           /* if (rewardResultVOS != null) {
                rewardResultVOS.add(rewardResultVO);
            }*/
        }
        // 如果奖励类型是旋转轮盘，累加到旋转轮盘次数
        else if (CheckInRewardTypeEnum.SPIN_WHEEL.getType().equals(rewardConfig.getRewardType())) {
            int currentSpinNum = checkInRewardResultVO.getAcquireSpinNum() == null ? 0 : checkInRewardResultVO.getAcquireSpinNum();
            int rewardNum = rewardConfig.getAcquireNum() == null ? 0 : rewardConfig.getAcquireNum();
            checkInRewardResultVO.setAcquireSpinNum(currentSpinNum + rewardNum);
            CheckInRewardResultVO rewardResultVO = new CheckInRewardResultVO();
            rewardResultVO.setAcquireSpinNum(rewardNum);
            rewardResultVO.setRewardType(CheckInRewardTypeEnum.SPIN_WHEEL.getType());
           /* if (rewardResultVOS != null) {
                rewardResultVOS.add(rewardResultVO);
            }*/
        }
    }

    /**
     * 处理并计算用户的签到奖励。
     * 根据奖励配置的不同类型，更新用户的奖励金额、免费轮次、旋转轮盘次数等。
     *
     * @param rewardConfig    奖励配置对象
     * @param rewardResultVOS 用户签到奖励结果对象
     * @param rewardType      奖励类型（1=每日，2=每周，3=累计）
     * @param rewardTypeCode  奖励类型对应的配置Code
     */
    private void processRewardCheckIn(CheckInRewardConfigVO rewardConfig,
                                      List<CheckInRewardResultVO> rewardResultVOS,
                                      Integer rewardType,
                                      Integer rewardTypeCode) {
        if (rewardConfig == null || rewardConfig.getRewardType() == null) {
            return;
        }

        CheckInRewardTypeEnum typeEnum = CheckInRewardTypeEnum.of(rewardConfig.getRewardType());
        if (typeEnum == null) {
            return; // 未知类型
        }

        CheckInRewardResultVO rewardResultVO = new CheckInRewardResultVO();
        rewardResultVO.setRewardFrom(rewardType);
        rewardResultVO.setRewardTypeCode(rewardTypeCode);
        rewardResultVO.setRewardDayCount(rewardConfig.getDayLimit());


        switch (typeEnum) {
            case AMOUNT:
                rewardResultVO.setAcquireAmount(
                        Optional.ofNullable(rewardConfig.getAcquireAmount()).orElse(BigDecimal.ZERO));
                rewardResultVO.setRewardType(CheckInRewardTypeEnum.AMOUNT.getType());
                break;
            case FREE_WHEEL:
                rewardResultVO.setAcquireFreeNum(
                        Optional.ofNullable(rewardConfig.getAcquireNum()).orElse(0));
                rewardResultVO.setRewardType(CheckInRewardTypeEnum.FREE_WHEEL.getType());
                CheckInRewardFreeGamePPDTO checkInRewardFreeGamePPDTO = new CheckInRewardFreeGamePPDTO();
                checkInRewardFreeGamePPDTO.setAcquireNum(rewardConfig.getAcquireNum());
                checkInRewardFreeGamePPDTO.setVenueCode(rewardConfig.getVenueCode());
                checkInRewardFreeGamePPDTO.setAccessParameters(rewardConfig.getAccessParameters());
                checkInRewardFreeGamePPDTO.setBetLimitAmount(rewardConfig.getBetLimitAmount());
                List<CheckInRewardFreeGamePPDTO> freeGamePPDTOList = rewardResultVO.getFreeGamePPDTOList();
                if (freeGamePPDTOList == null) {
                    freeGamePPDTOList = new ArrayList<>();
                }
                freeGamePPDTOList.add(checkInRewardFreeGamePPDTO);
                rewardResultVO.setFreeGamePPDTOList(freeGamePPDTOList);
                break;
            case SPIN_WHEEL:
                rewardResultVO.setAcquireSpinNum(
                        Optional.ofNullable(rewardConfig.getAcquireNum()).orElse(0));
                rewardResultVO.setRewardType(CheckInRewardTypeEnum.SPIN_WHEEL.getType());
                break;
            default:
                return;
        }

        rewardResultVOS.add(rewardResultVO);
    }

    /**
     * 校验用户在指定时间范围内是否满足签到活动所需的存款条件。
     *
     * <p>当活动配置中 `depositAmount` > 0 时，系统会查询用户在指定时间段的总存款金额，
     * 并将其转换为平台币进行比较，判断是否满足最低存款门槛。</p>
     *
     * @param userInfoVO            用户信息对象，包含用户ID、账户、站点信息、主货币等
     * @param timeStart             活动起始时间（毫秒时间戳）
     * @param timeEnd               活动结束时间（毫秒时间戳）
     * @param siteActivityCheckInPO 签到活动配置信息，包括最低存款要求等
     * @return true 表示满足存款条件；false 表示不满足
     */
    private Boolean checkDepositCondition(UserInfoVO userInfoVO, Long timeStart, Long timeEnd, SiteActivityCheckInPO siteActivityCheckInPO) {
        // 如果没有配置存款条件
        if (siteActivityCheckInPO.getDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        if (siteActivityCheckInPO.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder().userIds(Collections.singletonList(userInfoVO.getUserId())).startTime(timeStart).endTime(timeEnd).build());
            if (CollectionUtil.isEmpty(userDepositAmountVOS)) {
                log.info("会员没有存款:{}", userInfoVO.getUserAccount());
                return false;
            }
            BigDecimal depositAmount = userDepositAmountVOS.get(0).getAmount();
            // 主货币转换未平台币
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfoVO.getSiteCode());
            BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
            // 会员存款转换为平台币
            BigDecimal userDepositPlat = depositAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
            if (userDepositPlat.compareTo(siteActivityCheckInPO.getDepositAmount()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验用户在指定时间范围内是否满足签到补签活动所需的投注条件。
     *
     * <p>当活动配置中 `betAmount` > 0 时，系统会查询用户在指定时间段的总有效投注金额，
     * 并将其转换为平台币进行比较，判断是否满足最低投注门槛。</p>
     *
     * @param userInfoVO            用户信息对象，包含用户ID、账户、站点信息、主货币等
     * @param timeStart             活动起始时间（毫秒时间戳）
     * @param timeEnd               活动结束时间（毫秒时间戳）
     * @param siteActivityCheckInPO 签到活动配置信息，包括最低投注要求等
     * @return true 表示满足投注条件；false 表示不满足
     */
    private Boolean checkMakeupBetCondition(UserInfoVO userInfoVO, Long timeStart, Long timeEnd, SiteActivityCheckInPO siteActivityCheckInPO) {
        // 如果没有配置投注
        if (siteActivityCheckInPO.getBetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        if (siteActivityCheckInPO.getBetAmount().compareTo(BigDecimal.ZERO) > 0) {
            ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO.builder().userIdList(Collections.singletonList(userInfoVO.getUserId())).startTime(timeStart).endTime(timeEnd).siteCode(userInfoVO.getSiteCode()).build();
            Page<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOPage = reportUserVenueFixedWinLoseApi.queryUserBetsTop(userTopReqVO);

            if (CollectionUtil.isEmpty(ReportUserVenueBetsTopVOPage.getRecords())) {
                log.info("会员没有投注:{}", userInfoVO.getUserAccount());
                return false;
            }
            // 获取用户有效投注金额
            BigDecimal betAmount = ReportUserVenueBetsTopVOPage.getRecords().get(0).getValidAmount();
            // 获取平台币兑换汇率
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfoVO.getSiteCode());
            BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
            // 会员投注转换为平台币
            BigDecimal userBetPlat = betAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
            if (userBetPlat.compareTo(siteActivityCheckInPO.getMakeBetAmount()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验用户在指定时间范围内是否满足签到补签活动所需的存款条件。
     *
     * <p>当活动配置中 `depositAmount` > 0 时，系统会查询用户在指定时间段的总存款金额，
     * 并将其转换为平台币进行比较，判断是否满足最低存款门槛。</p>
     *
     * @param userInfoVO            用户信息对象，包含用户ID、账户、站点信息、主货币等
     * @param timeStart             活动起始时间（毫秒时间戳）
     * @param timeEnd               活动结束时间（毫秒时间戳）
     * @param siteActivityCheckInPO 签到活动配置信息，包括最低存款要求等
     * @return true 表示满足存款条件；false 表示不满足
     */
    private Boolean checkMakeupDepositCondition(UserInfoVO userInfoVO, Long timeStart, Long timeEnd, SiteActivityCheckInPO siteActivityCheckInPO) {
        // 如果没有配置存款条件
        if (siteActivityCheckInPO.getDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }

        if (siteActivityCheckInPO.getDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder().userIds(Collections.singletonList(userInfoVO.getUserId())).startTime(timeStart).endTime(timeEnd).build());
            if (CollectionUtil.isEmpty(userDepositAmountVOS)) {
                log.info("会员没有存款:{}", userInfoVO.getUserAccount());
                return false;
            }
            BigDecimal depositAmount = userDepositAmountVOS.get(0).getAmount();
            // 主货币转换未平台币
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfoVO.getSiteCode());
            BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
            // 会员存款转换为平台币
            BigDecimal userDepositPlat = depositAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
            if (userDepositPlat.compareTo(siteActivityCheckInPO.getMakeDepositAmount()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 校验用户在指定时间范围内是否满足签到活动所需的投注条件。
     *
     * <p>当活动配置中 `betAmount` > 0 时，系统会查询用户在指定时间段的总有效投注金额，
     * 并将其转换为平台币进行比较，判断是否满足最低投注门槛。</p>
     *
     * @param userInfoVO            用户信息对象，包含用户ID、账户、站点信息、主货币等
     * @param timeStart             活动起始时间（毫秒时间戳）
     * @param timeEnd               活动结束时间（毫秒时间戳）
     * @param siteActivityCheckInPO 签到活动配置信息，包括最低投注要求等
     * @return true 表示满足投注条件；false 表示不满足
     */
    private Boolean checkBetCondition(UserInfoVO userInfoVO, Long timeStart, Long timeEnd, SiteActivityCheckInPO siteActivityCheckInPO) {
        // 如果没有配置投注
        if (siteActivityCheckInPO.getBetAmount().compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        if (siteActivityCheckInPO.getBetAmount().compareTo(BigDecimal.ZERO) > 0) {
            ReportUserTopReqVO userTopReqVO = ReportUserTopReqVO.builder().userIdList(Collections.singletonList(userInfoVO.getUserId())).startTime(timeStart).endTime(timeEnd).siteCode(userInfoVO.getSiteCode()).build();
            Page<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOPage = reportUserVenueFixedWinLoseApi.queryUserBetsTop(userTopReqVO);

            if (CollectionUtil.isEmpty(ReportUserVenueBetsTopVOPage.getRecords())) {
                log.info("会员没有投注:{}", userInfoVO.getUserAccount());
                return false;
            }
            // 获取用户有效投注金额
            BigDecimal betAmount = ReportUserVenueBetsTopVOPage.getRecords().get(0).getValidAmount();
            // 获取平台币兑换汇率
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfoVO.getSiteCode());
            BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
            // 会员投注转换为平台币
            BigDecimal userBetPlat = betAmount.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
            if (userBetPlat.compareTo(siteActivityCheckInPO.getBetAmount()) < 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 处理用户的投注信息，用于签到活动，添加补签次数
     *
     * @param sendVO 投注信息对象，包含用户ID、投注金额、投注时间等
     * @return true 表示投注信息已处理；false 表示投注信息未处理
     */
    public boolean processCheckinBet(UserVenueWinLossSendVO sendVO) {
        if (CollectionUtil.isEmpty(sendVO.getVoList())) {
            return false;
        }
        long now = System.currentTimeMillis(); // 避免多次调用
        // 1. 按 siteCode 分组
        List<String> siteCodes = sendVO.getVoList().stream().map(UserVenueWinLossMqVO::getSiteCode).distinct().collect(Collectors.toList());
        for (String siteCode : siteCodes) {
            // 2. 获取当前站点的签到活动配置
            SiteActivityCheckInPO validCheckInConfig = getValidCheckInConfig(siteCode);
            if (validCheckInConfig == null) {
                log.info("【签到活动】站点 {} 未配置签到活动", siteCode);
                continue;
            }
            if (validCheckInConfig.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0) {
                log.info("【签到活动】站点 {} 签到活动未配置有效金额", siteCode);
                continue;
            }
            if (validCheckInConfig.getMakeupLimit() <= 0) {
                log.info("【签到活动】站点 {} 补签上限配置为 0", siteCode);
                continue;
            }
            // 4. 获取当前站点所有 userId
            List<String> userIds = sendVO.getVoList().stream()
                    .filter(e -> e.getSiteCode().equals(siteCode))
                    .map(UserVenueWinLossMqVO::getUserId)
                    .distinct()
                    .collect(Collectors.toList());
            log.info("处理流程 签到活动补签次数{}", siteCode);
            userIds = checkJoinTaskUserId(userIds);
            if (CollectionUtil.isEmpty(userIds)) {
                log.info("【签到活动】站点 {} 过滤后无有效用户参与", siteCode);
                continue;
            }
            log.info("处理流程 签到活动补签次数,userId:{}", JSONObject.toJSONString(userIds));
            // 5. 获取站点时区相关时间范围
            String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
            long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            long yesterdayEndTime = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), timeZone);
            long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            long startMonthTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
            long endMonthTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
            // 6. 查询用户当日有效投注额（按币种汇总）
            List<ReportUserVenueBetsTopVO> queryUserBetsTop = queryUserBetsTop(siteCode, userIds, timeStart, timeEnd);
            if (CollectionUtil.isEmpty(queryUserBetsTop)) {
                continue;
            }
            // 7. 查询用户当日存款额（如活动配置要求）
            Map<String, UserDepositAmountVO> userDepositAmountVOMap = new HashMap<>();
            if (validCheckInConfig.getMakeDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                // 2 查看用户存款
                List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder().userIds(userIds).startTime(timeStart).endTime(timeEnd).build());
                userDepositAmountVOMap = userDepositAmountVOS.stream().collect(Collectors.toMap(UserDepositAmountVO::getUserId, e -> e));
            }
            // 8. 获取汇率（币种 -> 平台币）
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
            for (ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO : queryUserBetsTop) {
                // 处理每条记录
                UserInfoVO userInfo = userInfoApi.getByUserId(ReportUserVenueBetsTopVO.getUserId());

                processUserMakeupCheckIn(ReportUserVenueBetsTopVO, allFinalRate, validCheckInConfig, userDepositAmountVOMap, userInfo, startMonthTime, endMonthTime, timeStart, yesterdayEndTime, timeZone);
            }

        }

        return true;
    }

    public void processUserMakeupCheckIn(ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO,
                                         Map<String, BigDecimal> allFinalRate,
                                         SiteActivityCheckInPO validCheckInConfig,
                                         Map<String, UserDepositAmountVO> userDepositAmountVOMap,
                                         UserInfoVO userInfo,
                                         long startMonthTime,     // long 时间戳，单位毫秒
                                         long endMonthTime,
                                         long timeStart, // 今天开始时间
                                         long yesterdayEndTime,
                                         String timeZone) {

        RLock lock = RedisUtil.getFairLock(RedisConstants.ACTIVITY_CHECK_IN_ADD_LOCK_KEY + userInfo.getUserId());
        boolean locked = false;
        try {
            locked = lock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("无法获取用户 {} 的补签锁", userInfo.getUserId());
                return;
            }
            // 处理每条记录
            BigDecimal finalRate = allFinalRate.get(userInfo.getMainCurrency());
            if (finalRate == null) {
                log.warn("【签到活动】站点 {} 用户 {} 无法获取币种 {} 的汇率", userInfo.getSiteCode(), userInfo.getUserId(), userInfo.getMainCurrency());
                return;
            }
            // 9. 查询用户本月已补签次数
            int makeupCount = queryUserMonthlyMakeupCount(ReportUserVenueBetsTopVO.getUserId(), userInfo.getSiteCode(), startMonthTime, endMonthTime);
            if (makeupCount >= validCheckInConfig.getMakeupLimit()) {
                return;
            }
            // 还有多少次补签次数
            int makeupLeftCount = validCheckInConfig.getMakeupLimit() - makeupCount;
            if (makeupLeftCount <= 0) return;
            int betAndDepositTime = 0;

            // 计算投注可获得补签次数
            if (validCheckInConfig.getMakeBetAmount().compareTo(BigDecimal.ZERO) > 0) {
                if (ReportUserVenueBetsTopVO == null) return;
                // 10. 计算投注转为平台币金额，并判断是否达标
                BigDecimal userPlatFormBetAmount = AmountUtils.divide(ReportUserVenueBetsTopVO.getValidAmount(), finalRate).subtract(validCheckInConfig.getBetAmount());
                // 判断是否达标
                if (userPlatFormBetAmount.compareTo(validCheckInConfig.getMakeBetAmount()) < 0) return;
                log.info("签到活动补签投注流水达标的用户{}", ReportUserVenueBetsTopVO.getUserId());
                // 计算多少倍
                int betTimes = userPlatFormBetAmount.divide(validCheckInConfig.getMakeBetAmount(), 0, RoundingMode.DOWN).intValue();
                betAndDepositTime = betTimes;
            } else {
                betAndDepositTime = makeupLeftCount;
            }
            // 计算存款可获得补签次数

            if (validCheckInConfig.getMakeDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
                UserDepositAmountVO userDepositAmountVO = userDepositAmountVOMap.get(ReportUserVenueBetsTopVO.getUserId());
                // 如果配置补签存款，但是存款未达到，也返回
                if (userDepositAmountVO == null) {
                    return;
                }
                BigDecimal userDepositAmount = userDepositAmountVO.getAmount();
                // 减去签到存款金额
                BigDecimal userDepositPlat = userDepositAmount.divide(finalRate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN).subtract(validCheckInConfig.getDepositAmount());
                if (userDepositPlat.compareTo(BigDecimal.ZERO) <= 0) return;
                log.info("签到活动补签投注流水和存款达标用户{}", ReportUserVenueBetsTopVO.getUserId());
                // 计算多少倍
                int depositTimes = userDepositPlat.divide(validCheckInConfig.getMakeDepositAmount(), 0, RoundingMode.DOWN).intValue();
                betAndDepositTime = Math.min(betAndDepositTime, depositTimes);

            }
            // 补签次数是今天的，需要减去今天已经补签的次数
            Integer makeupCountSumToday = getUserMakeupCountSum(ReportUserVenueBetsTopVO.getUserId(), userInfo.getSiteCode(), timeStart, timeStart);
            // 补签次数是今天的，需要减去今天已经补签的次数
            betAndDepositTime = Math.max(betAndDepositTime - makeupCountSumToday, 0);
            if (betAndDepositTime <= 0) {
                return;
            }
            log.info("签到活动补签投注流水达标用户{}", ReportUserVenueBetsTopVO.getUserId());
            // 12. 查询签到天数 & 计算缺签天数
            int checkInCount = queryCheckInCount(ReportUserVenueBetsTopVO.getUserId(), startMonthTime, yesterdayEndTime);
            // 一共多少天, 理论上签到次数
            List<String> dayCount = TimeZoneUtils.getBetweenDates(startMonthTime, yesterdayEndTime, timeZone);
            if (CollectionUtil.isEmpty(dayCount) || dayCount.size() == 0) {
                return;
            }
            // 查询已经获取的补签次数余额，
            Integer makeupCountSumBalance = getMakeupCount(userInfo.getSiteCode(), ReportUserVenueBetsTopVO.getUserId(), timeZone);
            // 一共天数-签到次数-补签次数
            int checkInTimes = dayCount.size() - checkInCount - makeupCountSumBalance;
            // 补签次数 取达到标准可获取的签到次数与补签次数的较小值
            int addCheckInCount = Math.min(checkInTimes, betAndDepositTime);
            addCheckInCount = Math.min(addCheckInCount, makeupLeftCount); // 限制补签上限
            log.info("插入补签变更记录， userAccount：{},补签次数-{}", ReportUserVenueBetsTopVO.getUserAccount(), addCheckInCount);
            if (addCheckInCount <= 0) {
                return;
            }
            // 13. 获取或创建补签余额记录
            //  插入到补签次数记录
            SiteMakeupCountBalancePO siteMakeupCountBalancePO = getOrCreateMakeupCountBalance(userInfo, userInfo.getSiteCode(), startMonthTime, timeZone);
            // 14. 插入补签变更记录
            log.info("插入补签变更记录， userAccount：{},补签次数-{}", ReportUserVenueBetsTopVO.getUserAccount(), addCheckInCount);
            recordMakeupCountChange(validCheckInConfig, siteMakeupCountBalancePO, addCheckInCount, timeZone);
            // 15. 更新用户补签余额
            updateMakeupBalance(siteMakeupCountBalancePO, addCheckInCount);
            log.info("用户 {} 成功补签 {} 次", ReportUserVenueBetsTopVO.getUserId(), addCheckInCount);
        } catch (Exception e) {
            log.error("处理用户 {} 补签异常", ReportUserVenueBetsTopVO.getUserId(), e);
        } finally {
            if (locked) {
                lock.unlock();
            }
        }
    }

    /**
     * 查询用户在指定时间范围内的补签次数总和
     *
     * @param userId          用户ID
     * @param siteCode        站点编码
     * @param startTimeMillis 开始时间（毫秒）
     * @param endTimeMillis   结束时间（毫秒）
     * @return 补签次数总和
     */
    public Integer getUserMakeupCountSum(String userId, String siteCode, long startTimeMillis, long endTimeMillis) {
        QueryWrapper<SiteActivityMakeupCountRecordPO> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .select("SUM(reward_count) as total")
                .eq("user_id", userId)
                .eq("site_code", siteCode)
                .between("day_millis", startTimeMillis, endTimeMillis);

        List<Object> result = makeupCountRecordRepository.selectObjs(queryWrapper);
        return result.get(0) != null ? ((Number) result.get(0)).intValue() : 0;
    }

    /**
     * 更新用户的补签次数余额
     *
     * @param siteMakeupCountBalancePO 当前用户的补签余额对象
     * @param addCheckInCount          要增加的补签次数
     */
    public void updateMakeupBalance(SiteMakeupCountBalancePO siteMakeupCountBalancePO, int addCheckInCount) {
        LambdaUpdateWrapper<SiteMakeupCountBalancePO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SiteMakeupCountBalancePO::getId, siteMakeupCountBalancePO.getId());
        updateWrapper.set(SiteMakeupCountBalancePO::getUpdatedTime, System.currentTimeMillis());
        updateWrapper.setSql("balance = balance + " + addCheckInCount);

        makeupCountBalanceRepository.update(null, updateWrapper);
    }

    /**
     * 补签次数变更记录方法：用于记录用户补签次数的增加操作
     *
     * @param validCheckInConfig       当前生效的签到活动配置对象（包含活动 baseId 等信息）
     * @param siteMakeupCountBalancePO 当前用户的补签次数余额对象
     * @param addCount                 增加的补签次数
     * @param timeZone                 时区标识，例如 "Asia/Shanghai"
     */
    public void recordMakeupCountChange(
            SiteActivityCheckInPO validCheckInConfig,
            SiteMakeupCountBalancePO siteMakeupCountBalancePO,
            int addCount,
            String timeZone) {
        Long now = System.currentTimeMillis();

        // 生成订单号：由用户ID + 活动模板序号 + 当前日期组成
        String orderNo = OrderNoUtils.genOrderNo(
                siteMakeupCountBalancePO.getUserId(),
                ActivityTemplateEnum.CHECKIN.getSerialNo(),
                TimeZoneUtils.getDayStringInTimeZone(now, timeZone)
        );

        // 构建补签记录实体
        SiteActivityMakeupCountRecordPO record = SiteActivityMakeupCountRecordPO.builder().build();
        record.setUserId(siteMakeupCountBalancePO.getUserId());
        record.setUserAccount(siteMakeupCountBalancePO.getUserAccount());
        record.setSiteCode(siteMakeupCountBalancePO.getSiteCode());
        record.setActivityId(validCheckInConfig.getBaseId());
        record.setOperationType(1); // 操作类型：1 表示增加
        record.setStartCount(siteMakeupCountBalancePO.getBalance()); // 变更前余额
        record.setEndCount(siteMakeupCountBalancePO.getBalance() + addCount); // 变更后余额

        record.setDayMillis(TimeZoneUtils.getStartOfDayInTimeZone(now, timeZone)); // 对应月份的起始时间戳
        record.setDayStr(TimeZoneUtils.getDayStringInTimeZone(now, timeZone)); // 字符串形式的日期
        record.setRewardCount(addCount); // 变更的补签次数
        record.setOrderNumber(orderNo);
        record.setCreatedTime(now);
        record.setUpdatedTime(now);
        record.setUserAccount(siteMakeupCountBalancePO.getUserAccount());

        // 插入记录
        makeupCountRecordRepository.insert(record);
    }

    /**
     * 获取用户当月补签次数余额记录；如果不存在则创建一条初始化记录
     *
     * @param userInfoVO     用户ID
     * @param siteCode       站点Code
     * @param startMonthTime 月初时间戳（毫秒）
     * @param timeZone       时区（如 "UTC+8"）
     * @return SiteMakeupCountBalancePO 对象
     */
    private SiteMakeupCountBalancePO getOrCreateMakeupCountBalance(UserInfoVO userInfoVO, String siteCode, Long startMonthTime, String timeZone) {
        LambdaQueryWrapper<SiteMakeupCountBalancePO> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SiteMakeupCountBalancePO::getUserId, userInfoVO.getUserId())
                .eq(SiteMakeupCountBalancePO::getSiteCode, siteCode)
                .eq(SiteMakeupCountBalancePO::getMonthMillis, startMonthTime);

        SiteMakeupCountBalancePO record = makeupCountBalanceRepository.selectOne(wrapper);

        if (record == null) {
            record = new SiteMakeupCountBalancePO();
            record.setUserId(userInfoVO.getUserId());
            record.setSiteCode(siteCode);
            record.setMonthMillis(startMonthTime);
            record.setMonthStr(TimeZoneUtils.getMonthString(startMonthTime, timeZone));
            record.setBalance(0);
            long now = System.currentTimeMillis();
            record.setCreatedTime(now);
            record.setUpdatedTime(now);
            record.setUserAccount(userInfoVO.getUserAccount());

            makeupCountBalanceRepository.insert(record);
        }

        return record;
    }

    /**
     * 查询指定用户在某站点、指定月份内的补签次数（仅统计增加操作）。
     *
     * @param userId         用户ID
     * @param siteCode       站点编码
     * @param startMonthTime 月起始时间戳（毫秒）
     * @param endMonthTime   月结束时间戳（毫秒）
     * @return 补签次数（增加操作）
     */
    private int queryUserMonthlyMakeupCount(String userId, String siteCode, long startMonthTime, long endMonthTime) {
        QueryWrapper<SiteActivityMakeupCountRecordPO> wrapper = new QueryWrapper<>();
        wrapper.select("IFNULL(SUM(reward_count), 0) AS reward_count")
                .eq("user_id", userId)
                .eq("site_code", siteCode)
                .ge("day_millis", startMonthTime)
                .le("day_millis", endMonthTime)
                .eq("operation_type", 1);

        List<Object> result = makeupCountRecordRepository.selectObjs(wrapper);
        int totalReward = result.isEmpty() || result.get(0) == null ? 0 : ((Number) result.get(0)).intValue();
        return totalReward;
    }


    /**
     * 查询指定用户在某个月内（从月初到昨日）的有效签到次数。
     *
     * @param userId         用户ID（已确保非空）
     * @param startMonthTime 月起始时间戳（毫秒）
     * @param timeStart      昨日结束时间戳（毫秒）
     * @return 有效签到次数
     */
    private int queryCheckInCount(String userId, long startMonthTime, long timeStart) {
        LambdaQueryWrapper<SiteCheckInRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteCheckInRecordPO::getUserId, userId);
        lambdaQueryWrapper.between(SiteCheckInRecordPO::getDayMillis, startMonthTime, timeStart);
        lambdaQueryWrapper.eq(SiteCheckInRecordPO::getRewardType, CommonConstant.business_one);
        int count = Math.toIntExact(this.baseMapper.selectCount(lambdaQueryWrapper));
        return count;
    }

    /**
     * 处理用户的存款信息，用于签到活动，添加补签次数
     *
     * @param triggerVO 存款信息对象，包含用户ID、存款金额、存款时间等
     * @return true 表示存款信息已处理；false 表示存款信息未处理
     */
    public boolean checkinMemberRechargeMessage(RechargeTriggerVO triggerVO) {
        String siteCode = triggerVO.getSiteCode();
        UserInfoVO userInfo = userInfoApi.getByUserId(triggerVO.getUserId());
        // 2. 获取当前站点的签到活动配置
        SiteActivityCheckInPO validCheckInConfig = getValidCheckInConfig(siteCode);
        if (validCheckInConfig == null) {
            log.info("【签到活动】站点 {} 未配置签到活动", siteCode);
            return true;
        }
        if (validCheckInConfig.getMakeBetAmount().compareTo(BigDecimal.ZERO) == 0 && validCheckInConfig.getMakeDepositAmount().compareTo(BigDecimal.ZERO) == 0) {
            log.info("【签到活动】站点 {} 签到活动未配置有效金额", siteCode);
            return true;
        }
        if (validCheckInConfig.getMakeupLimit() <= 0) {
            log.info("【签到活动】站点 {} 补签上限配置为 0", siteCode);
            return true;
        }
        // 4. 获取当前站点所有 userId
        List<String> userIds = CollectionUtil.newArrayList();
        userIds.add(triggerVO.getUserId());
        log.info("处理流程 签到活动补签次数{}", siteCode);
        userIds = checkJoinTaskUserId(userIds);
        if (CollectionUtil.isEmpty(userIds)) {
            log.info("【签到活动】站点 {} 过滤后无有效用户参与", siteCode);
            return true;
        }
        log.info("处理流程 签到活动补签次数,userId:{}", JSONObject.toJSONString(userIds));
        // 5. 获取站点时区相关时间范围
        String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
        long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
        long yesterdayEndTime = TimeZoneUtils.getEndOfYesterdayInTimeZone(System.currentTimeMillis(), timeZone);
        long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);
        long startMonthTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
        long endMonthTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), timeZone);
        // 6. 查询用户当日有效投注额（按币种汇总）
        List<ReportUserVenueBetsTopVO> queryUserBetsTop = queryUserBetsTop(siteCode, userIds, timeStart, timeEnd);
        if (CollectionUtil.isEmpty(queryUserBetsTop) && validCheckInConfig.getMakeBetAmount().compareTo(BigDecimal.ZERO) > 0 && queryUserBetsTop.size() == 0) {
            log.info("【签到活动】userID {} 投注未达到要求", triggerVO.getUserId());
            return true;
        }
        ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO = queryUserBetsTop.get(0);
        // 7. 查询用户当日存款额（如活动配置要求）
        Map<String, UserDepositAmountVO> userDepositAmountVOMap = new HashMap<>();
        if (validCheckInConfig.getMakeDepositAmount().compareTo(BigDecimal.ZERO) > 0) {
            // 2 查看用户存款
            List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder().userIds(userIds).startTime(timeStart).endTime(timeEnd).build());
            userDepositAmountVOMap = userDepositAmountVOS.stream().collect(Collectors.toMap(UserDepositAmountVO::getUserId, e -> e));
        }
        // 8. 获取汇率（币种 -> 平台币）
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        // 9. 遍历用户，判断是否满足条件并添加补签次数
        processUserMakeupCheckIn(ReportUserVenueBetsTopVO, allFinalRate, validCheckInConfig, userDepositAmountVOMap, userInfo, startMonthTime, endMonthTime, timeStart, yesterdayEndTime, timeZone);
        return true;
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
            if (org.springframework.util.StringUtils.hasText(userBenefit) && !userBenefit.contains(AgentUserBenefitEnum.DISCOUNT_ACTIVITY.getCode().toString())) {
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

        List<ReportUserVenueBetsTopVO> returnRecords = Optional.ofNullable(ReportUserVenueBetsTopVOPage)
                .map(Page::getRecords).filter(records -> !records.isEmpty())
                .orElse(Collections.emptyList());
        // 如果查不到记录，构造一个默认值（只返回第一个 userId 的）
        if (returnRecords.isEmpty() && !CollectionUtil.isEmpty(userIds)) {
            ReportUserVenueBetsTopVO defaultVO = new ReportUserVenueBetsTopVO();
            defaultVO.setUserId(userIds.get(0));
            defaultVO.setBetAmount(BigDecimal.ZERO);
            defaultVO.setValidAmount(BigDecimal.ZERO);
            defaultVO.setWinLossAmount(BigDecimal.ZERO);
            return Collections.singletonList(defaultVO);
        }

        return returnRecords;

    }
}
