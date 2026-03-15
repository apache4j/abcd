package com.cloud.baowang.activity.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.SiteActivityEventRecordPO;
import com.cloud.baowang.activity.repositories.SiteActivityEventRecordRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SiteActivityEventRecordService extends ServiceImpl<SiteActivityEventRecordRepository, SiteActivityEventRecordPO> {


    public Long toActivityEventRecordCount(SiteActivityEventRecordQueryParam param) {
        LambdaQueryWrapper<SiteActivityEventRecordPO> wrapper = SiteActivityEventRecordPO.getWrapper(param);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 找出已经参与活动的用户列表
     *
     * @return 活动用户列表
     */
    public Page<SiteActivityEventRecordPO> getPageEventRecord(Page<SiteActivityEventRecordPO> page, SiteActivityEventRecordQueryParam siteActivityEventRecordPO) {
        LambdaQueryWrapper<SiteActivityEventRecordPO> wrapper = SiteActivityEventRecordPO.getWrapper(siteActivityEventRecordPO)
                .orderByDesc(SiteActivityEventRecordPO::getCreatedTime);
        return baseMapper.selectPage(page, wrapper);
    }


    /**
     * 判断当前用户是否参加活动
     * 只要参加自动派发
     * 针对首存、次存活动
     *
     * @param siteActivityEventRecordQueryParam 请求参数
     * @return
     */
    public boolean permitSendReward(SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam) {
        LambdaQueryWrapper<SiteActivityEventRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordPO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getSiteCode, siteActivityEventRecordQueryParam.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getUserId, siteActivityEventRecordQueryParam.getUserId());
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getActivityTemplate, siteActivityEventRecordQueryParam.getActivityTemplate());
        SiteActivityEventRecordPO siteActivityEventRecordPO = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityEventRecordPO == null) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前用户是否参加活动
     * 指定存款日期活动、免费旋转活动
     *
     * @param siteActivityEventRecordQueryParam 请求参数
     * @return
     */
    public boolean hasPermitSendReward(SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam) {
        LambdaQueryWrapper<SiteActivityEventRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordPO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getSiteCode, siteActivityEventRecordQueryParam.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getUserId, siteActivityEventRecordQueryParam.getUserId());
        if (StringUtils.hasText(siteActivityEventRecordQueryParam.getActivityId())) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getActivityId, siteActivityEventRecordQueryParam.getActivityId());
        }
        if (StringUtils.hasText(siteActivityEventRecordQueryParam.getActivityTemplate())) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getActivityTemplate, siteActivityEventRecordQueryParam.getActivityTemplate());
        }
        if (siteActivityEventRecordQueryParam.getDay() != null) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getDay, siteActivityEventRecordQueryParam.getDay());
        }
        SiteActivityEventRecordPO siteActivityEventRecordPO = this.baseMapper.selectOne(lambdaQueryWrapper);
        if (siteActivityEventRecordPO == null) {
            return true;
        }
        return false;
    }

    public List<String> permitSendRewardUserIds(String siteCode, String activityId) {
        LambdaQueryWrapper<SiteActivityEventRecordPO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordPO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getSiteCode, siteCode);
        lambdaQueryWrapper.eq(SiteActivityEventRecordPO::getActivityId, activityId);
        List<SiteActivityEventRecordPO> siteActivityEventRecordPOList = this.baseMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(siteActivityEventRecordPOList)) {
            return Lists.newArrayList();
        }
        return siteActivityEventRecordPOList.stream().map(SiteActivityEventRecordPO::getUserId).toList();
    }
}
