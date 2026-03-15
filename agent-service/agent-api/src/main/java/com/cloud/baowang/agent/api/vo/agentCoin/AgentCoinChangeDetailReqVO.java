package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(title = "代理账变明细详情请求对象")
public class AgentCoinChangeDetailReqVO {

    @Schema(description = "订单号")
    @NotBlank
    private String orderNo;

    @Schema(description = "钱包类型（1-佣金钱包; 2-代存钱包）")
    @NotNull
    private String walletType;

    @Schema(description = "收支类型")
    private String balanceType;

    @Schema(hidden = true)
    private String agentAccount;
}
