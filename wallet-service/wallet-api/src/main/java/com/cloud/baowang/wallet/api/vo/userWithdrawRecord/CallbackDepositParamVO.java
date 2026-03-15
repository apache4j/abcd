package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 */
@Data
@Schema(description = "支付回调支付订单传参")
public class CallbackDepositParamVO {

    @Schema(description = "三方支付关联id")
    private String payId;

    @Schema(description = "用户账号")
    private String userAccount;

    @Schema(description = "三方支付code")
    private String payCode;

    @Schema(description = "三方支付回调订单状态参考ThirdPayOrderStatusEnum(0:处理中,1:交易成功,2:交易失败)")
    private int status;

    @Schema(description = "代付金额")
    private BigDecimal amount;

    @Schema(description = "系统订单编号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "实际交易金额")
    private BigDecimal tradeCurrencyAmount;
}
