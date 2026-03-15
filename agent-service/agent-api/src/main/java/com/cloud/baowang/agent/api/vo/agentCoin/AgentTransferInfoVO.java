package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "财务代理转账信息")
public class AgentTransferInfoVO {

    @Schema(description="转账总额")
    private BigDecimal transferAmount;

    @Schema(description="转账总次数")
    private Integer transferNum;

    @Schema(description="佣金钱包转账总额")
    private BigDecimal CommissionCoinTransferAmount;

    @Schema(description="佣金钱包转账总次数")
    private Integer  CommissionCoinTransferNum;

    @Schema(description="额度钱包转账总额")
    private BigDecimal quotaCoinTransferAmount;

    @Schema(description="额度钱包转账总次数")
    private Integer quotaCoinTransferNum;

}
