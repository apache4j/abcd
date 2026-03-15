package com.cloud.baowang.activity.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.task.TaskDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.task.TaskEnum;
import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.activity.api.vo.report.ReportTaskReportPageCopyVO;
import com.cloud.baowang.activity.api.vo.task.*;
import com.cloud.baowang.activity.po.SiteTaskConfigPO;
import com.cloud.baowang.activity.po.SiteTaskFlashCardBasePO;
import com.cloud.baowang.activity.po.SiteTaskOrderRecordPO;
import com.cloud.baowang.activity.po.SiteTaskOverViewConfigPO;
import com.cloud.baowang.activity.repositories.SiteTaskConfigRepository;
import com.cloud.baowang.activity.repositories.SiteTaskFlashCardRepository;
import com.cloud.baowang.activity.repositories.SiteTaskOrderRecordRepository;
import com.cloud.baowang.activity.repositories.SiteTaskOverViewConfigRepository;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.agent.api.api.AgentInfoApi;
import com.cloud.baowang.agent.api.enums.AgentUserBenefitEnum;
import com.cloud.baowang.agent.api.vo.agentreview.info.AgentInfoVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.constants.RedisLockConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.report.api.vo.ReportUserTopReqVO;
import com.cloud.baowang.user.api.enums.MedalCodeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.CoinBalanceTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.PlatformWalletEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountAdjustTypeEnum;
import com.cloud.baowang.wallet.api.enums.wallet.TypingAmountEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.ConvertUtil;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.vo.user.WalletUserInfoVO;
import com.cloud.baowang.wallet.api.vo.userCoin.CoinRecordResultVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserPlatformCoinAddVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.kafka.vo.*;
import com.cloud.baowang.common.redis.annotation.DistributedLock;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserVenueFixedWinLoseApi;
import com.cloud.baowang.report.api.vo.user.ReportUserVenueBetsTopVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.i18n.I18nSearchVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.SiteUserInviteRecordApi;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireBatchReqVO;
import com.cloud.baowang.user.api.vo.medal.MedalAcquireReqVO;
import com.cloud.baowang.user.api.vo.user.UserLoginInfoVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordTaskReqVO;
import com.cloud.baowang.user.api.vo.user.invite.SiteUserInviteRecordTaskResVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserDepositWithdrawApi;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountReqVO;
import com.cloud.baowang.wallet.api.vo.fundadjust.UserDepositAmountVO;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.recharge.SiteCurrencyInfoRespVO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Slf4j
@Service
@Transactional
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SiteTaskOrderRecordService extends ServiceImpl<SiteTaskOrderRecordRepository, SiteTaskOrderRecordPO> {

    private final static BigDecimal inviteFriendAmount = new BigDecimal("5");
    private final SiteTaskOrderRecordRepository siteTaskOrderRecordRepository;
    private final SiteTaskConfigRepository siteTaskConfigRepository;
    private final I18nApi i18nApi;
    private final UserInfoApi userInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final ReportUserVenueFixedWinLoseApi reportUserVenueFixedWinLoseApi;

    //private final TaskConfigProperties taskConfigProperties;
    private final SiteApi siteApi;
    private final AgentInfoApi agentInfoApi;
    private final SystemDictConfigApi systemDictConfigApi;
    private final UserDepositWithdrawApi userDepositWithdrawApi;
    private final SiteUserInviteRecordApi siteUserInviteRecordApi;
    private final SiteTaskOverViewConfigRepository overViewConfigRepository;
    private final SiteCurrencyInfoApi currencyInfoApi;
    private final SiteTaskFlashCardRepository siteTaskFlashCardRepository;
    private final SiteTaskOrderRecordService _this;

    private final ActivityUserCommonPlatformCoinService activityUserCommonPlatformCoinService;

    private final ActivityUserCommonCoinService activityUserCommonCoinService;


    public Long getTotalCount(SiteTaskOrderRecordReqVO requestVO) {
        // 首先根据name在国际化查询
        if (ObjectUtil.isNotEmpty(requestVO.getTaskName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder().searchContent(requestVO.getTaskName()).bizKeyPrefix(I18MsgKeyEnum.TASK_NAME.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();
            if (CollectionUtil.isEmpty(activityNameList)) {
                return 0L;
            } else {
                // 获取配置的任务id
                buildLambdaQueryWrapper(requestVO, activityNameList);
            }
        }
        LambdaQueryWrapper<SiteTaskOrderRecordPO> queryWrapper = SiteTaskOrderRecordPO.getQueryWrapper(requestVO);
        return siteTaskOrderRecordRepository.selectCount(queryWrapper);
    }

    private void buildLambdaQueryWrapper(SiteTaskOrderRecordReqVO requestVO, List<String> activityNameList) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, requestVO.getSiteCode());
        queryWrapper.in(SiteTaskConfigPO::getTaskNameI18nCode, activityNameList);
        List<SiteTaskConfigPO> taskConfigPOS = siteTaskConfigRepository.selectList(queryWrapper);
        List<String> ids = taskConfigPOS.stream().map(SiteTaskConfigPO::getId).collect(Collectors.toList());
        requestVO.setTaskIds(ids);
    }

    public Page<SiteTaskOrderRecordResVO> recordPageList(SiteTaskOrderRecordReqVO requestVO) {

        Page<SiteTaskOrderRecordPO> respVOPage = new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());

        // 首先根据name在国际化查询
        if (ObjectUtil.isNotEmpty(requestVO.getTaskName())) {
            List<String> activityNameList = i18nApi.search(I18nSearchVO.builder().searchContent(requestVO.getTaskName()).bizKeyPrefix(I18MsgKeyEnum.TASK_NAME.getCode()).lang(CurrReqUtils.getLanguage()).build()).getData();
            if (CollectionUtil.isEmpty(activityNameList)) {
                return new Page<>(requestVO.getPageNumber(), requestVO.getPageSize());
            } else {
                // 获取配置的任务id
                buildLambdaQueryWrapper(requestVO, activityNameList);
            }
        }
        LambdaQueryWrapper<SiteTaskOrderRecordPO> queryWrapper = SiteTaskOrderRecordPO.getQueryWrapper(requestVO);
        Page<SiteTaskOrderRecordPO> activityInfoPage = siteTaskOrderRecordRepository.selectPage(respVOPage, queryWrapper);
        Page<SiteTaskOrderRecordResVO> resultPage = new Page<>();
        BeanUtils.copyProperties(activityInfoPage, resultPage);
        if (CollectionUtils.isNotEmpty(activityInfoPage.getRecords())) {
            resultPage.setRecords(ConvertUtil.entityListToModelList(activityInfoPage.getRecords(), SiteTaskOrderRecordResVO.class));
            // 获取taskId
            Set<String> taskIds = activityInfoPage.getRecords().stream().map(SiteTaskOrderRecordPO::getTaskId).collect(Collectors.toSet());
            Map<String, String> nameMap = new HashMap<>();
            if (CollectionUtil.isNotEmpty(taskIds)) {
                // 获取配置的任务id
                LambdaQueryWrapper<SiteTaskConfigPO> queryWrapperConfig = new LambdaQueryWrapper<>();
                queryWrapperConfig.eq(SiteTaskConfigPO::getSiteCode, requestVO.getSiteCode());
                queryWrapperConfig.in(SiteTaskConfigPO::getId, taskIds);
                List<SiteTaskConfigPO> taskConfigs = siteTaskConfigRepository.selectList(queryWrapperConfig);
                if (CollectionUtil.isNotEmpty(taskConfigs)) {
                    nameMap = taskConfigs.stream().collect(Collectors.toMap(SiteTaskConfigPO::getId, SiteTaskConfigPO::getTaskNameI18nCode, (k1, k2) -> k2));
                }
            }
            Map<String, String> finalNameMap = nameMap;
            resultPage.getRecords().forEach(e -> {
                e.setTaskName(finalNameMap.get(e.getTaskId()));
            });
        }
        return resultPage;
    }

    /**
     * 根据站点代码、任务类型和可选的子任务类型获取站点任务配置信息。
     *
     * @param siteCode    站点代码，用于筛选任务配置。
     * @param taskType    任务类型，用于筛选任务配置。
     * @param subTaskType 子任务类型（可选，仅在不为空时生效）。
     * @return 如果找到匹配的 {@link SiteTaskConfigPO}，则返回对应对象；否则返回 null。
     */
    public SiteTaskConfigPO getSiteTaskConfig(String siteCode, String taskType, String subTaskType) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, siteCode);
        queryWrapper.eq(SiteTaskConfigPO::getTaskType, taskType);
        queryWrapper.eq(SiteTaskConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        queryWrapper.eq(ObjectUtil.isNotEmpty(subTaskType), SiteTaskConfigPO::getSubTaskType, subTaskType);
        return siteTaskConfigRepository.selectOne(queryWrapper);
    }

    /**
     * 根据站点编码和任务类型获取任务配置列表。
     * <p>
     * 此方法从数据库中查询符合以下条件的任务配置：
     * 1. 匹配指定的站点编码（siteCode）。
     * 2. 匹配指定的任务类型（taskType）。
     * 3. 任务配置的状态为启用（ENABLE）。
     * 4. 按照排序字段（sort）升序排列。
     *
     * @param siteCode 站点的唯一标识，用于筛选任务配置。
     * @param taskType 任务类型，例如：每日任务、每周任务等。
     * @return 符合条件的 {@link SiteTaskConfigPO} 对象列表。
     * 如果没有符合条件的任务配置，返回空列表。
     */
    public List<SiteTaskConfigPO> getSiteTaskConfigs(String siteCode, String taskType) {
        LambdaQueryWrapper<SiteTaskConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskConfigPO::getSiteCode, siteCode);
        queryWrapper.eq(SiteTaskConfigPO::getTaskType, taskType);
        queryWrapper.eq(SiteTaskConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        queryWrapper.orderByAsc(SiteTaskConfigPO::getSort);
        return siteTaskConfigRepository.selectList(queryWrapper);
    }

    /**
     * 查询指定会员或多个会员的指定任务类型获奖记录
     *
     * @param userIds   用户ID列表
     * @param taskType  任务类型
     * @param timeStart 时间起点，允许为空
     * @return 获奖记录列表
     */
    public List<SiteTaskOrderRecordPO> getSiteTaskOrderRecords(List<String> userIds, String taskType, Long timeStart, Long timeEnd) {
        LambdaQueryWrapper<SiteTaskOrderRecordPO> queryWrapper = new LambdaQueryWrapper<>();

        // 直接使用 in 处理单个或多个 userId
        queryWrapper.in(SiteTaskOrderRecordPO::getUserId, userIds);
        queryWrapper.eq(SiteTaskOrderRecordPO::getTaskType, taskType);

        // 如果 timeStart 不为空，则加上时间条件
        if (ObjectUtil.isNotEmpty(timeStart)) {
            queryWrapper.ge(SiteTaskOrderRecordPO::getCreatedTime, timeStart);
        }
        // 如果 timeStart 不为空，则加上时间条件
        if (ObjectUtil.isNotEmpty(timeEnd)) {
            queryWrapper.le(SiteTaskOrderRecordPO::getCreatedTime, timeEnd);
        }

        return siteTaskOrderRecordRepository.selectList(queryWrapper);
    }

    /**
     * 查询指定会员或多个会员的指定任务类型获奖记录
     *
     * @param taskType  任务类型
     * @param timeStart 时间起点，允许为空
     * @return 获奖记录列表
     */
    public List<String> getUserIdsOfSiteTaskOrderRecords(String taskType, String siteCode, Long timeStart, Long timeEnd) {

        return siteTaskOrderRecordRepository.getUserIdsOfSiteTaskOrderRecords(taskType, siteCode, timeStart, timeEnd);
    }

    /**
     * 处理流程 新人任务流程
     */
    @DistributedLock(name = RedisConstants.TASK_SEND_LOCK_KEY, unique = "#triggerVO.userId", waitTime = 3, leaseTime = 180)
    @Transactional(rollbackFor = Exception.class)
    public boolean process(TaskNoviceTriggerVO triggerVO) {
        // 判断类型
        List<String> subTaskTypes = triggerVO.getSubTaskTypes();
        if (CollectionUtil.isEmpty(subTaskTypes)) {
            return true;
        }
        // 查询时间
        //UserInfoVO userInfo = userInfoApi.getByUserId(triggerVO.getUserId());
        // 测试账号过滤
        /*if (UserAccountTypeEnum.TEST_ACCOUNT.getCode().equals(userInfo.getAccountType())) {
            return true;
        }*/
        if (!checkTask(triggerVO.getSuperAgentId())) {
            return true;
        }
        // 添加当前时间与注册时间，是否过了任务有效期
        // 过期截止时间， value 是过期多个小时。
        if (triggerVO.getRegisterTime() == null) {
            return true;
        }
        ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), triggerVO.getSiteCode());
        if (!byCode.isOk()) {
            log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
            return false;
        }
        Double value = Double.valueOf(byCode.getData().getConfigParam());
        long deadline = triggerVO.getRegisterTime() + (long) (60 * 60 * value * 1000L);
        // 截止时间与当前时间比较，如果当前时间大于截止时间，
        long expireTime = (deadline - System.currentTimeMillis()) / 1000;
        if (expireTime < 0) return true;
        long receiveEndTime = System.currentTimeMillis() + (long) (value * 60 * 60 * 1000L);
        // 判断任务是否启动
        for (String subTaskType : subTaskTypes) {
            SiteTaskConfigPO taskConfigPO = getSiteTaskConfig(triggerVO.getSiteCode(), TaskEnum.NOVICE_CURRENCY.getTaskType(), subTaskType);
            if (taskConfigPO == null) {
                continue;
            }
            if (taskConfigPO.getRewardAmount() == null || BigDecimal.ZERO.compareTo(taskConfigPO.getRewardAmount()) >= 0) {
                continue;
            }


            //生成订单 , siteCode+subTaskType+userId .一个用户只会生成一个
            String serialNo = TaskEnum.fromSubTaskType(subTaskType).getSerialNo();
            String orderNo = OrderNoUtils.genOrderNo(triggerVO.getUserId(), serialNo);
            // 确定该新人类型是否获取过奖励
            LambdaQueryWrapper<SiteTaskOrderRecordPO> recordWrapper = new LambdaQueryWrapper<>();
            recordWrapper.eq(SiteTaskOrderRecordPO::getOrderNo, orderNo);
            SiteTaskOrderRecordPO recordPO = siteTaskOrderRecordRepository.selectOne(recordWrapper);
            if (recordPO != null) {
                continue;
            }
            // 插入数据
            SiteTaskOrderRecordPO insert = new SiteTaskOrderRecordPO();
            insert.setOrderNo(orderNo);
            insert.setUserId(triggerVO.getUserId());
            insert.setSiteCode(triggerVO.getSiteCode());
            insert.setTaskId(taskConfigPO.getId());
            insert.setTaskType(TaskEnum.NOVICE_CURRENCY.getTaskType());
            insert.setSubTaskType(subTaskType);
            insert.setUserId(triggerVO.getUserId());
            insert.setUserAccount(triggerVO.getUserAccount());
            insert.setVipGradeCode(triggerVO.getVipGradeCode());
            insert.setVipRankCode(triggerVO.getVipRankCode());
            insert.setSuperAgentId(triggerVO.getSuperAgentId());
            insert.setUserName(triggerVO.getUserName());
            insert.setDistributionType(TaskDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode());
            insert.setReceiveStartTime(System.currentTimeMillis());
            insert.setTaskAmount(taskConfigPO.getRewardAmount());
            insert.setReceiveStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());
            insert.setReceiveEndTime(receiveEndTime);
            BigDecimal runningWater = NumberUtil.mul(taskConfigPO.getRewardAmount(), taskConfigPO.getWashRatio()).setScale(2, RoundingMode.UP);
            //  调资金接口。
            insert.setRunningWater(runningWater);
            insert.setWashRatio(taskConfigPO.getWashRatio());
            insert.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
            insert.setRemark(String.format("新人任务-%s, 奖励: [%s %s] ", TaskEnum.fromTask(TaskEnum.NOVICE_CURRENCY.getTaskType(), subTaskType).getName(), taskConfigPO.getRewardAmount(), taskConfigPO.getCurrencyCode()));
            if (siteTaskOrderRecordRepository.insert(insert) <= 0) {
                log.info("插入新人任务失败:siteCode {},userId:{}", triggerVO.getSiteCode(), triggerVO.getUserId());
            }

        }

        return true;
    }


    /**
     * 处理流程 每日与每周任务，触发奖励
     * siteCode,userId
     */
    public boolean processDailyAndWeek(UserVenueWinLossSendVO sendVO) {
        if (CollectionUtil.isEmpty(sendVO.getVoList())) {
            return true;
        }

        List<String> siteCodes = sendVO.getVoList().stream().map(UserVenueWinLossMqVO::getSiteCode).distinct().toList();
        for (String siteCode : siteCodes) {
            List<String> userIds = sendVO.getVoList().stream().filter(e -> e.getSiteCode().equals(siteCode)).map(UserVenueWinLossMqVO::getUserId).distinct().collect(Collectors.toList());
            // 过滤不能参加任务的id
            userIds = checkJoinTaskUserId(userIds);
            if (CollectionUtil.isEmpty(userIds)) {
                continue;
            }
            log.info("处理流程 每日任务奖励{}", siteCode);
            // 查询任务完成情况
            String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
            // 每日任务
            // 每日任务处理
            List<SiteTaskOrderRecordPO> insertDailyRecords = new ArrayList<>();
            List<SiteTaskConfigPO> dailyTaskConfigs = getSiteTaskConfigs(siteCode, TaskEnum.DAILY_BET.getTaskType());
            processDailyTasks(dailyTaskConfigs, siteCode, userIds, timeZone, insertDailyRecords);

            // 每周任务
            List<SiteTaskOrderRecordPO> insertWeekRecords = new ArrayList<>();
            processWeekTasks(siteCode, userIds, timeZone, insertWeekRecords);
            // 每个站点插入一次
            if (CollectionUtil.isNotEmpty(insertWeekRecords)) {
                log.info("处理流程 每周任务,批量插入奖励 {}", insertWeekRecords.size());
                this.saveBatch(insertWeekRecords);
            }
            if (CollectionUtil.isNotEmpty(insertDailyRecords)) {
                log.info("处理流程 每日任务,批量插入奖励 {}", insertDailyRecords.size());
                this.saveBatch(insertDailyRecords);
                //processSendMeal(userIdReadSends, timeZone, siteCode, 6 * configCount);
            }
        }
        return true;
    }

    /**
     * 处理存款消息 每日任务存款，触发奖励
     * 处理每周邀请任务，每周邀请也是靠存款任务来界定
     * siteCode,userId
     */
    public boolean processDailyDepositTask(RechargeTriggerVO sendVO) {

        UserInfoVO userInfoVO = userInfoApi.getByUserId(sendVO.getUserId());
        String userId = userInfoVO.getUserId();
        SiteTaskConfigPO siteTaskConfig = getSiteTaskConfig(userInfoVO.getSiteCode(), TaskEnum.DAILY_DEPOSIT.getTaskType(), TaskEnum.DAILY_DEPOSIT.getSubTaskType());
        if (siteTaskConfig == null) {
            log.info("每日任务存款活动未开启");
            return true;
        }
        if (org.springframework.util.StringUtils.hasText(userInfoVO.getSuperAgentId())) {
            if (!checkTask(userInfoVO.getSuperAgentId())) {
                log.info("会员对应的上级代理，不能参加任务:{}", userInfoVO.getUserId());
                return true;
            }
        }
        log.info("处理流程 每日任务存款活动{}", userInfoVO.getUserId());
        String siteCode = userInfoVO.getSiteCode();
        // 查询任务完成情况
        String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
        // 每日任务处理存款活动
        List<SiteTaskOrderRecordPO> insertDailyRecords = new ArrayList<>();
        Long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
        Long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);


        // 活动配置
        String taskConfigJson = siteTaskConfig.getTaskConfigJson();
        if (StringUtils.isBlank(taskConfigJson)) {
            log.info("每日任务存款活动未配置");
            return true;
        }
        List<TaskSubConfigReqVO> subConfigReqVOS = JSON.parseArray(taskConfigJson, TaskSubConfigReqVO.class);
        if (CollectionUtil.isEmpty(subConfigReqVOS)) {
            log.info("每日任务存款活动未配置");
            return true;
        }
        //
        List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder().userIds(Collections.singletonList(userId)).startTime(timeStart).endTime(timeEnd).build());
        if (CollectionUtil.isEmpty(userDepositAmountVOS)) {
            log.info("会员没有存款:{}", userId);
            return true;
        }
        UserDepositAmountVO userDepositAmountVO = userDepositAmountVOS.get(0);
        // 主货币转换未平台币
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
        // 会员存款转换为平台币
        BigDecimal userDepositPlat = userDepositAmountVO.getAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);

        subConfigReqVOS.sort(Comparator.comparing(TaskSubConfigReqVO::getStep));
        Optional<TaskSubConfigReqVO> first = subConfigReqVOS.stream().findFirst();
        if (first.get().getDepositAmount().compareTo(userDepositPlat) > 0) {
            log.info("{}:会员有存款:{},存款转换平台币是:{},小于配置的最小值:{}", userId, userDepositPlat, first.get().getDepositAmount());
            return true;
        }

        //这个是否有今天是否奖励发放
        List<SiteTaskOrderRecordPO> dailyRecordPOS = getSiteTaskOrderRecords(Collections.singletonList(userId), TaskEnum.DAILY_DEPOSIT.getTaskType(), timeStart, timeEnd);
        ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), siteCode);
        if (!byCode.isOk()) {
            log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
            return false;
        }
        Double value = Double.valueOf(byCode.getData().getConfigParam());
        Long deadline = (long) (60 * 60 * value * 1000L);

        for (TaskSubConfigReqVO configReqVO : subConfigReqVOS) {
            if (userDepositPlat.compareTo(configReqVO.getDepositAmount()) >= 0) {
                Integer step = configReqVO.getStep();
                if (!isCheckReward(step, dailyRecordPOS, userInfoVO.getUserId(), siteTaskConfig.getSubTaskType())) {
                    // 插入记录
                    // 创建奖励记录
                    String dateTime = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timeZone) + step;
                    long receivedEndTime = deadline + timeEnd;
                    SiteTaskOrderRecordPO insertRecord = createOrderDepositRecord(userInfoVO, siteTaskConfig, siteCode, receivedEndTime, dateTime, configReqVO);
                    insertDailyRecords.add(insertRecord);
                    log.info("处理流程 每日与每周任务,插入奖励:每日存款任务{}", JSONObject.toJSONString(insertRecord));
                }
            }
        }


        // 每个站点插入一次
        if (CollectionUtil.isNotEmpty(insertDailyRecords)) {
            log.info("处理流程 每日与每周任务,批量插入奖励 {}", insertDailyRecords.size());
            this.saveBatch(insertDailyRecords);
        }

        return true;
    }

    /**
     * -- 处理存款消息 每日任务存款，触发奖励
     * 处理每周邀请任务，每周邀请也是靠存款任务来界定
     * siteCode,userId
     */
    public boolean processWeekDepositTask(RechargeTriggerVO sendVO) {
        UserInfoVO userInfoVO = userInfoApi.getByUserId(sendVO.getUserId());
        String userId = userInfoVO.getUserId();
        // 查询任务完成情况
        String siteCode = userInfoVO.getSiteCode();
        String timeZone = siteApi.getSiteInfo(siteCode).getData().getTimezone();
        Long timeStartWeek = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
        Long timeEndWeek = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
        // 查询该用户的注册时间
        if (userInfoVO.getRegisterTime() < timeStartWeek) {
            log.info("该用户不是这个星期注册的：{}", userId);
            return true;
        }

        SiteTaskConfigPO weekTaskConfig = getSiteTaskConfig(userInfoVO.getSiteCode(), TaskEnum.WEEK_INVITE_FRIENDS.getTaskType(), TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType());
        if (weekTaskConfig == null) {
            log.info("每周任务邀请活动未开启");
            return true;
        }
        if (StringUtils.isNotBlank(userInfoVO.getSuperAgentId())) {
            if (!checkTask(userInfoVO.getSuperAgentId())) {
                log.info("会员对应的上级代理，不能参加任务:{}", userInfoVO.getUserId());
                return true;
            }
        }
        log.info("处理流程 每周任务邀请活动{}", userInfoVO.getUserId());
        // 每日任务处理存款活动
        List<SiteTaskOrderRecordPO> insertDailyRecords = new ArrayList<>();
        // 活动配置
        String taskConfigJson = weekTaskConfig.getTaskConfigJson();
        if (StringUtils.isBlank(taskConfigJson)) {
            log.info("每周任务邀请未配置");
            return true;
        }
        List<TaskSubConfigReqVO> subConfigReqVOS = JSON.parseArray(taskConfigJson, TaskSubConfigReqVO.class);
        if (CollectionUtil.isEmpty(subConfigReqVOS)) {
            log.info("每周任务邀请未配置");
            return true;
        }
        //
        List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(
                UserDepositAmountReqVO.builder().userIds(Collections.singletonList(userId)).startTime(timeStartWeek).endTime(timeEndWeek).build());
        if (CollectionUtil.isEmpty(userDepositAmountVOS)) {
            log.info("会员没有存款:{}", userId);
            return true;
        }
        UserDepositAmountVO userDepositAmountVO = userDepositAmountVOS.get(0);
        // 主货币转换未平台币
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        BigDecimal rate = allFinalRate.get(userInfoVO.getMainCurrency());
        // 会员存款转换为平台币
        BigDecimal userDepositPlat = userDepositAmountVO.getAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
        // 会员存款，邀请活动配置
        if (inviteFriendAmount.compareTo(userDepositPlat) > 0) {
            log.info("{}:会员有存款:{},存款转换平台币是:{},小于邀请配置的最小值:{}", userId, userDepositPlat, inviteFriendAmount);
            return true;
        }

        // 查询该用户的邀请人，查看他邀请了多少人，
        String inviteFriendUser = userInfoVO.getInviter();
        if (StringUtils.isBlank(inviteFriendUser)) {
            log.info("{}:会员没有邀请人", userId);
            return true;
        }
        // 查看邀请人邀请的好友记录
        SiteUserInviteRecordTaskReqVO inviteRecordTaskReqVO = SiteUserInviteRecordTaskReqVO.builder()
                .userAccount(inviteFriendUser).startTime(timeStartWeek).endTime(timeEndWeek).siteCode(siteCode).build();
        List<SiteUserInviteRecordTaskResVO> inviteRecord = siteUserInviteRecordApi.getInviteRecord(inviteRecordTaskReqVO);
        log.info("好友数量:userId:{},:{}", inviteFriendUser, inviteRecord.size());
        //计算达到要求的好友
        List<SiteUserInviteRecordTaskResVO> inviteRecordTarget = computerTargetUser(inviteRecord, inviteFriendAmount, allFinalRate);
        if (CollectionUtil.isEmpty(inviteRecordTarget)) {
            log.info("{}:被邀请人没有达标的", inviteFriendUser);
            return true;
        }
        // 实际邀请好友记录
        Integer friendInviteRecordCount = inviteRecordTarget.size();
        subConfigReqVOS.sort(Comparator.comparing(TaskSubConfigReqVO::getStep));
        Optional<TaskSubConfigReqVO> first = subConfigReqVOS.stream().findFirst();
        if (first.get().getInviteFriendCount().compareTo(friendInviteRecordCount) > 0) {
            log.info("{}:会员达标邀请好友记录数:{},存款转换平台币是:{},小于配置的最小值:{}", inviteFriendUser, friendInviteRecordCount, first.get().getInviteFriendCount());
            return true;
        }
        String inviterId = userInfoVO.getInviterId();
        UserInfoVO inviterInfo = userInfoApi.getByUserId(inviterId);
        //这个是否有今天是否奖励发放，--查询的是他邀请人
        List<SiteTaskOrderRecordPO> dailyRecordPOS = getSiteTaskOrderRecords(Collections.singletonList(inviterId), TaskEnum.WEEK_INVITE_FRIENDS.getTaskType(), timeStartWeek, timeEndWeek);
        ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), siteCode);
        if (!byCode.isOk()) {
            log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
            return false;
        }
        Double value = Double.valueOf(byCode.getData().getConfigParam());
        Long deadline = (long) (60 * 60 * value * 1000L);

        for (TaskSubConfigReqVO configReqVO : subConfigReqVOS) {
            if (friendInviteRecordCount.compareTo(configReqVO.getInviteFriendCount()) >= 0) {
                Integer step = configReqVO.getStep();
                if (!isCheckReward(step, dailyRecordPOS, userInfoVO.getUserId(), weekTaskConfig.getSubTaskType())) {
                    // 插入记录
                    // 创建奖励记录
                    String dateTime = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timeZone) + step;
                    long receivedEndTime = deadline + timeEndWeek;
                    // 邀请人获取奖励
                    SiteTaskOrderRecordPO insertRecord = createOrderDepositRecord(inviterInfo, weekTaskConfig, siteCode, receivedEndTime, dateTime, configReqVO);
                    insertDailyRecords.add(insertRecord);
                    log.info("处理流程 每日与每周任务,插入奖励,邀请任务奖励{}", JSONObject.toJSONString(insertRecord));
                }
            }
        }


        // 每个站点插入一次
        if (CollectionUtil.isNotEmpty(insertDailyRecords)) {
            log.info("处理流程 每周任务,批量插入奖励 {}", insertDailyRecords.size());
            this.saveBatch(insertDailyRecords);
        }

        return true;
    }

    /**
     * @param inviteRecord       邀请记录
     * @param inviteFriendAmount 配置邀请金额：平台币
     * @return 符合任务配置的邀请记录
     */
    private List<SiteUserInviteRecordTaskResVO> computerTargetUser(List<SiteUserInviteRecordTaskResVO> inviteRecord, BigDecimal inviteFriendAmount, Map<String, BigDecimal> allFinalRate) {
        if (CollectionUtil.isEmpty(inviteRecord)) {
            return Collections.emptyList();
        }
        List<SiteUserInviteRecordTaskResVO> resVOList = new ArrayList<>();
        for (SiteUserInviteRecordTaskResVO taskResVO : inviteRecord) {
            BigDecimal rate = allFinalRate.get(taskResVO.getCurrency());
            if (rate == null) {
                log.info("会员没有币种，或者币种没有汇率:{}", JSONObject.toJSONString(taskResVO));
                continue;
            }
            // 会员存款转换为平台币
            BigDecimal userDepositPlat = taskResVO.getFirstDepositAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN);
            if (userDepositPlat.compareTo(inviteFriendAmount) >= 0) {
                taskResVO.setFirstDepositPlatAmount(userDepositPlat);
                resVOList.add(taskResVO);
            }

        }
        return resVOList;
    }


    /**
     * @param step           阶梯值
     * @param dailyRecordPOS 领取记录
     * @return 是否获得奖项
     */
    private SiteTaskOrderRecordPO isCheckRewardReturnPO(Integer step, List<SiteTaskOrderRecordPO> dailyRecordPOS) {
        for (SiteTaskOrderRecordPO taskOrderRecordPO : dailyRecordPOS) {
            if (Objects.equals(step, taskOrderRecordPO.getStep())) {
                return taskOrderRecordPO;
            }
        }
        return null;
    }

    /**
     * @param step           阶梯值
     * @param dailyRecordPOS 领取记录，
     * @return 是否获得奖项
     */
    private Boolean isCheckReward(Integer step, List<SiteTaskOrderRecordPO> dailyRecordPOS, String userId, String subTaskType) {
        for (SiteTaskOrderRecordPO taskOrderRecordPO : dailyRecordPOS) {

            if (StringUtils.equals(userId, taskOrderRecordPO.getUserId())
                    && Objects.equals(step, taskOrderRecordPO.getStep())
                    && StringUtils.equals(subTaskType, taskOrderRecordPO.getSubTaskType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param step           阶梯值
     * @param dailyRecordPOS 领取记录
     * @return 是否领取奖项
     */
   /* private boolean hasClaimed(Integer step, List<SiteTaskOrderRecordPO> dailyRecordPOS) {
        for (SiteTaskOrderRecordPO taskOrderRecordPO : dailyRecordPOS) {
            if (Objects.equals(step, taskOrderRecordPO.getStep())) {
                if(taskOrderRecordPO.getReceiveStatus())
                return true;
            }
        }
        return false;
    }*/


    /**
     * 处理每周任务
     */
    private void processWeekTasks(String siteCode, List<String> userIds, String timeZone, List<SiteTaskOrderRecordPO> insertWeekRecords) {
        List<SiteTaskConfigPO> weekTaskConfigs = getSiteTaskConfigs(siteCode, TaskEnum.WEEK_BET.getTaskType());
        // 查询任务完成情况
        if (CollectionUtil.isNotEmpty(weekTaskConfigs) && CollectionUtil.isNotEmpty(userIds)) {
            Long timeStartWeek = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            Long timeEndWeek = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            // 查询投注信息和每周任务记录
            List<SiteTaskOrderRecordPO> weekRecordPOS = getSiteTaskOrderRecords(userIds, TaskEnum.WEEK_BET.getTaskType(), timeStartWeek, timeEndWeek);
            ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), siteCode);
            if (!byCode.isOk()) {
                log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
                return;
            }
            Double value = Double.valueOf(byCode.getData().getConfigParam());
            Long deadline = (long) (60 * 60 * value * 1000L);
            for (SiteTaskConfigPO taskConfig : weekTaskConfigs) {
                // 邀请任务，不在这个地方触发
                if (TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                    continue;
                }
                // 活动配置
                String taskConfigJson = taskConfig.getTaskConfigJson();
                if (StringUtils.isBlank(taskConfigJson)) {
                    log.info("每周任务活动未配置");
                    continue;
                }
                List<TaskSubConfigReqVO> subConfigReqVOS = JSON.parseArray(taskConfigJson, TaskSubConfigReqVO.class);
                if (CollectionUtil.isEmpty(subConfigReqVOS)) {
                    log.info("每周任务活动未配置");
                    continue;
                }

                List<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOSWeek = queryReportUserVenueBetsTopVOList(siteCode, userIds, timeStartWeek, timeEndWeek, taskConfig);
                if (CollectionUtil.isEmpty(ReportUserVenueBetsTopVOSWeek)) {
                    continue;  // 无投注信息直接返回
                }
                // 遍历每个用户
                for (ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO : ReportUserVenueBetsTopVOSWeek) {
                    UserInfoVO userInfo = userInfoApi.getByUserId(ReportUserVenueBetsTopVO.getUserId());
                    // 判断该用户是否已有奖励记录
                    // 对每一个配置进行校验
                    for (TaskSubConfigReqVO configReqVO : subConfigReqVOS) {
                        Integer step = configReqVO.getStep();
                        // 是否获取过
                        if (isCheckReward(step, weekRecordPOS, userInfo.getUserId(), taskConfig.getSubTaskType())) {
                            log.info("处理流程 每日任务,已经获取了 {}", JSONObject.toJSONString(userIds));
                            continue; // 无投注信息直接返回
                        }
                        // 判断是否符合奖励标准
                        if (isEligibleForRewardWeek(ReportUserVenueBetsTopVO, taskConfig, configReqVO)) {
                            String dateTime = TimeZoneUtils.getStartOfWeekStringInTimeZone(System.currentTimeMillis(), timeZone) + step;
                            // 创建奖励记录
                            long receiveEndTime = deadline + timeEndWeek;
                            SiteTaskOrderRecordPO insertRecord = createOrderDepositRecord(userInfo, taskConfig, siteCode, receiveEndTime, dateTime, configReqVO);
                            //siteTaskOrderRecordRepository.insert(insertDailyRecords);
                            insertWeekRecords.add(insertRecord);
                        }

                    }

                }
            }
        }
    }

    /**
     * 处理每日任务
     */
    public void processDailyTasks(List<SiteTaskConfigPO> dailyTaskConfigs, String siteCode, List<String> userIds, String timeZone, List<SiteTaskOrderRecordPO> insertDailyRecords) {
        if (CollectionUtil.isNotEmpty(dailyTaskConfigs) && CollectionUtil.isNotEmpty(userIds)) {
            Long timeStart = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            Long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            //这个是否有奖励发放
            List<SiteTaskOrderRecordPO> dailyRecordPOS = getSiteTaskOrderRecords(userIds, TaskEnum.DAILY_BET.getTaskType(), timeStart, timeEnd);
            //
            ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), siteCode);
            if (!byCode.isOk()) {
                log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
                return;
            }
            Double value = Double.valueOf(byCode.getData().getConfigParam());
            Long deadline = (long) (60 * 60 * value * 1000L);
            for (SiteTaskConfigPO taskConfig : dailyTaskConfigs) {
                // 不会触发的存款任务
                if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                    continue;
                }
                // 活动配置
                String taskConfigJson = taskConfig.getTaskConfigJson();
                if (StringUtils.isBlank(taskConfigJson)) {
                    log.info("每日任务活动未配置");
                    continue;
                }
                List<TaskSubConfigReqVO> subConfigReqVOS = JSON.parseArray(taskConfigJson, TaskSubConfigReqVO.class);
                if (CollectionUtil.isEmpty(subConfigReqVOS)) {
                    log.info("每日任务活动未配置");
                    continue;
                }
                // 查询投注信息和每日任务记录
                List<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOS = queryReportUserVenueBetsTopVOList(siteCode, userIds, timeStart, timeEnd, taskConfig);
                // ReportUserVenueBetsTopVOS
                if (CollectionUtil.isEmpty(ReportUserVenueBetsTopVOS)) {
                    log.info("处理流程 每日与每周任务,无投注信息直接返回 {}", JSONObject.toJSONString(userIds));
                    continue; // 无投注信息直接返回
                }
                //
                for (ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO : ReportUserVenueBetsTopVOS) {
                    UserInfoVO userInfo = userInfoApi.getByUserId(ReportUserVenueBetsTopVO.getUserId());
                    // 对每一个配置进行校验
                    for (TaskSubConfigReqVO configReqVO : subConfigReqVOS) {
                        Integer step = configReqVO.getStep();
                        // 是否获取过
                        // 获取对应的类型

                        if (isCheckReward(step, dailyRecordPOS, userInfo.getUserId(), taskConfig.getSubTaskType())) {
                            log.info("处理流程 每日任务,已经获取了 {}", JSONObject.toJSONString(userIds));
                            continue; // 无投注信息直接返回
                        }
                        // 进入任务配置阶梯
                        // 判断是否符合奖励标准
                        if (isEligibleForRewardDaily(ReportUserVenueBetsTopVO, taskConfig, configReqVO)) {

                            // 创建奖励记录
                            String dateTime = TimeZoneUtils.getDayStringInTimeZone(System.currentTimeMillis(), timeZone) + step;
                            long receivedEndTime = deadline + timeEnd;
                            SiteTaskOrderRecordPO insertRecord = createOrderDepositRecord(userInfo, taskConfig, siteCode, receivedEndTime, dateTime, configReqVO);
                            insertDailyRecords.add(insertRecord);
                            log.info("处理流程 每日与每周任务,插入奖励{}", JSONObject.toJSONString(insertRecord));
                            //siteTaskOrderRecordRepository.insert(insertRecord);
                        }

                    }
                }
            }
        }
    }


    /**
     * 处理用户的勋章派发逻辑，检查用户在本周的任务完成情况，并在周日派发相应的勋章。
     *
     * @param userIdForMedals 用户ID列表，用于检查勋章发放资格
     * @param timeZone        用户所在时区
     * @param siteCode        站点代码，用于标识不同的站点
     */
    /*@Async(ThreadPoolConfig.TASK_EXECUTOR) // 将此方法标记为异步执行
    public void processSendMeal(List<String> userIdForMedals, String timeZone, String siteCode, int ConfigCount) {
        // 判断-start
        // 1. 判断今天是不是周日
        boolean flag = TimeZoneUtils.isEndOfWeekDayTimeZone(System.currentTimeMillis(), timeZone);
        //  测试 (flag)
        if (flag) {
            // 2. 判断获取的奖励，从这周一到周六，是否都获取了
            // 获取本周的开始时间（周一）和结束时间（周六）
            Long currentTime = System.currentTimeMillis();
            Long timeStartWeek = TimeZoneUtils.getStartOfWeekInTimeZone(currentTime, timeZone);
            Long timeEndSaturday = TimeZoneUtils.getWeekOfSaturdayEndTimeInTimeZone(currentTime, timeZone);
            // 查询本周一到周六的任务记录
            List<SiteTaskOrderRecordPO> dailyRecordPOWeeks = getSiteTaskOrderRecords(userIdForMedals, TaskEnum.DAILY_BET.getTaskType(), timeStartWeek, timeEndSaturday);
            List<MedalAcquireReqVO> medalAcquireReqVOList = Lists.newArrayList();
            for (String userId : userIdForMedals) {
                long count = dailyRecordPOWeeks.stream().filter(e -> StringUtils.equals(userId, e.getUserId())).count();
                //  wade ConfigCount 测试使用
                // ConfigCount = 1;
                if (count >= ConfigCount) {
                    MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                    medalAcquireReqVO.setSiteCode(siteCode);
                    medalAcquireReqVO.setUserId(userId);
                    // MEDAL_1017("1017","任务大师","任意单个自然周 是否完成全部每日任务",0),
                    // 可选：获取用户账户信息（如果需要）
                    String userAccount = dailyRecordPOWeeks.stream().filter(e -> StringUtils.equals(userId, e.getUserId())).map(SiteTaskOrderRecordPO::getUserAccount) // 获取用户账户
                            .findFirst().orElse(null); // 如果找不到，则为null
                    medalAcquireReqVO.setUserAccount(userAccount);
                    medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1017.getCode());
                    medalAcquireReqVOList.add(medalAcquireReqVO);
                }
            }
            if (CollectionUtil.isNotEmpty(medalAcquireReqVOList)) {
                //符合要求的直接派发勋章
                MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
                medalAcquireBatchReqVO.setSiteCode(siteCode);
                medalAcquireBatchReqVO.setMedalCodeEnum(MedalCodeEnum.MEDAL_1017);
                medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalAcquireReqVOList);
                log.info("站点:{}开始派发勋章:{},人数:{}", siteCode, MedalCodeEnum.MEDAL_1017.getName(), medalAcquireReqVOList.size());
                KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
            }

        }
        // -end

    }*/

    /**
     * 处理用户的勋章派发逻辑，检查用户在本周的任务完成情况，并在周日派发相应的勋章。
     *
     * @param siteCode 站点代码，用于标识不同的站点
     */
    public void processSendMealJob(String siteCode) {
        log.info(" 任务大师发放,获取站点信息:{}", siteCode);
        ResponseVO<SiteVO> responseVO = siteApi.getSiteInfo(siteCode);
        if (!responseVO.isOk()) {
            log.error("任务大师发放,获取站点信息错误,站点{}", siteCode);
            return;
        }
        String timeZone = responseVO.getData().getTimezone();
        if (!org.springframework.util.StringUtils.hasText(timeZone)) {
            log.info("站点:{}对应的时区无数据", siteCode);
            return;
        }
        // 获取上周的开始时间（周一）和（周日）结束时间
        long currentTime = System.currentTimeMillis();
        long timeStartWeek = TimeZoneUtils.getStartOfLastWeekInTimeZone(currentTime, timeZone);
        long timeEndWeek = TimeZoneUtils.getEndOfLastWeekInTimeZone(currentTime, timeZone);
        // 查询本周一到周日的任务记录
        List<SiteTaskConfigPO> dailyTaskConfigs = getSiteTaskConfigs(siteCode, TaskEnum.DAILY_BET.getTaskType());
        if (CollectionUtil.isEmpty(dailyTaskConfigs)) {
            return;
        }
        // 每天获奖个数
        int configCount = 0;
        List<TaskSubConfigReqVO> subConfigReqVOS = new ArrayList<>();
        // 判断是否有每日存款了任务，如果有，则按照最新的判断
        for (SiteTaskConfigPO taskConfig : dailyTaskConfigs) {

            subConfigReqVOS = JSON.parseArray(taskConfig.getTaskConfigJson(), TaskSubConfigReqVO.class);
            if (CollectionUtil.isNotEmpty(subConfigReqVOS)) {
                int oneDaySize = subConfigReqVOS.size();
                configCount = subConfigReqVOS.size() + configCount;

            }

        }
        configCount = configCount * 7;
        // 获取会员ids
        List<String> userIdForMedals = getUserIdsOfSiteTaskOrderRecords(TaskEnum.DAILY_BET.getTaskType(), siteCode, timeStartWeek, timeEndWeek);
        List<MedalAcquireReqVO> medalAcquireReqVOList = new ArrayList<>();
        // 分为100个用户查询一次
        for (String userId : userIdForMedals) {
            List<SiteTaskOrderRecordPO> dailyRecordPOWeeks = getSiteTaskOrderRecords(List.of(userId), TaskEnum.DAILY_BET.getTaskType(), timeStartWeek, timeEndWeek);
            int count = dailyRecordPOWeeks.size();
            //  wade ConfigCount 测试使用
            // ConfigCount = 1;
            if (count >= configCount) {
                // 符合要求的直接派发勋章
                // 再次判断，如果每日存款情况，就是每天是否都获取了奖励
                boolean flag = true;
                for (SiteTaskConfigPO taskConfigPO : dailyTaskConfigs) {
                    // 存款任务是
                    if (CollectionUtil.isNotEmpty(subConfigReqVOS)) {
                        int dailySize = subConfigReqVOS.size() * 7;
                        List<SiteTaskOrderRecordPO> collect = dailyRecordPOWeeks.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), taskConfigPO.getSubTaskType())).toList();
                        if (CollectionUtil.isEmpty(collect) || collect.size() < dailySize) {
                            flag = false;
                            break;
                        }
                    }
                   /* if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(taskConfigPO.getSubTaskType())) {
                    } else {
                        // 其他任务
                        int dailySize = 7;
                        List<SiteTaskOrderRecordPO> collect = dailyRecordPOWeeks.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), taskConfigPO.getSubTaskType())).toList();
                        if (CollectionUtil.isEmpty(collect) || collect.size() < dailySize) {
                            flag = false;
                            break;
                        }

                    }*/
                }
                if (!flag) {
                    continue;
                }
                MedalAcquireReqVO medalAcquireReqVO = new MedalAcquireReqVO();
                medalAcquireReqVO.setSiteCode(siteCode);
                medalAcquireReqVO.setUserId(userId);
                // MEDAL_1017("1017","任务大师","任意单个自然周 是否完成全部每日任务",0),
                // 可选：获取用户账户信息（如果需要）
                String userAccount = dailyRecordPOWeeks.stream().filter(e -> StringUtils.equals(userId, e.getUserId())).map(SiteTaskOrderRecordPO::getUserAccount) // 获取用户账户
                        .findFirst().orElse(null); // 如果找不到，则为null
                medalAcquireReqVO.setUserAccount(userAccount);
                medalAcquireReqVO.setMedalCode(MedalCodeEnum.MEDAL_1017.getCode());
                medalAcquireReqVOList.add(medalAcquireReqVO);
            }
            // 是否 100 发送一次
            if (medalAcquireReqVOList.size() > 100) {
                sendMessage(siteCode, medalAcquireReqVOList);
                medalAcquireReqVOList = new ArrayList<>();
            }
        }
        if (CollectionUtil.isNotEmpty(medalAcquireReqVOList)) {
            sendMessage(siteCode, medalAcquireReqVOList);
        }

    }

    private int getConfigCount(List<SiteTaskConfigPO> dailyTaskConfigs) {
        int configCount = dailyTaskConfigs.size() * 7;
        for (SiteTaskConfigPO taskConfig : dailyTaskConfigs) {
            if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                List<TaskSubConfigReqVO> subConfigReqVOS = JSON.parseArray(taskConfig.getTaskConfigJson(), TaskSubConfigReqVO.class);
                if (CollectionUtil.isNotEmpty(subConfigReqVOS)) {
                    configCount = (dailyTaskConfigs.size() + subConfigReqVOS.size() - 1) * 7;
                }
            }
        }
        return configCount;
    }

    public void sendMessage(String siteCode, List<MedalAcquireReqVO> medalAcquireReqVOList) {
        //符合要求的直接派发勋章
        MedalAcquireBatchReqVO medalAcquireBatchReqVO = new MedalAcquireBatchReqVO();
        medalAcquireBatchReqVO.setSiteCode(siteCode);
        medalAcquireBatchReqVO.setMedalAcquireReqVOList(medalAcquireReqVOList);
        log.info("站点:{}开始派发勋章: 任务大师：{},人数:{}", siteCode, MedalCodeEnum.MEDAL_1017.getName(), medalAcquireReqVOList.size());
        KafkaUtil.send(TopicsConstants.MEDAL_ACQUIRE_QUEUE, medalAcquireBatchReqVO);
    }

    /**
     * 判断用户是否符合奖励标准,每日任务
     */
    private boolean isEligibleForRewardDaily(ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO, SiteTaskConfigPO taskConfig) {
        // 配置的是平台币，用户投注是主货币，需要进行汇率转换

        if (TaskEnum.DAILY_BET.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 有效投注大于配置的最小金额
            return taskConfig.getMinBetAmount().compareTo(ReportUserVenueBetsTopVO.getValidAmount()) <= 0;
        } else if (TaskEnum.DAILY_PROFIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 判断用户输赢金额是否符合要求
            return ReportUserVenueBetsTopVO.getWinLossAmount().compareTo(BigDecimal.ZERO) > 0 && ReportUserVenueBetsTopVO.getWinLossAmount().compareTo(taskConfig.getMinBetAmount()) >= 0;
        } else {
            return ReportUserVenueBetsTopVO.getWinLossAmount().compareTo(BigDecimal.ZERO) < 0 && ReportUserVenueBetsTopVO.getWinLossAmount().negate().compareTo(taskConfig.getMinBetAmount()) >= 0;
        }
    }


    /**
     * 判断用户是否符合奖励标准,每日任务
     */
    private boolean isEligibleForRewardDaily(ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO, SiteTaskConfigPO taskConfig, TaskSubConfigReqVO subConfigReqVO) {
        // 配置的是平台币，用户投注是主货币，需要进行汇率转换

        if (TaskEnum.DAILY_BET.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 有效投注大于配置的最小金额
            return subConfigReqVO.getMinBetAmount().compareTo(ReportUserVenueBetsTopVO.getPlatValidAmount()) <= 0;
        } else if (TaskEnum.DAILY_PROFIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 判断用户输赢金额是否符合要求
            return ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(BigDecimal.ZERO) > 0 && ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(subConfigReqVO.getMinBetAmount()) >= 0;
        } else {
            return ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(BigDecimal.ZERO) < 0 && ReportUserVenueBetsTopVO.getPlatWinLossAmount().negate().compareTo(subConfigReqVO.getMinBetAmount()) >= 0;
        }
    }

    /**
     * 判断用户是否符合奖励标准,每周任务任务
     */
    private boolean isEligibleForRewardWeek(ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO, SiteTaskConfigPO taskConfig, TaskSubConfigReqVO subConfigReqVO) {
        if (TaskEnum.WEEK_BET.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 有效投注大于配置的最小金额
            return ReportUserVenueBetsTopVO.getPlatValidAmount().compareTo(subConfigReqVO.getMinBetAmount()) >= 0;
        } else if (TaskEnum.WEEK_PROFIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
            // 判断用户输赢金额是否符合要求
            return ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(BigDecimal.ZERO) >= 0 && ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(subConfigReqVO.getMinBetAmount()) >= 0;

        } else {
            return ReportUserVenueBetsTopVO.getPlatWinLossAmount().compareTo(BigDecimal.ZERO) <= 0 && ReportUserVenueBetsTopVO.getPlatWinLossAmount().negate().compareTo(subConfigReqVO.getMinBetAmount()) >= 0;
        }
    }

    /**
     * 创建订单记录
     */
    private SiteTaskOrderRecordPO createOrderRecord(ReportUserVenueBetsTopVO ReportUserVenueBetsTopVO, SiteTaskConfigPO taskConfig, String siteCode, Long timeEnd, String dateTime) {

        // 当天时间
        String serialNo = TaskEnum.fromSubTaskType(taskConfig.getSubTaskType()).getSerialNo();
        String orderNo = OrderNoUtils.genOrderNo(ReportUserVenueBetsTopVO.getUserId(), serialNo, dateTime);

        BigDecimal runningWater = NumberUtil.mul(taskConfig.getRewardAmount(), taskConfig.getWashRatio()).setScale(2, RoundingMode.UP);

        SiteTaskOrderRecordPO record = new SiteTaskOrderRecordPO();
        record.setOrderNo(orderNo);
        record.setSiteCode(siteCode);
        record.setTaskId(taskConfig.getId());
        record.setTaskType(taskConfig.getTaskType());
        record.setSubTaskType(taskConfig.getSubTaskType());
        record.setUserId(ReportUserVenueBetsTopVO.getUserId());
        record.setUserAccount(ReportUserVenueBetsTopVO.getUserAccount());
        record.setDistributionType(TaskDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode());
        record.setReceiveStartTime(System.currentTimeMillis());
        record.setReceiveEndTime(timeEnd);
        // 奖励，查找获取的奖励
        record.setTaskAmount(taskConfig.getRewardAmount());
        // 都是插入的可领取状态
        record.setReceiveStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());
        // 需要更改的 wade
        record.setRunningWater(runningWater);
        record.setRemark(String.format("%s-%s, 奖励:[%s %s]", TaskEnum.fromTask(taskConfig.getTaskType(), taskConfig.getSubTaskType()).getTaskType(), TaskEnum.fromTask(taskConfig.getTaskType(), taskConfig.getSubTaskType()).getName(), taskConfig.getRewardAmount(), taskConfig.getCurrencyCode()));
        UserInfoVO userInfo = userInfoApi.getByUserId(ReportUserVenueBetsTopVO.getUserId());
        record.setVipRankCode(userInfo.getVipRank());
        record.setVipGradeCode(userInfo.getVipGradeCode());
        record.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        record.setSuperAgentId(userInfo.getSuperAgentId());
        record.setUserName(userInfo.getUserName());
        record.setWashRatio(taskConfig.getWashRatio());
        return record;
    }


    /**
     * 创建订单记录-通用
     */
    private SiteTaskOrderRecordPO createOrderDepositRecord(UserInfoVO userInfo, SiteTaskConfigPO taskConfig, String siteCode, Long timeEnd, String dateTime, TaskSubConfigReqVO configReqVO) {

        // 当天时间
        String serialNo = TaskEnum.fromSubTaskType(taskConfig.getSubTaskType()).getSerialNo();
        String orderNo = OrderNoUtils.genOrderNo(userInfo.getUserId(), serialNo, dateTime);

        BigDecimal runningWater = NumberUtil.mul(configReqVO.getRewardAmount(), taskConfig.getWashRatio()).setScale(2, RoundingMode.UP);

        SiteTaskOrderRecordPO record = new SiteTaskOrderRecordPO();
        record.setOrderNo(orderNo);
        record.setSiteCode(siteCode);
        record.setTaskId(taskConfig.getId());
        record.setTaskType(taskConfig.getTaskType());
        record.setSubTaskType(taskConfig.getSubTaskType());
        record.setUserId(userInfo.getUserId());
        record.setUserAccount(userInfo.getUserAccount());
        record.setDistributionType(TaskDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode());
        record.setReceiveStartTime(System.currentTimeMillis());
        record.setReceiveEndTime(timeEnd);
        record.setTaskAmount(configReqVO.getRewardAmount());
        // 都是插入的可领取状态
        record.setReceiveStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());
        record.setRunningWater(runningWater);
        record.setRemark(String.format("%s-%s, 奖励:[%s %s]", TaskEnum.fromTask(taskConfig.getTaskType(),
                taskConfig.getSubTaskType()).getTaskType(), TaskEnum.fromTask(taskConfig.getTaskType(),
                taskConfig.getSubTaskType()).getName(), configReqVO.getRewardAmount(), taskConfig.getCurrencyCode()));
        record.setVipRankCode(userInfo.getVipRank());
        record.setVipGradeCode(userInfo.getVipGradeCode());
        record.setCurrencyCode(CommonConstant.PLAT_CURRENCY_CODE);
        record.setSuperAgentId(userInfo.getSuperAgentId());
        record.setUserName(userInfo.getUserName());
        record.setWashRatio(taskConfig.getWashRatio());
        record.setStep(configReqVO.getStep());
        return record;
    }


    public boolean updateWallet(SiteTaskOrderRecordPO insert, UserInfoVO userInfoVO) {
        boolean coinResult;
        // 平台币上分
        UserPlatformCoinAddVO userPlatformCoinAddVO = new UserPlatformCoinAddVO();
        userPlatformCoinAddVO.setOrderNo(insert.getOrderNo());
        userPlatformCoinAddVO.setUserId(insert.getUserId());
        userPlatformCoinAddVO.setUserInfoVO(ConvertUtil.entityToModel(userInfoVO, WalletUserInfoVO.class));
        userPlatformCoinAddVO.setCoinType(PlatformWalletEnum.CoinTypeEnum.PROMOTIONS.getCode());
        userPlatformCoinAddVO.setBalanceType(CoinBalanceTypeEnum.INCOME.getCode());
        userPlatformCoinAddVO.setBusinessCoinType(PlatformWalletEnum.BusinessCoinTypeEnum.MEMBER_ACTIVITIES.getCode());
        userPlatformCoinAddVO.setCoinValue(insert.getTaskAmount());
        TaskEnum taskEnum = TaskEnum.fromSubTaskType(insert.getSubTaskType());
        userPlatformCoinAddVO.setActivityFlag(taskEnum.getAccountCoinType());
        String remark = TaskEnum.fromSubTaskType(insert.getSubTaskType()).getName();
        userPlatformCoinAddVO.setRemark(remark + insert.getTaskAmount());
        CoinRecordResultVO recordResultVO = activityUserCommonPlatformCoinService.userCommonPlatformCoin(userPlatformCoinAddVO);
        if (recordResultVO == null) {
            return false;
        }
        //   默认是ture
        coinResult = recordResultVO.getResult();
        // 如果是false 判断是否是重复交易，如果交易重复，也设置为成功
        /*if (!coinResult) {
            if (recordResultVO.getResultStatus() == UpdateBalanceStatusEnums.REPEAT_TRANSACTIONS) {
                coinResult = true;
            }
        }*/
        //增加打码量 = 只能是法币

        return coinResult;
    }

    public void sendTypingAmount(SiteTaskOrderRecordPO insert, UserInfoVO userInfoVO, BigDecimal finalRate) {

        //增加打码量 = 只能是法币

        BigDecimal runningWaterMultiple = insert.getWashRatio();
        BigDecimal activityAmount = insert.getTaskAmount();
        if (CommonConstant.PLAT_CURRENCY_CODE.equals(insert.getCurrencyCode())) {
            //平台币转法币
            activityAmount = AmountUtils.multiply(insert.getTaskAmount(), finalRate);
        }

        if (runningWaterMultiple == null) {
            log.info("打码倍数为空,配置错误");
            return;
        }
        if (activityAmount == null) {
            log.info("奖励金额为空,配置错误");
            return;
        }

        //流水倍数 * 打码量 = 所需打码量
        BigDecimal typingAmount = AmountUtils.multiply(activityAmount, runningWaterMultiple);
        UserTypingAmountRequestVO userTypingAmountRequestVO = new UserTypingAmountRequestVO();
        userTypingAmountRequestVO.setTypingAmount(typingAmount);
        userTypingAmountRequestVO.setOrderNo(insert.getOrderNo());
        userTypingAmountRequestVO.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmountRequestVO.setType(TypingAmountEnum.ADD.getCode());
        userTypingAmountRequestVO.setSiteCode(CurrReqUtils.getSiteCode());
        userTypingAmountRequestVO.setUserAccount(userInfoVO.getUserAccount());
        userTypingAmountRequestVO.setUserId(userInfoVO.getUserId());
        userTypingAmountRequestVO.setAdjustType(TypingAmountAdjustTypeEnum.TASK.getCode());
        UserTypingAmountMqVO userTypingAmountMqVO = UserTypingAmountMqVO.builder().userTypingAmountRequestVOList(List.of(userTypingAmountRequestVO)).build();
        //
        log.info("任务发送打码量:{}", JSONObject.toJSONString(userTypingAmountMqVO));
        KafkaUtil.send(TopicsConstants.PUSH_TYPING_AMOUNT_TOPIC, userTypingAmountMqVO);


    }


    public ResponseVO<APPTaskResponseVO> detail(APPTaskReqVO requestVO) {

        // 初始化响应对象
        APPTaskResponseVO responseAppTask = new APPTaskResponseVO();
        responseAppTask.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
        responseAppTask.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
        // 计算该用户已领取的任务奖励总和
        BigDecimal totalAmount = siteTaskOrderRecordRepository.sumTaskAmountByUserId(requestVO.getUserId(), TaskReceiveStatusEnum.CLAIMED.getCode());
        responseAppTask.setTotalAmount(totalAmount != null ? totalAmount : BigDecimal.ZERO);
        UserInfoVO userInfoVO = userInfoApi.getByUserId(requestVO.getUserId());


        // 处理每日任务
        handleDailyTasks(requestVO, responseAppTask, userInfoVO);

        // 处理每周任务
        handleWeeklyTasks(requestVO, responseAppTask, userInfoVO);

        // 处理新人任务，当其他任务都是禁止，则新人任务过期也展示
        handleNoviceTasks(requestVO, responseAppTask, userInfoVO);

        // add by xiaozhi 2025-02-05 需要汇总过期时间
        if (ObjectUtil.isNotEmpty(responseAppTask.getDailyTask())) {
            responseAppTask.setDailyTaskEndTime(responseAppTask.getDailyTask().stream().filter(obj -> obj.getExpireTime() != null).max(Comparator.comparing(TaskUserConfigAppResVO::getExpireTime)).orElse(new TaskUserConfigAppResVO()).getExpireTime());
        }
        if (ObjectUtil.isNotEmpty(responseAppTask.getWeeklyTask())) {
            responseAppTask.setWeeklyEndTime(responseAppTask.getWeeklyTask().stream().filter(obj -> obj.getExpireTime() != null).max(Comparator.comparing(TaskUserConfigAppResVO::getExpireTime)).orElse(new TaskUserConfigAppResVO()).getExpireTime());
        }
        if (ObjectUtil.isNotEmpty(responseAppTask.getNoviceTask())) {
            responseAppTask.setNoviceEndTime(responseAppTask.getNoviceTask().stream().filter(obj -> obj.getExpireTime() != null).max(Comparator.comparing(TaskUserConfigAppResVO::getExpireTime)).orElse(new TaskUserConfigAppResVO()).getExpireTime());
        }
        // 返回响应结果
        // 查看是否折叠
        responseAppTask.setExpandStatus(getSiteTaskOverViewConfig(requestVO.getSiteCode()).getExpandStatus());
        // 判断任务是否都是禁止，只有一条任务没有禁止就是true，否则是false
        responseAppTask.setAllTaskStatus(checkAllTaskStatus(responseAppTask));
        // 图卡
        // 处理卡图配置
        handleFlashCardTasksForDetail(requestVO, responseAppTask);
        return ResponseVO.success(responseAppTask);
    }

    /**
     * 检查任务列表中是否至少存在一个启用状态的任务（status == 1）
     * <p>
     * 包括：日常任务、每周任务、新手任务，只要任意一个任务启用（即 status 为 1），则返回 true；
     * 如果全部任务为空或都为禁用状态，则返回 false。
     *
     * @param responseAppTask 包含各类任务列表的任务响应对象
     * @return 是否存在至少一个启用状态的任务
     */
    private boolean checkAllTaskStatus(APPTaskResponseVO responseAppTask) {
        // 检查日常任务是否有启用任务
        if (CollectionUtil.isNotEmpty(responseAppTask.getDailyTask()) &&
                responseAppTask.getDailyTask().stream().anyMatch(task -> task.getStatus() == 1)) {
            return true;
        }

        // 检查每周任务是否有启用任务
        if (CollectionUtil.isNotEmpty(responseAppTask.getWeeklyTask()) &&
                responseAppTask.getWeeklyTask().stream().anyMatch(task -> task.getStatus() == 1)) {
            return true;
        }

        // 检查新手任务是否有启用任务
        if (CollectionUtil.isNotEmpty(responseAppTask.getNoviceTask()) &&
                responseAppTask.getNoviceTask().stream().anyMatch(task -> task.getStatus() == 1)) {
            return true;
        }

        // 都没有启用任务
        return false;
    }

    /**
     * 获取站点任务总览配置（SiteTaskOverViewConfigPO）。
     * <p>
     * 若数据库中不存在指定站点（siteCode）的配置记录，则返回一个默认配置对象：
     * expandStatus 默认为 1，siteCode 设置为传入值。
     * </p>
     *
     * @param siteCode 站点编码
     * @return SiteTaskOverViewConfigPO 对象，包含站点的任务总览配置，或默认配置
     */
    private SiteTaskOverViewConfigPO getSiteTaskOverViewConfig(String siteCode) {
        LambdaQueryWrapper<SiteTaskOverViewConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskOverViewConfigPO::getSiteCode, siteCode);
        SiteTaskOverViewConfigPO siteTaskOverViewConfigPO = overViewConfigRepository.selectOne(queryWrapper);
        if (ObjectUtil.isNull(siteTaskOverViewConfigPO)) {
            siteTaskOverViewConfigPO = new SiteTaskOverViewConfigPO();
            siteTaskOverViewConfigPO.setExpandStatus(1);
            siteTaskOverViewConfigPO.setSiteCode(siteCode);
            return siteTaskOverViewConfigPO;
        }
        return siteTaskOverViewConfigPO;
    }

    public ResponseVO<APPTaskConfigResponseVO> config(APPTaskReqVO requestVO) {
        // 初始化响应对象
        APPTaskConfigResponseVO appTaskConfigResponseVO = new APPTaskConfigResponseVO();
        appTaskConfigResponseVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
        appTaskConfigResponseVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
        long currentTimeMillis = System.currentTimeMillis();
        String timeZone = requestVO.getTimeZone();
        // 处理每日任务
        List<SiteTaskConfigPO> dailyTaskConfigPOS = getSiteTaskConfigs(requestVO.getSiteCode(), TaskEnum.DAILY_BET.getTaskType());
        // 终端是否匹配


        if (CollectionUtil.isNotEmpty(dailyTaskConfigPOS)) {
            appTaskConfigResponseVO.setDailyTaskFlag(true);
            long timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timeZone);
            appTaskConfigResponseVO.setDailyTaskEndTime(timeEnd);
        }
        //appTaskConfigResponseVO.setDailyTask(ConvertUtil.entityListToModelList(dailyTaskConfigPOS, TaskUserConfigAppResVO.class));


        // 处理每周任务
        List<SiteTaskConfigPO> weekTaskConfigPOS = getSiteTaskConfigs(requestVO.getSiteCode(), TaskEnum.WEEK_BET.getTaskType());
        if (CollectionUtil.isNotEmpty(weekTaskConfigPOS)) {
            appTaskConfigResponseVO.setWeeklyTaskFlag(true);
            long timeEnd = TimeZoneUtils.getEndOfWeekInTimeZone(currentTimeMillis, timeZone);
            appTaskConfigResponseVO.setWeeklyEndTime(timeEnd);
        }
        //appTaskConfigResponseVO.setWeeklyTask(ConvertUtil.entityListToModelList(weekTaskConfigPOS, TaskUserConfigAppResVO.class));
        appTaskConfigResponseVO.setExpandStatus(getSiteTaskOverViewConfig(requestVO.getSiteCode()).getExpandStatus());
        // 处理卡图配置
        handleFlashCardTasks(requestVO, appTaskConfigResponseVO);
        // 返回响应结果
        return ResponseVO.success(appTaskConfigResponseVO);
    }

    private void handleFlashCardTasks(APPTaskReqVO requestVO, APPTaskConfigResponseVO appTaskConfigResponseVO) {
        LambdaQueryWrapper<SiteTaskFlashCardBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskFlashCardBasePO::getSiteCode, requestVO.getSiteCode());
        List<SiteTaskFlashCardBasePO> siteTaskFlashCardBasePOS = siteTaskFlashCardRepository.selectList(queryWrapper);
        String terminal = requestVO.getShowTerminal();
        if (CollectionUtil.isNotEmpty(siteTaskFlashCardBasePOS)) {
            for (SiteTaskFlashCardBasePO po : siteTaskFlashCardBasePOS) {
                if (po.getTaskType().equals(TaskEnum.WEEK_BET.getTaskType()) && po.getStatus() == 1) {
                    // 终端是否匹配
                    if (ObjectUtil.isNotEmpty(po.getShowTerminal())) {
                        if (!po.getShowTerminal().contains(terminal)) {
                            continue;
                        }
                    }
                    appTaskConfigResponseVO.setWeekFlashCardTaskFlag(true);
                    // 根据终端选择图片
                    appTaskConfigResponseVO.setWeeklyTaskFlashFlag(ConvertUtil.entityToModel(po, SiteTaskFlashCardBaseAPPRespVO.class));

                } else if (po.getTaskType().equals(TaskEnum.DAILY_BET.getTaskType()) && po.getStatus() == 1) {
                    // 终端是否匹配
                    if (ObjectUtil.isNotEmpty(po.getShowTerminal())) {
                        if (!po.getShowTerminal().contains(terminal)) {
                            continue;
                        }
                    }
                    appTaskConfigResponseVO.setDailyTFlashCardTaskFlag(true);
                    appTaskConfigResponseVO.setDailyTaskFlashFlag(ConvertUtil.entityToModel(po, SiteTaskFlashCardBaseAPPRespVO.class));
                }
            }
        }

    }

    private void handleFlashCardTasksForDetail(APPTaskReqVO requestVO, APPTaskResponseVO appTaskConfigResponseVO) {
        LambdaQueryWrapper<SiteTaskFlashCardBasePO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskFlashCardBasePO::getSiteCode, requestVO.getSiteCode());
        List<SiteTaskFlashCardBasePO> siteTaskFlashCardBasePOS = siteTaskFlashCardRepository.selectList(queryWrapper);
        if (CollectionUtil.isNotEmpty(siteTaskFlashCardBasePOS)) {
            for (SiteTaskFlashCardBasePO po : siteTaskFlashCardBasePOS) {
                if (po.getTaskType().equals(TaskEnum.WEEK_BET.getTaskType()) && po.getStatus() == 1) {
                    appTaskConfigResponseVO.setWeekFlashCardTaskFlag(true);
                    appTaskConfigResponseVO.setWeeklyTaskFlashFlag(ConvertUtil.entityToModel(po, SiteTaskFlashCardBaseAPPRespVO.class));

                } else if (po.getTaskType().equals(TaskEnum.DAILY_BET.getTaskType()) && po.getStatus() == 1) {
                    appTaskConfigResponseVO.setDailyTFlashCardTaskFlag(true);
                    appTaskConfigResponseVO.setDailyTaskFlashFlag(ConvertUtil.entityToModel(po, SiteTaskFlashCardBaseAPPRespVO.class));
                }
            }
        }

    }

    // 处理新人任务
    private void handleNoviceTasks(APPTaskReqVO requestVO, APPTaskResponseVO responseAppTask, UserInfoVO userInfoVO) {
        List<SiteTaskConfigPO> noviceConfigPOS = getSiteTaskConfigs(requestVO.getSiteCode(), TaskEnum.NOVICE_WELCOME.getTaskType());

        // 过滤测试
        // 设置用户注册时间
        long registerTime = userInfoVO.getRegisterTime();
        // 过期截止时间， value 是过期多个小时。 换成数据字典
        // Integer value = taskConfigProperties.getTime();
        ResponseVO<SystemDictConfigRespVO> byCode = systemDictConfigApi.getByCode(DictCodeConfigEnums.TASK_BENEFIT_EXPIRATION_TIME.getCode(), requestVO.getSiteCode());
        if (!byCode.isOk()) {
            log.error("任务获取参数字典失败{}", JSONObject.toJSONString(byCode));
            return;
        }
        Double value = Double.valueOf(byCode.getData().getConfigParam());
        Long deadline = registerTime + (long) (60 * 60 * value * 1000L);
        // 截止时间与当前时间比较，如果当前时间大于截止时间，
        long expireTime = (deadline - System.currentTimeMillis()) / 1000;
        // 判断每日任务与每周任务是否都禁止
        boolean isAllTaskFor = responseAppTask != null
                && ObjectUtil.isEmpty(responseAppTask.getDailyTask())
                && ObjectUtil.isEmpty(responseAppTask.getWeeklyTask());
        // 新人任务过期了，且只要有一个其他任务，则不展示
        if (expireTime < 0 && !isAllTaskFor) {
            return;
        }
        // 截止时间
        if (CollectionUtil.isNotEmpty(noviceConfigPOS)) {
            // 查询用户完成情况
            List<SiteTaskOrderRecordPO> recordPOS = getSiteTaskOrderRecords(Collections.singletonList(requestVO.getUserId()), TaskEnum.NOVICE_WELCOME.getTaskType(), null, null);
            List<TaskUserConfigAppResVO> noviceResults = new ArrayList<>();
            List<String> subTaskTypes = new ArrayList<>();
            for (SiteTaskConfigPO noviceConfig : noviceConfigPOS) {
                TaskUserConfigAppResVO appResVO = ConvertUtil.entityToModel(noviceConfig, TaskUserConfigAppResVO.class);
                // 检查用户是否完成任务
                String subtaskType = noviceConfig.getSubTaskType();
                Optional<SiteTaskOrderRecordPO> orderRecordPO = recordPOS.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), noviceConfig.getSubTaskType())).findFirst();

                // 根据完成情况设置任务状态
                if (orderRecordPO.isPresent()) {
                    appResVO.setTaskStatus(orderRecordPO.get().getReceiveStatus());
                } else {
                    //任务不存在
                    appResVO.setTaskStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode());
                    // 如果没有完成，则去用户表查询是否已经完成-修改为不去查询，没有发就不补发了
                    if (StringUtils.equals(subtaskType, TaskEnum.NOVICE_EMAIL.getSubTaskType())) {
                        // 判断是否有邮箱绑定
                        if (StringUtils.isNotBlank(userInfoVO.getEmail())) {
                            // 插入完成任务消息
                            subTaskTypes.add(TaskEnum.NOVICE_EMAIL.getSubTaskType());
                            appResVO.setTaskStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());

                        }
                    } else if (StringUtils.equals(subtaskType, TaskEnum.NOVICE_PHONE.getSubTaskType())) {
                        // 判断是否有手机号码绑定
                        if (StringUtils.isNotBlank(userInfoVO.getPhone())) {
                            // 插入完成任务消息
                            subTaskTypes.add(TaskEnum.NOVICE_PHONE.getSubTaskType());
                            appResVO.setTaskStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());
                        }
                    } else {
                        // 新人任务欢迎新人 与币种确认，获取即可
                        subTaskTypes.add(subtaskType);
                        appResVO.setTaskStatus(TaskReceiveStatusEnum.ELIGIBLE.getCode());
                    }
                    if(expireTime < 0) {
                        appResVO.setTaskStatus(TaskReceiveStatusEnum.EXPIRED.getCode());
                    }
                }
                // 过期时间，这个需要重新配置
                appResVO.setExpireTime(expireTime);
                appResVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
                appResVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());

                noviceResults.add(appResVO);
            }
            // 补发机制：
            TaskNoviceTriggerVO triggerVO = TaskNoviceTriggerVO.builder()
                    .siteCode(userInfoVO.getSiteCode()).subTaskTypes(subTaskTypes)
                    .userAccount(userInfoVO.getUserAccount()).userId(userInfoVO.getUserId())
                    .vipGradeCode(userInfoVO.getVipGradeCode())
                    .vipRankCode(userInfoVO.getVipRank())
                    .registerTime(userInfoVO.getRegisterTime())
                    .superAgentId(userInfoVO.getSuperAgentId()).build();
            if (!_this.process(triggerVO)) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            noviceResults = sortByReceiveStatusAndSort(noviceResults);
            responseAppTask.setNoviceTask(noviceResults);
        }
    }

    /**
     * 更新处理流程的方法。
     *
     * @param requestVO 包含站点代码和用户 ID 的请求参数对象。
     */
    private void queryAndUpdateProcess(APPTaskReqVO requestVO) {
        UserVenueWinLossSendVO sendVO = new UserVenueWinLossSendVO();
        List<UserVenueWinLossMqVO> voList = new ArrayList<>(1);
        UserVenueWinLossMqVO mqVO = new UserVenueWinLossMqVO();
        mqVO.setSiteCode(requestVO.getSiteCode());
        mqVO.setUserId(requestVO.getUserId());
        voList.add(mqVO);
        sendVO.setVoList(voList);
        processDailyAndWeek(sendVO);
    }

    /**
     * 计算任务订单的领取状态
     * <p>
     * 规则：
     * 1. 如果列表中 **全部是 `0`** → 返回 `0`（可领取）。
     * 2. 如果列表中 **全部是 `1`** → 返回 `1`（已领取）。
     * 3. 如果列表中 **全部是 `3`** → 返回 `3`（未达标）。
     * 4. 如果列表中 **包含 `0`**（无论是否有 `1` 或 `3`） → 返回 `0`（可领取）。
     * 5. 如果列表中 **只有 `1` 和 `3`（但没有 `0`）** → 返回 `3`（未达标）。
     *
     * @param taskOrderRecordPOS 任务订单记录列表
     * @return 任务领取状态（0-可领取, 1-已领取, 3-未达标）
     */
    private int calculateReceiveStatus(List<APPTaskSubConfigReqVO> taskOrderRecordPOS) {
        // boolean hasZero = false;  // 是否有 0（可领取）
        boolean hasOne = false;   // 是否有 1（已领取）
        boolean hasThree = false; // 是否有 3（未达标）

        for (APPTaskSubConfigReqVO orderRecordPO : taskOrderRecordPOS) {
            int status = orderRecordPO.getReceiveStatus();

            if (status == 0) {
                return 0; // 只要有 0，则直接返回 0（可领取）
            }
            if (status == 1) {
                hasOne = true;
            }
            if (status == 3) {
                hasThree = true;
            }
        }

        // 如果只有 1 和 3（但没有 0），应返回 3（未达标）
        if (hasOne && hasThree) {
            return 3;
        }
        // 只有 1，没有 0 或 3
        if (hasOne) {
            return 1; // 已领取
        }

        return 3; // 默认返回 3（未达标）
    }


    /**
     * 计算任务配置的总存款金额
     *
     * @param taskConfigJson 任务配置的 JSON 字符串
     * @return 总存款金额（如果 `depositAmount` 为空，则默认为 0）
     */
    private BigDecimal calculateTotalDepositAmount(String taskConfigJson) {
        if (StringUtils.isBlank(taskConfigJson)) {
            return BigDecimal.ZERO;
        }
        List<APPTaskSubConfigReqVO> configReqVOS = JSON.parseArray(taskConfigJson, APPTaskSubConfigReqVO.class);

        return configReqVOS.stream()
                .map(vo -> vo.getDepositAmount() == null ? BigDecimal.ZERO : vo.getDepositAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 查找配置的最大存款
     *
     * @param taskConfigJson 任务配置的 JSON 字符串
     * @return 总存款金额（如果 `depositAmount` 为空，则默认为 0）
     */
    private BigDecimal calculateMaxDepositAmount(String taskConfigJson) {
        if (StringUtils.isBlank(taskConfigJson)) {
            return BigDecimal.ZERO;
        }
        List<APPTaskSubConfigReqVO> configReqVOS = JSON.parseArray(taskConfigJson, APPTaskSubConfigReqVO.class);


        return configReqVOS.stream()
                .map(vo -> vo.getDepositAmount() == null ? BigDecimal.ZERO : vo.getDepositAmount())
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
    }

    /**
     * 查找配置的最大邀请朋友个数
     *
     * @param taskConfigJson 任务配置的 JSON 字符串
     * @return 总存款金额（如果 `depositAmount` 为空，则默认为 0）
     */
    private Integer calculateMaxFriendsAmount(String taskConfigJson) {
        if (StringUtils.isBlank(taskConfigJson)) {
            return 0;
        }
        List<APPTaskSubConfigReqVO> configReqVOS = JSON.parseArray(taskConfigJson, APPTaskSubConfigReqVO.class);


        return configReqVOS.stream()
                .map(vo -> vo.getInviteFriendCount() == null ? 0 : vo.getInviteFriendCount())
                .max(Integer::compareTo)
                .orElse(0);
    }

    /**
     * 计算任务配置的邀请任务，奖励总存款金额
     *
     * @param taskConfigJson 任务配置的 JSON 字符串
     * @return 总存款金额（如果 `depositAmount` 为空，则默认为 0）
     */
    private BigDecimal calculateTotalRewardAmount(String taskConfigJson) {
        if (StringUtils.isBlank(taskConfigJson)) {
            return BigDecimal.ZERO;
        }
        List<APPTaskSubConfigReqVO> configReqVOS = JSON.parseArray(taskConfigJson, APPTaskSubConfigReqVO.class);

        return configReqVOS.stream()
                .map(vo -> vo.getRewardAmount() == null ? BigDecimal.ZERO : vo.getRewardAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    // 处理每日任务，app端展示
    private void handleDailyTasks(APPTaskReqVO requestVO, APPTaskResponseVO responseAppTask, UserInfoVO userInfoVO) {
        String userId = requestVO.getUserId();
        List<SiteTaskConfigPO> dailyTaskConfigPOS = getSiteTaskConfigs(requestVO.getSiteCode(), TaskEnum.DAILY_BET.getTaskType());
        if (CollectionUtil.isNotEmpty(dailyTaskConfigPOS)) {
            String timeZone = CurrReqUtils.getTimezone();
            long currentTimeMillis = System.currentTimeMillis();

            // 确定时间范围：如果在 00:00-00:05 之间，则查看前一天的数据
            long timeStart;
            long timeEnd;
            LocalDateTime now = TimeZoneUtils.timeByTimeZone(currentTimeMillis, timeZone);
            if (now.getHour() == 0 && now.getMinute() < 5) {
                // 获取前一天的开始和结束时间
                LocalDateTime yesterday = now.minusDays(1);
                timeStart = TimeZoneUtils.getStartOfDayInTimeZone(yesterday.atZone(ZoneId.of(timeZone)).toInstant().toEpochMilli(), timeZone);
                timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(yesterday.atZone(ZoneId.of(timeZone)).toInstant().toEpochMilli(), timeZone);
            } else {
                // 获取今天的开始时间
                timeStart = TimeZoneUtils.getStartOfDayInTimeZone(currentTimeMillis, timeZone);
                timeEnd = TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timeZone);
            }

            // 查询用户每日任务完成情况
            List<SiteTaskOrderRecordPO> dailyRecordPOS = getSiteTaskOrderRecords(Arrays.asList(requestVO.getUserId()), TaskEnum.DAILY_BET.getTaskType(), timeStart, timeEnd);
            List<TaskUserConfigAppResVO> dailyResults = new ArrayList<>();
            // 查询用户存款
            List<UserDepositAmountVO> userDepositAmountVOS = userDepositWithdrawApi.queryDepositAmountByUserIds(UserDepositAmountReqVO.builder()
                    .userIds(Collections.singletonList(userId)).startTime(timeStart).endTime(timeEnd).build());
            // 主货币转换未平台币
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(requestVO.getSiteCode());
            // 查询投注结果
            ReportUserVenueBetsTopVO venueBetsTopVO = queryReportUserVenueBetsTopVO(requestVO.getSiteCode(), Collections.singletonList(requestVO.getUserId()), timeStart, timeEnd, null);
            for (SiteTaskConfigPO taskConfig : dailyTaskConfigPOS) {
                TaskUserConfigAppResVO appResVO = ConvertUtil.entityToModel(taskConfig, TaskUserConfigAppResVO.class);
                if (StringUtils.isNotBlank(taskConfig.getTaskConfigJson())) {
                    appResVO.setTaskConfigJson(JSON.parseArray(taskConfig.getTaskConfigJson(), APPTaskSubConfigReqVO.class));
                }

                // 检查用户是否完成任务
                List<SiteTaskOrderRecordPO> taskOrderRecordPOS = dailyRecordPOS.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), taskConfig.getSubTaskType())).toList();


                // 根据完成情况设置任务状态和达成金额，有奖励

                // 设置每个阶梯是否获取奖励
                List<APPTaskSubConfigReqVO> taskConfigJson = appResVO.getTaskConfigJson();
                // 配置阶梯累计综合
                // 1. 累计奖励
                appResVO.setRewardAmount(calculateTotalRewardAmount(taskConfig.getTaskConfigJson()).setScale(2, RoundingMode.HALF_UP));
                List<SiteTaskOrderRecordPO> recordSubPOS = taskOrderRecordPOS.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), taskConfig.getSubTaskType())).toList();
                for (APPTaskSubConfigReqVO taskSubConfigReqVO : taskConfigJson) {
                    // 是否获取奖项
                    if (CollectionUtil.isNotEmpty(recordSubPOS)) {

                        recordSubPOS.stream().filter(e -> ObjectUtil.equals(e.getStep(), taskSubConfigReqVO.getStep())).findFirst().ifPresentOrElse(e -> {
                            taskSubConfigReqVO.setRewardFlag(true);
                            taskSubConfigReqVO.setReceiveStatus(e.getReceiveStatus());
                            taskSubConfigReqVO.setRecordId(e.getId());
                        }, () -> {
                            taskSubConfigReqVO.setRewardFlag(false);
                            taskSubConfigReqVO.setReceiveStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode()); // 或者默认值


                        });
                    } else {
                        taskSubConfigReqVO.setRewardFlag(false);
                        taskSubConfigReqVO.setReceiveStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode()); // 或


                    }

                    // 每日投注
                    if (TaskEnum.DAILY_BET.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                        if (venueBetsTopVO.getValidAmount() != null && venueBetsTopVO.getPlatValidAmount().compareTo(taskConfig.getMinBetAmount()) >= 0) {
                            // 处理每日任务, 当检查到还没有处理，会员查询，手工处理
                            //queryAndUpdateProcess(requestVO);
                        }
                        assert appResVO != null;
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {
                            taskSubConfigReqVO.setAchieveAmount(venueBetsTopVO.getPlatValidAmount().compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : venueBetsTopVO.getPlatValidAmount());
                        }

                        // 每日盈利
                    } else if (TaskEnum.DAILY_PROFIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                        BigDecimal achieveAmount = venueBetsTopVO.getPlatWinLossAmount() != null ? venueBetsTopVO.getPlatWinLossAmount() : BigDecimal.ZERO;
                        if (achieveAmount.compareTo(BigDecimal.ZERO) < 0) {
                            achieveAmount = BigDecimal.ZERO;
                        }
                        if (achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0) {
                            // 处理每日任务
                            //queryAndUpdateProcess(requestVO);
                        }
                        assert appResVO != null;
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {

                            taskSubConfigReqVO.setAchieveAmount(achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : achieveAmount);
                        }
                        // 每日负盈利
                    } else if (TaskEnum.DAILY_NEGATIVE.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                        BigDecimal achieveAmount = venueBetsTopVO.getPlatWinLossAmount() != null ? venueBetsTopVO.getPlatWinLossAmount() : BigDecimal.ZERO;
                        // 去反
                        if (achieveAmount.compareTo(BigDecimal.ZERO) > 0) {
                            achieveAmount = BigDecimal.ZERO;
                        } else {
                            achieveAmount = achieveAmount.negate();
                        }

                        if (achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0) {
                            // 处理每日任务
                            //queryAndUpdateProcess(requestVO);
                        }
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {

                            assert appResVO != null;
                            taskSubConfigReqVO.setAchieveAmount(achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : achieveAmount);
                        }
                    } else if (TaskEnum.DAILY_DEPOSIT.getSubTaskType().equals(taskConfig.getSubTaskType())) {
                        // 只查询，不处理 ,表示一个也没有获取。
                        BigDecimal achieveAmount = calculateUserDepositPlat(userDepositAmountVOS, userInfoVO.getMainCurrency(), allFinalRate);
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getDepositAmount());
                        } else {

                            assert appResVO != null;
                            // 会员实际存款
                            taskSubConfigReqVO.setAchieveAmount(achieveAmount.compareTo(taskSubConfigReqVO.getDepositAmount()) > 0 ? taskSubConfigReqVO.getDepositAmount() : achieveAmount);
                        }
                    }
                }
                //appResVO.setTaskStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode());


                appResVO.setExpireTime((TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timeZone) - System.currentTimeMillis()) / 1000);
                appResVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
                appResVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
                // 计算总total
                for (APPTaskSubConfigReqVO taskSubConfigReqVO : appResVO.getTaskConfigJson()) {
                    if (taskSubConfigReqVO.getReceiveStatus() != 1) {
                        appResVO.setTotalTaskStatus(0);
                    }
                }
                // 子阶梯排序 按照未领取，未完成，已领取排序 阶梯是否领取奖励 0-未领取 1-已领取 2-已过期 3-未达到领取条件
                // 按照 0  3 1 2
                List<APPTaskSubConfigReqVO> sortList = sortByReceiveStatus(appResVO.getTaskConfigJson());
                appResVO.setTaskConfigJson(sortList);
                dailyResults.add(appResVO);

            }
            // 排序 按照totalStatus 排序，0-未领取，，1-已领取， 再次按照 sort 从小到大排序

            dailyResults = sortByReceiveStatusAndSort(dailyResults);
            responseAppTask.setDailyTask(dailyResults);
            responseAppTask.setDailyTaskEndTime(TimeZoneUtils.getEndOfDayInTimeZone(currentTimeMillis, timeZone));
        }
    }

    /**
     * 按 receiveStatus 排序：0-未领取，3-未达到领取条件，1-已领取，2-已过期
     * 排序 按照totalStatus 排序，0-未领取，，1-已领取， 再次按照 sort 从小到大排序
     *
     * @param dailyResults 子任务列表
     * @return 排序后的列表
     */
    public List<TaskUserConfigAppResVO> sortByReceiveStatusAndSort(List<TaskUserConfigAppResVO> dailyResults) {
        List<Integer> priority = Arrays.asList(0, 3, 1, 2);

        return dailyResults.stream()
                .filter(Objects::nonNull) // 排除 null 对象
                .sorted(Comparator
                        .comparingInt((TaskUserConfigAppResVO o) -> {
                            Integer status = o.getTotalTaskStatus();
                            return priority.indexOf(status != null ? status : 999); // 给 null 一个较大默认值
                        })
                        .thenComparingInt(o -> {
                            Integer sort = o.getSort();
                            return sort != null ? sort : Integer.MAX_VALUE; // 给 null sort 一个较大默认值
                        }))
                .toList();
    }

    /**
     * 按 receiveStatus 排序：0-未领取，3-未达到领取条件，1-已领取，2-已过期
     *
     * @param list 子任务列表
     * @return 排序后的列表
     */
    public List<APPTaskSubConfigReqVO> sortByReceiveStatus(List<APPTaskSubConfigReqVO> list) {
        List<Integer> priority = Arrays.asList(0, 3, 1, 2);

        return list.stream()
                .sorted(Comparator.comparingInt(o -> priority.indexOf(o.getReceiveStatus())))
                .toList();
    }

    /**
     * 计算用户存款转换后的平台币金额
     *
     * @param userDepositAmountVOS 用户存款列表
     * @param mainCurrency         用户主货币
     * @param allFinalRate         站点的最终汇率映射表
     * @return 转换后的平台币金额
     */
    private BigDecimal calculateUserDepositPlat(List<UserDepositAmountVO> userDepositAmountVOS, String mainCurrency, Map<String, BigDecimal> allFinalRate) {
        if (CollectionUtil.isNotEmpty(userDepositAmountVOS)) {
            BigDecimal storeDeposit = userDepositAmountVOS.get(0).getAmount();
            if (storeDeposit == null) {
                storeDeposit = BigDecimal.ZERO;
            }
            BigDecimal rate = allFinalRate.getOrDefault(mainCurrency, BigDecimal.ONE);
            return storeDeposit.divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP)
                    .setScale(2, RoundingMode.DOWN);
        }
        return BigDecimal.ZERO;
    }

    // 处理每周任务,app端展示
    private void handleWeeklyTasks(APPTaskReqVO requestVO, APPTaskResponseVO responseAppTask, UserInfoVO userInfoVO) {
        List<SiteTaskConfigPO> weekTaskConfigPOS = getSiteTaskConfigs(requestVO.getSiteCode(), TaskEnum.WEEK_BET.getTaskType());
        if (CollectionUtil.isNotEmpty(weekTaskConfigPOS)) {
            String timeZone = CurrReqUtils.getTimezone();
            Long timeStart = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timeZone);
            Long timeEnd = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timeZone);

            // 查询用户每周任务完成情况
            List<SiteTaskOrderRecordPO> weekRecordPOS = getSiteTaskOrderRecords(Arrays.asList(requestVO.getUserId()), TaskEnum.WEEK_BET.getTaskType(), timeStart, timeEnd);
            List<TaskUserConfigAppResVO> weekResults = new ArrayList<>();
            // 只返回一个
            ReportUserVenueBetsTopVO venueBetsTopVO = queryReportUserVenueBetsTopVO(requestVO.getSiteCode(), Collections.singletonList(requestVO.getUserId()), timeStart, timeEnd, null);

            for (SiteTaskConfigPO weekConfig : weekTaskConfigPOS) {
                TaskUserConfigAppResVO appResVO = ConvertUtil.entityToModel(weekConfig, TaskUserConfigAppResVO.class);
                if (StringUtils.isNotBlank(weekConfig.getTaskConfigJson())) {
                    appResVO.setTaskConfigJson(JSON.parseArray(weekConfig.getTaskConfigJson(), APPTaskSubConfigReqVO.class));
                }

                // 根据完成情况设置任务状态和达成金额 ，存在说明完成任务
                // 设置每个阶梯是否获取奖励
                List<APPTaskSubConfigReqVO> taskConfigJson = appResVO.getTaskConfigJson();
                // 配置阶梯累计综合
                // 1. 累计奖励
                appResVO.setRewardAmount(calculateTotalRewardAmount(weekConfig.getTaskConfigJson()).setScale(2, RoundingMode.HALF_UP));

                // 检查用户是否完成任务
                List<SiteTaskOrderRecordPO> recordSubPOS = weekRecordPOS.stream().filter(e -> StringUtils.equals(e.getSubTaskType(), weekConfig.getSubTaskType())).toList();
                // 对每个配置的进行
                for (APPTaskSubConfigReqVO taskSubConfigReqVO : taskConfigJson) {
                    if (CollectionUtil.isNotEmpty(recordSubPOS)) {

                        recordSubPOS.stream().filter(e -> ObjectUtil.equals(e.getStep(), taskSubConfigReqVO.getStep())).findFirst().ifPresentOrElse(e -> {
                            taskSubConfigReqVO.setRewardFlag(true);
                            taskSubConfigReqVO.setReceiveStatus(e.getReceiveStatus());
                            taskSubConfigReqVO.setRecordId(e.getId());
                        }, () -> {
                            taskSubConfigReqVO.setRewardFlag(false);
                            taskSubConfigReqVO.setReceiveStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode()); // 或者默认值
                            appResVO.setTotalTaskStatus(0);

                        });
                    } else {
                        taskSubConfigReqVO.setRewardFlag(false);
                        taskSubConfigReqVO.setReceiveStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode()); // 或
                        appResVO.setTotalTaskStatus(0);
                    }
                    // 未完成， 设置状态，设置当前用户状态
                    assert appResVO != null;
                    //appResVO.setTaskStatus(TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode());
                    // 每周投注
                    if (TaskEnum.WEEK_BET.getSubTaskType().equals(weekConfig.getSubTaskType())) {
                        if (venueBetsTopVO.getPlatValidAmount() != null && venueBetsTopVO.getPlatValidAmount().compareTo(weekConfig.getMinBetAmount()) >= 0) {
                            // 处理每日任务
                            //queryAndUpdateProcess(requestVO);
                        }
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {

                            taskSubConfigReqVO.setAchieveAmount(venueBetsTopVO.getPlatValidAmount().compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : venueBetsTopVO.getPlatValidAmount());
                        }

                        // 每周盈利
                    } else if (TaskEnum.WEEK_PROFIT.getSubTaskType().equals(weekConfig.getSubTaskType())) {
                        BigDecimal achieveAmount = venueBetsTopVO.getPlatWinLossAmount() != null ? venueBetsTopVO.getPlatWinLossAmount() : BigDecimal.ZERO;
                        if (achieveAmount.compareTo(BigDecimal.ZERO) < 0) {
                            achieveAmount = BigDecimal.ZERO;
                        }
                        if (achieveAmount.compareTo(weekConfig.getMinBetAmount()) >= 0) {
                            // 处理每日/周任务
                            //queryAndUpdateProcess(requestVO);
                        }
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {

                            taskSubConfigReqVO.setAchieveAmount(achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : achieveAmount);
                        }

                        // 每周负盈利
                    } else if (TaskEnum.WEEK_NEGATIVE.getSubTaskType().equals(weekConfig.getSubTaskType())) {
                        BigDecimal achieveAmount = venueBetsTopVO.getPlatWinLossAmount() != null ? venueBetsTopVO.getPlatWinLossAmount() : BigDecimal.ZERO;
                        if (achieveAmount.compareTo(BigDecimal.ZERO) > 0) {
                            achieveAmount = BigDecimal.ZERO;
                        } else {
                            achieveAmount = achieveAmount.negate();
                        }
                        if (achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0) {
                            // 处理每日/周任务
                            //queryAndUpdateProcess(requestVO);
                        }
                        if (taskSubConfigReqVO.getRewardFlag()) {
                            taskSubConfigReqVO.setAchieveAmount(taskSubConfigReqVO.getMinBetAmount());
                        } else {

                            taskSubConfigReqVO.setAchieveAmount(achieveAmount.compareTo(taskSubConfigReqVO.getMinBetAmount()) >= 0 ? taskSubConfigReqVO.getMinBetAmount() : achieveAmount);
                        }

                    } else if (TaskEnum.WEEK_INVITE_FRIENDS.getSubTaskType().equals(weekConfig.getSubTaskType())) {


                        // 配置阶梯累计奖励金额
                        /*appResVO.setRewardAmount(calculateTotalRewardAmount(weekConfig.getTaskConfigJson()));*/
                        // 最大配置邀请人数
                        //appResVO.setMinBetCount(calculateMaxFriendsAmount(weekConfig.getTaskConfigJson()));
                        // 计算达到最低要求需要的wtc
                        // 查看邀请人邀请的好友记录

                        if (taskSubConfigReqVO.getRewardFlag()) {
                            // 获取奖励，配置全部
                            taskSubConfigReqVO.setAchieveCount(taskSubConfigReqVO.getInviteFriendCount());
                        } else {
                            // 设置实际
                            Integer total = calculateQualifiedInvites(userInfoVO, timeStart, timeEnd, inviteFriendAmount);
                            taskSubConfigReqVO.setAchieveCount(total);
                        }

                    }

                }

                // 只要是未完成
                appResVO.setExpireTime((timeEnd - System.currentTimeMillis()) / 1000);
                /*if (TaskReceiveStatusEnum.NOT_ELIGIBLE.getCode().equals(appResVO.getTotalTaskStatus()) || TaskReceiveStatusEnum.ELIGIBLE.getCode().equals(appResVO.getTotalTaskStatus())) {
                }*/
                // 计算总total
                for (APPTaskSubConfigReqVO taskSubConfigReqVO : appResVO.getTaskConfigJson()) {
                    if (taskSubConfigReqVO.getReceiveStatus() != 1) {
                        appResVO.setTotalTaskStatus(0);
                    }
                }
                appResVO.setCurrencyName(CurrReqUtils.getPlatCurrencyName());
                appResVO.setPlatCurrencySymbol(CurrReqUtils.getPlatCurrencySymbol());
                // 子阶梯排序 按照未领取，未完成，已领取排序 阶梯是否领取奖励 0-未领取 1-已领取 2-已过期 3-未达到领取条件
                // 按照 0  3 1 2
                List<APPTaskSubConfigReqVO> sortList = sortByReceiveStatus(appResVO.getTaskConfigJson());
                appResVO.setTaskConfigJson(sortList);
                weekResults.add(appResVO);
            }
            weekResults = sortByReceiveStatusAndSort(weekResults);
            responseAppTask.setWeeklyTask(weekResults);
            responseAppTask.setWeeklyEndTime(timeEnd);
        }
    }

    /**
     * 计算符合要求的邀请好友数量
     *
     * @param userInfoVO         用户信息
     * @param timeStart          查询开始时间
     * @param timeEnd            查询结束时间
     * @param inviteFriendAmount 邀请好友存款要求
     * @return 符合条件的好友数量 11
     */
    private Integer calculateQualifiedInvites(UserInfoVO userInfoVO, Long timeStart, Long timeEnd, BigDecimal inviteFriendAmount) {
        // 查询邀请记录 客户端查询自己邀请人记录
        SiteUserInviteRecordTaskReqVO inviteRecordTaskReqVO = SiteUserInviteRecordTaskReqVO.builder()
                .userAccount(userInfoVO.getUserAccount())
                .startTime(timeStart)
                .endTime(timeEnd)
                .siteCode(userInfoVO.getSiteCode())
                .build();
        List<SiteUserInviteRecordTaskResVO> inviteRecord = siteUserInviteRecordApi.getInviteRecord(inviteRecordTaskReqVO);

        // 获取汇率信息
        Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(userInfoVO.getSiteCode());

        // 计算符合要求的好友
        List<SiteUserInviteRecordTaskResVO> inviteRecordTarget = computerTargetUser(inviteRecord, inviteFriendAmount, allFinalRate);

        return CollectionUtil.isEmpty(inviteRecordTarget) ? 0 : inviteRecordTarget.size();
    }

    // 提取公共查询逻辑，且把主货币转换为平台币
    private Page<ReportUserVenueBetsTopVO> queryUserBetsTop(String siteCode, List<String> userIds, Long timeStart, long timeEnd, SiteTaskConfigPO taskConfig) {
        ReportUserTopReqVO userTopReqVO = new ReportUserTopReqVO();
        userTopReqVO.setSiteCode(siteCode);
        userTopReqVO.setUserIdList(userIds);
        userTopReqVO.setStartTime(timeStart);
        userTopReqVO.setEndTime(timeEnd);
        if (taskConfig != null) {
            String venCode = taskConfig.getVenueCode();
            if (StringUtils.isNotBlank(venCode)) {  // 判断 venCode 是否有值
                List<String> venueCodeList = Arrays.asList(venCode.split(","));
                userTopReqVO.setVenueCodeList(venueCodeList);
            }
        }
        Page<ReportUserVenueBetsTopVO> resultPage = reportUserVenueFixedWinLoseApi.queryUserBetsTop(userTopReqVO);
        log.debug("任务触发,汇率转换前:{}", JSONObject.toJSONString(resultPage.getRecords()));
        if (CollectionUtil.isNotEmpty(resultPage.getRecords())) {
            Map<String, BigDecimal> allFinalRate = siteCurrencyInfoApi.getAllFinalRate(siteCode);
            for (ReportUserVenueBetsTopVO record : resultPage.getRecords()) {
                BigDecimal rate = allFinalRate.get(record.getCurrency());
                if (rate == null) {
                    log.error("汇率未配置，货币是:{},用户是：{}", record.getCurrency(), record.getUserId());
                    record.setBetAmount(BigDecimal.ZERO);
                    record.setValidAmount(BigDecimal.ZERO);
                    record.setWinLossAmount(BigDecimal.ZERO);
                } else {
                    /*record.setBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                    record.setValidAmount(record.getValidAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                    record.setWinLossAmount(record.getWinLossAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));*/

                    record.setPlatBetAmount(record.getBetAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                    record.setPlatValidAmount(record.getValidAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                    record.setPlatWinLossAmount(record.getWinLossAmount().divide(rate, CommonConstant.business_four, RoundingMode.HALF_UP).setScale(2, RoundingMode.DOWN));
                }


            }

        }
        log.debug("任务触发,汇率转换后:{}", JSONObject.toJSONString(resultPage.getRecords()));
        return resultPage;
    }


    /**
     * 返回多个
     */
    private List<ReportUserVenueBetsTopVO> queryReportUserVenueBetsTopVOList(String siteCode, List<String> userIds, Long timeStart, long timeEnd, SiteTaskConfigPO taskConfig) {

        Page<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOPage = queryUserBetsTop(siteCode, userIds, timeStart, timeEnd, taskConfig);

        return Optional.of(ReportUserVenueBetsTopVOPage).map(Page::getRecords).filter(records -> !records.isEmpty()).orElse(Collections.emptyList());
    }

    /**
     * 返回一个。 客户端使用
     */
    private ReportUserVenueBetsTopVO queryReportUserVenueBetsTopVO(String siteCode, List<String> userIds, Long timeStart, long timeEnd, SiteTaskConfigPO taskConfig) {
        Page<ReportUserVenueBetsTopVO> ReportUserVenueBetsTopVOPage = queryUserBetsTop(siteCode, userIds, timeStart, timeEnd, taskConfig);

        return Optional.of(ReportUserVenueBetsTopVOPage).map(Page::getRecords).filter(records -> !records.isEmpty()).map(records -> records.get(0)).orElseGet(ReportUserVenueBetsTopVO::new);  // 返回默认值
    }

    /**
     * 福利中心领奖，可单个领奖，也可批量领奖
     * 任务领取流程：
     * 2. 查询用户的任务记录，确保有可领取的任务。
     * 3. 更新任务记录的领取状态、领取时间等信息。
     * 4. 更新用户的钱包，发放任务奖励。
     *
     * @param requestVO 用户的任务领取请求对象，包含用户ID、任务ID、站点信息等。
     * @return 返回任务领取结果，包括领取的奖励金额。
     */
    //@Transactional(rollbackFor = Exception.class)
    public ResponseVO<Boolean> receiveTask(TaskReceiveBatchAppReqVO requestVO) {
        boolean lock = false;
        long current = System.currentTimeMillis();
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.TASK_RECEIVE_LOCK_KEY + requestVO.getUserId());
        try {
            log.info("用户的任务领取用户:{}", requestVO.getUserId());
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                // 1. 获取用户最新登录信息 (IP 和设备号)
                UserInfoVO userInfoVO = userInfoApi.getByUserId(requestVO.getUserId());
                // 获取IP与登录设备号码
                UserLoginInfoVO latestLoginInfoByUser = userInfoApi.getLatestLoginInfoByUserId(requestVO.getUserId());
                // 2. 查询
                LambdaUpdateWrapper<SiteTaskOrderRecordPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(SiteTaskOrderRecordPO::getSiteCode, requestVO.getSiteCode()).eq(SiteTaskOrderRecordPO::getUserId, requestVO.getUserId()).eq(StringUtils.isNotBlank(requestVO.getId()), SiteTaskOrderRecordPO::getId, requestVO.getId()).eq(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.ELIGIBLE.getCode());
                List<SiteTaskOrderRecordPO> recordPOs = siteTaskOrderRecordRepository.selectList(lambdaUpdateWrapper);
                if (CollectionUtil.isEmpty(recordPOs)) {
                    return ResponseVO.success(Boolean.FALSE);
                }
                List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = currencyInfoApi.getBySiteCode(
                        userInfoVO.getSiteCode());
                BigDecimal finalRate = currencyInfoRespVOS.stream().filter(e -> e.getCurrencyCode().equals(userInfoVO.getMainCurrency()))
                        .map(SiteCurrencyInfoRespVO::getFinalRate).findFirst().orElse(BigDecimal.ZERO);
                for (SiteTaskOrderRecordPO recordPO : recordPOs) {
                    // 如果已经过期了，则需要更该状态
                    if (recordPO.getReceiveEndTime() < current) {
                        updateTaskOrderRecordExpired(recordPO);
                        continue;
                    }
                    //  获取用户信息，并更新用户钱包
                    boolean flag = updateWallet(recordPO, userInfoVO);
                    // 更新状态
                    if (flag) {
                        updateTaskOrderRecord(recordPO, userInfoVO, latestLoginInfoByUser);
                        sendTypingAmount(recordPO, userInfoVO, finalRate);
                        //  发送mq消息
                        handleSendWinLossMessage(recordPO, userInfoVO);
                    } else {
                        throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
                    }
                }
            }
            return ResponseVO.success(Boolean.TRUE);

        } catch (Exception e) {
            log.error("用户的任务领取发生异常", e);
            return ResponseVO.success(Boolean.FALSE);
        } finally {
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }

    /**
     * 更新任务订单记录的领取状态、时间以及用户和登录信息。
     * <p>
     * 此方法用于更新 `SiteTaskOrderRecordPO` 记录的多个字段，例如领取状态、领取时间、更新时间、代理ID、IP地址和设备号。
     * 在 `latestLoginInfoByUser` 不为 null 时，会更新IP和设备号字段。
     *
     * @param recordPO              `SiteTaskOrderRecordPO` 对象，包含需要更新的记录信息。
     * @param userInfoVO            `UserInfoVO` 对象，包含用户相关的信息，例如超级代理ID，
     * @param latestLoginInfoByUser `UserLoginInfoVO` 对象，包含用户的最新登录信息，
     *                              如IP地址和设备号。如果该参数不为 null，则任务订单记录中的相关字段将会被更新。
     * @throws RuntimeException 如果更新操作失败（即没有任何记录被更新），则抛出 `RuntimeException` 异常。
     */
    public void updateTaskOrderRecord(SiteTaskOrderRecordPO recordPO, UserInfoVO userInfoVO, UserLoginInfoVO latestLoginInfoByUser) {
        //  更新领取状态和时间
        LambdaUpdateWrapper<SiteTaskOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SiteTaskOrderRecordPO::getReceiveTime, System.currentTimeMillis()).set(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.CLAIMED.getCode()).set(SiteTaskOrderRecordPO::getUpdatedTime, System.currentTimeMillis()).set(SiteTaskOrderRecordPO::getSuperAgentId, userInfoVO.getSuperAgentId());
        if (latestLoginInfoByUser != null) {
            updateWrapper.set(SiteTaskOrderRecordPO::getIp, latestLoginInfoByUser.getIp());
            updateWrapper.set(SiteTaskOrderRecordPO::getDeviceNo, latestLoginInfoByUser.getDeviceNo());
        }
        // 获取IP 设备号
        updateWrapper.eq(SiteTaskOrderRecordPO::getId, recordPO.getId());
        // 执行更新操作
        int updateValue = siteTaskOrderRecordRepository.update(null, updateWrapper);
        if (updateValue <= 0) {
            throw new RuntimeException(ResultCode.SYSTEM_ERROR.getDesc());
        }

    }

    /**
     * 更新任务订单记录的状态为过期（EXPIRED）。
     *
     * <p>
     * 此方法用于将指定的任务订单记录的状态设置为“已过期”，
     * 并更新记录的最后更新时间。更新操作基于任务订单记录的唯一标识（ID）。
     * </p>
     *
     * <ul>
     *   <li>领取状态：更新为 {@code TaskReceiveStatusEnum}</li>
     *   <li>更新时间：设置为当前系统时间</li>
     * </ul>
     *
     * @param recordPO `SiteTaskOrderRecordPO` 对象，包含需要更新的任务订单记录信息，
     *                 特别是任务订单的唯一标识（ID）。
     */
    public void updateTaskOrderRecordExpired(SiteTaskOrderRecordPO recordPO) {
        //  更新领取状态和时间
        LambdaUpdateWrapper<SiteTaskOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.EXPIRED.getCode()).set(SiteTaskOrderRecordPO::getUpdatedTime, System.currentTimeMillis()).eq(SiteTaskOrderRecordPO::getId, recordPO.getId());
        ;
        // 执行更新操作
        siteTaskOrderRecordRepository.update(null, updateWrapper);
    }


    /**
     * 任务领取流程：
     * 1. 检查任务配置是否存在。
     * 2. 查询用户的任务记录，确保有可领取的任务。
     * 3. 更新任务记录的领取状态、领取时间等信息。
     * 4. 更新用户的钱包，发放任务奖励。
     *
     * @param requestVO 用户的任务领取请求对象，包含用户ID、任务ID、站点信息等。
     * @return 返回任务领取结果，包括领取的奖励金额。
     */
    //@Transactional(rollbackFor = Exception.class)
    public ResponseVO<TaskReceiveAppResVO> receive(TaskReceiveAppReqVO requestVO) {
        boolean lock = false;
        RLock fairLock = RedisUtil.getFairLock(RedisConstants.TASK_RECEIVE_LOCK_KEY + requestVO.getUserId());
        try {
            log.info("用户的任务领取用户:{}, 领取任务类型:{}", requestVO.getUserId(), requestVO.getSubTaskType());
            lock = fairLock.tryLock(RedisLockConstants.WAIT_TIME, RedisLockConstants.UNLOCK_TIME, TimeUnit.SECONDS);
            if (lock) {
                TaskReceiveAppResVO receiveAppResVO = new TaskReceiveAppResVO();
                // 1. 判断任务配置是否存在
                SiteTaskConfigPO configPO = siteTaskConfigRepository.selectById(requestVO.getId());
                if (configPO == null) {
                    throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                }
                //NOTE 除了新人任务外, 都要传任务奖励ID
                if (!configPO.getTaskType().equals(TaskEnum.NOVICE_WELCOME.getTaskType()) && ObjectUtil.isEmpty(requestVO.getRecordId())) {
                    throw new BaowangDefaultException(ResultCode.MISSING_PARAMETERS);
                }
                String timeZone = requestVO.getTimeZone();
                // 2. 获取用户最新登录信息 (IP 和设备号)
                UserInfoVO userInfoVO = userInfoApi.getByUserId(requestVO.getUserId());
                UserLoginInfoVO latestLoginInfoByUser = userInfoApi.getLatestLoginInfoByUserId(requestVO.getUserId());

                LambdaUpdateWrapper<SiteTaskOrderRecordPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                lambdaUpdateWrapper.eq(SiteTaskOrderRecordPO::getSiteCode, requestVO.getSiteCode())
                        .eq(SiteTaskOrderRecordPO::getUserId, requestVO.getUserId())
                        .eq(SiteTaskOrderRecordPO::getSubTaskType, configPO.getSubTaskType())
                        .eq(SiteTaskOrderRecordPO::getTaskType, configPO.getTaskType())
                        .eq(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.ELIGIBLE.getCode())
                        .eq(ObjectUtil.isNotEmpty(requestVO.getRecordId()), SiteTaskOrderRecordPO::getId, requestVO.getRecordId());

                List<SiteTaskOrderRecordPO> recordPOs = siteTaskOrderRecordRepository.selectList(lambdaUpdateWrapper);
                if (CollectionUtil.isEmpty(recordPOs)) {
                    throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
                }
                List<SiteCurrencyInfoRespVO> currencyInfoRespVOS = currencyInfoApi.getBySiteCode(
                        userInfoVO.getSiteCode());
                BigDecimal finalRate = currencyInfoRespVOS.stream().filter(e -> e.getCurrencyCode().equals(userInfoVO.getMainCurrency()))
                        .map(SiteCurrencyInfoRespVO::getFinalRate).findFirst().orElse(BigDecimal.ZERO);
                for (SiteTaskOrderRecordPO recordPO : recordPOs) {
                    // 如果已经过期了，则需要更改状态
                    if (recordPO.getReceiveEndTime() < System.currentTimeMillis()) {
                        updateTaskOrderRecordExpired(recordPO);
                        // ACTIVITY_NOT_YET_CLAIM_EXPIRED
                        throw new BaowangDefaultException(ResultCode.ACTIVITY_NOT_YET_CLAIM_EXPIRED);
                    }
                    // 4. 更新领取状态和时间
                    updateTaskOrderRecord(recordPO, userInfoVO, latestLoginInfoByUser);
                    // 5. 获取用户信息，并更新用户钱包
                    boolean flag = updateWallet(recordPO, userInfoVO);
                    if (flag) {
                        // 6. 返回奖励金额
                        //receiveAppResVO.setRewardAmount(recordPO.getTaskAmount());
                        // 7. 发送mq消息
                        handleSendWinLossMessage(recordPO, userInfoVO);
                        sendTypingAmount(recordPO, userInfoVO, finalRate);

                    } else {
                        throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
                    }
                }
                BigDecimal totalAmount = recordPOs.stream().map(
                                e -> e.getTaskAmount() == null ? BigDecimal.ZERO : e.getTaskAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                receiveAppResVO.setRewardAmount(totalAmount);
                return ResponseVO.success(ResultCode.RECEIVE_SUCCESS, receiveAppResVO);
            } else {
                throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
            }

        } catch (Exception e) {
            log.error("用户的任务领取发生异常", e);
            throw new BaowangDefaultException(ResultCode.RECEIVE_FAIL_DESCRIPTION);
        } finally {
            if (lock && fairLock.isHeldByCurrentThread()) {
                fairLock.unlock();
            }
        }
    }


    /**
     * 处理并发送会员每日盈亏消息到 Kafka 队列。
     *
     * @param siteActivityOrderRecordPO 包含订单记录信息的对象
     * @param userInfoVO                包含用户信息的对象
     */
    private void handleSendWinLossMessage(SiteTaskOrderRecordPO siteActivityOrderRecordPO, UserInfoVO userInfoVO) {
        UserWinLoseMqVO userWinLoseMqVO = new UserWinLoseMqVO();
        userWinLoseMqVO.setOrderId(siteActivityOrderRecordPO.getOrderNo());
        userWinLoseMqVO.setUserId(siteActivityOrderRecordPO.getUserId());
        //userWinLoseMqVO.setUserAccount(siteActivityOrderRecordPO.getUserAccount());
        userWinLoseMqVO.setAgentId(userInfoVO.getSuperAgentId());
        //userWinLoseMqVO.setAgentAccount(userInfoVO.getSuperAgentAccount());
        userWinLoseMqVO.setDayHourMillis(System.currentTimeMillis());
        // 任务发放的是平台币
        userWinLoseMqVO.setCurrency(siteActivityOrderRecordPO.getCurrencyCode());
        userWinLoseMqVO.setPlatformFlag(true);
        userWinLoseMqVO.setActivityAmount(siteActivityOrderRecordPO.getTaskAmount());
        userWinLoseMqVO.setBizCode(CommonConstant.business_five);
        userWinLoseMqVO.setSiteCode(userInfoVO.getSiteCode());


        KafkaUtil.send(TopicsConstants.USER_WIN_LOSE_CHANNEL, userWinLoseMqVO);
    }


    public void taskAwardExpire() {
        // 查询所有站点
        ResponseVO<List<SiteVO>> responseVO = siteApi.allSiteInfo();
        List<SiteVO> data = responseVO.getData();
        for (SiteVO siteVO : data) {
            long start = System.currentTimeMillis();
            log.info("执行任务获取订单逻辑-开始:siteCode:{}", siteVO.getSiteCode());
            //处理已过期的订单
            String siteCode = siteVO.getSiteCode();
            String timezone = siteVO.getTimezone();
            if (ObjectUtil.isEmpty(timezone)) {
                log.info("执行任务获取订单逻辑-异常:siteCode:{}", siteVO.getSiteCode());
                return;
            }
            long currentTime = System.currentTimeMillis();
            int pageSize = 100;
            int pageNumber = 1;
            boolean hasNext = true;
            LambdaQueryWrapper<SiteTaskOrderRecordPO> wrapper = Wrappers.lambdaQuery(SiteTaskOrderRecordPO.class).select(SiteTaskOrderRecordPO::getId).eq(SiteTaskOrderRecordPO::getSiteCode, siteCode).lt(SiteTaskOrderRecordPO::getReceiveEndTime, currentTime).eq(SiteTaskOrderRecordPO::getDistributionType, TaskDistributionTypeEnum.SELF_EXPIRE_INVALID.getCode()).eq(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.ELIGIBLE.getCode()).orderByDesc(SiteTaskOrderRecordPO::getCreatedTime);
            while (hasNext) {
                Page<SiteTaskOrderRecordPO> iPage = baseMapper.selectPage(new Page<>(pageNumber, pageSize), wrapper);
                List<SiteTaskOrderRecordPO> orderRecordList = iPage.getRecords();
                if (CollectionUtil.isEmpty(orderRecordList)) {
                    break;
                }
                for (SiteTaskOrderRecordPO po : orderRecordList) {
                    LambdaUpdateWrapper<SiteTaskOrderRecordPO> updateWrapper = new LambdaUpdateWrapper<>();
                    updateWrapper.set(SiteTaskOrderRecordPO::getReceiveStatus, TaskReceiveStatusEnum.EXPIRED.getCode()).eq(SiteTaskOrderRecordPO::getId, po.getId());
                    this.baseMapper.update(null, updateWrapper);
                }
                // 判断是否还有下一页
                hasNext = iPage.hasNext();
                pageNumber++;
            }
            log.info("执行任务获取订单逻辑-结束:时间:{},siteCode:{}", System.currentTimeMillis() - start, siteVO.getSiteCode());
        }

    }


    public List<ReportTaskOrderRecordResVO> reportList(Long startTime, Long endTime, String siteCode, String timeZoneDb) {
        List<ReportTaskOrderRecordResVO> sendList = this.baseMapper.reportList(startTime, endTime, siteCode, timeZoneDb);
        List<ReportTaskOrderRecordResVO> receivedList = this.baseMapper.reportReceivedList(startTime, endTime, siteCode, timeZoneDb);
        return mergeRecordList(sendList, receivedList);
    }

    private List<ReportTaskOrderRecordResVO> mergeRecordList(List<ReportTaskOrderRecordResVO> sendList, List<ReportTaskOrderRecordResVO> receivedList) {
        List<ReportTaskOrderRecordResVO> resultList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sendList) && CollectionUtil.isEmpty(receivedList)) {
            return resultList;
        }
        Map<String, ReportTaskOrderRecordResVO> receivedMap = receivedList.stream().collect(Collectors.toMap(ReportTaskOrderRecordResVO::getSubTaskType, Function.identity()));

        Map<String, ReportTaskOrderRecordResVO> sendMap = sendList.stream().collect(Collectors.toMap(ReportTaskOrderRecordResVO::getSubTaskType, Function.identity()));

        Set<String> set = new HashSet<>();
        for (ReportTaskOrderRecordResVO record : sendList) {
            set.add(record.getSubTaskType());
            ReportTaskOrderRecordResVO recordResVO = new ReportTaskOrderRecordResVO();
            recordResVO.setTaskId(record.getTaskId());
            recordResVO.setCurrencyCode(record.getCurrencyCode());
            recordResVO.setSiteCode(record.getSiteCode());
            recordResVO.setTaskType(record.getTaskType());
            recordResVO.setSubTaskType(record.getSubTaskType());

            recordResVO.setSendAmount(record.getSendAmount());

            // 计算未领取， 发送的有多少未领取
            recordResVO.setReceiveNoCount(record.getAllCount() - record.getReceiveCount());
            recordResVO.setReceiveNoAmount(record.getSendAmount().subtract(record.getReceiveAmount()));
            // 补充这段时间领取人数
            ReportTaskOrderRecordResVO received = receivedMap.get(record.getSubTaskType());
            if (received != null) {
                recordResVO.setReceiveCount(received.getReceiveCount());
                recordResVO.setReceiveAmount(received.getReceiveAmount());
            }
            resultList.add(recordResVO);

        }
        for (ReportTaskOrderRecordResVO record : receivedList) {
            if (!set.contains(record.getSubTaskType())) {
                ReportTaskOrderRecordResVO recordResVO = new ReportTaskOrderRecordResVO();
                recordResVO.setTaskId(record.getTaskId());
                recordResVO.setCurrencyCode(record.getCurrencyCode());
                recordResVO.setSiteCode(record.getSiteCode());
                recordResVO.setTaskType(record.getTaskType());
                recordResVO.setSubTaskType(record.getSubTaskType());
                // 有领取，没有发送的
                recordResVO.setReceiveAmount(record.getReceiveAmount());
                recordResVO.setReceiveCount(record.getReceiveCount());
                // 补充这段时间领取人数
                ReportTaskOrderRecordResVO send = sendMap.get(record.getSubTaskType());
                if (send != null) {
                    // 这一步，应该没有可能
                    recordResVO.setSendAmount(send.getSendAmount());
                    // 计算未领取， 发送的有多少未领取
                    recordResVO.setReceiveNoCount(record.getAllCount() - record.getReceiveCount());
                    recordResVO.setReceiveNoAmount(record.getSendAmount().subtract(record.getReceiveAmount()).setScale(2, RoundingMode.HALF_UP));

                }
                resultList.add(recordResVO);
            }
        }
        return resultList;
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
            return !org.springframework.util.StringUtils.hasText(userBenefit) || userBenefit.contains(AgentUserBenefitEnum.TASK_REWARD.getCode().toString());
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
            // 过滤测试账号
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

    public Page<ReportTaskOrderRecordResVO> reportListPage(ReportTaskReportPageCopyVO reportPageVO) {
        Page<ReportTaskOrderRecordResVO> page = new Page<>(reportPageVO.getPageNumber(), reportPageVO.getPageSize());
        return this.baseMapper.reportListPage(page, reportPageVO);
    }

    public ReportTaskOrderRecordResVO reportListPageTotal(ReportTaskReportPageCopyVO reportPageVO) {
        return this.baseMapper.reportListPageTotal(reportPageVO);
    }

    public long getTotalCountReport(ReportTaskReportPageCopyVO reportPageVO) {
        return this.baseMapper.getTotalCountReport(reportPageVO);
    }

    public ReportTaskOrderRecordResVO reportListAll(ReportTaskReportPageCopyVO reportPageVO) {
        return this.baseMapper.reportListAll(reportPageVO);
    }


    public Integer noviceStatus(TaskAppReqVO taskAppReqVO) {
        // 查询新人任务是否开启，如果关闭了，就设置状态为2.
        LambdaQueryWrapper<SiteTaskConfigPO> taskConfigPOLambdaQueryWrapper
                = new LambdaQueryWrapper<>();
        taskConfigPOLambdaQueryWrapper.select(SiteTaskConfigPO::getId);
        taskConfigPOLambdaQueryWrapper.eq(SiteTaskConfigPO::getSiteCode, taskAppReqVO.getSiteCode());
        taskConfigPOLambdaQueryWrapper.eq(SiteTaskConfigPO::getSubTaskType, TaskEnum.NOVICE_WELCOME.getSubTaskType());
        taskConfigPOLambdaQueryWrapper.eq(SiteTaskConfigPO::getStatus, EnableStatusEnum.ENABLE.getCode());
        taskConfigPOLambdaQueryWrapper.last(" limit 1  ");
        Long id = siteTaskConfigRepository.selectObjs(taskConfigPOLambdaQueryWrapper).stream().map(obj -> (Long) obj).findFirst().orElse(null);
        if (id == null) {
            return 2;
        }
        LambdaQueryWrapper<SiteTaskOrderRecordPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteTaskOrderRecordPO::getUserAccount, taskAppReqVO.getUserAccount());
        queryWrapper.eq(SiteTaskOrderRecordPO::getSiteCode, taskAppReqVO.getSiteCode());

        queryWrapper.eq(SiteTaskOrderRecordPO::getTaskType, taskAppReqVO.getTaskType());
        queryWrapper.eq(SiteTaskOrderRecordPO::getSubTaskType, taskAppReqVO.getSubTaskType());
        queryWrapper.last(" limit 1  ");
        SiteTaskOrderRecordPO taskOrderRecordPO = this.baseMapper.selectOne(queryWrapper);
        // 如果没有记录，就设置状态为2.
        if (taskOrderRecordPO == null) {
            return 2;
        }
        return taskOrderRecordPO.getReceiveStatus();
    }
}
