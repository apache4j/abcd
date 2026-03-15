package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理钱包余额返回对象")
public class AgentCoinBalanceVO {

    @Schema(description = "代理ID")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "冻结金额")
    private BigDecimal freezeAmount;

    @Schema(description = "可用余额")
    private BigDecimal availableAmount;
}
