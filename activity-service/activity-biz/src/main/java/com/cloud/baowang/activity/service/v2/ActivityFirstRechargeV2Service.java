package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDeadLineEnum;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.param.CalculateParamV2;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityFirstRechargeV2PO;
import com.cloud.baowang.activity.repositories.v2.ActivityFirstRechargeV2Repository;
import com.cloud.baowang.activity.service.ActivityTypingAmountService;
import com.cloud.baowang.activity.service.base.activityV2.ActivityActionV2Context;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.RedisConstants;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.config.RedisUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class ActivityFirstRechargeV2Service extends ServiceImpl<ActivityFirstRechargeV2Repository, SiteActivityFirstRechargeV2PO> {
    private final ActivityFirstRechargeV2Repository rechargeV2Repository;
    private final UserInfoApi userInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;
    private final SystemDictConfigApi systemDictConfigApi;
    private final ActivityTypingAmountService activityTypingAmountService;

    private final I18nApi i18nApi;

    private final ActivityActionV2Context activityActionV2Context;

    public ActivityFirstRechargeV2Service(
            ActivityFirstRechargeV2Repository rechargeV2Repository,
            UserInfoApi userInfoApi,
            SiteCurrencyInfoApi siteCurrencyInfoApi,
            SiteActivityEventRecordV2Service siteActivityEventRecordV2Service,
            SystemDictConfigApi systemDictConfigApi,
            ActivityTypingAmountService activityTypingAmountService,
            I18nApi i18nApi,
            @Lazy ActivityActionV2Context activityActionV2Context) {
        this.rechargeV2Repository = rechargeV2Repository;
        this.userInfoApi = userInfoApi;
        this.siteCurrencyInfoApi = siteCurrencyInfoApi;
        this.siteActivityEventRecordV2Service = siteActivityEventRecordV2Service;
        this.systemDictConfigApi = systemDictConfigApi;
        this.activityTypingAmountService = activityTypingAmountService;
        this.i18nApi = i18nApi;
        this.activityActionV2Context = activityActionV2Context;
    }


    /**
     * 新增首存活动
     *
     * @param rechargeVO 首存活动配置
     * @param activityId 活动基础信息id
     * @return true
     */
    @Transactional
    public Boolean saveFirstRecharge(ActivityFirstRechargeV2VO rechargeVO, String activityId) {
        SiteActivityFirstRechargeV2PO po = BeanUtil.copyProperties(rechargeVO, SiteActivityFirstRechargeV2PO.class);
        po.setActivityId(Long.valueOf(activityId));
        po.setConditionalValue(checkParam(rechargeVO));
        po.setPlatformOrFiatCurrency(rechargeVO.getPlatformOrFiatCurrency());
        rechargeV2Repository.insert(po);
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
    public Boolean updFirstRecharge(ActivityFirstRechargeV2VO rechargeVO, String activityId) {
        SiteActivityFirstRechargeV2PO po = BeanUtil.copyProperties(rechargeVO, SiteActivityFirstRechargeV2PO.class);
        po.setConditionalValue(checkParam(rechargeVO));
        po.setActivityId(Long.valueOf(activityId));
        LambdaUpdateWrapper<SiteActivityFirstRechargeV2PO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivityFirstRechargeV2PO>();
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getDiscountType, po.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getConditionalValue, po.getConditionalValue());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getParticipationMode, po.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getDistributionType, po.getDistributionType());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getPlatformOrFiatCurrency, po.getPlatformOrFiatCurrency());

        lambdaUpdateWrapper.set(SiteActivityFirstRechargeV2PO::getVenueType, po.getVenueType());
        lambdaUpdateWrapper.eq(SiteActivityFirstRechargeV2PO::getActivityId, po.getActivityId());

        return this.update(lambdaUpdateWrapper);
    }


    public ActivityBaseV2RespVO getActivityByActivityId(SiteActivityBaseV2PO siteActivityBasePO, String siteCode) {

        String key = RedisConstants.getToSetSiteCodeKeyConstant(siteCode, String.format(RedisConstants.ACTIVITY_CONFIG_V2, siteActivityBasePO.getId()));
        Object value = RedisUtil.getValue(key);
        if (value != null) {
            return JSON.parseObject(value.toString(), ActivityFirstRechargeV2RespVO.class);
        }

        LambdaQueryWrapper<SiteActivityFirstRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargeV2PO::getActivityId, siteActivityBasePO.getId());
        query.eq(SiteActivityFirstRechargeV2PO::getSiteCode, siteCode);
        SiteActivityFirstRechargeV2PO po = rechargeV2Repository.selectOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivityFirstRechargeV2RespVO respVO = BeanUtil.copyProperties(po, ActivityFirstRechargeV2RespVO.class);
        List<DepositConfigV2DTO> depositConfigV2DTOS = JSON.parseArray(po.getConditionalValue(), DepositConfigV2DTO.class);
        if (ObjectUtil.isEmpty(respVO.getVenueType())) {
            Integer discountType = respVO.getDiscountType();
            respVO.setConditionalValue(po.getConditionalValue());

            DepositConfigV2DTO depositConfigV2DTO = depositConfigV2DTOS.get(0);
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                respVO.setPercentageVO(depositConfigV2DTO.getPercentageVO());
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                respVO.setFixedAmountVOS(depositConfigV2DTO.getFixedAmountVOS());
            }
        } else {
            respVO.setDepositConfigDTOS(depositConfigV2DTOS);
        }
        respVO.setConditionalValue(po.getConditionalValue());

        BeanUtils.copyProperties(siteActivityBasePO, respVO);
        RedisUtil.setValue(key, JSON.toJSONString(respVO), 5L, TimeUnit.MINUTES);

        return respVO;
    }

    /**
     * 根据当前用户计算奖励信息
     */
    public ActivityDepositDetailVO getActivityDepositDetail(ActivityFirstRechargeV2RespVO firstRechargeRespVO, String userId,
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

        depositDetailVO.setActivityAmountCurrencyCode(userInfo.getMainCurrency());
        //首存金额
        BigDecimal firstDepositAmount = userInfo.getFirstDepositAmount();
        //首存时间
        Long firstDepositTime = userInfo.getFirstDepositTime();
        boolean hasDeposit = true;
        if (ObjectUtil.isEmpty(firstDepositTime)|| firstDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            firstDepositAmount = BigDecimal.ZERO;
            hasDeposit = false;
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

        //首充金额
        depositDetailVO.setDepositAmount(firstDepositAmount);
        //洗码倍率
        // 判断

        BigDecimal washRatio = firstRechargeRespVO.getWashRatio();


        //币种 存款是主货币
        depositDetailVO.setDepositCurrencyCode(userInfo.getMainCurrency());

        List<DepositConfigV2DTO> depositConfigV2DTOS = JSON.parseArray(firstRechargeRespVO.getConditionalValue(), DepositConfigV2DTO.class);

        if (CollUtil.isEmpty(depositConfigV2DTOS)) {
            log.error("长期,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
            return depositDetailVO;
        }

        // 首先按照游戏大类，
        // 当没有申请了游戏大类
        if (ObjectUtil.isEmpty(firstRechargeRespVO.getVenueType())) {


            DepositConfigV2DTO depositConfigV2DTO = depositConfigV2DTOS.get(0);
            // 按照配置获取奖励金额
            CalculateParamV2 calculateParam = new CalculateParamV2();
            calculateParam.setSiteCode(userInfo.getSiteCode());
            calculateParam.setSourceCurrencyCode(userInfo.getMainCurrency());
            calculateParam.setSourceAmount(firstDepositAmount);
            calculateParam.setWashRatio(washRatio);
            calculateParam.setRate(rate);

            String currencyConfig;
            //活动币种类型（0.平台币，1. 法币）
            if ("1".equals(depositConfigV2DTO.getPlatformOrFiatCurrency())) {
                currencyConfig = userInfo.getMainCurrency();
            } else {
                currencyConfig = "WTC";
            }
            calculateParam.setRewardCurrencyCode(currencyConfig);

            calculateParam.setDiscountType(depositConfigV2DTO.getDiscountType());
            if (depositConfigV2DTO.getDiscountType() == 0) {
                RechargePercentageV2VO rechargePercentageV2VO = depositConfigV2DTO.getPercentageVO().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);
                calculateParam.setConditionalValue(JSON.toJSONString(rechargePercentageV2VO));
            } else {
                FixedAmountV2VO amountV2VO = depositConfigV2DTO.getFixedAmountVOS().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);
                calculateParam.setConditionalValue(JSON.toJSONString(amountV2VO));
            }

            calculateRewardAmount(calculateParam);
            depositDetailVO.setActivityAmount(calculateParam.getRewardAmount());
            //按照配置计算奖励金额
            depositDetailVO.setRunningWater(calculateParam.getRequiredTurnover());
//        depositDetailVO.setActivityAmountCurrencyCode(userInfo.getMainCurrency());
            if (calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                depositDetailVO.setActivityCondition(true);
            }
            depositDetailVO.setActivityAmount(calculateParam.getRewardAmount());
            depositDetailVO.setActivityAmountCurrencyCode(calculateParam.getRewardCurrencyCode());
        } else {
            // 不为空，针对每个游戏大类进行配置
            if (!Objects.isNull(activityConfigDetailVO)) {
                if (activityConfigDetailVO.getVenueTypeList() != null && !activityConfigDetailVO.getVenueTypeList().isEmpty()) {
                    // 对应游戏大类的配置
                    List<DepositConfigV2DTO> depositConfigDTOS = firstRechargeRespVO.getDepositConfigDTOS();
                    for (VenueValueVO venueValueVO : activityConfigDetailVO.getVenueTypeList()) {
                        // 对应type的游戏大类配置
                        String type = venueValueVO.getCode();
                        DepositConfigV2DTO depositConfigDTO = depositConfigDTOS.stream().filter(e -> e.getVenueType().equals(type)).findFirst().orElse(null);
                        if (depositConfigDTO == null) {
                            continue;
                        }

                        String currencyConfig;
                        //活动币种类型（0.平台币，1. 法币）
                        if ("1".equals(depositConfigDTO.getPlatformOrFiatCurrency())) {
                            currencyConfig = userInfo.getMainCurrency();
                        } else {
                            currencyConfig = "WTC";
                        }

                        // 对应游戏大类的充值金额
                        // 按照配置获取奖励金额
                        CalculateParamV2 calculateParamTemp = new CalculateParamV2();


                        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(depositConfigDTO.getDiscountType())) {
                            RechargePercentageV2VO rechargePercentageV2VO = depositConfigDTO.getPercentageVO().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);
                            calculateParamTemp.setConditionalValue(JSON.toJSONString(rechargePercentageV2VO));
                        } else {

                            FixedAmountV2VO amountV2VO = depositConfigDTO.getFixedAmountVOS().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);
                            calculateParamTemp.setConditionalValue(JSON.toJSONString(amountV2VO));
                        }

                        calculateParamTemp.setSiteCode(userInfo.getSiteCode());
                        calculateParamTemp.setSourceCurrencyCode(userInfo.getMainCurrency());
                        calculateParamTemp.setSourceAmount(firstDepositAmount);
                        // 个性化配置
                        calculateParamTemp.setDiscountType(depositConfigDTO.getDiscountType());
                        calculateParamTemp.setRate(rate);
                        // 个性化配置
                        calculateParamTemp.setWashRatio(depositConfigDTO.getWashRatio());
                        calculateParamTemp.setRewardCurrencyCode(currencyConfig);

                        //按照配置计算奖励金额
                        calculateRewardAmount(calculateParamTemp);
                        // 计算后金额
                        venueValueVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                        venueValueVO.setRunningWater(calculateParamTemp.getRequiredTurnover());
                        // 活动彩金货币类型 百分比就是固定主货币，否则就是平台币
                        /*String activityCurrencyCodeTemp = ActivityDiscountTypeEnum.PERCENTAGE.getType()
                                .equals(depositConfigDTO.getDiscountType()) ? userInfo.getMainCurrency() :
                                CommonConstant.PLAT_CURRENCY_CODE;*/

                        venueValueVO.setActivityAmountCurrencyCode(calculateParamTemp.getRewardCurrencyCode());
                        venueValueVO.setRunningWaterCurrencyCode(userInfo.getMainCurrency());
                        venueValueVO.setActivityRuleI18nCode(depositConfigDTO.getActivityRuleI18nCode());

                        if (calculateParamTemp.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                            depositDetailVO.setActivityCondition(true);
                            venueValueVO.setActivityCondition(true);
                        }
                        depositDetailVO.setRunningWater(calculateParamTemp.getRequiredTurnover());
                        depositDetailVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                    }

                }
            }else {
                log.error("首充活动，游戏大类配置失败,siteCode:{},userId:{}", siteCode, userId);
            }

        }

        //限时活动判断逻辑
        if (hasDeposit && Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), firstRechargeRespVO.getActivityDeadline())) {
            if (!(firstDepositTime >= firstRechargeRespVO.getActivityStartTime())) {
                log.info("限时活动判断逻辑,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        if (hasDeposit && Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), firstRechargeRespVO.getActivityDeadline())) {
            if (!(firstDepositTime >= firstRechargeRespVO.getActivityStartTime() && firstDepositTime <= firstRechargeRespVO.getActivityEndTime())) {
                log.info("长期,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        return depositDetailVO;
    }

    /**
     * 校验当前会员充值是否满足首充活动，满足则根据配置派发奖励
     *
     * @param trigger 充值实体
     */
    public void validateAndReward(RechargeTriggerVO trigger, SiteActivityBaseV2PO siteActivityBasePO) {

        BigDecimal rechargeAmount = trigger.getRechargeAmount();
        String siteCode = trigger.getSiteCode();
        String mainCurrency = trigger.getCurrencyCode();
        String userId = trigger.getUserId();
        UserInfoVO byUserId = userInfoApi.getByUserId(userId);
        if (rechargeAmount == null || rechargeAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("首次充值金额不符合要求:{}", rechargeAmount);
            return;
        }
        LambdaQueryWrapper<SiteActivityFirstRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargeV2PO::getActivityId, siteActivityBasePO.getId());
        SiteActivityFirstRechargeV2PO activityFirstRechargePO = rechargeV2Repository.selectOne(query);
        //当前活动未生效 无须派发
        if (activityFirstRechargePO == null) {
            log.info("首存活动不存在:{},无须派发", siteActivityBasePO.getId());
            return;
        }
        // 判断活动是否自动派发，如果是自动派发，则添加游戏大类是电子，如果没有电子，则随机一个游戏大类
        String venueType = "";
        if (ObjectUtil.equals(ActivityParticipationModeEnum.AUTO.getCode(), activityFirstRechargePO.getParticipationMode())
                && ObjectUtil.isNotEmpty(activityFirstRechargePO.getVenueType())) {
            // 返回游戏大类
            venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(activityFirstRechargePO.getVenueType(), byUserId);
        }
        //会员存款金额大于配置的最小存款金额，满足条件
        Integer participationMode = activityFirstRechargePO.getParticipationMode();
        //人工参与
        boolean manualFlag = false;
        if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
            SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam = new SiteActivityEventRecordQueryParam();
            siteActivityEventRecordQueryParam.setSiteCode(activityFirstRechargePO.getSiteCode());
            siteActivityEventRecordQueryParam.setUserId(trigger.getUserId());
            siteActivityEventRecordQueryParam.setActivityTemplate(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType());
            //判断是否允许自动发放奖励
            manualFlag = siteActivityEventRecordV2Service.permitSendReward(siteActivityEventRecordQueryParam);
            log.info("是否允许自动发放奖励:{}", manualFlag);
        }

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(trigger.getSiteCode());
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("次充活动计算,次存详情货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", siteCode, userId, currencyRateMap);
            return;
        }

        CalculateParamV2 calculateParam = new CalculateParamV2();
        calculateParam.setSiteCode(trigger.getSiteCode());
        calculateParam.setSourceCurrencyCode(trigger.getCurrencyCode());
        calculateParam.setSourceAmount(trigger.getRechargeAmount());
        // 查看个人是否配置了游戏大类
        venueType = activityTypingAmountService.getUserActivityTypingConfig(byUserId);
        // 活动游戏大类配置
        String configVenueType = activityFirstRechargePO.getVenueType();
        boolean configFlag = false;// ture 走通用配置，false游戏大类配置
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
                return;
            }
        }
        String configCurrency;

        List<DepositConfigV2DTO> depositConfigDTOS = JSON.parseArray(activityFirstRechargePO.getConditionalValue(), DepositConfigV2DTO.class);
        // 配置了游戏大类
        // 优惠方式类型，0.百分比，1.固定
        if (CollectionUtil.isEmpty(depositConfigDTOS)) {
            log.error("首充活动v2配置异常,siteCode:{},userId:{},depositConfigDTOS:{}", siteCode, userId, depositConfigDTOS);
            return;
        }

        // 没有配置游戏大类
        if (configFlag) {

            //NOTE 如果是非大类游戏的, 只能有一条记录, 记录里如果判断是不是法币
            DepositConfigV2DTO depositConfigV2DTO = depositConfigDTOS.get(0);

            if ("1".equals(depositConfigV2DTO.getPlatformOrFiatCurrency())) {
                configCurrency = mainCurrency;
            } else {
                configCurrency = "WTC";
            }
            calculateParam.setRewardCurrencyCode(configCurrency);
            Integer discountType = depositConfigV2DTO.getDiscountType();

            if (discountType == 0) {
                List<RechargePercentageV2VO> rechargePercentageV2VOS = depositConfigV2DTO.getPercentageVO();
                RechargePercentageV2VO rechargePercentageV2VO = rechargePercentageV2VOS.stream().filter(dto -> configCurrency.equalsIgnoreCase(dto.getCurrency())).findFirst().orElse(null);
                log.info("首充活动v2配置, ,conditionalValue:{}", rechargePercentageV2VOS);
                if (rechargePercentageV2VO == null) {
                    log.error("首充活动v2， 非游戏大类找不到对应币种配置rechargePercentageV2VO, ,siteCode:{},userId:{},币种:{}", siteCode, userId, configCurrency);
                    return;
                }
                calculateParam.setDiscountType(discountType);
                calculateParam.setConditionalValue(JSON.toJSONString(rechargePercentageV2VO));
                calculateParam.setWashRatio(siteActivityBasePO.getWashRatio());
            } else {
                List<FixedAmountV2VO> fixedAmountV2VOS = depositConfigV2DTO.getFixedAmountVOS();
                FixedAmountV2VO fixedAmountV2VO = fixedAmountV2VOS.stream().filter(dto -> configCurrency.equalsIgnoreCase(dto.getCurrency())).findFirst().orElse(null);

                if (fixedAmountV2VO == null) {
                    log.error("首充活动v2， 非游戏大类找不到对应币种配置fixedAmountV2VO, ,siteCode:{},userId:{},币种:{}", siteCode, userId, configCurrency);
                    return;
                }
                calculateParam.setDiscountType(discountType);
                calculateParam.setConditionalValue(JSON.toJSONString(fixedAmountV2VO));
                calculateParam.setWashRatio(siteActivityBasePO.getWashRatio());
            }

        } else {

            String finalVenueType = venueType;
            DepositConfigV2DTO depositConfigDTO = depositConfigDTOS.stream()
                    .filter(e -> e.getVenueType().equals(finalVenueType))
                    .findFirst().orElse(null);

            if (depositConfigDTO == null) {
                log.error("首充活动v2， 游戏大类找不到对应币种配置, ,siteCode:{},userId:{}", siteCode, userId);
                return;
            }

            if ("1".equals(depositConfigDTO.getPlatformOrFiatCurrency())) {
                configCurrency = mainCurrency;
            } else {
                configCurrency = "WTC";
            }
            calculateParam.setRewardCurrencyCode(configCurrency);
            calculateParam.setDiscountType(depositConfigDTO.getDiscountType());

            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(depositConfigDTO.getDiscountType())) {
                List<RechargePercentageV2VO> rechargePercentageV2VOS = depositConfigDTO.getPercentageVO();
                RechargePercentageV2VO percentageV2VO = rechargePercentageV2VOS.stream().filter(dto -> configCurrency.equalsIgnoreCase(dto.getCurrency())).findFirst().orElse(null);
                if (percentageV2VO == null) {
                    log.error("首充活动v2， 游戏大类找不到对应币种配置,百分比,siteCode:{},userId:{},币种:{}", siteCode, userId, mainCurrency);
                    return;
                }
                calculateParam.setConditionalValue(JSON.toJSONString(percentageV2VO));
            } else {
                List<FixedAmountV2VO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                FixedAmountV2VO fixedAmountV2VO = fixedAmountVOS.stream().filter(dto -> configCurrency.equalsIgnoreCase(dto.getCurrency())).findFirst().orElse(null);

                if (fixedAmountV2VO == null) {
                    log.error("首充活动v2， 游戏大类找不到对应币种配置,固定金额,siteCode:{},userId:{},币种:{}", siteCode, userId, mainCurrency);
                    return;
                }
                calculateParam.setConditionalValue(JSON.toJSONString(fixedAmountV2VO));
            }
            calculateParam.setWashRatio(depositConfigDTO.getWashRatio());
        }

        calculateParam.setRate(currencyRateMap.get(trigger.getCurrencyCode()));
        //按照配置计算奖励金额
        calculateRewardAmount(calculateParam);

        //人工参与已申请或自动参与
        if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
            log.info("首次充值,人工曾参与已申请:{}或者自动参与:{}", manualFlag, participationMode);
            processReward(trigger, activityFirstRechargePO, calculateParam);
        }else {
            log.error("首次充值,人工曾参与未申请:{} 且不是自动参与:{}, 账号: {}", manualFlag, participationMode, byUserId.getUserAccount());
        }
    }

    /**
     * 根据规则计算奖励金额
     */
    public void calculateRewardAmount(CalculateParamV2 calculateParam) {
        BigDecimal rechargeAmount = calculateParam.getSourceAmount();
        BigDecimal washRatio = calculateParam.getWashRatio();
        //当前会员充值的平台币金额
        BigDecimal rechargeAmountPlat = AmountUtils.divide(rechargeAmount, calculateParam.getRate());
        BigDecimal rewardAmount = BigDecimal.ZERO;
        BigDecimal rewardAmountPlat = BigDecimal.ZERO;
        BigDecimal requiredTurnover = BigDecimal.ZERO;

        String rewardCurrencyCode = calculateParam.getRewardCurrencyCode();

        log.info("计算奖励参数:{},充值平台币金额:{}", calculateParam, rechargeAmountPlat);

        if (StrUtil.isEmpty(calculateParam.getConditionalValue())) {
            log.error("奖励金额条件配置不存在, 条件配置: {}", calculateParam);
        }

        //判断优惠方式类型
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(calculateParam.getDiscountType())) {
            //百分比类型，使用会员主货币计算
            RechargePercentageV2VO percentageVO = JSON.parseObject(calculateParam.getConditionalValue(), RechargePercentageV2VO.class);
            if (percentageVO == null) {
                log.error("首存活动V2, 百分比类型条件配置不存在, 币种: {}", rewardCurrencyCode);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

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
            if (rewardCurrencyCode.equals("WTC")) {
                if (rechargeAmountPlat.compareTo(minDeposit) >= 0) {
                    //获取优惠百分比
                    BigDecimal discountPct = percentageVO.getDiscountPct();
                    // 计算赠送金额 直接拿充值金额*百分比=赠送金额 赠送金额按照法币 但是要和配置的最高赠送金额进行对比
                    BigDecimal bonusPlatAmount = AmountUtils.multiplyPercent(rechargeAmountPlat, discountPct);
                    //配置单日最高赠送金额
                    BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                    // 比较赠送金额与配置的单日最高赠送金额
                    if (bonusPlatAmount.compareTo(maxDailyBonus) > 0) {//按照平台币进行对比
                        //赠送金额大于配置的单日最高，使用配置的单日最高(平台币)
                        //平台币转化为主货币
                        rewardAmountPlat = maxDailyBonus;
                    } else {
                        //按照法币进行转换获得奖励
                        rewardAmountPlat = bonusPlatAmount;
                    }
                    //所需求流水=（本金+彩金）*倍数
                    rewardAmount = AmountUtils.multiply(rewardAmountPlat, calculateParam.getRate());

                    BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                    requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                }
                log.info("百分比类型,充值平台币金额:{},奖励金额:{},记录到法币账户", rechargeAmountPlat, rewardAmount);

                calculateParam.setRewardAmount(rewardAmountPlat);
            } else {

                if (rechargeAmount.compareTo(minDeposit) >= 0) {
                    //获取优惠百分比
                    BigDecimal discountPct = percentageVO.getDiscountPct();
                    // 计算赠送金额 直接拿充值金额*百分比=赠送金额 赠送金额按照法币 但是要和配置的最高赠送金额进行对比
                    BigDecimal bonusAmount = AmountUtils.multiplyPercent(rechargeAmount, discountPct);
                    //配置单日最高赠送金额
                    BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                    // 比较赠送金额与配置的单日最高赠送金额
                    if (bonusAmount.compareTo(maxDailyBonus) > 0) {//按照平台币进行对比
                        //赠送金额大于配置的单日最高，使用配置的单日最高(平台币)
                        //平台币转化为主货币
                        rewardAmount = maxDailyBonus;
                    } else {
                        //按照法币进行转换获得奖励
                        rewardAmount = bonusAmount;
                    }
                    //所需求流水=（本金+彩金）*倍数
                    BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                    requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                }

                log.info("首充百分比类型,充值平台币金额:{},奖励金额:{},记录到法币账户", rechargeAmountPlat, rewardAmount);

                calculateParam.setRewardAmount(rewardAmount);
            }

        } else {

            FixedAmountV2VO amountV2VO = JSON.parseObject(calculateParam.getConditionalValue(), FixedAmountV2VO.class);

            if (amountV2VO == null) {
                log.error("首存活动V2, 固定金额类型条件配置不存在, 币种: {}", rewardCurrencyCode);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

            //固定金额，使用平台币计算
            //判断当前首充平台币是否满足某个区间范围
            //根据当前会员的充值金额，站点，币种，转平台币判断区间
            //按照区间最大值排序 满足条件里的最大奖励金额

            List<AmountV2VO> amountList = amountV2VO.getAmount().stream().sorted(Comparator.comparing(AmountV2VO::getMaxDeposit)).toList();

            if (rewardCurrencyCode.equals("WTC")) {
                for (AmountV2VO fixedAmountVO : amountList) {
                    //判断一下是否满足某个区间
                    BigDecimal minDeposit = fixedAmountVO.getMinDeposit();
                    BigDecimal maxDeposit = fixedAmountVO.getMaxDeposit();
                    //只要大于最大值 奖励金额取配置金额
                    log.debug("平台币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeAmountPlat, minDeposit, maxDeposit, fixedAmountVO.getBonusAmount());
                    if (rechargeAmountPlat.compareTo(maxDeposit) >= 0) {
                        rewardAmountPlat = fixedAmountVO.getBonusAmount();
                    } else {
                        //需要大于最小值
                        if (rechargeAmountPlat.compareTo(minDeposit) >= 0) {
                            rewardAmountPlat = fixedAmountVO.getBonusAmount();
                        }
                        break;
                    }
                }
                rewardAmount = AmountUtils.multiply(rewardAmountPlat, calculateParam.getRate());
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                log.info("固定金额,使用平台币计算,首存充值平台币金额:{},奖励金额:{},所需流水:{},记录到平台币账户", rechargeAmountPlat, rewardAmount, requiredTurnover);
                calculateParam.setRewardAmount(rewardAmountPlat);
            } else {

                for (AmountV2VO fixedAmountVO : amountList) {
                    //判断一下是否满足某个区间
                    BigDecimal minDeposit = fixedAmountVO.getMinDeposit();
                    BigDecimal maxDeposit = fixedAmountVO.getMaxDeposit();
                    //只要大于最大值 奖励金额取配置金额
                    log.debug("首充平台币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeAmountPlat, minDeposit, maxDeposit, fixedAmountVO.getBonusAmount());
                    if (rechargeAmount.compareTo(maxDeposit) >= 0) {
                        rewardAmount = fixedAmountVO.getBonusAmount();
                    } else {
                        //需要大于最小值
                        if (rechargeAmount.compareTo(minDeposit) >= 0) {
                            rewardAmount = fixedAmountVO.getBonusAmount();
                        }
                        break;
                    }
                }
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                log.info("首充固定金额,使用法币计算,首存充值平台币金额:{},奖励金额:{},所需流水:{},记录到法币账户", rechargeAmountPlat, rewardAmount, requiredTurnover);
                calculateParam.setRewardAmount(rewardAmount);
            }
        }
        //如果是 首充跟次充 需要在减去本金,因为充值每笔充值都有打码量
        if (rewardAmount.compareTo(BigDecimal.ZERO) >= 1) {
            requiredTurnover = requiredTurnover.subtract(rechargeAmount);
        }else {
            requiredTurnover = BigDecimal.ZERO;
        }
        calculateParam.setRequiredTurnover(requiredTurnover);
    }


    /**
     * 处理奖励派发相关
     *
     * @param trigger                 首充消息实体
     * @param activityFirstRechargePO 当前首充配置
     * @param calculateParam          奖励金额
     */
    private void processReward(RechargeTriggerVO trigger, SiteActivityFirstRechargeV2PO activityFirstRechargePO, CalculateParamV2 calculateParam) {
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
        ToActivityVO toActivityVO = activityActionV2Context.checkToActivity(userBaseReqVO);

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
        activitySendMqVO.setActivityTemplate(ActivityTemplateV2Enum.FIRST_DEPOSIT_V2.getType());
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

        activitySendMqVO.setHandicapMode(1);
        log.info("满足条件开始派发金额:{}", activitySendMqVO);
        activitySendMqVOList.add(activitySendMqVO);

        ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
        activitySendListMqVO.setList(activitySendMqVOList);
        //发送通知消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
    }


    /**
     * NOTE 首存活动规则配置校验
     */
    private String checkParam(ActivityFirstRechargeV2VO activity) {

        String conditionValue = "";

        List<DepositConfigV2DTO> depositConfigV2DTOS = new ArrayList<>();
        // 场馆类型为空
        if (!StringUtils.hasText(activity.getVenueType())) {

            DepositConfigV2DTO depositConfigV2DTO = new DepositConfigV2DTO();

            Integer discountType = activity.getDiscountType();


            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                //百分比
                if (activity.getPercentageVO() == null) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                depositConfigV2DTO.setPercentageVO(activity.getPercentageVO());
            } else {
                //固定金额
                List<FixedAmountV2VO> fixedAmountVOS = activity.getFixedAmountVOS();
                validateFixedAmountList(fixedAmountVOS);
                depositConfigV2DTO.setFixedAmountVOS(fixedAmountVOS);
                //金额区间校验
            }

            depositConfigV2DTO.setDiscountType(discountType);
            depositConfigV2DTO.setWashRatio(activity.getWashRatio());
            depositConfigV2DTO.setPlatformOrFiatCurrency(activity.getPlatformOrFiatCurrency());

            depositConfigV2DTOS.add(depositConfigV2DTO);
            conditionValue = JSON.toJSONString(depositConfigV2DTOS);
        } else {
            List<DepositConfigV2DTO> depositConfigDTOS = activity.getDepositConfigDTOS();
            if (CollectionUtil.isEmpty(depositConfigDTOS)) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
            for (DepositConfigV2DTO depositConfigDTO : depositConfigDTOS) {
                Integer discountType = depositConfigDTO.getDiscountType();
                if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                    //百分比
                    if (depositConfigDTO.getPercentageVO() == null) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                } else {
                    //固定金额
                    List<FixedAmountV2VO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                    validateFixedAmountList(fixedAmountVOS);
                }
                // 设置互动规则
                String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
                depositConfigDTO.setActivityRuleI18nCode(activityRuleI18);
                i18nData.put(activityRuleI18, depositConfigDTO.getActivityRuleI18nCodeList());
            }
            // 插入i8
            ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
            if (!i18Bool.isOk() || !i18Bool.getData()) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            conditionValue = JSON.toJSONString(depositConfigDTOS);
        }
        return conditionValue;
    }

    /**
     * 校验固定金额对应参数
     *
     * @param fixedAmountVOS
     */
    private void validateFixedAmountList(List<FixedAmountV2VO> fixedAmountVOS) {
        if (fixedAmountVOS == null || fixedAmountVOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }
        for (FixedAmountV2VO current : fixedAmountVOS) {
            List<AmountV2VO> amountList = current.getAmount();
            BigDecimal previousMaxDeposit = BigDecimal.ZERO;
            for (AmountV2VO amountV2VO : amountList) {
                // 校验当前对象的存款最大值是否大于存款最小值
                if (amountV2VO.getMaxDeposit().compareTo(amountV2VO.getMinDeposit()) <= 0) {
                    log.info("当前对象最大存款值小于最小存款值:{}", current);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                // 校验当前对象的存款最大值是否小于下一个对象的存款最小值
                if (amountV2VO.getMinDeposit().compareTo(previousMaxDeposit) <= 0) {
                    log.info("当校验当前对象的存款最大值是否没有小于下一个对象的存款最小值:{}", current);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                previousMaxDeposit = amountV2VO.getMaxDeposit();
            }
        }
    }

    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityFirstRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargeV2PO::getSiteCode, siteCode);
        this.baseMapper.delete(query);
    }

    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityFirstRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivityFirstRechargeV2PO::getActivityId, activityId);
        this.baseMapper.delete(query);
    }
}
