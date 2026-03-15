package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包雨活动场次历史表
 * @TableName site_activity_red_bag_session
 */
@TableName(value ="site_activity_red_bag_session")
@Data
public class SiteActivityRedBagSessionPO extends BasePO implements Serializable {

    /**
     * 活动base表id
     */
    private String baseId;

    /**
     * 场次id
     */
    private String sessionId;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 日期 格式 2024-09-01
     */
    private String day;

    /**
     * 站点配置开始时分 20:00
     */
    private String startTimeStr;

    /**
     * 站点配置结束时分 20:00
     */
    private String endTimeStr;

    /**
     * 存款金额
     */
    private BigDecimal depositAmount;

    /**
     * 投注流水
     */
    private BigDecimal betAmount;

    /**
     * 段位要求 配置
     */
    private String rankLimitConfig;

    /**
     * 提前时间 秒
     */
    private Integer advanceTime;

    /**
     * 红包总金额
     */
    private BigDecimal totalAmount;

    /**
     * 红包掉落时间 秒
     */
    private Integer dropTime;

    /**
     * 状态 1 进行中 2 已结束
     */
    private Integer status;

    /**
     * 是否当天最后一场次 1 是 0 否
     */
    private Integer latest;

    /**
     * 场次是否已结算 1 是 0 否
     */
    private Integer settled;

    /**
     * 结算时间
     */
    private Long settleTime;
}