package com.cloud.baowang.play.api.vo.marbles;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SettleBetReq {


    @JsonProperty("PlayerId")
    private String playerId;

    @JsonProperty("Provider")
    private String provider;

    // 所投注的游戏代码。
    @JsonProperty("GameId")
    private String gameId;

    // 所投注的 IM 游戏名称。
    @JsonProperty("GameName")
    private String gameName;

    // 所投注的彩期
    // 仅适用于彩票 Lottery
    @JsonProperty("GameNo")
    private String gameNo;

    // 注单代码。
    @JsonProperty("BetId")
    private String betId;

    // IM 交易代码。
    @JsonProperty("TransactionId")
    private String transactionId;

    @JsonProperty("RefTransactionId")
    private List<String> refTransactionId;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Currency")
    private String currency;

    // 投注金额，最多至4 小数。
    @JsonProperty("Amount")
    private BigDecimal amount;

    @JsonProperty("Details")
    private String Ddetails;

    @JsonProperty("SettlementDate")
    private String settlementDate;

    @JsonProperty("TimeStamp")
    private String timeStamp;
}
