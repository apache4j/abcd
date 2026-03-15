package com.cloud.baowang.play.game.lgd.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FreeGameReqVO {

    // 玩家账号 必填
    @JsonProperty("PLAYERNAME")
    private String playerName;
    // 游戏编码 必填
    @JsonProperty("GAME_CODE")
    private String gameCode;
    // 免费次数 必填
    @JsonProperty("FREENUMS")
    private Integer freeNums;
    // 优惠线注
    @JsonProperty("BETLINE")
    private String betLine;
    // 有效时间
    @JsonProperty("VALIDHOURS")
    private Integer validHours = 72;
    // 祝词  必填
    @JsonProperty("MESSAGE")
    private String message = "Congratulations";

}
