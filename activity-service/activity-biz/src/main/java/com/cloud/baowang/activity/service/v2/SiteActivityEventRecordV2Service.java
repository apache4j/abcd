package com.cloud.baowang.activity.service.v2;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.activity.po.v2.SiteActivityEventRecordV2PO;
import com.cloud.baowang.activity.repositories.v2.SiteActivityEventRecordV2Repository;
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
public class SiteActivityEventRecordV2Service extends ServiceImpl<SiteActivityEventRecordV2Repository, SiteActivityEventRecordV2PO> {


    public Long toActivityEventRecordCount(SiteActivityEventRecordQueryParam param) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> wrapper = SiteActivityEventRecordV2PO.getWrapper(param);
        return baseMapper.selectCount(wrapper);
    }

    /**
     * 找出已经参与活动的用户列表
     *
     * @return 活动用户列表
     */
    public Page<SiteActivityEventRecordV2PO> getPageEventRecord(Page<SiteActivityEventRecordV2PO> page, SiteActivityEventRecordQueryParam siteActivityEventRecordPO) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> wrapper = SiteActivityEventRecordV2PO.getWrapper(siteActivityEventRecordPO)
                .orderByDesc(SiteActivityEventRecordV2PO::getCreatedTime);
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
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordV2PO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getSiteCode, siteActivityEventRecordQueryParam.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getUserId, siteActivityEventRecordQueryParam.getUserId());
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityTemplate, siteActivityEventRecordQueryParam.getActivityTemplate());
        SiteActivityEventRecordV2PO siteActivityEventRecordPO = this.baseMapper.selectOne(lambdaQueryWrapper);
        return siteActivityEventRecordPO != null;
    }

    /**
     * 判断当前用户是否参加活动
     * 指定存款日期活动、免费旋转活动
     *
     * @param siteActivityEventRecordQueryParam 请求参数
     * @return
     */
    public boolean hasPermitSendReward(SiteActivityEventRecordQueryParam siteActivityEventRecordQueryParam) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordV2PO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getSiteCode, siteActivityEventRecordQueryParam.getSiteCode());
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getUserId, siteActivityEventRecordQueryParam.getUserId());
        if (StringUtils.hasText(siteActivityEventRecordQueryParam.getActivityId())) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityId, siteActivityEventRecordQueryParam.getActivityId());
        }
        if (StringUtils.hasText(siteActivityEventRecordQueryParam.getActivityTemplate())) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityTemplate, siteActivityEventRecordQueryParam.getActivityTemplate());
        }
        if (siteActivityEventRecordQueryParam.getDay() != null) {
            lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getDay, siteActivityEventRecordQueryParam.getDay());
        }
        SiteActivityEventRecordV2PO siteActivityEventRecordPO = this.baseMapper.selectOne(lambdaQueryWrapper);
        return siteActivityEventRecordPO == null;
    }

    public List<SiteActivityEventRecordV2PO> permitSendRewardUserIds(String siteCode, List<String> userIdList, long dayStartTime) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordV2PO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getSiteCode, siteCode);
        //lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityId, activityId);
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getDay, dayStartTime);

        lambdaQueryWrapper.in(SiteActivityEventRecordV2PO::getUserId, userIdList);

        List<SiteActivityEventRecordV2PO> siteActivityEventRecordPOList = this.baseMapper.selectList(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(siteActivityEventRecordPOList)) {
            return Lists.newArrayList();
        }
        return siteActivityEventRecordPOList;
    }

    public SiteActivityEventRecordV2PO getByUserIdAndDay(String siteCode, String userId, long dayStartTime, String activityTemplate) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> lambdaQueryWrapper = new LambdaQueryWrapper<SiteActivityEventRecordV2PO>();
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getSiteCode, siteCode);
        //lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityId, activityId);
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getActivityTemplate, activityTemplate);
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getUserId, userId);
        lambdaQueryWrapper.eq(SiteActivityEventRecordV2PO::getDay, dayStartTime);
        lambdaQueryWrapper.last("limit 1");
        return this.baseMapper.selectOne(lambdaQueryWrapper);
    }
}
