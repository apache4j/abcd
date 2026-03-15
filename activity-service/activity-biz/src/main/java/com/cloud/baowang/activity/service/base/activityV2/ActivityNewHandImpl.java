package com.cloud.baowang.activity.service.base.activityV2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailVO;
import com.cloud.baowang.activity.api.vo.ActivityOrderRecordReqVO;
import com.cloud.baowang.activity.api.vo.ToActivityVO;
import com.cloud.baowang.activity.api.vo.UserBaseReqVO;
import com.cloud.baowang.activity.api.vo.base.ActiveBaseOnOffVO;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.api.vo.v2.newHand.*;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstDepositVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstWithdrawalVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleNegativeProfitVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleSignInVO;
import com.cloud.baowang.activity.param.CalculateParamV2;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityNewHandPO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.service.ActivityGameService;
import com.cloud.baowang.activity.service.ActivityTypingAmountService;
import com.cloud.baowang.activity.service.v2.SiteActivityEventRecordV2Service;
import com.cloud.baowang.activity.service.v2.SiteActivityNewHandService;
import com.cloud.baowang.activity.service.v2.SiteActivityOrderRecordV2Service;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.enums.StatusEnum;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.report.api.api.ReportUserRechargeApi;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.user.api.vo.user.UserInfoPageVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 指定存款日期-活动
 */
@Service
@Slf4j
public class ActivityNewHandImpl implements ActivityBaseV2Interface<ActivityNewHandRespVO> {

    @Resource
    private SiteActivityNewHandService siteActivityNewHandService;
    @Resource
    private SiteApi siteApi;
    @Resource
    private ReportUserRechargeApi reportUserRechargeApi;
    @Lazy
    @Resource
    private SiteActivityBaseV2Service siteActivityBaseV2Service;
    @Resource
    private SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;
    @Resource
    private SiteActivityOrderRecordV2Service siteActivityOrderRecordV2Service;

    @Resource
    private ReportUserWinLoseApi reportUserWinLoseApi;

    @Resource
    private SiteCurrencyInfoApi siteCurrencyInfoApi;
    @Resource
    private ActivityParticipateV2Api activityParticipateV2Api;
    @Resource
    private SystemDictConfigApi systemDictConfigApi;

    @Resource
    private ActivityGameService gameService;

    @Resource
    private ActivityTypingAmountService activityTypingAmountService;

    @Resource
    private UserInfoApi userInfoApi;


    @Override
    public ActivityTemplateV2Enum getActivity() {
        return ActivityTemplateV2Enum.NEW_HAND;
    }

    @Override
    public boolean saveActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {

        activityBaseVO.setId(baseId);
        return siteActivityNewHandService.insert((ActivityNewHandVO) activityBaseVO);
    }

    @Override
    public boolean upActivityDetail(ActivityBaseV2VO activityBaseVO, String baseId) {
        activityBaseVO.setId(baseId);
        return siteActivityNewHandService.updateInfo((ActivityNewHandVO) activityBaseVO);
    }


    /**
     * 指定存款日期活动详情。
     *
     * @return 详情
     */
    @Override
    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {

        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG_V2, siteActivityBasePO.getId()));
        Object value = RedisUtil.getValue(key);
        if (value != null) {
            return JSON.parseObject(value.toString(), ActivityNewHandRespVO.class);
        }
        ActivityNewHandRespVO activityNewHandRespVO = ActivityNewHandRespVO.builder().build();
        SiteActivityNewHandPO activityNewHandPO = siteActivityNewHandService.info(siteActivityBasePO.getId());
        //基础活动和子活动配置放到同一个实体中
        BeanUtils.copyProperties(activityNewHandPO, activityNewHandRespVO);
        BeanUtils.copyProperties(siteActivityBasePO, activityNewHandRespVO);

        activityNewHandRespVO.setFirstDepositConditionVO(JSON.parseObject(activityNewHandPO.getConditionFirstDeposit(), RuleFirstDepositVO.class));
        activityNewHandRespVO.setFirstWithdrawalConditionVO(JSON.parseObject(activityNewHandPO.getConditionFirstWithdrawal(), RuleFirstWithdrawalVO.class));
        activityNewHandRespVO.setSignInConditionVO(JSON.parseObject(activityNewHandPO.getConditionSignIn(), RuleSignInVO.class));
        activityNewHandRespVO.setNegativeProfitConditionVO(JSON.parseObject(activityNewHandPO.getConditionNegativeProfit(), RuleNegativeProfitVO.class));

        RedisUtil.setValue(key, JSON.toJSONString(activityNewHandRespVO), 5L, TimeUnit.MINUTES);

        return activityNewHandRespVO;
    }


    @Override
    public ActivityBaseV2VO getActivityBody(ActivityConfigV2VO activityConfigVO) {
        return activityConfigVO.getActivityNewHandVO();
    }

    @Override
    public void awardExpire(SiteVO siteVO) {
        siteActivityBaseV2Service.expiredActivity(siteVO, getActivity());
        siteActivityOrderRecordV2Service.awardExpire(siteVO, getActivity());
    }


    @Override
    public void awardActive(SiteVO siteVO, String param) {
        String timeZoneId = siteVO.getTimezone();
        String siteCode = siteVO.getSiteCode();

        long todayStartTime = DateUtils.getTodayStartTime(timeZoneId);

        long sevenDaysStartTime = todayStartTime - 1000 * 3600 * 24 * 7;
        long sevenDaysEndTime = sevenDaysStartTime + 1000 * 3600 * 24;

        // 查找活动配置
        ActivityBaseV2RespVO activityBaseV2RespVO = findActivityNewhandConfig(siteCode, siteVO.getTimezone());

        if (activityBaseV2RespVO == null) {
            log.info("新手活动当前站点无开启的配置:{}", siteCode);
        }
        //查询七日前注册的用户，查询七日的负盈利
        UserInfoPageVO userReqVO = new UserInfoPageVO();

        userReqVO.setPageNumber(1);
        userReqVO.setPageSize(500);

        userReqVO.setRegisterTimeStart(sevenDaysStartTime);
        userReqVO.setRegisterTimeEnd(sevenDaysEndTime);

        Page<UserInfoResponseVO> userInfoResponseVOPage = userInfoApi.listPage(userReqVO);

        //NOTE 会员打码记录
        if (CollUtil.isEmpty(userInfoResponseVOPage.getRecords())) {
            log.info("新手活动，无满足条件的会员, 站点:{}, 日期开始时间戳{}", siteCode, sevenDaysStartTime);
            return;
        }
        long totalPage = userInfoResponseVOPage.getPages();
        log.info("新手活动,当前站点:{},总会员数据量:{},总页数:{}开始处理", siteCode, userInfoResponseVOPage.getRecords().size(), totalPage);

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.error("新手活动计算,货币转换异常.,siteCode:{}", siteCode);
            return;
        }

        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());

        for (int pageIndex = 1; pageIndex <= totalPage; pageIndex++) {
            userReqVO.setPageNumber(pageIndex);
            userInfoResponseVOPage = userInfoApi.listPage(userReqVO);
            singleOneProcess(siteCode, userInfoResponseVOPage.getRecords(), timeZoneId, activityBaseV2RespVO, sevenDaysStartTime, sevenDaysEndTime, currencyRateMap, hourTime);
            log.info("[新手活动计算]当前站点:{},第:{}页处理完毕", siteCode, pageIndex);
        }
    }


    /**
     *
     */
    public ActivityBaseV2RespVO findActivityNewhandConfig(String siteCode, String timeZone) {
        List<SiteActivityBaseV2PO> baseV2POS = siteActivityBaseV2Service.selectBySiteAndTemplate(siteCode, ActivityTemplateV2Enum.NEW_HAND.getType());
        if (CollUtil.isNotEmpty(baseV2POS) && baseV2POS.stream().anyMatch(po -> po.getStatus().equals(StatusEnum.OPEN.getCode()))) {
            SiteActivityBaseV2PO baseV2PO = baseV2POS.stream().filter(po -> po.getStatus().equals(StatusEnum.OPEN.getCode())).findFirst().orElse(new SiteActivityBaseV2PO());
            return BeanUtil.copyProperties(baseV2PO, ActivityBaseV2RespVO.class);
        }
        return null;
    }


    /**
     * NOTE 站点单次处理
     * NOTE 新手活动活动配置，活动配置
     */
    public void singleOneProcess(String siteCode,
                                 List<UserInfoResponseVO> userInfoList,
                                 String timeZoneId,
                                 ActivityBaseV2RespVO activityBaseV2RespVO,
                                 long sevenDayAgoStartTime,
                                 long todayStartTime,
                                 Map<String, BigDecimal> currencyRateMap,
                                 Double hourTime) {


        ActivityNewHandRespVO newHandRespVO = (ActivityNewHandRespVO) activityBaseV2RespVO;
        List<ActivitySendMqVO> activitySendMqVOS = Lists.newArrayList();
        String activityTemplate = ActivityTemplateV2Enum.NEW_HAND.getType();
        for (UserInfoResponseVO userInfo : userInfoList) {
            String userId = userInfo.getUserId();
            String orderNo = OrderNoUtils.genOrderNo(userInfo.getUserId(), ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), "4");

            //NOTE 1. 生成唯一用户订单号，查询发放表是否已经发放。
            ActivityOrderRecordReqVO activityOrderRecordReqVO = ActivityOrderRecordReqVO.builder()
                    .orderNo(orderNo).siteCode(userInfo.getSiteCode()).activityTemplate(ActivityTemplateV2Enum.NEW_HAND.getType()).build();
            Long recordsCount = siteActivityOrderRecordV2Service.getActivityOrderRecordCount(activityOrderRecordReqVO);
            if (recordsCount > 0) {
                log.info("新手活动负盈利奖励已经发放， 当前站点:{}, 用户{}", siteCode, userInfo.getUserAccount());
                continue;
            }
            //NOTE 2. 判断用户是否满足条件, 负盈利查询条件对比
            String negativeProfitCurrencyFlag = newHandRespVO.getPlatformOrFiatCurrency();
            String negativeProfitConfigCurrency;
            if ("0".equals(negativeProfitCurrencyFlag)) {
                negativeProfitConfigCurrency = "WTC";
            } else {
                negativeProfitConfigCurrency = userInfo.getMainCurrency();
            }
            RuleNegativeProfitVO negativeProfitConditionVO = newHandRespVO.getNegativeProfitConditionVO();
            ConditionNegativeProfitVO conditionNegativeProfitVO = negativeProfitConditionVO.getConditionVOS().stream()
                    .filter(vo -> vo.getCurrencyCode().equals(negativeProfitConfigCurrency)).findFirst().orElse(null);
            ConditionNegativeProfitRespVO negativeProfitRespVO = BeanUtil.copyProperties(conditionNegativeProfitVO, ConditionNegativeProfitRespVO.class);
            if (negativeProfitRespVO == null) {
                log.info("当前站点:{}没有获取到当前站点配置新手首充信息, 币种:{}", siteCode, userId);
                negativeProfitRespVO = new ConditionNegativeProfitRespVO();
                negativeProfitRespVO.setCurrencyCode(negativeProfitConfigCurrency);
            }
            CalculateParamV2 calculateNegativeProfit = new CalculateParamV2();
            calculateNegativeProfit.setNegativeProfit(BigDecimal.ZERO);

            //TODO 查询第一天到第七天的总盈利
            String dbZone = TimeZoneUtils.getTimeZoneUTC(timeZoneId);

            DailyWinLoseVO build = DailyWinLoseVO.builder().startTime(sevenDayAgoStartTime).endTime(todayStartTime).siteCode(siteCode).timezone(dbZone).userId(userId).build();
            List<DailyWinLoseResponseVO> dailyWinLoseVOS = reportUserWinLoseApi.dailyWinLoseCurrency(build);
            if (CollUtil.isNotEmpty(dailyWinLoseVOS)) {
                BigDecimal reduce = dailyWinLoseVOS.stream().map(DailyWinLoseResponseVO::getBetWinLose).reduce(BigDecimal.ZERO, BigDecimal::add);
                calculateNegativeProfit.setNegativeProfit(reduce);
            }

            CalculateParamV2 calculateParam = new CalculateParamV2();
            calculateParam.setRate(currencyRateMap.get(userInfo.getMainCurrency()));
            calculateParam.setSourceAmount(BigDecimal.ZERO);
            calculateParam.setSourceCurrencyCode(userInfo.getMainCurrency());
            calculateParam.setConditionNegativeProfitVO(conditionNegativeProfitVO);
            calculateParam.setRewardCurrencyCode(negativeProfitConfigCurrency);
            calculateRewardAmount(calculateParam);
            //NOTE 3. 奖励发放。

            Integer distributionType = newHandRespVO.getDistributionType();
            Integer participationMode = newHandRespVO.getParticipationMode();
            //自动参与
            if (Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode) && calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {

                ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();

                activitySendMqVO.setOrderNo(orderNo);
                activitySendMqVO.setSiteCode(siteCode);
                activitySendMqVO.setUserId(userId);
                activitySendMqVO.setCurrencyCode(calculateParam.getRewardCurrencyCode());
                activitySendMqVO.setActivityTemplate(activityTemplate);
                activitySendMqVO.setActivityId(newHandRespVO.getId());
                activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
                activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
                activitySendMqVO.setActivityAmount(calculateParam.getRewardAmount());
                activitySendMqVO.setSiteCode(siteCode);
                //指定盘口
                activitySendMqVO.setHandicapMode(1);

                // 打码倍数 打码流水
                activitySendMqVO.setRunningWaterMultiple(calculateParam.getWashRatio());
                activitySendMqVO.setRunningWater(calculateParam.getRequiredTurnover());
                activitySendMqVO.setDistributionType(distributionType);
                activitySendMqVO.setParticipationMode(participationMode);

                activitySendMqVOS.add(activitySendMqVO);
            }
        }

        if (!CollectionUtils.isEmpty(activitySendMqVOS)) {
            ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
            activitySendListMqVO.setList(activitySendMqVOS);
            log.info("站点:{},指定存款日期开始发放条数:{}", siteCode, activitySendMqVOS.size());
            KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
        }
    }


    public void calculateRewardAmount(CalculateParamV2 calculateParam) {

        BigDecimal rate = calculateParam.getRate();
        calculateParam.setRequiredTurnover(BigDecimal.ZERO);
        calculateParam.setRewardAmount(BigDecimal.ZERO);
        boolean wtcFlag = calculateParam.getRewardCurrencyCode().equalsIgnoreCase("WTC");

        ConditionNegativeProfitVO negativeProfitVO = calculateParam.getConditionNegativeProfitVO();

        BigDecimal rewardPct = negativeProfitVO.getRewardPct();
        BigDecimal negativeProfitAmount = negativeProfitVO.getNegativeProfitAmount();
        BigDecimal negativeProfit = calculateParam.getNegativeProfit();
        BigDecimal rewardMax = negativeProfitVO.getRewardMax();
        BigDecimal washRatio = negativeProfitVO.getWashRatio();

        if (wtcFlag) {
            BigDecimal negativeProfitWTC = negativeProfit.multiply(rate);
            if (negativeProfitWTC.compareTo(negativeProfitAmount) >= 0) {
                BigDecimal rewardAmountConfig = negativeProfitWTC.multiply(rewardPct).divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
                if (rewardAmountConfig.compareTo(rewardMax) > 0) {
                    rewardAmountConfig = rewardMax;
                }
                calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(rate).multiply(washRatio));
                calculateParam.setRewardAmount(rewardAmountConfig);
            }
        } else {
            if (negativeProfit.compareTo(negativeProfitAmount) >= 0) {
                BigDecimal rewardAmountConfig = negativeProfit.multiply(rewardPct).divide(new BigDecimal("100"), 4, RoundingMode.DOWN);
                if (rewardAmountConfig.compareTo(rewardMax) > 0) {
                    rewardAmountConfig = rewardMax;
                }
                calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(washRatio));
                calculateParam.setRewardAmount(rewardAmountConfig);
            }
        }
    }


    /**
     * 活动保存，下一步，各个活动自己校验参数
     */
    @Override
    public void checkSecond(ActivityConfigV2VO activityConfigVO) {
        ActivityNewHandVO activityNewHandVO = activityConfigVO.getActivityNewHandVO();


        RuleFirstDepositVO firstDepositConditionVO = activityNewHandVO.getFirstDepositConditionVO();
        RuleFirstWithdrawalVO firstWithdrawalConditionVO = activityNewHandVO.getFirstWithdrawalConditionVO();
        RuleSignInVO signInConditionVO = activityNewHandVO.getSignInConditionVO();
        RuleNegativeProfitVO negativeProfitConditionVO = activityNewHandVO.getNegativeProfitConditionVO();

        if (ObjectUtil.isEmpty(firstDepositConditionVO) || CollUtil.isEmpty(firstDepositConditionVO.getConditionVOS())
                || ObjectUtil.isEmpty(firstWithdrawalConditionVO) || CollUtil.isEmpty(firstWithdrawalConditionVO.getConditionVOS())
                || ObjectUtil.isEmpty(signInConditionVO) || CollUtil.isEmpty(signInConditionVO.getConditionVOS())
                || ObjectUtil.isEmpty(negativeProfitConditionVO) || CollUtil.isEmpty(negativeProfitConditionVO.getConditionVOS())) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        //TODO 验证数据
        for (ConditionFirstDepositVO vo : firstDepositConditionVO.getConditionVOS()) {

        }
        for (ConditionFirstWithdrawalVO vo : firstWithdrawalConditionVO.getConditionVOS()) {


        }
        for (ConditionSignInVO vo : signInConditionVO.getConditionVOS()) {


        }
        for (ConditionNegativeProfitVO vo : negativeProfitConditionVO.getConditionVOS()) {

        }
    }


    /**
     * 活动删除
     */
    @Override
    public void delete(ActiveBaseOnOffVO vo) {
        siteActivityNewHandService.deleteByActivityId(vo.getId());
    }


    @Override
    public void operateStatus(ActiveBaseOnOffVO vo, List<SiteActivityBaseV2PO> allValidBasePos) {
        if (CollectionUtils.isEmpty(allValidBasePos)) {
            log.info("新手活动,不存在已开启的,可以直接操作");
        } else {
            log.info("新手活动,存在相同配置已开启,此活动:{}无法开启", vo.getId());
            throw new BaowangDefaultException(ResultCode.OPEN_STATUS);
        }
    }


    public ToActivityVO toActivity(ActivityBaseV2RespVO activityBaseRespVO, UserBaseReqVO userBaseReqVO) {

        SiteActivityBaseV2PO siteActivityBaseV2PO = BeanUtil.copyProperties(activityBaseRespVO, SiteActivityBaseV2PO.class);

        ActivityNewHandRespVO respVO = (ActivityNewHandRespVO)getActivityByActivityId(siteActivityBaseV2PO, userBaseReqVO.getSiteCode());

        String activityTemplate = respVO.getActivityTemplate();
        Integer participationMode = respVO.getParticipationMode();
        String userId = userBaseReqVO.getUserId();
        String siteCode = userBaseReqVO.getSiteCode();
        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);

        //NOTE 根据orderNo来查询
        String orderNo = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), "3");

        //是否发放过记录
        Wrapper<SiteActivityOrderRecordV2PO> eq = Wrappers.lambdaQuery(SiteActivityOrderRecordV2PO.class)
                .eq(SiteActivityOrderRecordV2PO::getActivityTemplate, activityTemplate)
                .eq(SiteActivityOrderRecordV2PO::getUserId, userId)
                .eq(SiteActivityOrderRecordV2PO::getOrderNo, orderNo)
                .eq(SiteActivityOrderRecordV2PO::getSiteCode, userBaseReqVO.getSiteCode());

        if (siteActivityOrderRecordV2Service.getBaseMapper().selectCount(eq) > 0) {
            log.info("新手活动  申请参与:{},活动,siteCoe:{}userId:{},被拒绝,重复参与", activityTemplate, userBaseReqVO.getSiteCode(), userId);
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_REPEAT.getCode()).message(ResultCode.ACTIVITY_REPEAT.getMessageCode()).build();
        }
        //
        RuleSignInVO signInConditionVO = respVO.getSignInConditionVO();

        String signInCurrencyFlag = signInConditionVO.getPlatformOrFiatCurrency();

        List<ConditionSignInVO> conditionVOS = signInConditionVO.getConditionVOS();

        String signInConfigCurrency;
        if ("0".equals(signInCurrencyFlag)) {
            signInConfigCurrency = "WTC";
        } else {
            signInConfigCurrency = userInfoVO.getMainCurrency();
        }
        ConditionSignInVO conditionSignInVO = conditionVOS.stream()
                .filter(vo -> vo.getCurrencyCode().equals(signInConfigCurrency)).findFirst().orElse(null);

        ConditionSignInRespVO conditionSignInRespVO = BeanUtil.copyProperties(conditionSignInVO, ConditionSignInRespVO.class);
        if (conditionSignInRespVO == null) {
            log.info("没有获取到当前站点配置新手签到配置信息, 站点{}, 币种:{}", siteCode, userId);
            conditionSignInRespVO = new ConditionSignInRespVO();
            conditionSignInRespVO.setCurrencyCode(signInConfigCurrency);

            return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();

        }

        List<DailyWinLoseResponseVO> dailyWinLoseVOS = new ArrayList<>();


        Long registerTime = userInfoVO.getRegisterTime();

        long startOfDayInTimeZone = TimeZoneUtils.getStartOfDayInTimeZone(registerTime, userBaseReqVO.getTimezone());
        long curTimeInTimeZone = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), userBaseReqVO.getTimezone());

        long registerTime7 = startOfDayInTimeZone + (1000 * 3600 * 24 * 7L);

        if (registerTime7>curTimeInTimeZone){
            registerTime7 = curTimeInTimeZone;
        }

        //TODO 查询第一天到第七天的投注记录
        String dbZone = TimeZoneUtils.getTimeZoneUTC(userBaseReqVO.getTimezone());
        DailyWinLoseVO build = DailyWinLoseVO.builder().startTime(startOfDayInTimeZone).endTime(registerTime7).siteCode(siteCode).userId(userId).timezone(dbZone).build();
        dailyWinLoseVOS = reportUserWinLoseApi.dailyWinLoseCurrency(build);

        Map<String, DailyWinLoseResponseVO> collect = dailyWinLoseVOS.stream().collect(Collectors.toMap(DailyWinLoseResponseVO::getDayStr, vo -> vo));

        List<Integer> signInList = conditionSignInRespVO.getSignInList();
        for (int i = 0; i < 7; i++) {
            long timestamp = startOfDayInTimeZone + (i * 1000 * 3600 * 24);
            if (timestamp <= registerTime7) {
                String formattedDate = Instant.ofEpochSecond(timestamp).atZone(ZoneId.of(userBaseReqVO.getTimezone())).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                if (collect.containsKey(formattedDate)) {
                    BigDecimal validBetAmount = conditionSignInRespVO.getValidBetAmount();
                    DailyWinLoseResponseVO vo = collect.get(formattedDate);
                    if (vo.getValidBetAmount().compareTo(validBetAmount)>=0) {
                        signInList.set(i, 1);
                    }
                }
            }
        }

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.error("新手活动计算,货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", siteCode, userId, currencyRateMap);
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_NOT.getCode()).message(ResultCode.ACTIVITY_NOT.getMessageCode()).build();
        }
        CalculateParamV2 calculateSignIn = new CalculateParamV2();

        calculateSignIn.setRate(currencyRateMap.get(userInfoVO.getMainCurrency()));
        calculateSignIn.setSourceAmount(BigDecimal.ZERO);
        calculateSignIn.setSourceCurrencyCode(userInfoVO.getMainCurrency());
        calculateSignIn.setConditionSignInVO(conditionSignInVO);
        calculateSignIn.setRewardCurrencyCode(signInConfigCurrency);
        calculateSignIn.setValidAmountCount(signInList.stream().filter(integer -> integer==1L).count());
        calculateSignIn.setNewHandType(3);
        siteActivityNewHandService.calculateRewardAmount(calculateSignIn);

        //NOTE 直接发消息
        if (calculateSignIn.getRewardAmount().compareTo(BigDecimal.ZERO)>0){
            siteActivityNewHandService.processReward(userInfoApi.getByUserId(userId), respVO, calculateSignIn);
            return ToActivityVO.builder().status(ResultCode.SUCCESS.getCode()).build();
        }else {
            return ToActivityVO.builder().status(ResultCode.ACTIVITY_DEPOSIT_NOT_SATISFIED.getCode()).message(ResultCode.ACTIVITY_DEPOSIT_NOT_SATISFIED.getMessageCode()).build();
        }
    }

    public ActivityConfigDetailVO getConfigDetail(ActivityBaseV2RespVO activityBase, ActivityConfigDetailVO detailVO, String siteCode, String timezone, String userId) {
        ActivityNewHandRespVO activityNewHandRespVO = (ActivityNewHandRespVO) activityBase;
        List<SiteActivityOrderRecordV2PO> orderRecordV2POS = new ArrayList<>();
        if (userId == null) {
            log.info("当前站点:{}没有获取到当前用户信息", siteCode);
        } else {
            LambdaQueryWrapper<SiteActivityOrderRecordV2PO> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SiteActivityOrderRecordV2PO::getSiteCode, siteCode);
            wrapper.eq(SiteActivityOrderRecordV2PO::getUserId, userId);
            wrapper.eq(SiteActivityOrderRecordV2PO::getActivityId, activityBase.getId());
            wrapper.eq(SiteActivityOrderRecordV2PO::getActivityTemplate, ActivityTemplateV2Enum.NEW_HAND.getType());
            orderRecordV2POS = siteActivityOrderRecordV2Service.getBaseMapper().selectList(wrapper);
        }
        detailVO.setStatus(ResultCode.SUCCESS.getCode());
        detailVO.setActivityCondition(true);
        detailVO.setSiteCode(siteCode);
        siteActivityNewHandService.getActivityNewHandDetail(activityNewHandRespVO, userId, detailVO,orderRecordV2POS);
        return detailVO;
    }

}

