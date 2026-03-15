package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PgTransferRes {

    /**
     * 特殊交易识别码
     */
    private String transactionId;

    /**
     * 交易前的玩家余额
     */
    private BigDecimal balanceAmountBefore;

    /**
     * 交易后的玩家余额
     */
    private BigDecimal balanceAmount;

    /**
     * 交易金额
     */
    private BigDecimal amount;
}
