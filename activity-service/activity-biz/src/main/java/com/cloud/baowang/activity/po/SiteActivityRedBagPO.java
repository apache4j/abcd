package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包雨活动配置
 * @TableName site_activity_red_bag
 */
@TableName(value ="site_activity_red_bag")
@Data
public class SiteActivityRedBagPO extends BasePO implements Serializable {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 存款金额
     */
    private BigDecimal depositAmount;

    /**
     * 投注流水
     */
    private BigDecimal betAmount;

    /**
     * 段位要求 vip_rank_code 数组
     */
    private String rankLimit;

    /**
     * 红包雨场次开始时间 数组
     */
    private String sessionStartTime;

    /**
     * 红包雨场次结束时间 数组
     */
    private String sessionEndTime;

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
     * 活动主键id
     */
    private String baseId;

    /**
     * 红包雨开启job id 多个用逗号拼接
     */
    private String startJobId;

    /**
     * 红包雨结束job id 多个用逗号拼接
     */
    private String endJobId;
}