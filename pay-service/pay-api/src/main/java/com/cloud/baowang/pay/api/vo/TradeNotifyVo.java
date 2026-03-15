package com.cloud.baowang.pay.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/6/11 14:46
 * @Version: V1.0
 **/
@Data
public class TradeNotifyVo implements Serializable {

    @Schema(description = "商户号")
    private String merNo;
    @Schema(description = "平台编号等同于站点号")
    private String platNo;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;


    /**
     * 订单类型 RECHARGE 充值  WITHDRAW 提币 MINI_RECHARGE 小额充值
     */
    @Schema(description = "订单类型 RECHARGE 充值  WITHDRAW 提币 MINI_RECHARGE 小额充值 ")
    private String orderType;

    /**
     * 订单类型名称
     */
    @Schema(description = "订单类型名称")
    private String orderTypeName;

    /**
     * 交易hash
     */
    @Schema(description = "交易hash")
    private String tradeHash;

    /**
     * 交易时间
     */
    @Schema(description = "交易时间")
    private Long tradeTime;

    /**
     * 链上交易状态 1 成功  0 失败
     */
    @Schema(description = "链上交易状态 1 成功  0 失败")
    private int tradeStatus;

    /**
     * 链上交易状态名称
     */
    @Schema(description = "链上交易状态名称 1 成功  0 失败")
    private String tradeStatusName;

    /**
     * userAddress的归属用户
     */
    @Schema(description = "userAddress的归属用户")
    private String ownerUserId;
    @Schema(description = "userAddress的归属用户类型")
    private String ownerUserType;
    /**
     * 充值 等于 toAddress 提币 等于 实际用户地址
     */
    @Schema(description = "充值 等于 toAddress 提币 等于 实际用户地址")
    private String userAddress;

    /**
     * 转出地址
     */
    @Schema(description = "转出地址")
    private String fromAddress;

    /**
     * 转入地址
     */
    @Schema(description = "转入地址")
    private String toAddress;

    /**
     * 交易金额 链上金额除以精度
     */
    @Schema(description = "交易金额 链上金额除以精度 截断")
    private BigDecimal tradeAmount;

    /**
     * 币种 usdt trx
     */
    @Schema(description = "币种 usdt")
    private String coinCode;

    /**
     * 网络类型 trc20 erc20
     */
    @Schema(description = "网络类型 trc20 erc20")
    private String networkType;

    /**
     * 链类型 tron eth btc
     */
    @Schema(description = "链类型 tron eth btc")
    private String chainType;


    /**
     * 区块编号
     */
    @Schema(description = "区块高度")
    private Long blockNumber;


    /**
     * 链上响应代码
     */
    private String chainRespCode;

    /**
     * 链上响应消息
     */
    private String chainRespMsg;

}
