package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "额度转账-余额")
public class AgentQuotaTransferVO {

    private String agentInfoId;

    @Schema(title = "转账金额")
    private BigDecimal transferAmount;

    @Schema(title = "支付密码")
    private String payPassword;

}
