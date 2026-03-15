package com.cloud.baowang.report.api.vo.report;


import lombok.Data;

@Data
public class ReportBetUserRemainingResVO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 日期串（例： 2025-0-01）
     */
    private String dayStr;

    /**
     * 日期开始时间戳
     */
    private Long dayMillis;

    /**
     * 注册人数
     */
    private Long registerCount;

    /**
     * 次日投注过的玩家数
     */
    private Integer secondDayBetUserCount;

    /**
     * 次日投注过的玩家比例
     */
    private Double secondDayBetUserRate;

    /**
     * 三日投注过的玩家数
     */
    private Integer threeDayBetUserCount;

    /**
     * 三日投注过的玩家比例
     */
    private Double threeDayBetUserRate;

    /**
     * 七日投注过的玩家数
     */
    private Integer sevenDayBetUserCount;

    /**
     * 七日投注过的玩家比例
     */
    private Double sevenDayBetUserRate;

    /**
     * 15日投注过的玩家数
     */
    private Integer fifteenDayBetUserCount;

    /**
     * 15日投注过的玩家比例
     */
    private Double fifteenDayBetUserRate;

    /**
     * 30日投注过的玩家数
     */
    private Integer thirtyDayBetUserCount;

    /**
     * 30日投注过的玩家比例
     */
    private Double thirtyDayBetUserRate;
}
