package com.cloud.baowang.agent.api.vo.agentCoin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "额度转账-余额")
public class AgentQuotaTransferBalanceVO {

    @Schema(title = "额度余额（可用余额）")
    private BigDecimal commissionBalance;

    @Schema(title = "代充钱包（总余额）")
    private BigDecimal quotaBalance;

}
