package com.cloud.baowang.wallet.api.vo.userCoinRecord;

import com.cloud.baowang.common.core.vo.base.MessageBaseVO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2025/5/2 13:03
 * @Version: V1.0
 **/
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ChainTradeMessageVO extends MessageBaseVO implements Serializable {
    private static final long serialVersionUID = -1L;
    /**
     * 订单类型 RECHARGE 充值  COLLECT 归集
     */
    private String orderType;

    /**
     * 交易hash
     */
    private String tradeHash;

    /**
     * 交易时间
     */
    private Long tradeTime;

    /**
     * 链上交易状态 1 成功  0 失败
     */
    private Integer tradeStatus;

    /**
     * 归属人编号
     */
    private String ownerUserId;


    /**
     * 归属人类型
     */
    private String ownerUserType;

    /**
     * 用户地址
     */
    private String userAddress;

    /**
     * 转出地址
     */
    private String fromAddress;

    /**
     * 转入地址
     */
    private String toAddress;

    /**
     * 交易金额 链上金额除以精度 金额截断
     */
    private BigDecimal tradeAmount;

    /**
     * 链上交易原始金额
     */
    private String tradeAmt;

    /**
     * 币种 usdt trx
     */
    private String coinCode;

    /**
     * 网络类型 trc20 erc20
     */
    private String networkType;

    /**
     * 链类型 tron ethereum btc
     */
    private String chainType;


    /**
     * 转出方用户编号
     */
    private String fromUserId;

    /**
     * 转入方用户编号
     */
    private String toUserId;

    /**
     * 交易备注
     */
    private String memo;

    /**
     * 链上失败原因
     */
    private String errMsg;

    /**
     * 区块编号
     */
    private Long blockNumber;


}
