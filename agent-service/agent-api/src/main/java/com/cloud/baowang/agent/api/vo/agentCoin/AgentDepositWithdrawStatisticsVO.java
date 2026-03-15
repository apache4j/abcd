package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "财务代理充提信息")
public class AgentDepositWithdrawStatisticsVO {

    @Schema(description="存款总额")
    private BigDecimal depositAmount;

    @Schema(description="存款次数")
    private Integer depositNum;

    @Schema(description="佣金钱包存款总额")
    private BigDecimal commissionCoinDepositAmount;

    @Schema(description="佣金钱包存款次数")
    private Integer commissionCoinDepositNum;

    @Schema(description="额度钱包存款总额")
    private BigDecimal quotaCoinDepositAmount;

    @Schema(description="额度钱包存款次数")
    private Integer quotaCoinDepositNum;

    @Schema(description="取款金额")
    private BigDecimal withdrawAmount;

    @Schema(description="取款次数")
    private Integer withdrawNum;

    @Schema(description="普通取款次数")
    private Integer commonWithdrawNum;

    @Schema(description="大额取款次数")
    private Integer bigMoneyWithdrawNum;

}
