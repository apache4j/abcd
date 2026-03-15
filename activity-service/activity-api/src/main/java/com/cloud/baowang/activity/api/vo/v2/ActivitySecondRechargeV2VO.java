package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.ActivityBaseVO;
import com.cloud.baowang.activity.api.vo.DepositConfigDTO;
import com.cloud.baowang.activity.api.vo.FixedAmountVO;
import com.cloud.baowang.activity.api.vo.RechargePercentageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 次存
 */
@Data
public class ActivitySecondRechargeV2VO extends ActivityBaseV2VO implements Serializable {

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
    private List<RechargePercentageV2VO> percentageVO;

    @Schema(description = "固定金额对应条件值--优惠方式==1")
    private List<FixedAmountV2VO> fixedAmountVOS;

    /**
     * 首存，次存，指定日存款
     */
    @Schema(title = "游戏大类存款配置")
    //@NotEmpty(message = "游戏大类存款配置不能为空")
    private List<DepositConfigV2DTO> depositConfigDTOS;


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
    private String venueType;


    /**
     * 注册成功弹窗终端
     */
    @Schema(title = "注册成功弹窗终端")
    private String recommendTerminals;
    /**
     * 是否推荐活动（0.不推荐。 1. 推荐）
     */
    @Schema(title = "是否推荐活动(0.不推荐。 1. 推荐）")
    private Boolean recommended;

    /**
     * 弹窗宣传图PC
     */
    @Schema(title = "弹窗宣传图PC")
    private String picShowupPcI18nCode;

    /**
     * 弹窗宣传图APP
     */
    @Schema(title = "弹窗宣传图APP")
    private String picShowupAppI18nCode;

    /**
     *  platform_or_fiat_currency
     */
    @Schema(title = "活动币种类型（0.平台币，1. 法币）")
    private String platformOrFiatCurrency;

}
