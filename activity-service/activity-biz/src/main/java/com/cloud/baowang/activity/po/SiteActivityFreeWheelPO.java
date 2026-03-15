package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 免费旋转活动
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_free_wheel")
public class SiteActivityFreeWheelPO extends SiteBasePO {

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 指定日期 周一、周二等
     */
    private String weekDays;


    /**
     * 优惠方式 0:阶梯次数 1:固定次数
     */
    private Integer discountType;


    /**
     * 参与方式,0.手动参与，1.自动参与
     * {@link ActivityParticipationModeEnum}
     */
    private Integer participationMode;


    /**
     * 匹配条件 json格式 固定次数:{min_deposit_amt,acquire_num} jsonArray格式阶梯次数:{min_deposit_amt,max_deposit_amt,acquire_num}
     */
    private String conditionVal;

    /**
     * 游戏场馆
     */
    private String venueCode;

    /**
     * pp游戏code
     */
    private String accessParameters;

    /**
     * 限注金额
     */
    private BigDecimal betLimitAmount;


}
