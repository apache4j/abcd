package com.cloud.baowang.play.api.vo.nextSpin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NextSpinReq implements Serializable {

    // 用户标识 ID
    private String acctId;
    // 语言
    private String language;
    // 商户编码
    private String merchantCode;
    // 用户token
    private String token;
    // 游戏会话的唯一ID
    private String serialNo;

    //标识转帐流水
    private String transferId;

    //金额
    private BigDecimal amount;
    //币种
    private String currency;

    //type 1下注 2取消下注 4派彩 6jackpot的派彩
    private Integer type;

    /**
     * 游戏代码
     */
    private String gameCode;

    /**
     * channel web
     */
    private String channel;
    /**
     * 局号
     */
    private String referenceId;
    /**
     * 注单id
     */
    private String ticketId;

    private SpecialGameReq specialGame;
}
