package com.cloud.baowang.pay.api.vo;

import lombok.Data;

/**
 * 虚拟币提现支付请求VO
 */
@Data
public class VirtualCurrencyPayRequestVO {

    private String platNo;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 会员ID
     */
    private String ownerUserId;

    private String ownerUserType;

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 提现地址
     */
    private String toAddress;

    /**
     * 提现金额
     */
    private String withdrawAmt;
}
