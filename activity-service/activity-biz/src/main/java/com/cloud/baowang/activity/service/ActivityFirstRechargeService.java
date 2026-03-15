package com.cloud.baowang.activity.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.param.CalculateParam;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.po.SiteActivityFirstRechargePO;
import com.cloud.baowang.activity.repositories.ActivityFirstRechargeRepository;
import com.cloud.baowang.activity.service.base.ActivityActionContext;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ActivityFirstRechargeService extends ServiceImpl<ActivityFirstRechargeRepository, SiteActivityFirstRechargePO> {
    private final ActivityFirstRechargeRepository rechargeRepository;
    private final UserInfoApi userInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteActivityEventRecordService siteActivityEventRecordService;
    private final ActivityParticipateApi activityParticipateApi;
    private final SystemDictConfigApi systemDictConfigApi;
    private final ActivityTypingAmountService activityTypingAmountService;

    private final I18nApi i18nApi;

    private final ActivityActionContext activityActionContext;

    public ActivityFirstRechargeService(
            ActivityFirstRechargeRepository rechargeRepository,
            UserInfoApi userInfoApi,
            SiteCurrencyInfoApi siteCurrencyInfoApi,
            SiteActivityEventRecordService siteActivityEventRecordService,
            ActivityParticipateApi activityParticipateApi,
            SystemDictConfigApi systemDictConfigApi,
            ActivityTypingAmountService activityTypingAmountService,
            I18nApi i18nApi,
            @Lazy ActivityActionContext activityActionContext) {
        this.rechargeRepository = rechargeRepository;
        this.userInfoApi = userInfoApi;
        this.siteCurrencyInfoApi = siteCurrencyInfoApi;
        this.siteActivityEventRecordService = siteActivityEventRecordService;
        this.activityParticipateApi = activityParticipateApi;
        this.systemDictConfigApi = systemDictConfigApi;
        this.activityTypingAmountService = activityTypingAmountService;
        this.i18nApi = i18nApi;
        this.activityActionContext = activityActionContext;
    }


    /**
     * 新增首存活动
     *
     * @param rechargeVO 首存活动配置
     * @param activityId 活动基础信息id
     * @return true
     */
    @Transactional
    public Boolean saveFirstRecharge(ActivityFirstRechargeVO rechargeVO, String activityId) {
        SiteActivityFirstRechargePO po = BeanUtil.copyProperties(rechargeVO, SiteActivityFirstRechargePO.class);
        po.setActivityId(Long.valueOf(activityId));
        po.setConditionalValue(checkParam(rechargeVO));
        rechargeRepository.insert(po);
        return true;
    }

    /**
     * 修改首存活动
     *
     * @param rechargeVO 首存活动配置
     * @param activityId 活动基础信息id
     * @return true
     */
    @Transactional
    public Boolean updFirstRecharge(ActivityFirstRechargeVO rechargeVO, String activityId) {
        SiteActivityFirstRechargePO po = BeanUtil.copyProperties(rechargeVO, SiteActivityFirstRechargePO.class);
        po.setConditionalValue(checkParam(rechargeVO));
        po.setActivityId(Long.valueOf(activityId));
        LambdaUpdateWrapper<SiteActivityFirstRechargePO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivityFirstRechargePO>();
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getDiscountType, po.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getConditionalValue, po.getConditionalValue());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getParticipationMode, po.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getDistributionType, po.getDistributionType());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargePO::getVenueType, po.getVenueType());
        lambdaUpdateWrapper.eq(SiteActivityFirstRechargePO::getActivityId, po.getActivityId());
        this.update(lambdaUpdateWrapper);
        return true;
    }


    public ActivityFirstRechargeRespVO getActivityByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityFirstRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargePO::getActivityId, activityId);
        SiteActivityFirstRechargePO po = rechargeRepository.selectOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivityFirstRechargeRespVO respVO = BeanUtil.copyProperties(po, ActivityFirstRechargeRespVO.class);
        if (ObjectUtil.isEmpty(respVO.getVenueType())) {
            Integer discountType = respVO.getDiscountType();
            respVO.setConditionalValue(po.getConditionalValue());
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                respVO.setPercentageVO(JSON.parseObject(po.getConditionalValue(), RechargePercentageVO.class));
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                respVO.setFixedAmountVOS(JSON.parseArray(po.getConditionalValue(), FixedAmountVO.class));
            }
        } else {
            respVO.setDepositConfigDTOS(JSON.parseArray(po.getConditionalValue(), DepositConfigDTO.class));
        }


        respVO.setConditionalValue(po.getConditionalValue());
        return respVO;
    }

    /**
     * 根据当前用户计算奖励信息
     *
     * @return
     */
    public ActivityDepositDetailVO getActivityDepositDetail(ActivityFirstRechargeRespVO firstRechargeRespVO, String userId,
                                                            ActivityConfigDetailVO activityConfigDetailVO) {
        UserInfoVO userInfo = userInfoApi.getByUserId(userId);
        String siteCode = firstRechargeRespVO.getSiteCode();
        if (userInfo == null) {
            log.info("当前站点:{}没有获取到当前用户信息:{}", siteCode, userId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        ActivityDepositDetailVO depositDetailVO = ActivityDepositDetailVO.builder()
                .activityAmount(BigDecimal.ZERO)
                .depositAmount(BigDecimal.ZERO)
                .runningWater(BigDecimal.ZERO)
                .depositCurrencyCode(userInfo.getMainCurrency())
                .activityCondition(true)
                .runningWaterCurrencyCode(userInfo.getMainCurrency())// 流水货币类型就是主货币
                .build();


        String activityCurrencyCode = ActivityDiscountTypeEnum.PERCENTAGE.getType()
                .equals(firstRechargeRespVO.getDiscountType()) ? userInfo.getMainCurrency() :
                CommonConstant.PLAT_CURRENCY_CODE;

        depositDetailVO.setActivityAmountCurrencyCode(activityCurrencyCode);

        //首存金额
        BigDecimal firstDepositAmount = userInfo.getFirstDepositAmount();

        //首存时间
        Long firstDepositTime = userInfo.getFirstDepositTime();

        if (ObjectUtil.isEmpty(firstDepositTime) || ObjectUtil.isEmpty(firstDepositAmount) || firstDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取首充活动详情,用户未首充,siteCode:{},userId:{}", siteCode, userId);
            //没有进充值 = true = 可以点击参与按钮
            depositDetailVO.setActivityCondition(true);
            return depositDetailVO;
        }


        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("获取首充活动详情,次存详情货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}",
                    siteCode, userId, currencyRateMap);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        BigDecimal rate = currencyRateMap.get(userInfo.getMainCurrency());

        if (ObjectUtil.isEmpty(rate) || rate.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取首充活动详情,次存详情货币转换异常.汇率获取异常,siteCode:{},userId:{},MainCurrency:{},rate:{}",
                    siteCode, userId, userInfo.getMainCurrency(), rate);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        //法币转平台币
        BigDecimal firstDepositPlatAmount = AmountUtils.divide(firstDepositAmount, rate);
        if (firstDepositPlatAmount == null || firstDepositPlatAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取首充活动详情,首存详情货币转换异常.,siteCode:{},userId:{},secondDepositAmount:{},rate:{}",
                    siteCode, userId, firstDepositPlatAmount, rate);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }


        //限时活动判断逻辑
        if (Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), firstRechargeRespVO.getActivityDeadline())) {
            if (!(firstDepositTime >= firstRechargeRespVO.getActivityStartTime())) {
                log.info("限时活动判断逻辑,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), firstRechargeRespVO.getActivityDeadline())) {
            if (!(firstDepositTime >= firstRechargeRespVO.getActivityStartTime() && firstDepositTime <= firstRechargeRespVO.getActivityEndTime())) {
                log.info("长期,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }
        //首充金额
        depositDetailVO.setDepositAmount(firstDepositAmount);
        //洗码倍率
        // 判断

        BigDecimal washRatio = firstRechargeRespVO.getWashRatio();

        //币种 存款是主货币
        depositDetailVO.setDepositCurrencyCode(userInfo.getMainCurrency());
        // 首先按照游戏大类，
        // 当没有申请了游戏大类
        if (ObjectUtil.isEmpty(firstRechargeRespVO.getVenueType())) {
            // 按照配置获取奖励金额
            CalculateParam calculateParam = new CalculateParam();
            calculateParam.setSiteCode(userInfo.getSiteCode());
            calculateParam.setSourceCurrencyCode(userInfo.getMainCurrency());
            calculateParam.setSourceAmount(firstDepositAmount);
            calculateParam.setDiscountType(firstRechargeRespVO.getDiscountType());
            calculateParam.setRate(rate);
            calculateParam.setWashRatio(washRatio);
            calculateParam.setConditionalValue(firstRechargeRespVO.getConditionalValue());
            //按照配置计算奖励金额
            calculateParam = calculateRewardAmount(calculateParam);
            depositDetailVO.setActivityAmount(calculateParam.getRewardAmount());
            depositDetailVO.setRunningWater(calculateParam.getRequiredTurnover());
//        depositDetailVO.setActivityAmountCurrencyCode(userInfo.getMainCurrency());
            if (calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                depositDetailVO.setActivityCondition(true);
            }
        } else {
            // 不为空，针对每个游戏大类进行配置
            if (!Objects.isNull(activityConfigDetailVO)) {
                if (activityConfigDetailVO.getVenueTypeList() != null && activityConfigDetailVO.getVenueTypeList().size() > 0) {
                    // 对应游戏大类的配置
                    List<DepositConfigDTO> depositConfigDTOS = firstRechargeRespVO.getDepositConfigDTOS();
                    for (VenueValueVO venueValueVO : activityConfigDetailVO.getVenueTypeList()) {
                        // 对应type的游戏大类配置
                        String type = venueValueVO.getCode();
                        DepositConfigDTO depositConfigDTO = depositConfigDTOS.stream().filter(e -> e.getVenueType().equals(type)).findFirst().orElse(null);
                        if (depositConfigDTO == null) {
                            continue;
                        }
                        // 对应游戏大类的充值金额
                        // 按照配置获取奖励金额
                        CalculateParam calculateParamTemp = new CalculateParam();
                        calculateParamTemp.setSiteCode(userInfo.getSiteCode());
                        calculateParamTemp.setSourceCurrencyCode(userInfo.getMainCurrency());
                        calculateParamTemp.setSourceAmount(firstDepositAmount);
                        // 个性化配置
                        calculateParamTemp.setDiscountType(depositConfigDTO.getDiscountType());
                        calculateParamTemp.setRate(rate);
                        // 个性化配置
                        calculateParamTemp.setWashRatio(depositConfigDTO.getWashRatio());
                        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(calculateParamTemp.getDiscountType())) {
                            calculateParamTemp.setConditionalValue(JSON.toJSONString(depositConfigDTO.getPercentageVO()));
                        } else {
                            calculateParamTemp.setConditionalValue(JSON.toJSONString(depositConfigDTO.getFixedAmountVOS()));
                        }
                        //按照配置计算奖励金额
                        calculateParamTemp = calculateRewardAmount(calculateParamTemp);
                        // 计算后金额
                        venueValueVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                        venueValueVO.setRunningWater(calculateParamTemp.getRequiredTurnover());
                        // 活动彩金货币类型 百分比就是固定主货币，否则就是平台币
                        String activityCurrencyCodeTemp = ActivityDiscountTypeEnum.PERCENTAGE.getType()
                                .equals(depositConfigDTO.getDiscountType()) ? userInfo.getMainCurrency() :
                                CommonConstant.PLAT_CURRENCY_CODE;
                        venueValueVO.setActivityAmountCurrencyCode(activityCurrencyCodeTemp);
                        venueValueVO.setRunningWaterCurrencyCode(userInfo.getMainCurrency());
                        venueValueVO.setActivityRuleI18nCode(depositConfigDTO.getActivityRuleI18nCode());

                        if (calculateParamTemp.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                            depositDetailVO.setActivityCondition(true);
                            venueValueVO.setActivityCondition(true);
                        }
                        depositDetailVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                        depositDetailVO.setRunningWater(calculateParamTemp.getRequiredTurnover());
                    }

                }
            }
        }

        return depositDetailVO;
    }

    /**
     * 校验当前会员充值是否满足首充活动，满足则根据配置派发奖励
     *
     * @param trigger 充值实体
     */
    public boolean validateAndReward(RechargeTriggerVO trigger, SiteActivityBasePO siteActivityBasePO) {

        BigDecimal rechargeAmount = trigger.getRechargeAmount();
        String siteCode = trigger.getSiteCode();
        String userId = trigger.getUserId();
        UserInfoVO byUserId = userInfoApi.getByUserId(userId);
        if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("首次充值金额不符合要求:{}", rechargeAmount);
            return false;
        }
        LambdaQueryWrapper<SiteActivityFirstRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargePO::getActivityId, siteActivityBasePO.getId());
        SiteActivityFirstRechargePO activityFirstRechargePO = rechargeRepository.selectOne(query);
        //当前活动未生效 无须派发
        if (activityFirstRechargePO == null) {
            log.info("首存活动不存在:{},无须派发", siteActivityBasePO.getId());
            return false;
        }
        // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
        String venueType;
        if (ObjectUtil.equals(ActivityParticipationModeEnum.AUTO.getCode(), activityFirstRechargePO.getParticipationMode())
                && ObjectUtil.isNotEmpty(activityFirstRechargePO.getVenueType())) {
            // 返回游戏大类
            venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(activityFirstRechargePO.getVenueType(), byUserId);
        } else {
            venueType = "";
        }
        //会员存款金额大于配置的最小存款金额，满足条件
        Integer participationMode = activityFirstRechargePO.getParticipationMode();
        //人工参与
        boolean manualFlag = false;
        if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
            SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam = new SiteActivityEventRecordQueryParam();
            siteActivityEventRecordQueryParam.setSiteCode(activityFirstRechargePO.getSiteCode());
            siteActivityEventRecordQueryParam.setUserId(trigger.getUserId());
            siteActivityEventRecordQueryParam.setActivityTemplate(ActivityTemplateEnum.FIRST_DEPOSIT.getType());
            //判断是否允许自动发放奖励
            manualFlag = siteActivityEventRecordService.permitSendReward(siteActivityEventRecordQueryParam);
            log.info("是否允许自动发放奖励:{}", manualFlag);
        }

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(trigger.getSiteCode());
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("次充活动计算,次存详情货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", siteCode, userId, currencyRateMap);
            return false;
        }

        CalculateParam calculateParam = new CalculateParam();
        calculateParam.setSiteCode(trigger.getSiteCode());
        calculateParam.setSourceCurrencyCode(trigger.getCurrencyCode());
        calculateParam.setSourceAmount(trigger.getRechargeAmount());
        // 查看个人是否配置了游戏大类
        venueType = activityTypingAmountService.getUserActivityTypingConfig(byUserId);
        // 活动游戏大类配置
        String configVenueType = activityFirstRechargePO.getVenueType();
        Boolean configFlag = false;// ture 走通用配置，false游戏大类配置
        // 先看活动的配置，如果活动配置了游戏大类，在看会员配置，
        // 如果会员配置了游戏大类，但是活动没有配置，就按照活动的通用配置发放奖励
        // 如果活动配置了游戏大类，会员一定要游戏大类，就按照会员配置的游戏大类发放奖励
        // 先看游戏
        // 判断逻辑：
        if (StringUtils.isEmpty(configVenueType)) {
            // 活动没配置游戏大类
            configFlag = true; // 走通用配置
        } else {
            // 活动配置了游戏大类
            if (!StringUtils.isEmpty(venueType)) {
                // 会员也配置了游戏大类
                configFlag = false; // 用会员的游戏大类配置
            } else {
                // 活动配置了，会员没配置，无法发放
                log.info("首存活动计算,活动配置了，会员没配置游戏，无法发放.");
                return false;
            }
        }
        if (configFlag) {
            // 没有配置游戏大类
            calculateParam.setDiscountType(activityFirstRechargePO.getDiscountType());
            calculateParam.setConditionalValue(activityFirstRechargePO.getConditionalValue());
            calculateParam.setWashRatio(siteActivityBasePO.getWashRatio());
        } else {
            // 配置了游戏大类
            // 优惠方式类型，0.百分比，1.固定
            activityFirstRechargePO.getConditionalValue();
            List<DepositConfigDTO> depositConfigDTOS = JSON.parseArray(activityFirstRechargePO.getConditionalValue(), DepositConfigDTO.class);
            if (CollectionUtil.isEmpty(depositConfigDTOS)) {
                log.info("首充活动配置异常,siteCode:{},userId:{},depositConfigDTOS:{}", siteCode, userId, depositConfigDTOS);
                return false;
            }

            String finalVenueType = venueType;
            DepositConfigDTO depositConfigDTO = depositConfigDTOS.stream()
                    .filter(e -> e.getVenueType().equals(finalVenueType))
                    .findFirst()
                    .orElseThrow(() -> {
                        log.info("首充活动配置异常, siteCode: {}, userId: {}, depositConfigDTOS: {}", siteCode, userId, depositConfigDTOS);
                        return new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
                    });
            calculateParam.setDiscountType(depositConfigDTO.getDiscountType());
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(depositConfigDTO.getDiscountType())) {
                calculateParam.setConditionalValue(JSON.toJSONString(depositConfigDTO.getPercentageVO()));
            } else {
                calculateParam.setConditionalValue(JSON.toJSONString(depositConfigDTO.getFixedAmountVOS()));
            }
            calculateParam.setWashRatio(depositConfigDTO.getWashRatio());
        }

        calculateParam.setRate(currencyRateMap.get(trigger.getCurrencyCode()));
        //按照配置计算奖励金额
        calculateParam = calculateRewardAmount(calculateParam);

        log.info("首次充值,人工曾参与已申请:{}或者自动参与:{}", manualFlag, participationMode);
        //人工参与已申请或自动参与
        if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
            processReward(trigger, activityFirstRechargePO, calculateParam);
        }
        return true;
    }

    /**
     * 根据规则计算奖励金额
     *
     * @param calculateParam 计算参数
     * @return
     */
    public CalculateParam calculateRewardAmount(CalculateParam calculateParam) {
        BigDecimal rechargeAmount = calculateParam.getSourceAmount();
        BigDecimal washRatio = calculateParam.getWashRatio();
        //当前会员充值的平台币金额
        BigDecimal rechargeTransferPlatAmount = AmountUtils.divide(rechargeAmount, calculateParam.getRate());
        BigDecimal rewardAmount = BigDecimal.ZERO;
        BigDecimal requiredTurnover = BigDecimal.ZERO;
        String rewardCurrencyCode = calculateParam.getSourceCurrencyCode();
        log.info("计算奖励参数:{},平台币金额:{}", calculateParam, rechargeTransferPlatAmount);
        //判断优惠方式类型
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(calculateParam.getDiscountType())) {
            //百分比类型，使用会员主货币计算
            RechargePercentageVO percentageVO = JSON.parseObject(calculateParam.getConditionalValue(), RechargePercentageVO.class);
            if (percentageVO.getMinDeposit() == null) {
                log.info("首存活动最小充值金额为空,不符合条件");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            if (percentageVO.getDiscountPct() == null) {
                log.info("首存活动获取优惠百分比为空,不符合条件");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            if (percentageVO.getMaxDailyBonus() == null) {
                log.info("首存活动 配置单日最高赠送金额 为空,不符合条件");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            BigDecimal minDeposit = percentageVO.getMinDeposit();
            if (rechargeTransferPlatAmount.compareTo(minDeposit) >= 0) {
                //获取优惠百分比
                BigDecimal discountPct = percentageVO.getDiscountPct();
                // 计算赠送金额 直接拿充值金额*百分比=赠送金额 赠送金额按照法币 但是要和配置的最高赠送金额进行对比
                BigDecimal bonusPlatAmount = AmountUtils.multiplyPercent(rechargeTransferPlatAmount, discountPct);
                //配置单日最高赠送金额
                BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                // 比较赠送金额与配置的单日最高赠送金额
                if (bonusPlatAmount.compareTo(maxDailyBonus) > 0) {//按照平台币进行对比
                    //赠送金额大于配置的单日最高，使用配置的单日最高(平台币)
                    //平台币转化为主货币
                    rewardAmount = AmountUtils.multiply(maxDailyBonus, calculateParam.getRate());
                } else {
                    //按照法币进行转换获得奖励
                    rewardAmount = AmountUtils.multiplyPercent(rechargeAmount, discountPct);
                }
                //所需求流水=（本金+彩金）*倍数
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            }
            log.info("百分比类型,充值平台币金额:{},奖励金额:{},记录到法币账户", rechargeTransferPlatAmount, rewardAmount);
        } else {
            //固定金额，使用平台币计算
            List<FixedAmountVO> fixedAmountVOS = JSON.parseArray(calculateParam.getConditionalValue(), FixedAmountVO.class);
            //判断当前首充平台币是否满足某个区间范围
            //根据当前会员的充值金额，站点，币种，转平台币判断区间
            rewardCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            //按照区间最大值排序 满足条件里的最大奖励金额
            fixedAmountVOS = fixedAmountVOS.stream().sorted(Comparator.comparing(FixedAmountVO::getMaxDeposit)).collect(Collectors.toUnmodifiableList());
            for (FixedAmountVO fixedAmountVO : fixedAmountVOS) {
                //判断一下是否满足某个区间
                BigDecimal minDeposit = fixedAmountVO.getMinDeposit();
                BigDecimal maxDeposit = fixedAmountVO.getMaxDeposit();
                //只要大于最大值 奖励金额取配置金额
                log.debug("平台币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeTransferPlatAmount, minDeposit, maxDeposit, fixedAmountVO.getBonusAmount());
                if (rechargeTransferPlatAmount.compareTo(maxDeposit) >= 0) {
                    rewardAmount = fixedAmountVO.getBonusAmount();
                } else {
                    //需要大于最小值
                    if (rechargeTransferPlatAmount.compareTo(minDeposit) >= 0) {
                        rewardAmount = fixedAmountVO.getBonusAmount();
                    }
                    break;
                }
            }
            //充值金额大于等于 某个区间最大值 获得奖励上限
            log.info("充值平台币金额:{},实际奖励金额:{}", rechargeTransferPlatAmount, rewardAmount);
            // 计算流水 = 充本金的法币 + 彩金平台币(转法币) * 倍数
            //彩金转法币
            BigDecimal activityAmountCurrency = AmountUtils.multiply(rewardAmount, calculateParam.getRate());
            BigDecimal totalAmount = rechargeAmount.add(activityAmountCurrency);
            requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            log.info("固定金额,使用平台币计算,充值平台币金额:{},奖励金额:{},所需流水:{},记录到平台币账户", rechargeTransferPlatAmount, rewardAmount, requiredTurnover);
        }
        //如果是 首充跟次充 需要在减去本金,因为充值每笔充值都有打码量
        calculateParam.setRequiredTurnover(requiredTurnover);
        if (rewardAmount.compareTo(BigDecimal.ZERO) >= 1) {
            requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            calculateParam.setRequiredTurnover(requiredTurnover);
        }
        calculateParam.setRewardAmount(rewardAmount);
        calculateParam.setRewardCurrencyCode(rewardCurrencyCode);
        return calculateParam;
    }


    /**
     * 处理奖励派发相关
     *
     * @param trigger                 首充消息实体
     * @param activityFirstRechargePO 当前首充配置
     * @param calculateParam          奖励金额
     */
    private void processReward(RechargeTriggerVO trigger, SiteActivityFirstRechargePO activityFirstRechargePO, CalculateParam calculateParam) {
        BigDecimal rewardAmount = calculateParam.getRewardAmount();
        String currencyCode = calculateParam.getRewardCurrencyCode();
        if (rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("奖励金额小于等于0,无需派发");
            return;
        }
        String activityId = String.valueOf(activityFirstRechargePO.getActivityId());
        String siteCode = activityFirstRechargePO.getSiteCode();
        String userId = trigger.getUserId();
        UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
        userBaseReqVO.setActivityId(activityId);
        userBaseReqVO.setUserId(userId);
        userBaseReqVO.setSiteCode(trigger.getSiteCode());
        userBaseReqVO.setApplyFlag(trigger.isApplyFlag());
        //自动触发才校验 手动触发无须校验
        //ResponseVO<ToActivityVO> responseVO = activityParticipateApi.checkToActivity(userBaseReqVO);
        // 派发的时候，需要指定用户配置的游戏大类
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(trigger, userInfoVO);
        userInfoVO.setUserId(trigger.getUserId());
        userInfoVO.setSiteCode(trigger.getSiteCode());
        String userActivityTypingConfig = activityTypingAmountService.getUserActivityTypingConfig(userInfoVO);
        userBaseReqVO.setVenueType(userActivityTypingConfig);
        ToActivityVO toActivityVO = activityActionContext.checkToActivity(userBaseReqVO);
        /*if (!responseVO.isOk()) {
            log.info("派发前校验失败:{},无须派发", responseVO.getMessage());
            return;
        }
        ToActivityVO toActivityVO = responseVO.getData();*/
        if (ResultCode.SUCCESS.getCode() != toActivityVO.getStatus()) {
            log.info("派发前校验错误:{},无须派发", toActivityVO.getMessage());
            return;
        }
        log.info("首次充值,开始派发:{},币种:{},奖励金额:{}", activityFirstRechargePO, currencyCode, rewardAmount);
        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());
        log.info("首次充值,获取过期时间参数:{}", systemDictConfigRespVO);

        List<ActivitySendMqVO> activitySendMqVOList = Lists.newArrayList();
        ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
        activitySendMqVO.setOrderNo(OrderNoUtils.genOrderNo(trigger.getUserId(), activityId));
        activitySendMqVO.setSiteCode(activityFirstRechargePO.getSiteCode());
        activitySendMqVO.setActivityTemplate(ActivityTemplateEnum.FIRST_DEPOSIT.getType());
        activitySendMqVO.setUserId(userId);
        activitySendMqVO.setDistributionType(activityFirstRechargePO.getDistributionType());
        activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
        // 72小时失效
        activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
        activitySendMqVO.setActivityAmount(rewardAmount);
        activitySendMqVO.setCurrencyCode(currencyCode);
        activitySendMqVO.setRunningWaterMultiple(calculateParam.getWashRatio());
        activitySendMqVO.setRunningWater(calculateParam.getRequiredTurnover());
        activitySendMqVO.setActivityId(activityId);
        activitySendMqVO.setParticipationMode(activityFirstRechargePO.getParticipationMode());
        log.info("满足条件开始派发金额:{}", activitySendMqVO);
        activitySendMqVOList.add(activitySendMqVO);

        ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
        activitySendListMqVO.setList(activitySendMqVOList);
        //发送通知消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
    }


    /**
     * 校验参数，奖励方式对应条件转字符串
     *
     * @param activity 首存活动配置
     * @return 条件值json字符串
     */
    private String checkParam(ActivityFirstRechargeVO activity) {

        String conditionValue = "";
        // 场馆类型为空
        if (!StringUtils.hasText(activity.getVenueType())) {
            Integer discountType = activity.getDiscountType();
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                //百分比
                if (activity.getPercentageVO() == null) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                conditionValue = JSON.toJSONString(activity.getPercentageVO());
            } else {
                //固定金额
                List<FixedAmountVO> fixedAmountVOS = activity.getFixedAmountVOS();
                validateFixedAmountList(fixedAmountVOS);
                //金额区间校验
                conditionValue = JSON.toJSONString(fixedAmountVOS);
            }
            // 场馆类型不为null
        } else {
            List<DepositConfigDTO> depositConfigDTOS = activity.getDepositConfigDTOS();
            if (CollectionUtil.isEmpty(depositConfigDTOS)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
            List<DepositConfigInsertDTO> insertList = Lists.newArrayList();
            for (DepositConfigDTO depositConfigDTO : depositConfigDTOS) {
                Integer discountType = depositConfigDTO.getDiscountType();
                DepositConfigInsertDTO insertDTO = new DepositConfigInsertDTO();
                if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                    //百分比
                    if (depositConfigDTO.getPercentageVO() == null) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    //conditionValue = JSON.toJSONString(activity.getPercentageVO());
                } else {
                    //固定金额
                    List<FixedAmountVO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                    validateFixedAmountList(fixedAmountVOS);
                    //金额区间校验
                    //conditionValue = JSON.toJSONString(fixedAmountVOS);
                }
                // 设置互动规则
                String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
                depositConfigDTO.setActivityRuleI18nCode(activityRuleI18);
                i18nData.put(activityRuleI18, depositConfigDTO.getActivityRuleI18nCodeList());
                BeanUtils.copyProperties(depositConfigDTO, insertDTO);
                insertList.add(insertDTO);
            }
            // 插入i8
            ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
            if (!i18Bool.isOk() || !i18Bool.getData()) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            conditionValue = JSON.toJSONString(insertList);
        }
        return conditionValue;
    }

    /**
     * 校验固定金额对应参数
     *
     * @param fixedAmountVOS
     */
    private void validateFixedAmountList(List<FixedAmountVO> fixedAmountVOS) {
        if (fixedAmountVOS == null || fixedAmountVOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        BigDecimal previousMaxDeposit = null;
        for (int i = 0; i < fixedAmountVOS.size(); i++) {
            FixedAmountVO current = fixedAmountVOS.get(i);
            // 校验当前对象的存款最大值是否大于存款最小值
            if (current.getMaxDeposit().compareTo(current.getMinDeposit()) <= 0) {
                log.info("当前对象最大存款值小于最小存款值:{}", current);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            // 校验当前对象的存款最大值是否小于下一个对象的存款最小值
            if (previousMaxDeposit != null && current.getMinDeposit().compareTo(previousMaxDeposit) <= 0) {
                log.info("当校验当前对象的存款最大值是否没有小于下一个对象的存款最小值:{}", current);
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            previousMaxDeposit = current.getMaxDeposit();
        }
    }

    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityFirstRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargePO::getSiteCode, siteCode);
        this.baseMapper.delete(query);
    }

    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityFirstRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargePO::getActivityId, activityId);
        this.baseMapper.delete(query);
    }
}
