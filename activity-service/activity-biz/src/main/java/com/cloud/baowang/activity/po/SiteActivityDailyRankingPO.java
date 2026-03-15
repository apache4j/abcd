package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName(value = "site_activity_daily_ranking")
public class SiteActivityDailyRankingPO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 所属活动-详情ID
     */
    private String activityDailyCompetitionId;

    /**
     * 排名
     */
    private Integer ranking;

    /**
     * 活动详情配置
     */
    private String activityDetail;




}
