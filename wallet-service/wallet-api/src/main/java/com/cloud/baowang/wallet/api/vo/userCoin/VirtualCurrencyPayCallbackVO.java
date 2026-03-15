package com.cloud.baowang.wallet.api.vo.userCoin;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class VirtualCurrencyPayCallbackVO {

    /**
     * 区块编码
     */
    private String blockNumber;

    /**
     * 链类型
     */
    private String chainType;

    /**
     * 资金CODE USDT
     */
    private String coinCode;

    /**
     * 来源地址
     */
    private String fromAddress;

    /**
     * 网络协议
     */
    private String networkType;

    /**
     * 订单编码
     */
    private String orderNo;

    /**
     * 订单时间
     */
    private Long orderTime;

    /**
     * 类型 RECHARGE
     */
    private String orderType;

    /**
     * 类型名称 充值
     */
    private String orderTypeName;

    /**
     * 会员ID
     */
    private String ownerUserId;

    /**
     * 到达地址
     */
    private String toAddress;

    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;

    /**
     * 交易哈希
     */
    private String tradeHash;

    /**
     * 交易状态
     */
    private Integer tradeStatus;

    /**
     * 交易状态名称
     */
    private String tradeStatusName;

    /**
     * 交易时间
     */
    private Long tradeTime;

    /**
     * 会员地址
     */
    private String userAddress;


    /**
     * 通道id
     */
    private String channelCode;

    /**
     * 通道id
     */
    private String channelName;
}
