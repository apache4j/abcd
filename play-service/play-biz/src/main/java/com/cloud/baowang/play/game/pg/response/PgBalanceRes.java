package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PgBalanceRes {

    /**
     * 玩家选择的币种
     */
    private String currencyCode;

    /**
     * 所有玩家钱包余额的总和：
     * • 现金余额
     * • 红利余额
     * • 免费游戏余额
     */
    private BigDecimal totalBalance;

    /**
     * 玩家的现金钱包余额
     * 运营商可参考该值作为玩家余额
     */
    private BigDecimal cashBalance;

    /**
     * 玩家的红利余额
     * 这将显示玩家的所有可用红利余额，它无法被转出。
     */
    private BigDecimal totalBonusBalance;

    /**
     * 玩家的免费游戏钱包余额
     * 这将显示玩家的所有可用的免费游戏，它无法被转出。
     */
    private BigDecimal freeGameBalance;
}
