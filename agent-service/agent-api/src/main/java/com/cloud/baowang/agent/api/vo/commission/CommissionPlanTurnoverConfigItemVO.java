package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 有效流水方案配币种分组VO
 *
 * @author remo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "有效流水方案配币种分组VO", description = "有效流水方案配币种分组VO")
public class CommissionPlanTurnoverConfigItemVO implements Serializable {
    @Schema(title = "等级")
    private Integer tierNum;

    @Schema(title = "有效投注")
    private BigDecimal betAmount;

    @Schema(title = "返佣比例")
    private BigDecimal rate;
}
