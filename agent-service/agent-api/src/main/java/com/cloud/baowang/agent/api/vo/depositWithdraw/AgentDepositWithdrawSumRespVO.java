package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 *
 **/
@Data
public class AgentDepositWithdrawSumRespVO {

    private BigDecimal agentAccount;
    @Schema(description = "日期")
    private String myDay;

    private String currencyCode;
    private BigDecimal applyAmount;
    private BigDecimal arriveAmount;
    private BigDecimal agentCount;


}
