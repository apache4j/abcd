package com.cloud.baowang.pay.api.vo;

import lombok.Data;

@Data
public class WithdrawalResponseVO {

    private Integer code;

    private String message;
    /**
     * {@linkplain com.cloud.baowang.common.core.enums.pay.PayoutStatusEnum}
     */
    private Integer withdrawOrderStatus;

    //提交金额
    private String amount;

    //订单手续费
    private String commissionAmount;

    //查询的订单号
    private String orderNo;


    // 平台方订单号，对接错误时返回空字符串
    private String withdrawOrderId;

    //只有本地虚拟币有值
    private TradeNotifyVo tradeNotifyVo;
}
