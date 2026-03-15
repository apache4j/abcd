package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 16:56
 * @description: 盈利分成阶梯配置VO
 */
@Data
@Schema(title = "盈利分成阶梯配置VO", description = "盈利分成阶梯配置VO")
public class AgentCommissionLadderVO extends BaseVO implements Serializable {
    @Schema(title = "佣金方案ID")
    private String planId;
    @Schema(title = "阶梯档位")
    private String levelName;
    @Schema(title = "平台最少盈利")
    private BigDecimal winLossAmount;
    @Schema(title = "最少有效投注金额")
    private BigDecimal validAmount;
    @Schema(title = "最少活跃玩家数量")
    private Integer activeNumber;
    @Schema(title = "最少有效新增玩家数量")
    private Integer newValidNumber;
    @Schema(title = "盈利分成比例")
    private String rate;
    @Schema(title = "结算周期  1 自然日 2 自然周  3 自然月")
    private Integer settleCycle;

    public Double getNumberRate() {
        return Double.parseDouble(rate);
    }
}
