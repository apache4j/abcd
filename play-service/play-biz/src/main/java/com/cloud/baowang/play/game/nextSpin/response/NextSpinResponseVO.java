package com.cloud.baowang.play.game.nextSpin.response;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class NextSpinResponseVO {

    /**
     * 下注单号
     */
    private String ticketId;
    /**
     * 	用户标识 ID
     */
    private String acctId;
    /**
     * 下注时间
     */
    private String ticketTime;
    /**
     * 	游戏种类
     */
    private String categoryId;
    /**
     * 	游戏代码
     */
    private String gameCode;
    /**
     * 货币 ISO 代码
     */
    private String currency;
    /**
     * 	下注金额
     */
    private BigDecimal betAmount;
    /**
     * 结果
     */
    private String result;
    /**
     * 	用户输赢
     */
    private BigDecimal winLoss;
    /**
     * 	用户输赢
     */
    private BigDecimal jackpotAmount;

    /**
     * 	用户下注 IP
     */
    private String betIp;
    /**
     * luckyDrawId
     */
    private Long luckyDrawId;
    /**
     * 	是否已结束
     */
    private boolean completed;
    /**
     * 	游戏 log ID
     */
    private String roundId;
    /**
     * 没有特别游戏
     */
    private Integer sequence;
    /**
     * 注单来自手机或网
     */
    private String channel;
    /**
     * 上轮余额
     */
    private BigDecimal balance;
    /**
     * 积宝赢额
     */
    private BigDecimal jpWin;
    /**
     * 参照 transfer ID (只用于单一钱包)
     */
    private String referenceId;

    // 原始注单
    private String originalBetDetail;






}
