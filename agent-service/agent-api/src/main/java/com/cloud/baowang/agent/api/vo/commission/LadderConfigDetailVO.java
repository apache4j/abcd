package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 16:56
 * @description: 盈利分成阶梯配置VO
 */
@Data
@Schema(title = "盈利分成阶梯配置展示", description="盈利分成阶梯配置展示")
public class LadderConfigDetailVO implements Serializable {
    @Schema(description = "阶梯档位")
    @NotNull
    private String levelName;
    @Schema(description = "平台最少盈利")
    @NotNull
    private BigDecimal winLossAmount;
    @Schema(description = "最少有效投注金额")
    @NotNull
    private BigDecimal validAmount;
    @Schema(description = "最少活跃玩家数量")
    @NotNull
    private Integer activeNumber;
    @Schema(description = "最少有效新增玩家数量")
    @NotNull
    private Integer newValidNumber;
    @Schema(description = "盈利分成比例")
    @NotNull
    private String rate;
}
