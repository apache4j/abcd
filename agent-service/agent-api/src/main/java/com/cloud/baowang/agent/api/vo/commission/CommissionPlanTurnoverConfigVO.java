package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 有效流水方案配置项VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配置项VO", description = "有效流水方案配置项VO")
public class CommissionPlanTurnoverConfigVO implements Serializable {
    @Schema(title = "游戏类型")
    @NotNull
    private Integer venueType;

    @Schema(title = "币种")
    @NotBlank
    private String currency;

    @Schema(title = "等级")
    @NotNull
    private Integer tierNum;

    @Schema(title = "有效投注")
    @NotNull
    private BigDecimal betAmount;

    @Schema(title = "返佣比例")
    @NotNull
    private BigDecimal rate;

}
