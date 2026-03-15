package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "固定金额VO，包含存款和赠送金额信息")
@Data
public class FixedAmountVO {

    @Schema(description = "存款最小值", example = "100.00")
    @NotNull(message = "存款最小值不能为空")
    private BigDecimal minDeposit;

    @Schema(description = "存款最大值", example = "10000.00")
    @NotNull(message = "存款最大值不能为空")
    private BigDecimal maxDeposit;

    @Schema(description = "赠送金额", example = "50.00")
    @NotNull(message = "赠送金额不能为空")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal bonusAmount;


}