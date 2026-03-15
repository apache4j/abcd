package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.api.ActivityParticipateApi;
import com.cloud.baowang.activity.api.api.v2.ActivityParticipateV2Api;
import com.cloud.baowang.activity.api.enums.*;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.v2.*;
import com.cloud.baowang.activity.param.CalculateParam;
import com.cloud.baowang.activity.param.CalculateParamV2;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivitySecondRechargeV2PO;
import com.cloud.baowang.activity.repositories.v2.ActivitySecondRechargeV2Repository;
import com.cloud.baowang.activity.service.ActivityTypingAmountService;
import com.cloud.baowang.activity.service.SiteActivityEventRecordService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivitySecondRechargeV2Service extends ServiceImpl<ActivitySecondRechargeV2Repository, SiteActivitySecondRechargeV2PO> {
    private final ActivitySecondRechargeV2Repository rechargeRepository;
    private final UserInfoApi userInfoApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final SiteActivityEventRecordV2Service siteActivityEventRecordV2Service;

    private final ActivityParticipateV2Api activityParticipateV2Api;

    private final SystemDictConfigApi systemDictConfigApi;

    private final ActivityTypingAmountService activityTypingAmountService;

    private final I18nApi i18nApi;

    /**
     * 新增次存活动
     *
     * @param rechargeVO 次存存活动配置
     * @param activityId 活动基础信息id
     */
    @Transactional
    public void saveNextRecharge(ActivitySecondRechargeV2VO rechargeVO, String activityId) {
        SiteActivitySecondRechargeV2PO po = BeanUtil.copyProperties(rechargeVO, SiteActivitySecondRechargeV2PO.class);
        po.setActivityId(Long.valueOf(activityId));
        po.setConditionalValue(checkParam(rechargeVO));
        po.setPlatformOrFiatCurrency(rechargeVO.getPlatformOrFiatCurrency());
        rechargeRepository.insert(po);
    }

    /**
     * 修改首存活动
     *
     * @param rechargeVO 首存活动配置
     * @param activityId 活动基础信息id
     * @return true
     */
    @Transactional
    public Boolean updNextRecharge(ActivitySecondRechargeV2VO rechargeVO, String activityId) {
        SiteActivitySecondRechargeV2PO po = BeanUtil.copyProperties(rechargeVO, SiteActivitySecondRechargeV2PO.class);
        po.setConditionalValue(checkParam(rechargeVO));
        po.setActivityId(Long.valueOf(activityId));
        LambdaUpdateWrapper<SiteActivitySecondRechargeV2PO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getDiscountType, po.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getConditionalValue, po.getConditionalValue());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getParticipationMode, po.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getDistributionType, po.getDistributionType());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getVenueType, po.getVenueType());
        lambdaUpdateWrapper.set(SiteActivitySecondRechargeV2PO::getPlatformOrFiatCurrency, po.getPlatformOrFiatCurrency());

        lambdaUpdateWrapper.eq(SiteActivitySecondRechargeV2PO::getActivityId, po.getActivityId());
        this.update(lambdaUpdateWrapper);
        return true;
    }


    public ActivityBaseV2RespVO getActivityByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivitySecondRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivitySecondRechargeV2PO::getActivityId, activityId);
        SiteActivitySecondRechargeV2PO po = rechargeRepository.selectOne(query);
        if (po == null) {
            throw new BaowangDefaultException(ResultCode.DATA_NOT_EXIST);
        }
        ActivityFirstRechargeV2RespVO respVO = BeanUtil.copyProperties(po, ActivityFirstRechargeV2RespVO.class);
        if (StrUtil.isEmpty(po.getConditionalValue())) {
            Integer discountType = respVO.getDiscountType();
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                respVO.setPercentageVO(JSON.parseArray(po.getConditionalValue(), RechargePercentageV2VO.class));
            }
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(discountType)) {
                respVO.setFixedAmountVOS(JSON.parseArray(po.getConditionalValue(), FixedAmountV2VO.class));
            }
            respVO.setConditionalValue(po.getConditionalValue());
        } else {
            respVO.setDepositConfigDTOS(JSON.parseArray(po.getConditionalValue(), DepositConfigV2DTO.class));

        }
        return respVO;
    }

    /**
     * 次存活动详情
     */
    public ActivityDepositDetailVO getActivityDepositDetail(ActivitySecondRechargeV2RespVO activitySecondRechargeRespVO, String userId,
                                                            ActivityConfigDetailVO activityConfigDetailVO) {
        UserInfoVO userInfo = userInfoApi.getByUserId(userId);
        String siteCode = activitySecondRechargeRespVO.getSiteCode();
        if (userInfo == null) {
            log.info("当前站点:{},没有获取到当前用户信息:{}", siteCode, userId);
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        ActivityDepositDetailVO depositDetailVO = ActivityDepositDetailVO.builder()
                .activityAmount(BigDecimal.ZERO)
                .depositCurrencyCode(userInfo.getMainCurrency())
                .runningWater(BigDecimal.ZERO)
                .depositCurrencyCode(userInfo.getMainCurrency())
                .activityCondition(true)
                .activityAmount(BigDecimal.ZERO)
                .runningWaterCurrencyCode(userInfo.getMainCurrency())
                .build();

        depositDetailVO.setActivityAmountCurrencyCode(userInfo.getMainCurrency());
        //存金额
        BigDecimal secondDepositAmount = userInfo.getSecondDepositAmount();
        //存时间
        Long secondDepositTime = userInfo.getSecondDepositTime();
        boolean hasDeposit = true;
        if (ObjectUtil.isEmpty(secondDepositAmount) || secondDepositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            secondDepositAmount = BigDecimal.ZERO;
            hasDeposit = false;
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

        List<DepositConfigV2DTO> depositConfigV2DTOS = JSON.parseArray(activitySecondRechargeRespVO.getConditionalValue(), DepositConfigV2DTO.class);

        if (CollUtil.isEmpty(depositConfigV2DTOS)) {
            log.error("长期,获取首充活动详情,用户首充时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
            return depositDetailVO;
        }

        if (ObjectUtil.isEmpty(activitySecondRechargeRespVO.getVenueType())) {

            DepositConfigV2DTO depositConfigV2DTO = depositConfigV2DTOS.get(0);

            String currencyConfig;
            //活动币种类型（0.平台币，1. 法币）
            if ("1".equals(depositConfigV2DTO.getPlatformOrFiatCurrency())) {
                currencyConfig = userInfo.getMainCurrency();
            } else {
                currencyConfig = "WTC";
            }

            CalculateParamV2 calculateParam = new CalculateParamV2();
            if (depositConfigV2DTO.getDiscountType() == 0) {

                RechargePercentageV2VO rechargePercentageV2VO = depositConfigV2DTO.getPercentageVO().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);

                calculateParam.setConditionalValue(JSON.toJSONString(rechargePercentageV2VO));
            } else {
                FixedAmountV2VO amountV2VO = depositConfigV2DTO.getFixedAmountVOS().stream().filter(vo -> currencyConfig.equals(vo.getCurrency())).findFirst().orElse(null);

                calculateParam.setConditionalValue(JSON.toJSONString(amountV2VO));
            }

            // 按照配置获取奖励金额
            calculateParam.setDiscountType(depositConfigV2DTO.getDiscountType());
            calculateParam.setSiteCode(userInfo.getSiteCode());
            calculateParam.setSourceCurrencyCode(userInfo.getMainCurrency());
            calculateParam.setSourceAmount(secondDepositAmount);
            calculateParam.setRate(rate);
            calculateParam.setWashRatio(washRatio);
            //NOTE 直接指定币种
            calculateParam.setRewardCurrencyCode(currencyConfig);//按照配置计算奖励金额
            calculateRewardAmount(calculateParam);
            depositDetailVO.setActivityAmount(calculateParam.getRewardAmount());
            depositDetailVO.setActivityAmountCurrencyCode(calculateParam.getRewardCurrencyCode());
            depositDetailVO.setDepositCurrencyCode(userInfo.getMainCurrency());
            depositDetailVO.setRunningWater(calculateParam.getRequiredTurnover());
//        depositDetailVO.setActivityAmountCurrencyCode(userInfo.getMainCurrency());
            if (calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
                depositDetailVO.setActivityCondition(true);
            }
        } else {
            // 不为空，针对每个游戏大类进行配置
            if (!Objects.isNull(activityConfigDetailVO)) {
                if (activityConfigDetailVO.getVenueTypeList() != null && !activityConfigDetailVO.getVenueTypeList().isEmpty()) {
                    // 对应游戏大类的配置
                    List<DepositConfigV2DTO> depositConfigDTOS = activitySecondRechargeRespVO.getDepositConfigDTOS();
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
                        calculateParamTemp.setSourceAmount(secondDepositAmount);
                        calculateParamTemp.setDiscountType(depositConfigDTO.getDiscountType());
                        calculateParamTemp.setRate(rate);
                        calculateParamTemp.setWashRatio(depositConfigDTO.getWashRatio());
                        //NOTE 直接指定币种
                        calculateParamTemp.setRewardCurrencyCode(currencyConfig);

                        //按照配置计算奖励金额
                        calculateRewardAmount(calculateParamTemp);
                        // 计算后金额
                        venueValueVO.setActivityAmount(calculateParamTemp.getRewardAmount());
                        venueValueVO.setRunningWater(calculateParamTemp.getRequiredTurnover());

                        venueValueVO.setActivityAmountCurrencyCode(currencyConfig);
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
            }else {
                log.error("次充活动，游戏大类配置失败,siteCode:{},userId:{}", siteCode, userId);
            }
        }
        //限时活动判断逻辑
        if (hasDeposit && Objects.equals(ActivityDeadLineEnum.LONG_TERM.getType(), activitySecondRechargeRespVO.getActivityDeadline())) {
            if (!(secondDepositTime >= activitySecondRechargeRespVO.getActivityStartTime())) {
                log.info("限时活动判断逻辑,获取次存活动详情,用户次存时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
                return depositDetailVO;
            }
        }

        if (hasDeposit && Objects.equals(ActivityDeadLineEnum.LIMITED_TIME.getType(), activitySecondRechargeRespVO.getActivityDeadline())) {
            if (!(secondDepositTime >= activitySecondRechargeRespVO.getActivityStartTime() && secondDepositTime <= activitySecondRechargeRespVO.getActivityEndTime())) {
                log.info("长期活动判断逻辑,获取次充活动详情,用户次存时间条件不满足,siteCode:{},userId:{}", siteCode, userId);
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
        LambdaQueryWrapper<SiteActivitySecondRechargeV2PO> query = Wrappers.lambdaQuery();
        query.eq(SiteActivitySecondRechargeV2PO::getActivityId, siteActivityBasePO.getId());
        SiteActivitySecondRechargeV2PO activitySecondRechargePO = rechargeRepository.selectOne(query);
        //当前活动未生效 无须派发
        if (activitySecondRechargePO == null) {
            log.info("当前站点:{}不存在已生效的次存活动,无须派发", trigger.getSiteCode());
            return;
        }
        String siteCode = trigger.getSiteCode();
        String userId = trigger.getUserId();
        String mainCurrency = trigger.getCurrencyCode();
        //会员存款金额大于配置的最小存款金额，满足条件
        Integer participationMode = activitySecondRechargePO.getParticipationMode();
        //人工参与
        boolean manualFlag = false;
        if (Objects.equals(ActivityParticipationModeEnum.MANUAL.getCode(), participationMode)) {
            SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam = new SiteActivityEventRecordQueryParam();
            siteActivityEventRecordQueryParam.setSiteCode(activitySecondRechargePO.getSiteCode());
            siteActivityEventRecordQueryParam.setUserId(trigger.getUserId());
            siteActivityEventRecordQueryParam.setActivityTemplate(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType());
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
        String venueType = "";
        if (ObjectUtil.equals(ActivityParticipationModeEnum.AUTO.getCode(), activitySecondRechargePO.getParticipationMode())
                && ObjectUtil.isNotEmpty(activitySecondRechargePO.getVenueType())) {
            venueType = activityTypingAmountService.initUserActivityTypingAmountLimit(activitySecondRechargePO.getVenueType(), byUserId);

        }
        CalculateParamV2 calculateParam = new CalculateParamV2();


        calculateParam.setSiteCode(trigger.getSiteCode());
        calculateParam.setSourceCurrencyCode(mainCurrency);
        calculateParam.setSourceAmount(trigger.getRechargeAmount());

        // 查看个人是否配置了游戏大类
        venueType = activityTypingAmountService.getUserActivityTypingConfig(byUserId);

        // 活动配置的游戏大类（可能为 null 或空）
        String configVenueType = activitySecondRechargePO.getVenueType();
        boolean configFlag = false;
        // 判断逻辑：
        if (StrUtil.isEmpty(configVenueType)) {
            // 活动没配置游戏大类
            configFlag = true; // 走通用配置
        } else {
            // 活动配置了游戏大类
            if (StrUtil.isEmpty(venueType)) {
                // 活动配置了，会员没配置，无法发放
                log.info("次存活动计算,当前活动要求绑定游戏大类，请先进行配置.");
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }
        String configCurrency;

        List<DepositConfigV2DTO> depositConfigDTOS = JSON.parseArray(activitySecondRechargePO.getConditionalValue(), DepositConfigV2DTO.class);
        // 配置了游戏大类
        // 优惠方式类型，0.百分比，1.固定
        if (CollectionUtil.isEmpty(depositConfigDTOS)) {
            log.error("首充活动v2配置异常,siteCode:{},userId:{},depositConfigDTOS:{}", siteCode, userId, depositConfigDTOS);
            return;
        }

        // 没有配置游戏大类
        if (configFlag) {

            log.info("二次充值V2奖励计算,无游戏大类, 用户ID:{}", userId);


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
                log.info("次充活动v2配置, ,conditionalValue:{}", rechargePercentageV2VOS);
                if (rechargePercentageV2VO == null) {
                    log.error("次充活动v2， 非游戏大类找不到对应币种配置rechargePercentageV2VO, ,siteCode:{},userId:{},币种:{}", siteCode, userId, configCurrency);
                    return;
                }
                calculateParam.setDiscountType(discountType);
                calculateParam.setConditionalValue(JSON.toJSONString(rechargePercentageV2VO));
                calculateParam.setWashRatio(siteActivityBasePO.getWashRatio());
            }else {
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

            log.info("二次充值V2奖励计算, 游戏大类, 用户ID:{}", userId);

            String finalVenueType = venueType;
            DepositConfigV2DTO depositConfigDTO = depositConfigDTOS.stream()
                    .filter(e -> e.getVenueType().equals(finalVenueType))
                    .findFirst().orElse(null);

            if (depositConfigDTO == null) {
                log.error("次充活动v2， 游戏大类找不到对应币种配置, ,siteCode:{},userId:{}", siteCode, userId);
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
                    log.error("次充活动v2， 游戏大类找不到对应币种配置,百分比,siteCode:{},userId:{},mainCurrency币种:{}", siteCode, userId, mainCurrency);
                    return;
                }
                calculateParam.setConditionalValue(JSON.toJSONString(percentageV2VO));
            } else {
                List<FixedAmountV2VO> fixedAmountVOS = depositConfigDTO.getFixedAmountVOS();
                FixedAmountV2VO fixedAmountV2VO = fixedAmountVOS.stream().filter(dto -> configCurrency.equalsIgnoreCase(dto.getCurrency())).findFirst().orElse(null);

                if (fixedAmountV2VO == null) {
                    log.error("次充活动v2， 游戏大类找不到对应币种配置,固定金额,siteCode:{},userId:{},mainCurrency币种:{}", siteCode, userId, mainCurrency);
                    return;
                }
                calculateParam.setConditionalValue(JSON.toJSONString(fixedAmountV2VO));
            }
            calculateParam.setWashRatio(depositConfigDTO.getWashRatio());
        }

        calculateParam.setRate(currencyRateMap.get(mainCurrency));
        //NOTE 这里领取奖励的币种直接设置好
        calculateParam.setRewardCurrencyCode(configCurrency);
        //按照配置计算奖励金额
        calculateRewardAmount(calculateParam);

        log.info("二次充值V2奖励计算 用户ID:{}, 计算金额:{}", userId,calculateParam.getRewardAmount());

        //人工参与已申请或自动参与
        if (manualFlag || Objects.equals(ActivityParticipationModeEnum.AUTO.getCode(), participationMode)) {
            log.info("二次充值,人工曾参与已申请:{}或者自动参与:{}", manualFlag, participationMode);
            processReward(trigger, activitySecondRechargePO, calculateParam);
        }else {
            log.error("二次充值,人工曾参与未申请:{} 且不是自动参与:{}, 账号: {}", manualFlag, participationMode, byUserId.getUserAccount());

        }
    }


    /**
     * 按照配置计算奖励金额
     *
     * @param calculateParam 计算参数
     */
    private void calculateRewardAmount(CalculateParamV2 calculateParam) {
        BigDecimal rechargeAmount = calculateParam.getSourceAmount();
        BigDecimal washRatio = calculateParam.getWashRatio();
        //当前会员充值的平台币
        BigDecimal rechargeAmountPlat = AmountUtils.divide(rechargeAmount, calculateParam.getRate());
        BigDecimal rewardAmount = BigDecimal.ZERO;
        BigDecimal rewardAmountPlat = BigDecimal.ZERO;

        BigDecimal requiredTurnover = BigDecimal.ZERO;

        String rewardCurrencyCode = calculateParam.getRewardCurrencyCode();

        if (StrUtil.isEmpty(calculateParam.getConditionalValue())){
            log.error("奖励金额条件配置不存在, 条件配置: {}", calculateParam);
        }

        //判断优惠方式类型
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(calculateParam.getDiscountType())) {

            RechargePercentageV2VO percentageVO = JSON.parseObject(calculateParam.getConditionalValue(), RechargePercentageV2VO.class);

            if (percentageVO==null){
                log.error("次存活动V2, 百分比类型条件配置不存在, 币种: {}", rewardCurrencyCode);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

            BigDecimal minDeposit = percentageVO.getMinDeposit();
            log.info("奖励计算,按照比例派发,最小充值金额:{},当前充值金额:{}", minDeposit, rechargeAmount);

            if (rewardCurrencyCode.equals("WTC")){
                if (rechargeAmountPlat.compareTo(minDeposit) >= 0) {
                    //获取优惠百分比
                    BigDecimal discountPct = percentageVO.getDiscountPct();
                    // 计算赠送金额 直接拿充值金额*百分比=赠送金额
                    BigDecimal bonusPlatAmount = AmountUtils.multiplyPercent(rechargeAmountPlat, discountPct);
                    //配置单日最高赠送金额
                    BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                    // 比较赠送金额与配置的单日最高赠送金额
                    if (bonusPlatAmount.compareTo(maxDailyBonus) > 0) {
                        //赠送金额大于配置的单日最高，使用配置的单日最高
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

                    log.info("次充V2奖励计算,按照比例派发,奖励金额:{},当前充值金额:{},洗码倍率:{},所需求流水(（本金+彩金）*倍数):{}", rewardAmount, rechargeAmount, washRatio, requiredTurnover);
                }
                calculateParam.setRewardAmount(rewardAmountPlat);
            }else {
                if (rechargeAmount.compareTo(minDeposit) >= 0) {
                    //获取优惠百分比
                    BigDecimal discountPct = percentageVO.getDiscountPct();
                    // 计算赠送金额 直接拿充值金额*百分比=赠送金额
                    BigDecimal bonusAmount = AmountUtils.multiplyPercent(rechargeAmount, discountPct);
                    //配置单日最高赠送金额
                    BigDecimal maxDailyBonus = percentageVO.getMaxDailyBonus();
                    // 比较赠送金额与配置的单日最高赠送金额
                    if (bonusAmount.compareTo(maxDailyBonus) > 0) {
                        //赠送金额大于配置的单日最高，使用配置的单日最高
                        //平台币转化为主货币
                        rewardAmount = maxDailyBonus;
                    } else {
                        //按照法币进行转换获得奖励
                        rewardAmount = bonusAmount;
                    }
                    rewardAmountPlat = AmountUtils.divide(rewardAmount, calculateParam.getRate());
                    //所需求流水=（本金+彩金）*倍数
                    BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                    requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                    log.info("奖励计算,按照比例派发,奖励金额:{},当前充值金额本币:{},洗码倍率:{},所需求流水(（本金+彩金）*倍数):{}", rewardAmount, rechargeAmount, washRatio, requiredTurnover);
                }
                calculateParam.setRewardAmount(rewardAmount);
            }


        } else {
            log.info("奖励计算,按照固定金额派发,当前充值金额:{}", rechargeAmount);

            FixedAmountV2VO amountV2VO = JSON.parseObject(calculateParam.getConditionalValue(), FixedAmountV2VO.class);

            if (amountV2VO == null) {
                log.error("首存活动V2, 固定金额类型条件配置不存在, 币种: {}", rewardCurrencyCode);
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }

            List<AmountV2VO> amountList = amountV2VO.getAmount();
            //判断当前首充平台币是否满足某个区间范围
            //根据当前会员的充值金额，站点，币种，转平台币判断区间
            //按照区间最大值排序 满足条件里的最大奖励金额
            if (rewardCurrencyCode.equals("WTC")){
                for (AmountV2VO fixedAmountVO : amountList) {
                    //判断一下是否满足某个区间
                    BigDecimal minDeposit = fixedAmountVO.getMinDeposit();
                    BigDecimal maxDeposit = fixedAmountVO.getMaxDeposit();
                    BigDecimal bonusAmount = fixedAmountVO.getBonusAmount();
                    //只要大于最大值 奖励金额取配置金额
                    log.debug("平台币金额rechargeAmountPlat:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeAmountPlat, minDeposit, maxDeposit, fixedAmountVO.getBonusAmount());
                    if (rechargeAmountPlat.compareTo(maxDeposit) >= 0) {
                        rewardAmountPlat = bonusAmount;
                    } else {
                        //需要大于最小值
                        if (rechargeAmountPlat.compareTo(minDeposit) >= 0) {
                            rewardAmountPlat = bonusAmount;
                        }
                        break;
                    }
                }
                //充值金额大于等于 某个区间最大值 获得奖励上限
                log.info("次存充值平台币金额:{},实际奖励平台币金额:{}", rechargeAmountPlat, rewardAmountPlat);

                rewardAmount = AmountUtils.multiply(rewardAmountPlat, calculateParam.getRate());
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);
                log.info("固定金额,使用平台币计算,次存充值平台币金额:{},奖励金额:{},所需流水:{},记录到平台币账户", rechargeAmountPlat, rewardAmount, requiredTurnover);
                calculateParam.setRewardAmount(rewardAmountPlat);
            }else {

                for (AmountV2VO fixedAmountVO : amountList) {
                    //判断一下是否满足某个区间
                    BigDecimal minDeposit = fixedAmountVO.getMinDeposit();
                    BigDecimal maxDeposit = fixedAmountVO.getMaxDeposit();
                    //只要大于最大值 奖励金额取配置金额
                    log.debug("本币金额:{},最小充值:{},最大充值:{},当前配置奖励金额:{}", rechargeAmount, minDeposit, maxDeposit, fixedAmountVO.getBonusAmount());
                    if (rechargeAmount.compareTo(maxDeposit) >= 0) {
                        rewardAmount = fixedAmountVO.getBonusAmount();
                    } else {
                        //需要大于最小值
                        if (rechargeAmount.compareTo(minDeposit) >= 0) {
                            rewardAmount= fixedAmountVO.getBonusAmount();
                        }
                        break;
                    }
                }
                BigDecimal totalAmount = rechargeAmount.add(rewardAmount);
                requiredTurnover = AmountUtils.multiply(totalAmount, washRatio);

                rewardAmountPlat = AmountUtils.divide(rewardAmount, calculateParam.getRate());
                log.info("固定金额,使用法币计算,次存充值平台币金额:{},奖励金额:{},所需流水:{},记录到法币账户", rewardAmountPlat, rewardAmount, requiredTurnover);

                calculateParam.setRewardAmount(rewardAmount);
            }
        }
        //如果是 首充跟次充 需要在减去本金,因为充值每笔充值都有打码量
        calculateParam.setRequiredTurnover(requiredTurnover);
        log.info("奖励计算,奖励金额:{},当前充值金额:{},所需求流水:{}", rewardAmount, rechargeAmount, requiredTurnover);
        if (rewardAmount.compareTo(BigDecimal.ZERO) >= 1) {
            requiredTurnover = requiredTurnover.subtract(rechargeAmount);
        }else {
            requiredTurnover = BigDecimal.ZERO;
        }
        calculateParam.setRequiredTurnover(requiredTurnover);
        log.info("奖励计算,奖励金额:{},当前充值金额:{},所需求流水扣除充值金额后:{}", rewardAmount, rechargeAmount, requiredTurnover);
    }


    /**
     * 处理奖励派发相关
     *
     * @param trigger                  首充消息实体
     * @param activitySecondRechargePO 当前首充配置
     * @param calculateParam           奖励金额
     */
    private void processReward(RechargeTriggerVO trigger, SiteActivitySecondRechargeV2PO activitySecondRechargePO, CalculateParamV2 calculateParam) {
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
        userBaseReqVO.setTimezone(trigger.getTimezone());
        // 派发的时候，需要指定用户配置的游戏大类
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(trigger, userInfoVO);
        userInfoVO.setUserId(trigger.getUserId());
        userInfoVO.setSiteCode(trigger.getSiteCode());
        String userActivityTypingConfig = activityTypingAmountService.getUserActivityTypingConfig(userInfoVO);
        userBaseReqVO.setVenueType(userActivityTypingConfig);
        //自动触发才校验 手动触发无须校验
        ResponseVO<ToActivityVO> responseVO = activityParticipateV2Api.checkToActivity(userBaseReqVO);
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
        activitySendMqVO.setActivityTemplate(ActivityTemplateV2Enum.SECOND_DEPOSIT_V2.getType());
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

        activitySendMqVO.setHandicapMode(1);

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
    private String checkParam(ActivitySecondRechargeV2VO activity) {
        String conditionValue = "";

        // 场馆类型为空
        if (!StringUtils.hasText(activity.getVenueType())) {
            List<DepositConfigV2DTO> depositConfigV2DTOS = new ArrayList<>();
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
                //金额区间校验
                depositConfigV2DTO.setFixedAmountVOS(fixedAmountVOS);
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
     */
    private void validateFixedAmountList(List<FixedAmountV2VO> fixedAmountVOS) {
        if (fixedAmountVOS == null || fixedAmountVOS.isEmpty()) {
            throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
        }

        for (FixedAmountV2VO current : fixedAmountVOS) {
            BigDecimal previousMaxDeposit = null;
            for (AmountV2VO amountV2VO : current.getAmount()) {
                // 校验当前对象的存款最大值是否大于存款最小值
                if (amountV2VO.getMaxDeposit().compareTo(amountV2VO.getMinDeposit()) <= 0) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                // 校验当前对象的存款最大值是否小于下一个对象的存款最小值
                if (previousMaxDeposit != null && amountV2VO.getMinDeposit().compareTo(previousMaxDeposit) <= 0) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
                previousMaxDeposit = amountV2VO.getMaxDeposit();
            }

        }
    }
}
