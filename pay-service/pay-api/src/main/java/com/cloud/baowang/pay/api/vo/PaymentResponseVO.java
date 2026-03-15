package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "三方代收统一返回数据VO")
public class PaymentResponseVO {

    @Schema(description = "0 为成功， -1 为失败")
    private Integer code;

    @Schema(description = "支付地址")
    private String paymentUrl;

    @Schema(description = "我方订单id")
    private String orderId;

    @Schema(description = "三方订单id，不一定有值")
    private String thirdOrderId;

    @Schema(description = "请求金额")
    private String amount;

    @Schema(description = "返回消息")
    private String message;

    @Schema(description = "银行卡信息 卡转卡通道才有值")
    private BankInfoVO bankInfo;

    @Schema(description = "充值时间")
    private Long depositTime;

    @Schema(description = "手续费")
    private BigDecimal handlingFee;
}


