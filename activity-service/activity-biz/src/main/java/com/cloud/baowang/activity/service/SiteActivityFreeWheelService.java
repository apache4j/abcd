package com.cloud.baowang.activity.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.DisCountTypeEnum;
import com.cloud.baowang.activity.api.vo.ActivityFreeWheelVO;
import com.cloud.baowang.activity.po.SiteActivityFreeWheelPO;
import com.cloud.baowang.activity.repositories.SiteActivityBaseRepository;
import com.cloud.baowang.activity.repositories.SiteActivityFreeWheelRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/9 13:36
 * @Version: V1.0
 **/
@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityFreeWheelService extends ServiceImpl<SiteActivityFreeWheelRepository, SiteActivityFreeWheelPO> {

    private final SiteActivityBaseRepository siteActivityBaseRepository;

    public boolean insert(ActivityFreeWheelVO activityFreeWheelVO) {
        //
        /*LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapperBase= Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.FREE_WHEEL.getType())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SiteActivityBasePO> siteActivityBasePOS=siteActivityBaseRepository.selectList(lambdaQueryWrapperBase);
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getSiteCode,activityFreeWheelVO.getSiteCode());
        if(!CollectionUtils.isEmpty(siteActivityBasePOS)){
            List<String> activityIds=siteActivityBasePOS.stream().map(SiteActivityBasePO::getId).toList();
            lambdaQueryWrapper.in(SiteActivityFreeWheelPO::getActivityId,activityIds);
        }
        List<SiteActivityFreeWheelPO> siteActivityFreeWheelPOS= this.baseMapper.selectList(lambdaQueryWrapper);
        String weekDays=activityFreeWheelVO.getWeekDays();
        String[] weekDayArray=weekDays.split(CommonConstant.COMMA);
        for(String weekDay:weekDayArray){
            Set<SiteActivityFreeWheelPO> siteActivityFreeWheelPOSet=siteActivityFreeWheelPOS.stream().filter(o->o.getWeekDays().contains(weekDay)).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(siteActivityFreeWheelPOSet)){
                log.info("新增免费旋转活动,站点:{},指定日期:{} 免费旋转活动已存在",activityFreeWheelVO.getSiteCode(),weekDay);
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
        }*/
        SiteActivityFreeWheelPO siteActivityFreeWheelPO = new SiteActivityFreeWheelPO();
        siteActivityFreeWheelPO.setActivityId(activityFreeWheelVO.getId());
        siteActivityFreeWheelPO.setSiteCode(activityFreeWheelVO.getSiteCode());
        siteActivityFreeWheelPO.setWeekDays(activityFreeWheelVO.getWeekDays());
        siteActivityFreeWheelPO.setDiscountType(activityFreeWheelVO.getDiscountType());
        siteActivityFreeWheelPO.setParticipationMode(activityFreeWheelVO.getParticipationMode());
        String freeWheelCondJson = "";
        if (activityFreeWheelVO.getDiscountType() == DisCountTypeEnum.FIX.getValue()) {
            freeWheelCondJson = JSON.toJSONString(activityFreeWheelVO.getFixCondVO());
        } else {
            freeWheelCondJson = JSON.toJSONString(activityFreeWheelVO.getStepCondVOList());
        }
        siteActivityFreeWheelPO.setConditionVal(freeWheelCondJson);
        siteActivityFreeWheelPO.setVenueCode(activityFreeWheelVO.getVenueCode());
        siteActivityFreeWheelPO.setAccessParameters(activityFreeWheelVO.getAccessParameters());
        siteActivityFreeWheelPO.setBetLimitAmount(activityFreeWheelVO.getBetLimitAmount());
        this.baseMapper.insert(siteActivityFreeWheelPO);
        return true;
    }

    public boolean updateInfo(ActivityFreeWheelVO activityFreeWheelVO) {
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getActivityId, activityFreeWheelVO.getId());
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getSiteCode, activityFreeWheelVO.getSiteCode());
        SiteActivityFreeWheelPO siteActivityFreeWheelPODb = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityFreeWheelPODb == null) {
            return false;
        }
        /*LambdaQueryWrapper<SiteActivityBasePO> lambdaQueryWrapperBase= Wrappers.lambdaQuery(SiteActivityBasePO.class)
                .eq(SiteActivityBasePO::getActivityTemplate, ActivityTemplateEnum.FREE_WHEEL.getType())
                .eq(SiteActivityBasePO::getStatus, EnableStatusEnum.ENABLE.getCode());
        List<SiteActivityBasePO> siteActivityBasePOS=siteActivityBaseRepository.selectList(lambdaQueryWrapperBase);

        LambdaQueryWrapper<SiteActivityFreeWheelPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(SiteActivityFreeWheelPO::getSiteCode,activityFreeWheelVO.getSiteCode());
        if(!CollectionUtils.isEmpty(siteActivityBasePOS)){
            List<String> activityIds=siteActivityBasePOS.stream().map(SiteActivityBasePO::getId).toList();
            lambdaQueryWrapper.in(SiteActivityFreeWheelPO::getActivityId,activityIds);
        }
        List<SiteActivityFreeWheelPO> siteActivityFreeWheelPOS= this.baseMapper.selectList(queryWrapper);
        String weekDays=activityFreeWheelVO.getWeekDays();
        String[] weekDayArray=weekDays.split(CommonConstant.COMMA);
        for(String weekDay:weekDayArray){
            siteActivityFreeWheelPOS=siteActivityFreeWheelPOS.stream().filter(o->!o.getActivityId().equals(activityFreeWheelVO.getId())).collect(Collectors.toUnmodifiableList());
            Set<SiteActivityFreeWheelPO> siteActivityFreeWheelPOSet=siteActivityFreeWheelPOS.stream().filter(o->o.getWeekDays().contains(weekDay)).collect(Collectors.toSet());
            if(!CollectionUtils.isEmpty(siteActivityFreeWheelPOSet)){
                log.info("修改免费旋转活动,站点:{},指定日期:{} 免费旋转活动已存在",activityFreeWheelVO.getSiteCode(),weekDay);
                throw new BaowangDefaultException(ResultCode.DATA_IS_EXIST);
            }
        }*/
        SiteActivityFreeWheelPO siteActivityFreeWheelPO = new SiteActivityFreeWheelPO();
        siteActivityFreeWheelPO.setId(siteActivityFreeWheelPODb.getId());
        siteActivityFreeWheelPO.setActivityId(activityFreeWheelVO.getId());
        siteActivityFreeWheelPO.setSiteCode(activityFreeWheelVO.getSiteCode());
        siteActivityFreeWheelPO.setWeekDays(activityFreeWheelVO.getWeekDays());
        siteActivityFreeWheelPO.setDiscountType(activityFreeWheelVO.getDiscountType());
        siteActivityFreeWheelPO.setParticipationMode(activityFreeWheelVO.getParticipationMode());
        String freeWheelCondJson = "";
        if (ActivityDiscountTypeEnum.FIXED_AMOUNT.getType().equals(activityFreeWheelVO.getDiscountType())) {
            freeWheelCondJson = JSON.toJSONString(activityFreeWheelVO.getFixCondVO());
        }
        if (ActivityDiscountTypeEnum.PERCENTAGE.getType().equals(activityFreeWheelVO.getDiscountType())) {
            freeWheelCondJson = JSON.toJSONString(activityFreeWheelVO.getStepCondVOList());
        }
        siteActivityFreeWheelPO.setConditionVal(freeWheelCondJson);
        LambdaUpdateWrapper<SiteActivityFreeWheelPO> lambdaUpdateWrapper = new LambdaUpdateWrapper<SiteActivityFreeWheelPO>();
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getWeekDays, siteActivityFreeWheelPO.getWeekDays());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getDiscountType, siteActivityFreeWheelPO.getDiscountType());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getConditionVal, siteActivityFreeWheelPO.getConditionVal());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getParticipationMode, siteActivityFreeWheelPO.getParticipationMode());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getVenueCode, activityFreeWheelVO.getVenueCode());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getAccessParameters, activityFreeWheelVO.getAccessParameters());
        lambdaUpdateWrapper.set(SiteActivityFreeWheelPO::getBetLimitAmount, activityFreeWheelVO.getBetLimitAmount());
        lambdaUpdateWrapper.eq(SiteActivityFreeWheelPO::getActivityId, siteActivityFreeWheelPO.getActivityId());
        this.update(lambdaUpdateWrapper);
        return true;
    }

    public SiteActivityFreeWheelPO info(String activityId) {
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getActivityId, activityId);
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }


    public void deleteBySiteCode(String siteCode) {
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getSiteCode, siteCode);
        this.baseMapper.delete(lambdaQueryWrapper);
    }

    public List<SiteActivityFreeWheelPO> selectByActivityIds(List<String> activityIds) {
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SiteActivityFreeWheelPO::getActivityId, activityIds);
        return this.baseMapper.selectList(lambdaQueryWrapper);
    }


    public void deleteByActivityId(String activityId) {
        LambdaQueryWrapper<SiteActivityFreeWheelPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SiteActivityFreeWheelPO::getActivityId, activityId);
        this.baseMapper.delete(lambdaQueryWrapper);
    }
}
