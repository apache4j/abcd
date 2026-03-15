package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(description = "首存活动-百分比对应条件值视图")
public class RechargePercentageVO implements Serializable {


    @Schema(description = "最小存款金额")
    @NotNull(message = "最小存款金额不能为空")
    private BigDecimal minDeposit;
    @Schema(description = "优惠百分比")
    @NotNull(message = "优惠百分比不能为空")
    @DecimalMin(value = "0.01", message = "优惠百分比最小不能小于0.01")
    @DecimalMax(value = "500", message = "优惠百分比最大不能超过500")
    private BigDecimal discountPct;
    @Schema(description = "单日最高赠送金额")
    @NotNull(message = "单日最高赠送金额不能为空")
    private BigDecimal maxDailyBonus;
}
