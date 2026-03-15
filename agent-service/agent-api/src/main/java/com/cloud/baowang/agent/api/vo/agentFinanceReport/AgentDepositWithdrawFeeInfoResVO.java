package com.cloud.baowang.agent.api.vo.agentFinanceReport;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@Schema(description = "代理财务报表-存提手续费详情查询返参vo")
public class AgentDepositWithdrawFeeInfoResVO {
    @Schema(description = "支付方式名称")
    @I18nField
    private String paymentMethodName;
    @Schema(description = "存/取款金额")
    private BigDecimal amount = BigDecimal.ZERO;
    @Schema(description = "手续费")
    private BigDecimal feeAmount = BigDecimal.ZERO;
    @Schema(description = "币种")
    private String currency;
}
