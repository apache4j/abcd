package com.cloud.baowang.play.game.shaba.response.client;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientResettlesInfo {
    /**
     * 重新结算时间
     */
    private LocalDateTime resettleDate;
    /**
     * 余额是否更动
     */
    private boolean balanceChange;
    /**
     * 注单状态
     */
    private String status;
    /**
     * 前次结算输或赢的金额
     */
    private BigDecimal reSettlePrice;
}
