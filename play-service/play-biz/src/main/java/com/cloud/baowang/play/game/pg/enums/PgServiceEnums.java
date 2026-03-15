package com.cloud.baowang.play.game.pg.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PgServiceEnums {
    CREATE("/v3/Player/Create", "创建用户"),
    //BALANCE("/Cash/v3/GetPlayerWallet", "查询余额"),
    //TRANSFER_IN("/Cash/v3/TransferIn", "资金转入"),
    //TRANSFER_OUT("/Cash/v3/TransferOut", "资金转出"),
    //此API只能在最后一笔交易的 5 分钟调用
    TRANSFER_CHECK("/Cash/v3/GetSingleTransaction", "获取单个交易记录"),
    /**
     * 运营商可获得最近60天的投注历史记录
     */
    QUERY_GAME_HISTORY("/Bet/v4/GetHistory", "拉取注单"),
    QUERY_GAME_LIST("/Game/v2/Get", "获取游戏列表"),
    LOGIN_GAME("-game-launcher/api/v1/GetLaunchURLHTML", "进入游戏"),
    FREE_GAME("/FreeGame/v1/TransferInFlexibleFreeGame", "免费游戏"),
    GAME_GET("/Game/v2/Get", "最新游戏"),
    KICK_OUT("/v3/Player/Kick", "用户踢线"),
    ;

    /**
     * 接口路径
     */
    private String path;

    /**
     * 描述
     */
    private String name;

}
