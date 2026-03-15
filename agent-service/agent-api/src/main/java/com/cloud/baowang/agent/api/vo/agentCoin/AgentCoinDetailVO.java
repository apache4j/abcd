package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理佣金和额度钱包余额返回对象")
public class AgentCoinDetailVO {

    @Schema(description="佣金钱包对象")
    private  AgentCoinBalanceVO commissionCoinBalance;

    @Schema(description="额度钱包对象")
    private AgentCoinBalanceVO quotaCoinBalance;
}
