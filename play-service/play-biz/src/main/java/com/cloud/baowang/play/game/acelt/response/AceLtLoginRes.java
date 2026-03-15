package com.cloud.baowang.play.game.acelt.response;

import lombok.Data;

/**
 * <h2></h2>
 *
 */
@Data
public class AceLtLoginRes {

    /**
     * 平台游戏用户账号名称
     */
    private String accountName;

    /**
     * 平台游戏用户账号ID
     */
    private String accountId;

    /**
     * 平台游戏用户身份识别
     */
    private String platformToken;

    /**
     * 平台游戏大厅地址
     */
    private String url;

}
