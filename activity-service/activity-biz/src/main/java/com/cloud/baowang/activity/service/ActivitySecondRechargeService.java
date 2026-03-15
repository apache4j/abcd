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
import com.cloud.baowang.activity.po.SiteActivitySecondRechargePO;
import com.cloud.baowang.activity.repositories.ActivitySecondRechargeRepository;
import com.cloud.baowang.activity.service.base.SiteActivityBaseService;
import com.cloud.baowang.activity.service.v2.SiteActivityEventRecordV2Service;
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
import com.cloud.baowang.wallet.api.api.UserCoinApi;
import com.cloud.baowang.wallet.api.api.UserPlatformCoinApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivitySecondRechargeService extends ServiceImpl<ActivitySecondRechargeRepository, SiteActivitySecondRechargePO> {
    private final ActivitySecondRechargeRepository rechargeRepository;
    private final UserCoinApi coinApi;
    private final UserPlatformCoinApi platformCoinApi;
    private final UserInfoApi userInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteActivityBaseService siteActivityBaseService;
    private final SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;

    private final ActivityParticipateApi activityParticipateApi;

    private final SystemDictConfigApi systemDictConfigApi;

    private final ActivityTypingAmountService activityTypingAmountService;

    private final I18nApi i18nApi;

    /**
     * 新增次存活动
     *
     * @param rechargeVO 次存存活动配置
     * @param activityId 活动基础信息id
     * @return true
     */
    @Transactional
    public Boolean saveNextRecharge(ActivitySecondRechargeVO rechargeVO, String activityId) {
        SiteActivitySecondRechargePO po = BeanUtil.copyProperties(rechargeVO, SiteActivitySecondRechargePO.class);
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
    public Boolean updNextRecharge(ActivitySecondRechargeVO rechargeVO, String activityId) {
        SiteActivitySecondRechargePO po = BeanUtil.copyProperties(rechargeVO, SiteActivitySecondRechargePO.class);
        po.setConditionalValue(checkParam(rechargeVO));
        po.setActivityId(Long.valueOf(activityId));
        LambdaUpdateWrapper<SiteActivitySecondRechargePO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivitySecondRechargePO>();
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getDiscountType, po.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getConditionalValue, po.getConditionalValue());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getParticipationMode, po.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getDistributionType, po.getDistributionType());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargePO::getVenueType, po.getVenueType());
        lambdaUpdateWrapper.eq(SiteActivitySecondRechargePO::getActivityId, po.getActivityId());
        this.update(lambdaUpdateWrapper);
        return true;
    }


    public ActivitySecondRechargeRespVO getActivityByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivitySecondRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivitySecondRechargePO::getActivityId, activityId);
        SiteActivitySecondRechargePO po = rechargeRepository.selectOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivitySecondRechargeRespVO respVO = BeanUtil.copyProperties(po, ActivitySecondRechargeRespVO.class);
        if (StringUtils.isEmpty(po.getConditionalValue())) {
            Integer discountType = respVO.getDiscountType();
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                respVO.setPercentageVO(JSON.parseObject(po.getConditionalValue(), RechargePercentageVO.class));
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                respVO.setFixedAmountVOS(JSON.parseArray(po.getConditionalValue(), FixedAmountVO.class));
            }
            respVO.setConditionalValue(po.getConditionalValue());
        } else {
            respVO.setDepositConfigDTOS(JSON.parseArray(po.getConditionalValue(), DepositConfigDTO.class));

        }
        return respVO;
    }

    /**
     * 次存活动详情
     */
    public ActivityDepositDetailVO getActivityDepositDetail(ActivitySecondRechargeRespVO activitySecondRechargeRespVO, String userId,
                                                            ActivityConfigDetailVO activityConfigDetailVO) {
        UserInfoVO userInfo = userInfoApi.getByUserId(userId);
        String siteCode = activitySecondRechargeRespVO.getSiteCode();
        ;
        if (userInfo == null) {
            log.info("当前站点:{},没有获取到当前用户信息:{}", siteCode, userId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        ActivityDepositDetailVO depositDetailVO = ActivityDepositDetailVO.builder()
                .activityAmount(BigDecimal.ZERO)
                .depositAmount(BigDecimal.ZERO)
                .runningWater(BigDecimal.ZERO)
                .depositCurrencyCode(userInfo.getMainCurrency())
                .activityCondition(true)
                .runningWaterCurrencyCode(userInfo.getMainCurrency())
                .build();


        String activityCurrencyCode = ActivityDiscountTypeEnum.PERCENTAGE.getType()
                .equals(activitySecondRechargeRespVO.getDiscountType()) ? userInfo.getMainCurrency() :
                CommonConstant.PLAT_CURRENCY_CODE;

        depositDetailVO.setActivityAmountCurrencyCode(activityCurrencyCode);

        //存金额
        BigDecimal secondDepositAmount = userInfo.getSecondDepositAmount();

        Long secondDepositTime = userInfo.getSecondDepositTime();

        if (ObjectUtil.isEmpty(secondDepositAmount) || ObjectUtil.isEmpty(secondDepositTime) || secondDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取次充活动详情,用户未次存,siteCode:{},userId:{}", siteCode, userId);

            //没有充值可以点击按钮
            depositDetailVO.setActivityCondition(true);
            return depositDetailVO;
        }


        //限时活动判断逻辑
        if (Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), activitySecondRechargeRespVO.getActivityDeadline())) {
            if (!(secondDepositTime >= activitySecondRechargeRespVO.getActivityStartTime())) {
                log.info("限时活动判断逻辑,获取次存活动详情,用户次存时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        if (Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), activitySecondRechargeRespVO.getActivityDeadline())) {
            if (!(secondDepositTime >= activitySecondRechargeRespVO.getActivityStartTime() && secondDepositTime <= activitySecondRechargeRespVO.getActivityEndTime())) {
                log.info("长期活动判断逻辑,获取次充活动详情,用户次存时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        //洗码倍率
        BigDecimal washRatio = activitySecondRechargeRespVO.getWashRatio();
        depositDetailVO.setDepositAmount(secondDepositAmount);

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("获取次充活动详情,次存详情货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}",
                    siteCode, userId, currencyRateMap);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        BigDecimal rate = currencyRateMap.get(userInfo.getMainCurrency());

        if (ObjectUtil.isEmpty(rate) || rate.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取次充活动详情,次存详情货币转换异常.汇率获取异常,siteCode:{},userId:{},MainCurrency:{},rate:{}",
                    siteCode, userId, userInfo.getMainCurrency(), rate);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }

        BigDecimal secondDepositPlatAmount = AmountUtils.divide(secondDepositAmount, rate);

        if (secondDepositPlatAmount == null || secondDepositPlatAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("获取次充活动详情,次存详情货币转换异常.,siteCode:{},userId:{},secondDepositAmount:{},rate:{}",
                    siteCode, userId, secondDepositAmount, rate);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        //次存金额
        // depositDetailVO.setDepositAmount(secondDepositAmount);
        //币种
        // depositDetailVO.setDepositCurrencyCode(userInfo.getMainCurrency());
        if (ObjectUtil.isEmpty(activitySecondRechargeRespVO.getVenueType())) {
            // 按照配置获取奖励金额
            CalculateParam calculateParam = new CalculateParam();
            calculateParam.setSiteCode(userInfo.getSiteCode());
            calculateParam.setSourceCurrencyCode(userInfo.getMainCurrency());
            calculateParam.setSourceAmount(secondDepositAmount);
            calculateParam.setDiscountType(activitySecondRechargeRespVO.getDiscountType());
            calculateParam.setRate(rate);
            calculateParam.setWashRatio(washRatio);
            calculateParam.setConditionalValue(activitySecondRechargeRespVO.getConditionalValue());
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
                    List<DepositConfigDTO> depositConfigDTOS = activitySecondRechargeRespVO.getDepositConfigDTOS();
                    for (VenueValueVO venueValueVO : activityConfigDetailVO.getVenueTypeList()) {
                        // 对应type的游戏大类配置
                        String type = venueValueVO.getCode();
                        DepositConfigDTO depositConfigDTO = depositConfigDTOS.stream().filter(e -> e.getVenueType().equals(type)).findFirst().orElse(null);
                        if (depositConfigDTO == null) {
                            continue;
                        }
                        // 按照配置获取奖励金额
                        CalculateParam calculateParamTemp = new CalculateParam();
                        calculateParamTemp.setSiteCode(userInfo.getSiteCode());
                        calculateParamTemp.setSourceCurrencyCode(userInfo.getMainCurrency());
                        calculateParamTemp.setSourceAmount(secondDepositAmount);
                        calculateParamTemp.setDiscountType(depositConfigDTO.getDiscountType());
                        calculateParamTemp.setRate(rate);
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
                        depositDetailVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                        depositDetailVO.setRunningWater(calculateParamTemp.getRequiredTurnover());
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
        LambdaQueryWrapper<SiteActivitySecondRechargePO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivitySecondRechargePO::getActivityId, siteActivityBasePO.getId());
        SiteActivitySecondRechargePO activitySecondRechargePO = rechargeRepository.selectOne(query);
        //当前活动未生效 无须派发
        if (activitySecondRechargePO == null) {
            log.info("当前站点:{}不存在已生效的次存活动,无须派发", trigger.getSiteCode());
            return false;
        }
        String siteCode = trigger.getSiteCode();
        String userId = trigger.getUserId();
        //会员存款金额大于配置的最小存款金额，满足条件
        Integer participationMode = activitySecondRechargePO.getParticipationMode();
        //人工参与
        boolean manualFlag = false;
        if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
            SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam = new SiteActivityEventRecordQueryParam();
            siteActivityEventRecordQueryParam.setSiteCode(activitySecondRechargePO.getSiteCode());
            siteActivityEventRecordQueryParam.setUserId(trigger.getUserId());
            siteActivityEventRecordQueryParam.setActivityTemplate(ActivityTemplateEnum.SECOND_DEPOSIT.getType());
            //判断是否允许自动发放奖励
            manualFlag = siteActivityEventRecordV2Service.permitSendReward(siteActivityEventRecordQueryParam);
        }
        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(trigger.getSiteCode());
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("次充活动计算,次存详情货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", siteCode, userId, currencyRateMap);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        UserInfoVO byUserId = userInfoApi.getByUserId(userId);
        // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
        String venueType;
        if (ObjectUtil.equals(ActivityParticipationModeEnum.AUTO.getCode(), activitySecondRechargePO.getParticipationMode())
                && ObjectUtil.isNotEmpty(activitySecondRechargePO.getVenueType())) {
            venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(activitySecondRechargePO.getVenueType(), byUserId);

        } else {
            venueType = "";
        }
        CalculateParam calculateParam = new CalculateParam();
        calculateParam.setSiteCode(trigger.getSiteCode());
        calculateParam.setSourceCurrencyCode(trigger.getCurrencyCode());
        calculateParam.setSourceAmount(trigger.getRechargeAmount());
        // 查看个人是否配置了游戏大类
        venueType = activityTypingAmountService.getUserActivityTypingConfig(byUserId);

        // 活动配置的游戏大类（可能为 null 或空）
        String configVenueType = activitySecondRechargePO.getVenueType();
        Boolean configFlag = false;
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
                log.info("次存活动计算,当前活动要求绑定游戏大类，请先进行配置.");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }
        if (configFlag) {
            // 没有配置游戏大类
            calculateParam.setDiscountType(activitySecondRechargePO.getDiscountType());
            calculateParam.setConditionalValue(activitySecondRechargePO.getConditionalValue());
            calculateParam.setWashRatio(siteActivityBasePO.getWashRatio());
        } else {
            // 配置了游戏大类
            // 优惠方式类型，0.百分比，1.固定
            List<DepositConfigDTO> depositConfigDTOS = JSON.parseArray(activitySecondRechargePO.getConditionalValue(), DepositConfigDTO.class);
            if (CollectionUtil.isEmpty(depositConfigDTOS)) {
                log.info("首充活动配置异常,siteCode:{},userId:{},depositConfigDTOS:{}", siteCode, userId, depositConfigDTOS);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
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
        calculateRewardAmount(calculateParam);
        log.info("二次充值,人工曾参与已申请:{}或者自动参与:{}", manualFlag, participationMode);
        //人工参与已申请或自动参与
        if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
            processReward(trigger, activitySecondRechargePO, calculateParam);
        }
        return true;
    }


    /**
     * 按照配置计算奖励金额
     *
     * @param calculateParam 计算参数
     * @return 奖励金额
     */
    private CalculateParam calculateRewardAmount(CalculateParam calculateParam) {
        BigDecimal rechargeAmount = calculateParam.getSourceAmount();
        BigDecimal washRatio = calculateParam.getWashRatio();
        //当前会员充值的平台币
        BigDecimal rechargeTransferPlatAmount = AmountUtils.divide(rechargeAmount, calculateParam.getRate());
        BigDecimal rewardAmount = BigDecimal.ZERO;
        BigDecimal requiredTurnover = BigDecimal.ZERO;
        String rewardCurrencyCode = calculateParam.getSourceCurrencyCode();
        //判断优惠方式类型
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(calculateParam.getDiscountType())) {
            //百分比类型，使用会员主货币计算
            RechargePercentageVO percentageVO = JSON.parseObject(calculateParam.getConditionalValue(), RechargePercentageVO.class);
            BigDecimal minDeposit = percentageVO.getMinDeposit();
            log.info("奖励计算,按照比例派发,最小充值金额:{},当前充值金额:{}", minDeposit, rechargeAmount);
            if (rechargeTransferPlatAmount.compareTo(minDeposit) >= 0) {
                //获取优惠百分比
                BigDecimal discountPct = percentageVO.getDiscountPct();
                // 计算赠送金额 直接拿充值金额*百分比=赠送金额
                BigDecimal bonusPlatAmount = AmountUtils.multiplyPercent(rechargeTransferPlatAmount, discountPct);
                //配置单日最高赠送金额
                BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                // 比较赠送金额与配置的单日最高赠送金额
                if (bonusPlatAmount.compareTo(maxDailyBonus) > 0) {
                    //赠送金额大于配置的单日最高，使用配置的单日最高
                    //平台币转化为主货币
                    rewardAmount = AmountUtils.multiply(maxDailyBonus, calculateParam.getRate());
                } else {
                    //按照法币进行转换获得奖励
                    rewardAmount = AmountUtils.multiplyPercent(rechargeAmount, discountPct);
                }
                //所需求流水=（本金+彩金）*倍数
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                log.info("奖励计算,按照比例派发,奖励金额:{},当前充值金额:{},洗码倍率:{},所需求流水(（本金+彩金）*倍数):{}", rewardAmount, rechargeAmount, washRatio, requiredTurnover);
            }
        } else {
            log.info("奖励计算,按照固定金额派发,当前充值金额:{}", rechargeAmount);
            rewardCurrencyCode = CommonConstant.PLAT_CURRENCY_CODE;
            //固定金额，使用平台币计算
            List<FixedAmountVO> fixedAmountVOS = JSON.parseArray(calculateParam.getConditionalValue(), FixedAmountVO.class);
            //判断当前首充平台币是否满足某个区间范围
            //根据当前会员的充值金额，站点，币种，转平台币判断区间 满足条件里的最大奖励金额
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
            log.info("次存充值平台币金额:{},实际奖励金额:{}", rechargeTransferPlatAmount, rewardAmount);
            // 计算流水 = 充本金的法币 + 彩金平台币(转法币) * 倍数
            //彩金转法币
            BigDecimal activityAmountCurrency = AmountUtils.multiply(rewardAmount, calculateParam.getRate());
            BigDecimal totalAmount = rechargeAmount.add(activityAmountCurrency);
            requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
            log.info("固定金额,使用平台币计算,次存充值平台币金额:{},奖励金额:{},所需流水:{},记录到平台币账户", rechargeTransferPlatAmount, rewardAmount, requiredTurnover);
        }
        //如果是 首充跟次充 需要在减去本金,因为充值每笔充值都有打码量
        calculateParam.setRequiredTurnover(requiredTurnover);
        log.info("奖励计算,奖励金额:{},当前充值金额:{},所需求流水:{}", rewardAmount, rechargeAmount, requiredTurnover);
        if (rewardAmount.compareTo(BigDecimal.ZERO) >= 1) {
            requiredTurnover = requiredTurnover.subtract(rechargeAmount);
            calculateParam.setRequiredTurnover(requiredTurnover);
        }
        calculateParam.setRewardAmount(rewardAmount);
        calculateParam.setRewardCurrencyCode(rewardCurrencyCode);
        calculateParam.setRequiredTurnover(requiredTurnover);
        log.info("奖励计算,奖励金额:{},当前充值金额:{},所需求流水扣除充值金额后:{}", rewardAmount, rechargeAmount, requiredTurnover);
        return calculateParam;
    }


    /**
     * 处理奖励派发相关
     *
     * @param trigger                  首充消息实体
     * @param activitySecondRechargePO 当前首充配置
     * @param calculateParam           奖励金额
     */
    private void processReward(RechargeTriggerVO trigger, SiteActivitySecondRechargePO activitySecondRechargePO, CalculateParam calculateParam) {
        BigDecimal rewardAmount = calculateParam.getRewardAmount();
        String currencyCode = calculateParam.getRewardCurrencyCode();
        if (rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("当前奖励金额:{} 为0 无须派发", rewardAmount);
            return;
        }
        String activityId = String.valueOf(activitySecondRechargePO.getActivityId());
        String siteCode = trigger.getSiteCode();
        String userId = trigger.getUserId();
        UserBaseReqVO userBaseReqVO = new UserBaseReqVO();
        userBaseReqVO.setActivityId(activityId);
        userBaseReqVO.setUserId(userId);
        userBaseReqVO.setSiteCode(siteCode);
        userBaseReqVO.setApplyFlag(trigger.isApplyFlag());
        // 派发的时候，需要指定用户配置的游戏大类
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(trigger, userInfoVO);
        userInfoVO.setUserId(trigger.getUserId());
        userInfoVO.setSiteCode(trigger.getSiteCode());
        String userActivityTypingConfig = activityTypingAmountService.getUserActivityTypingConfig(userInfoVO);
        userBaseReqVO.setVenueType(userActivityTypingConfig);
        //自动触发才校验 手动触发无须校验
        ResponseVO<ToActivityVO> responseVO = activityParticipateApi.checkToActivity(userBaseReqVO);
        if (!responseVO.isOk()) {
            log.info("派发前校验失败:{},无须派发", responseVO.getMessage());
            return;
        }
        ToActivityVO toActivityVO = responseVO.getData();
        if (ResultCode.SUCCESS.getCode() != toActivityVO.getStatus()) {
            log.info("派发前校验错误:{},无须派发", toActivityVO.getMessage());
            return;
        }
        log.info("二次充值,开始派发:{},币种:{},奖励金额:{}", activitySecondRechargePO, currencyCode, rewardAmount);
        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());
        log.info("二次充值,获取过期时间参数:{}", systemDictConfigRespVO);

        List<ActivitySendMqVO> activitySendMqVOList = Lists.newArrayList();
        ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();
        activitySendMqVO.setOrderNo(OrderNoUtils.genOrderNo(trigger.getUserId(), activitySecondRechargePO.getActivityId().toString()));
        activitySendMqVO.setSiteCode(activitySecondRechargePO.getSiteCode());
        activitySendMqVO.setActivityTemplate(ActivityTemplateEnum.SECOND_DEPOSIT.getType());
        activitySendMqVO.setUserId(trigger.getUserId());
        activitySendMqVO.setDistributionType(activitySecondRechargePO.getDistributionType());
        activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
        // 72小时失效
        activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
        activitySendMqVO.setActivityAmount(rewardAmount);
        activitySendMqVO.setRunningWaterMultiple(calculateParam.getWashRatio());
        activitySendMqVO.setRunningWater(calculateParam.getRequiredTurnover());
        activitySendMqVO.setCurrencyCode(currencyCode);
        activitySendMqVO.setActivityId(String.valueOf(activitySecondRechargePO.getActivityId()));
        activitySendMqVO.setParticipationMode(activitySecondRechargePO.getParticipationMode());
        activitySendMqVOList.add(activitySendMqVO);


        ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
        activitySendListMqVO.setList(activitySendMqVOList);
        //发送通知消息
        log.info("二次充值,符合要求,开始发送消息:{}", activitySendMqVO);
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
    }


    /**
     * 校验参数，奖励方式对应条件转字符串
     *
     * @param activity 首存活动配置
     * @return 条件值json字符串
     */
    private String checkParam(ActivitySecondRechargeVO activity) {
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
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            // 校验当前对象的存款最大值是否小于下一个对象的存款最小值
            if (previousMaxDeposit != null && current.getMinDeposit().compareTo(previousMaxDeposit) <= 0) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            previousMaxDeposit = current.getMaxDeposit();
        }
    }
}
