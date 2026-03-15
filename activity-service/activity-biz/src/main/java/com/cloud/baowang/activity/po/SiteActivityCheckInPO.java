package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 转盘
 */
@Data
@NoArgsConstructor
@TableName("site_activity_check_in")
public class SiteActivityCheckInPO extends SiteBasePO  {


    /**
     * 活动主键id
     */
    private String baseId;

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 存款金额
     */
    private BigDecimal depositAmount;

    /**
     * 投注金额
     */
    private BigDecimal betAmount;

    /**
     * 周奖励配置
     */
    private String rewardWeek;



    /**
     * 月奖励配置
     */
    private String rewardMonth;


    /**
     * 补签开关（0：关闭，1：开启）
     */
    private Integer checkInSwitch;

    /**
     * 当日存款金额
     */
    private BigDecimal depositAmountToday;

    /**
     * 当日投注金额
     */
    private BigDecimal betAmountToday;

    /**
     * 极光推送开关（0：关闭，1：开启）
     */
    private Integer pushSwitch;

    /**
     * 极光推送终端（如：ANDROID、IOS）
     */
    private String pushTerminal;

    /**
     * 累计配置
     */
    private String rewardTotal;

    /**
     * 存款金额
     */
    private BigDecimal makeDepositAmount;

    /**
     * 有效投注金额
     */
    private BigDecimal makeBetAmount;

    /**
     * 补签次数限制
     */
    private Integer makeupLimit;

    /**
     * 免费旋转
     */
    private String freeWheelPic;

    /**
     * 转盘
     */
    private String spinWheelPic;

    /**
     * 奖金
     */
    private String amountPic;



}
