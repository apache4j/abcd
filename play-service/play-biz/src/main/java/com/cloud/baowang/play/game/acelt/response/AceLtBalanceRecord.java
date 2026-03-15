package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <h2></h2>
 *
 */
@Data
public class AceLtBalanceRecord {

    /**
     * 平台游戏用户金币余额
     */
    private BigDecimal amount;

    /**
     * 用户状态：1=冻结 2=正常
     */
    private String status;

}
