package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreePlaceBet {



    // 游戏提供商玩家账号
    @JsonProperty("ProviderPlayerId")
    private String providerPlayerId;

    // 玩家账号。此账号必须与营运商创建于IMOne 端的玩家账号相同。
    @JsonProperty("PlayerId")
    private String playerId;

    // 所投注的游戏提供商代码。
    @JsonProperty("Provider")
    private String provider;

    // IM 福利编号。
    @JsonProperty("IMBonusId")
    private String IMBonusId;

    // IM 福利名称。
    @JsonProperty("BonusName")
    private String bonusName;

    // 所投注的游戏代码。。
    @JsonProperty("GameId")
    private String gameId;

    //所投注的 IM 游戏名称。
    @JsonProperty("GameName")
    private String gameName;

    // 所投注的游戏提供商游戏局号
    @JsonProperty("ProviderRoundId")
    private String providerRoundId;

    // 游戏提供商交易代码。
    @JsonProperty("ProviderTransactionId")
    private String providerTransactionId;

    // 注单币别。。
    @JsonProperty("Currency")
    private String currency;

    // 下注
    @JsonProperty("BetAmount")
    private BigDecimal betAmount;


    // 所奖励的免费回合的剩余回合。
    @JsonProperty("RemainingRounds")
    private Integer remainingRounds;


    // IM 端收到该笔交易的时间。
    //• 时区为 UTC + 8
    //• 格式为 yyyy-MM-dd HH:mm:ss +08:00
    @JsonProperty("TimeStamp")
    private String timeStamp;
}
