package com.cloud.baowang.agent.api.vo.agentFinanceReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * GetAgentDepositWithdrawalListVO
 * </p>
 *
 * @author kimi
 * @since 2023-10-18
 */
@Data
@Schema(title = "GetAgentDepositWithdrawalListVO对象", description = "GetAgentDepositWithdrawalListVO")
public class GetAgentDepositWithdrawalListVO {

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "订单类型: 1充值, 2提现")
    private Integer type;

    @Schema(title = "实际到账金额")
    private BigDecimal arriveAmount;

    @Schema(title = "申请金额")
    private BigDecimal applyAmount;

    @Schema(title = "是否大额出款 (0否，1是)")
    private String isBigMoney;
}
