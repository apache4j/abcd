package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AgentSubLineUserWinLoseResVO {
    @Schema(description ="代理账号")
    private String agentAccount;
    @Schema(description ="用户输赢金额")
    private BigDecimal winLoseAmount;
}
