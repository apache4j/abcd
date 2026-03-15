package com.cloud.baowang.play.api.vo.evo;

import lombok.Data;

/**
 * @className: EvoApiTokens
 * @author: wade
 * @description: token
 * @date: 13/8/25 11:10
 */
@Data
public class EvoApiTokens {

    /**
     * UA2 API token for internal communication
     * UA2 API 通常是用户账户、会话相关的接口。获取游戏链接是这个token
     */
    private String ua2Token;

    /**
     * Token used for accessing Game History API
     * 即获取游戏记录、下注历史的接口
     */
    private String gameHistoryApiToken;

    /**
     * Token used for accessing External Lobby API
     * -比如获取游戏列表、桌台信息等功能
     */
    private String externalLobbyApiToken;

    /**
     * Token used for accessing Reward Game API
     * 奖励、免费游戏、投注活动接口）
     */
    private String rewardGameToken;

    /**
     * UA2 API token for internal communication
     * UA2 API 通常是用户账户、会话相关的接口）
     */
    private String walletToken;


}

