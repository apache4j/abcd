package com.cloud.baowang.play.game.v8.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SingleV8OrderDetaiVO {
    // 游戏局号列表
    @JsonProperty("GameID")
    private String gameID;
    // 玩家帐号列表
    @JsonProperty("Accounts")
    private String accounts;
    // 游戏房间代号
    @JsonProperty("ServerID")
    private Integer serverID;
    // 游戏 ID 列表
    @JsonProperty("KindID")
    private Integer kindID;

    // 桌子号列表
    @JsonProperty("TableID")
    private Integer tableID;

    // 椅子号列表
    @JsonProperty("ChairID")
    private Integer chairID;

    // 玩家数量列表
    @JsonProperty("userCount")
    private Integer UserCount;

    // 有效下注列表
    @JsonProperty("CellScore")
    private BigDecimal cellScore;

    // 总下注列表
    @JsonProperty("AllBet")
    private BigDecimal allBet;

    // 盈利列表
    @JsonProperty("Profit")
    private BigDecimal profit;

    // 抽水列表
    @JsonProperty("Revenue")
    private BigDecimal revenue;

    // 游戏开始时间列表
    @JsonProperty("GameStartTime")
    private Date gameStartTime;

    // 游戏结束时间列表
    @JsonProperty("GameEndTime")
    private Date gameEndTime;

    // 游戏语系列表
    @JsonProperty("CardValue")
    private String cardValue;

    // 手牌公共牌列表
    @JsonProperty("ChannelID")
    private Integer channelID;

    // 游戏结果对应玩家所属站点列表
    @JsonProperty("LineCode")
    private String lineCode;

    // 币别列表
    @JsonProperty("Currency")
    private String currency;

    // 游戏语系列表
    @JsonProperty("Language")
    private String language;


}
