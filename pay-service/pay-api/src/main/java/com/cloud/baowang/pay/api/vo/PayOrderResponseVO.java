package com.cloud.baowang.pay.api.vo;

import lombok.Data;

@Data
public class PayOrderResponseVO {
    //0 成功  非0 失败
    private Integer code;

    private String message;

    private String amount;

    private Integer payOrderStatus;

    //查询的订单号
    private String orderNo;

    //三方订单号
    private String thirdOrderNo;

    //只有本地虚拟币订单有值
    private TradeNotifyVo tradeNotifyVo;
}
