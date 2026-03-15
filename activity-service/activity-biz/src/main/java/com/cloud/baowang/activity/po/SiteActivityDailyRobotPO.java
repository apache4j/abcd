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
@TableName(value = "site_activity_daily_robot")
public class SiteActivityDailyRobotPO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 活动ID
     */
    private String activityId;

    /**
     * 竞赛ID
     */
    private String activityDailyCompetitionId;


    /**
     * 机器人账号
     */
    private String robotAccount;

    /**
     * 投注金额(流水WTC)
     */
    private BigDecimal robotBetAmount;

    /**
     * 初始化-投注金额(流水WTC)
     */
    private BigDecimal initRobotBetAmount;

    /**
     * 机器人流水最高阀值(流水WTC)
     */
    private BigDecimal maxRobotBetAmount;

    /**
     * 流水增长百分比(%)
     */
    private BigDecimal betGrowthPct;


    /**
     * 是否编辑过（1是,0否）,这个字段每天晚上会重新恢复成0
     */
    private Boolean edit;

    /**
     * 版本号锁
     */
    private Integer version;



}
