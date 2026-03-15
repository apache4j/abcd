package com.cloud.baowang.activity.service.v2;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.vo.FixedAmountVO;
import com.cloud.baowang.activity.api.vo.v2.ActivityAssignDayV2VO;
import com.cloud.baowang.activity.api.vo.v2.ActivityAssignDayVenueV2VO;
import com.cloud.baowang.activity.po.SiteActivityAssignDayPO;
import com.cloud.baowang.activity.po.v2.SiteActivityAssignDayV2PO;
import com.cloud.baowang.activity.repositories.v2.SiteActivityAssignDayV2Repository;
import com.cloud.baowang.activity.repositories.v2.SiteActivityBaseV2Repository;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityAssignDayV2Service extends ServiceImpl<SiteActivityAssignDayV2Repository, SiteActivityAssignDayV2PO> {


    private final I18nApi i18nApi;

    public boolean insert(ActivityAssignDayV2VO activityAssignDayVO) {

        SiteActivityAssignDayV2PO siteActivityAssignDayPO = new SiteActivityAssignDayV2PO();
        siteActivityAssignDayPO.setActivityId(activityAssignDayVO.getId());
        siteActivityAssignDayPO.setSiteCode(activityAssignDayVO.getSiteCode());
        siteActivityAssignDayPO.setWeekDays(activityAssignDayVO.getWeekDays());
        siteActivityAssignDayPO.setDistributionType(activityAssignDayVO.getDistributionType());
        siteActivityAssignDayPO.setParticipationMode(activityAssignDayVO.getParticipationMode());
        siteActivityAssignDayPO.setVenueType(activityAssignDayVO.getVenueType());
        List<ActivityAssignDayVenueV2VO> list = activityAssignDayVO.getActivityAssignDayVenueVOS();
        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        for (ActivityAssignDayVenueV2VO activityAssignDayVenueVO : list) {
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityAssignDayVenueVO.getDiscountType())) {
                if (CollUtil.isEmpty(activityAssignDayVenueVO.getPercentCondVO())) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            } else {
                if (CollUtil.isEmpty(activityAssignDayVenueVO.getFixCondVOList())) {
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }
            // 活动规则
            String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
            activityAssignDayVenueVO.setActivityRuleI18nCode(activityRuleI18);
            i18nData.put(activityRuleI18, activityAssignDayVenueVO.getActivityRuleI18nCodeList());
        }
        // 插入i8
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        siteActivityAssignDayPO.setConditionVal(JSON.toJSONString(list));
        this.baseMapper.insert(siteActivityAssignDayPO);
        return true;
    }


    public boolean updateInfo(ActivityAssignDayV2VO activityAssignDayVO) {
        LambdaQueryWrapper<SiteActivityAssignDayV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayV2PO::getActivityId, activityAssignDayVO.getId());
        lambdaQueryWrapper.eq(SiteActivityAssignDayV2PO::getSiteCode, activityAssignDayVO.getSiteCode());
        SiteActivityAssignDayV2PO siteActivityAssignDayPODb = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityAssignDayPODb == null) {
            return false;
        }

        SiteActivityAssignDayPO siteActivityAssignDayPO = new SiteActivityAssignDayPO();
        siteActivityAssignDayPO.setId(siteActivityAssignDayPODb.getId());
        siteActivityAssignDayPO.setActivityId(activityAssignDayVO.getId());
        siteActivityAssignDayPO.setSiteCode(activityAssignDayVO.getSiteCode());
        siteActivityAssignDayPO.setWeekDays(activityAssignDayVO.getWeekDays());
        siteActivityAssignDayPO.setDistributionType(activityAssignDayVO.getDistributionType());
        siteActivityAssignDayPO.setParticipationMode(activityAssignDayVO.getParticipationMode());

        siteActivityAssignDayPO.setVenueType(activityAssignDayVO.getVenueType());

        List<ActivityAssignDayVenueV2VO> activityAssignDayVenueVOS = activityAssignDayVO.getActivityAssignDayVenueVOS();

        Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
        for (ActivityAssignDayVenueV2VO activityAssignDayVenueVO : activityAssignDayVenueVOS) {
            // 设置互动规则
            String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
            activityAssignDayVenueVO.setActivityRuleI18nCode(activityRuleI18);
            i18nData.put(activityRuleI18, activityAssignDayVenueVO.getActivityRuleI18nCodeList());
        }
        // 插入i8
        ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
        if (!i18Bool.isOk() || !i18Bool.getData()) {
            throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
        }
        LambdaUpdateWrapper<SiteActivityAssignDayV2PO> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getWeekDays, siteActivityAssignDayPO.getWeekDays());
        //lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getDiscountType, siteActivityAssignDayPO.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getConditionVal, JSON.toJSONString(activityAssignDayVO.getActivityAssignDayVenueVOS()));
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getParticipationMode, siteActivityAssignDayPO.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getDistributionType, siteActivityAssignDayPO.getDistributionType());

        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getVenueCode, siteActivityAssignDayPO.getVenueCode());
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getAccessParameters, siteActivityAssignDayPO.getAccessParameters());
        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getBetLimitAmount, siteActivityAssignDayPO.getBetLimitAmount());

        lambdaUpdateWrapper.set(SiteActivityAssignDayV2PO::getVenueType, siteActivityAssignDayPO.getVenueType());
        lambdaUpdateWrapper.eq(SiteActivityAssignDayV2PO::getActivityId, siteActivityAssignDayPO.getActivityId());

        this.update(lambdaUpdateWrapper);
        return true;
    }

    public SiteActivityAssignDayV2PO info(String activityId) {
        LambdaQueryWrapper<SiteActivityAssignDayV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayV2PO::getActivityId, activityId);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }


    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityAssignDayV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayV2PO::getSiteCode, siteCode);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    public List<SiteActivityAssignDayV2PO> selectByActivityIds(List<String> activityIds) {
        LambdaQueryWrapper<SiteActivityAssignDayV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityAssignDayV2PO>();
        lambdaQueryWrapper.in(SiteActivityAssignDayV2PO::getActivityId, activityIds);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityAssignDayV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayV2PO::getActivityId, activityId);
        this.baseMapper.delete(lambdaQueryWrapper);
    }
}
