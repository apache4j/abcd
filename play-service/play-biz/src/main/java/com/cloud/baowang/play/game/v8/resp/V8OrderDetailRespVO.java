package com.cloud.baowang.play.game.v8.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class V8OrderDetailRespVO {
    // 游戏局号列表
    @JsonProperty("GameID")
    private List<String> gameID;
    // 玩家帐号列表
    @JsonProperty("Accounts")
    private List<String> accounts;
    // 游戏房间代号
    @JsonProperty("ServerID")
    private List<Integer> serverID;
    // 游戏 ID 列表
    @JsonProperty("KindID")
    private List<Integer> kindID;

    // 桌子号列表
    @JsonProperty("TableID")
    private List<Integer> tableID;

    // 椅子号列表
    @JsonProperty("ChairID")
    private List<Integer> chairID;

    // 玩家数量列表
    @JsonProperty("userCount")
    private List<Integer> UserCount;

    // 有效下注列表
    @JsonProperty("CellScore")
    private List<BigDecimal> cellScore;

    // 总下注列表
    @JsonProperty("AllBet")
    private List<BigDecimal> allBet;

    // 盈利列表
    @JsonProperty("Profit")
    private List<BigDecimal> profit;

    // 抽水列表
    @JsonProperty("Revenue")
    private List<BigDecimal> revenue;

    // 游戏开始时间列表
    @JsonProperty("GameStartTime")
    private List<Date> gameStartTime;

    // 游戏结束时间列表
    @JsonProperty("GameEndTime")
    private List<Date> gameEndTime;

    // 游戏语系列表
    @JsonProperty("CardValue")
    private List<String> cardValue;

    // 手牌公共牌列表
    @JsonProperty("ChannelID")
    private List<Integer> channelID;

    // 游戏结果对应玩家所属站点列表
    @JsonProperty("LineCode")
    private List<String> lineCode;

    // 币别列表
    @JsonProperty("Currency")
    private List<String> currency;

    // 游戏语系列表
    @JsonProperty("Language")
    private List<String> language;

}
