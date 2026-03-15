package com.cloud.baowang.play.game.evo.response;

import lombok.Data;

/**
 * EVO 游戏信息 DTO
 * 对应游戏的基础信息
 */
@Data
public class EVOGameInfo {

    /**
     * 游戏类型，例如 "apollopays"
     */
    private String gameType;

    /**
     * 游戏类型名称，例如 "Apollo Pays"
     */
    private String gameTypeName;

    /**
     * 游戏子类型（可为空）
     */
    private String gameSubtype;

    /**
     * 游戏子类型名称（可为空）
     */
    private String gameSubtypeName;

    /**
     * 游戏提供商标识，例如 "btg"
     */
    private String gameProvider;

    /**
     * 游戏提供商名称，例如 "Big Time Gaming"
     */
    private String gameProviderName;

    /**
     * 游戏子提供商标识，例如 "btg"
     */
    private String gameSubprovider;

    /**
     * 游戏子提供商名称，例如 "Big Time Gaming"
     */
    private String gameSubproviderName;

    /**
     * 游戏垂直领域标识，例如 "slots"
     */
    private String gameVertical;

    /**
     * 游戏垂直领域名称，例如 "Slots"
     */
    private String gameVerticalName;

    /**
     * 游戏类别标识，例如 "slots"
     */
    private String gameCategory;

    /**
     * 游戏类别名称，例如 "Slot"
     */
    private String gameCategoryName;

    /**
     * 游戏代码，例如 "apollopays"
     */
    private String game;

    /**
     * 游戏名称，例如 "Apollo Pays"
     */
    private String gameName;
}
