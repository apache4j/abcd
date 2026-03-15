package com.cloud.baowang.play.game.pg.response;

import lombok.Data;

@Data
public class PgGameListRes {

    /**
     * 游戏的唯一标识符
     */
    private Integer gameId;
    /**
     * 游戏名称
     */
    private String gameName;
    /**
     * 游戏的唯一识别码
     */
    private String gameCode;
    /**
     * 游戏类型：
     * 1：视频老虎机游戏
     * 2：卡牌游戏
     */
    private Integer category;
}
