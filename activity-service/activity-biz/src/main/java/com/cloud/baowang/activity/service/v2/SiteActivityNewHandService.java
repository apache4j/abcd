package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityTemplateV2Enum;
import com.cloud.baowang.activity.api.vo.ActivityConfigDetailVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityNewHandRespVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityNewHandVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.*;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstDepositVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleFirstWithdrawalVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleNegativeProfitVO;
import com.cloud.baowang.activity.api.vo.v2.newHand.condition.RuleSignInVO;
import com.cloud.baowang.activity.param.CalculateParamV2;
import com.cloud.baowang.activity.po.v2.SiteActivityBaseV2PO;
import com.cloud.baowang.activity.po.v2.SiteActivityNewHandPO;
import com.cloud.baowang.activity.po.v2.SiteActivityOrderRecordV2PO;
import com.cloud.baowang.activity.repositories.v2.SiteActivityNewHandRepository;
import com.cloud.baowang.activity.utils.OrderNoUtils;
import com.cloud.baowang.activity.vo.mq.ActivitySendListMqVO;
import com.cloud.baowang.activity.vo.mq.ActivitySendMqVO;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.utils.AmountUtils;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.kafka.constants.TopicsConstants;
import com.cloud.baowang.common.kafka.utils.KafkaUtil;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.report.api.api.ReportUserWinLoseApi;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseResponseVO;
import com.cloud.baowang.report.api.vo.userwinlose.DailyWinLoseVO;
import com.cloud.baowang.system.api.api.dict.SystemDictConfigApi;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import com.cloud.baowang.system.api.api.site.SiteApi;
import com.cloud.baowang.system.api.enums.dict.DictCodeConfigEnums;
import com.cloud.baowang.system.api.vo.dict.SystemDictConfigRespVO;
import com.cloud.baowang.system.api.vo.site.SiteVO;
import com.cloud.baowang.user.api.api.UserInfoApi;
import com.cloud.baowang.user.api.vo.UserInfoVO;
import com.cloud.baowang.wallet.api.api.SiteCurrencyInfoApi;
import com.cloud.baowang.wallet.api.api.UserWithdrawRecordApi;
import com.cloud.baowang.wallet.api.vo.recharge.RechargeTriggerVO;
import com.cloud.baowang.wallet.api.vo.userCoin.UserDepositWithdrawalResVO;
import com.cloud.baowang.wallet.api.vo.userWithdrawRecord.UserWithdrawalRecordRequestVO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityNewHandService extends ServiceImpl<SiteActivityNewHandRepository, SiteActivityNewHandPO> {

    private final I18nApi i18nApi;
    private final SystemDictConfigApi systemDictConfigApi;
    private final SiteCurrencyInfoApi siteCurrencyInfoApi;
    private final UserInfoApi userInfoApi;
    private final SiteApi siteApi;
    private final UserWithdrawRecordApi userWithdrawRecordApi;

    private final ReportUserWinLoseApi reportUserWinLoseApi;

    public boolean insert(ActivityNewHandVO activityNewHandVO) {

        SiteActivityNewHandPO activityNewHandPO = new SiteActivityNewHandPO();
        activityNewHandPO.setActivityId(activityNewHandVO.getId());
        activityNewHandPO.setSiteCode(activityNewHandVO.getSiteCode());
        activityNewHandPO.setDistributionType(activityNewHandVO.getDistributionType());
        activityNewHandPO.setParticipationMode(activityNewHandVO.getParticipationMode());
        RuleFirstDepositVO firstDepositConditionVO = activityNewHandVO.getFirstDepositConditionVO();
        RuleFirstWithdrawalVO firstWithdrawalConditionVO = activityNewHandVO.getFirstWithdrawalConditionVO();
        RuleSignInVO signInConditionVO = activityNewHandVO.getSignInConditionVO();
        RuleNegativeProfitVO negativeProfitConditionVO = activityNewHandVO.getNegativeProfitConditionVO();

        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        for (ConditionFirstDepositVO vo : firstDepositConditionVO.getConditionVOS()) {

            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }

        for (ConditionFirstWithdrawalVO vo : firstWithdrawalConditionVO.getConditionVOS()) {

            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }

        for (ConditionSignInVO vo : signInConditionVO.getConditionVOS()) {


            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }

        for (ConditionNegativeProfitVO vo : negativeProfitConditionVO.getConditionVOS()) {

            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }
        activityNewHandPO.setConditionFirstDeposit(JSON.toJSONString(firstDepositConditionVO));
        activityNewHandPO.setConditionFirstWithdrawal(JSON.toJSONString(firstWithdrawalConditionVO));
        activityNewHandPO.setConditionSignIn(JSON.toJSONString(signInConditionVO));
        activityNewHandPO.setConditionNegativeProfit(JSON.toJSONString(negativeProfitConditionVO));

        // 插入i8
        if (!i18nData.isEmpty()) {
            ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
            if (!i18Bool.isOk() || !i18Bool.getData()) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }
        this.baseMapper.insert(activityNewHandPO);
        return true;
    }


    public boolean updateInfo(ActivityNewHandVO activityNewHandVO) {
        LambdaQueryWrapper<SiteActivityNewHandPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityNewHandPO::getActivityId, activityNewHandVO.getId());
        lambdaQueryWrapper.eq(SiteActivityNewHandPO::getSiteCode, activityNewHandVO.getSiteCode());
        SiteActivityNewHandPO siteActivityAssignDayPODb = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityAssignDayPODb == null) {
            return false;
        }

        RuleFirstDepositVO firstDepositConditionVO = activityNewHandVO.getFirstDepositConditionVO();
        RuleFirstWithdrawalVO firstWithdrawalConditionVO = activityNewHandVO.getFirstWithdrawalConditionVO();
        RuleSignInVO signInConditionVO = activityNewHandVO.getSignInConditionVO();
        RuleNegativeProfitVO negativeProfitConditionVO = activityNewHandVO.getNegativeProfitConditionVO();

        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        for (ConditionFirstDepositVO vo : firstDepositConditionVO.getConditionVOS()) {
            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }
        for (ConditionFirstWithdrawalVO vo : firstWithdrawalConditionVO.getConditionVOS()) {
            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }
        for (ConditionSignInVO vo : signInConditionVO.getConditionVOS()) {
            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }
        for (ConditionNegativeProfitVO vo : negativeProfitConditionVO.getConditionVOS()) {
            if (CollUtil.isNotEmpty(vo.getDetailShowI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowI18nCodeList());
                vo.setDetailShowI18nCode(activityNameI18);
                vo.setDetailShowI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcI18nCodeList());
                vo.setDetailShowDarkI18nCode(activityNameI18);
                vo.setDetailShowDarkI18nCodeList(null);

            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowDarkI18nCodeList());
                vo.setDetailShowPcI18nCode(activityNameI18);
                vo.setDetailShowPcI18nCodeList(null);
            }

            if (CollUtil.isNotEmpty(vo.getDetailShowPcDarkI18nCodeList())) {
                String activityNameI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_DETAIL_SHOW_PIC_PC_DARK.getCode());
                i18nData.put(activityNameI18, vo.getDetailShowPcDarkI18nCodeList());
                vo.setDetailShowPcDarkI18nCode(activityNameI18);
                vo.setDetailShowPcDarkI18nCodeList(null);
            }
        }
        // 插入i8
        if (!i18nData.isEmpty()) {
            ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
            if (!i18Bool.isOk() || !i18Bool.getData()) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
        }

        LambdaUpdateWrapper<SiteActivityNewHandPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getParticipationMode, activityNewHandVO.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getDistributionType, activityNewHandVO.getDistributionType());
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getPlatformOrFiatCurrency, activityNewHandVO.getPlatformOrFiatCurrency());

        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getConditionFirstDeposit, JSON.toJSONString(firstDepositConditionVO));
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getConditionFirstWithdrawal, JSON.toJSONString(firstWithdrawalConditionVO));
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getConditionSignIn, JSON.toJSONString(signInConditionVO));
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getConditionNegativeProfit, JSON.toJSONString(negativeProfitConditionVO));

        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getUpdatedTime, System.currentTimeMillis());
        lambdaUpdateWrapper.set(SiteActivityNewHandPO::getUpdater, CurrReqUtils.getAccount());

        lambdaUpdateWrapper.eq(SiteActivityNewHandPO::getActivityId, activityNewHandVO.getId());

        this.update(lambdaUpdateWrapper);
        return true;
    }

    public SiteActivityNewHandPO info(String activityId) {
        LambdaQueryWrapper<SiteActivityNewHandPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityNewHandPO::getActivityId, activityId);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }


    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityNewHandPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityNewHandPO::getSiteCode, siteCode);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    public List<SiteActivityNewHandPO> selectByActivityIds(List<String> activityIds) {
        LambdaQueryWrapper<SiteActivityNewHandPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityNewHandPO>();
        lambdaQueryWrapper.in(SiteActivityNewHandPO::getActivityId, activityIds);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityNewHandPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityNewHandPO::getActivityId, activityId);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    /**
     * NOTE 计算新手活动首充奖励
     *      充值时间和注册时间在7天内
     */
    public void firstDepositValidateAndReward(RechargeTriggerVO trigger, SiteActivityBaseV2PO siteActivityBasePO) {

        String siteCode = trigger.getSiteCode();
        String timezone = trigger.getTimezone();
        String userId = trigger.getUserId();

        UserInfoVO userInfoVO = userInfoApi.getByUserId(userId);
        String mainCurrency = userInfoVO.getMainCurrency();

        long registerTime = userInfoVO.getRegisterTime();
        long registerTime7 = registerTime + (1000 * 3600 * 24 * 7L);
        long rechargeTime = userInfoVO.getFirstDepositTime();
        long endOf7DayInTimeZone = TimeZoneUtils.getEndOfDayInTimeZone(registerTime7, timezone);
        if (rechargeTime > endOf7DayInTimeZone) {
            log.info("新手首次充值时间已经超过七天，不符合要求:{}", trigger);
            return;
        }
        SiteActivityNewHandPO info = this.info(siteActivityBasePO.getId());
        ActivityNewHandRespVO activityNewHandRespVO = BeanUtil.copyProperties(info, ActivityNewHandRespVO.class);
        //用基础表的ID就是活动ID
        activityNewHandRespVO.setId(siteActivityBasePO.getId());

        String conditionFirstDeposit = info.getConditionFirstDeposit();

        RuleFirstDepositVO conditionVO = JSON.parseObject(conditionFirstDeposit, RuleFirstDepositVO.class);

        if (conditionVO == null) {
            log.error("新手首次充值配置不存在:{}", trigger);
            return;
        }

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(trigger.getSiteCode());
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.info("新手首次充值活动计算,货币转换异常.,siteCode:{},userId:{},currencyRateMap:{}", siteCode, trigger.getUserId(), currencyRateMap);
            return;
        }
        String processCurrency;
        String platformOrFiatCurrency = conditionVO.getPlatformOrFiatCurrency();
        if ("1".equals(platformOrFiatCurrency)) {
            processCurrency = mainCurrency;
        } else {
            processCurrency = "WTC";
        }
        List<ConditionFirstDepositVO> conditionFirstDepositVOS = conditionVO.getConditionVOS();
        ConditionFirstDepositVO firstDepositVO = conditionFirstDepositVOS.stream().filter(conditionFirstDepositVO ->
                processCurrency.equalsIgnoreCase(conditionFirstDepositVO.getCurrencyCode())).findFirst().orElse(null);

        if (ObjectUtil.isEmpty(firstDepositVO)) {
            log.info("新手首次充值配置不存在. siteCode:{},userId:{}, 币种:{}", siteCode, trigger.getUserId(), processCurrency);
            return;
        }

        CalculateParamV2 calculateParam = new CalculateParamV2();
        calculateParam.setConditionFirstDepositVO(firstDepositVO);
        calculateParam.setRate(currencyRateMap.get(mainCurrency));
        calculateParam.setSourceCurrencyCode(mainCurrency);
        calculateParam.setSiteCode(siteCode);
        calculateParam.setSourceAmount(trigger.getRechargeAmount());
        calculateParam.setRewardCurrencyCode(processCurrency);
        calculateParam.setNewHandType(1);

        //NOTE 计算奖励金额
        calculateRewardAmount(calculateParam);
        if (calculateParam.getRewardAmount().compareTo(BigDecimal.ZERO) > 0) {
            //NOTE 发送奖励消息
            processReward(userInfoVO, activityNewHandRespVO, calculateParam);
        }

    }


    /**
     * 处理奖励派发相关
     *
     * @param trigger           首充消息实体
     * @param activityNewHandPO 当前首充配置
     * @param calculateParam    奖励金额
     */
    public void processReward(UserInfoVO trigger, ActivityNewHandRespVO activityNewHandPO, CalculateParamV2 calculateParam) {
        BigDecimal rewardAmount = calculateParam.getRewardAmount();
        String currencyCode = calculateParam.getRewardCurrencyCode();
        if (rewardAmount.compareTo(BigDecimal.ZERO) <= 0) {
            log.info("奖励金额小于等于0,无需派发");
            return;
        }
        String activityId = activityNewHandPO.getId();
        String siteCode = activityNewHandPO.getSiteCode();
        String userId = trigger.getUserId();

        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(trigger, userInfoVO);
        userInfoVO.setUserId(trigger.getUserId());
        userInfoVO.setSiteCode(trigger.getSiteCode());
        log.info("新手首次充值,开始派发, 会员id:{},币种:{},奖励金额:{}", trigger.getUserId(), currencyCode, rewardAmount);

        SystemDictConfigRespVO systemDictConfigRespVO = systemDictConfigApi.getByCode(DictCodeConfigEnums.ACTIVITY_BENEFIT_EXPIRATION_TIME.getCode(), siteCode).getData();
        Double hourTime = Double.valueOf(systemDictConfigRespVO.getConfigParam());

        List<ActivitySendMqVO> activitySendMqVOList = Lists.newArrayList();
        ActivitySendMqVO activitySendMqVO = new ActivitySendMqVO();

        //NOTE 订单号生成问题

        String orderNo = OrderNoUtils.genOrderNo(trigger.getUserId(), ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), calculateParam.getNewHandType() + "");
        activitySendMqVO.setOrderNo(orderNo);

        activitySendMqVO.setSiteCode(activityNewHandPO.getSiteCode());
        activitySendMqVO.setActivityTemplate(ActivityTemplateV2Enum.NEW_HAND.getType());
        activitySendMqVO.setUserId(userId);
        activitySendMqVO.setDistributionType(activityNewHandPO.getDistributionType());
        activitySendMqVO.setReceiveStartTime(System.currentTimeMillis());
        // 72小时失效
        activitySendMqVO.setReceiveEndTime(DateUtils.addHour(System.currentTimeMillis(), hourTime));
        activitySendMqVO.setActivityAmount(rewardAmount);
        activitySendMqVO.setCurrencyCode(currencyCode);
        activitySendMqVO.setRunningWaterMultiple(calculateParam.getWashRatio());
        activitySendMqVO.setRunningWater(calculateParam.getRequiredTurnover());
        activitySendMqVO.setActivityId(activityId);
        activitySendMqVO.setParticipationMode(activityNewHandPO.getParticipationMode());

        activitySendMqVO.setHandicapMode(1);
        log.info("满足条件开始派发金额:{}", activitySendMqVO);
        activitySendMqVOList.add(activitySendMqVO);

        ActivitySendListMqVO activitySendListMqVO = new ActivitySendListMqVO();
        activitySendListMqVO.setList(activitySendMqVOList);
        //发送通知消息
        KafkaUtil.send(TopicsConstants.SEND_USER_ACTIVITY_ORDER_LIST, activitySendListMqVO);
    }

    /**
     * 根据规则计算奖励金额
     */
    public void calculateRewardAmount(CalculateParamV2 calculateParam) {

        BigDecimal rate = calculateParam.getRate();
        calculateParam.setRequiredTurnover(BigDecimal.ZERO);
        calculateParam.setRewardAmount(BigDecimal.ZERO);
        boolean wtcFlag = calculateParam.getRewardCurrencyCode().equalsIgnoreCase("WTC");
        int newHandType = calculateParam.getNewHandType();

        if (newHandType == 1) {
            ConditionFirstDepositVO conditionFirstDepositVO = calculateParam.getConditionFirstDepositVO();
            BigDecimal rewardAmountConfig = conditionFirstDepositVO.getRewardAmount();
            BigDecimal depositAmount = conditionFirstDepositVO.getDepositAmount();
            //充值金额
            BigDecimal sourceAmount = calculateParam.getSourceAmount();
            //NOTE 充值金额平台币
            BigDecimal sourcePlatAmount = AmountUtils.divide(sourceAmount, rate);
            BigDecimal requiredTurnover = BigDecimal.ZERO;
            BigDecimal rewardAmount = BigDecimal.ZERO;
            BigDecimal washRatio = conditionFirstDepositVO.getWashRatio();
            if (wtcFlag) {
                //NOTE 当实际首充值金额大于配置充值金额，发放奖励
                if (sourcePlatAmount.compareTo(depositAmount) >= 0) {
                    rewardAmount = rewardAmountConfig;
                    requiredTurnover = rewardAmountConfig.multiply(rate).multiply(washRatio);
                }
            } else {
                if (sourceAmount.compareTo(depositAmount) >= 0) {
                    rewardAmount = rewardAmountConfig;
                    requiredTurnover = rewardAmountConfig.multiply(washRatio);
                }
            }
            calculateParam.setRewardAmount(rewardAmount);
            calculateParam.setRequiredTurnover(requiredTurnover);
        } else if (newHandType == 2) {
            ConditionFirstWithdrawalVO firstWithdrawalVO = calculateParam.getConditionFirstWithdrawalVO();
            BigDecimal rewardAmountConfig = firstWithdrawalVO.getRewardAmount();
            BigDecimal washRatio = firstWithdrawalVO.getWashRatio();
            if (calculateParam.getWithdrawalCount() > 0) {
                if (wtcFlag) {
                    calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(rate).multiply(washRatio));
                } else {
                    calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(washRatio));
                }
                calculateParam.setRewardAmount(rewardAmountConfig);
            }
        } else if (newHandType == 3) {
            ConditionSignInVO signInVO = calculateParam.getConditionSignInVO();
            long validAmountCount = calculateParam.getValidAmountCount();
            if (validAmountCount > 4) {
                BigDecimal rewardAmountConfig = signInVO.getRewardAmount();
                BigDecimal washRatio = signInVO.getWashRatio();
                if (wtcFlag) {
                    calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(rate).multiply(washRatio));
                } else {
                    calculateParam.setRequiredTurnover(rewardAmountConfig.multiply(washRatio));
                }
                calculateParam.setRewardAmount(rewardAmountConfig);
            }
        } else if (newHandType == 4) {
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

    }

    /**
     * NOTE    状态相关 CODE
     *      10000 进行中
     *      30049 存款
     *      30050 取款
     *      30047 已达标
     *      30048 未达标
     *      30055 已过期
     *          80051  立即签到 高亮
     *          80045  立即签到 置灰
     */
    public void getActivityNewHandDetail(ActivityNewHandRespVO activityNewHandRespVO, String userId,
                                         ActivityConfigDetailVO activityConfigDetailVO, List<SiteActivityOrderRecordV2PO> orderRecordV2POS) {

        UserInfoVO userInfo = null;
        if (StrUtil.isNotEmpty(userId)) {
            userInfo = userInfoApi.getByUserId(userId);
        }

        int allStatus = 10000;
        int firstDepositStatus = 30049;
        int firstWithdrawalStatus = 30050;
        int signInStatus = 80051;
        int negativeProfitStatus = 10000;

        String siteCode = activityNewHandRespVO.getSiteCode();
        ResponseVO<SiteVO> siteInfoRes = siteApi.getSiteInfo(siteCode);
        SiteVO siteVO = siteInfoRes.getData();

        String timeZone = siteVO.getTimezone();

        Map<String, BigDecimal> currencyRateMap = siteCurrencyInfoApi.getAllFinalRate(siteCode);
        if (ObjectUtil.isEmpty(currencyRateMap) || CollectionUtil.isEmpty(currencyRateMap)) {
            log.error("新手活动计算,货币汇率查询异常.,siteCode:{}", siteCode);
            return;
        }

        //NOTE 四个类型的数据
        //NOTE 1. 首次充值
        RuleFirstDepositVO firstDepositConditionVO = activityNewHandRespVO.getFirstDepositConditionVO();
        RuleFirstWithdrawalVO firstWithdrawalConditionVO = activityNewHandRespVO.getFirstWithdrawalConditionVO();
        RuleSignInVO signInConditionVO = activityNewHandRespVO.getSignInConditionVO();
        RuleNegativeProfitVO negativeProfitConditionVO = activityNewHandRespVO.getNegativeProfitConditionVO();

        String firstDepositCurrencyFlag = firstDepositConditionVO.getPlatformOrFiatCurrency();
        String firstWithdrawalCurrencyFlag = firstWithdrawalConditionVO.getPlatformOrFiatCurrency();
        String signInCurrencyFlag = signInConditionVO.getPlatformOrFiatCurrency();
        String negativeProfitCurrencyFlag = negativeProfitConditionVO.getPlatformOrFiatCurrency();

        List<ConditionFirstDepositVO> depositVOS = firstDepositConditionVO.getConditionVOS();

        List<ConditionFirstWithdrawalVO> firstWithdrawalVOS = firstWithdrawalConditionVO.getConditionVOS();
        List<ConditionSignInVO> conditionSignInVOS = signInConditionVO.getConditionVOS();
        List<ConditionNegativeProfitVO> negativeProfitVOS = negativeProfitConditionVO.getConditionVOS();
        if (userInfo != null) {
            Map<String, SiteActivityOrderRecordV2PO> orderRecordPOMap = orderRecordV2POS.stream()
                    .collect(Collectors.toMap(SiteActivityOrderRecordV2PO::getOrderNo, siteActivityOrderRecordV2PO -> siteActivityOrderRecordV2PO));

            Long registerTime = userInfo.getRegisterTime();

            long startRegisterTimeInZone = TimeZoneUtils.getStartOfDayInTimeZone(registerTime, timeZone);

            long curTimeStartInTimeZone = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timeZone);
            long curTimeEndInTimeZone = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timeZone);

            long endRegisterTimeAfter7 = startRegisterTimeInZone + (1000 * 3600 * 24 * 7L) - 1;

            boolean isDoingFlag = false;
            if (curTimeEndInTimeZone < endRegisterTimeAfter7) {
                signInStatus = 80045;
                endRegisterTimeAfter7 = curTimeEndInTimeZone;
                activityConfigDetailVO.setNewbieStatus(1);
                isDoingFlag = true;
            } else {
                allStatus = 30048;
                firstDepositStatus = 30048;
                firstWithdrawalStatus = 30048;
                signInStatus = 30055;
                negativeProfitStatus = 30048;
                activityConfigDetailVO.setNewbieStatus(2);
            }

            Long firstDepositTime = userInfo.getFirstDepositTime();
            BigDecimal firstRechargeAmount = userInfo.getFirstDepositAmount();
            if (firstDepositTime == null) {
                firstRechargeAmount = BigDecimal.ZERO;
            }
            String firstDepositConfigCurrency;
            if ("0".equals(firstDepositCurrencyFlag)) {
                firstDepositConfigCurrency = "WTC";
            } else {
                firstDepositConfigCurrency = userInfo.getMainCurrency();
            }
            ConditionFirstDepositVO conditionFirstDepositVO = depositVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(firstDepositConfigCurrency)).findFirst().orElse(null);
            ConditionFirstDepositRespVO firstDepositRespVO = BeanUtil.copyProperties(conditionFirstDepositVO, ConditionFirstDepositRespVO.class);
            if (firstDepositRespVO == null) {
                log.info("当前站点:{} 没有获取到当前站点配置新手首充信息, 币种:{}", siteCode, userId);
                firstDepositRespVO = new ConditionFirstDepositRespVO();
                firstDepositRespVO.setCurrencyCode(firstDepositConfigCurrency);

            }
            if (isDoingFlag){
                CalculateParamV2 calculateFirstDeposit = new CalculateParamV2();
                calculateFirstDeposit.setRate(currencyRateMap.get(userInfo.getMainCurrency()));
                calculateFirstDeposit.setSourceAmount(firstRechargeAmount);
                calculateFirstDeposit.setSourceCurrencyCode(userInfo.getMainCurrency());
                calculateFirstDeposit.setConditionFirstDepositVO(conditionFirstDepositVO);
                calculateFirstDeposit.setRewardCurrencyCode(firstDepositConfigCurrency);
                calculateFirstDeposit.setNewHandType(1);
                calculateRewardAmount(calculateFirstDeposit);
                if (calculateFirstDeposit.getRewardAmount().compareTo(BigDecimal.ZERO) <= 0 && firstRechargeAmount.compareTo(BigDecimal.ZERO) > 0) {
                    //NOTE 查询数据库，有没有奖励记录，已经充值， 没有奖励， 就是未达标
                    firstDepositStatus = 30048;
                }
            }
            String orderNoOfFirstDeposit = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(),  "1");
            if (orderRecordPOMap.containsKey(orderNoOfFirstDeposit)) {
                firstDepositStatus = 30047;
            }

            firstDepositRespVO.setStatus(firstDepositStatus);
            activityConfigDetailVO.setConditionFirstDepositRespVO(firstDepositRespVO);

            //NOTE 2. 首次提现
            String firstWithdrawalConfigCurrency;
            if ("0".equals(firstWithdrawalCurrencyFlag)) {
                firstWithdrawalConfigCurrency = "WTC";
            } else {
                firstWithdrawalConfigCurrency = userInfo.getMainCurrency();
            }
            ConditionFirstWithdrawalVO conditionFirstWithdrawalVO = firstWithdrawalVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(firstWithdrawalConfigCurrency)).findFirst().orElse(null);

            ConditionFirstWithdrawalRespVO firstWithdrawalRespVO = BeanUtil.copyProperties(conditionFirstWithdrawalVO, ConditionFirstWithdrawalRespVO.class);
            if (firstWithdrawalRespVO == null) {
                log.info("当前站点:{}没有获取到当前站点配置新手首提信息, 币种:{}", siteCode, userId);
                firstWithdrawalRespVO = new ConditionFirstWithdrawalRespVO();
                firstWithdrawalRespVO.setCurrencyCode(firstWithdrawalConfigCurrency);
            }

            if (isDoingFlag){
                //TODO 查询第一次提现记录
                UserWithdrawalRecordRequestVO requestVO = new UserWithdrawalRecordRequestVO();
                requestVO.setSiteCode(siteCode);
                requestVO.setUserId(userId);
                requestVO.setWithdrawalStartTime(startRegisterTimeInZone);
                requestVO.setWithdrawalEndTime(endRegisterTimeAfter7);
                UserDepositWithdrawalResVO firstSuccessWithdrawal = userWithdrawRecordApi.getUserFirstSuccessWithdrawal(requestVO);
                CalculateParamV2 calculateFirstWithdrawal = new CalculateParamV2();
                calculateFirstWithdrawal.setRewardAmount(BigDecimal.ZERO);
                if (firstSuccessWithdrawal != null) {
                    calculateFirstWithdrawal.setRate(currencyRateMap.get(userInfo.getMainCurrency()));
                    calculateFirstWithdrawal.setSourceAmount(BigDecimal.ZERO);
                    calculateFirstWithdrawal.setSourceCurrencyCode(userInfo.getMainCurrency());
                    calculateFirstWithdrawal.setConditionFirstWithdrawalVO(conditionFirstWithdrawalVO);
                    calculateFirstWithdrawal.setRewardCurrencyCode(firstDepositConfigCurrency);
                    calculateFirstWithdrawal.setWithdrawalCount(1);
                    calculateFirstWithdrawal.setNewHandType(2);
                    calculateRewardAmount(calculateFirstWithdrawal);
                    //TODO 查询数据库，有没有奖励记录， 如果有，改成已领取
                    String orderNo = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), calculateFirstWithdrawal.getNewHandType() + "");
                    if (orderRecordPOMap.containsKey(orderNo)) {
                        firstWithdrawalStatus = 30047;
                    } else {
                        if (curTimeEndInTimeZone < endRegisterTimeAfter7) {
                            firstWithdrawalStatus = 30048;
                        }
                    }
                }
            }


            firstWithdrawalRespVO.setStatus(firstWithdrawalStatus);
            activityConfigDetailVO.setConditionFirstWithdrawalRespVO(firstWithdrawalRespVO);


            //NOTE 3. 签到
            String signInConfigCurrency;
            if ("0".equals(signInCurrencyFlag)) {
                signInConfigCurrency = "WTC";
            } else {
                signInConfigCurrency = userInfo.getMainCurrency();
            }
            ConditionSignInVO conditionSignInVO = conditionSignInVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(signInConfigCurrency)).findFirst().orElse(null);

            ConditionSignInRespVO conditionSignInRespVO = BeanUtil.copyProperties(conditionSignInVO, ConditionSignInRespVO.class);
            if (conditionSignInRespVO == null) {
                log.info("当前站点:{}没有获取到当前站点配置新手签到信息, 币种:{}", siteCode, userId);
                conditionSignInRespVO = new ConditionSignInRespVO();
                conditionSignInRespVO.setCurrencyCode(signInConfigCurrency);
            }
            CalculateParamV2 calculateSignIn = new CalculateParamV2();
            conditionSignInRespVO.setCurDayValidBetAmount(BigDecimal.ZERO);
            List<DailyWinLoseResponseVO> dailyWinLoseVOS = new ArrayList<>();

            if (isDoingFlag){
                //NOTE 查询第一天到第七天的投注记录
                String dbZone = TimeZoneUtils.getTimeZoneUTC(timeZone);
                DailyWinLoseVO build = DailyWinLoseVO.builder().startTime(startRegisterTimeInZone).endTime(endRegisterTimeAfter7).siteCode(siteCode).userId(userId).timezone(dbZone).build();
                dailyWinLoseVOS = reportUserWinLoseApi.dailyWinLoseCurrency(build);

                List<Integer> signInList = conditionSignInRespVO.getSignInList();
                conditionSignInRespVO.setCurDayValidBetAmount(BigDecimal.ZERO);
                if (CollUtil.isNotEmpty(dailyWinLoseVOS)){
                    Map<String, DailyWinLoseResponseVO> collect = dailyWinLoseVOS.stream().collect(Collectors.toMap(DailyWinLoseResponseVO::getDayStr, vo -> vo));
                    for (int i = 0; i < 7; i++) {
                        long timestamp = startRegisterTimeInZone + (i * 1000 * 3600 * 24);
                        if (timestamp <= endRegisterTimeAfter7) {
                            String formattedDate = Instant.ofEpochMilli(timestamp).atZone(ZoneId.of(timeZone)).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                            if (collect.containsKey(formattedDate)) {
                                BigDecimal validBetAmount = conditionSignInRespVO.getValidBetAmount();
                                DailyWinLoseResponseVO vo = collect.get(formattedDate);
                                if (vo.getValidBetAmount().compareTo(validBetAmount)>=0) {
                                    signInList.set(i, 1);
                                }
                                if (timestamp == curTimeStartInTimeZone) {
                                    conditionSignInRespVO.setCurDayValidBetAmount(vo.getValidBetAmount());
                                }
                            }
                        }
                    }
                    if (signInList.stream().filter(integer -> integer==1).count()>4){
                        if (curTimeEndInTimeZone < endRegisterTimeAfter7) {
                            signInStatus = 80051;
                        }
                    }
                }
                conditionSignInRespVO.setSignInList(signInList);

                calculateSignIn.setRate(currencyRateMap.get(userInfo.getMainCurrency()));
                calculateSignIn.setSourceAmount(BigDecimal.ZERO);
                calculateSignIn.setSourceCurrencyCode(userInfo.getMainCurrency());
                calculateSignIn.setConditionSignInVO(conditionSignInVO);
                calculateSignIn.setRewardCurrencyCode(signInConfigCurrency);
                calculateSignIn.setNewHandType(3);
                calculateRewardAmount(calculateSignIn);

                //NOTE 查询数据库，有没有奖励记录， 如果有，改成已领取
                String orderNoOfSignIn = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(), calculateSignIn.getNewHandType() + "");
                if (orderRecordPOMap.containsKey(orderNoOfSignIn)) {
                    signInStatus = 30047;
                }
            }

            conditionSignInRespVO.setStatus(signInStatus);
            activityConfigDetailVO.setConditionSignInRespVO(conditionSignInRespVO);

            //NOTE 4. 负盈利
            String negativeProfitConfigCurrency;
            if ("0".equals(negativeProfitCurrencyFlag)) {
                negativeProfitConfigCurrency = "WTC";
            } else {
                negativeProfitConfigCurrency = userInfo.getMainCurrency();
            }
            ConditionNegativeProfitVO conditionNegativeProfitVO = negativeProfitVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(negativeProfitConfigCurrency)).findFirst().orElse(null);
            ConditionNegativeProfitRespVO negativeProfitRespVO = BeanUtil.copyProperties(conditionNegativeProfitVO, ConditionNegativeProfitRespVO.class);
            if (negativeProfitRespVO == null) {
                log.info("当前站点:{}没有获取到当前站点配置新手负盈利信息, 币种:{}", siteCode, userId);
                negativeProfitRespVO = new ConditionNegativeProfitRespVO();
                negativeProfitRespVO.setCurrencyCode(negativeProfitConfigCurrency);
            }
            if (isDoingFlag){
                CalculateParamV2 calculateNegativeProfit = new CalculateParamV2();
                calculateNegativeProfit.setNegativeProfit(BigDecimal.ZERO);
                //NOTE 查询第一天到第七天的总盈利
                if (CollUtil.isNotEmpty(dailyWinLoseVOS)) {
                    BigDecimal reduce = dailyWinLoseVOS.stream().map(dailyWinLoseResponseVO -> dailyWinLoseResponseVO.getBetWinLose())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    calculateNegativeProfit.setNegativeProfit(reduce);
                }

                calculateNegativeProfit.setRate(currencyRateMap.get(userInfo.getMainCurrency()));
                calculateNegativeProfit.setSourceAmount(BigDecimal.ZERO);
                calculateNegativeProfit.setSourceCurrencyCode(userInfo.getMainCurrency());
                calculateNegativeProfit.setConditionNegativeProfitVO(conditionNegativeProfitVO);
                calculateNegativeProfit.setRewardCurrencyCode(negativeProfitConfigCurrency);
                calculateNegativeProfit.setNewHandType(4);
                calculateRewardAmount(calculateNegativeProfit);
            }else {
                //NOTE 查询数据库，有没有奖励记录， 如果有，改成已领取
                String orderNoOfNP = OrderNoUtils.genOrderNo(userId, ActivityTemplateV2Enum.NEW_HAND.getSerialNo(),  "4");
                if (orderRecordPOMap.containsKey(orderNoOfNP)) {
                    negativeProfitStatus = 30047;
                }
            }
            negativeProfitRespVO.setStatus(negativeProfitStatus);
            activityConfigDetailVO.setConditionNegativeProfitRespVO(negativeProfitRespVO);
        } else {
            //NOTE 默认CNY ， 如果没有CNY的，再改WTC
            String defaultCurrency = "CNY";
            String platformCurrency = "WTC";
            ConditionFirstDepositVO conditionFirstDepositVO = depositVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(defaultCurrency)).findFirst().orElse(null);
            if (conditionFirstDepositVO == null) {
                conditionFirstDepositVO = depositVOS.stream()
                        .filter(vo -> vo.getCurrencyCode().equals(platformCurrency)).findFirst().orElse(null);
            }
            ConditionFirstWithdrawalVO conditionFirstWithdrawalVO = firstWithdrawalVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(defaultCurrency)).findFirst().orElse(null);
            if (conditionFirstWithdrawalVO == null) {
                conditionFirstWithdrawalVO = firstWithdrawalVOS.stream()
                        .filter(vo -> vo.getCurrencyCode().equals(platformCurrency)).findFirst().orElse(null);
            }
            ConditionSignInVO conditionSignInVO = conditionSignInVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(defaultCurrency)).findFirst().orElse(null);
            if (conditionSignInVO == null) {
                conditionSignInVO = conditionSignInVOS.stream()
                        .filter(vo -> vo.getCurrencyCode().equals(platformCurrency)).findFirst().orElse(null);
            }
            ConditionNegativeProfitVO conditionNegativeProfitVO = negativeProfitVOS.stream()
                    .filter(vo -> vo.getCurrencyCode().equals(defaultCurrency)).findFirst().orElse(null);
            if (conditionNegativeProfitVO == null) {
                conditionNegativeProfitVO = negativeProfitVOS.stream()
                        .filter(vo -> vo.getCurrencyCode().equals(platformCurrency)).findFirst().orElse(null);
            }
            ConditionFirstDepositRespVO firstDepositRespVO = BeanUtil.copyProperties(conditionFirstDepositVO, ConditionFirstDepositRespVO.class);
            ConditionFirstWithdrawalRespVO firstWithdrawalRespVO = BeanUtil.copyProperties(conditionFirstWithdrawalVO, ConditionFirstWithdrawalRespVO.class);
            ConditionSignInRespVO conditionSignInRespVO = BeanUtil.copyProperties(conditionSignInVO, ConditionSignInRespVO.class);
            ConditionNegativeProfitRespVO negativeProfitRespVO = BeanUtil.copyProperties(conditionNegativeProfitVO, ConditionNegativeProfitRespVO.class);
            if (firstDepositRespVO != null) {
                firstDepositRespVO.setStatus(firstDepositStatus);
            }
            if (firstWithdrawalRespVO != null) {
                firstWithdrawalRespVO.setStatus(firstWithdrawalStatus);
            }
            if (conditionSignInRespVO != null) {
                conditionSignInRespVO.setStatus(signInStatus);
            }
            if (negativeProfitRespVO != null) {
                negativeProfitRespVO.setStatus(negativeProfitStatus);
            }

            activityConfigDetailVO.setConditionFirstDepositRespVO(firstDepositRespVO);
            activityConfigDetailVO.setConditionFirstWithdrawalRespVO(firstWithdrawalRespVO);
            activityConfigDetailVO.setConditionSignInRespVO(conditionSignInRespVO);
            activityConfigDetailVO.setConditionNegativeProfitRespVO(negativeProfitRespVO);
        }
        activityConfigDetailVO.setActivityCondition(allStatus == ResultCode.SUCCESS.getCode());
    }
}
