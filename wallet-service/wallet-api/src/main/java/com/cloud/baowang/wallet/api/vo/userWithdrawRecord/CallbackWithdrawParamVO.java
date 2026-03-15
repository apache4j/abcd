package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 14/1/23 11:28 AM
 * @Version 1.0
 */
@Data
@Schema(description = "代付回调支付订单传参")
public class CallbackWithdrawParamVO {

    @Schema(description = "三方支付关联id")
    private String payId;

    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "三方支付code")
    private String payCode;

    @Schema(description = "三方支付回调订单状态参考ThirdPayOrderStatusEnum(0:处理中,1:交易成功,2:交易失败)")
    private Integer status;

    @Schema(description = "代付金额")
    private BigDecimal amount;

    @Schema(description = "系统订单编号")
    private String orderNo;

    @Schema(description = "备注")
    private String remark;
}
