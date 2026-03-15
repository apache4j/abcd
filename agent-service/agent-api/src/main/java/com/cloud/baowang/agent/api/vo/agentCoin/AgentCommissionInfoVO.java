package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "财务代理佣金信息")
public class AgentCommissionInfoVO {

    @Schema(description="代理账号")
    private String agentAccount;

    @Schema(description="累计应收总佣金")
    private BigDecimal totalReceivableCommission = BigDecimal.ZERO;

    @Schema(description="累计应收负盈利佣金")
    private BigDecimal totalNegativeProfitCommission = BigDecimal.ZERO;

    @Schema(description="累计应收有效流水返点")
    private BigDecimal totalEffectiveTurnoverCommission = BigDecimal.ZERO;

    @Schema(description="累计应收人头费")
    private BigDecimal totalCapitationFeeCommission = BigDecimal.ZERO;


}
