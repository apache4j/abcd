package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 首存活动-详情实体
 */
@Data
@Schema(description = "首存活动详细信息")
public class ActivityFirstRechargeVO extends ActivityBaseVO  implements Serializable {

    /**
     * 优惠方式类型，0.百分比，1.固定
     * {@link com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum}
     */
    @Schema(description = "优惠方式类型，0.百分比，1.固定")
    @NotNull(message = "优惠方式类型不能为空")
    @Min(value = 0, message = "优惠方式类型不能小于0")
    @Max(value = 1, message = "优惠方式类型不能大于1")
    private Integer discountType;


    @Schema(description = "百分比类型对应条件值--优惠方式==0")
    private RechargePercentageVO percentageVO;

    @Schema(description = "固定金额对应条件值--优惠方式==1")
    private List<FixedAmountVO> fixedAmountVOS;
    /**
     * 首存，次存，指定日存款
     */
    @Schema(title = "游戏大类存款配置")
    //@NotEmpty(message = "游戏大类存款配置不能为空")
    private List<DepositConfigDTO> depositConfigDTOS;


    /**
     * 参与方式,0 手动参与 1 自动参与
     * {@link com.cloud.baowang.activity.api.enums.ActivityParticipationModeEnum}
     */
    @Schema(description = "参与方式,0 手动参与 1 自动参与")
    @NotNull(message = "参与方式不能为空")
    @Min(value = 0, message = "参与方式不能小于0")
    @Max(value = 1, message = "参与方式不能大于1")
    private Integer participationMode;


    /**
     * 派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发
     * {@link com.cloud.baowang.activity.api.enums.ActivityDistributionTypeEnum}
     */
    @Schema(description = "派发方式,0.玩家自领-过期作废，1.玩家自领-过期作废 2.立即派发")
    @NotNull(message = "派发方式不能为空")
    @Min(value = 0, message = "派发方式不能小于0")
    @Max(value = 1, message = "派发方式不能大于1")
    private Integer distributionType;

    /**
     * 游戏大类
     * system_param "venue_type"
     */
    @Schema(description = "游戏大类,可多选,使用逗号隔开")
    @NotNull(message = "游戏大类不能为空")
    private String venueType;


}
