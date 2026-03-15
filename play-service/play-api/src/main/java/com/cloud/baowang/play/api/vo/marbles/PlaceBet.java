package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceBet {



    // 需要求取余额的产品钱包
    @JsonProperty("ProviderPlayerId")
    private String providerPlayerId;

    // 玩家账号。此账号必须与营运商创建于IMOne 端的玩家账号相同。
    @JsonProperty("PlayerId")
    private String playerId;

    @JsonProperty("Provider")
    private String provider;

    @JsonProperty("GameId")
    private String gameId;

    @JsonProperty("GameName")
    private String gameName;

    @JsonProperty("RoundId")
    private String roundId;

    @JsonProperty("BetId")
    private String betId;

    @JsonProperty("TransactionId")
    private String transactionId;

    @JsonProperty("Type")
    private String type;

    // 玩家注册于 IMOne 端的币别。
    @JsonProperty("Currency")
    private String currency;

    @JsonProperty("Amount")
    private BigDecimal amount;

    @JsonProperty("BetOn")
    private String betOn;

    @JsonProperty("BetType")
    private String betType;

    @JsonProperty("Platform")
    private String platform;

    @JsonProperty("Tray")
    private String tray;

    @JsonProperty("betDate")
    private String BetDate;

    @JsonProperty("TimeStamp")
    private String timeStamp;
}
