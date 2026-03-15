package com.cloud.baowang.play.api.vo.winto.req;


import lombok.Data;
import java.math.BigDecimal;


@Data
public class WintoTransferVO{

    /** 运营商独有的代码 */
    private String operatorCode;

    /** 运营商生成的用户令牌（可选） */
    private String userToken;

    /** 用户账号名 */
    private String userName;

    /** 每个游戏分配的唯一ID */
    private String gameId;

    /** 投注唯一ID */
    private Long betId;

    /** 用户选择的币种 */
    private String currency;

    /** 投注金额（免费游戏发送0金额） */
    private BigDecimal betAmount;

    /** 中奖金额 */
    private BigDecimal winAmount;

    /** 玩家输赢金额 */
    private BigDecimal transferAmount;

    /** 投注时间 */
    private String betTime;

    /** 投注交易唯一ID */
    private String transactionId;

    /** 投注类型（可选） */
    private String betType;

    /** 回合ID（特殊模式需要，可选） */
    private String roundId;

    /** 投注更新时间 */
    private String updateTime;

    /** 是否是因验证原因重新发送的投注请求（可选） */
    private Boolean isValidateBet;

    /** 待调整的投注（可选） */
    private Boolean isAdjustment;

    /** 投注金额对应USD金额 */
    private BigDecimal usdBetAmount;

    /** 中奖金额对应USD金额 */
    private BigDecimal usdWinAmount;
}

