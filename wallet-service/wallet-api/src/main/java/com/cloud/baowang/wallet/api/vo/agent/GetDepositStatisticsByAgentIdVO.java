package com.cloud.baowang.wallet.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理客户端-存取款金额 按天统计 VO")
public class GetDepositStatisticsByAgentIdVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1;

    @Schema(description = "日期")
    private String myDay;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "存款金额总和")
    private BigDecimal depositAmount;

    @Schema(description = "取款金额总和")
    private BigDecimal withdrawAmount;
}
