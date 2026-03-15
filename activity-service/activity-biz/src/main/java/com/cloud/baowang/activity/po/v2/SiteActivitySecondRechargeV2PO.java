package com.cloud.baowang.activity.po.v2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 次存活动基础实体
 */
@Data
@TableName(value = "site_activity_second_recharge_v2")
public class SiteActivitySecondRechargeV2PO extends BasePO implements Serializable {
    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 所属活动id
     */
    private Long activityId;
    /**
     * 优惠方式类型，0.百分比，1.固定
     * {@link com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum}
     */
    private Integer discountType;
    /**
     * 对应的活动条件值
     */
    private String conditionalValue;
    /**
     * 参与方式,0.手动参与，1.自动参与
     * {@link ActivityParticipationModeEnum}
     */
    private Integer participationMode;

    /**
     * 派发方式
     * {@link ActivityDistributionTypeEnum}
     */
    private Integer distributionType;

    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    private String venueType;

    private String platformOrFiatCurrency;
}
