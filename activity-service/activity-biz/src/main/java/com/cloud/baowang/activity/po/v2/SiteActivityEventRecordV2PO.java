package com.cloud.baowang.activity.po.v2;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.cloud.baowang.activity.api.enums.ActivityCalculateTypeEnum;
import com.cloud.baowang.activity.param.SiteActivityEventRecordQueryParam;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会员活动参与记录
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "site_activity_event_record_v2")
public class SiteActivityEventRecordV2PO extends BasePO implements Serializable {
    /**
     * 站点code
     */
    private String siteCode;


    /**
     * 站点code
     */
    private String code;

    /**
     * 所属活动id
     */
    private String activityId;

    /**
     * 当天-开始的时间戳
     */
    private Long day;

    /**
     * 模板
     */
    private String activityTemplate;

    /**
     * userId
     */
    private String userId;

    /**
     * 活动账号
     */
    private String userAccount;

    /**
     * VIP 等级
     */
    private Integer vipRank;

    /**
     * 设备号
     */
    private String deviceNo;

    /**
     * IP
     */
    private String ip;

    /**
     * 结算周期: 0 - 日结, 1 - 周结, 2 - 月结
     */
    private Integer calculateType;

    /**
     * 发放状态,0=未发放，1=已发放
     */
    private Integer status;

    public static LambdaQueryWrapper<SiteActivityEventRecordV2PO> getWrapper(SiteActivityEventRecordQueryParam siteActivityEventRecordPO) {
        LambdaQueryWrapper<SiteActivityEventRecordV2PO> wrapper = Wrappers.lambdaQuery(SiteActivityEventRecordV2PO.class)
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getUserId()), SiteActivityEventRecordV2PO::getUserId,
                        siteActivityEventRecordPO.getUserId())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getActivityId()), SiteActivityEventRecordV2PO::getActivityId,
                        siteActivityEventRecordPO.getActivityId())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getCalculateType()), SiteActivityEventRecordV2PO::getCalculateType,
                        siteActivityEventRecordPO.getCalculateType())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getSiteCode()), SiteActivityEventRecordV2PO::getSiteCode, siteActivityEventRecordPO.getSiteCode())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getStatus()), SiteActivityEventRecordV2PO::getStatus, siteActivityEventRecordPO.getStatus())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getActivityTemplate()), SiteActivityEventRecordV2PO::getActivityTemplate, siteActivityEventRecordPO.getActivityTemplate())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getIp()), SiteActivityEventRecordV2PO::getIp, siteActivityEventRecordPO.getIp())
                .eq(ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getDay()), SiteActivityEventRecordV2PO::getDay, siteActivityEventRecordPO.getDay())
                .in(CollectionUtils.isNotEmpty(siteActivityEventRecordPO.getDayList()), SiteActivityEventRecordV2PO::getDay, siteActivityEventRecordPO.getDayList());

        Long startTime = null;
        Long endTime = null;
        String timezone = siteActivityEventRecordPO.getTimezone();
        if (ObjectUtil.isNotEmpty(siteActivityEventRecordPO.getCalculateType())) {
            if (siteActivityEventRecordPO.getCalculateType().equals(ActivityCalculateTypeEnum.DAY.getCode())) {
                startTime = TimeZoneUtils.getStartOfDayInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfDayInTimeZone(System.currentTimeMillis(), timezone);
            } else if (siteActivityEventRecordPO.getCalculateType().equals(ActivityCalculateTypeEnum.WEEK.getCode())) {
                startTime = TimeZoneUtils.getStartOfWeekInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfWeekInTimeZone(System.currentTimeMillis(), timezone);
            } else if (siteActivityEventRecordPO.getCalculateType().equals(ActivityCalculateTypeEnum.MONTH.getCode())) {
                startTime = TimeZoneUtils.getStartOfMonthInTimeZone(System.currentTimeMillis(), timezone);
                endTime = TimeZoneUtils.getEndOfMonthInTimeZone(System.currentTimeMillis(), timezone);
            }
        }
        wrapper.ge(ObjectUtil.isNotEmpty(startTime), SiteActivityEventRecordV2PO::getCreatedTime, startTime)
                .le(ObjectUtil.isNotEmpty(endTime), SiteActivityEventRecordV2PO::getCreatedTime, endTime);

        return wrapper;
    }

}
