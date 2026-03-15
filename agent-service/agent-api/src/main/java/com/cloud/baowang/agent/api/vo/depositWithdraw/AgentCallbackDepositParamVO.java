package com.cloud.baowang.agent.api.vo.depositWithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 */
@Data
@Schema(description = "代理支付回调支付订单传参")
public class AgentCallbackDepositParamVO {

    @Schema(description = "三方支付关联id")
    private String payId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "三方支付code")
    private String payCode;

    @Schema(description = "三方支付回调订单状态参考ThirdPayOrderStatusEnum(0:处理中,1:交易成功,2:交易失败)")
    private int status;

    @Schema(description = "存款金额(法币)")
    private BigDecimal amount;

    @Schema(description = "系统订单编号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "实际交易金额(三方实际到账金额)")
    private BigDecimal tradeCurrencyAmount;
}
