package com.cloud.baowang.play.game.im.impl.resp.marbles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarblesOrderDataRespVO {

    // 产品供应商代码
    @JsonProperty("Provider")
    private String provider;

    // IMOne 系统内的 GameID
    @JsonProperty("GameId")
    private String gameId;

    // 游戏名称
    @JsonProperty("GameName")
    private String gameName;

    @JsonProperty("ChineseGameName")
    private String chineseGameName;

    // 期号
    @JsonProperty("GameNo")
    private String gameNo;

    // 结算 ID（产品供应商提供）
    @JsonProperty("GameNoId")
    private String gameNoId;

    // 玩家账号
    @JsonProperty("PlayerId")
    private String playerId;

    // 产品提供商玩家账号
    @JsonProperty("ProviderPlayerId")
    private String providerPlayerId;
    // 货币代码
    @JsonProperty("Currency")
    private String currency;

    // 盘口.
    //不适用于SGWIN_LOTTERY.
    @JsonProperty("Tray")
    private String tray;

    // 产品供应商提供的注单号
    @JsonProperty("BetId")
    private String betId;

    // 下注项目
    @JsonProperty("BetOn")
    private String betOn;

    // 下注类别
    @JsonProperty("BetType")
    private String betType;

    // 注单明细
    @JsonProperty("BetDetails")
    private String betDetails;

    // IG 彩票
    //赔率
    @JsonProperty("Odds")
    private String odds;

    // 下注金额. 返回4 位小数点。
    @JsonProperty("BetAmount")
    private BigDecimal betAmount;

    // 注单数
    @JsonProperty("BetCount")
    private Integer betCount;

    // 有效投注. 返回4 位小数点
    @JsonProperty("ValidBet")
    private BigDecimal validBet;

    // 输赢 （根据营运商赔率设定计算）
    // 返回 4 位小数点。
    @JsonProperty("WinLoss")
    private BigDecimal winLoss;

    // 输赢 根据玩家赔率设定计算
    @JsonProperty("PlayerWinLoss")
    private BigDecimal playerWinLoss;


    @JsonProperty("Status")
    private String status;

    // 下注平台
    @JsonProperty("Platform")
    private String platform;

    // 下注时间（产品供应商提供）
    @JsonProperty("BetDate")
    private String betDate;

    // 结算时间（产品供应商提供）
    // 时间格式：yyyy-mm-dd hh:mm:ss +08:00
    @JsonProperty("ResultDate")
    private String resultDate;

    // 结算时间（产品供应商提供）。同上，此属性将会取代
    // ResultDate 属性。
    // 时间格式：yyyy-mm-dd hh:mm:ss +08:0
    @JsonProperty("SettlementDate")
    private String settlementDate;

    // IMOne 系统在收到下注单是的创建时间戳。
    // 时间格式：yyyy-mm-dd hh:mm:ss +08:00
    @JsonProperty("DateCreated")
    private String dateCreated;

    // IMOne 系统对注单的最后跟新时间。
    // 时间格式：yyyy-mm-dd hh:mm:ss +08:00
    @JsonProperty("LastUpdatedDate")
    private String lastUpdatedDate;


}
