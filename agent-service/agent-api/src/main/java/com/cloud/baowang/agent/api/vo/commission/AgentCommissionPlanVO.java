package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.vo.base.BaseVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "代理佣金方案配置VO", description = "代理佣金方案配置VO")
public class AgentCommissionPlanVO extends BaseVO implements Serializable {
    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "方案code")
    private String planCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "活跃人数最少充值金额")
    private BigDecimal activeDeposit;

    @Schema(title = "活跃人数最少有效投注金额")
    private BigDecimal activeBet;

    @Schema(title = "有效新增最少充值金额")
    private BigDecimal validDeposit;

    @Schema(title = "有效新增最少有效投注额")
    private BigDecimal validBet;

    @Schema(title = "方案状态  0 已编辑  1 未编辑")
    private Integer status;

}
