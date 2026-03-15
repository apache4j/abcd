package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentSubLineUserResVO {
    private String agentId;
    private String agentAccount;
    @Schema(description ="下线用户数")
    private Integer subLineUserNum;
    @Schema(description ="用户首存数")
    private Integer userFirstDepositNum;
    @Schema(description ="用户首存金额")
    private BigDecimal userFirstDepositAmount;
    @Schema(description ="主货币")
    private String mainCurrency;
}
