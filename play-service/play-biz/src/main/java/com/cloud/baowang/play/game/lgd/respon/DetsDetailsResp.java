package com.cloud.baowang.play.game.lgd.respon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DetsDetailsResp {

    // 游戏局号
    @JsonProperty("id")
    private String id;

    // 玩家账号
    @JsonProperty("playerName")
    private String playerName;

    // 游戏编码
    @JsonProperty("gameCode")
    private String gameCode;

    // 投注额
    @JsonProperty("betPrice")
    private String betPrice;

    // 结算前额度
    @JsonProperty("creditBefore")
    private String creditBefore;

    // 结算后额度
    @JsonProperty("creditAfter")
    private String creditAfter;

    // 奖金
    @JsonProperty("betWins")
    private String betWins;

    // 输赢值
    @JsonProperty("prizeWins")
    private String prizeWins;

    // 游戏时间
    @JsonProperty("createTime")
    private String createTime;

    // 游戏来源ID
    @JsonProperty("partentId")
    private String partentId;

    // 投注线
    @JsonProperty("betLines")
    private String betLines;

    // 货币类型
    @JsonProperty("currency")
    private String currency;

    // 来源设备
    @JsonProperty("clientType")
    private String clientType;

    // 数据类型
    @JsonProperty("rewardType")
    private String rewardType;

    // 免费旋转ID
    @JsonProperty("fcid")
    private String fcid;

}
