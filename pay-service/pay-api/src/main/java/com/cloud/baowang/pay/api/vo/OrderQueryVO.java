package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class OrderQueryVO  {
    private static final long serialVersionUID = 1L;

    @Schema(description = "订单号")
    private String orderNo;
    //三方订单号
    @Schema(description = "三方订单号")
    private String thirdOrderNo;

    @Schema(description = "通道Id")
    private String channelId;

    /**
     * 充值渠道信息VO
     */
    private SystemRechargeChannelVO rechargeChannelVO;

    /**
     * 提现渠道VO
     */
    private SystemWithdrawChannelVO withdrawChannelVO;
}
