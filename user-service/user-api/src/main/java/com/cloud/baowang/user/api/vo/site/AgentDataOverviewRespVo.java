package com.cloud.baowang.user.api.vo.site;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理数据概览")
@Builder
public class AgentDataOverviewRespVo implements Serializable {


    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "代理充值金额")
    private BigDecimal agentRechargeAmount =  BigDecimal.ZERO;

    @Schema(description = "代理提款金额")
    private BigDecimal agentWithdrawAmount =  BigDecimal.ZERO;

    @Schema(description = "代理佣金金额")
    private BigDecimal agentCommissionAmount =  BigDecimal.ZERO;


    @Schema(description = "代理充值金额环比")
    private BigDecimal agentRechargeAmountComparePer =  new BigDecimal("0.00");

    @Schema(description = "代理提款金额环比")
    private BigDecimal agentWithdrawAmountComparePer =  new BigDecimal("0.00");

    @Schema(description = "代理佣金金额环比")
    private BigDecimal agentCommissionAmountComparePer =  new BigDecimal("0.00");
}