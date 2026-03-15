package com.cloud.baowang.activity.po.v2;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum;
import com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 指定日期存款活动
 */
@Data
@NoArgsConstructor
@TableName(value = "site_activity_assign_day_v2")
public class SiteActivityAssignDayV2PO extends SiteBasePO {

    /**
     * 所属活动
     */
    private String activityId;

    /**
     * 指定日期 周一、周二等
     */
    private String weekDays;


    /**
     * 优惠方式
     * {@link ActivityDiscountTypeEnum}
     */
    private Integer discountType;


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
     *匹配条件 json格式  jsonArray格式阶梯次数:{min_deposit_amt,max_deposit_amt,acquire_num}
     */
    private String conditionVal;

    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    private String venueType;

    /**
     * 场馆类型
     * system_param "venue_type"
     */
    @Schema(description = "场馆类型")
    @NotNull(message = "场馆不能为空,固定PP")
    private String venueCode;
    @Schema(description = "pp游戏code")
    @NotNull(message = "pp游戏code不能为空")
    private String accessParameters;
    @Schema(description = "限注金额")
    @NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;

    @Schema(title = "平台币还是法币")
    private String platformOrFiatCurrency;


}
