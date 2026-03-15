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
@TableName(value = "site_activity_daily_competition")
public class SiteActivityDailyCompetitionPO extends BasePO {


    private String competitionI18nCode;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 唯一ID
     */
    private String comNo;


    /**
     * 场馆类型
     */
    private Integer venueType;

    /**
     * 场馆CODE
     */
    private String venueCode;

    /**
     * 初始化金额
     */
    private BigDecimal initAmount;

    /**
     * 场馆百分比
     */
    private BigDecimal venuePercentage;

    /**
     * 优惠方式: 0 - 百分比, 1 - 固定金额
     */
    private Integer activityDiscountType;


}
