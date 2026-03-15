package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/21 18:07
 * @description:
 */
@Data
@Schema(title = "定义配置VO", description = "定义配置VO")
public class PlanConfigVO implements Serializable {
    @Schema(description = "活跃人数最少充值金额")
    @NotNull
    private BigDecimal activeDeposit;

    @Schema(description = "活跃人数最少有效投注金额")
    @NotNull
    private BigDecimal activeBet;

    @Schema(description = "有效新增最少充值金额")
    @NotNull
    private BigDecimal validDeposit;

    @Schema(description = "有效新增最少有效投注额")
    @NotNull
    private BigDecimal validBet;

    @Schema(description = "货币单位")
    private String currencyUnit;
}
