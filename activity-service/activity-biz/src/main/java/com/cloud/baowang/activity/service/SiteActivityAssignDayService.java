package com.cloud.baowang.activity.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityTemplateEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.po.SiteActivityAssignDayPO;
import com.cloud.baowang.activity.po.SiteActivityBasePO;
import com.cloud.baowang.activity.repositories.SiteActivityAssignDayRepository;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.enums.I18MsgKeyEnum;
import com.cloud.baowang.common.core.enums.ResultCode;
import com.cloud.baowang.common.core.exceptions.BaowangDefaultException;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.cloud.baowang.common.core.vo.base.ResponseVO;
import com.cloud.baowang.common.redis.constant.RedisKeyTransUtil;
import com.cloud.baowang.system.api.api.i18n.I18nApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Desciption: 指定存款日期
 * @Author: Ford
 * @Date: 2024/9/9 13:36
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityAssignDayService extends ServiceImpl<SiteActivityAssignDayRepository, SiteActivityAssignDayPO> {

    private final SiteActivityBaseRepository activityBaseRepository;

    private final I18nApi i18nApi;

    public boolean insert(ActivityAssignDayVO activityAssignDayVO) {
        /*LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapperBase=Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.ASSIGN_DAY.getType())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SiteActivityBasePO> siteActivityBasePOS=activityBaseRepository.selectList(lambdaQueryWrapperBase);
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getSiteCode,activityAssignDayVO.getSiteCode());
        if(!CollectionUtils.isEmpty(siteActivityBasePOS)){
            List<String> activityIds=siteActivityBasePOS.stream().map(SiteActivityBasePO::getId).toList();
            lambdaQueryWrapper.in(SiteActivityAssignDayPO::getActivityId,activityIds);
        }
        List<SiteActivityAssignDayPO> siteActivityAssignDayPOS= this.baseMapper.selectList(lambdaQueryWrapper);
        String weekDays=activityAssignDayVO.getWeekDays();
        String[] weekDayArray=weekDays.split(CommonConstant.COMMA);
        //指定存款日期 在历史已生效活动中不存在
        for(String weekDay:weekDayArray){
            Set<SiteActivityAssignDayPO> siteActivityAssignDayPOSet=siteActivityAssignDayPOS.stream().filter(o->o.getWeekDays().contains(weekDay)).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(siteActivityAssignDayPOSet)){
                log.info("新增指定存款日活动,站点:{},指定日期:{} 活动已存在",activityAssignDayVO.getSiteCode(),weekDay);
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
                // return false;
            }
        }*/
        SiteActivityAssignDayPO siteActivityAssignDayPO = new SiteActivityAssignDayPO();
        siteActivityAssignDayPO.setActivityId(activityAssignDayVO.getId());
        siteActivityAssignDayPO.setSiteCode(activityAssignDayVO.getSiteCode());
        siteActivityAssignDayPO.setWeekDays(activityAssignDayVO.getWeekDays());
        siteActivityAssignDayPO.setDiscountType(activityAssignDayVO.getDiscountType());
        siteActivityAssignDayPO.setDistributionType(activityAssignDayVO.getDistributionType());
        siteActivityAssignDayPO.setParticipationMode(activityAssignDayVO.getParticipationMode());

        siteActivityAssignDayPO.setVenueCode(activityAssignDayVO.getVenueCode());
        siteActivityAssignDayPO.setAccessParameters(activityAssignDayVO.getAccessParameters());
        siteActivityAssignDayPO.setBetLimitAmount(activityAssignDayVO.getBetLimitAmount());

        siteActivityAssignDayPO.setVenueType(activityAssignDayVO.getVenueType());
        String freeWheelCondJson = "";
        // 场馆类型为空
        if (!StringUtils.hasText(activityAssignDayVO.getVenueType())) {
            if (Objects.equals(activityAssignDayVO.getDiscountType(), ActivityDiscountTypeEnum.PERCENTAGE.getType())) {
                freeWheelCondJson = JSON.toJSONString(activityAssignDayVO.getPercentCondVO());
            } else {
                freeWheelCondJson = JSON.toJSONString(activityAssignDayVO.getFixCondVOList());
            }
        } else {
            List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS = activityAssignDayVO.getActivityAssignDayVenueVOS();
            if (activityAssignDayVenueVOS == null || activityAssignDayVenueVOS.isEmpty()) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            for (ActivityAssignDayVenueVO activityAssignDayVenueVO : activityAssignDayVenueVOS) {
                if (!activityAssignDayVenueVO.validate()) {
                    log.info("新增指定日存款活动:,参数异常:{}", activityAssignDayVO);
                    throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                }
            }

            Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
            List<ActivityAssignDayVenueDTO>  inserts = new ArrayList<>();
            for (ActivityAssignDayVenueVO activityAssignDayVenueVO : activityAssignDayVenueVOS) {
                Integer discountType = activityAssignDayVenueVO.getDiscountType();
                ActivityAssignDayVenueDTO insert = new ActivityAssignDayVenueDTO();

                if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(discountType)) {
                    //百分比
                    if (activityAssignDayVenueVO.getPercentCondVO() == null) {
                        throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
                    }
                    //conditionValue = JSON.toJSONString(activity.getPercentageVO());
                } else {
                    //固定金额
                    /*List<ActivityAssignDayCondVO> fixedAmountVOS = activityAssignDayVenueVO.getFixCondVOList();
                    activityAssignDayVenueVOS.validate();*/
                    //金额区间校验
                    //conditionValue = JSON.toJSONString(fixedAmountVOS);
                }
                // 活动规则
                String activityRuleI18 = RedisKeyTransUtil.getI18nDynamicKey(I18MsgKeyEnum.ACTIVITY_BASE_RULE.getCode());
                activityAssignDayVenueVO.setActivityRuleI18nCode(activityRuleI18);

                i18nData.put(activityRuleI18, activityAssignDayVenueVO.getActivityRuleI18nCodeList());
                BeanUtils.copyProperties(activityAssignDayVenueVO, insert);
                inserts.add(insert);
            }
            // 插入i8
            ResponseVO<Boolean> i18Bool = i18nApi.insert(i18nData);
            if (!i18Bool.isOk() || !i18Bool.getData()) {
                throw new BaowangDefaultException(ResultCode.SYSTEM_ERROR);
            }
            freeWheelCondJson = JSON.toJSONString(inserts);
        }

        siteActivityAssignDayPO.setConditionVal(freeWheelCondJson);
        this.baseMapper.insert(siteActivityAssignDayPO);
        return true;
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

    public boolean updateInfo(ActivityAssignDayVO activityAssignDayVO) {
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getActivityId, activityAssignDayVO.getId());
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getSiteCode, activityAssignDayVO.getSiteCode());
        SiteActivityAssignDayPO siteActivityAssignDayPODb = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityAssignDayPODb == null) {
            return false;
        }
        /*LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapperBase=Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.ASSIGN_DAY.getType())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SiteActivityBasePO> siteActivityBasePOS=activityBaseRepository.selectList(lambdaQueryWrapperBase);

        LambdaQueryWrapper<SiteActivityAssignDayPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityAssignDayPO::getSiteCode,activityAssignDayVO.getSiteCode());
        if(!CollectionUtils.isEmpty(siteActivityBasePOS)){
            List<String> activityIds=siteActivityBasePOS.stream().map(SiteActivityBasePO::getId).toList();
            lambdaQueryWrapper.in(SiteActivityAssignDayPO::getActivityId,activityIds);
        }
        List<SiteActivityAssignDayPO> siteActivityAssignDayPOS= this.baseMapper.selectList(queryWrapper);
        String weekDays=activityAssignDayVO.getWeekDays();
        String[] weekDayArray=weekDays.split(CommonConstant.COMMA);
        //指定存款日期 在历史活动中不存在
        for(String weekDay:weekDayArray){
            siteActivityAssignDayPOS=siteActivityAssignDayPOS.stream().filter(o-> !Objects.equals(o.getActivityId(), activityAssignDayVO.getId())).collect(Collectors.toUnmodifiableList());
            Set<SiteActivityAssignDayPO> siteActivityAssignDayPOSet=siteActivityAssignDayPOS.stream().filter(o->o.getWeekDays().contains(weekDay)).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(siteActivityAssignDayPOSet)){
                log.info("修改指定存款日,站点:{},指定日期:{} 活动已存在",activityAssignDayVO.getSiteCode(),weekDay);
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
        }*/

        SiteActivityAssignDayPO siteActivityAssignDayPO = new SiteActivityAssignDayPO();
        siteActivityAssignDayPO.setId(siteActivityAssignDayPODb.getId());
        siteActivityAssignDayPO.setActivityId(activityAssignDayVO.getId());
        siteActivityAssignDayPO.setSiteCode(activityAssignDayVO.getSiteCode());
        siteActivityAssignDayPO.setWeekDays(activityAssignDayVO.getWeekDays());
        siteActivityAssignDayPO.setDiscountType(activityAssignDayVO.getDiscountType());
        siteActivityAssignDayPO.setDistributionType(activityAssignDayVO.getDistributionType());
        siteActivityAssignDayPO.setParticipationMode(activityAssignDayVO.getParticipationMode());

        siteActivityAssignDayPO.setVenueCode(activityAssignDayVO.getVenueCode());
        siteActivityAssignDayPO.setAccessParameters(activityAssignDayVO.getAccessParameters());
        siteActivityAssignDayPO.setBetLimitAmount(activityAssignDayVO.getBetLimitAmount());

        siteActivityAssignDayPO.setVenueType(activityAssignDayVO.getVenueType());
        String freeWheelCondJson = "";
        if (ObjectUtil.isEmpty(activityAssignDayVO.getVenueType())) {
            if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(activityAssignDayVO.getDiscountType())) {
                freeWheelCondJson = JSON.toJSONString(activityAssignDayVO.getFixCondVOList());
            }
            if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityAssignDayVO.getDiscountType())) {
                freeWheelCondJson = JSON.toJSONString(activityAssignDayVO.getPercentCondVO());
            }
        } else {
            List<ActivityAssignDayVenueVO> activityAssignDayVenueVOS = activityAssignDayVO.getActivityAssignDayVenueVOS();
            if (activityAssignDayVenueVOS == null || activityAssignDayVenueVOS.isEmpty()) {
                throw new BaowangDefaultException(ResultCode.PARAM_ERROR);
            }
            Map<String, List<I18nMsgFrontVO>> i18nData = new HashMap<>();
            for (ActivityAssignDayVenueVO activityAssignDayVenueVO : activityAssignDayVenueVOS) {
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
            freeWheelCondJson = JSON.toJSONString(activityAssignDayVenueVOS);
        }
        siteActivityAssignDayPO.setConditionVal(freeWheelCondJson);
        LambdaUpdateWrapper<SiteActivityAssignDayPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivityAssignDayPO>();
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getWeekDays, siteActivityAssignDayPO.getWeekDays());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getDiscountType, siteActivityAssignDayPO.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getConditionVal, siteActivityAssignDayPO.getConditionVal());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getParticipationMode, siteActivityAssignDayPO.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getDistributionType, siteActivityAssignDayPO.getDistributionType());

        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getVenueCode, siteActivityAssignDayPO.getVenueCode());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getAccessParameters, siteActivityAssignDayPO.getAccessParameters());
        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getBetLimitAmount, siteActivityAssignDayPO.getBetLimitAmount());

        lambdaUpdateWrapper.set(SiteActivityAssignDayPO::getVenueType, siteActivityAssignDayPO.getVenueType());
        lambdaUpdateWrapper.eq(SiteActivityAssignDayPO::getActivityId, siteActivityAssignDayPO.getActivityId());

        this.update(lambdaUpdateWrapper);
        return true;
    }

    public SiteActivityAssignDayPO info(String activityId) {
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getActivityId, activityId);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }


    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getSiteCode, siteCode);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    public List<SiteActivityAssignDayPO> selectByActivityIds(List<String> activityIds) {
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SiteActivityAssignDayPO::getActivityId, activityIds);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }

    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityAssignDayPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityAssignDayPO::getActivityId, activityId);
        this.baseMapper.delete(lambdaQueryWrapper);
    }
}
