package com.cloud.baowang.play.game.v8.resp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class V8OrderDataRespVO {

    // 玩家昵称
    private Integer code;
    // 玩家id
    @JsonProperty("start")
    private Long start;
    // 游戏id
    @JsonProperty("end")
    private Long end;
    // 牌局id
    @JsonProperty("count")
    private Integer count;
    // 牌局id(字符串格式)
    @JsonProperty("list")
    private V8OrderDetailRespVO list;


}
