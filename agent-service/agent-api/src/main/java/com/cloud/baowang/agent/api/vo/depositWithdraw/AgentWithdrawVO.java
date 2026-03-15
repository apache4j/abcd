package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "提款信息汇总")
public class AgentWithdrawVO {

    @Schema(description = "今日提款次数")
    private Integer todayWithdrawalNums;

    @Schema(description = "今日提款金额")
    private BigDecimal todayWithdrawalAmount;

    @Schema(description = "累计提款金额")
    private BigDecimal withdrawalAmount;

    @Schema(description = "累计订单")
    private Integer withdrawalNums;
}
