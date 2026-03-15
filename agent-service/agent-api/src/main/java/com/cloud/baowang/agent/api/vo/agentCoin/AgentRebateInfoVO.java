package com.cloud.baowang.agent.api.vo.agentCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "财务代理返点信息")
public class AgentRebateInfoVO {


    @Schema(description = "累计返点金额")
    private BigDecimal totalRebateAmount;

    @Schema(description = "累计返点期数")
    private Integer totalRebatePeriod;
}
