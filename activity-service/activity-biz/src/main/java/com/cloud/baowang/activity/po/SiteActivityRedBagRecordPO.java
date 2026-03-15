package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 红包雨活动红包记录表
 *
 * @TableName site_activity_red_bag_record
 */
@TableName(value = "site_activity_red_bag_record")
@Data
public class SiteActivityRedBagRecordPO extends BasePO implements Serializable {

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
     * 用户id
     */
    private String userId;

    /**
     * 用户编码
     */
    private String userAccount;
    /**
     * 抢红包时间
     */
    private Long grabTime;
    /**
     * 发放时间
     */
    private Long receiveTime;

    /**
     * 红包金额
     */
    private BigDecimal redbagAmount;

    /**
     * 奖池剩余金额
     */
    private BigDecimal remainingAmount;

    /**
     * 状态 0 未发放 1 已发放
     */
    private Integer status;
}