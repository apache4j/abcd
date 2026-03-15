package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "财务代理代存信息")
public class AgentProxyDepositVO {

//    @Schema(description="代存总额")
//    private BigDecimal proxyDepositAmount;
//
//    @Schema(description="代存次数")
//    private Integer proxyDepositNum;

//    @Schema(description="佣金代存总额")
//    private BigDecimal commissionProxyDepositAmount;
//
//    @Schema(description="佣金代存次数")
//    private Integer commissionProxyDepositNum;

    @Schema(description="额度代存总额")
    private BigDecimal quotaProxyDepositAmount;

    @Schema(description="额度代存次数")
    private Integer quotaProxyDepositNum;

}
