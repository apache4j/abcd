package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 转盘
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_spin_wheel")
public class ActivitySpinWheelPO extends SiteBasePO  {


    /**
     * 存款奖励次数
     */
    private Integer depositTimes;


    /**
     * 存款金额
     */
    private BigDecimal depositAmount;

    /**
     * 投注流水
     */
    private BigDecimal betAmount;
    /**
     * 投注奖励次数
     */
    private Integer betTimes;
    /**
     * 转盘初始获得金额
     */
    private BigDecimal initAmount;

    /**
     * 每位会员每日可领取次数上限，0-当选择全部会员的时候，1-是根据VIP等级限制会员领取次数
     */
    private Integer maxTimeType;
    /**
     * 每位会员每日可领取次数上限，当选择全部会员的时候
     */
    private Integer maxTimes;

    /**
     * 基础活动主键
     */
    private String baseId;


}
