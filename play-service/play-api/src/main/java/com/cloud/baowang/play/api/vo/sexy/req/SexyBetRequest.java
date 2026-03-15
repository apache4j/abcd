package com.cloud.baowang.play.api.vo.sexy.req;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SexyBetRequest {

    /**
     * Platform transaction ID 游戏商注单号
     */
    private String platformTxId;

    /**
     * User ID 玩家帐号
     */
    private String userId;

    /**
     * Player currency code 玩家使用的货币代码
     */
    private String currency;

    /**
     * Platform name 游戏平台名称
     */
    private String platform;

    /**
     * Platform game type 平台游戏类型
     */
    private String gameType;

    /**
     * Platform game code 平台游戏代码
     */
    private String gameCode;

    /**
     * Game name 游戏名称
     */
    private String gameName;

    /**
     * Platform bet type 游戏平台的下注项目
     * 可能为 null 或空值
     */
    private String betType;

    /**
     * Bet amount (how much did user bet) 下注金额
     */
    private BigDecimal betAmount;

    /**
     * Place bet time (ISO8601 format) 玩家下注时间 (ISO8601格式)
     */
    private String betTime;

    /**
     * Round ID 游戏商的回合识别码
     */
    private String roundId;

    /**
     * Display game info from game providers in JSON format
     * 不验证内容
     */
    private String gameInfo;

    /**

     * 特殊收费游戏标记
     */
    private Boolean isPremium;

    /**
     * 调整投注
     */
    private BigDecimal adjustAmount;



    /**
     * 返还金额 (包含下注金额)
     */
    private BigDecimal winAmount;

    /**
     * 打赏金额
     */
    private BigDecimal tip;


    /**
     * 有效投注金额
     */
    private BigDecimal turnover;

    /**
     * 辨认交易时间依据-ISO8601
     */
    private String txTime;

    /**
     * 注单更新时间
     */
    private String updateTime;

    /**
     * 用此注单号作为结算的参考
     */
    private String refPlatformTxId;

    private Integer voidType;

    /**
     * 结算方法
     */
    private String settleType;

//    private UserInfoVO userInfo;
}
