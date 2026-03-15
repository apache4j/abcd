package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "财务代理余额信息")
public class AgentBalanceVO {

    @Schema(description="总余额")
    private BigDecimal totalBalance;

    @Schema(description="佣金余额")
    private BigDecimal commissionBalance;

    @Schema(description="额度余额")
    private BigDecimal quotaBalance;

    @Schema(description="冻结金额")
    private BigDecimal freezeBalance;

}
