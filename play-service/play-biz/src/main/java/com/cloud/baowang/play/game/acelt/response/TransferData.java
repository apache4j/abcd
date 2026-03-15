package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

/**
 * @className: TransferResponseRes
 * @author: wade
 * @description: 充值 与转出 Data
 * @date: 2024/3/28 15:22
 */
@Data
public class TransferData {
    /**
     * 交易金额
     */
    private int amount;
    /**
     * 交易ID
     */
    private String transactionId;
    /**
     * 交易状态 0-失败 1-成功 2-处理中
     */
    private int transferStatus;
}
