package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "代理财务信息")
public class AgentFinanceVO {

    @Schema(description ="平台币币种")
    private String platformCurrencyCode;

    @Schema(description="代理余额")
    private AgentBalanceVO agentBalanceVO;

    @Schema(description="佣金信息")
    private AgentCommissionInfoVO agentCommissionInfoVO;

//    @Schema(description="返点信息")
//    private AgentRebateInfoVO agentRebateInfoVO;

    @Schema(description="充提信息")
    private AgentDepositWithdrawStatisticsVO agentDepositWithdrawStatisticsVO;

    @Schema(description="代存信息")
    private AgentProxyDepositVO agentProxyDepositVO;

    @Schema(description="转账信息")
    private AgentTransferInfoVO agentTransferInfoVO;
}
