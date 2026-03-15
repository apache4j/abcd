package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PgTransferCheckRes {

    /**
     * 特殊交易识别码
     */
    private String transactionId;

    private String playerName;

    /**
     * 交易类型:
     * 100: TransferInCash
     * 200: TransferOutCash
     */
    private Integer transactionType;

    private BigDecimal transactionAmount;

    private BigDecimal transactionFrom;

    private BigDecimal transactionTo;

    /**
     * 交易的日期和时间（Unix 时间戳，以毫秒为单位）
     */
    private Long transactionDateTime;
}
